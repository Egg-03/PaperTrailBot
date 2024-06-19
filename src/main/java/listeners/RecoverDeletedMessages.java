package listeners;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//TEXT-ONLY
public class RecoverDeletedMessages extends ListenerAdapter {
	private Map<String, String> messageCache = new ConcurrentHashMap<>();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(!event.getAuthor().isBot()) {
			messageCache.put(event.getMessageId(), event.getMessage().getContentRaw());
		}
			
	}
	
	@Override
	 public void onMessageDelete(MessageDeleteEvent event) {
		String key = event.getMessageId();
		String messageContent = messageCache.get(key);
		
		if(messageContent!=null && !messageContent.isEmpty()) {
			event.getGuild().getDefaultChannel().asTextChannel().sendMessage("``Deleted Text: "+messageContent+"``").queue();
			messageCache.remove(key);
		}
	}
}
