package org.papertrail.database;

import org.jooq.DSLContext;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.table;
import static org.papertrail.database.Schema.CHANNEL_ID_COLUMN;
import static org.papertrail.database.Schema.GUILD_ID_COLUMN;

public class GuildDataAccess {

	private final DSLContext dsl;

	public GuildDataAccess(DSLContext dsl) {
		this.dsl = dsl;
	}

	public void registerGuildAndChannel(String guildId, String channelId, String tableName) {

		dsl.insertInto(table(tableName))
				.columns(field(GUILD_ID_COLUMN), field(CHANNEL_ID_COLUMN))
				.values(Long.parseLong(guildId), Long.parseLong(channelId))
				.onConflictDoNothing()
				.execute();

	}

	public List<String> retrieveAllRegisteredGuilds(String tableName) {

		return dsl.select(field(GUILD_ID_COLUMN))
				.from(table(tableName))
				.fetch(r -> String.valueOf(r.get(GUILD_ID_COLUMN)));

	}

	public List<String> retrieveAllRegisteredChannels(String tableName) {

		return dsl.select(field(CHANNEL_ID_COLUMN))
				.from(table(tableName))
				.fetch(r -> String.valueOf(r.get(CHANNEL_ID_COLUMN)));

	}

	public String retrieveRegisteredChannel (String guildId, String tableName) {

		return dsl.select(field(CHANNEL_ID_COLUMN))
				.from(table(tableName))
				.where(field(GUILD_ID_COLUMN).eq(Long.parseLong(guildId)))
				.fetchOneInto(String.class);
	}

	public boolean isGuildRegistered (String guildId, String tableName) {

		return dsl.fetchExists(
				selectOne()
						.from(tableName)
						.where(field(GUILD_ID_COLUMN).eq(Long.parseLong(guildId)))
		);

	}

	public boolean isChannelRegistered (String channelId, String tableName) {

		return dsl.fetchExists(
				selectOne()
						.from(tableName)
						.where(field(CHANNEL_ID_COLUMN).eq(Long.parseLong(channelId)))
		);
	}

	public void unregister (String guildId, String tableName) {

		dsl.deleteFrom(table(tableName))
				.where(field(GUILD_ID_COLUMN).eq(Long.parseLong(guildId)))
				.execute();
	}

}
