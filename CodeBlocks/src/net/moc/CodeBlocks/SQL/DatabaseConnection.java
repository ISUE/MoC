package net.moc.CodeBlocks.SQL;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import net.moc.CodeBlocks.CodeBlocks;

public class DatabaseConnection {
	public enum DatabaseType { MySQL, SQLite, Unknown };
	protected DatabaseType type = DatabaseType.Unknown; public DatabaseType getType() { return type; }
	
	private CodeBlocks plugin;
	private String host;
	private String username;
	private String password;
	private String databaseName;
	private int port;
	
	private Connection connection = null;


//	public String getDBSpecific_INSERT_ORIGNORE() { 
//		String retval = "";
//
//		switch(this.thisdbtype)
//		{
//		case MySQL: retval = "IGNORE"; break;
//		case SQLite: retval = "OR IGNORE"; break;
//		default:
//			System.err.println("ERROR: Unknown how to give INSERT_ORIGNORE for Database "+this.thisdbtype+"\n"+
//					Thread.currentThread().getStackTrace().toString());
//		}
//		return retval;
//	}

	public DatabaseConnection(CodeBlocks plugin, boolean isMySQL, String host, String username, String password, String databaseName, int port) {
		this.plugin = plugin;
		
		if (isMySQL) type = DatabaseType.MySQL;
		else type = DatabaseType.SQLite;
		
		this.host = host;
		this.username = username;
		this.password = password;
		this.databaseName = databaseName;
		this.port = port;
		
	}

	//======================================================================================================
	public Boolean checkConnection() {
		Boolean retval = false;

		if (connection == null) { retval = openConnection(); }
		else {
			try { if (connection.isClosed()) { retval = openConnection(); return retval; } }
			catch (SQLException e) { e.printStackTrace(); return retval;}
			
			retval = true;
			
		}

		return retval;
		
	}
	//======================================================================================================

	//======================================================================================================
	private boolean openConnection() {
		if (type == DatabaseType.MySQL) {
			Boolean retval = false;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				plugin.getLog().info("OpenConnection: jdbc:mysql://" + host + ":" + port + "/" + databaseName + ":" + username);
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, username, password);
				if ( connection != null ) retval = true;
				
		    } catch (ClassNotFoundException e) { e.printStackTrace(); } catch (SQLException e) { e.printStackTrace(); }
			
