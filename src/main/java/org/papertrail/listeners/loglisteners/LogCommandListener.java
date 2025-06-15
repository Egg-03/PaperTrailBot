package org.papertrail.listeners.loglisteners;

import java.awt.Color;
import java.sql.SQLException;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LogCommandListener extends ListenerAdapter {

	private DatabaseConnector dc;
	private final EmbedBuilder eb = new EmbedBuilder();

	public LogCommandListener(DatabaseConnector dc) {
		this.dc = dc;
		eb.setTitle("Audit Log Configuration");
		eb.setColor(Color.CYAN);
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		// Command for binding the listener to a channel
		if (event.getName().equals("ralc")) {
			
			// Only members with MANAGE_SERVER permissions should be able to use this command
			Member member = event.getMember();
	        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
	            event.reply("❌ You don't have the permission required to use this command.")
	                 .setEphemeral(true)
	                 .queue();
	            return;
	        }
			
			String guildId = event.getGuild().getId();
			// retrieve the previously registered channel_id associated with the given
			// guild_id
			String registeredChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			// if there is a registered channel_id in the database, send a warning message
			// in the channel where the command was called from, stating that a channel has
			// already been registered
			if (registeredChannelId != null && !registeredChannelId.isBlank()) {
				
				GuildChannel registeredChannel = event.getGuild().getGuildChannelById(registeredChannelId);
				eb.addField("Audit Log Channel Already Registered", (registeredChannel !=null ? registeredChannel.getAsMention() : registeredChannelId)+ " has already been selected as the audit log channel", false);
				eb.setColor(Color.YELLOW);
				
				MessageEmbed mb = eb.build();
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
				return;
			}

			// if there is no channel registered, get the channel id from where the command
			// was called
			String channelIdToRegister = event.getChannel().asTextChannel().getId();
			try {
				// register the channel_id along with guild_id in the database
				dc.registerGuildAndChannel(guildId, channelIdToRegister, TableNames.AUDIT_LOG_TABLE);
				
				eb.addField("Audit Log Channel Registration","All audit log info will be logged here", false);
				eb.setColor(Color.GREEN);
				MessageEmbed mb = eb.build();
				
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
				
			} catch (SQLException e) {
				
				eb.addField("Audit Log Channel Registration Failure","Channel could not be registered", false);
				eb.setColor(Color.BLACK);
				MessageEmbed mb = eb.build();
				
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
				
				e.printStackTrace();
				// TODO log
			}
		}

		// Command for getting the channel where audit logs are posted
		if (event.getName().equals("galc")) {
			
			// Only members with MANAGE_SERVER permissions should be able to use this command
			Member member = event.getMember();
	        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
	            event.reply("❌ You don't have the permission required to use this command.")
	                 .setEphemeral(true)
	                 .queue();
	            return;
	        }

			String guildId = event.getGuild().getId();

			// retrieve the channel_id registered in the database
			String registeredChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			// if there is no channel_id for the given guild_id in the database, then inform
			// the user of the same, else link the channel that has been registered
			if (registeredChannelId == null || registeredChannelId.isBlank()) {
				eb.addField("Audit Log Registration Check", "No channel has been registered for audit logs", false);
				eb.setColor(Color.RED);
				MessageEmbed mb = eb.build();
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
			} else {
				// check if the channelId actually exists in the guild
				// this is particularly useful when a channel that was set for logging may have been deleted
				GuildChannel registeredChannel =  event.getJDA().getGuildChannelById(registeredChannelId);
				if(registeredChannel==null) {
					eb.addField("Audit Log Registration Check", registeredChannelId+" does not exist. Please remove it using `//ualc` and re-register using `//ralc`", false);
				} else {
					eb.addField("Audit Log Registration Check", registeredChannel.getAsMention()+ " has been registered as the audit log channel", false);
				}
				
				MessageEmbed mb = eb.build();
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
			}
		}

		// Command for un-setting a previously bound listener from a channel
		if (event.getName().equals("ualc")) {
			
			// Only members with MANAGE_SERVER permissions should be able to use this command
			Member member = event.getMember();
	        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
	            event.reply("❌ You don't have the permission required to use this command.")
	                 .setEphemeral(true)
	                 .queue();
	            return;
	        }

			String guildId = event.getGuild().getId();
			String registeredChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			if (registeredChannelId == null || registeredChannelId.isBlank()) {
				eb.addField("Audit Log Channel Removal", "No channel has been registered for audit logs", false);
				eb.setColor(Color.RED);
				MessageEmbed mb = eb.build();
				
				event.replyEmbeds(mb).setEphemeral(false).queue();
				
				eb.clearFields();
				
			} else {
				try {
					
					dc.unregisterGuildAndChannel(guildId, TableNames.AUDIT_LOG_TABLE);
					
					eb.addField("Audit Log Channel Removal", "Channel successfully unset", false);
					eb.setColor(Color.GREEN);
					MessageEmbed mb = eb.build();
					
					event.replyEmbeds(mb).setEphemeral(false).queue();
					
					eb.clearFields();
				} catch (SQLException e) {
					eb.addField("Audit Log Channel Removal Failure", "Channel could not be removed", false);
					eb.setColor(Color.BLACK);
					MessageEmbed mb = eb.build();
					
					event.replyEmbeds(mb).setEphemeral(false).queue();
					
					eb.clearFields();
					e.printStackTrace();
				}
			}

		}
	}
}
