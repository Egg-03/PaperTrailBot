package listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		
		Message msg = event.getMessage();
		String message = msg.getContentRaw();
		
		//react with 🔥 on cat
		if(message.contains("cat")) {
			msg.addReaction(Emoji.fromUnicode("🔥")).queue();
		}
		//ping command
		else if(message.equals("!ping")){
			MessageChannel channel = event.getChannel();
			channel.getJDA().getRestPing().queue(time ->
		     channel.sendMessageFormat("``%d ms``", time).queue());
		}
	}
}
