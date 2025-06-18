package org.papertrail.cleanup;

import java.sql.SQLException;

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
		this.dc=dc;
	}
	
	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {
		Guild leftGuild = event.getGuild();
		try {
			dc.unregisterGuildAndChannel(leftGuild.getId(), TableNames.AUDIT_LOG_TABLE);
		} catch (SQLException e) {
			Logger.error("Could not auto-unregister guild upon guild leave", e);
		}
	}
}
