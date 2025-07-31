package org.papertrail.database;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import com.zaxxer.hikari.HikariDataSource;

import static org.papertrail.database.Schema.initializeSchema;


public class DatabaseConnector {

	private static final String DB_URL = EnvConfig.get("DATABASEURL");
	private static HikariDataSource dataSource;
	private final DSLContext dsl;

	public DatabaseConnector() {
		initializeDataSource();
		this.dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
		initializeSchema(dsl);
	}
	
	public GuildDataAccess getGuildDataAccess() {
		return new GuildDataAccess(dsl);
	}
	
	public MessageDataAccess getMessageDataAccess() {
		return new MessageDataAccess(dsl);
	}
	
	private static synchronized void initializeDataSource() {
		
		if (dataSource == null) {
			dataSource = new HikariDataSource();
			dataSource.setJdbcUrl(DB_URL);
			dataSource.setMaximumPoolSize(20); // Adjust as needed
			dataSource.setMinimumIdle(2); // Adjust as needed
			dataSource.setConnectionTimeout(30000); // 30 seconds
			dataSource.setIdleTimeout(600000); // 10 minutes
			dataSource.setMaxLifetime(1800000); // 30 minutes	
			dataSource.setPoolName("PaperTrailPool");
			dataSource.setConnectionInitSql("SET TIME ZONE 'UTC'");
		}
		// Ensure the data source is closed on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    if (dataSource != null && !dataSource.isClosed()) {
		        dataSource.close();
		        Logger.info("Database connection pool closed.");
		    }
		}));
	}
	
}
