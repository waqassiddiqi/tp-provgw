package provgw.skycall.handler;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class AddSkypeContactCommandHandler extends CommandHandler {
	
	static {
		ResourceBundle myResources = ResourceBundle.getBundle("provgw");
		try {
			maxSkypeContacts = Integer.parseInt(myResources.getString("provgw.skype_contact.max"));
			skypeContactValidationPatter = myResources.getString("provgw.skype_contact.validation");
		} catch (Exception e) { }
	}
	
	static int maxSkypeContacts = 10;
	static String skypeContactValidationPatter = "^[a-z0-9_.-]{3,15}$";
	
	SubscriberDAO subDao;
	
	public AddSkypeContactCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 3, 0, ""));
		
		subDao = new SubscriberDAO();
	}

	@Override
	public String execute() {
		
		String msisdn = this.requestParameters.get("msisdn"); 
		String skypeId = this.requestParameters.get("skypeid");
		
		
		if(msisdn == null) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, ResponseBuilder.RESULTCODE_MSISDN_MISSING, "");
		}
		
		this.getSvcEntry().setMsisdn(msisdn);
		this.getSvcEntry().setSkypeId(skypeId);
		
		if(subDao.getSubscriberByMsisdn(msisdn.trim()) == null) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_MSISDN_NOT_REGISTERED, MessageRepository.getMessage("message.sub_not_exists"));
		}
		
		if(validateSkypeId(skypeId) == false) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_SKYPE_INVALID_ID, 
					MessageRepository.getMessage("message.skype_invalid_id"));
		}
		
		List<String> skypeContacts = this.subDao.getSkypeContacts(msisdn.trim());
		
		if(skypeContacts.size() >= maxSkypeContacts) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_REACHED_MAXIMUM_QUOTA, 
					MessageRepository.getMessage("message.skype_limit_exceeds"));
		}
		
		if(skypeContacts.size() > 0) {
			for(String contact : skypeContacts) {
				if(contact.trim().equalsIgnoreCase(skypeId.trim().toLowerCase())) {
					return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
							ResponseBuilder.RESULTCODE_SKYPE_CONTACT_ALREADY_EXISTS, 
							MessageRepository.getMessage("message.skype_already_exists", new String[] { skypeId }));
				}
			}
		}
		
		String virtualId = subDao.getVirtualId(skypeId, msisdn);
		
		if(virtualId != null && virtualId.trim().length() > 0) {
			this.setResult(true);
			this.getSvcEntry().setStat(1);
			
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, 
					MessageRepository.getMessage("message.skype_add_success", new String[] { skypeId }), 
					new String[] { "virtualID", virtualId });
		} else {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_ERROR, 
					MessageRepository.getMessage("message.server_error"));
		}
	}
	
	private boolean validateSkypeId(String skypeId) {
		
		if(skypeId == null)
			return false;
		
		if(skypeId.trim().length() <= 0)
			return false;
		
		
		Pattern pattern = Pattern.compile(skypeContactValidationPatter);
		Matcher matcher = pattern.matcher(skypeId);
		
		return matcher.matches();
	}
}