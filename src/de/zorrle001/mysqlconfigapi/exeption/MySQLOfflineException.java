package de.zorrle001.mysqlconfigapi.exeption;

public class MySQLOfflineException extends RuntimeException {

	private static final long serialVersionUID = 1383827630953530224L;
	
	public MySQLOfflineException(String msg) {
		super("\n\n-> Fehlertyp: MySQL Datenbank Offline\n-> Fehlerbeschreibung: " + msg + "\n");
	}

}
