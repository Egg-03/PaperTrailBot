package listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserUpdateListener extends ListenerAdapter {
    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
    	User user = event.getUser();
    	String message = user.getEffectiveName()+" has set their status to: "+event.getNewOnlineStatus().getKey();
    	if(event.getGuild().isMember(user))
    		event.getGuild().getDefaultChannel().asTextChannel().sendMessage(message).queue();
    }
}
