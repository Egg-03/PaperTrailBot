package org.papertrail.database;

import org.jooq.DSLContext;

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

		String createSchema = String.format("""
				CREATE SCHEMA IF NOT EXISTS %s;
				""", SCHEMA_NAME);

		dsl.execute(createSchema);

		String createAuditLogTable = String.format("""
				CREATE TABLE IF NOT EXISTS %s.%s (
					%s int8 NOT NULL,
					%s int8 NOT NULL,
					CONSTRAINT %s_pk PRIMARY KEY (%s),
					CONSTRAINT %s_unique UNIQUE (%s)
				);
				""", SCHEMA_NAME, AUDIT_LOG_TABLE, GUILD_ID_COLUMN, CHANNEL_ID_COLUMN,
					 AUDIT_LOG_TABLE, GUILD_ID_COLUMN,
					 AUDIT_LOG_TABLE, CHANNEL_ID_COLUMN);

		dsl.execute(createAuditLogTable);

		String createMessageLogRegistrationTable = String.format("""
				CREATE TABLE IF NOT EXISTS %s.%s (
					%s int8 NOT NULL,
					%s int8 NOT NULL,
					CONSTRAINT %s_pk PRIMARY KEY (%s),
					CONSTRAINT %s_unique UNIQUE (%s)
				);
				""", SCHEMA_NAME, MESSAGE_LOG_REGISTRATION_TABLE, GUILD_ID_COLUMN, CHANNEL_ID_COLUMN,
					 MESSAGE_LOG_REGISTRATION_TABLE, GUILD_ID_COLUMN,
					 MESSAGE_LOG_REGISTRATION_TABLE, CHANNEL_ID_COLUMN);

		dsl.execute(createMessageLogRegistrationTable);

		String createMessageLogContentTable = String.format("""
				CREATE TABLE IF NOT EXISTS %s.%s (
					%s int8 NOT NULL,
					%s text NULL,
					%s int8 NOT NULL,
					created_at timestamp DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'::text) NOT NULL,
					CONSTRAINT %s_pk PRIMARY KEY (%s)
				);
				CREATE INDEX IF NOT EXISTS %s_created_at_idx ON %s.%s USING btree (created_at);
				""", SCHEMA_NAME, MESSAGE_LOG_CONTENT_TABLE, MESSAGE_ID_COLUMN, MESSAGE_CONTENT_COLUMN, AUTHOR_ID_COLUMN,
					 MESSAGE_LOG_CONTENT_TABLE, MESSAGE_ID_COLUMN,
					 MESSAGE_LOG_CONTENT_TABLE, SCHEMA_NAME, MESSAGE_LOG_CONTENT_TABLE);

		dsl.execute(createMessageLogContentTable);
	}
}
