package provgw.skycall.db;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import provgw.skycall.model.Subscriber;

public class SubscriberDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	
	protected DatabaseConnection db;
	
	public SubscriberDAO() {
		db = DatabaseConnection.getInstance();
	}
	
	public Subscriber getSubscriberByMsisdn(String msisdn) {
		Subscriber sub = null;
		ResultSet rs = null;
		String strSql = "SELECT * FROM subscriber_tab WHERE msisdn = ?";
		PreparedStatement  stmt = null;
		
		try {
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, msisdn.trim());
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				sub = new Subscriber(rs.getString("msisdn"), rs.getInt("subtype"), rs.getInt("status"));
				sub.setId(rs.getInt("id"));
			}
		} catch (SQLException e) {
			log.error("getSubscriberByMsisdn failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return sub;
	}
	
	public boolean addSubscriber(Subscriber sub) {
		PreparedStatement  stmt = null;
		String strSql;
		
		try {
			
			Subscriber existingSub = getSubscriberByMsisdn(sub.getMsisdn());
			if(existingSub != null) {
				if(existingSub.getStatus() != 1) {
					strSql = "UPDATE subscriber_tab set status = 1, last_updated_date = NOW() WHERE msisdn = ?";
					
					stmt = this.db.getConnection().prepareStatement(strSql);
					stmt.setString(1, sub.getMsisdn());
					
				} else {
					return true;
				}
			} else {
				strSql = "INSERT INTO subscriber_tab(msisdn, subtype, status, subscribed_date, next_renewal_date, created_date, last_updated_date, last_successful_charging_date) "
						+ "VALUES(?, ?, ?, NOW(), NOW(), NOW(), NOW(), NOW())";
				stmt = this.db.getConnection().prepareStatement(strSql);
				
				stmt.setString(1, sub.getMsisdn());
				stmt.setInt(2, sub.getSubscriberType());
				stmt.setInt(3, sub.getStatus());
			}
			
			if(stmt.executeUpdate() > 0)
				return true;
			
		} catch (SQLException e) {
			log.error("addSubscriber failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public boolean removeSubscriber(Subscriber sub) {
		PreparedStatement stmt = null;
		
		try {
			String strSql = "UPDATE subscriber_tab set status = 0, last_updated_date = NOW() WHERE msisdn = ?";
			stmt = this.db.getConnection().prepareStatement(strSql);
			
			stmt.setString(1, sub.getMsisdn());
			
			if(stmt.executeUpdate() > 0)
				return true;
			
		} catch (SQLException e) {
			log.error("removeSubscriber failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public boolean removeSkypeContact(String msisdn, String skypeId) {
		PreparedStatement stmt = null;
		
		try {
			String strSql = "DELETE FROM skype_list_tab WHERE subscriber_id = " +
					"(SELECT id FROM subscriber_tab s WHERE s.msisdn = ?) AND " +
					"mapping_id = (SELECT mapping_id FROM mapping_tab m WHERE m.skype_id = ?);";
			
			stmt = this.db.getConnection().prepareStatement(strSql);
			
			stmt.setString(1, msisdn);
			stmt.setString(2, skypeId);
			
			if(stmt.executeUpdate() > 0)
				return true;
			
		} catch (SQLException e) {
			log.error("removeSubscriber failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public List<String> getSkypeContacts(String msisdn) {
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		String strSql = "SELECT skype_id FROM mapping_tab m " +
				"INNER JOIN skype_list_tab l ON l.mapping_id = m.mapping_id " +
				"INNER JOIN subscriber_tab s ON s.id = l.subscriber_id " +
				"WHERE s.msisdn =  ?";
		
		PreparedStatement  stmt = null;
		
		try {
			
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, msisdn.trim());
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				list.add(rs.getString(1));
			}
			
			log.debug("Skype ids fetched: " + list.size() + "[ " + Arrays.toString(list.toArray()) + " ]");
			
		} catch (SQLException e) {
			log.error("getSkypeContacts failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return list;
	}
	
	public Map<String, String> getSkypeContactsWithVirtualIds(String msisdn) {
		Map<String, String> map = new HashMap<String, String>();
		ResultSet rs = null;
		String strSql = "SELECT skype_id, virtual_id FROM mapping_tab m " +
				"INNER JOIN skype_list_tab l ON l.mapping_id = m.mapping_id " +
				"INNER JOIN subscriber_tab s ON s.id = l.subscriber_id " +
				"WHERE s.msisdn =  ?";
		
		PreparedStatement  stmt = null;
		
		try {
			
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, msisdn.trim());
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				map.put(rs.getString(1), rs.getString(2));
			}
			
			log.debug("Skype ids fetched: " + map.size() + "[ " + map.toString() + " ]");
			
		} catch (SQLException e) {
			log.error("getSkypeContacts failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return map;
	}
	
	public String getVirtualId(String skypeId, String msisdn) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.db.getConnection().prepareCall("{ call sp_getVirtualId(?, ?) }");
			stmt.setString(1, skypeId);
			stmt.setString(2, msisdn);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			log.error("getVirtualId failed: " + e.getMessage(), e);
		} finally {
			try {
				if(rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return null;
	}
	
	public String getSkypeIdByVirtualNumber(String virtualId) {
		String skypeId = null;
		ResultSet rs = null;
		String strSql = "SELECT skype_id FROM mapping_tab WHERE virtual_id =  ?";

		PreparedStatement stmt = null;
		try {
			stmt = this.db.getConnection().prepareStatement(strSql);
			stmt.setString(1, virtualId.trim());

			rs = stmt.executeQuery();
			if (rs.next()) {
				skypeId = rs.getString(1);
			}
		} catch (SQLException e) {
			this.log.error(
					"getSkypeIdByVirtualNumber failed: " + e.getMessage(), e);
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				this.log.error(
						"failed to close db resources: " + ex.getMessage(), ex);
			}
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				this.log.error(
						"failed to close db resources: " + ex.getMessage(), ex);
			}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				this.log.error(
						"failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		return skypeId;
	}
}