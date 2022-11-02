package de.zorrle001.mysqlconfigapi.mysql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.zorrle001.mysqlconfigapi.MySQLConfigAPI;
import de.zorrle001.mysqlconfigapi.exeption.ConfigException;
import de.zorrle001.mysqlconfigapi.file.MySQLConfig;

public class FileManager {
	
	MySQLConfigAPI api;
	MySQL mysql;
	
	public FileManager(MySQLConfigAPI api) {
		this.api = api;
		this.mysql = api.getMySQLConnection();
	}
	
	//MUSS IMMER VOR UPLOAD AUSGEFÜHRT WERDEN!
	public void syncConfig(File file, MySQLConfig config) {
		
		if(mysql.isConnected()) {
		
			try {
			
				if(!file.exists()) {
					file.createNewFile();
				}
				
				//String table = file.getName().replace(".yml", "") + "database";
				String table = config.getLocalPath().replace(".yml", "") + "database";
				table = table.replace(api.getConfigPath(), "");
				
				mysql.update("CREATE TABLE IF NOT EXISTS `" + table + "` (line VARCHAR(1000))");
				
				ResultSet rs = mysql.getResult("SELECT * FROM `" + table + "`");
				ArrayList<String> fileContent = new ArrayList<String>();
				
				if(rs != null) {
					
					while(rs.next()) {
						String line = rs.getString("line");
						fileContent.add(line);
					}
					
				}
				rs.close();
				
				if (file.exists()) {
					FileWriter fw1 = new FileWriter(file, false); //true für append! 
			        BufferedWriter bw1 = new BufferedWriter(fw1);
			        
			        bw1.write("");
			        
			        bw1.close();
					
			    	FileWriter fw = new FileWriter(file, false); //true für append! 
			        BufferedWriter bw = new BufferedWriter(fw);
			        
			        int count = 0;
			        for(String content : fileContent) {
			        	
			        	if(count == 0) {
			        		bw.write(content);
			        	}else {
			        		bw.write("\n" + content);
			        	}
			        	count++;
			        	
			        }
			        
			        bw.close(); 
			        fw.close();
			        
			    }
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	//VOR UPLOAD MUSS IMMER SYNC AUSGEFÜHRT WERDEN!
	public void uploadConfig(File file, MySQLConfig config) {
		
		if(mysql.isConnected()) {
		
			if(!file.exists() || !file.canRead()) {
				return;
			}
			
			String path = file.getAbsolutePath();
			path = path.replace("\\", "//");
			path = path.replace("//", "/");
			
			/*String table = file.getName().replace(".yml", "") + "database";*/
			/*String table = path.replace(api.getConfigPath(), "");
			table = table.replace(".yml", "") + "database";*/
			String table = config.getLocalPath().replace(".yml", "") + "database";
			table = table.replace(api.getConfigPath(), "");
			
			mysql.update("CREATE TABLE IF NOT EXISTS `" + table + "` (line VARCHAR(1000))");
			mysql.update("DELETE FROM `" + table +"`");
			mysql.update("LOAD DATA INFILE '" + path + "' INTO TABLE `" + table + "`");
			
		}
		
	}
	
	
	public boolean isSynced(MySQLConfig config) throws SQLException {
		
		String table = config.getLocalPath().replace(".yml", "") + "database";
		table = table.replace(api.getConfigPath(), "");
		
		//mysql.update("CREATE TABLE IF NOT EXISTS `" + table + "` (line VARCHAR(1000))");
		
		ResultSet preRS = mysql.getResult("SHOW TABLES LIKE `" + table + "`");
		if(!preRS.next()) {
			if(api.getThrowExceptionsValue()) {
				throw new ConfigException("Config besitzt keine zugehörige MySQL Tabelle");
			}else {
				return false;
			}
		}
		
		ResultSet rs = mysql.getResult("SELECT * FROM `" + table + "`");
		ArrayList<String> fileContent = new ArrayList<String>();
		
		if(rs != null) {
			
			while(rs.next()) {
				String line = rs.getString("line");
				fileContent.add(line);
			}
			
		}
		
		String result = "";
		for(String content : fileContent) {
			result = result + content + "\n";
		}
		
		if(config.saveToString().equalsIgnoreCase(result)) {
			return true;
		}
		
		return false;
		
	}
	
	public void renameConfig(MySQLConfig config, String newName, boolean overwrite) throws IOException, SQLException {
		
		File file = config.getConfigFile();
		
		File newFile = new File(api.getAbsoluteConfigPath() + "\\" + newName + ".yml");
		if((overwrite == true) || (!newFile.exists() && overwrite == false)) {
			
			String table = newName + "database";
			
			//mysql.update("CREATE TABLE IF NOT EXISTS `" + table + "` (line VARCHAR(1000))");
			
			ResultSet preRS = mysql.getResult("SHOW TABLES LIKE '" + table + "'");
			if(preRS.next()) {
				throw new ConfigException("Config mit neuem Namen existiert bereits");
			}
			
			mysql.update("RENAME TABLE '" + config.getName() + "database' TO '" + table + "'");
			
			
			file.renameTo(newFile);
			newFile.createNewFile();
			System.out.println(newFile.getAbsolutePath());
		}else {
			throw new ConfigException("Eine Config mit dem neuen Namen existiert bereits");
		}
		
		//if()
		
	}
	
	
}
