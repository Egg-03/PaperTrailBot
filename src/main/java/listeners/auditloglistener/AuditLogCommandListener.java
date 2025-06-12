package listeners.auditloglistener;

import java.awt.Color;
import java.sql.SQLException;

import database.DatabaseConnector;
import database.TableNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogCommandListener extends ListenerAdapter {

	private DatabaseConnector dc;
	private final EmbedBuilder eb = new EmbedBuilder();

	public AuditLogCommandListener(DatabaseConnector dc) {
		this.dc = dc;
		eb.setTitle("Audit Log Configuration");
		eb.setColor(Color.CYAN);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		// Command for binding the listener to a channel
		if (event.getMessage().getContentRaw().equals("!alc")) {
			
			String guildId = event.getGuild().getId();
			// retrieve the previously registered channel_id associated with the given
			// guild_id
			String getRegisteredChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			// if there is a registered channel_id in the database, send a warning message
			// in the channel where the command was called from, stating that a channel has
			// already been registered
			if (getRegisteredChannelId != null && !getRegisteredChannelId.isBlank()) {
				
				eb.addField("Audit Log Channel Already Registered",event.getJDA().getTextChannelById(getRegisteredChannelId).getAsMention()+ " has already been selected as the audit log channel", false);
				
				MessageEmbed mb = eb.build();
				event.getGuild()
					 .getTextChannelById(event.getChannel().asTextChannel().getId()).sendMessageEmbeds(mb)
					 .queue();
				
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
				MessageEmbed mb = eb.build();
				
				event.getGuild().getTextChannelById(channelIdToRegister).sendMessageEmbeds(mb).queue();
				
				eb.clearFields();
				
			} catch (SQLException e) {
				
				eb.addField("Audit Log Channel Registration Failure","Channel could not be registered", false);
				MessageEmbed mb = eb.build();
				
				event.getGuild().getTextChannelById(channelIdToRegister).sendMessageEmbeds(mb).queue();
				
				eb.clearFields();
				
				e.printStackTrace();
				// TODO log
			}
		}

		// Command for getting a bound listener
		if (event.getMessage().getContentRaw().equals("!galc")) {

			String guildId = event.getGuild().getId();

			// retrieve the channel_id registered in the database
			String textChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			// if there is no channel_id for the given guild_id in the database, then inform
			// the user of the same, else link the channel that has been registered
			if (textChannelId == null || textChannelId.isBlank()) {
				eb.addField("Audit Log Registration Check", "No channel has been registered for audit logs", false);
				
				MessageEmbed mb = eb.build();
				event.getGuild()
					 .getTextChannelById(event.getChannel().asTextChannel().getId()).sendMessageEmbeds(mb)
					 .queue();
				
				eb.clearFields();
			} else {
				// check if the channelId actually exists in the guild
				// this is particularly useful when a channel that was set for logging may have been deleted
				TextChannel registeredChannel =  event.getJDA().getTextChannelById(textChannelId);
				if(registeredChannel==null) {
					eb.addField("Audit Log Registration Check", textChannelId+" does not exist. Please remove it using `!ualc` and re-register using `!alc`", false);
				} else {
					eb.addField("Audit Log Registration Check", registeredChannel.getAsMention()+ " has been registered as the audit log channel", false);
				}
				
				MessageEmbed mb = eb.build();
				event.getGuild()
					 .getTextChannelById(event.getChannel().asTextChannel().getId())
					 .sendMessageEmbeds(mb)
					 .queue();
				
				eb.clearFields();
			}
		}

		// Command for un-setting a previously bound listener from a channel
		if (event.getMessage().getContentRaw().equals("!ualc")) {

			String guildId = event.getGuild().getId();
			String textChannelId = dc.retrieveChannelId(guildId, TableNames.AUDIT_LOG_TABLE);

			if (textChannelId == null || textChannelId.isBlank()) {
				eb.addField("Audit Log Channel Removal", "No channel has been registered for audit logs", false);
				MessageEmbed mb = eb.build();
				
				event.getGuild()
					 .getTextChannelById(event.getChannel().asTextChannel().getId())
					 .sendMessageEmbeds(mb)
					 .queue();
				
				eb.clearFields();
				
			} else {
				try {
					
					dc.unregisterGuildAndChannel(guildId, TableNames.AUDIT_LOG_TABLE);
					
					eb.addField("Audit Log Channel Removal", "Channel successfully unset", false);
					MessageEmbed mb = eb.build();
					
					event.getGuild()
						 .getTextChannelById(event.getChannel().asTextChannel().getId())
						 .sendMessageEmbeds(mb)
						 .queue();
					
					eb.clearFields();
				} catch (SQLException e) {
					eb.addField("Audit Log Channel Removal Failure", "Channel could not be removed", false);
					MessageEmbed mb = eb.build();
					
					event.getGuild()
						 .getTextChannelById(event.getChannel().asTextChannel().getId())
						 .sendMessageEmbeds(mb)
						 .queue();
					
					eb.clearFields();
					e.printStackTrace();
				}
			}

		}
	}
}
