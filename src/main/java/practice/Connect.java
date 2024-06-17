package practice;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

class MessagePass extends ListenerAdapter{
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return; //ignore message from other bots
		
		Message msg = event.getMessage();
		String content = msg.getContentRaw();
		if(content.matches("^.img ([^\n]+)")) {
			String searchTerm = content.substring(content.indexOf(".img")+5).strip();
			System.out.println(searchTerm);
			String searchTermURL = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
		    String imageUrl = "https://www.google.com/search?q=images+" + searchTermURL;
		    
			MessageChannel channel = event.getChannel();
			channel.sendMessage(imageUrl).queue();
		}
	}
}

public class Connect{
	public static void main(String[] args) {
		JDABuilder api = JDABuilder.createDefault("MTI1MjEwMTg4NjkyMDc1MzE1Mg.G-rkgL.kn0-CgzJpEl9easgdYGBQQcq2oSEOVJtbfZUjs");
		api.enableIntents(GatewayIntent.MESSAGE_CONTENT);
		api.setActivity(Activity.customStatus("answering to .img your_search_term"));
		api.build().addEventListener(new MessagePass());
	}
}
