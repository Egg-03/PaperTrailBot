package listeners;

import java.util.List;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberList extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) {
			return;
		}
		
		List<Member> member = event.getGuild().getMembers();
		String command = event.getMessage().getContentRaw();
		if(command.equals("!online")) {
			for(Member m: member) {
				if(m.getOnlineStatus()==OnlineStatus.ONLINE) {
					event.getGuild().getDefaultChannel().asTextChannel().sendMessage(m.getUser().getEffectiveName()+ ": ONLINE\n").queue();
				}
			}
		}
		
		if(command.equals("!offline")) {
			for(Member m: member) {
				if(m.getOnlineStatus()==OnlineStatus.OFFLINE) {
					event.getGuild().getDefaultChannel().asTextChannel().sendMessage(m.getUser().getEffectiveName()+ ": OFFLINE/INVISIBLE\n").queue();
				}
			}
		}
		
		if(command.equals("!idle")) {
			for(Member m: member) {
				if(m.getOnlineStatus()==OnlineStatus.IDLE) {
					event.getGuild().getDefaultChannel().asTextChannel().sendMessage(m.getUser().getEffectiveName()+ ": IDLE\n").queue();
				}
			}
		}
		
		if(command.equals("!dnd")) {
			for(Member m: member) {
				if(m.getOnlineStatus()==OnlineStatus.DO_NOT_DISTURB) {
					event.getGuild().getDefaultChannel().asTextChannel().sendMessage(m.getUser().getEffectiveName()+ ": DO NOT DISTURB\n").queue();
				}
			}
		}
		
	}
}
