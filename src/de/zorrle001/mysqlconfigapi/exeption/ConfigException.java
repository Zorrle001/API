package de.zorrle001.mysqlconfigapi.exeption;

public class ConfigException extends RuntimeException {
	
	private static final long serialVersionUID = 4380421815593746389L;
	
	public ConfigException(String msg) {
		super("\n-> Fehler: " + msg + "\n-> Fehlerbeschreibung: ");
	}

}
