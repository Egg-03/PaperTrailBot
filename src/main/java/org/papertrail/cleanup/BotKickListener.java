package org.papertrail.cleanup;

import org.jetbrains.annotations.NotNull;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.Executor;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
public class BotKickListener extends ListenerAdapter {

	private final Executor vThreadPool;
	private final DatabaseConnector dc;

	public BotKickListener(DatabaseConnector dc, Executor vThreadPool) {
		this.dc = dc;
		this.vThreadPool = vThreadPool;
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {

		Guild leftGuild = event.getGuild();
		vThreadPool.execute(()->{
			dc.getGuildDataAccess().unregister(leftGuild.getId(), TableNames.AUDIT_LOG_TABLE);
			dc.getGuildDataAccess().unregister(leftGuild.getId(), TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		});

	}
}
