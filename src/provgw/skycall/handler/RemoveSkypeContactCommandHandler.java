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
		
		String response = "";
		
		if(msisdn == null) {
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, ResponseBuilder.RESULTCODE_MSISDN_MISSING, "");
		}
		
		this.getSvcEntry().setMsisdn(msisdn);		
		this.getSvcEntry().setSkypeId(skypeId);
		
		if(subDao.getSubscriberByMsisdn(msisdn.trim()) == null) {						
			
			if(ProvisioningRequestHandler.isAutoProvisionEnabled()) {
				
				response = new SubscriptionCommandHandler(getRequestParameters()).execute();
				
			} else {
				return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
						ResponseBuilder.RESULTCODE_MSISDN_NOT_REGISTERED, 
						MessageRepository.getMessage("message.sub_not_exists"));
			}
		}
		
		if ((skypeId != null) && (skypeId.matches("\\d+"))) {
			String tempId = this.subDao.getSkypeIdByVirtualNumber(skypeId
					.trim());
			if ((tempId != null) && (tempId.trim().length() > 0)) {
				skypeId = tempId.trim();
			} else {
				response += "|" + ResponseBuilder.build("1", "480", MessageRepository
						.getMessage("message.skype_invalid_id"));
			}
		}

		this.getSvcEntry().setSkypeId(skypeId);
		
		if(subDao.removeSkypeContact(msisdn, skypeId)) {
			
			this.setResult(true);
			this.getSvcEntry().setStat(0);
			
			response += "|" + ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
					ResponseBuilder.RESULTCODE_SUCCESS, 
					MessageRepository.getMessage("message.skype_remove_success", new String[] { skypeId }));
		} else {
			response += "|" + ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_SKYPE_CONTACT_ALREADY_REMOVED, 
					MessageRepository.getMessage("message.skype_already_removed", new String[] { skypeId }));
		}
		
		return response;
	}
}