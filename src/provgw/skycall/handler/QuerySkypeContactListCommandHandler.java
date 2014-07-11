package provgw.skycall.handler;

import java.util.HashMap;
import java.util.List;

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
		
		List<String> skypeContacts = this.subDao.getSkypeContacts(msisdn.trim());
		
		this.setResult(true);
		this.getSvcEntry().setStat(1);
		
		String[] contacts = new String[skypeContacts.size() * 2];
		for(int i=0; i<skypeContacts.size(); i++) {
			contacts[2*i] = "skypeid";
			contacts[2*i+1] = skypeContacts.get(i);
		}
		
		if(skypeContacts.size() > 0)
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, "", contacts);
		else
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, "");
	}
}