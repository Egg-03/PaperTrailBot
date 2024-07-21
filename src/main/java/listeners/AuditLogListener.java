package listeners;

import java.util.Map.Entry;

import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogListener extends ListenerAdapter{
	 @Override
	 public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
		 AuditLogEntry ale = event.getEntry();
		 
		 String audit = ale.getUserIdLong()+" has executed the following: \n";
		 event.getGuild().getDefaultChannel().asTextChannel().sendMessage(audit).queue();
		 
		 for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			 event.getGuild().getTextChannelById("1263139186110627892").sendMessage(changes.getKey()+": "+ changes.getValue().getOldValue()+" to "+changes.getValue().getNewValue()).queue();
		 }
	 }
}
