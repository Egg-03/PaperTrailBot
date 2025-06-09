package listeners;

import java.util.Map.Entry;

import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogListener extends ListenerAdapter{
	
	private String textChannel;
	 @Override
	 public void onMessageReceived(MessageReceivedEvent event) {
		 if(event.getMessage().getContentRaw().equals("!register")) {
			 textChannel=event.getChannel().asTextChannel().getId();
			 event.getGuild().getTextChannelById(textChannel).sendMessage("All info will be logged here").queue();
		 }	 
	 }
	 
	 @Override
	 public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
		 if(textChannel==null ||textChannel.isBlank()) {
			 return;
		 }
		 
		 AuditLogEntry ale = event.getEntry();
		 
		 String audit = ale.getJDA().getUserById(ale.getUserIdLong()).getAsMention()+" has executed the following: \n";
		 event.getGuild().getTextChannelById(textChannel).sendMessage(audit).queue();
		 
		 for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			 event.getGuild().getTextChannelById(textChannel).sendMessage(changes.getKey()+": "+ changes.getValue().getOldValue()+" to "+changes.getValue().getNewValue()).queue();
		 }
	 }
}
