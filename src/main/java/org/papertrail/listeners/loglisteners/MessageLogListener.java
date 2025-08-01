package org.papertrail.listeners.loglisteners;

import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import com.google.common.util.concurrent.Striped;
import org.papertrail.database.AuthorAndMessageEntity;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.Schema;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.tinylog.Logger;

public class MessageLogListener extends ListenerAdapter {

	private final DatabaseConnector dc;
	private final Executor vThreadPool;
	private final Striped<Lock> messageLocks = Striped.lock(8192);
	private final AtomicLong activeLockCount = new AtomicLong(0);

	public MessageLogListener(DatabaseConnector dc, Executor vThreadPool) {
		this.dc = dc;
		this.vThreadPool = vThreadPool;
	}

	private void withMessageLock (String messageId, Runnable task) {
		Lock lock = messageLocks.get(messageId);
		lock.lock();
		Logger.info("Lock acquired for message id: {} . Active lock count: {}", messageId, activeLockCount.incrementAndGet());
		try{
			task.run();
		} finally {
			lock.unlock();
			Logger.info("Lock released for message id: {} . Active lock count: {}", messageId, activeLockCount.decrementAndGet());
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		// if the author is a bot or system, don't log
		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}
		// don't register non-textual contents
		if(event.getMessage().getContentRaw().isEmpty()) {
			return;
		}

		// get the guild id for which the event was fired
		String guildId = event.getGuild().getId();
		// see if the guild id is registered in the database for logging

		// if not registered, exit
		if(!dc.getGuildDataAccess().isGuildRegistered(guildId, Schema.MESSAGE_LOG_REGISTRATION_TABLE)) {
			return;
		}

		// else if the registered guild id matches with the event fetched guild id, log the message with its ID and author
		vThreadPool.execute(()-> {
			String messageId = event.getMessageId();
			withMessageLock(messageId, ()-> dc.getMessageDataAccess().logMessage(messageId, event.getMessage().getContentRaw(), event.getAuthor().getId()));
		});


	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {

		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}
		
		String guildId = event.getGuild().getId();
		if(!dc.getGuildDataAccess().isGuildRegistered(guildId, Schema.MESSAGE_LOG_REGISTRATION_TABLE)) {
			return;
		}

		vThreadPool.execute(()->{
			// get the message id of the message which was updated
			String messageId = event.getMessageId();

			// fetch the old message content and its author from the database
			AuthorAndMessageEntity ame = dc.getMessageDataAccess().retrieveAuthorAndMessage(messageId);
			if(ame==null || ame.authorId()==null || ame.authorId().isBlank()) { // would be true only if the unedited message was not logged in the first place
				return;
			}
			// fetch the updated message and its author from the event
			String updatedMessage = event.getMessage().getContentRaw();

			// Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
			// This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
			if(updatedMessage.equals(ame.messageContent())) {
				return;
			}

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("📝 Message Edit Event");
			eb.setDescription("A message sent by "+event.getAuthor().getAsMention()+" has been edited in: "+event.getJumpUrl());
			eb.setColor(Color.YELLOW);

			eb.addField("Old Message", ame.messageContent(), false); // get only the message and not the author
			eb.addField("New Message", updatedMessage, false);

			eb.setFooter(event.getGuild().getName());
			eb.setTimestamp(Instant.now());
			// update the database with the new message
			withMessageLock(messageId, ()->dc.getMessageDataAccess().updateMessage(messageId, updatedMessage));
			// the reason this is above the send queue is because in case where the user did not give sufficient permissions to
			// the bot, the error responses wouldn't block the update of the message in the database.

			// fetch the channel id from the database
			// this channel is where the logs will be sent to
			// wrap the embed and send
			MessageEmbed mb = eb.build();
			String channelIdToSendTo = dc.getGuildDataAccess().retrieveRegisteredChannel(guildId, Schema.MESSAGE_LOG_REGISTRATION_TABLE);
			Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
		});
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
			
		String guildId = event.getGuild().getId();

		if(!dc.getGuildDataAccess().isGuildRegistered(guildId, Schema.MESSAGE_LOG_REGISTRATION_TABLE)) {
			return;
		}

		vThreadPool.execute(()-> {
			// get the message id of the message which was deleted
			String messageId = event.getMessageId();

			// if the message id does not exist in db, it means the message has not been logged in the first place
			if(!dc.getMessageDataAccess().messageExists(messageId)) {
				return;
			}

			// retrieve the channel id where the logs must be sent
			String channelIdToSendTo = dc.getGuildDataAccess().retrieveRegisteredChannel(guildId, Schema.MESSAGE_LOG_REGISTRATION_TABLE);
			// retrieve the stored message in the database which was deleted
			AuthorAndMessageEntity ame = dc.getMessageDataAccess().retrieveAuthorAndMessage(messageId);

			User author = event.getJDA().getUserById(ame.authorId());
			String mentionableAuthor = (author !=null ? author.getAsMention() : ame.authorId());

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🗑️ Message Delete Event");
			eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted");
			eb.setColor(Color.RED);
			eb.addField("Deleted Message", ame.messageContent(), false);

			eb.setFooter(event.getGuild().getName());
			eb.setTimestamp(Instant.now());

			// delete the message from the database
			withMessageLock(messageId, ()->dc.getMessageDataAccess().deleteMessage(messageId));
			// the reason this is above the send queue is because in case where the user did not give sufficient permissions to
			// the bot, (such as no send message permissions) the exceptions wouldn't block the deletion in the database.

			// send the fetched deleted message to the logging channel
			MessageEmbed mb = eb.build();
			Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
		});
	}
}
