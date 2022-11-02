package de.zorrle001.mysqlconfigapi;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import de.zorrle001.mysqlconfigapi.config.MySQLDataConfig;
import de.zorrle001.mysqlconfigapi.config.MySQLOfflineConfiguration;
import de.zorrle001.mysqlconfigapi.exeption.ConfigException;
import de.zorrle001.mysqlconfigapi.exeption.MySQLOfflineException;
import de.zorrle001.mysqlconfigapi.file.MySQLConfig;
import de.zorrle001.mysqlconfigapi.mechanic.DeleteConfig;
import de.zorrle001.mysqlconfigapi.mysql.FileManager;
import de.zorrle001.mysqlconfigapi.mysql.MySQL;

public class MySQLConfigAPI {
	
	private String configPath = "/root/";
	private MySQL mysql;
	private FileManager fileManager;
	
	private MySQLOfflineConfiguration offlineCfg = MySQLOfflineConfiguration.getDefaultConfiguration();
	
	private boolean throwExeptions = true;
	
	public MySQLConfigAPI(String host, String port, String database, 
		String username, String password, String configPath) throws SQLException {
		
		mysql = new MySQL();
		
		mysql.setMySQLData(host, port, database, username, password);
		
		mysql.connect(this);
		
		configPath = configPath.replace("//", "/");
		String[] split = configPath.split("");
		if(!split[split.length-1].equals("/")) {
			configPath = configPath + "/";
		}
		
		this.configPath = configPath;
		this.fileManager = new FileManager(this);
		
		this.offlineCfg = MySQLOfflineConfiguration.getDefaultConfiguration();
	}
	
	public MySQLConfigAPI(MySQLDataConfig data, String configPath) throws SQLException {
		
		mysql = new MySQL();
		
		mysql.setMySQLData(data.getHost(), data.getPort(), data.getDatabase(), data.getUsername(), data.getPassword());
		
		mysql.connect(this);
		
		configPath = configPath.replace("//", "/");
		String[] split = configPath.split("");
		if(!split[split.length-1].equals("/")) {
			configPath = configPath + "/";
		}
		
		this.configPath = configPath;
		this.fileManager = new FileManager(this);
	
		this.offlineCfg = MySQLOfflineConfiguration.getDefaultConfiguration();
	}
	
	public MySQLConfigAPI(String configPath) {
		
		mysql = new MySQL();
		
		configPath = configPath.replace("//", "/");
		String[] split = configPath.split("");
		if(!split[split.length-1].equals("/")) {
			configPath = configPath + "/";
		}
		
		this.configPath = configPath;
		this.fileManager = new FileManager(this);
	
		this.offlineCfg = MySQLOfflineConfiguration.getDefaultConfiguration();
		
	}
	
	public boolean isOffline() {
		return mysql.isOffline();
	}
	
	public void connect() throws MySQLOfflineException {
		mysql.connect(this);
	}
	
	public void disconnect() {
		mysql.disconnect();
	}
	
	public String getConfigPath() {
		return configPath;
	}
	
	public String getAbsoluteConfigPath() {
		return new File(getConfigPath()).getAbsolutePath();
	}
	
	public MySQL getMySQLConnection() {
		return mysql;
	}
	
	public void throwExeptions(boolean value) {
		throwExeptions = value;
	}
	
	/*
	 * createConfig(name) überschreibt die vorherigen daten in der Datenbank 
	 */
	public MySQLConfig createConfig(String name) {
		return new MySQLConfig(this, name, false, true);
	}
	
	public MySQLConfig createLocalConfig(String name) {
		return new MySQLConfig(this, name, false, false);
	}
	
	public MySQLConfig createOrGetConfig(String name) {
		return new MySQLConfig(this, name, true);
	}
	
	public MySQLConfig createOrGetLocalConfig(String name) {
		return new MySQLConfig(this, name, false);
	}
	
	public void deleteConfig(String name) {
		new DeleteConfig().delete(this, name, true);
	}
	
	public void deleteLocalConfig(String name) {
		try {
			new DeleteConfig().delete(this, name, false);
		} catch (ConfigException e) {
			e.printStackTrace();
		}
	}
	
	public MySQLConfig getConfig(String name) {
		return new MySQLConfig(this, name, true, true);
	}
	
	public boolean existsMySQLConfig(String name) {
		
		if(isOffline()) {
			throw new MySQLOfflineException("Überprüfung ob Config in MySQL Datenbank existiert nicht möglich!");
		}
		
		try {
		
			String table = name.toLowerCase() + "database";
			
			ResultSet res = mysql.getResult("SHOW TABLES LIKE '" + table + "'");
			
			if(res.next()) {
				return true;
			}else {
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean existsLocalConfig(String name) {
		File file = new File(getConfigPath() + name + ".yml");
		if(file.exists()) {
			return true;
		}
		return false;
	}
	
	
	
	
	
	public void deleteAllConfigs() {
		for(MySQLConfig config : getAllConfigs()) {
			new DeleteConfig().delete(this, config.getName(), true);
		}
	}
	
	public void deleteAllLocalConfigs() {
		for(MySQLConfig config : getAllConfigs()) {
			new DeleteConfig().delete(this, config.getName(), false);
		}
	}
	
	public ArrayList<MySQLConfig> getAllConfigs() {
		ArrayList<File> allFiles = getFiles(getAbsoluteConfigPath());
		ArrayList<MySQLConfig> allConfigs = new ArrayList<MySQLConfig>();
		
		for(File file : allFiles) {
			String path = file.getPath().replace(".yml", "");
			path = path.replace(getAbsoluteConfigPath(), "");
			path = path.replace("\\", "/");
			allConfigs.add(new MySQLConfig(this, path, true, true));	
		}	
		
		return allConfigs;
	}
	
	private ArrayList<File> getFiles(String directoryName) {
        File directory = new File(directoryName);

        ArrayList<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                //System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
            	resultList.remove(file);
                resultList.addAll(getFiles(file.getAbsolutePath()));
            }
        }
        return resultList;
    } 
	
	
	public void setOfflineConfiguration(MySQLOfflineConfiguration offlineCfg) {
		this.offlineCfg = offlineCfg;
	}
	
	public MySQLOfflineConfiguration getOfflineConfiguration() {
		return offlineCfg;
	}
	
	
	
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public boolean getThrowExceptionsValue() {
		return throwExeptions;
	}
	
}
