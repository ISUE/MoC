package net.moc.CodeBlocks.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.SQL.DatabaseConnection.DatabaseType;
import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.RobotHistory;
import net.moc.CodeBlocks.workspace.Robotnik.Direction;
import net.moc.CodeBlocks.workspace.RobotnikController;
import net.moc.CodeBlocks.workspace.parts.Directive;

public class CodeBlocksSQL {
	//===============================================
	private CodeBlocks plugin;
	private DatabaseServer db;
	private DatabaseConnection databaseConnection;
	
	public CodeBlocksSQL(CodeBlocks plugin) {
		this.plugin = plugin;
		this.db = new DatabaseServer(plugin);
		this.databaseConnection = db.getDatabase();
		
		checkTables();
		
	}
	//===============================================
	
	//########################################################################################################################################
	private void checkTables() {
		if (databaseConnection.getType() == DatabaseType.MySQL) {
			//cb_functions
			this.databaseConnection.ensureTable("cb_functions", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null, owner_id int(10) not null, description text not null, data text not null");
			
			//cb_robotniks
			this.databaseConnection.ensureTable("cb_robotniks", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null, owner_id int(10) not null, function varchar(50) not null, speed int(10) not null, x int(10) not null, y int(10) not null, z int(10) not null, d varchar(50) not null, world varchar(50) not null, levels text not null, inventory text not null");
			
			//cb_achievements
			this.databaseConnection.ensureTable("cb_robot_history", "id int(10) not null auto_increment, PRIMARY KEY (id), type varchar(50) not null, owner_id int(10) not null, value text not null");
			
			//wp_users
			this.databaseConnection.ensureTable("wp_users", "id bigint(20) not null auto_increment, PRIMARY KEY (id), user_login varchar(60) not null, user_status int(11) not null DEFAULT 0");
			
		} else {
			//SQLLite
			//cb_functions
			this.databaseConnection.ensureTable("cb_functions", "id integer PRIMARY KEY, name varchar(50), owner_id integer, description text, data text");
			
			//cb_robotniks
			this.databaseConnection.ensureTable("cb_robotniks", "id integer PRIMARY KEY, name varchar(50), owner_id integer, function varchar(50), speed integer, x integer, y integer, z integer, d varchar(50), world varchar(50), levels text, inventory text");
			
			//cb_achievements
			this.databaseConnection.ensureTable("cb_robot_history", "id integer PRIMARY KEY, type varchar(50), owner_id integer, value text");
			
			//wp_users
			this.databaseConnection.ensureTable("wp_users", "id PRIMARY KEY, user_login varchar(60), user_status integer");
			
		}
	
	}
	//########################################################################################################################################
	public ArrayList<RobotnikController> getRobotniks(String playerName) {
		ArrayList<RobotnikController> robotniks = new ArrayList<RobotnikController>();
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT * FROM cb_robotniks WHERE owner_id='" + getPlayerId(playerName) + "'");
		
		try {
			if (rs.next()) {
				
				do {
					Location location = new Location(plugin.getServer().getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
					RobotnikController rc = new RobotnikController(plugin, playerName, location, Direction.valueOf(rs.getString("d")), rs.getString("levels"));
					
					rc.setFunctionName(rs.getString("function"));
					rc.setSpeed(rs.getInt("speed"));
					rc.setId(rs.getInt("id"));
					rc.setName(rs.getString("name"));
					rc.getRobotnik().setInventory(rs.getString("inventory"));
					
					robotniks.add(rc);
					
				} while (rs.next());
				
			}
			
		} catch (SQLException e) {}
		
		return robotniks;
		
	}
	
	//########################################################################################################################################
	public void removeRobotnik(int id) { this.databaseConnection.querySafeInsertUpdateDelete("DELETE FROM cb_robotniks WHERE id=" + id); }

	public void saveRobotnik(String playerName, RobotnikController robotnik) {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT id FROM cb_robotniks WHERE id=" + robotnik.getId());
		
		String directives = ";";
		for (Directive d : robotnik.getRobotnik().getDirectivesEnabled()) directives += d.getName() + ":";

		try {
			if (rs.next()) {
				//UPDATE
				PreparedStatement statement = this.databaseConnection.prepareStatement("UPDATE cb_robotniks SET name=?, function=?, speed=?, x=?, y=?, z=?, d=?, world=?, levels=?, inventory=? WHERE id=?");

				statement.setString(1, robotnik.getName());
				statement.setString(2, robotnik.getFunctionName());
				statement.setInt(3, robotnik.getSpeed());
				statement.setInt(4, robotnik.getRobotnik().getLocation().getBlockX());
				statement.setInt(5, robotnik.getRobotnik().getLocation().getBlockY());
				statement.setInt(6, robotnik.getRobotnik().getLocation().getBlockZ());
				statement.setString(7, robotnik.getRobotnik().getDirection().toString());
				statement.setString(8, robotnik.getRobotnik().getLocation().getWorld().getName());
				statement.setString(9, robotnik.getRobotnik().getStats().getPowerCurrent() + ";" + robotnik.getRobotnik().getStats().getHealthCurrent() + ";" + robotnik.getRobotnik().getStats().getArmorCurrent() + directives);
				statement.setString(10, robotnik.getRobotnik().getInventoryHash());
				statement.setInt(11, rs.getInt(1));
				
				this.databaseConnection.queryInsertUpdateDelete(statement);

			} else {
				//INSERT
				rs = databaseConnection.querySafeInsertUpdateDeleteAndKeys("INSERT INTO cb_robotniks (name, owner_id, function, speed, x, y, z, d, world, levels, inventory)" +
						" VALUES (" +
						"'" + robotnik.getName() + "'," +
						this.getPlayerId(playerName) + "," +
						"'" + robotnik.getFunctionName() + "'," +
						robotnik.getSpeed() + "," +
						robotnik.getRobotnik().getLocation().getBlockX() + "," +
						robotnik.getRobotnik().getLocation().getBlockY() + "," +
						robotnik.getRobotnik().getLocation().getBlockZ() + "," +
						"'" + robotnik.getRobotnik().getDirection().toString() + "'," +
						"'" + robotnik.getRobotnik().getLocation().getWorld().getName() + "'," +
						"'" + robotnik.getRobotnik().getStats().getPowerCurrent() + ";" + robotnik.getRobotnik().getStats().getHealthCurrent() + ";" + robotnik.getRobotnik().getStats().getArmorCurrent() + directives + "'," +
						"'" + robotnik.getRobotnik().getInventoryHash() + "')");
				
				rs.next();
				
				robotnik.setId(rs.getInt(1));

			}

		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	//########################################################################################################################################
	public ArrayList<Function> getFunctions() {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT * FROM cb_functions");
		ArrayList<Function> functions = new ArrayList<Function>();
		
		try {
			if (rs.next()) {
				
				do {
					Function function = Function.toFunction(getPlayerName(rs.getInt("owner_id")), rs.getString("name"), rs.getString("data"), rs.getString("description"), rs.getInt("id"), plugin);
					
					functions.add(function);
					
				} while (rs.next());
				
			}
			
		} catch (SQLException e) {}
		
		return functions;
		
	}
	
	public ArrayList<Function> getFunctions(String playerName) {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT * FROM cb_functions WHERE owner_id='" + getPlayerId(playerName) + "'");
		ArrayList<Function> functions = new ArrayList<Function>();
		
		try {
			if (rs.next()) {
				
				do {
					Function function = Function.toFunction(playerName, rs.getString("name"), rs.getString("data"), rs.getString("description"), rs.getInt("id"), plugin);
					
					functions.add(function);
					
				} while (rs.next());
				
			}
			
		} catch (SQLException e) {}
		
		return functions;
		
	}
	
	//########################################################################################################################################
	public void removeFunction(int id) { this.databaseConnection.querySafeInsertUpdateDelete("DELETE FROM cb_functions WHERE id=" + id); }
	
	public void saveFunction(Player player, Function function) {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT * FROM cb_functions WHERE id=" + function.getId());
		
		try {
			if (rs.next()) {
				//UPDATE
				PreparedStatement statement = this.databaseConnection.prepareStatement("UPDATE cb_functions SET name=?, data=?, description=? WHERE id=?");
				
				statement.setString(1, function.getName());
				statement.setString(2, function.toText());
				statement.setString(3, function.getDescription());
				statement.setInt(4, rs.getInt(1));
				
				this.databaseConnection.queryInsertUpdateDelete(statement);
				
			} else {
				//INSERT
				PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO cb_functions (name, owner_id, description, data) VALUES (?,?,?,?)");
				
				statement.setString(1, function.getName());
				statement.setInt(2, this.getPlayerId(player.getName()));
				statement.setString(3, function.getDescription());
				statement.setString(4, function.toText());
				
				databaseConnection.queryInsertUpdateDelete(statement);
				
					rs = statement.getGeneratedKeys();
					rs.next();
					function.setId(rs.getInt(1));
				
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	//########################################################################################################################################
	public int getPlayerId(String playerName) {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT id FROM wp_users WHERE user_login='" + playerName + "'");
		try {
			while (rs.next()) { return rs.getInt(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		//No match found write player to database
		rs = databaseConnection.querySafeInsertUpdateDeleteAndKeys("INSERT INTO wp_users (user_login, user_status) VALUES ('" + playerName + "', -1)");
		
		try {
			rs.next();
			return rs.getInt(1);
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
		
	}
	
	public String getPlayerName(int playerId) {
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT user_login FROM wp_users WHERE id=" + playerId);
		try {
			while (rs.next()) { return rs.getString(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		return "";
		
	}
	
	//########################################################################################################################################
	public RobotHistory getRobotHistory(String playerName) {
		RobotHistory history = new RobotHistory();
		ResultSet rs = this.databaseConnection.querySafeSelect("SELECT * FROM cb_robot_history WHERE owner_id='" + getPlayerId(playerName) + "'");
		
		try {
			if (rs.next()) {
				do {
					history.set(rs.getString("type"), rs.getString("value"));
					
				} while (rs.next());
				
			}
			
		} catch (SQLException e) {}
		
		return history;
		
	}
	
	public void saveRobotHistory(String playerName) {
		HashMap<String, String> history = plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().get();
		
		for (String type : history.keySet()) {
			ResultSet rs = databaseConnection.querySafeSelect("SELECT * FROM cb_robot_history WHERE owner_id='" + getPlayerId(playerName) + "' AND type='" + type + "'");
			
			try {
				if (rs.next()) {
					//UPDATE
					PreparedStatement statement = this.databaseConnection.prepareStatement("UPDATE cb_robot_history SET value=? WHERE id=?");
					
					statement.setString(1, history.get(type));
					statement.setInt(2, rs.getInt("id"));
					
					this.databaseConnection.queryInsertUpdateDelete(statement);
					
				} else {
					//INSERT
					PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO cb_robot_history (owner_id, type, value) VALUES (?,?,?)");
					
					statement.setInt(1, getPlayerId(playerName));
					statement.setString(2, type);
					statement.setString(3, history.get(type));
					
					this.databaseConnection.queryInsertUpdateDelete(statement);
					
				}
				
			} catch (SQLException e) { e.printStackTrace(); }
			
		}
		
	}
	
}
