package org.papertrail.listeners.customlisteners;

import java.awt.Color;
import java.time.Instant;

import org.papertrail.version.AuthorInfo;
import org.papertrail.version.ProjectInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotInfoListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if(event.getName().equals("about")) {
				
			EmbedBuilder eb = new EmbedBuilder(); 
			eb.setTitle("📊 About "+ProjectInfo.APPNAME+" 📊");
			eb.setDescription("👤 Developed By: **"+AuthorInfo.AUTHOR_NAME+"**");
			eb.setThumbnail(AuthorInfo.AUTHOR_AVATAR_URL);
			eb.setColor(Color.PINK);
			
			eb.addField("🏷️ App Name", "╰┈➤"+ProjectInfo.APPNAME, false);
			eb.addField("⚙️ App Version", "╰┈➤"+ProjectInfo.VERSION, false);
			eb.addField("📃 App Source Code", "╰┈➤"+"[GitHub]("+ProjectInfo.PROJECT_LINK+")", false);
			eb.addField("🔐 Privacy Policy", "╰┈➤"+"[Privacy.md]("+ProjectInfo.PRIVACY+")", false);
			eb.addField("🤝 Terms of Use", "╰┈➤"+"[Terms.md]("+ProjectInfo.TERMS+")", false);
				
			eb.setFooter(ProjectInfo.APPNAME+" "+ProjectInfo.VERSION);
			eb.setTimestamp(Instant.now());
			
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
		}
		
	}
}
