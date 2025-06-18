package org.papertrail.utilities;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class ColorFormatter {
	
	private ColorFormatter() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static String formatToHex(Object colorValue) {
		
		if(String.valueOf(colorValue).equals("null")) {
			return "N/A";
		}
		
		try {
			int color = Integer.parseInt(String.valueOf(colorValue));
			return String.format("#%06X", color);
		} catch (NumberFormatException e ) {
			return "No Parsable Color Integer Detected";
		} catch  (IllegalFormatException e) {
			return "Cannot parse color to hex";
		}
	}
	
	public static String formatGradientColorSystemToHex(Object gradientValue) {
		
		if(String.valueOf(gradientValue).equals("null")) {
			return  "N/A";
		}
		
		// Example gradient: "{tertiary_color=null, secondary_color=null, primary_color=3066993}"		
		String cleanedroleValueString = StringUtils.strip(String.valueOf(gradientValue), "{}");
		// the string should now be "tertiary_color=null, secondary_color=null, primary_color=3066993"
		String[] splitValues = cleanedroleValueString.trim().split(",");
		// this should fill the array with three items
		// [tertiary_color=null, secondary_color=null, primary_color=3066993]
		Map<String, String>colorKeyAndValues = new HashMap<>();
		for(String splitValue: splitValues) {
			String[] keyValueSplit = StringUtils.split(splitValue, "="); // further split into 3 arrays [tertiary_color, null] [secondary_color, null] [primary_color, 3066993]
			if(keyValueSplit.length==2) {
				colorKeyAndValues.put(keyValueSplit[0].trim(), formatToHex(keyValueSplit[1].trim())); // put them into the map after converting the int color to hex
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> colorKeyValue: colorKeyAndValues.entrySet()) {
			sb.append(colorKeyValue.getKey()).append(": ").append(colorKeyValue.getValue()).append(System.lineSeparator());
		}
		
		return sb.toString();
	}
}
