package listeners;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		//callback for REST Action
		//does not require cache
		//Since cache is enabled, retrieveUser() returns getUser() instead and skips the REST Action
		event.retrieveUser().queue(user->{
			if(!user.isBot()) {
				String emoji = event.getReaction().getEmoji().getAsReactionCode();
				String channelMention = event.getChannel().getAsMention();
				String jumpURL = event.getJumpUrl();
				
				String reply = user.getName()+" has reacted to a comment in "+channelMention+" with "+emoji+". To view, click on "+jumpURL;
				event.getGuild().getDefaultChannel().asTextChannel().sendMessage(reply).queue();
			}
		});
	}
	
	@Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		event.retrieveUser().queue(user -> {
			if(!user.isBot()) {
				String emoji = event.getReaction().getEmoji().getAsReactionCode();
				String channelMention = event.getChannel().getAsMention();
				String jumpURL = event.getJumpUrl();
				
				String reply = user.getName()+" has removed the reaction "+emoji+" from a comment in "+channelMention+". To view, click on "+jumpURL;
				event.getGuild().getDefaultChannel().asTextChannel().sendMessage(reply).queue();
			}
		});
	}
}
