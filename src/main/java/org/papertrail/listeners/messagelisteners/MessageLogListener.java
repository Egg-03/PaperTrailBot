package org.papertrail.listeners.messagelisteners;

import java.awt.Color;
import java.sql.SQLException;
import java.time.Instant;
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
	public MessageLogListener(DatabaseConnector dc) {
		this.dc = dc;
		
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
				Logger.error(e, "Could not log message");
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
							
				// fetch the old message content and its author from the database
				List<String> oldAuthorAndMessage = dc.retrieveAuthorAndMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				if(oldAuthorAndMessage==null || oldAuthorAndMessage.isEmpty()) { // would be true only if the unedited message was not logged in the first place
					return;
				}
				// fetch the updated message from the event
				String updatedMessage = event.getMessage().getContentRaw();	
				
				// Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
				// This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
				if(updatedMessage.equals(oldAuthorAndMessage.getLast())) {
					return;
				}
							
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("📝 Message Edit Event");
				eb.setDescription("A message sent by "+event.getAuthor().getAsMention()+" has been edited in: "+event.getJumpUrl());
				eb.setColor(Color.YELLOW);
				
				eb.addField("Old Message", oldAuthorAndMessage.getLast(), false); // get only the message and not the author
				eb.addField("New Message", updatedMessage, false);
				
				eb.setFooter(event.getGuild().getName());
				eb.setTimestamp(Instant.now());
				// update the database with the new message
				dc.updateMessage(messageId, updatedMessage, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				// the reason this is above the send queue is because in case where the user did not give sufficient permissions to
				// the bot, the error responses wouldn't block the update of the message in the database.		
				
				// fetch the channel id from the database
				// this channel is where the logs will be sent to
				// wrap the embed and send
				MessageEmbed mb = eb.build();
				String channelIdToSendTo = dc.retrieveRegisteredChannelId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();							
			} catch (SQLException e) {
				Logger.error(e, "Could not log updated message");
				e.printStackTrace();		
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
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("🗑️ Message Delete Event");
				eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted");
				eb.setColor(Color.RED);
				eb.addField("Deleted Message", oldAuthorAndMessage.getLast(), false);
				
				eb.setFooter(event.getGuild().getName());
				eb.setTimestamp(Instant.now());
				
				// delete the message from the database 
				dc.deleteMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				// the reason this is above the send queue is because in case where the user did not give sufficient permissions to
				// the bot, the error responses wouldn't block the deletion in the database.
				
				// send the fetched deleted message to the logging channel
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();			
				
			} catch (SQLException e) {
				Logger.error(e, "Could not delete message");
				e.printStackTrace();
				
			}
		}
	}
}
