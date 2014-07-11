package provgw.skycall.handler;

import java.util.HashMap;

import provgw.skycall.model.ServiceManagementEntry;

public abstract class CommandHandler {
	HashMap<String, String> requestParameters;
	boolean result;
	ServiceManagementEntry svcEntry;
	
	public CommandHandler(HashMap<String, String> requestParameters, ServiceManagementEntry svcEntry) {
		this.requestParameters = requestParameters;
		this.result = false;
		this.svcEntry = svcEntry;
	}
	
	public abstract String execute();
	
	public HashMap<String, String> getRequestParameters() {
		return this.requestParameters;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public ServiceManagementEntry getSvcEntry() {
		return svcEntry;
	}

	public void setSvcEntry(ServiceManagementEntry svcEntry) {
		this.svcEntry = svcEntry;
	}
}
