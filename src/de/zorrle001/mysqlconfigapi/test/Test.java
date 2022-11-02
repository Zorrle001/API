package de.zorrle001.mysqlconfigapi.test;

import java.io.IOException;
import java.sql.SQLException;

import de.zorrle001.mysqlconfigapi.MySQLConfigAPI;
import de.zorrle001.mysqlconfigapi.config.MySQLOfflineConfiguration;
import de.zorrle001.mysqlconfigapi.exeption.ConfigException;
import de.zorrle001.mysqlconfigapi.exeption.MySQLOfflineException;
import de.zorrle001.mysqlconfigapi.file.MySQLConfig;

public class Test {

	public static void main(String[] args) throws SQLException, IOException, ConfigException, MySQLOfflineException {
		
		/*MySQLDataConfig mysqlData = new MySQLDataConfig("/configs/mysql/", "mysql");
		
		MySQLConfigAPI api = new MySQLConfigAPI(mysqlData, "/configs/");
		
		MySQLConfig cfg = api.createOrGetConfig("Test");
		cfg.set("Test", "Servus lol xD");
		cfg.saveConfig();*/
		
		//MySQLConfigAPI api = new MySQLConfigAPI("localhost", "3306","mysqlconfigapi","root","","//configs//");
		MySQLConfigAPI api = new MySQLConfigAPI("//configs//Test");
		
		api.setOfflineConfiguration(new MySQLOfflineConfiguration(true, true, true));
		
		MySQLConfig config = api.createOrGetConfig("test");
		config.set("Test", "Holy Shit");
		config.saveConfig();
		
		MySQLConfig get = api.getConfig("test");
		System.out.println(get.getString("Test"));
		
		api.getConfig("LOL");
		
	}
	
}
