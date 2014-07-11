package provgw.skycall.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import provgw.skycall.model.ServiceManagementEntry;

public class ServiceManagementDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	
	protected DatabaseConnection db;
	
	public ServiceManagementDAO() {
		db = DatabaseConnection.getInstance();
	}
	
	public boolean addEntry(ServiceManagementEntry entry) {
		PreparedStatement  stmt = null;
		String strSql;
		
		try {
			strSql = "INSERT INTO svc_mgmt_tab(msisdn, skype_id, action_type, stat, channel, created) " +
					"VALUES(?, ?, ?, ?, ?, NOW())";
			
			stmt = this.db.getConnection().prepareStatement(strSql);
			
			stmt.setString(1, entry.getMsisdn());
			stmt.setString(2, entry.getSkypeId());
			stmt.setInt(3, entry.getActionType());
			stmt.setInt(4, entry.getStat());
			stmt.setString(5, entry.getChannel());
			
			if(stmt.executeUpdate() > 0)
				return true;
			
		} catch (SQLException e) {
			log.error("addEntry failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
}