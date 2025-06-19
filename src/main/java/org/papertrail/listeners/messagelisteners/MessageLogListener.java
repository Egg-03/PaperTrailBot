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
		
		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}
			
		String guildId = event.getGuild().getId();		
		String registeredGuildId = dc.retrieveRegisteredGuildId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		
		if(registeredGuildId==null) {
			return;
		}
		
		if(registeredGuildId.equals(guildId)) {
			try {
				String messageId = event.getMessageId();
				dc.logMessage(messageId, event.getMessage().getContentDisplay(), event.getAuthor().getId(), TableNames.MESSAGE_LOG_CONTENT_TABLE);
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

		String registeredGuildId = dc.retrieveRegisteredGuildId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		if(registeredGuildId==null) {
			return;
		}

		if(registeredGuildId.equals(guildId)) {
			try {
				String messageId = event.getMessageId();
				String channelIdToSendTo = dc.retrieveRegisteredChannelId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				
				List<String> oldAuthorAndMessage = dc.retrieveAuthorAndMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				String updatedMessage = event.getMessage().getContentDisplay();
				
				eb.setDescription("A message has been edited in: "+event.getJumpUrl());
				eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
				eb.setColor(Color.YELLOW);
				eb.addField("Old Message", oldAuthorAndMessage.getLast(), false); // get only the message and not the author
				eb.addField("New Message", updatedMessage, false);
				
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
				eb.clearFields();
				// log the new message replacing the old one
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

		String registeredGuildId = dc.retrieveRegisteredGuildId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		if(registeredGuildId==null) {
			return;
		}

		if(registeredGuildId.equals(guildId)) {
			try {
				String messageId = event.getMessageId();
				// TODO if message id not found in db ,return
				String channelIdToSendTo = dc.retrieveRegisteredChannelId(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				List<String> oldAuthorAndMessage = dc.retrieveAuthorAndMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
				
				User author = event.getJDA().getUserById(oldAuthorAndMessage.getFirst());
				String mentionableAuthor = (author !=null ? author.getAsMention() : oldAuthorAndMessage.getFirst());
				
				eb.setDescription("A message sent by"+mentionableAuthor+"has been deleted");
				eb.setColor(Color.RED);
				eb.addField("Deleted Message", oldAuthorAndMessage.getLast(), false);
				
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
				eb.clearFields();
				// delete the message from the db after displaying
				dc.deleteMessage(messageId, TableNames.MESSAGE_LOG_CONTENT_TABLE);
			} catch (SQLException e) {
				Logger.error("Could not delete message", e);
				e.printStackTrace();
				eb.clearFields();
			}
		}
	}

}
