package de.zorrle001.mysqlconfigapi.config;

public class MySQLOfflineConfiguration {

	public boolean stopPlugin;
	public boolean overwriteMySQLConfigs;
	public boolean createConflictCopies;

	/**
	 * #Bei Verbindungsfehler mit MySQL wird der Offline Modus gestartet
	   #Hier kann der Offline Modus konfiguriert werden
	   
	   #Soll das Plugin Stoppen bei einem Verbindungsfehler?
	   stopPlugin: false
	   
	   
	   #Wenn im Offline Modus Daten in die Configs geschrieben werden, sind sie nicht syncronisiert
	   #mit der MySQL Datenbank
	   
	   #Sollen die alten MySQL Configs überschrieben werden oder die MySQL Daten in die Localen Configs
	   #geschrieben werden
	   #
	   #   true: MySQL Configs überschrieben | false: Local Configs überschreiben
	   #
	   overwriteMySQLConfigs: true
		
	   #Sollen Konflikt-Kopien erstellt werden bei ungleichheiten zwischen Localen und MySQL Configs
	   createConflictCopies: true
	 * @param stopPlugin
	 * @param overwriteMySQLConfigs
	 * @param createConflictCopies
	 */
	public MySQLOfflineConfiguration(boolean stopPlugin, boolean overwriteMySQLConfigs, boolean createConflictCopies) {
		this.stopPlugin = stopPlugin;
		this.overwriteMySQLConfigs = overwriteMySQLConfigs;
		this.createConflictCopies = createConflictCopies;
	}
	
	public static MySQLOfflineConfiguration getDefaultConfiguration() {
		return new MySQLOfflineConfiguration(false, true, true);
	}
	
}
