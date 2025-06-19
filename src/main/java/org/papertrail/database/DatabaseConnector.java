package org.papertrail.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
	
	public String retrieveRegisteredChannelId(String guildId, String tableName) {

		String sqlStatement = "SELECT channel_id FROM " + tableName + " WHERE guild_id = ?";
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
	
	public String retrieveRegisteredGuildId(String guildId, String tableName) {

		String sqlStatement = "SELECT guild_id FROM " + tableName + " WHERE guild_id = ?";
		
		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setString(1, guildId);

			try (ResultSet rs = psmt.executeQuery()) {
				if (!rs.isBeforeFirst()) {
					return null;
				} else {
					while (rs.next()) { 					
						return rs.getString("guild_id");
					}
				}
			}
			return null;
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
	
	public void logMessage(String messageId, String messageContent, String authorId, String tableName) throws SQLException {
		
		String sqlStatement = "INSERT INTO "+tableName+" (message_id, message_content, author_id) VALUES (?, ?, ?)";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, messageId);
			psmt.setString(2, messageContent);
			psmt.setString(3, authorId);
			psmt.executeUpdate();
		} 
	}
	
	public List<String> retrieveAuthorAndMessage(String messageId, String tableName) {

		String sqlStatement = "SELECT author_id, message_content FROM " + tableName + " WHERE message_id = ?";

		String messageContent = "";
		String authorId = "";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setString(1, messageId);

			try (ResultSet rs = psmt.executeQuery()) {	
				if (rs.next()) { // only one message is logged per row per message id
					messageContent = rs.getString("message_content");
					authorId = rs.getString("author_id");
					return List.of(authorId, messageContent);
				}
			}
			return Collections.emptyList();
		} catch (SQLException e) {
			Logger.error("Could not retrieve message", e);
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public void updateMessage(String messageId, String messageContent, String tableName) throws SQLException {
		
		String sqlStatement = "UPDATE " + tableName + " SET message_content = ? WHERE message_id = ?";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, messageContent);
			psmt.setString(2, messageId);
			psmt.executeUpdate();
		} 
	}
	
	public void deleteMessage(String messageId, String tableName) throws SQLException {
		
		String sqlStatement = "DELETE FROM "+tableName+" WHERE message_id = ?";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, messageId);
			psmt.executeUpdate();
		} 
	}

}
