package org.papertrail.listeners.customlisteners;

import java.awt.Color;
import java.time.Instant;

import org.papertrail.version.AuthorInfo;
import org.papertrail.version.VersionInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotInfoListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if(event.getName().equals("about")) {
				
			EmbedBuilder eb = new EmbedBuilder(); 
			eb.setTitle("📊 About "+VersionInfo.APPNAME+" 📊");
			eb.setDescription("👤 Developed By: **"+AuthorInfo.AUTHOR_NAME+"**");
			eb.setThumbnail(AuthorInfo.AUTHOR_AVATAR_URL);
			eb.setColor(Color.PINK);
			
			eb.addField("🏷️ App Name", "╰┈➤"+VersionInfo.APPNAME, false);
			eb.addField("⚙️ App Version", "╰┈➤"+VersionInfo.VERSION, false);
			eb.addField("🗂️ App Source Code", VersionInfo.PROJECT_LINK, false);
			
			eb.setFooter(VersionInfo.APPNAME+" is free and open source ");
			eb.setTimestamp(Instant.now());
			
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
		}
		
	}
}
