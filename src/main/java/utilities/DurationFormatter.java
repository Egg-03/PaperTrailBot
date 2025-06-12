package utilities;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DurationFormatter {
	
	private DurationFormatter() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static String formatSeconds(int seconds) {
		if (seconds == 0) return "No Limits";
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
	
	public static String formatMinutes(int minutes) {
		if (minutes == 0) return "No Limits";
		Duration d = Duration.ofMinutes(minutes);
		long days = d.toDays();
		long hours = d.toHoursPart();
		
		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append("d ");
		if (hours > 0) sb.append(hours).append("h ");
		return sb.toString().trim();
	}
	
	public static String isoToLocalTimeCounter(String isoTime) {
		
		if(isoTime==null || isoTime.equals("null") || isoTime.isBlank()) {
			return "N/A";
		}
		
		OffsetDateTime odt = OffsetDateTime.parse(isoTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		long unixTimestamp = odt.toEpochSecond();
		return "<t:" + unixTimestamp + ":f>";
	}
}
