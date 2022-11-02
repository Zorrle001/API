package de.zorrle001.mysqlconfigapi.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.zorrle001.mysqlconfigapi.MySQLConfigAPI;
import de.zorrle001.mysqlconfigapi.exeption.MySQLOfflineException;

public class MySQL {

	private String host;
	private String port;
	private String database;
	private String username;
	private String password;
	private Connection con;
	
	private boolean offlineMode = false;
		
	public void connect(MySQLConfigAPI api) throws MySQLOfflineException {
		
		if(!isConnected()) {
			
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} catch (SQLException e) {
				if(api.getOfflineConfiguration().stopPlugin) {
					throw new MySQLOfflineException("Plugin wird gestoppt, wegen OfflineConfiguration");
				}else {
					offlineMode = true;
				}
			}
		}
	}
	
	public void disconnect() {
		if(isConnected()) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		return (con == null ? false : true);
	}
	
	public void update(String qry) {
		try {
			PreparedStatement ps = con.prepareStatement(qry);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet getResult(String qry) {
		try {
			PreparedStatement ps = con.prepareStatement(qry);
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isOffline() {
		return offlineMode;
	}
	
	
	
	
	//PUBLIC API METHODES
	public void reconnect(MySQLConfigAPI api) throws SQLException, MySQLOfflineException {
		this.disconnect();
		this.connect(api);
	}
	
	public String getHost() {
		return host;
	}
	public String getPort() {
		return port;
	}
	public String getDatabase() {
		return database;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setMySQLData(String host, String port, String database, 
			String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
}
