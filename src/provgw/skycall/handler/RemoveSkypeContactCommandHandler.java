package provgw.skycall.handler;

import java.util.HashMap;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class RemoveSkypeContactCommandHandler extends CommandHandler {
	
	SubscriberDAO subDao;
	
	public RemoveSkypeContactCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 4, 0, ""));
		
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
			
			this.setResult(true);
			this.getSvcEntry().setStat(0);
			
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_MSISDN_NOT_REGISTERED, MessageRepository.getMessage("message.sub_not_exists"));
		}
		
		if(subDao.removeSkypeContact(msisdn, skypeId)) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, 
					MessageRepository.getMessage("message.skype_remove_success", new String[] { skypeId }));
		} else {
			return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, 
					MessageRepository.getMessage("message.skype_already_removed", new String[] { skypeId }));
		}
	}
}