package provgw.skycall.handler;

import java.util.HashMap;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.model.Subscriber;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class CancelSubscriptionCommandHandler extends CommandHandler {
	
	SubscriberDAO subDao;
	
	public CancelSubscriptionCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 2, 0, ""));
		
		subDao = new SubscriberDAO();
	}

	@Override
	public String execute() {
		
		String msisdn = this.requestParameters.get("msisdn"); 
		
		if(msisdn == null) {
			ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, ResponseBuilder.RESULTCODE_MSISDN_MISSING, "");
		}
		
		this.getSvcEntry().setMsisdn(msisdn);
		
		Subscriber sub = subDao.getSubscriberByMsisdn(msisdn);
		
		if(sub == null) {
			
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_MSISDN_NOT_REGISTERED, 
					MessageRepository.getMessage("message.unsub_not_exists"));
			
		} else {
			
			if(sub != null && sub.getStatus() == 0) {
				
				return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
						ResponseBuilder.RESULTCODE_MSISDN_ALREADY_REGISTERED, 
						MessageRepository.getMessage("message.already_un_sub"));
				
			} else if(subDao.removeSubscriber(sub)) {
				
				this.setResult(true);
				this.getSvcEntry().setStat(0);
				
				return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
						ResponseBuilder.RESULTCODE_SUCCESS, 
						MessageRepository.getMessage("message.unsub_success"));
				
			} else {
				return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
						ResponseBuilder.RESULTCODE_ERROR, 
						MessageRepository.getMessage("message.server_error"));
			}
			
		}
	}
}