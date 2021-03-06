package provgw.skycall.handler;

import java.util.HashMap;

import provgw.skycall.db.SubscriberDAO;
import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.model.Subscriber;
import provgw.skycall.util.MessageRepository;
import provgw.skycall.util.ResponseBuilder;

public class QuerySubscriberCommandHandler extends CommandHandler {
	
	SubscriberDAO subDao;
	
	public QuerySubscriberCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 6, 0, ""));
		
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
			
			this.setResult(true);
			this.getSvcEntry().setStat(0);
			
			if(sub.getStatus() == 1) {
				return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
						ResponseBuilder.RESULTCODE_SUCCESS, 
						MessageRepository.getMessage("message.sub_status_active", 
								new String[] { sub.getSubscriptionDate().toString() }), 
						new String[] { "subscribeDate", sub.getSubscriptionDate().toString(), 
							"subStatus", String.valueOf(sub.getStatus()) } );
			} else {
				return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
						ResponseBuilder.RESULTCODE_SUCCESS, 
						MessageRepository.getMessage("message.sub_status_inactive", 
								new String[] { sub.getSubscriptionDate().toString() }), 
						new String[] { "subscribeDate", sub.getSubscriptionDate().toString(), 
							"subStatus", String.valueOf(sub.getStatus()) } );
			}

		}
	}
}