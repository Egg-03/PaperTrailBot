package listeners.auditloglistener;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import database.DatabaseConnector;
import database.TableNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utilities.DurationFormatter;
import utilities.PermissionResolver;
import utilities.TypeResolver;

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
		case AUTO_MODERATION_RULE_CREATE -> formatAutoModRuleCreate(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_DELETE -> formatAutoModRuleDelete(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case BAN -> formatBan(event, ale, channelIdToSendTo);
		case BOT_ADD -> formatBotAdd(event, ale, channelIdToSendTo);
		case CHANNEL_CREATE -> formatChannelCreate(event, ale, channelIdToSendTo);
		case CHANNEL_DELETE -> formatChannelDelete(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_CREATE -> formatChannelOverrideCreate(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_DELETE -> formatChannelOverrideDelete(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_UPDATE -> formatChannelOverrideUpdate(event, ale, channelIdToSendTo);
		case CHANNEL_UPDATE -> formatChannelUpdate(event, ale, channelIdToSendTo);
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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();

			switch(change) {

			case "code":
				eb.addField("Invite Code", String.valueOf(newValue), false);
				break;

			case "inviter_id":
				User inviter = ale.getJDA().getUserById(String.valueOf(newValue));
				eb.addField("Invite Created By", (inviter != null ? inviter.getAsMention() : ale.getUserId()), false);
				break;

			case "temporary":
				eb.addField("Temporary Invite", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;

			case "max_uses":
				int maxUses = Integer.parseInt(String.valueOf(newValue));
				eb.addField("Max Uses", (maxUses == 0 ? "Unlimited" : String.valueOf(maxUses)), false);
				break;
			case "uses", "flags":
				break;

			case "max_age":
				eb.addField("Expires After", DurationFormatter.formatSeconds((Integer) newValue), false);
				break;
			case "channel_id":
				GuildChannel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+String.valueOf(newValue)+"`"), false);
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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();

			switch(change) {

			case "code":
				eb.addField("Deleted Invite Code", String.valueOf(oldValue), false);
				break;

			case "inviter_id":
				User inviter = ale.getJDA().getUserById(String.valueOf(oldValue));
				eb.addField("Invite Deleted By", (inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
				break;

			case "temporary":
				eb.addField("Temporary Invite", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
				break;

			case "max_uses", "flags", "max_age":
				break;
			case "uses":
				eb.addField("Number of times the invite was used", String.valueOf(oldValue), false);
				break;
			case "channel_id":
				Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+String.valueOf(oldValue)+"`"), false);
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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);  

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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

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
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

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
					eb.addField("Nickname Update", "Updated "+targetMention+"'s name from "+String.valueOf(oldValue)+ " to "+ String.valueOf(newValue), false);
				} else if(oldValue==null && newValue!=null) { // changing from default nickname to a new nickname
					eb.addField("Nickname Update", "Set "+targetMention+"'s name as "+ String.valueOf(newValue), false);
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
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
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
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
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
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
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
	
	private void formatChannelCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "user_limit":
				eb.addField("User Limit", TypeResolver.formatNumberOrUnlimited((Integer) newValue), false);
				break;
				
			case "rate_limit_per_user":
				eb.addField("Slowmode", DurationFormatter.formatSeconds((Integer) newValue), false);
				break;
				
			case "type":
				eb.addField("Channel Type", TypeResolver.channelTypeResolver((Integer) newValue), false);
				break;
				
			case "nsfw":
				eb.addField("NSFW", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;
			
			case "permission_overwrites", "flags":
				break;
				
			case "name":
				eb.addField("Channel Name", String.valueOf(newValue), false);
				// provide a channel link next to its name
				GuildChannel targetChannel = ale.getGuild().getGuildChannelById(ale.getTargetId());
				eb.addField("Channel Link", (targetChannel!=null ? targetChannel.getAsMention() : ale.getTargetId()), true);
				break;
				
			case "bitrate":
				eb.addField("Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver((Integer) newValue), false);
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
	
	private void formatChannelUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		eb.addBlankField(true);
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "user_limit":
				eb.addField("Old User Limit", TypeResolver.formatNumberOrUnlimited((Integer) oldValue), true);
				eb.addField("New User Limit", TypeResolver.formatNumberOrUnlimited((Integer) newValue), true);
				eb.addBlankField(true);
				break;
				
			case "rate_limit_per_user":
				eb.addField("Old Slowmode Value", DurationFormatter.formatSeconds((Integer) oldValue), true);
				eb.addField("New Slowmode Value", DurationFormatter.formatSeconds((Integer) newValue), true);
				eb.addBlankField(true);
				break;
					
			case "nsfw":
				eb.addField("Old NSFW Settings", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), true);
				eb.addField("New NSFW Settings", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), true);
				eb.addBlankField(true);
				break;
			
			case "video_quality_mode":
				eb.addField("Old Quality Mode", (oldValue == null ? "Unknown" : TypeResolver.videoQualityModeResolver((Integer) oldValue)), true);
				eb.addField("New Quality Mode", (newValue == null ? "Unknown" : TypeResolver.videoQualityModeResolver((Integer) newValue)), true);
				eb.addBlankField(true);
				break;
				
			case "name":
				eb.addField("Old Channel Name", String.valueOf(oldValue), true);
				eb.addField("New Channel Name", String.valueOf(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "bitrate":
				eb.addField("Old Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver((Integer) oldValue), true);
				eb.addField("New Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver((Integer) newValue), true);
				eb.addBlankField(true);
				break;
				
			case "rtc_region":
				eb.addField("Old Region", String.valueOf(oldValue), true);
				eb.addField("New Region", String.valueOf(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "topic":
				eb.addField("Old Topic", String.valueOf(oldValue), true);
				eb.addField("New topic", String.valueOf(newValue), true);
				eb.addBlankField(true);
				break;

			case "default_auto_archive_duration":
				eb.addField("Old Hide After Inactivity Timer", (oldValue==null ? "N/A" : DurationFormatter.formatMinutes((Integer) oldValue)), true);
				eb.addField("New Hide After Inactivity Timer", (newValue==null ? "N/A" : DurationFormatter.formatMinutes((Integer) newValue)), true);
				eb.addBlankField(true);
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
	
	private void formatChannelDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
			case "name":
				eb.addField("Name", String.valueOf(oldValue), false);
				break;
				
			case "type":
				eb.addField("Type", TypeResolver.channelTypeResolver((Integer) oldValue), false);				
				break;
				
			case "user_limit", "rate_limit_per_user", "nsfw", "permission_overwrites", "video_quality_mode", "flags", "bitrate", "rtc_region":
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
	
	private void formatChannelOverrideCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "type":
				eb.addField("Override Type", TypeResolver.channelOverrideTypeResolver((Integer) newValue), false);				
				break;
			
			case "deny":
				// in case the newValue returns null, return an empty list having no permissions
				// usually newValue in deny/allow cases return 0 instead of null in case no overrides are created, but for null safety, this check is placed
				// the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
				List<String> deniedPermissions = (newValue==null ? Collections.emptyList() : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(newValue))));
				StringBuilder sbDenied = new StringBuilder();
				for(String permission: deniedPermissions) {
					sbDenied.append("❌").append(permission).append(System.lineSeparator());
				}
				eb.addField("Denied Permissions", sbDenied.toString(), false);
				break;
				
			case "allow":
				// in case the newValue returns null, return an empty list having no permissions
				// usually newValue in deny/allow cases return 0 instead of null in case no overrides are created, but for null safety, this check is placed
				// the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
				List<String> allowedPermissions = (newValue==null ? Collections.emptyList() : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(newValue))));
				StringBuilder sbAllowed = new StringBuilder();
				for(String permission: allowedPermissions) {
					sbAllowed.append("✅").append(permission).append(System.lineSeparator());
				}
				eb.addField("Allowed Permissions", sbAllowed.toString(), false);
				break;
				
			case "id":
				// id exposes the member/role id which for which the channel permissions are over-riden
				Member mb = event.getGuild().getMemberById(String.valueOf(newValue));
				Role r = event.getGuild().getRoleById(String.valueOf(newValue));
				
				String mentionableRoleOrMember = "";
				if(mb!=null) {
					mentionableRoleOrMember = mb.getAsMention();
				} else if (r!=null) {
					mentionableRoleOrMember = r.getAsMention();
				}
				eb.addField("Target", mentionableRoleOrMember, false);
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		// add the target channel whose permissions were over-riden
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		eb.addField("Target Channel", (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId()), false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelOverrideUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "deny":
				List<String> deniedPermissions = (newValue==null ? Collections.list(null) : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(newValue))));
				StringBuilder sbDenied = new StringBuilder();
				for(String permission: deniedPermissions) {
					sbDenied.append("❌").append(permission).append(System.lineSeparator());
				}
				// if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
				eb.addField("Denied Permissions", (sbDenied.toString().isBlank() ? "Permissions Synced With Category" : sbDenied.toString()), false);
				break;
				
			case "allow":
				List<String> allowedPermissions = (newValue==null ? Collections.emptyList() : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(newValue))));
				StringBuilder sbAllowed = new StringBuilder();
				for(String permission: allowedPermissions) {
					sbAllowed.append("✅").append(permission).append(System.lineSeparator());
				}
				// if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
				eb.addField("Allowed Permissions", (sbAllowed.toString().isBlank() ? "Permissions Synced With Category" : sbAllowed.toString()), false);
				break;
			
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		// add the target channel whose permissions were over-riden
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		eb.addField("Target Channel", (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId()), false);
			
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelOverrideDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "type":
				eb.addField("Override Type", TypeResolver.channelOverrideTypeResolver((Integer) oldValue), false);				
				break;
			
			case "deny":
				// in case the oldValue returns null, return an empty list having no permissions
				// usually oldValue in deny/allow cases return a defined number instead of null in case no overrides are deleted, but for null safety, this check is placed
				// the newValue will return null if an over-ride is deleted but we're not concerned with newValue
				List<String> deniedPermissions = (oldValue==null ? Collections.emptyList() : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(oldValue))));
				StringBuilder sbDenied = new StringBuilder();
				for(String permission: deniedPermissions) {
					sbDenied.append("❌").append(permission).append(System.lineSeparator());
				}
				eb.addField("Previously Denied Permissions", sbDenied.toString(), false);
				break;
				
			case "allow":
				// in case the oldValue returns null, return an empty list having no permissions
				// usually oldValue in deny/allow cases return a defined number instead of null in case no overrides are deleted, but for null safety, this check is placed
				// the newValue will return null if an over-ride is deleted but we're not concerned with newValue
				List<String> allowedPermissions = (oldValue==null ? Collections.emptyList() : PermissionResolver.getPermissionList(Long.valueOf(String.valueOf(oldValue))));
				StringBuilder sbAllowed = new StringBuilder();
				for(String permission: allowedPermissions) {
					sbAllowed.append("✅").append(permission).append(System.lineSeparator());
				}
				eb.addField("Previously Allowed Permissions", sbAllowed.toString(), false);
				break;
				
			case "id":
				// id exposes the member/role id which for which the channel permissions are over-riden
				Member mb = event.getGuild().getMemberById(String.valueOf(oldValue));
				Role r = event.getGuild().getRoleById(String.valueOf(oldValue));
				
				String mentionableRoleOrMember = "";
				if(mb!=null) {
					mentionableRoleOrMember = mb.getAsMention();
				} else if (r!=null) {
					mentionableRoleOrMember = r.getAsMention();
				}
				eb.addField("Deleted Target", mentionableRoleOrMember, false);
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		// add the target channel whose permissions were over-riden
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		eb.addField("Target Channel", (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId()), false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatAutoModRuleCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
				
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "exempt_roles":
				String roleIds = String.valueOf(newValue);
				String cleanedRoleIds = StringUtils.strip(roleIds, "[]");			
				List<String> roleIdList = Arrays.asList(StringUtils.split(cleanedRoleIds, ","));
				StringBuilder mentionableRoles = new StringBuilder();
				for(String roleId : roleIdList) {
					Role r = ale.getGuild().getRoleById(roleId.strip());
					mentionableRoles.append(r!=null ? r.getAsMention() : roleId.strip()).append(", ");
				}
				eb.addField("Exempt Roles: ", mentionableRoles.toString(), false);
				break;
				
			case "enabled":
				eb.addField("Enabled", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;
				
			case "trigger_type":
				eb.addField("Trigger Type", TypeResolver.automodTriggerType((Integer) newValue), false);
				break;
				
			case "actions":
				eb.addField("Actions", String.valueOf(newValue), false);
				break;
				
			case "exempt_channels":
				String channelIds = String.valueOf(newValue);
				String cleanedChannelIds = StringUtils.strip(channelIds, "[]");			
				List<String> channelIdList = Arrays.asList(StringUtils.split(cleanedChannelIds, ","));
				StringBuilder mentionableChannels = new StringBuilder();
				for(String channelId : channelIdList) {
					GuildChannel r = ale.getGuild().getGuildChannelById(channelId.strip());
					mentionableChannels.append(r!=null ? r.getAsMention() : channelId.strip()).append(", ");
				}
				eb.addField("Exempt Channels: ", mentionableChannels.toString(), false);
				break;
				
			case "event_type":
				eb.addField("Event Type", TypeResolver.automodEventType((Integer) newValue), false);
				break;
				
			case "trigger_metadata":
				eb.addField("Trigger Metadata", String.valueOf(newValue), false);
				break;
				
			case "name":	
				eb.addField("AutoMod Rule Name ", String.valueOf(newValue), false);
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
	
	private void formatAutoModRuleDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		
		eb.setDescription((executor != null ? executor.getAsMention() : ale.getUserId())+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
				
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "exempt_roles", "enabled", "trigger_type", "actions", "exempt_channels", "event_type", "trigger_metadata":
				break;
				
			case "name":	
				eb.addField("AutoMod Rule Name ", String.valueOf(oldValue), false);
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
