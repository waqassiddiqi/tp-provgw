package provgw.skycall.handler;

import java.util.HashMap;
import java.util.ResourceBundle;

import provgw.skycall.model.ServiceManagementEntry;
import provgw.skycall.util.ResponseBuilder;

public class HelpCommandHandler extends CommandHandler {
	
	static String helpMenu = "";
	
	static {
		ResourceBundle myResources = ResourceBundle.getBundle("provgw");
		try {
			helpMenu = myResources.getString("provgw.help_menu");
		} catch (Exception e) { }
	}
	
	public HelpCommandHandler(HashMap<String, String> requestParameters) {
		super(requestParameters, new ServiceManagementEntry("", "", 7, 1, ""));				
	}
	
	@Override
	public String execute() {
		return ResponseBuilder.build(ResponseBuilder.RESULT_SUCCESS, 
				ResponseBuilder.RESULTCODE_SUCCESS, 
				helpMenu);
	}

}
