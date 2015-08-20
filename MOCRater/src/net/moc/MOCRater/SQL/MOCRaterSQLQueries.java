package net.moc.MOCRater.SQL;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.moc.MOCRater.MOCRater;

import org.bukkit.Location;
import org.getspout.spoutapi.player.SpoutPlayer;

import moc.MOCDBLib.DBConnector;

public class MOCRaterSQLQueries {
	//===============================================
	private MOCRater plugin;
	private DBConnector databaseConnection;
	
	private ArrayList<MOCPattern> patterns = new ArrayList<MOCPattern>();
	private long lastPatternsUpdate;
	private ArrayList<MOCComment> comments = new ArrayList<MOCComment>();
	private long lastCommentsUpdate;
	private ArrayList<MOCLike> likes = new ArrayList<MOCLike>();
	private long lastLikesUpdate;
	private HashMap<Integer, AtomicInteger> likesCounts = new HashMap<Integer, AtomicInteger>();
	private HashMap<Integer, AtomicInteger> dislikesCounts = new HashMap<Integer, AtomicInteger>();
	
	public MOCRaterSQLQueries(MOCRater plugin, DBConnector databaseConnection) {
		this.plugin = plugin;
		this.databaseConnection = databaseConnection;
		
		this.checkTables();
		
		this.updateComments();
		this.lastCommentsUpdate = System.currentTimeMillis();
		this.updatePatterns();
		this.lastPatternsUpdate = System.currentTimeMillis();
		this.updateLikes();
		this.lastLikesUpdate = System.currentTimeMillis();
		
	}
	//===============================================
	

