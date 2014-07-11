package provgw.skycall.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MessageRepository {
	private static HashMap<String, String> messagesRepo;
	
	static  {
		messagesRepo = new HashMap<String, String>();
		
		ResourceBundle myResources = ResourceBundle.getBundle("provgw");
		List<String> keys = Collections.list(myResources.getKeys() );
		
		for(String key : keys) {
			if(key.startsWith("message.")) {
				messagesRepo.put(key, myResources.getString(key));
			}
		}
	}
	
	public static String getMessage(String key) {
		if(!messagesRepo.containsKey(key))
			return "";
		
		return messagesRepo.get(key);
	}
	
	public static String getMessage(String key, String... args) {
		if(!messagesRepo.containsKey(key))
			return "";
		
		String str = messagesRepo.get(key);
		
		for(int i=1; i<=args.length; i++) {
			str = str.replace("%" + i, args[i-1]);
		}
		
		return str;
	}
}
