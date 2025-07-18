package org.papertrail.listeners.commandlisteners;

import java.awt.Color;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.tinylog.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogSetupCommandListener extends ListenerAdapter {
	
	private DatabaseConnector dc;
	private final EmbedBuilder eb = new EmbedBuilder();

	public MessageLogSetupCommandListener(DatabaseConnector dc) {
		this.dc = dc;
		eb.setTitle("📝 Message Log Configuration");
		eb.setColor(Color.CYAN);
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
			
		switch(event.getName()) {
		
		case "messagelogchannel-set":
			setMessageLogging(event);
			break;
			
		case "messagelogchannel-view":
			retrieveMessageLoggingChannel(event);
			break;
			
		case "messagelogchannel-remove":
			unsetMessageLogging(event);
			break;
			
		default:
			break;
		}
	}

	private void setMessageLogging(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		Guild guild = event.getGuild();
		String guildId = guild.getId();
			
		String registeredChannelId = dc.getGuildDataAccess().retrieveRegisteredChannel(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		
		if(registeredChannelId!=null && !registeredChannelId.isBlank()) {
			
			GuildChannel registeredChannel = event.getGuild().getGuildChannelById(registeredChannelId);
			eb.addField("⚠️ Channel Already Registered", "╰┈➤"+(registeredChannel !=null ? registeredChannel.getAsMention() : registeredChannelId)+ " has already been selected as the message log channel", false);
			eb.setColor(Color.YELLOW);
			
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
			
			eb.clearFields();
			return;
		}
		
		String channelIdToRegister = event.getChannelId();
		try {
			// register the channel_id along with guild_id in the database
			dc.getGuildDataAccess().registerGuildAndChannel(guildId, channelIdToRegister, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
			
			eb.addField("✅ Channel Registration Success","╰┈➤"+"All edited and deleted messages will be logged here", false);
			eb.setColor(Color.GREEN);
			MessageEmbed mb = eb.build();
			
			event.replyEmbeds(mb).setEphemeral(false).queue();
			
			eb.clearFields();
			
		} catch (Exception e) {
			
			eb.addField("❌ Channel Registration Failure","╰┈➤"+"Channel could not be registered", false);
			eb.setColor(Color.BLACK);
			MessageEmbed mb = eb.build();
			
			event.replyEmbeds(mb).setEphemeral(false).queue();
			
			eb.clearFields();
			
			Logger.error(e, "Message Log Channel could not be registered");
		}
		
	}
	
	private void retrieveMessageLoggingChannel(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		Guild guild = event.getGuild();
		String guildId = guild.getId();
			
		String registeredChannelId = dc.getGuildDataAccess().retrieveRegisteredChannel(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		// if there is no channel_id for the given guild_id in the database, then inform
		// the user of the same, else link the channel that has been registered
		if (registeredChannelId == null || registeredChannelId.isBlank()) {
			eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for message logging", false);
			eb.setColor(Color.YELLOW);
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();

			eb.clearFields();
		} else {
			// check if the channelId actually exists in the guild
			// this is particularly useful when a channel that was set for logging may have been deleted
			GuildChannel registeredChannel =  event.getJDA().getGuildChannelById(registeredChannelId);
			if(registeredChannel==null) {
				eb.addField("⚠️ Channel Registration Check", "╰┈➤"+registeredChannelId+" does not exist. Please remove it using `/messagelogchannel-remove` and re-register using `/messagelogchannel-set`", false);
				eb.setColor(Color.RED);
			} else {
				eb.setColor(Color.CYAN);
				eb.addField("✅ Channel Registration Check", "╰┈➤"+registeredChannel.getAsMention()+ " is found to be registered as the message log channel", false);
			}

			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();

			eb.clearFields();
		}
	}
	
	private void unsetMessageLogging(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		Guild guild = event.getGuild();
		String guildId = guild.getId();
			
		String registeredChannelId = dc.getGuildDataAccess().retrieveRegisteredChannel(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		
		if (registeredChannelId == null || registeredChannelId.isBlank()) {
			eb.addField("ℹ️ Channel Removal", "╰┈➤"+"No channel has been registered for message logs", false);
			eb.setColor(Color.YELLOW);
			MessageEmbed mb = eb.build();
			
			event.replyEmbeds(mb).setEphemeral(false).queue();
			
			eb.clearFields();
			
		} else {
			try {
				
				dc.getGuildDataAccess().unregister(guildId, TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
				
				eb.addField("✅ Channel Removal", "╰┈➤"+"Channel successfully unset", false);
				eb.setColor(Color.GREEN);
				MessageEmbed mb = eb.build();
				
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
			} catch (Exception e) {
				eb.addField("❌ Channel Removal Failure", "╰┈➤"+"Channel could not be unset", false);
				eb.setColor(Color.BLACK);
				MessageEmbed mb = eb.build();
				
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
				Logger.error(e, "Could not un-register message log channel");
			}
		}
		
	}

}
