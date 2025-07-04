package org.papertrail.listeners.customlisteners;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.papertrail.utilities.EnvConfig;
import org.papertrail.version.AuthorInfo;
import org.papertrail.version.ProjectInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class AnnouncementListener extends ListenerAdapter {
	
	private final DatabaseConnector dc;
	
	public AnnouncementListener(DatabaseConnector dc) {
		this.dc = dc;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if(!event.getUser().getId().equals(EnvConfig.get("DEVELOPER_OR_HOSTER_ID"))) { // devID
			event.reply("❌ Only the developer/hoster can use this command.").setEphemeral(true).queue();
			return;
		}		
			
		if(event.getName().equals("announcement")) {
			
			List<String> registeredChannelList = dc.retrieveAllRegisteredChannels(TableNames.AUDIT_LOG_TABLE);
			if(registeredChannelList.isEmpty()) {
				return;
			}
			
			EmbedBuilder eb = new EmbedBuilder(); 
			eb.setTitle("📣 " +ProjectInfo.APPNAME+" Announcement: "+event.getOption("type").getAsString());
			eb.setDescription("📝 " +event.getOption("description").getAsString());
			eb.setThumbnail(AuthorInfo.AUTHOR_AVATAR_URL);
			eb.setColor(Color.WHITE);
						
			eb.addField("🏷️ Detail", "╰┈➤"+event.getOption("detail").getAsString(), false);
			OptionMapping extra = event.getOption("extra");
			if(extra!=null) {
				eb.addField("🏷️ Extras", "╰┈➤"+extra.getAsString(), false);
			}
				
			eb.setFooter(ProjectInfo.APPNAME+" "+ProjectInfo.VERSION);
			eb.setTimestamp(Instant.now());
			
			MessageEmbed mb = eb.build();
			event.reply("Announcing to: "+registeredChannelList.size()+" channels").setEphemeral(true).queue();
			
			for(String registeredChannel: registeredChannelList) {
				TextChannel channelToSendTo = event.getJDA().getTextChannelById(registeredChannel);
				if(channelToSendTo!=null) {
					event.getJDA().getTextChannelById(registeredChannel).sendMessageEmbeds(mb).queue(); // TODO implement rate-limitation beyond JDA's system
				}	
			}
		}		
	}
}
