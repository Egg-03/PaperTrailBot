package org.papertrail.main;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

/*
 * This class updates the number of servers the bot is in 
 */
public class ActivityUpdateListener extends ListenerAdapter {

	private final ShardManager manager;

	public ActivityUpdateListener(ShardManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event) { // update on cold start
		updateActivity();
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event) { // update on guild join
		updateActivity();
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) { // update on guild leave
		updateActivity();
	}

	private void updateActivity() {
		manager.setActivity(Activity.watching(manager.getGuildCache().size() + " Servers"));
	}

}
