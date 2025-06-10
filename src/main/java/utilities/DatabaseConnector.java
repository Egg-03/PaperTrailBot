package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

	private static final String DB_URL = "jdbc:postgresql://" + EnvConfig.get("PGHOST") + "/neondb?user="
			+ EnvConfig.get("PGUSER") + "&password=" + EnvConfig.get("PGPASSWORD") + "&sslmode=require";
	
	
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
		
		String sqlStatement = "SELECT (channel_id) FROM "+tableName+" WHERE guild_id = ?";
		String channelId = "";
		
		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, guildId);
			
			ResultSet rs = psmt.executeQuery();
			if(!rs.isBeforeFirst()) {
				return null;
			} else {
				while(rs.next()) {
					channelId = rs.getString("channel_id");
				}
			}
			
			rs.close();
			return channelId;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
