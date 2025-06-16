package org.papertrail.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import org.papertrail.cleanup.BotKickListener;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.listeners.loglisteners.AuditLogListener;
import org.papertrail.listeners.loglisteners.LogCommandListener;
import org.papertrail.listeners.memberlisteners.GuildMemberJoinAndLeaveListener;
import org.papertrail.listeners.voicelisteners.GuildVoiceListener;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FireRun {

	public static void main(String[] args) throws IOException, SQLException {
		
		DatabaseConnector dc = new DatabaseConnector();
		
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new LogCommandListener(dc));
		ci.getManager().addEventListener(new AuditLogListener(dc));
		ci.getManager().addEventListener(new GuildVoiceListener(dc));
		ci.getManager().addEventListener(new GuildMemberJoinAndLeaveListener(dc));
		
		ci.getManager().addEventListener(new BotKickListener(dc));
			
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/ping", new PingHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	 exchange.sendResponseHeaders(200, -1);
        }
    }

}
