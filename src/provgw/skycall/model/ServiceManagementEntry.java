package provgw.skycall.model;

import java.util.Date;

public class ServiceManagementEntry {
	
	private int id;
	private String msisdn;
	private String skypeId;
	private int actionType;
	private int stat;
	private String channel;
	private Date created;

	public ServiceManagementEntry(String msisdn, String skypeId, int actionType, int stat, String channel) {
		this.msisdn = msisdn;
		this.skypeId = skypeId;
		this.actionType = actionType;
		this.stat = stat;
		this.channel = channel;
		this.created = new Date();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getSkypeId() {
		return skypeId;
	}

	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public int getStat() {
		return stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
