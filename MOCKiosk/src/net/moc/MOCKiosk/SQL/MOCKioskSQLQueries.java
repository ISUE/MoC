package net.moc.MOCKiosk.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;
import net.moc.MOCKiosk.MOCKiosk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import moc.MOCDBLib.DBConnector;

public class MOCKioskSQLQueries {
	//===============================================
	private MOCKiosk plugin;
	private DBConnector databaseConnection;
	
	private TreeSet<MOCKioskKiosk> kiosks = new TreeSet<MOCKioskKiosk>();
	private long lastKiosksUpdate;
	private TreeSet<MOCKioskKioskDeck> decks = new TreeSet<MOCKioskKioskDeck>();
	private long lastDecksUpdate;
	private TreeSet<MOCKioskKioskSlide> slides = new TreeSet<MOCKioskKioskSlide>();
	private long lastSlidesUpdate;
	
	public MOCKioskSQLQueries(MOCKiosk plugin, DBConnector databaseConnection) {
		this.plugin = plugin;
		this.databaseConnection = databaseConnection;
		
		this.checkTables();
		
		this.updateKiosks();
		this.lastKiosksUpdate = System.currentTimeMillis();
		this.updateDecks();
		this.lastDecksUpdate = System.currentTimeMillis();
		this.updateSlides();
		this.lastSlidesUpdate = System.currentTimeMillis();
		
		
	}
	//===============================================
	

	private void checkTables() {
		//kiosks
		this.databaseConnection.ensureTable("kiosks", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null, owner_id int(10) not null, neartext varchar(255) not null, nearurl varchar(255) not null," +
				" clicktext varchar(255) not null, clickurl varchar(255) not null, popup_deck_id int(10) not null, isactive int(1)  not null default 1, location_id int(11) not null");
		
		//kiosk_decks
		this.databaseConnection.ensureTable("kiosk_decks", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null");
		
		//kiosk_slides
		this.databaseConnection.ensureTable("kiosk_slides", "id int(10) not null auto_increment, PRIMARY KEY (id), name varchar(50) not null, indeck_id int(10) not null," +
				" title varchar(50) not null, text varchar(255) not null, url varchar(255) not null, image varchar(255) not null, image_size enum('medium', 'small', 'large') not null");
		
		//locations
		this.databaseConnection.ensureTable("locations", "id int(10) not null auto_increment, PRIMARY KEY (id), X float not null, Y float not null, Z float not null, H float not null, P float not null, world varchar(50) not null");
		
		//wp_users
		this.databaseConnection.ensureTable("wp_users", "id bigint(20) not null auto_increment, PRIMARY KEY (id), user_login varchar(60) not null, user_status int(11) not null DEFAULT 0");
	
	}


	//########################################################################################################################################
	public int saveKiosk(String kioskName, String nearText, String nearURL, String clickText, String clickURL, boolean usePopup,
						String popupTitle, String popupText, String popupURL, String popupImageURL, int popupImageSize, String playerName, Block block, MOCKioskKiosk editKiosk, MOCKioskKioskSlide editSlide) {
		int id = -1;
		
		if (editKiosk == null) {
			//New kiosk - Save
			PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO `kiosks` (`name`, `owner_id`, `neartext`, `nearurl`, `clicktext`, `clickurl`, `popup_deck_id`, `isactive`, `location_id`)" +
					" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			try {
				int playerId = this.getPlayerId(playerName);
				int popupDeckId = -1;
				int locationId = this.getLocationId(block.getLocation());

				statement.setString(1, kioskName); //name
				statement.setInt(2, playerId); //owner_id
				statement.setString(3, nearText); //neartext
				statement.setString(4, nearURL); //nearurl
				statement.setString(5, clickText);//clicktext
				statement.setString(6, clickURL);//clickurl
				if (usePopup) {
					popupDeckId = this.getPopupDeckId(kioskName, popupTitle, popupText, popupURL, popupImageURL, popupImageSize);
					statement.setInt(7, popupDeckId);//popup_deck_id
				} else {
					statement.setInt(7, popupDeckId);//popup_deck_id
				}
				statement.setInt(8, 1);//isactive
				statement.setInt(9, locationId);//location_id

				this.databaseConnection.insertQuery(statement);

				ResultSet rs = statement.getGeneratedKeys();
				rs.next();

				id = rs.getInt(1);

				this.kiosks.add(new MOCKioskKiosk(id, kioskName, playerId, playerName, nearText, nearURL, clickText, clickURL,
						popupDeckId, 1, locationId, block.getLocation()));

			} catch (SQLException e) { e.printStackTrace(); }

		} else {
			//Update existing Kiosk
			PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("UPDATE `kiosks` SET " +
											"`name`=?, `neartext`=?, `nearurl`=?, `clicktext`=?, `clickurl`=?, `popup_deck_id`=? WHERE `id`=?");

			try {
				statement.setString(1, kioskName); //name
				statement.setString(2, nearText); //neartext
				statement.setString(3, nearURL); //nearurl
				statement.setString(4, clickText);//clicktext
				statement.setString(5, clickURL);//clickurl
				
				editKiosk.setName(kioskName);
				editKiosk.setNeartext(nearText);
				editKiosk.setNearurl(nearURL);
				editKiosk.setClicktext(clickText);
				editKiosk.setClickurl(clickURL);
				
				int popupDeckId = editKiosk.getPopup_deck_id();
				
				if (usePopup) {
					if (editSlide == null) {
						//Adding popup
						popupDeckId = this.getPopupDeckId(kioskName, popupTitle, popupText, popupURL, popupImageURL, popupImageSize);
						statement.setInt(6, popupDeckId);//popup_deck_id
						editKiosk.setPopup_deck_id(popupDeckId);
						
					} else {
						//Updating popup
						statement.setInt(6, popupDeckId);//popup_deck_id
						this.updatePopupSlide(editSlide, kioskName, popupTitle, popupText, popupURL, popupImageURL, popupImageSize);
						
					}
					
				} else {
					//No popup - removing or overwriting -1
					editKiosk.setPopup_deck_id(-1);
					statement.setInt(6, -1);//popup_deck_id
					
				}
				
				statement.setInt(7, editKiosk.getId());//id
				
				//Update kiosk
				this.databaseConnection.updateQuery(statement);
				
				id = editKiosk.getId();

			} catch (SQLException e) { e.printStackTrace(); }
			
		}
		
		return id;
		
	}
	//########################################################################################################################################
	private int getPopupDeckId(String kioskName, String popupTitle, String popupText, String popupURL, String popupImageURL, int popupImageSize) {
		int deckId = this.getPopupDeckId(kioskName);
		
		PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO `kiosk_slides` (`name`, `indeck_id`, `title`, `text`, `url`, `image`, `image_size`)" +
				" VALUES (?,?,?,?,?,?,?)");
		
