package de.zorrle001.mysqlconfigapi.mechanic;

import java.io.File;

import de.zorrle001.mysqlconfigapi.MySQLConfigAPI;
import de.zorrle001.mysqlconfigapi.exeption.ConfigException;

public class DeleteConfig {

	public void delete(MySQLConfigAPI api, String name, boolean deleteMySQL) {
		
		name = name.toLowerCase();
		
		if(api.existsLocalConfig(name)) {
		
			String path = api.getConfigPath() + name + ".yml";
			File file = new File(path);
			
			file.delete();
			
			if(deleteMySQL == true) {
				api.getMySQLConnection().update("DROP TABLE IF EXISTS '" + name + "database'");
			}
			
		}else {
			if(api.getThrowExceptionsValue()) {
				throw new ConfigException("[MySQLConfigAPI] Die Config '" + name + "' existiert nicht");
			}
		}
		
	}
	
}
