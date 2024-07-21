package listeners;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CatListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(!event.getAuthor().isBot()) return;
		
		Message msg = event.getMessage();
		String message = msg.getContentRaw();
		//react with 🔥 on cat
		if(message.contains("cat has appeared! Type \"cat\" to catch it!")) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} finally {
				msg.getChannel().sendMessage("cat").queue();
			}
			
		}
	}
}