		try {
			statement.setString(1, kioskName);
			statement.setInt(2, deckId);
			statement.setString(3, popupTitle);
			statement.setString(4, popupText);
			statement.setString(5, popupURL);
			statement.setString(6, popupImageURL);
			statement.setInt(7, popupImageSize);
			
			this.databaseConnection.insertQuery(statement);
			
			ResultSet rs = statement.getGeneratedKeys();
			
			rs.first();
			
			int slideId = rs.getInt(1);
			
			this.slides.add(new MOCKioskKioskSlide(slideId, kioskName, deckId, popupTitle, popupText, popupURL, popupImageURL, popupImageSize));
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return deckId;
		
	}
	
	private void updatePopupSlide(MOCKioskKioskSlide editSlide, String kioskName, String popupTitle, String popupText, String popupURL, String popupImageURL, int popupImageSize) {
		editSlide.setName(kioskName);
		editSlide.setTitle(popupTitle);
		editSlide.setText(popupText);
		editSlide.setUrl(popupURL);
		editSlide.setImage(popupImageURL);
		editSlide.setImage_size(popupImageSize);
		
		PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("UPDATE `kiosk_slides` SET `name`=?, `title`=?, `text`=?, `url`=?, `image`=?, `image_size`=? WHERE `id`=?");
		
		try {
			statement.setString(1, kioskName);//name
			statement.setString(2, popupTitle);//title
			statement.setString(3, popupText);//text
			statement.setString(4, popupURL);//url
			statement.setString(5, popupImageURL);//image
			statement.setInt(6, popupImageSize);//image_size
			statement.setInt(7, editSlide.getId());//id
			
			this.databaseConnection.updateQuery(statement);
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	private int getPopupDeckId(String deckName) {
		PreparedStatement statement = this.databaseConnection.prepareStatementAndKeys("SELECT `id` FROM `kiosk_decks` WHERE `name`=?");
		
		int id = -1;
		
		try {
			statement.setString(1, deckName);
			
			ResultSet rs = this.databaseConnection.sqlQuery(statement);
			
			if (!rs.first()) {
				statement = this.databaseConnection.prepareStatementAndKeys("INSERT INTO `kiosk_decks` (`name`) VALUES (?)");
				statement.setString(1, deckName);
				
				this.databaseConnection.insertQuery(statement);
				
				rs = statement.getGeneratedKeys();
				
				rs.first();
				
				id = rs.getInt(1);
				
				this.decks.add(new MOCKioskKioskDeck(id, deckName));
			
			} else {
				id = rs.getInt(1);
				
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		return id;
		
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
	public void deleteKiosk(MOCKioskKiosk kiosk) {
		//Remove block
		this.plugin.getServer().getWorld(kiosk.getLocation().getWorld().getName()).getBlockAt(kiosk.getLocation()).setType(Material.AIR);
		
		//Remove in memory records
		MOCKioskKioskDeck deck = getDeck(kiosk.getPopup_deck_id());
		MOCKioskKioskSlide slide = getSlide(kiosk.getPopup_deck_id());
		this.kiosks.remove(kiosk);
		this.slides.remove(slide);
		this.decks.remove(deck);
		
		//Remove database records
		this.databaseConnection.deleteSafeQuery("DELETE FROM `kiosks` WHERE `id`=" + kiosk.getId());
		this.databaseConnection.deleteSafeQuery("DELETE FROM `kiosk_decks` WHERE `id`=" + kiosk.getPopup_deck_id());
		this.databaseConnection.deleteSafeQuery("DELETE FROM `kiosk_slides` WHERE `indeck_id`=" + kiosk.getPopup_deck_id());
		
	}
	
	public MOCKioskKiosk getKiosk(int kioskId) {
		for (MOCKioskKiosk kiosk : this.kiosks) {
			if (kiosk.getId() == kioskId) {
				return kiosk;
				
			}
			
		}
		return null;
		
	}
	
	public TreeSet<MOCKioskKiosk> getKiosks() {
		if (System.currentTimeMillis() - this.lastKiosksUpdate > 600000) this.updateKiosks();
		return this.kiosks;
	}
	
	public void updateKiosks() {
		this.kiosks.clear();
		
		this.lastKiosksUpdate = System.currentTimeMillis();
		
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT * FROM `kiosks`");

		try {
			while (rs != null && rs.next()) {
				MOCKioskKiosk kiosk = new MOCKioskKiosk(rs.getInt("id"), rs.getString("name"), rs.getInt("owner_id"), this.getPlayerName(rs.getInt("owner_id")),
														rs.getString("neartext"), rs.getString("nearurl"), rs.getString("clicktext"), rs.getString("clickurl"),
														rs.getInt("popup_deck_id"), rs.getInt("isactive"), rs.getInt("location_id"), this.getLocation(rs.getInt("location_id")));
				this.kiosks.add(kiosk);
				
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	//########################################################################################################################################
	public MOCKioskKioskDeck getDeck(int deckId) {
		for (MOCKioskKioskDeck deck : this.decks) {
			if (deck.getId() == deckId) {
				return deck;
				
			}
			
		}
		return null;
		
	}
	
	public TreeSet<MOCKioskKioskDeck> getDecks() {
		if (System.currentTimeMillis() - this.lastDecksUpdate > 600000) this.updateDecks();
		return this.decks;
	}
	
	public void updateDecks() {
		this.decks.clear();
		
		this.lastDecksUpdate = System.currentTimeMillis();
		
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT * FROM `kiosk_decks`");

		try {
			while (rs != null && rs.next()) {
				MOCKioskKioskDeck deck = new MOCKioskKioskDeck(rs.getInt("id"), rs.getString("name"));
				this.decks.add(deck);
				
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	//########################################################################################################################################
	public MOCKioskKioskSlide getSlide(int slideId) {
		if (slideId != -1) {
			for (MOCKioskKioskSlide slide : this.slides) {
				if (slide.getIndeck_id() == slideId) {
					//Found matching slide
					return slide;
					
				}

			}
			
		}
		return null;
		
	}
	
	public TreeSet<MOCKioskKioskSlide> getSlides() {
		if (System.currentTimeMillis() - this.lastSlidesUpdate > 600000) this.updateSlides();
		return this.slides;
	}
	
	public void updateSlides() {
		this.slides.clear();
		
		this.lastSlidesUpdate = System.currentTimeMillis();
		
		ResultSet rs = this.databaseConnection.sqlSafeQuery("SELECT * FROM `kiosk_slides`");

		try {
			while (rs != null && rs.next()) {
				String imageSizeEnum = rs.getString("image_size");
				int imageSizeEnumInt;
				if (imageSizeEnum.equalsIgnoreCase("medium")) imageSizeEnumInt = 0;
				else if (imageSizeEnum.equalsIgnoreCase("small")) imageSizeEnumInt = 1;
				else imageSizeEnumInt = 2;
				
				MOCKioskKioskSlide slide = new MOCKioskKioskSlide(rs.getInt("id"), rs.getString("name"), rs.getInt("indeck_id"), rs.getString("title"), rs.getString("text"),
																	rs.getString("url"), rs.getString("image"), imageSizeEnumInt);
				this.slides.add(slide);
				
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	//########################################################################################################################################
}
