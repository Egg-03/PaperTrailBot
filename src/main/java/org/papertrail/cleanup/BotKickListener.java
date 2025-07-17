package org.papertrail.cleanup;

import org.jetbrains.annotations.NotNull;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.tinylog.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
public class BotKickListener extends ListenerAdapter {

	private DatabaseConnector dc;

	public BotKickListener(DatabaseConnector dc) {
		this.dc = dc;
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {
		try {
			Guild leftGuild = event.getGuild();
			dc.getGuildDataAccess().unregister(leftGuild.getId(), TableNames.AUDIT_LOG_TABLE);
			dc.getGuildDataAccess().unregister(leftGuild.getId(), TableNames.MESSAGE_LOG_REGISTRATION_TABLE);
		} catch (Exception e) {
			Logger.error(e, "Could not auto-unregister guild upon guild leave: " + event.getGuild().getId() + "/"
					+ event.getGuild().getName());
		}
	}
}
