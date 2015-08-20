package net.moc.CodeBlocks.SQL;

import net.moc.CodeBlocks.CodeBlocks;

public class DatabaseServer {
	private CodeBlocks plugin;
	
	private boolean isMySQL;
	private String host;
	private String username;
	private String password;
	private String databaseName;
	private int port;
	
	private DatabaseConnection connection; public DatabaseConnection getDatabase() { return connection; }
	
	public DatabaseServer(CodeBlocks plugin) {
		this.plugin = plugin;
		
		startDatabaseConnection();
		
	}
	
	private void startDatabaseConnection() {
		//Get settings
		isMySQL = plugin.getConfiguration().getDatabaseUseMySQL();
		host = plugin.getConfiguration().getDatabaseHost();
		username = plugin.getConfiguration().getDatabaseUsername();
		password = plugin.getConfiguration().getDatabasePassword();
		databaseName = plugin.getConfiguration().getDatabaseDatabase();
		port = plugin.getConfiguration().getDatabasePort();

		//Check Settings
		if (isMySQL == true) {
			if (host == null)     { isMySQL = false; plugin.getLog().warn("MySQL is on, but host is not defined, defaulting to SQLite"); }
			if (username == null)     { isMySQL = false; plugin.getLog().warn("MySQL is on, but username is not defined, defaulting to SQLite"); }
			if (password == null)     { isMySQL = false; plugin.getLog().warn("MySQL is on, but password is not defined, defaulting to SQLite"); }
			if (databaseName == null) { isMySQL = false; plugin.getLog().warn("MySQL is on, but database is not defined, defaulting to SQLite"); }
			if (port <= 0) { plugin.getLog().warn("MySQL is on, but no port specified. Using default 3306."); port = 3306; }
			
		}

		// Create the SQL/MySQL connection
		connection = new DatabaseConnection(plugin, isMySQL, host, username, password, databaseName, port);

		// Check if the Connection was successful
		if (connection != null && connection.checkConnection()) {
			plugin.getLog().info("Database connection successful.");
			
		} else {
			connection = null;
			plugin.getLog().warn("Error using host/database:port '" + host + "/" + databaseName + ":" + port + "'... do you need to create the database on the host? 'CREATE DATABASE X;'");
			plugin.getLog().warn("Or maybe you are not running MySQL on port " + port + "?");
			
		}

	}

}
