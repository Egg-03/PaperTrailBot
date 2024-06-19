package listeners;

import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogListener extends ListenerAdapter{
	 @Override
	 public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
		 AuditLogEntry ale = event.getEntry();
		 String audit = ale.getUserId()+" has executed the following: \n"+ale.getChanges().values()+"\n for: "+ale.getReason();
		 event.getGuild().getDefaultChannel().asTextChannel().sendMessage(audit).queue();
	 }
}
