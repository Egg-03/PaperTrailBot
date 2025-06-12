package starter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import database.DatabaseConnector;
import listeners.ReactionListener;
import listeners.auditloglistener.AuditLogCommandListener;
import listeners.auditloglistener.AuditLogListener;

public class FireRun {

	public static void main(String[] args) throws IOException, SQLException {
		
		DatabaseConnector dc = new DatabaseConnector();
		
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new AuditLogCommandListener(dc));
		ci.getManager().addEventListener(new AuditLogListener(dc));
		
		ci.getManager().addEventListener(new ReactionListener());
		
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
