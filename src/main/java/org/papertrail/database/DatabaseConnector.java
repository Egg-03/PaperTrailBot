package org.papertrail.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.papertrail.utilities.EnvConfig;

public class DatabaseConnector {

	private static final String DB_URL = EnvConfig.get("DATABASEURL");

	private final DSLContext dsl;

	public DatabaseConnector() throws SQLException {
		Connection connection = DriverManager.getConnection(DB_URL);
		this.dsl = DSL.using(connection, SQLDialect.POSTGRES);
	}
	
	public GuildDataAccess getGuildDataAccess() {
		return new GuildDataAccess(dsl);
	}
	
	public MessageDataAccess getMessageDataAccess() {
		return new MessageDataAccess(dsl);
	}


}
