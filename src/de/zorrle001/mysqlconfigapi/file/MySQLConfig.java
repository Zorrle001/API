package de.zorrle001.mysqlconfigapi.file;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import de.zorrle001.mysqlconfigapi.MySQLConfigAPI;
import de.zorrle001.mysqlconfigapi.exeption.ConfigException;
import de.zorrle001.mysqlconfigapi.exeption.MySQLOfflineException;
import de.zorrle001.mysqlconfigapi.mechanic.DeleteConfig;
import de.zorrle001.mysqlconfigapi.mysql.FileManager;

public class MySQLConfig extends YamlConfiguration {
	
	MySQLConfigAPI api;
	
	String name;
	File file;
	String path;
	FileManager fileManager;
	
	boolean getConfig;
	boolean overwrite;
	
	public MySQLConfig(MySQLConfigAPI api, String name, boolean getConfig, boolean overwrite) {
		this.api = api;
		
		if(api.isOffline()) {
			overwrite = false;
		}
		
		this.name = name.toLowerCase();
		this.path = api.getConfigPath() + name.toLowerCase() + ".yml";
		this.fileManager = api.getFileManager();
		
		this.getConfig = getConfig;
		this.overwrite = overwrite;
		
		createLocalFile();
	}
	
	public MySQLConfig(MySQLConfigAPI api, String name, boolean override) {
		this.api = api;
		
		if(api.isOffline()) {
			overwrite = false;
		}
		
		this.name = name.toLowerCase();
		this.path = api.getConfigPath() + name.toLowerCase() + ".yml";
		this.fileManager = api.getFileManager();
		
		createOrGetLocalFile(override);
	}
	
	//PRIVATE METHODS
	private void createLocalFile() {
		file = (File) new File(path);
		if(file.exists()) {
			
			if(getConfig == false) {
				if(api.getThrowExceptionsValue()) {
					throw new ConfigException("[MySQLConfigAPI] Die Config '" + name + "' existiert bereits");
				}else {
					return;
				}
			}	
			
			if(!api.isOffline()) {
				syncWithMySQL();
			}
			
			file = (File) new File(path);
	        
			try {
				this.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}else {
			if(getConfig == true) {
				if(api.getThrowExceptionsValue()) {
					throw new ConfigException("[MySQLConfigAPI] Die Config '" + name + "' existiert nicht");
				}else {
					return;
				}
			}
			
			try {
				Files.createParentDirs(file);
				Files.touch(file);
			} catch (IOException e1) {
				if(api.getThrowExceptionsValue()) {
					throw new ConfigException("Die Config '" + name + "' konnte nicht erstellt werden");
				}else {
					return;
				}
			}
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(overwrite == true) {
				fileManager.uploadConfig(file, this);
			}
			
		}
	}
	
	private void createOrGetLocalFile(boolean override) {
		file = (File) new File(path);
		if(file.exists()) {	
			
			if(!api.isOffline()) {
				syncWithMySQL();
			}
			
			file = (File) new File(path);
	        
			try {
				this.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}else {
			
			try {
				Files.createParentDirs(file);
				Files.touch(file);
			} catch (IOException e1) {
				if(api.getThrowExceptionsValue()) {
					throw new ConfigException("Die Übergeordneten Ordner von Config '" + name + "' konnten nicht erstellt werden");
				}else {
					return;
				}
			}
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(override) {
				fileManager.uploadConfig(file, this);
			}
			
		}
	}
	
	
	//PUBLIC API METHODS
	public String getName() {
		return name;
	}
	
	public String getMySQLName() {
		return name + "database";
	}
	
	public String getLocalFolderPath() {
		String relativePath = path;
		String[] split = relativePath.split("");
		
		for(int i = split.length; i > 0; i--) {	
			if(split[i-1].equalsIgnoreCase("/")) {
				String result = "";
				for(String spliter : split) {
					result = result + spliter;
				}
				return result;
			}else {
				split[i-1] = "";
			}
		}
		return api.getConfigPath();
	}
	public String getLocalPath() {	
		return path; 
	}
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	
	public void renameConfig(String newName) throws IOException {
		try {
			fileManager.renameConfig(this, newName, false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Nicht funktionstüchtig
	/*public void renameOverwriteConfig(String newName) throws ConfigException, IOException {
		fileManager.renameConfig(this, newName, true);
	}*/
	
	
	public void clearConfig() {
		this.set("", null);
	}
	
	public void deleteConfig() {
		new DeleteConfig().delete(api, name, !api.isOffline());
	}
	
	public void deleteLocalConfig() {
		new DeleteConfig().delete(api, name, false);
	}
	
	/**
	 * @apiNote Auch in Offline Modus verfügbar
	 * @throws IOException
	 */
	public void saveConfig() throws IOException {
		this.save(file);
		if(!api.isOffline()) {
			fileManager.uploadConfig(file, this);
		}
	}
	public void saveLocal() throws IOException {
		this.save(file);
	}
	
	
	
	/*
	 * Überschreibt den lokalen Configstand mit dem aus der Datenbank
	 */
	/**
	 * @apiNote Im Offline-Modus NICHT verfügbar
	 * @throws MySQLOfflineException
	 */
	public void syncWithMySQL() {
		if(api.isOffline()) {
			throw new MySQLOfflineException("Config Syncronisation nicht möglich!");
		}
		
		fileManager.syncConfig(file, this);
		loadConfiguration(file);
	}
	
	
	/*
	 * Liest aktuellen auch ungespeicherten Config wert aus und vergleicht mit MySQL
	 * Wenn Datenbank nicht vorhanden wird Exception ausgeworfen oder immer false returned
	 */
	public boolean isSyncedWithMySQL() throws SQLException {	
		if(api.isOffline()) {
			throw new MySQLOfflineException("Config Syncronisations-Abfrage nicht möglich!");
		}
		
		return fileManager.isSynced(this);
	}
	
	
	
	public File getConfigFile() {
		return file;
	}
	public MySQLConfigAPI getAPI() {
		return api;
	}
	
}
