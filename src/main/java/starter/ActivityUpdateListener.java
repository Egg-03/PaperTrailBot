package starter;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Activity;
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
		manager.setActivity(Activity.watching(manager.getGuilds().size() + " Servers"));
	}

}
