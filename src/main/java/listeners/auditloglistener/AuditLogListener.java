package listeners.auditloglistener;

import java.awt.Color;
import java.time.Duration;
import java.util.Map.Entry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
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
		 User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		 eb.setDescription((executor != null ? executor.getAsMention() : "`Unknown`")+" has executed the following action:");
		 eb.setColor(Color.CYAN);
		 eb.addField("Action Type", ale.getType().name(), true);
		 eb.addField("Target Type", ale.getTargetType().name(), true);
		 		 
		 auditLogEntryKeyInterpreter(ale, eb);
		 
		 eb.setFooter("Audit Log Entry ID: "+ale.getId());
		 eb.setTimestamp(ale.getTimeCreated());
		 
		 MessageEmbed mb = eb.build();
		 
		 event.getGuild().getTextChannelById(textChannelId).sendMessageEmbeds(mb).queue();
	 }
	 
	 private void auditLogEntryKeyInterpreter(AuditLogEntry ale, EmbedBuilder eb) {
		 ActionType action = ale.getType();
		 switch(action) {
		 case APPLICATION_COMMAND_PRIVILEGES_UPDATE -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_FLAG_TO_CHANNEL -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_MEMBER_TIMEOUT -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_RULE_CREATE -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_RULE_DELETE -> formatGeneric(ale, eb);
		 case AUTO_MODERATION_RULE_UPDATE -> formatGeneric(ale, eb);
		 case BAN -> formatGeneric(ale, eb);
		 case BOT_ADD -> formatGeneric(ale, eb);
		 case CHANNEL_CREATE -> formatGeneric(ale, eb);
		 case CHANNEL_DELETE -> formatGeneric(ale, eb);
		 case CHANNEL_OVERRIDE_CREATE -> formatGeneric(ale, eb);
		 case CHANNEL_OVERRIDE_DELETE -> formatGeneric(ale, eb);
		 case CHANNEL_OVERRIDE_UPDATE -> formatGeneric(ale, eb);
		 case CHANNEL_UPDATE -> formatGeneric(ale, eb);
		 case EMOJI_CREATE -> formatGeneric(ale, eb);
		 case EMOJI_DELETE -> formatGeneric(ale, eb);
		 case EMOJI_UPDATE -> formatGeneric(ale, eb);
		 case GUILD_UPDATE -> formatGeneric(ale, eb);
		 case INTEGRATION_CREATE -> formatGeneric(ale, eb);
		 case INTEGRATION_DELETE -> formatGeneric(ale, eb);
		 case INTEGRATION_UPDATE -> formatGeneric(ale, eb);
		 case INVITE_CREATE -> formatInviteCreate(ale, eb);
		 case INVITE_DELETE -> formatInviteDelete(ale, eb);
		 case INVITE_UPDATE -> formatGeneric(ale, eb);
		 case KICK -> formatGeneric(ale, eb);
		 case MEMBER_ROLE_UPDATE -> formatGeneric(ale, eb);
		 case MEMBER_UPDATE -> formatGeneric(ale, eb);
		 case MEMBER_VOICE_KICK -> formatGeneric(ale, eb);
		 case MEMBER_VOICE_MOVE -> formatGeneric(ale, eb);
		 case MESSAGE_BULK_DELETE -> formatGeneric(ale, eb);
		 case MESSAGE_CREATE -> formatGeneric(ale, eb);
		 case MESSAGE_DELETE -> formatGeneric(ale, eb);
		 case MESSAGE_PIN -> formatGeneric(ale, eb);
		 case MESSAGE_UNPIN -> formatGeneric(ale, eb);
		 case MESSAGE_UPDATE -> formatGeneric(ale, eb);
		 case PRUNE -> formatGeneric(ale, eb);
		 case ROLE_CREATE -> formatGeneric(ale, eb);
		 case ROLE_DELETE -> formatGeneric(ale, eb);
		 case ROLE_UPDATE -> formatGeneric(ale, eb);
		 case SCHEDULED_EVENT_CREATE -> formatGeneric(ale, eb);
		 case SCHEDULED_EVENT_DELETE -> formatGeneric(ale, eb);
		 case SCHEDULED_EVENT_UPDATE -> formatGeneric(ale, eb);
		 case STAGE_INSTANCE_CREATE -> formatGeneric(ale, eb);
		 case STAGE_INSTANCE_DELETE -> formatGeneric(ale, eb);
		 case STAGE_INSTANCE_UPDATE -> formatGeneric(ale, eb);
		 case STICKER_CREATE -> formatGeneric(ale, eb);
		 case STICKER_DELETE -> formatGeneric(ale, eb);
		 case STICKER_UPDATE -> formatGeneric(ale, eb);
		 case THREAD_CREATE -> formatGeneric(ale, eb);
		 case THREAD_DELETE -> formatGeneric(ale, eb);
		 case THREAD_UPDATE -> formatGeneric(ale, eb);
		 case UNBAN -> formatGeneric(ale, eb);
		 case UNKNOWN -> formatGeneric(ale, eb);
		 case VOICE_CHANNEL_STATUS_DELETE -> formatGeneric(ale, eb);
		 case VOICE_CHANNEL_STATUS_UPDATE -> formatGeneric(ale, eb);
		 case WEBHOOK_CREATE -> formatGeneric(ale, eb);
		 case WEBHOOK_REMOVE -> formatGeneric(ale, eb);
		 case WEBHOOK_UPDATE -> formatGeneric(ale, eb);
		 default -> formatGeneric(ale, eb);
		 }
	 }

	 private void formatGeneric(AuditLogEntry ale, EmbedBuilder eb) {
		 
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
	 }
	 
	 private String formatDuration(int seconds) {
		    if (seconds == 0) return "Never";
		    Duration d = Duration.ofSeconds(seconds);
		    long days = d.toDays();
		    long hours = d.toHoursPart();
		    long minutes = d.toMinutesPart();

		    StringBuilder sb = new StringBuilder();
		    if (days > 0) sb.append(days).append("d ");
		    if (hours > 0) sb.append(hours).append("h ");
		    if (minutes > 0) sb.append(minutes).append("m");
		    return sb.toString().trim();
	 }

	 private void formatInviteCreate(AuditLogEntry ale, EmbedBuilder eb) {
		 
		 for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			 
			 String change = changes.getKey();
			 Object oldValue = changes.getValue().getOldValue();
			 Object newValue = changes.getValue().getNewValue();
			 
			 switch(change) {
			 
			 case "code":
				 eb.addField("Invite Code", newValue.toString(), false);
				 break;
			
			 case "inviter_id":
				 User inviter = ale.getJDA().getUserById(newValue.toString());
				 eb.addField("Invite Created By", (inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
				 break;
				 
			 case "temporary":
				 eb.addField("Temporary Invite", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				 break;
				 
			 case "max_uses", "uses", "flags":
				 break;
			 
			 case "max_age":
				 eb.addField("Expires After", formatDuration((Integer) newValue), false);
				 break;
			 case "channel_id":
				 Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
				 eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+newValue.toString()+"`"), false);
				 break;
			default:
				 eb.addField(change, "from "+oldValue.toString()+" to "+newValue.toString(), false);
			 }
		 }
		 
	 }
	 
	 private void formatInviteDelete(AuditLogEntry ale, EmbedBuilder eb) {
		 
		 for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			 
			 String change = changes.getKey();
			 Object oldValue = changes.getValue().getOldValue();
			 Object newValue = changes.getValue().getNewValue();
			 
			 switch(change) {
			 
			 case "code":
				 eb.addField("Deleted Invite Code", oldValue.toString(), false);
				 break;
			
			 case "inviter_id":
				 User inviter = ale.getJDA().getUserById(oldValue.toString());
				 eb.addField("Invite Deleted By", (inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
				 break;
				 
			 case "temporary":
				 eb.addField("Temporary Invite", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
				 break;
				 
			 case "max_uses", "uses", "flags", "max_age":
				 break;
			 
			 case "channel_id":
				 Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
				 eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+oldValue.toString()+"`"), false);
				 break;
			default:
				 eb.addField(change, "from "+oldValue.toString()+" to "+newValue.toString(), false);
			 }
		 }
	 }
	 
	 
}