	private void checkTables() {
		//rating_comments
		this.databaseConnection.ensureTable("rating_comments", "id int(10) not null auto_increment, PRIMARY KEY (id), pattern_id int(10) not null, title varchar(255) not null, rating_t int(4) not null, " +
				"score tinyint(4) not null, location_id int(4) not null, comment text not null, createon timestamp not null DEFAULT CURRENT_TIMESTAMP, player_id int(10) not null, screen_src  varchar(255) not null, is_on tinyint(1) not null default 1");
		
		//patterns
		this.databaseConnection.ensureTable("patterns", "id int(10) not null auto_increment, PRIMARY KEY (id), player_id int(11) not null, name varchar(255) not null, description text not null, " +
				"screen_src varchar(255) not null, createdon timestamp not null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP, context varchar(255) not null, problem varchar(255) not null, " +
				"solution text not null");

		//related_patterns
		this.databaseConnection.ensureTable("related_patterns", "id int(11) not null auto_increment, PRIMARY KEY (id), pattern_id int(11) not null, related_pattern_id int(11) not null");

		//comment_types
		this.databaseConnection.ensureTable("comment_types", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null");

		//locations
		this.databaseConnection.ensureTable("locations", "id int(10) not null auto_increment, PRIMARY KEY (id), X float not null, Y float not null, Z float not null, H float not null, P float not null, world varchar(50) not null");
		
		//wp_users
		this.databaseConnection.ensureTable("wp_users", "id bigint(20) not null auto_increment, PRIMARY KEY (id), user_login varchar(60) not null, user_status int(11) not null DEFAULT 0");

		//likes
		this.databaseConnection.ensureTable("likes", "id int(10) not null auto_increment, PRIMARY KEY (id), player_id int(11) not null, comment_id int(11) not null, choice enum('likes', 'dislikes') not null");

	}
	//########################################################################################################################################
	public int saveComment(String patternName, String title, String ratingTypeName, int score, Location location, String comment, SpoutPlayer player, String screenshot, int is_on) {
		this.plugin.getGui().getScreenShots().put(this.plugin.getGui().getLatestScreenShots().get(player), true);
		
		comment.replaceAll(System.getProperty("line.separator"), " ");
		
		PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO `rating_comments` (`pattern_id`, `title`, `rating_t`, `score`, `location_id`, `comment`, `player_id`, `screen_src`, `is_on`)" +
																									" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		try {
			statement.setInt(1, this.getPatternId(patternName));
			statement.setString(2, title);
			statement.setInt(3, this.getRatingTypeId(ratingTypeName));
			statement.setInt(4, score);
			statement.setInt(5, this.getLocationId(location));
			statement.setString(6, comment);
			statement.setInt(7, this.getPlayerId(player.getName()));
			statement.setString(8, player.getName() + File.separator + screenshot);
			statement.setInt(9, is_on);
			this.databaseConnection.insertQuery(statement);
			
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			
			this.comments.add(new MOCComment(rs.getInt(1), this.getPatternId(patternName), patternName, title, this.getRatingTypeId(ratingTypeName),
					ratingTypeName, score, this.getLocationId(location), location, comment, null, this.getPlayerId(player.getName()), player.getName(), player.getName() + File.separator + screenshot, is_on));
			
			Collections.sort(this.comments);
			
			return rs.getInt(1);
			
		} catch (SQLException e) { e.printStackTrace(); }
		return -1;
		
	}
	//########################################################################################################################################
	public boolean savePattern(int editPatternId, String name, String screenshot, String context, String problem, String solution, ArrayList<String> relatedPatterns, SpoutPlayer player) {
		this.plugin.getGui().getScreenShots().put(this.plugin.getGui().getLatestScreenShots().get(player), true);
		
		context.replaceAll(System.getProperty("line.separator"), " ");
		problem.replaceAll(System.getProperty("line.separator"), " ");
		solution.replaceAll(System.getProperty("line.separator"), " ");
		
		if (editPatternId == -1) {
			PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO `patterns` (`player_id`, `name`, `description`, `screen_src`, `context`, `problem`, `solution`)"+ 
																							"VALUES(?, ?, '', ?, ?, ?, ?)");
			
			try {
				statement.setInt(1, this.getPlayerId(player.getName()));
				statement.setString(2, name);
				statement.setString(3, player.getName() + File.separator + screenshot);
				statement.setString(4, context);
				statement.setString(5, problem);
				statement.setString(6, solution);
				
				this.databaseConnection.insertQuery(statement);
				
				ResultSet rs = statement.getGeneratedKeys();
				rs.next();
				int patternId = rs.getInt(1);
				
				for (String relatedPattern : relatedPatterns) {
					statement = this.databaseConnection.prepareStatement("INSERT INTO `related_patterns` (`pattern_id`, `related_pattern_id`)" +
																		" VALUES (?, ?)");
					statement.setInt(1, patternId);
					statement.setInt(2, this.getPatternId(relatedPattern));
					
					this.databaseConnection.insertQuery(statement);

				}
				
				this.patterns.add(new MOCPattern(patternId, this.getPlayerId(player.getName()), player.getName(), name, player.getName() + File.separator + screenshot, null, context, problem, solution));
				Collections.sort(this.patterns);
				
				return true;
				
			} catch (SQLException e1) { e1.printStackTrace(); }
			
			return false;
			
		} else {
			
			PreparedStatement statement = this.databaseConnection.prepareStatement("UPDATE `patterns` SET " +
					"`player_id` =?, `name`=?, `description`='', `context`=?, `problem`=?, `solution`=? WHERE `id`=?");
			
			try {
				statement.setInt(1, this.getPlayerId(player.getName()));
				statement.setString(2, name);
				statement.setString(3, context);
				statement.setString(4, problem);
				statement.setString(5, solution);
				statement.setInt(6, editPatternId);
				
				this.databaseConnection.updateQuery(statement);
				
				statement = this.databaseConnection.prepareStatement("DELETE FROM `related_patterns` WHERE `pattern_id`=?");
				statement.setInt(1, editPatternId);
				
				this.databaseConnection.deleteQuery(statement);
				
				for (String relatedPattern : relatedPatterns) {
					statement = this.databaseConnection.prepareStatement("INSERT INTO `related_patterns` (`pattern_id`, `related_pattern_id`)" +
																		" VALUES (?, ?)");
					statement.setInt(1, editPatternId);
					statement.setInt(2, this.getPatternId(relatedPattern));
					
					this.databaseConnection.insertQuery(statement);
					
				}
				
				MOCPattern pattern = null;
				
				for (int i = 0 ; i < this.patterns.size() ; i++) {
					if (this.patterns.get(i).getPattern_id() == editPatternId) {
						pattern = this.patterns.get(i); 
						break;
					}
				}
				
				this.patterns.remove(pattern);
				
				this.patterns.add(new MOCPattern(editPatternId, this.getPlayerId(player.getName()), player.getName(), name, screenshot, null, context, problem, solution));
				Collections.sort(this.patterns);
				
				return true;
				
			} catch (SQLException e) { e.printStackTrace(); }

			return false;
			
		}
		
	}
	//########################################################################################################################################
	

	
	
	
	
	
	//########################################################################################################################################
	public int getRatingTypeId(String ratingTypeName) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `id` FROM `comment_types` WHERE `name`='" + ratingTypeName + "'");
		
		try {
			if (!rs.first()) {
				rs = this.databaseConnection.insertSafeQueryAndKeys("INSERT INTO `comment_types` (`name`) VALUES ('" + ratingTypeName + "')");
				
			}
			
			rs.first();
			
			return rs.getInt(1);
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
		
	}
	public String getRatingType(int ratingTypeId) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `name` FROM `comment_types` WHERE `id`='" + ratingTypeId + "'");
		
		try {
			while (rs.next()) { return rs.getString(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		return "";
		
	}
	//########################################################################################################################################
	public int getLocationId(Location location) {
		ResultSet rs = this.databaseConnection.insertSafeQueryAndKeys("INSERT INTO `locations` (`X`, `Y`, `Z`, `H`, `P`, `world`)" +
					"VALUES(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", " +
								(int)location.getYaw() + ", " + (int)location.getPitch() + ", '" + location.getWorld().getName() + "')");
		
		try {
			while(rs.next()) { return rs.getInt(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
		
	}
	public Location getLocation(int locationId) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT * FROM `locations` WHERE `id`=" + locationId);
		Location location = null;
		try {
			while(rs.next()) {
				location = new Location(this.plugin.getServer().getWorld(rs.getString("world")), rs.getInt("X"), rs.getInt("Y"), rs.getInt("Z"), rs.getInt("H"), rs.getInt("P"));
				return location;
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return location;
		
	}
	//########################################################################################################################################
	public int getPatternId(String patternName) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `id` FROM `patterns` WHERE `name`='" + patternName + "'");
		
		try {
			while(rs != null && rs.next()) { return rs.getInt(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
		
	}
	public String getPatternName(int patternId) {
		try {
			ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `name` FROM `patterns` WHERE `id`=" + patternId);
			while (rs.next()) { return rs.getString(1); }
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return "";
		
	}
	//########################################################################################################################################
	public int getPlayerId(String playerName) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `id` FROM `wp_users` WHERE `user_login`='" + playerName + "'");
		try {
			while (rs.next()) { return rs.getInt(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		//No match found write player to database
		rs = this.databaseConnection.insertSafeQueryAndKeys("INSERT INTO `wp_users` (`user_login`, `user_status`) VALUES ('" + playerName + "', -1)");
		
		try {
			rs.first();
			return rs.getInt(1);
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
		
	}
	
	public String getPlayerName(int playerId) {
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `user_login` FROM `wp_users` WHERE `id`=" + playerId);
		try {
			while (rs.next()) { return rs.getString(1); }
		} catch (SQLException e) { e.printStackTrace(); }
		
		return "";
		
	}
	//########################################################################################################################################
	public ArrayList<String> getPatternRelatedPatterns(String patternName) {
		ArrayList<String> relatedPatterns = new ArrayList<String>();
		
		try {
			ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT `related_pattern_id` FROM `related_patterns` WHERE `pattern_id`=" + this.getPatternId(patternName));
			while (rs != null && rs.next()) {
				relatedPatterns.add(this.getPatternName(rs.getInt(1)));
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return relatedPatterns;
	}
	//########################################################################################################################################

	
	
	
	
	
	
	//########################################################################################################################################
	public ArrayList<MOCPattern> getPatterns() {
		if (System.currentTimeMillis() - this.lastPatternsUpdate > 600000) this.updatePatterns();
		return this.patterns;
	}
	
	public void updatePatterns() {
		this.patterns.clear();
		this.lastPatternsUpdate = System.currentTimeMillis();
		
		ResultSet patternsSet = this.databaseConnection.sqlSafeQuery("SELECT * FROM `patterns`");

		try {
			while (patternsSet != null && patternsSet.next()) {
				MOCPattern pattern = new MOCPattern(patternsSet.getInt("id"), patternsSet.getInt("player_id"), this.getPlayerName(patternsSet.getInt("player_id")),
						patternsSet.getString("name"), patternsSet.getString("screen_src"), patternsSet.getTimestamp("createdon"), patternsSet.getString("context"),
						patternsSet.getString("problem"), patternsSet.getString("solution"));
				patterns.add(pattern);
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		Collections.sort(this.patterns);
			
	}
	//########################################################################################################################################
	public ArrayList<MOCComment> getComments() {
		if (System.currentTimeMillis() - this.lastCommentsUpdate > 600000) this.updateComments();
		return this.comments;
	}

	public void updateComments() {
		this.comments.clear();
		this.lastCommentsUpdate = System.currentTimeMillis();
		
		ResultSet commentsSet = this.databaseConnection.sqlSafeQuery("SELECT * FROM `rating_comments`");

		try {
			while (commentsSet != null && commentsSet.next()) {
				MOCComment comment = new MOCComment(commentsSet.getInt("id"), commentsSet.getInt("pattern_id"), this.getPatternName(commentsSet.getInt("pattern_id")),
						commentsSet.getString("title"), commentsSet.getInt("rating_t"), this.getRatingType(commentsSet.getInt("rating_t")), commentsSet.getInt("score"),
						commentsSet.getInt("location_id"), this.getLocation(commentsSet.getInt("location_id")), commentsSet.getString("comment"), commentsSet.getTimestamp("createon"),
						commentsSet.getInt("player_id"), this.getPlayerName(commentsSet.getInt("player_id")), commentsSet.getString("screen_src"), commentsSet.getInt("is_on"));
				this.comments.add(comment);
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		Collections.sort(this.comments);
			
	}
	//########################################################################################################################################


	public void setCommentIS_ON(MOCComment selectedComment, boolean isOn) {
		if (this.comments.contains(selectedComment)) {
			//Update comment in memory
			selectedComment.setIsOn(isOn);
			
			int is_on;
			if (isOn) is_on = 1;
			else is_on = 0;
			
			//Update comment in database
			this.databaseConnection.updateSafeQueryAndKeys("UPDATE `rating_comments` SET `is_on`=" + is_on + " WHERE `id`=" + selectedComment.getRating_comment_id());
			
		}
		
	}


	public void likeComment(MOCComment selectedComment, SpoutPlayer player, String choice) {
		//Write to database
		this.databaseConnection.insertSafeQuery("INSERT INTO `likes` (`player_id`, `comment_id`, `choice`)" +
												" VALUES ('" + this.getPlayerId(player.getName()) + "','" + selectedComment.getRating_comment_id() + "','" + choice + "')");
		//Update in memory store
		this.likes.add(new MOCLike(this.getPlayerId(player.getName()), selectedComment.getRating_comment_id(), choice));
		
		if (choice.equalsIgnoreCase("likes")) {
			if (this.likesCounts.containsKey(selectedComment.getRating_comment_id())) {
				this.likesCounts.get(selectedComment.getRating_comment_id()).incrementAndGet();
				
			} else {
				this.likesCounts.put((Integer)selectedComment.getRating_comment_id(), new AtomicInteger(1));
				
			}
			
		} else if (choice.equalsIgnoreCase("dislikes")) {
			if (this.dislikesCounts.containsKey(selectedComment.getRating_comment_id())) {
				this.dislikesCounts.get(selectedComment.getRating_comment_id()).incrementAndGet();
				
			} else {
				this.dislikesCounts.put((Integer)selectedComment.getRating_comment_id(), new AtomicInteger(1));
				
			}
			
		}
		
	}


	public boolean playerLikedComment(MOCComment selectedComment, SpoutPlayer player) {
		for (MOCLike like : this.likes) {
			if (like.getPlayer_id() == this.getPlayerId(player.getName()) && like.getComment_id() == selectedComment.getRating_comment_id()) {
				return true;
				
			}
			
		}
		return false;
		
	}
	//########################################################################################################################################
	public ArrayList<MOCLike> getLikes() {
		if (System.currentTimeMillis() - this.lastLikesUpdate > 600000) this.updateLikes();
		return this.likes;
	}
	
	public void updateLikes() {
		this.likes.clear();
		this.lastLikesUpdate = System.currentTimeMillis();
		
		ResultSet likesSet = this.databaseConnection.sqlSafeQuery("SELECT * FROM `likes`");

		try {
			while (likesSet != null && likesSet.next()) {
				MOCLike like = new MOCLike(likesSet.getInt("player_id"), likesSet.getInt("comment_id"), likesSet.getString("choice"));
				this.likes.add(like);
			}
			this.updateLikesCounts();
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	//########################################################################################################################################
	public void updateLikesCounts() {
		for (MOCLike like : this.likes) {
			if (like.getChoice().equalsIgnoreCase("likes")) {
				if (this.likesCounts.containsKey(like.getComment_id())) {
					this.likesCounts.get(like.getComment_id()).incrementAndGet();
					
				} else {
					this.likesCounts.put((Integer)like.getComment_id(), new AtomicInteger(1));
					
				}
				
			} else if (like.getChoice().equalsIgnoreCase("dislikes")) {
				if (this.dislikesCounts.containsKey(like.getComment_id())) {
					this.dislikesCounts.get(like.getComment_id()).incrementAndGet();
					
				} else {
					this.dislikesCounts.put((Integer)like.getComment_id(), new AtomicInteger(1));
					
				}
				
			}
			
		}
		
	}
	
	public int getLikesCount(int commentId) {
		if (this.likesCounts.containsKey(commentId)) { return this.likesCounts.get(commentId).intValue(); }
		
		return 0;
		
	}
	
	public int getDislikesCount(int commentId) {
		if (this.dislikesCounts.containsKey(commentId)) { return this.dislikesCounts.get(commentId).intValue(); }
		
		return 0;
		
	}
	//########################################################################################################################################
}
