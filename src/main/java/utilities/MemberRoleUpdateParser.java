package utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MemberRoleUpdateParser {
	
	private MemberRoleUpdateParser() {
		throw new IllegalStateException("Utility Class");
	}
	// parses Strings of type  shown in this example [{name=2, id=1383465849507151922}] role_name, role_id into a map
	public static Map<String, String> parseRoleUpdate(Object roleUpdateValue) {
		
		if(roleUpdateValue==null) {
			return  Collections.emptyMap();
		}
		
		String roleUpdateValueString = String.valueOf(roleUpdateValue);
		if(roleUpdateValueString.isBlank()) {
			return Collections.emptyMap();
		}
		
		String cleanedroleValueString = StringUtils.strip(roleUpdateValueString, "[{}]");
		// the string should now be "name=2, id=1383465849507151922" without the quotation marks
		String[] splitValues = cleanedroleValueString.trim().split(",");
		// this should fill the array with two items
		// [name=2, id=1383465849507151922]
		// 
		
		Map<String, String> keyValueMap = new HashMap<>();
		for(String splitValue: splitValues) {
			String[] keyValue = StringUtils.split(splitValue, "="); 
			// this should create 2 arrays each of size 2
			// Array 1 = [name,2] & Array 2 = [id, 1383465849507151922]
			if(keyValue.length==2) {
				keyValueMap.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}
		
		return keyValueMap;
	}
}
