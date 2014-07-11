package provgw.skycall.util;

public class ResponseBuilder {
	
	public final static String RESULTCODE_SUCCESS = "000";
	public final static String RESULTCODE_MSISDN_MISSING = "101";
	public final static String RESULTCODE_MSISDN_ALREADY_REGISTERED = "102";
	public final static String RESULTCODE_MSISDN_NOT_REGISTERED = "402";
	public final static String RESULTCODE_REACHED_MAXIMUM_QUOTA = "403";
	public final static String RESULTCODE_SKYPE_INVALID_ID = "480";
	public final static String RESULTCODE_SKYPE_CONTACT_ALREADY_EXISTS = "490";
	public final static String RESULTCODE_TIME_OUT = "604";
	public final static String RESULTCODE_ERROR = "605";
	
	public final static String RESULT_SUCCESS = "0";
	public final static String RESULT_FAILED = "1";
	
	
	public static String build(String result, String resultCode, String resultMessage) {
		StringBuilder sb = new StringBuilder("<Response>");
		sb.append("<result>" + result + "</result>");
		sb.append("<resultCode>" + resultCode + "</resultCode>");
		sb.append("<resultMessage>" + resultMessage + "</resultMessage>");
		
		sb.append("</Response>");
		return sb.toString();
	}
	
	public static String build(String result, String resultCode, String resultMessage, String... nameValuePairs) {
		StringBuilder sb = new StringBuilder("<Response>");
		sb.append("<result>" + result + "</result>");
		sb.append("<resultCode>" + resultCode + "</resultCode>");
		sb.append("<resultMessage>" + resultMessage + "</resultMessage>");
		
		if(nameValuePairs.length > 0 && nameValuePairs.length % 2 == 0) {
			for(int i=0; i<=nameValuePairs.length/2; i+=2) {
				sb.append("<");
				sb.append(nameValuePairs[i]);
				sb.append(">");
				
				sb.append(nameValuePairs[i + 1]);
				
				sb.append("</");
				sb.append(nameValuePairs[i]);
				sb.append(">");
			}
		}
		
		sb.append("</Response>");
		return sb.toString();
	}
}