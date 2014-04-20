package resource;

public class String2URLFriendly {
	
	public static String transform(String string) {
		return string.replaceAll("\\s+","§");
	}
	
	public static String revert(String string) {
		return string.replaceAll("§"," ");
	}
	
}
