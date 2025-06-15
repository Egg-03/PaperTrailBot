package starter;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ActivityUpdateListener extends ListenerAdapter {

	private ShardManager manager;

	public ActivityUpdateListener(ShardManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		updateActivity();
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event) {
		updateActivity();
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {
		updateActivity();
	}

	private void updateActivity() {
		manager.setActivity(Activity.watching(manager.getGuildCache().size() + " Servers"));
	}

}