			return retval;
			
		} else if (type == DatabaseType.SQLite) {
			if (databaseName.contains("/") || databaseName.contains("\\") || databaseName.endsWith(".db")) {
				plugin.getLog().warn("The database name can not contain: /, \\, or .db");
				connection = null;
				return false;
				
			}
			
			Boolean retval= false;
			
			try {
				Class.forName("org.sqlite.JDBC");
				File db = new File(plugin.getConfiguration().getPluginPath() + File.separator + databaseName);
				plugin.getLog().info("OpenConnection: jdbc:sqlite://" + db.getAbsolutePath()+".db");
				connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath()+".db");
				retval = true;
			
			} catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
			
			return retval;
			
		}
		
		return false;
		
	}
	//======================================================================================================
	
	
	//======================================================================================================
	public Boolean closeConnection() {
		Boolean retval = false;
		
		try { if (connection != null) { connection.close(); retval = true; } } catch (Exception e) { plugin.getLog().warn("Failed to close database connection! " + e.getMessage()); }

		return retval;
		
	}
	//======================================================================================================


	//======================================================================================================
	public Connection getConnection() {
		if (connection == null) { openConnection(); }
		else try { if (connection.isClosed()) openConnection(); } catch (SQLException e) { e.printStackTrace(); }
		
		return connection;
		
	}
	//======================================================================================================

	
	
	//======================================================================================================
	public int queryInsertUpdateDelete(PreparedStatement statement) { try { return statement.executeUpdate(); } catch (SQLException e) { e.printStackTrace(); } return -1; }
	//======================================================================================================
	

	
	//======================================================================================================
	private class InternalQueryReturn {
		public InternalQueryReturn() {}
		public int numchanges = -1;
		public ResultSet possibleGeneratedKeys = null;
	}
	
	public int querySafeInsertUpdateDelete(String query) { InternalQueryReturn iqr = querySafeInsertUpdateDelete(query, false); return iqr.numchanges; }
	public ResultSet querySafeInsertUpdateDeleteAndKeys(String query) { InternalQueryReturn iqr = querySafeInsertUpdateDelete(query, true); return iqr.possibleGeneratedKeys; }
	private InternalQueryReturn querySafeInsertUpdateDelete(String query, boolean generateKeys) {
	    InternalQueryReturn retval = new InternalQueryReturn();	
		
		try {
			Statement statement = connection.createStatement();
			
			if (generateKeys == true) {
				if (type == DatabaseType.MySQL) retval.numchanges = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
				else if (type == DatabaseType.SQLite) retval.numchanges = statement.executeUpdate(query);
				
				retval.possibleGeneratedKeys = statement.getGeneratedKeys();
				
			} else { retval.numchanges = statement.executeUpdate(query); }
			
		} catch (SQLException e) { plugin.getLog().warn("ERROR: error with query '" + query + "'"); e.printStackTrace(); }
		
		return retval;
		
	}
	
	public ResultSet querySafeSelect(String query) { 
		ResultSet retval = null;
		
		try {
			Statement statement = connection.createStatement();
			retval = statement.executeQuery(query);
			return retval;
			
		} catch (SQLException e) { plugin.getLog().warn(" bad sqlSafeQuery '" + query + "'"); e.printStackTrace(); }
		
		return null;
		
	}
	//======================================================================================================

	
	
	//======================================================================================================
	public PreparedStatement prepareStatement(String _query)        { return this.prepareStatement(_query, false); }
	public PreparedStatement prepareStatementAndKeys(String _query) { return this.prepareStatement(_query, true); }
	private PreparedStatement prepareStatement(String _query, boolean _generatedKeys) {
		Connection connection = getConnection();
		PreparedStatement retval = null;
		try {
			if ( _generatedKeys == true ) {
				if (type == DatabaseType.MySQL) retval = connection.prepareStatement(_query,Statement.RETURN_GENERATED_KEYS);
				else if (type == DatabaseType.SQLite) retval = connection.prepareStatement(_query);
				
			} else { retval = connection.prepareStatement(_query); }
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return retval;
		
	}
	//======================================================================================================
	

	
	//======================================================================================================
	public Boolean checkTable(String _table) {
		Boolean retval = false;
		try {
			Connection connection = getConnection();

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM '"+_table+"';");
			if (result != null) retval = true;
			
		} catch (SQLException e) { if (e.getMessage().contains("exist")) { retval = false; } else { e.printStackTrace(); } }
		
		return retval;
		
	}
	//======================================================================================================

	
	
	//======================================================================================================
	public Boolean wipeTable(String _table) {
		Boolean retval = false;
		
		try {
			if (!this.checkTable(_table)) {
				plugin.getLog().warn("Error at Wipe Table: table, " + _table + ", does not exist");
				retval = false;
				
			} else {
				Connection connection = getConnection();
				PreparedStatement pstatement = connection.prepareStatement("DELETE FROM ?;");
				pstatement.setString(1, _table);
				pstatement.executeUpdate();
				retval = true;
				
			}

		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet")) plugin.getLog().warn("Error at SQL WIPE TABLE Query: " + ex);
			retval = false;
			
		}

		return retval;
		
	}
	//======================================================================================================


	
	//======================================================================================================
	public Boolean ensureTableFull(DatabaseTable _tbl) {
		//		get the table description and go down one-by-one ... if errors out b/c does not exist, then 
		//		call the normal ensureTable method
		return false;
		
	}
	//======================================================================================================

	
	
	//======================================================================================================
	// --------------------------------------------------------------------- 
	//  Created this since SQLite has issues with Date formats.
	// --------------------------------------------------------------------- 
	public Timestamp getTimestamp(ResultSet _rs, int _col) {
		Timestamp retval = null;
		String val = null;
		
		try {
			val = _rs.getString(_col);
			retval = this._getTimestamp(val);
		} catch (SQLException e) { plugin.getLog().warn(" error getting timestamp from '"+val+"' in getTimestamp"); e.printStackTrace(); }
		
		return retval;
		
	}
	
	public Timestamp getTimestamp(ResultSet _rs, String _columnname) {
		Timestamp retval = null;
		String val = null;
		
		try {
			val = _rs.getString(_columnname);
			retval = this._getTimestamp(val);
		} catch (SQLException e) { plugin.getLog().warn(" error getting timestamp from '"+_columnname+"' in getTimestamp"); e.printStackTrace(); }
		
		return retval;
		
	}

	protected Timestamp _getTimestamp(String _val) {
		Timestamp retval = null;
		Date d;

		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try { d = df.parse(_val); retval = new Timestamp(d.getTime()); } catch (ParseException e) { e.printStackTrace(); }
		
		return retval;
		
	}
	//======================================================================================================


	
	//======================================================================================================
	// --------------------------------------------------------------------- 
	// --------------------------------------------------------------------- 
	// This is a simple method to merge data together. So, you can store your
	//   database columns as an array for your own uses but send here to
	//   make a database initialization string.
	//
	//  EXAMPLE:
	//   static enum TCOLS { col1, col2 };  // using enums helps keep all the string usage consistent as there is no compile time checks otherwise
	//   static String TABLENAME = "MyTable";
	///  static String TABLEKEYS = TCOLS.col1+","+TCOLS.col2;
	//   static String[][] TABLECOLS= {
	//       { TCOLS.col1.toString, "integer DEFAULT 0" },
	//       { TCOLS.col2.toString, "text DEFAULT ''" } };
	//   ...
	//   String tableinitstring = DBConnector.createTableString(TABLENAME,TABLECOLS,TABLEKEYS);
	//   DBConnector dbc = MOCDBLib.getConnector().sqlSafeQuery(tableinitstring);
	// --------------------------------------------------------------------- 
	public String createTableString(String _tablename, String[][] _cols, String _keys ) { return _createTableString(_tablename, _cols, _keys, "CREATE TABLE `"); }
	public String createTableIfNotExistsString(String _tablename, String[][] _cols, String _keys ) { return _createTableString(_tablename, _cols, _keys, "CREATE TABLE IF NOT EXISTS `"); }
	
	protected String _createTableString(String _tablename, String[][] _cols, String _keys, String _ct) {
		String retval = "";
		
		for(int i=0;i<_cols.length;i++) {
			if ( retval.equalsIgnoreCase("") ) retval += _ct +_tablename+"` ( ";
			else retval += ", ";
			
			retval+= _cols[i][0]+" "+_cols[i][1];
			
		}
		
		// add in the primary keys
		if ( _keys != null ) retval += ", PRIMARY KEY ( "+_keys+" ) ";
		
		retval+= " );";
		
		return retval;
		
	}

	// Check if the table exists in the database. if not, create it
	public boolean ensureTable(String _tbl, String _tblcolumns) {
		Boolean retval = false;
		
		String s = "CREATE TABLE IF NOT EXISTS " + _tbl + " ( " + _tblcolumns + ");";
		
		if ( querySafeInsertUpdateDelete(s, false).numchanges == 0 ) retval = true;
		else plugin.getLog().warn("Error ensuring table " + _tbl + " exists with database '" + databaseName + "'... do you need to create the database?");

		return retval;
		
	}
	
	public Boolean checkForTable(String tableName) {
		Boolean retval = false;
		
		try {
			Connection connection = getConnection();
			PreparedStatement pstatement = connection.prepareStatement("SELECT count(*) FROM information_schema.tables WHERE TABLE_SCHEMA=? and table_name=?;");
			pstatement.setString(1, databaseName);
			pstatement.setString(2, tableName);
			ResultSet result = pstatement.executeQuery();
			if (result.next() == true) {
				if ( result.getInt(1) > 0 )
					retval = true;
				
			}
			
		} catch (SQLException e) { if (e.getMessage().contains("exist")) { retval = false; } else { e.printStackTrace(); } }
		
		return retval;
		
	}
	
	
	public Boolean checkForColumnInTable(String columnName, String tableName) {
		Boolean retval = false;
		
		try {
			Connection connection = getConnection();
			String sql = "SELECT count(*) FROM information_schema.columns WHERE table_schema=? AND table_name=? AND column_name=?;";
			PreparedStatement pstatement = connection.prepareStatement(sql);
			
			pstatement.setString(1, databaseName);
			pstatement.setString(2, tableName);
			pstatement.setString(3, columnName);
			
			ResultSet result = pstatement.executeQuery();
			
			if (result.next() == true) { if ( result.getInt(1) > 0 ) { retval = true; } }
			
		} catch (SQLException e) { if (e.getMessage().contains("exist")) { retval = false; } else { e.printStackTrace(); } }
		
		return retval;
		
	}
	
	
	public Boolean createColumnInTable(String _colname, String _coldesc, String _table) {
		Boolean retval = false;
		//TODO
		//String sql = "ALTER TABLE "+_table+" ADD COLUMN "+_colname+" "+_coldesc+";";
		//int result = this.updateSafeQuery(sql);
		//if ( result > 0 ) retval = true;
		
		return retval;
		
	}


}
