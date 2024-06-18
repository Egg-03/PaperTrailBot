package listeners;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SimpleImageProvider extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return; //ignore message from other bots
		
		Message msg = event.getMessage();
		String content = msg.getContentRaw();
		
		if(content.matches("^.img ([^\n]+)")) {
			String searchTerm = content.substring(content.indexOf(".img")+5).strip();
			String searchTermURL = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
		    String imageUrl = "https://www.google.com/search?q=images+" + searchTermURL;
		    
			MessageChannel channel = event.getChannel();
			channel.sendMessage(imageUrl).queue();
		}
	}
}
