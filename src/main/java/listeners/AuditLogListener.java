package listeners;

import java.awt.Color;
import java.util.Map.Entry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogListener extends ListenerAdapter{
	
	private String textChannel;
	 @Override
	 public void onMessageReceived(MessageReceivedEvent event) {
		 if(event.getMessage().getContentRaw().equals("!a")) {
			 textChannel=event.getChannel().asTextChannel().getId();
			 event.getGuild().getTextChannelById(textChannel).sendMessage("All audit log info will be logged here").queue();
		 }	 
	 }
	 
	 @Override
	 public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
		 if(textChannel==null ||textChannel.isBlank()) {
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
				 eb.addField(change, "to "+newValue.toString(), false);
			 } else if(newValue==null){
				 eb.addField(change, "from "+oldValue.toString()+" to null", false);
			 } else {
				 eb.addField(change, "from "+oldValue.toString()+" to "+newValue.toString(), false);
			 }
			 
		 }
		 
		 eb.setFooter("Audit Log Entry ID: "+ale.getId());
		 eb.setTimestamp(ale.getTimeCreated());
		 
		 MessageEmbed mb = eb.build();
		 
		 event.getGuild().getTextChannelById(textChannel).sendMessageEmbeds(mb).queue();
	 }
}
