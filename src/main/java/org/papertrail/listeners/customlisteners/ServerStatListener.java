package org.papertrail.listeners.customlisteners;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

import org.papertrail.utilities.DurationFormatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerStatListener extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if(event.getName().equals("stats")) {
			
			Guild guild = event.getGuild();
			
			List<Member> members = guild.getMemberCache().asList();
			Integer botCount = 0;
			Integer userCount = 0;
			Integer onlineUserCount = 0;
			for(Member member: members) {
				if(member.getUser().isBot()) {
					botCount++;
				}
					
				else {
					OnlineStatus status = member.getOnlineStatus();
					if(status.equals(OnlineStatus.DO_NOT_DISTURB) || status.equals(OnlineStatus.IDLE) | status.equals(OnlineStatus.ONLINE)) {
						onlineUserCount++;
					}
					userCount++;
				}
			}
			
			EmbedBuilder eb = new EmbedBuilder(); 
			eb.setTitle("📊 Server Statistics 📊");
			eb.setDescription("📁 Server Statistics For: **"+guild.getName()+"**");
			eb.setThumbnail(guild.getIconUrl());
			eb.setColor(Color.PINK);
			
			eb.addField("🏠 Guild Name", guild.getName(), false);
			eb.addField("👑 Guild Owner", guild.getMemberById(guild.getOwnerId()).getAsMention(), false);
			eb.addField("📅 Guild Created On", DurationFormatter.isoToLocalTimeCounter(guild.getTimeCreated()), false);
			eb.addField("🔗 Guild Vanity URL", (guild.getVanityUrl() !=null ? guild.getVanityUrl() : "Not Set"), false);
			
			eb.addField("👥 Member Count", String.valueOf(userCount), true);
			eb.addField("🤖 Bot Count", String.valueOf(botCount), true);	
			eb.addField("🟢 Members Online", onlineUserCount+"/"+userCount, true);
				
			List<Member> boosters = guild.getBoosters();
			StringBuilder mentionableBoosters = new StringBuilder();
			for(Member booster: boosters) {
				mentionableBoosters.append(booster.getAsMention()).append(" ");
			}
			eb.addField("🚀 Guild Boosters ", mentionableBoosters.toString(), false);
			eb.addField("💖 Guild Boost Count", String.valueOf(guild.getBoostCount()), false);
			eb.addField("📎 Booster Role", (guild.getBoostRole() !=null ? guild.getBoostRole().getAsMention() : "No Boost Role Found"), false);
			eb.addField("🗼 Boost Tier", guild.getBoostTier().toString(), false);
			
			eb.addField("🌐 Locale", guild.getLocale().getNativeName(), true);
			eb.addField("🔒 Verification", guild.getVerificationLevel().name(), true);
			eb.addField("🧱 Roles", String.valueOf(guild.getRoles().size()), true);
			eb.addField("🗂️ Categories", String.valueOf(guild.getCategories().size()), true);
			eb.addField("💬 Text Channels", String.valueOf(guild.getTextChannels().size()), true);
			eb.addField("🔊 Voice Channels", String.valueOf(guild.getVoiceChannels().size()), true);

			eb.addField("📋 Data Requested By", event.getMember().getAsMention(), false);
			eb.setFooter("📋 Stats By: PaperTrail 📋");
			eb.setTimestamp(Instant.now());
			
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
		}
		
	}
}
