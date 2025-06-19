package org.papertrail.listeners.messagelisteners;

import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.tinylog.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogListener extends ListenerAdapter {
	
	private DatabaseConnector dc;
	private final EmbedBuilder eb = new EmbedBuilder();

	public MessageLogListener(DatabaseConnector dc) {
		this.dc = dc;
		eb.setTitle("📝 Message Log Event");
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
		String registeredGuildId = dc.checkGuildRegistration(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		
		// if not registered, exit
		if(registeredGuildId==null) {
			return;
		}
		
		// else if the registered guild id matches with the event fetched guild id, log the message with its ID and author
		if(registeredGuildId.equals(guildId)) {
			try {
				String messageId = event.getMessageId();
				dc.logMessage(messageId, event.getMessage().getContentRaw(), event.getAuthor().getId(), TableNames.MESSAGE_LOG_CONTENT_TABLE);
			} catch (SQLException e) {
				Logger.error("Could not log message", e);
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {

		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}
		
		String guildId = event.getGuild().getId();

		String registeredGuildId = dc.checkGuildRegistration(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		if(registeredGuildId==null) {
			return;
		}

		if(registeredGuildId.equals(guildId)) {
			try {
				// get the message id of the message which was updated
				String messageId = event.getMessageId();
				// fetch the channel id from the database
				// this channel is where the logs will be sent to
				String channelIdToSendTo = dc.retrieveRegisteredChannelId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				
				// fetch the old message content and its author from the database
				List<String> oldAuthorAndMessage = dc.retrieveAuthorAndMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				
				// fetch the updated message from the event
				String updatedMessage = event.getMessage().getContentRaw();				
				String mentionableAuthor = event.getAuthor().getAsMention();
				
				eb.setDescription("A message sent by "+mentionableAuthor+" has been edited in: "+event.getJumpUrl());
				eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
				eb.setColor(Color.YELLOW);
				eb.addField("Old Message", oldAuthorAndMessage.getLast(), false); // get only the message and not the author
				eb.addField("New Message", updatedMessage, false);
				
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
				eb.clearFields();
				// update the database with the new message
				dc.updateMessage(messageId, updatedMessage, TableNames.MESSAGE_LOG_CONTENT_TABLE);
			} catch (SQLException e) {
				Logger.error("Could not log updated message", e);
				e.printStackTrace();
				eb.clearFields();
			}
		}
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
			
		String guildId = event.getGuild().getId();

		String registeredGuildId = dc.checkGuildRegistration(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		if(registeredGuildId==null) {
			return;
		}

		if(registeredGuildId.equals(guildId)) {
			try {
				// get the message id of the message which was deleted
				String messageId = event.getMessageId();
				// if the message id does not exist in db, it means the message has not been logged in the first place
				if(dc.checkMessageId(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE)==null) {
					return;
				}
				
				// retrieve the channel id where the logs must be sent
				String channelIdToSendTo = dc.retrieveRegisteredChannelId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				// retrieve the stored message in the database which was deleted
				List<String> oldAuthorAndMessage = dc.retrieveAuthorAndMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				
				User author = event.getJDA().getUserById(oldAuthorAndMessage.getFirst());
				String mentionableAuthor = (author !=null ? author.getAsMention() : oldAuthorAndMessage.getFirst());
				
				eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted");
				eb.setColor(Color.RED);
				eb.addField("Deleted Message", oldAuthorAndMessage.getLast(), false);
				
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
				eb.clearFields();
				// delete the message from the database after logging
				dc.deleteMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
			} catch (SQLException e) {
				Logger.error("Could not delete message", e);
				e.printStackTrace();
				eb.clearFields();
			}
		}
	}
}
