package org.papertrail.database;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import static org.jooq.impl.DSL.constraint;

public class Schema {
	
	private Schema() {
		throw new IllegalStateException("Utility Class");
	}

	public static final String SCHEMA_NAME = "public";
	
	public static final String AUDIT_LOG_TABLE = "audit_log_table";
	public static final String MESSAGE_LOG_REGISTRATION_TABLE = "message_log_registration_table";
	// the following to column names are used in both tables: message_log_registration_table and audit_log_table
	public static final String GUILD_ID_COLUMN = "guild_id";
	public static final String CHANNEL_ID_COLUMN = "channel_id";

	public static final String MESSAGE_LOG_CONTENT_TABLE = "message_log_content_table";
	public static final String MESSAGE_ID_COLUMN = "message_id";
	public static final String MESSAGE_CONTENT_COLUMN = "message_content";
	public static final String AUTHOR_ID_COLUMN = "author_id";

	public static void initializeSchema(DSLContext dsl) {

		dsl.createSchemaIfNotExists(SCHEMA_NAME).execute();

		// create audit log table
		dsl.createTableIfNotExists(AUDIT_LOG_TABLE)
				.column(GUILD_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.column(CHANNEL_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.constraints(
						constraint(AUDIT_LOG_TABLE+"_pk").primaryKey(GUILD_ID_COLUMN),
						constraint(AUDIT_LOG_TABLE+"_unique").unique(CHANNEL_ID_COLUMN)
				)
				.execute();

		// create message log registration table
		dsl.createTableIfNotExists(MESSAGE_LOG_REGISTRATION_TABLE)
				.column(GUILD_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.column(CHANNEL_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.constraints(
						constraint(MESSAGE_LOG_REGISTRATION_TABLE+"_pk").primaryKey(GUILD_ID_COLUMN),
						constraint(MESSAGE_LOG_REGISTRATION_TABLE+"_unique").unique(CHANNEL_ID_COLUMN)
				).execute();

		// create message log content table
		dsl.createTableIfNotExists(MESSAGE_LOG_CONTENT_TABLE)
				.column(MESSAGE_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.column(MESSAGE_CONTENT_COLUMN, SQLDataType.CLOB.nullable(true))
				.column(AUTHOR_ID_COLUMN, SQLDataType.BIGINT.notNull())
				.column("created_at", SQLDataType.TIMESTAMP.default_(DSL.currentTimestamp()).notNull())
				.constraints(
						constraint(MESSAGE_LOG_CONTENT_TABLE+"pk").primaryKey(MESSAGE_ID_COLUMN)
				)
				.execute();
		// create index on created_at column
		dsl.createIndexIfNotExists(MESSAGE_LOG_CONTENT_TABLE+"_created_at_idx")
				.on(MESSAGE_LOG_CONTENT_TABLE, "created_at")
				.execute();

		setupCronDeletion(dsl);
	}

	private static void setupCronDeletion(DSLContext dsl) {
		dsl.query("CREATE EXTENSION IF NOT EXISTS pg_cron;").execute();

		String cronScheduleQuery = String.format("""
				SELECT cron.schedule(
				  'daily_log_cleanup',
				  '0 2 * * *',  -- 2:00 AM UTC daily
				  $$DELETE FROM %s WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '30 days';$$
				);
				""", MESSAGE_LOG_CONTENT_TABLE);

		dsl.query(cronScheduleQuery).execute();

	}
}
