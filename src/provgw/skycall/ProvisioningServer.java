package provgw.skycall;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import provgw.skycall.handler.ProvisioningRequestHandler;

public class ProvisioningServer {

	private Logger log = Logger.getLogger(getClass().getName());
	private int mPort = 8081;
	private int mMaxClients = 10;
	private ServerSocket mListenSocket;
	ExecutorService mRequestPool;

	public ProvisioningServer() {
		readConfig();
	}

	public void readConfig() {
		this.log.info("Initializing ProvisioningServer (ProvGW)...");

		try {
			
			ResourceBundle myResources = ResourceBundle.getBundle("provgw");
			this.mPort = Integer.parseInt(myResources.getString("provgw.port"));
			this.mMaxClients = Integer.parseInt(myResources.getString("provgw.client.max"));
			this.mRequestPool = Executors.newFixedThreadPool(this.mMaxClients);

		} catch (Exception ex) {
			log.error("Unable to initialize: " + ex.getMessage(), ex);
			System.exit(-1);
		}

		this.log.info("ProvisioningServer (ProvGW) initialized");
	}

	public void startServer() {
		try {
			this.mListenSocket = new ServerSocket(this.mPort);
			log.info("ReqTrigger TCP Server started at port: " + this.mPort);
			while (true) {
				Socket clientSocket = this.mListenSocket.accept();
				ProvisioningRequestHandler req = new ProvisioningRequestHandler(clientSocket);
				this.mRequestPool.execute(req);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			log.info("Shutting down ReqTrigger TCP Server...");
		}
	}

	public static void main(String[] args) {
		new ProvisioningServer().startServer();
	}
}