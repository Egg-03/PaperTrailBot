package org.papertrail.listeners.voicelisteners;

import java.awt.Color;
import java.time.Instant;

import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildVoiceListener extends ListenerAdapter {
	
	private DatabaseConnector dc;

	public GuildVoiceListener(DatabaseConnector dc) {
		this.dc=dc;
	}
	
	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		
		// this will return a non-null text id if a channel was previously registered in
		// the database
		String registeredChannelId = dc.retrieveChannelId(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);

		if (registeredChannelId == null || registeredChannelId.isBlank()) {
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("User Voice Activity Logging");
		
	    Member member = event.getMember();
	    AudioChannel left = event.getOldValue(); // can be null if user joined for first time
	    AudioChannel joined = event.getNewValue(); // can be null if user left
	    
	    if(left==null && joined!=null) {
	    	// User has joined a vc
	    	eb.setDescription("A Member has joined a voice channel");
			eb.setColor(Color.GREEN);
			eb.addField("Member Joined", member.getAsMention()+" joined the voice channel "+joined.getAsMention(), false);
	    }
	    
	    if (left != null && joined != null) {
	        // Moved from one channel to another
	    	eb.setDescription("A Member has switched voice channels");
			eb.setColor(Color.YELLOW);
			eb.addField("Member Switched Channels", member.getAsMention()+" joined the switched from channel "+left.getAsMention()+ " to "+joined.getAsMention(), false);
	    }
	    
	    if (left!=null && joined==null) {
	        // User disconnected voluntarily (or was disconnected by a moderator)
	    	eb.setDescription("A Member has left a voice channel");
			eb.setColor(Color.RED);
			eb.addField("Member Left A Voice Channel", member.getAsMention()+" left the voice channel "+left.getAsMention(), false);
	    }
	    
	    eb.setFooter("Voice Activity Detection");
		eb.setTimestamp(Instant.now());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(registeredChannelId).sendMessageEmbeds(mb).queue();
	}
}
