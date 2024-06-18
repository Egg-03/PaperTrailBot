package starter;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ConnectionInitializer {
	
	private final ShardManager manager;
	private final Dotenv config;
	
	public ConnectionInitializer() {
		config = Dotenv.configure().load();
		String token = config.get("TOKEN");
		
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.watching("for .img your_search_term"));
		manager = builder.build();
	}

	public Dotenv getConfig() {
		return config;
	}

	public ShardManager getManager() {
		return manager;
	}
}
