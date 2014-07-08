package provgw.skycall.handler;

import java.util.HashMap;
import java.util.Map;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class QuerySkypeContactListCommandHandler extends CommandHandler {
	
	SubscriberDAO subDao;
	
	public QuerySkypeContactListCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 5, 0, ""));
		
		subDao = new SubscriberDAO();
	}

	@Override
	public String execute() {
		
		String msisdn = this.requestParameters.get("msisdn");
		
		if(msisdn == null) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, ResponseBuilder.RESULTCODE_MSISDN_MISSING, "");
		}
		
		this.getSvcEntry().setMsisdn(msisdn);
		
		if(subDao.getSubscriberByMsisdn(msisdn.trim()) == null) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_MSISDN_NOT_REGISTERED, MessageRepository.getMessage("message.sub_not_exists"));
		}
		
		Map<String, String> skypeContacts = this.subDao.getSkypeContactsWithVirtualIds(msisdn.trim());		
		
		this.setResult(true);
		this.getSvcEntry().setStat(0);
		
		String[] contacts = new String[skypeContacts.size() * 2];
		int index = 0;
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<String, String> entry : skypeContacts.entrySet()) {
		    String skypeId = entry.getKey();
		    String virualId = entry.getValue();
		    
		    contacts[2*index] = "skypeid";
			contacts[2*index+1] = skypeId + "," + virualId;		    
			index++;
			
			sb.append("> " + skypeId + "," + virualId);
			sb.append(System.lineSeparator());
		}		
		
		if(skypeContacts.size() > 0)
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, 
					MessageRepository.getMessage("message.skype_list", new String[] { sb.toString() }), 
					contacts);
		else
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, MessageRepository.getMessage("message.skype_list_empty"));
	}
}