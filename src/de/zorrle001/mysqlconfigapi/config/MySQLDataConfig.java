package de.zorrle001.mysqlconfigapi.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MySQLDataConfig extends YamlConfiguration {
	
	File cfgFile;
	String path;
	String name;
	
	public MySQLDataConfig(String path, String name) {
		
		path = path.replace("//", "/");
		String[] split = path.split("");
		if(!split[split.length-1].equals("/")) {
			path = path + "/";
		}
		
		File cfgFile = new File(path + name + ".yml");
		try {
			this.load(cfgFile);
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		
		this.options().copyDefaults(true);
		//cfg.options().header("Hier kannst du deine MySQL-Daten eintragen und MySQL aktivieren oder auch deaktivieren");
		
		this.addDefault("enableMySQL", true);
		this.addDefault("host", "localhost");
		this.addDefault("port", "3306");
		this.addDefault("database", "mysqlconfigapi");
		this.addDefault("username", "root");
		this.addDefault("password", "");
		
		try {
			this.save(cfgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.cfgFile = cfgFile;
		this.path = path;
		this.name = name;
		
	}
	
	public boolean getMySQLEnabled() {
		return this.getBoolean("enableMySQL");
	}
	
	public String getHost() {
		return this.getString("host");
	}
	
	public String getPort() {
		return this.getString("port");
	}
	
	public String getDatabase() {
		return this.getString("database");
	}
	
	public String getUsername() {
		return this.getString("username");
	}
	
	public String getPassword() {
		return this.getString("password");
	}
	
	public String getName() {
		return name;
	}
	
	public String getFileName() {
		return name + ".yml";
	}
	
	public String getFolderPath() {
		return path;
	}
	public String getPath() {	
		return path + name + ".yml"; 
	}
	public String getAbsolutePath() {
		return cfgFile.getAbsolutePath();
	}
	public void saveDataConfig() throws IOException {
		this.save(cfgFile);
	}
	
}
