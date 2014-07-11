package provgw.skycall.handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import provgw.skycall.db.ServiceManagementDAO;


public class ProvisioningRequestHandler implements Runnable {
	
	private Logger log = Logger.getLogger(getClass().getName());
	private Socket mClientSocket;
	private boolean mClientConnected = false;
	
	public ProvisioningRequestHandler(Socket clientSocket) {
        this.mClientSocket = clientSocket;
        this.mClientConnected = true;
    }
	
	public void run() {
        log.info("New incoming request");
        
		try {
			while (this.mClientConnected) {
				if (this.mClientSocket.isOutputShutdown()) {
					this.mClientConnected = false;
				} else {
					BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.mClientSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(this.mClientSocket.getOutputStream());

					String xml = inFromClient.readLine();

					this.log.info("<< incoming request: " + xml);
					
					String response = processRequest(xml);
					
					outToClient.writeBytes(response);
					
					this.log.info(">> outgoing response: " + response);
						
					this.mClientConnected = false;
					this.mClientSocket.close();
				}
			}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		} finally {
			try {
				if(this.mClientSocket != null && this.mClientSocket.isClosed() == false)
					this.mClientSocket.close();
			} catch (IOException e) {
				log.error("Failed to free socket resource", e);
			}
		}
	}
	
	private String processRequest(String xml) throws XPathExpressionException {
		XPathFactory xpathFactory = XPathFactory.newInstance();
	    XPath xpath = xpathFactory.newXPath();
	    String msisdn = "";
	    String channel = "";
	    
		InputSource source = new InputSource(new StringReader(xml));
		
		String function = xpath.evaluate("/methodCall/function", source);
		
		CommandHandler cmdHandler = null;
		HashMap<String, String> commandParams = new HashMap<String, String>();
		
		source = new InputSource(new StringReader(xml));
		msisdn = xpath.evaluate("/methodCall/msisdn", source);
		
		commandParams.put("msisdn", msisdn);
		
		source = new InputSource(new StringReader(xml));
		channel = xpath.evaluate("/methodCall/channel", source);
		commandParams.put("channel", channel);
		
		if(function.equalsIgnoreCase("subscribeService")) {
			cmdHandler = new SubscriptionCommandHandler(commandParams);
		} else if(function.equalsIgnoreCase("unsubscribeService")) {
			cmdHandler = new CancelSubscriptionCommandHandler(commandParams);
		} else if(function.equalsIgnoreCase("querySubscriber")) {
			cmdHandler = new QuerySubscriberCommandHandler(commandParams);
		} else if(function.equalsIgnoreCase("addSkypeID")) {
			
			source = new InputSource(new StringReader(xml));
			channel = xpath.evaluate("/methodCall/skypeid", source);
			commandParams.put("skypeid", channel);
			
			cmdHandler = new AddSkypeContactCommandHandler(commandParams);
			
		} else if(function.equalsIgnoreCase("querybuddylist")) {
			cmdHandler = new QuerySkypeContactListCommandHandler(commandParams);
		} else if(function.equalsIgnoreCase("removeSkypeID")) {
			
			source = new InputSource(new StringReader(xml));
			channel = xpath.evaluate("/methodCall/skypeid", source);
			commandParams.put("skypeid", channel);
			
			cmdHandler = new RemoveSkypeContactCommandHandler(commandParams);
		}
		
		String commandResponse = "";
		
		if(cmdHandler != null) {
			commandResponse = cmdHandler.execute();
			cmdHandler.getSvcEntry().setChannel(channel);
			
			new ServiceManagementDAO().addEntry(cmdHandler.getSvcEntry());
		}
		
		return commandResponse;
	}
}