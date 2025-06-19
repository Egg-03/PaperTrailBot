package org.papertrail.listeners.memberlisteners;

import java.awt.Color;
import java.time.Instant;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.papertrail.utilities.DurationFormatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoinAndLeaveListener extends ListenerAdapter {
	
	private DatabaseConnector dc;

	public GuildMemberJoinAndLeaveListener (DatabaseConnector dc) {
		this.dc=dc;
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		
		// this will return a non-null text id if a channel was previously registered in
		// the database
		// guild member join and leave events are mapped to audit log table
		String registeredChannelId = dc.retrieveRegisteredChannelId(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);

		if (registeredChannelId == null || registeredChannelId.isBlank()) {
			return;
		}
		
		Guild guild = event.getGuild();
		User user = event.getUser();
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("🛬 Member Join Event");
		eb.setDescription("A Member has joined "+guild.getName());
		eb.setColor(Color.GREEN);
		
		eb.addField("🏷️ Member Name", "╰┈➤"+user.getName(), false);
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("ℹ️ Member Mention", "╰┈➤"+user.getAsMention(), false);
		eb.addField("🆔 Member ID", "╰┈➤"+user.getId(), false);
		eb.addField("📅 Account Created", "╰┈➤"+DurationFormatter.isoToLocalTimeCounter(user.getTimeCreated()), false);
		eb.addField("🤖 Is Application ?", "╰┈➤"+((Boolean.TRUE.equals(user.isBot())) ? "✅" : "❌"), false);
		eb.setFooter("Member Join Detection");
		eb.setTimestamp(Instant.now());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(registeredChannelId).sendMessageEmbeds(mb).queue();
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		
		// this will return a non-null text id if a channel was previously registered in
		// the database
		String registeredChannelId = dc.retrieveRegisteredChannelId(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);

		if (registeredChannelId == null || registeredChannelId.isBlank()) {
			return;
		}
		
		Guild guild = event.getGuild();
		User user = event.getUser();
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("🛫 Member Leave Event");
		eb.setDescription("A Member has left "+guild.getName());
		eb.setColor(Color.RED);
		
		eb.addField("🏷️ Member Name", "╰┈➤"+user.getName(), false);
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("🆔 Member ID", "╰┈➤"+user.getId(), false);
		
		eb.setFooter("Member Leave Detection");
		eb.setTimestamp(Instant.now());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(registeredChannelId).sendMessageEmbeds(mb).queue();
	}
}
