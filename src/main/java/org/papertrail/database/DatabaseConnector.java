package org.papertrail.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

public class DatabaseConnector {

	private static final String DB_URL = EnvConfig.get("DATABASEURL");
	
	private Connection connect;
	
	public DatabaseConnector() throws SQLException {
		connect = DriverManager.getConnection(DB_URL);
	}
	
	public void registerGuildAndChannel(String guildId, String channelId, String tableName) throws SQLException  {
		
		String sqlStatement = "INSERT INTO "+tableName+" (guild_id, channel_id) VALUES (?, ?)";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, guildId);
			psmt.setString(2, channelId);
			psmt.executeUpdate();
		} 
		
	}
	
	public String retrieveChannelId(String guildId, String tableName) {

		String sqlStatement = "SELECT (channel_id) FROM " + tableName + " WHERE guild_id = ?";
		String channelId = "";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setString(1, guildId);

			try (ResultSet rs = psmt.executeQuery()) {
				if (!rs.isBeforeFirst()) {
					return null;
				} else {
					while (rs.next()) { // by configuration, there will always be only one channel id row at a time
										// because registering multiple channels is not allowed
						channelId = rs.getString("channel_id");
					}
				}
			}
			return channelId;
		} catch (SQLException e) {
			Logger.error("Could not retrieve registered guild channel id", e);
			return null;
		}
	}
	
	public void unregisterGuildAndChannel(String guildId, String tableName) throws SQLException {
		
		String sqlStatement = "DELETE FROM "+tableName+" WHERE guild_id = ?";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, guildId);
			psmt.executeUpdate();
		} 
	}

}
