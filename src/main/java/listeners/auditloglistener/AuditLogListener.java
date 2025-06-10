package listeners.auditloglistener;

import java.awt.Color;
import java.util.Map.Entry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utilities.DatabaseConnector;
import utilities.TableNames;

public class AuditLogListener extends ListenerAdapter{
	
	private DatabaseConnector dc;
	
	 public AuditLogListener(DatabaseConnector dc) {
		this.dc=dc;
	}
 
	 @Override
	 public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
		 
		 // this will return a non-null text id if a channel was previously registered in the database
		 String textChannelId=dc.retrieveChannelId(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);
		 
		 if(textChannelId==null ||textChannelId.isBlank()) {
			 return;
		 }
		 
		 AuditLogEntry ale = event.getEntry();
		 EmbedBuilder eb = new EmbedBuilder();
		 
		 eb.setTitle("Audit Log Entry");
		 eb.setDescription(ale.getJDA().getUserById(ale.getUserIdLong()).getAsMention()+" has executed the following action:");
		 eb.setColor(Color.CYAN);
		 eb.addField("Action Type", ale.getType().name(), true);
		 eb.addField("Target Type", ale.getTargetType().name(), true);
		 		 
		 for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			 String change = changes.getKey();
			 Object oldValue = changes.getValue().getOldValue();
			 Object newValue = changes.getValue().getNewValue();
			 
			 if(oldValue==null) {
				 eb.addField(change, newValue.toString(), false);
			 } else if(newValue==null){
				 eb.addField(change, "from "+oldValue.toString()+" to null", false);
			 } else {
				 eb.addField(change, "from "+oldValue.toString()+" to "+newValue.toString(), false);
			 }
			 
		 }
		 
		 eb.setFooter("Audit Log Entry ID: "+ale.getId());
		 eb.setTimestamp(ale.getTimeCreated());
		 
		 MessageEmbed mb = eb.build();
		 
		 event.getGuild().getTextChannelById(textChannelId).sendMessageEmbeds(mb).queue();
	 }
}
