package org.papertrail.listeners.loglisteners;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;
import org.papertrail.utilities.ColorFormatter;
import org.papertrail.utilities.DurationFormatter;
import org.papertrail.utilities.GuildSystemChannelFlagResolver;
import org.papertrail.utilities.MemberRoleUpdateParser;
import org.papertrail.utilities.PermissionResolver;
import org.papertrail.utilities.TypeResolver;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogListener extends ListenerAdapter{

	private DatabaseConnector dc;

	public AuditLogListener(DatabaseConnector dc) {
		this.dc=dc;
	}

	@Override
	public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {

		// this will return a non-null text id if a channel was previously registered in the database
		String registeredChannelId=dc.retrieveChannelId(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);

		if(registeredChannelId==null ||registeredChannelId.isBlank()) {
			return;
		}

		AuditLogEntry ale = event.getEntry();
		auditLogParser(event, ale, registeredChannelId);
	}

	private void auditLogParser(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		ActionType action = ale.getType();
		switch(action) {
		case APPLICATION_COMMAND_PRIVILEGES_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		
		case AUTO_MODERATION_FLAG_TO_CHANNEL -> formatAutoModFlagToChannel(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_MEMBER_TIMEOUT -> formatAutoModMemberTimeout(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> formatAutoModRuleBlockMessage(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_CREATE -> formatAutoModRuleCreate(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_DELETE -> formatAutoModRuleDelete(event, ale, channelIdToSendTo);
		case AUTO_MODERATION_RULE_UPDATE -> formatAutoModRuleUpdate(event, ale, channelIdToSendTo);
		
		case KICK -> formatKick(event, ale, channelIdToSendTo);
		case PRUNE -> formatGeneric(event, ale, channelIdToSendTo);
		case BAN -> formatBan(event, ale, channelIdToSendTo);
		case UNBAN -> formatUnban(event, ale, channelIdToSendTo);
		case BOT_ADD -> formatBotAdd(event, ale, channelIdToSendTo);
		
		case CHANNEL_CREATE -> formatChannelCreate(event, ale, channelIdToSendTo);
		case CHANNEL_UPDATE -> formatChannelUpdate(event, ale, channelIdToSendTo);
		case CHANNEL_DELETE -> formatChannelDelete(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_CREATE -> formatChannelOverrideCreate(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_DELETE -> formatChannelOverrideDelete(event, ale, channelIdToSendTo);
		case CHANNEL_OVERRIDE_UPDATE -> formatChannelOverrideUpdate(event, ale, channelIdToSendTo);
		
		case ROLE_CREATE -> formatRoleCreate(event, ale, channelIdToSendTo);
		case ROLE_UPDATE -> formatRoleUpdate(event, ale, channelIdToSendTo);
		case ROLE_DELETE -> formatRoleDelete(event, ale, channelIdToSendTo);
		
		case EMOJI_CREATE -> formatEmojiCreate(event, ale, channelIdToSendTo);
		case EMOJI_UPDATE -> formatEmojiUpdate(event, ale, channelIdToSendTo);
		case EMOJI_DELETE -> formatEmojiDelete(event, ale, channelIdToSendTo);
		
		case STICKER_CREATE -> formatStickerCreate(event, ale, channelIdToSendTo);
		case STICKER_UPDATE -> formatStickerUpdate(event, ale, channelIdToSendTo);
		case STICKER_DELETE -> formatStickerDelete(event, ale, channelIdToSendTo);
			
		case GUILD_UPDATE -> formatGuildUpdate(event, ale, channelIdToSendTo);
		
		case INTEGRATION_CREATE -> formatIntegrationCreate(event, ale, channelIdToSendTo);
		case INTEGRATION_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case INTEGRATION_DELETE -> formatIntegrationDelete(event, ale, channelIdToSendTo);
		
		case INVITE_CREATE -> formatInviteCreate(event, ale, channelIdToSendTo);
		case INVITE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		case INVITE_DELETE -> formatInviteDelete(event, ale, channelIdToSendTo);
				
		case MEMBER_ROLE_UPDATE -> formatMemberRoleUpdate(event, ale, channelIdToSendTo);
		case MEMBER_UPDATE -> formatMemberUpdate(event, ale, channelIdToSendTo);

		case MEMBER_VOICE_KICK -> formatMemberVoiceKick(event, ale, channelIdToSendTo);
		case MEMBER_VOICE_MOVE -> formatMemberVoiceMove(event, ale, channelIdToSendTo);
		
		// this seemingly don't trigger properly, or are unreliable
		case MESSAGE_BULK_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case MESSAGE_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		
		case MESSAGE_PIN -> formatMessagePin(event, ale, channelIdToSendTo);
		case MESSAGE_UNPIN -> formatMessageUnpin(event, ale, channelIdToSendTo);
		
		case SCHEDULED_EVENT_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case SCHEDULED_EVENT_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case SCHEDULED_EVENT_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);

		case STAGE_INSTANCE_CREATE -> formatStageInstanceCreate(event, ale, channelIdToSendTo);
		case STAGE_INSTANCE_UPDATE -> formatStageInstanceUpdate(event, ale, channelIdToSendTo);
		case STAGE_INSTANCE_DELETE -> formatStageInstanceDelete(event, ale, channelIdToSendTo);
		
		case THREAD_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case THREAD_DELETE -> formatGeneric(event, ale, channelIdToSendTo);
		case THREAD_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
	
		case VOICE_CHANNEL_STATUS_DELETE -> formatVoiceChannelStatusDelete(event, ale, channelIdToSendTo);
		case VOICE_CHANNEL_STATUS_UPDATE -> formatVoiceChannelStatusUpdate(event, ale, channelIdToSendTo);

		case WEBHOOK_CREATE -> formatGeneric(event, ale, channelIdToSendTo);
		case WEBHOOK_REMOVE -> formatGeneric(event, ale, channelIdToSendTo);
		case WEBHOOK_UPDATE -> formatGeneric(event, ale, channelIdToSendTo);
		
		case UNKNOWN -> formatGeneric(event, ale, channelIdToSendTo);
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
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
				
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
				eb.addField("Expires After", DurationFormatter.formatSeconds(newValue), false);
				break;
			case "channel_id":
				GuildChannel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+newValue+"`"), false);
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.BLUE);
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
				eb.addField("Invite Channel", (channel != null ? channel.getAsMention() : "`"+oldValue+"`"), false);
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
				
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.ORANGE);
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		String moderatorId = ale.getUserId();
		String targetId = ale.getTargetId();
		String reason = ale.getReason();
		
		// A REST Action is required here because kicked members are not cached
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
					eb.addField("Timeout Lifted", "Timeout for "+mentionableTarget+ " has been removed", false);
				} else {
					eb.setColor(Color.YELLOW);
					eb.addField("Timeout Received", mentionableTarget+ " has received a timeout", false);
					eb.addField("Till", DurationFormatter.isoToLocalTimeCounter(newValue), false);
					eb.addField("Reason", (ale.getReason()!=null ? ale.getReason() : "No Reason Provided"), false);
				}
					
				break;

			case "nick":							
				if(oldValue!=null && newValue==null) { // resetting to default nickname
					eb.addField("Nickname Update", "Reset "+mentionableTarget+"'s name", false);
				} else if(oldValue!=null && newValue!=null) { // changing from one nickname to another
					eb.addField("Nickname Update", "Updated "+mentionableTarget+"'s name from "+oldValue+ " to "+ newValue, false);
				} else if(oldValue==null && newValue!=null) { // changing from default nickname to a new nickname
					eb.addField("Nickname Update", "Set "+mentionableTarget+"'s name as "+ newValue, false);
				}
				break;
				
			case "mute":
				eb.addField("Is Muted", "Set "+mentionableTarget+"'s Mute Status as "+ ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;
				
			case "deaf":							
				eb.addField("Is Deafened", "Set "+mentionableTarget+"'s Deafened Status as "+ ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;
				
			case "bypasses_verification":
				eb.addField("Bypass Verification", "Set "+mentionableTarget+"'s verification bypass status as "+ ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
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
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.CYAN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		eb.addField("Added a bot: ", mentionableTarget, false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatIntegrationCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());				
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
	
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
		GuildChannel targetChannel = ale.getGuild().getGuildChannelById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "user_limit":
				eb.addField("User Limit", TypeResolver.formatNumberOrUnlimited(newValue), false);
				break;
				
			case "rate_limit_per_user":
				eb.addField("Slowmode", DurationFormatter.formatSeconds(newValue), false);
				break;
				
			case "type":
				eb.addField("Channel Type", TypeResolver.channelTypeResolver(newValue), false);
				break;
				
			case "nsfw":
				eb.addField("NSFW", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
				break;
			
			case "permission_overwrites", "flags":
				break;
				
			case "name":
				eb.addField("Channel Name", String.valueOf(newValue), false);
				// provide a channel link next to its name. This mentionable channel can be obtained via the target ID of ALE
				eb.addField("Channel Link", mentionableTargetChannel, true);
				break;
				
			case "bitrate":
				eb.addField("Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver(newValue), false);
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
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
				eb.addField("Old User Limit", TypeResolver.formatNumberOrUnlimited(oldValue), true);
				eb.addField("New User Limit", TypeResolver.formatNumberOrUnlimited(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "rate_limit_per_user":
				eb.addField("Old Slowmode Value", DurationFormatter.formatSeconds(oldValue), true);
				eb.addField("New Slowmode Value", DurationFormatter.formatSeconds(newValue), true);
				eb.addBlankField(true);
				break;
					
			case "nsfw":
				eb.addField("Old NSFW Settings", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), true);
				eb.addField("New NSFW Settings", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), true);
				eb.addBlankField(true);
				break;
			
			case "video_quality_mode":
				eb.addField("Old Quality Mode", TypeResolver.videoQualityModeResolver(oldValue), true);
				eb.addField("New Quality Mode", TypeResolver.videoQualityModeResolver(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "name":
				eb.addField("Old Channel Name", String.valueOf(oldValue), true);
				eb.addField("New Channel Name", String.valueOf(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "bitrate":
				eb.addField("Old Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver(oldValue), true);
				eb.addField("New Voice Channel Bitrate", TypeResolver.voiceChannelBitrateResolver(newValue), true);
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
				eb.addField("Old Hide After Inactivity Timer", DurationFormatter.formatMinutes(oldValue), true);
				eb.addField("New Hide After Inactivity Timer", DurationFormatter.formatMinutes(newValue), true);
				eb.addBlankField(true);
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		// mention the channel that got updated, id can be exposed via ALE's TargetID
		eb.addField("Target Channel", mentionableTargetChannel, false);
			
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetIdLong());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId()); // this will return only the ID cause the channel with the ID has been deleted
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
				eb.addField("Type", TypeResolver.channelTypeResolver(oldValue), false);				
				break;
				
			case "user_limit", "rate_limit_per_user", "nsfw", "permission_overwrites", "video_quality_mode", "flags", "bitrate", "rtc_region":
				break;
				
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		eb.addField("Deleted Channel ID", mentionableTargetChannel, false);
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelOverrideCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "type":
				eb.addField("Override Type", TypeResolver.channelOverrideTypeResolver(newValue), false);				
				break;
			
			case "deny":
				// the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
				// the new value contains the list of denied permissions the moderator sets when creating overrides for the first time				
				eb.addField("Denied Permissions", PermissionResolver.getParsedPermissions(newValue, "❌"), false);
				break;
				
			case "allow":
				// the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
				// the new value contains the list of allowed permissions the moderator sets when creating overrides for the first time				
				eb.addField("Allowed Permissions", PermissionResolver.getParsedPermissions(newValue, "✅"), false);
				break;
				
			case "id":
				// id exposes the member/role id which for which the channel permissions are over-riden
				// only one member/role permissions can be over-riden at a time
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
		// exposed via ALE's TargetID
		eb.addField("Target Channel", mentionableTargetChannel, false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelOverrideUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "deny":
				String deniedPerms = PermissionResolver.getParsedPermissions(newValue, "❌");
				// if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
				eb.addField("Denied Permissions", (deniedPerms.isBlank() ? "Permissions Synced With Category" : deniedPerms), false);
				break;
				
			case "allow":
				String allowedPerms = PermissionResolver.getParsedPermissions(newValue, "✅");			
				// if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
				eb.addField("Allowed Permissions", (allowedPerms.isBlank() ? "Permissions Synced With Category" : allowedPerms), false);
				break;
			
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		// add the target channel whose permissions were over-riden
		// can be retrieved via ALE's TargetID
		eb.addField("Target Channel", mentionableTargetChannel, false);
			
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatChannelOverrideDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
						
			case "type":
				eb.addField("Override Type", TypeResolver.channelOverrideTypeResolver(oldValue), false);				
				break;
			
			case "deny":	
				// the newValue will return null if an over-ride is deleted but we're not concerned with newValue
				// the oldValue returns the permissions the channel was previously denied
				eb.addField("Previously Denied Permissions", PermissionResolver.getParsedPermissions(oldValue, "❌"), false);
				break;
				
			case "allow":
				/// the newValue will return null if an over-ride is deleted but we're not concerned with newValue
				// the oldValue returns the permissions the channel was previously allowed
				eb.addField("Previously Allowed Permissions", PermissionResolver.getParsedPermissions(oldValue, "✅"), false);
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
		// can be retrieved via ALE's TargetID
		eb.addField("Target Channel", mentionableTargetChannel, false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatAutoModRuleCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
				eb.addField("Trigger Type", TypeResolver.automodTriggerTypeResolver(newValue), false);
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
				eb.addField("Event Type", TypeResolver.automodEventTypeResolver(newValue), false);
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
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
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
	
	private void formatAutoModRuleUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
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
				eb.addField("New Exempt Roles: ", mentionableRoles.toString(), false);
				break;
				
						
			case "actions":
				eb.addField("New Actions", String.valueOf(newValue), false);
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
				eb.addField("New Exempt Channels: ", mentionableChannels.toString(), false);
				break;
				
				
			case "trigger_metadata":
				eb.addField("New Trigger Metadata", String.valueOf(newValue), false);
				break;
													
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		// add name of the rule which got updated
		AutoModRule rule = ale.getGuild().retrieveAutoModRuleById(ale.getTargetId()).complete();
		eb.addField("AutoMod Rule Name ", rule.getName(), false);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatEmojiCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
				
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":
				eb.addField("Emoji Name", String.valueOf(newValue), false);
				eb.addField("Emoji", "<:"+newValue+":"+ale.getTargetId()+">", false); // ale's TargetID retrieves the ID of the created emoji
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
	
	private void formatEmojiUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
	
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
				
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":
				eb.addField("Emoji Name Updated", "From "+oldValue+" to "+newValue, false);
				eb.addField("Target Emoji", "<:"+newValue+":"+ale.getTargetId()+">", false);
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
	
	private void formatEmojiDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
				
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":
				eb.addField("Deleted Emoji", ":"+oldValue+":", false);			
				break;
													
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		eb.addField("Deleted Emoji ID", ale.getTargetId(), false);
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatStickerCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
			
			case "format_type", "type", "asset", "available", "guild_id":			
				break;
			
			case "id":
				eb.addField("Sticker ID", String.valueOf(newValue), false);
				GuildSticker sticker = event.getGuild().getStickerById(String.valueOf(newValue));
				eb.addField("Sticker Link", (sticker!=null ? sticker.getIconUrl() : "N/A"), false);
				break;
			
			case "tags":
				eb.addField("Related Emoji", String.valueOf(newValue), false);
				break;
			
			case "description":
				eb.addField("Description", String.valueOf(newValue), false);
				break;
			
			case "name":
				eb.addField("Sticker Name", String.valueOf(newValue), false);
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
	
	private void formatStickerDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
			
			case "format_type", "type", "asset", "available", "guild_id":			
				break;
			
			case "id":
				eb.addField("Sticker ID", String.valueOf(oldValue), false);
				break;
			
			case "tags":
				eb.addField("Related Emoji", String.valueOf(oldValue), false);
				break;
			
			case "description":
				eb.addField("Description", String.valueOf(oldValue), false);
				break;
			
			case "name":
				eb.addField("Sticker Name", String.valueOf(oldValue), false);
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
	
	private void formatStickerUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
			
			case "format_type", "type", "asset", "available", "guild_id", "id":			
				break;
			
			case "tags":
				eb.addField("Related Emoji", "from "+oldValue+" to "+newValue, false);
				break;
			
			case "description":
				eb.addField("Description", "from `"+oldValue+"` to `"+newValue+"`", false);
				break;
			
			case "name":
				eb.addField("Sticker Name", "from `"+oldValue+"` to `"+newValue+"`", false);
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
	
	private void formatGuildUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.MAGENTA);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "description":
				eb.addField("Description Change", "from `"+oldValue+"` to `"+newValue+"`", false);
				break;
			
			case "icon_hash":
				eb.addField("Icon Hash Change", "from `"+oldValue+"` to `"+newValue+"`", false);
				break;
			
			case "name":
				eb.addField("Guild Name Change", "from `"+oldValue+"` to `"+newValue+"`", false);
				break;
				
			case "afk_channel_id":
				eb.addField("AFK Channel Changed To", "`"+newValue+"`", false);		
				break;
			
			case "default_message_notifications":
				eb.addField("Default Message Notifications Update", "`"+newValue+"`", false);
				break;
			
			case "afk_timeout":
				eb.addField("AFK Channel Timeout Change", "`"+newValue+"s`", false);
				break;
						
			case "system_channel_id":
				eb.addField("Community Updates Channel Changed To", "`"+newValue+"`", false);		
				break;
			
			case "widget_enabled":
				eb.addField("Widget Enabled", "`"+newValue+"`", false);
				break;
				
			case "widget_channel_id":		
				eb.addField("Widget Channel Changed To", "`"+newValue+"`", false);
				break;
				
			case "premium_progress_bar_enabled":
				eb.addField("Server Boost Progress Bar Enabled", "`"+newValue+"`", false);
				break;
				
			case "mfa_level":
				eb.addField("MFA Requirement", "`"+newValue+"`", false);
				break;
				
			case "verification_level":
				eb.addField("Verification Level", "`"+TypeResolver.guildVerificationLevelResolver(newValue)+"`", false);
				break;
				
			case "owner_id":
				User oldOwner = ale.getJDA().getUserById(String.valueOf(oldValue));
				User newOwner = ale.getJDA().getUserById(String.valueOf(newValue));
				String mentionableOldOwner = (oldOwner!=null ? oldOwner.getAsMention() : String.valueOf(oldValue));
				String mentionableNewOwner = (newOwner!=null ? newOwner.getAsMention() : String.valueOf(newValue));
				eb.addField("Ownership Change", "from "+mentionableOldOwner+" to "+mentionableNewOwner, false);
				break;
				
			case "public_updates_channel_id":
				eb.addField("Announcements Channel Changed To", "`"+newValue+"`", false);		
				break;
								
			case "rules_channel_id":
				eb.addField("Rules Channel Changed To", "`"+newValue+"`", false);		
				break;
				
				
			case "system_channel_flags":
				eb.addField("System Channel Flags", GuildSystemChannelFlagResolver.getParsedFlags(newValue), false);
				break;
				
			case "explicit_content_filter":
				eb.addField("Explicit Content Filter", TypeResolver.explicitFilterTypeResolver(newValue), false);
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
	
	private void formatMemberRoleUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		User target = ale.getJDA().getUserById(ale.getTargetIdLong());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.CYAN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "$add":
				eb.setColor(Color.GREEN);
				eb.addField("Target Member", mentionableTarget, false);
				Map<String, String> addedRoleNameAndId = MemberRoleUpdateParser.parseRoleUpdate(newValue);
				eb.addField("Role Added", "✅ "+addedRoleNameAndId.getOrDefault("name", "`ERROR: Not Found`"), false);
				eb.addField("Added Role ID", addedRoleNameAndId.getOrDefault("id", "`ERROR: Not Found`"), false);
				break;
				
			case "$remove":
				eb.setColor(Color.RED);
				eb.addField("Target Member", mentionableTarget, false);
				Map<String, String> removedRoleNameAndId = MemberRoleUpdateParser.parseRoleUpdate(newValue);
				eb.addField("Role Removed", "❌ "+removedRoleNameAndId.getOrDefault("name", "`ERROR: Not Found`"), false);
				eb.addField("Removed Role ID", removedRoleNameAndId.getOrDefault("id", "`ERROR: Not Found`"), false);
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
	
	private void formatRoleCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());
		Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetRole = (targetRole !=null ? targetRole.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		eb.addField("Target Role", mentionableTargetRole, false);
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":		
				eb.addField("Role Name", String.valueOf(newValue), false);
				break;
			
				/*
				 * discord for some reason shows the following to be default/null even
				 * when you set them during the creation of the role itself
				 * and delegates them to ROLE_UPDATE event
				 */
			case "colors", "hoist", "color", "permissions", "mentionable": 
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
	
	private void formatRoleUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());
		Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetRole = (targetRole !=null ? targetRole.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		eb.addField("Target Role", mentionableTargetRole, false);
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":
				eb.addField("Old Role Name", String.valueOf(oldValue), true);
				eb.addField("New Role Name", String.valueOf(newValue), true);
				eb.addBlankField(true);
				break;
			
			case "hoist":
				eb.addField("Old Display Seperately", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), true);
				eb.addField("New Display Seperately", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), true);
				eb.addBlankField(true);
				break;	
				
			case "color":
				eb.addField("Old Color", ColorFormatter.formatToHex(oldValue), true);
				eb.addField("New Color", ColorFormatter.formatToHex(newValue), true);
				eb.addBlankField(true);
				break;
				
			case "permissions":
				eb.addField("Old Role Permissions", PermissionResolver.getParsedPermissions(oldValue, "✅"), true);
				eb.addField("New Role Permissions", PermissionResolver.getParsedPermissions(newValue, "✅"), true);
				eb.addBlankField(true);
				break;
				
			case "mentionable":
				eb.addField("Old Mentionable", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), true);
				eb.addField("New Mentionable", ((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), true);
				eb.addBlankField(true);
				break;
			
			case "colors":
				eb.addField("Old Gradient Color System", ColorFormatter.formatGradientColorSystemToHex(oldValue), true);
				eb.addField("New Gradient Color System", ColorFormatter.formatGradientColorSystemToHex(newValue), true);
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
	
	private void formatRoleDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());
		Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());
		
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		String mentionableTargetRole = (targetRole !=null ? targetRole.getAsMention() : ale.getTargetId()); // this will always return the ID only
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 
		
		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {	
							
			case "name":		
				eb.addField("Role Name", String.valueOf(oldValue), false);
				break;
			
			case "hoist":
				eb.addField("Display Seperately", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
				break;	
				
			case "color": 
				eb.addField("Color", ColorFormatter.formatToHex(oldValue), false);
				break;
				
			case "permissions": 
				eb.addField("Role Permissions", PermissionResolver.getParsedPermissions(oldValue, "✅"), false);
				break;
				
			case "mentionable": 
				eb.addField("Mentionable", ((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
				break;
			
			case "colors":
				eb.addField("Gradient Color System", ColorFormatter.formatGradientColorSystemToHex(oldValue), false);
				break;
			default:
				eb.addField(change, "from "+oldValue+" to "+newValue, false);			
			}	
		}
		
		eb.addField("Deleted Role ID", mentionableTargetRole, false);
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();

		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	// the audit log does not expose much information regarding member vc move and kick events
	// therefore GuildVoiceListener has been created to know about channels the target has been moved or kicked from
	private void formatMemberVoiceKick(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatMemberVoiceMove(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
			
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatVoiceChannelStatusUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetId());
		String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has updated the status of the voice channel: "+mentionableTargetChannel);
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatVoiceChannelStatusDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserId());	
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetId());
		String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());
		
		eb.setDescription(mentionableExecutor+" has deleted the status of the voice channel: "+mentionableTargetChannel);
		eb.setColor(Color.ORANGE);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatAutoModFlagToChannel(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
			
		User targetUser = ale.getJDA().getUserById(ale.getTargetId());
		String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());
		
		eb.setDescription("Automod has flagged a message sent by: "+mentionableTargetUser);
		eb.setColor(Color.YELLOW);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatAutoModMemberTimeout (GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
			
		User targetUser = ale.getJDA().getUserById(ale.getTargetId());
		String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());
		
		eb.setDescription("Automod has timed out"+mentionableTargetUser+" for a defined rule violation");
		eb.setColor(Color.MAGENTA);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatAutoModRuleBlockMessage (GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
			
		User targetUser = ale.getJDA().getUserById(ale.getTargetId());
		String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());
		
		eb.setDescription("Automod has deleted a message sent by: "+mentionableTargetUser);
		eb.setColor(Color.ORANGE);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatMessagePin(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
			
		User executor = ale.getJDA().getUserById(ale.getTargetId());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());
			
		eb.setDescription(mentionableExecutor+ "pinned a message");
		eb.setColor(Color.PINK);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	// these audit log events don't expose anything other than the executor of the event
	private void formatMessageUnpin(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Audit Log Entry");
			
		User executor = ale.getJDA().getUserById(ale.getTargetId());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());
				
		eb.setDescription(mentionableExecutor+ "has un-pinned a message");
		eb.setColor(Color.MAGENTA);
		
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
		
		eb.setFooter("Audit Log Entry ID: "+ale.getId());
		eb.setTimestamp(ale.getTimeCreated());

		MessageEmbed mb = eb.build();
		event.getGuild().getTextChannelById(channelIdToSendTo).sendMessageEmbeds(mb).queue();
	}
	
	private void formatStageInstanceCreate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.GREEN);
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "topic":
				eb.addField("Stage Topic", String.valueOf(newValue), false);
				break;
				
			case "privacy_level":
				eb.addField("Stage Privacy", String.valueOf(newValue), false);
				eb.addField("Stage Privacy Result Inference", "-# A value of 1 means PUBLIC (deprecated) and 2 means GUILD_ONLY", false);
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
	
	private void formatStageInstanceUpdate(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
	
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.YELLOW);
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "topic":
				eb.addField("OldStage Topic", String.valueOf(oldValue), false);
				eb.addField("New Stage Topic", String.valueOf(newValue), false);
				break;
				
			case "privacy_level":
				eb.addField("Old Stage Privacy", String.valueOf(oldValue), false);
				eb.addField("New Stage Privacy", String.valueOf(newValue), false);
				eb.addField("Stage Privacy Result Inference", "-# A value of 1 means PUBLIC (deprecated) and 2 means GUILD_ONLY", false);
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
	
	private void formatStageInstanceDelete(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

		EmbedBuilder eb = new EmbedBuilder(); 
		eb.setTitle("Audit Log Entry");
		
		User executor = ale.getJDA().getUserById(ale.getUserIdLong());
		String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
		
		eb.setDescription(mentionableExecutor+" has executed the following action:");
		eb.setColor(Color.RED);
		eb.addField("Action Type", String.valueOf(ale.getType()), true);
		eb.addField("Target Type", String.valueOf(ale.getTargetType()), true); 

		for(Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
			String change = changes.getKey();
			Object oldValue = changes.getValue().getOldValue();
			Object newValue = changes.getValue().getNewValue();
			
			switch(change) {
			case "topic":
				eb.addField("Deleted Stage Topic", String.valueOf(oldValue), false);
				break;
				
			case "privacy_level":
				eb.addField("Deleted Stage Privacy", String.valueOf(oldValue), false);
				eb.addField("Stage Privacy Result Inference", "-# A value of 1 means PUBLIC (deprecated) and 2 means GUILD_ONLY", false);
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
