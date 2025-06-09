package listeners;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {
	
	private String textChannel;
	 @Override
	 public void onMessageReceived(MessageReceivedEvent event) {
		 if(event.getMessage().getContentRaw().equals("!r")) {
			 textChannel=event.getChannel().asTextChannel().getId();
			 event.getGuild().getTextChannelById(textChannel).sendMessage("All reaction info will be logged here").queue();
		 }	 
	 }
	 
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if(textChannel==null ||textChannel.isBlank()) {
			 return;
		 }
		//callback for REST Action
		//does not require cache
		//Since cache is enabled, retrieveUser() returns getUser() instead and skips the REST Action
		event.retrieveUser().queue(user->{
			if(!user.isBot()) {
				String emoji = event.getReaction().getEmoji().getAsReactionCode();
				String channelMention = event.getChannel().getAsMention();
				String jumpURL = event.getJumpUrl();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Reaction Log Entry");
				eb.setDescription(user.getAsMention()+" has reacted to a message: ");
				
				eb.addField("URL: ", jumpURL, false);
				eb.addField("With Emoji: ", emoji, false);
				eb.addField("In Channel: ", channelMention, false);
				
				eb.setColor(Color.CYAN);
				
				MessageEmbed mb = eb.build(); 
				event.getGuild().getTextChannelById(textChannel).sendMessageEmbeds(mb).queue();
			}
		});
	}
	
	@Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		
		if(textChannel==null ||textChannel.isBlank()) {
			 return;
		 }
		
		event.retrieveUser().queue(user -> {
			if(!user.isBot()) {
				String emoji = event.getReaction().getEmoji().getAsReactionCode();
				String channelMention = event.getChannel().getAsMention();
				String jumpURL = event.getJumpUrl();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Reaction Log Entry");
				eb.setDescription(user.getAsMention()+" has removed reactions from a message: ");
				
				eb.addField("URL: ", jumpURL, false);
				eb.addField("With Emoji: ", emoji, false);
				eb.addField("In Channel: ", channelMention, false);
				
				eb.setColor(Color.CYAN);
				
				MessageEmbed mb = eb.build(); 
				event.getGuild().getTextChannelById(textChannel).sendMessageEmbeds(mb).queue();
			}
		});
	}
}
