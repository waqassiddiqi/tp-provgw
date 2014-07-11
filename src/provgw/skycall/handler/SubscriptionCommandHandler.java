package provgw.skycall.handler;

import java.util.HashMap;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.model.Subscriber;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class SubscriptionCommandHandler extends CommandHandler {
	
	SubscriberDAO subDao;
	
	public SubscriptionCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 1, 0, ""));
		
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
		
		if(sub != null && sub.getStatus() == 1) {
			
			return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
					ResponseBuilder.RESULTCODE_MSISDN_ALREADY_REGISTERED, 
					MessageRepository.getMessage("message.sub_already_exists"));
			
		} else {
			
			if(subDao.addSubscriber(new Subscriber(msisdn, 1, 1))) {
				
				this.setResult(true);
				this.getSvcEntry().setStat(1);
				
				return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
						ResponseBuilder.RESULTCODE_SUCCESS, 
						MessageRepository.getMessage("message.sub_success"));
				
			} else {
				return ResponseBuilder.build(ResponseBuilder.RESULT_FAILED, 
						ResponseBuilder.RESULTCODE_ERROR, 
						MessageRepository.getMessage("message.server_error"));
			}
		}
	}
}