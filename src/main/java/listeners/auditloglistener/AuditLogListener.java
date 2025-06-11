package listeners.auditloglistener;

import java.awt.Color;
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
import utilities.DurationFormatter;
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
		auditLogParser(event, ale, textChannelId);
	}

	private void auditLogParser(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		ActionType action = ale.getType();
		switch(action) {
		case APPLICATION_COMMAND_PRIVILEGES_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_FLAG_TO_CHANNEL -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_MEMBER_TIMEOUT -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case BAN -> formatBan(event, ale, channelIdToSendTo);
		case BOT_ADD -> formatBotAdd(event, ale, channelIdToSendTo);
		case CHANNEL_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case CHANNEL_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case CHANNEL_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case EMOJI_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case EMOJI_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case EMOJI_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case GUILD_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case INTEGRATION_CREATE -> formatIntegrationCreate(event, ale, channelIdToSendTo);
		case INTEGRATION_DELETE -> formatIntegrationDelete(event, ale, channelIdToSendTo);
		case INTEGRATION_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case INVITE_CREATE -> formatInviteCreate(event, ale, channelIdToSendTo);
		case INVITE_DELETE -> formatInviteDelete(event, ale, channelIdToSendTo);
		case INVITE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case KICK -> formatKick(event, ale, channelIdToSendTo);
		case MEMBER_ROLE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case MEMBER_UPDATE -> formatMemberUpdate(event, ale, channelIdToSendTo);
		case MEMBER_VOICE_KICK -> formatGeneric(event, ale, channelIdToSendTo);
		case MEMBER_VOICE_MOVE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_BULK_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_PIN -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_UNPIN -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case PRUNE -> formatGeneric(event, ale, channelIdToSendTo);
		case ROLE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case ROLE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case ROLE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case SCHEDULED_EVENT_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case SCHEDULED_EVENT_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case SCHEDULED_EVENT_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case STAGE_INSTANCE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case STAGE_INSTANCE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case STAGE_INSTANCE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case STICKER_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case STICKER_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case STICKER_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case THREAD_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case THREAD_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case THREAD_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case UNBAN -> formatUnban(event, ale, channelIdToSendTo);
		case UNKNOWN -> formatGeneric(event, ale, channelIdToSendTo);
		case VOICE_CHANNEL_STATUS_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case VOICE_CHANNEL_STATUS_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case WEBHOOK_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case WEBHOOK_REMOVE -> formatGeneric(event, ale, channelIdToSendTo);
		case WEBHOOK_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		default -> formatGeneric(event, ale, channelIdToSendTo);
		}
	}


	private void formatGeneric(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.LIGHT_GRAY);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			eb.addField(change, "from "+oldValue+" to "+newValue, false);		
		}
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();	 
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();	
	}

	private void formatInviteCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale , String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.CYAN);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true);

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
				eb.addField("Invite Created By", (inviter != null ? inviter.getAsMention() : ale.getUserId()), false);
				break;

			case "temporary":
				eb.addField("Temporary Invite", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;

			case "max_uses":
				int maxUses = Integer.parseInt(newValue.toString());
				eb.addField("Max Uses", (maxUses == 0 ? "Unlimited" : String.valueOf(maxUses)), false);
				break;
			case "uses", "flags":
				break;

			case "max_age":
				eb.addField("Expires After", DurationFormatter.formatInviteDuration((Integer) newValue), false);
				break;
			case "channel_id":
				Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+newValue.toString()+"`"), false);
				break;
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);
			}
		}

		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();

	}

	private void formatInviteDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.CYAN);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

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

			case "max_uses", "flags", "max_age":
				break;
			case "uses":
				eb.addField("Number of times the invite was used", oldValue.toString(), false);
				break;
			case "channel_id":
				Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+oldValue.toString()+"`"), false);
				break;
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);
			}
		}

		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}

	private void formatKick(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.ORANGE);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

		String moderatorId = ale.getUserId();
		String targetId = ale.getTargetId();
		String reason = ale.getReason();

		event.getJDA().retrieveUserById(moderatorId).queue(moderator->{
			event.getJDA().retrieveUserById(targetId).queue(target->{
				// if user objects are null we cannot use their mention so we instead use their IDs instead since they will never be null
				eb.addField("A member/application has been kicked", (moderator!=null ? moderator.getAsMention() : moderatorId)+" has kicked "+(target!=null ? target.getAsMention() : targetId), false);
				eb.addField("With Reason", (reason!=null ? reason : "No Reason Provided"), false);

				eb.setFooter("Audit Log Entry ID: "+ale.getId());
				eb.setTimestamp(ale.getTimeCreated()); 
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
			});	
		});
	}

	private void formatBan(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.RED);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

		String moderatorId = ale.getUserId();
		String targetId = ale.getTargetId();
		String reason = ale.getReason();

		event.getJDA().retrieveUserById(moderatorId).queue(moderator->{
			event.getJDA().retrieveUserById(targetId).queue(target->{
				// if user objects are null we cannot use their mention so we instead use their IDs instead since they will never be null
				eb.addField("A member has been banned", (moderator!=null ? moderator.getAsMention() : moderatorId)+" has banned "+(target!=null ? target.getAsMention() : targetId), false);
				eb.addField("With Reason", (reason!=null ? reason : "No Reason Provided"), false);

				eb.setFooter("Audit Log Entry ID: "+ale.getId());
				eb.setTimestamp(ale.getTimeCreated()); 
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
			});	
		});
	}
	
	private void formatUnban(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.GREEN);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

		String moderatorId = ale.getUserId();
		String targetId = ale.getTargetId();
		
		event.getJDA().retrieveUserById(moderatorId).queue(moderator->{
			event.getJDA().retrieveUserById(targetId).queue(target->{
				// if user objects are null we cannot use their mention so we instead use their IDs instead since they will never be null
				eb.addField("A member has been un-banned", (moderator!=null ? moderator.getAsMention() : moderatorId)+" has un-banned "+(target!=null ? target.getAsMention() : targetId), false);
				
				eb.setFooter("Audit Log Entry ID: "+ale.getId());
				eb.setTimestamp(ale.getTimeCreated()); 
				MessageEmbed mb = eb.build();
				event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
			});	
		});
	}
	
	private void formatMemberUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		User target = ale.getJDA().getUserById(ale.getTargetIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.CYAN);
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();

			switch(change) {

			case "communication_disabled_until":
				if(newValue==null) {
					eb.setColor(Color.GREEN);
					eb.addField("Timeout Lifted", "Timeout for "+(target !=null ? target.getAsMention() : ale.getTargetId())+ " has been removed", false);
				} else {
					eb.setColor(Color.YELLOW);
					eb.addField("Timeout Received", (target !=null ? target.getAsMention() : ale.getTargetId())+ " has received a timeout", false);
					eb.addField("Till", DurationFormatter.isoToLocalTimeCounter(String.valueOf(newValue)), false);
					eb.addField("Reason", (ale.getReason()!=null ? ale.getReason() : "No Reason Provided"), false);
				}
					
				break;

			case "nick":
				String targetMention = (target !=null ? target.getAsMention() : ale.getTargetId());
				
				if(oldValue!=null && newValue==null) { // resetting to default nickname
					eb.addField("Nickname Update", "Reset "+targetMention+"'s name", false);
				} else if(oldValue!=null && newValue!=null) { // changing from one nickname to another
					eb.addField("Nickname Update", "Updated "+targetMention+"'s name from "+oldValue.toString()+ " to "+ newValue.toString(), false);
				} else if(oldValue==null && newValue!=null) { // changing from default nickname to a new nickname
					eb.addField("Nickname Update", "Set "+targetMention+"'s name as "+ newValue.toString(), false);
				}
				break;

			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);
			}
		}

		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatBotAdd(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		User target = ale.getJDA().getUserById(ale.getTargetIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.CYAN);
		
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true);
		eb.addField("Added a bot: ", (target!=null ? target.getAsMention() : ale.getTargetId()), false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatIntegrationCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.PINK);
		
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true);
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "type":
				eb.addField("Integration Type", String.valueOf(newValue), false);
				break;
			
			case "name":
				eb.addField("Integration Name", String.valueOf(newValue), false);
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
			
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatIntegrationDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", ale.getType().toString(), true);
		eb.addField("Target Type", ale.getTargetType().toString(), true);
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "type":
				eb.addField("Integration Type", String.valueOf(oldValue), false);
				break;
			
			case "name":
				eb.addField("Integration Name", String.valueOf(oldValue), false);
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
			
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
}
