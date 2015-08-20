package net.moc.MOCDreamCatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import net.moc.MOCDreamCatcher.Data.Dream;
import net.moc.MOCDreamCatcher.Data.DreamPlayer;
import net.moc.MOCDreamCatcher.Data.DreamPlayer.PlayerState;
import net.moc.MOCDreamCatcher.Data.Survey;
import net.moc.MOCDreamCatcher.Data.Thought;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MOCDreamCatcherConfig {
	//===============================================================================
	private MOCDreamCatcher plugin;
	
	private File configFile;
	private YamlConfiguration config;
	
	private File playersFile;
	private YamlConfiguration players;
	
	private File statsFile;
	private YamlConfiguration stats;
	
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	
	private String pathMaxWorlds = "dream.maxWorlds";
	private String pathWorldPermissions = "dream.worldPermissions";
	
	private String pathPlayers = "players";
	private String pathThoughts = "thoughts";

	private String pathThoughtInventory = "inventory";
	private String pathThoughtStart = "start";
	private String pathThoughtEnd = "end";
	private String pathThoughtSurvey = "survey";
	private String pathThoughtPublished = "published";

	private String pathDream = "dream";
	private String pathDreamBase = "dream.base";
	private String pathDreamBaseAuthor = "dream.baseAuthor";
	private String pathDreamWakeInventory = "wakeInventory";
	private String pathDreamWakeLocation = "wakeLocation";

	private String pathPlayerState = "state";
	private String pathPlayerFirstTimeUser = "firstTimeUser";
	//===============================================================================
	public MOCDreamCatcherConfig(File configFile, File playersFile, File statsFile, MOCDreamCatcher plugin) {
		this.plugin = plugin;
		
		this.configFile = configFile;
		this.config = new YamlConfiguration();
		
		this.playersFile = playersFile;
		this.players = new YamlConfiguration();
		
		this.statsFile = statsFile;
		this.stats = new YamlConfiguration();
		
		//Some default settings
		configDefaultsHash.put(pathMaxWorlds, 3);
		String[] permissions = {
				"multiverse.core.confirm",
				"multiverse.core.coord",
				"multiverse.core.info",
				"multiverse.core.list.environments",
				"multiverse.core.list.who",
				"commandbook.give",
				"commandbook.rules",
				"commandbook.setspawn",
				"commandbook.time.check",
				"commandbook.time.lock",
				"commandbook.spawnmob",
				"commandbook.weather",
				"commandbook.weather.thunder",
				"commandbook.biome",
				"commandbook.spawn",
				"commandbook.teleport",
				"commandbook.locations.coords",
				"commandbook.whereami",
				"commandbook.whereami.compass",
				"commandbook.gamemode",
				"commandbook.heal",
				"commandbook.god",
				"worldedit.*",
				"citizens.basic.*",
				"citizens.toggle.all",
				"citizens.guard.*",
				"citizens.blacksmith.*",
				"citizens.healer.*",
				"citizens.quester.*",
				"citizens.trader.*",
				"citizens.wizard.*",};
		
		configDefaultsHash.put(pathWorldPermissions, Arrays.asList(permissions));
		
		reload();
		
	}
	
	//===============================================================================
	private void save() { try { config.save(configFile); } catch (IOException e) { e.printStackTrace(); } }
	private void load() { try { config.load(configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }

	private void savePlayers() { try { players.save(playersFile); } catch (IOException e) { e.printStackTrace(); } }
	private void loadPlayers() { try { players.load(playersFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	
	private void saveStats() { try { stats.save(statsFile); } catch (IOException e) { e.printStackTrace(); } }
	private void loadStats() { try { stats.load(statsFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	
	public void reload() {
		//Check if configuration file exists
		if (configFile.exists()) {
			//If does, load it
			load();

			if (config.getString(pathMaxWorlds) == null) { config.set(pathMaxWorlds, configDefaultsHash.get(pathMaxWorlds)); }
			if (config.getString(pathWorldPermissions) == null) { config.set(pathWorldPermissions, configDefaultsHash.get(pathWorldPermissions)); }
			
			save();
			
		} else {
			//Otherwise create and populate default values
			for (String key : configDefaultsHash.keySet()) { config.set(key, configDefaultsHash.get(key)); }

			save();
			
		}
		
		if (playersFile.exists()) { loadPlayers(); }
		else { savePlayers(); }
		
		if (statsFile.exists()) { loadStats(); }
		else { saveStats(); }
		
	}

	//===============================================================================
	public int getMaxDreamsPerPerson() { return config.getInt(pathMaxWorlds, 3); }
	public List<String> getDefaultWorldPermissions() { return config.getStringList(pathWorldPermissions); }

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
	//===============================================================================
	public ArrayList<DreamPlayer> getDreamPlayers() {
		ArrayList<DreamPlayer> dreamPlayers = new ArrayList<DreamPlayer>();
		
		if (playerList() != null) for (String playerName : playerList()) { dreamPlayers.add(getDreamPlayer(playerName)); }
		
		return dreamPlayers;
		
	}
	
	//===============================================================================
	private DreamPlayer getDreamPlayer(String playerName) {
		DreamPlayer dp = new DreamPlayer(playerName, plugin, getDreamPlayerThoughts(playerName), getDreamPlayerDream(playerName), getDreamPlayerState(playerName), getDreamPlayerWakeInventory(playerName), getDreamPlayerWakeLocation(playerName));
		
		return dp;
		
	}
	
	//===============================================================================
	public void setDreamPlayer(DreamPlayer dp) {
		setDreamPlayerState(dp.getName(), dp.getState());
		setDreamPlayerThoughts(dp.getName(), dp.getThoughts());
		setDreamPlayerWakeInfo(dp.getName(), dp.getWakeInventory(), dp.getWakeLocation());
		setDreamPlayerDream(dp.getDream(), dp.getName());
		
	}

	//===============================================================================
	private PlayerState getDreamPlayerState(String playerName) { return PlayerState.valueOf(players.getString(pathPlayers + "." + playerName + "." + pathPlayerState)); }
	public void setDreamPlayerState(String playerName, PlayerState state) { players.set(pathPlayers + "." + playerName + "." + pathPlayerState, state.toString()); savePlayers(); }

	//===============================================================================
	private ArrayList<Thought> getDreamPlayerThoughts(String playerName) {
		ArrayList<Thought> playerThoughts = new ArrayList<Thought>();
		
		if (playerThoughtsList(playerName) != null)
			for (String thoughtName : playerThoughtsList(playerName))
				playerThoughts.add(getDreamPlayerThought(playerName, thoughtName));
		
		return playerThoughts;
		
	}

	private Thought getDreamPlayerThought(String playerName, String thoughtName) {
		Thought thought = new Thought(thoughtName, playerName);
		
		//Published
		thought.setPublished(players.getBoolean(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName + "." + pathThoughtPublished));
		
		//Inventory
		thought.setInventory(stringToInventory(players.getString(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName + "." + pathThoughtInventory)));
		
		//Start
		thought.setStart(stringToLocation(players.getString(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName + "." + pathThoughtStart)));
		
		//End
		thought.setEnd(stringToLocation(players.getString(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName + "." + pathThoughtEnd)));
		
		//Survey
		thought.setSurvey(stringToSurvey(players.getString(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName + "." + pathThoughtSurvey)));
		
		return thought;
		
	}
	
	public void setDreamPlayerThoughts(String playerName, ArrayList<Thought> thoughts) {
		//Clear all thoughts first
		players.set(pathPlayers + "." + playerName + "." + pathThoughts, null);

		for (Thought t : thoughts) setDreamPlayerThought(playerName, t);
		
	}
	
	public void setDreamPlayerThought(String playerName, Thought thought) {
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thought.getName() + "." + pathThoughtPublished, thought.isPublished());
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thought.getName() + "." + pathThoughtInventory, inventoryToString(thought.getInventory()));
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thought.getName() + "." + pathThoughtStart, locationToString(thought.getStart()));
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thought.getName() + "." + pathThoughtEnd, locationToString(thought.getEnd()));
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thought.getName() + "." + pathThoughtSurvey, surveyToString(thought.getSurvey()));
		
		savePlayers();
		
	}
	
	public void removeDreamPlayerThought(String playerName, String thoughtName) {
		players.set(pathPlayers + "." + playerName + "." + pathThoughts + "." + thoughtName, null);
		
		savePlayers();
		
	}

	//===============================================================================
	private Survey stringToSurvey(String surveyString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String surveyToString(Survey survey) {
		//TODO
		return null;
	}

	//===============================================================================
	private Location stringToLocation(String locationString) {
		Location location = new Location(null, 0, 0, 0, 0, 0);
		
		if (locationString == null) return location;
		
		String parts[] = locationString.split(":");
		if (parts.length != 5) return location;
		
		try {
			location.setX(Double.parseDouble(parts[0]));
			location.setY(Double.parseDouble(parts[1]));
			location.setZ(Double.parseDouble(parts[2]));
			location.setPitch(Float.parseFloat(parts[3]));
			location.setYaw(Float.parseFloat(parts[4]));
		
		} catch (NumberFormatException e) {}
		
		return location;
		
	}
	
	private Location stringToLocationFull(String locationString) {
		Location location = new Location(null, 0, 0, 0, 0, 0);
		
		if (locationString == null) return location;
		
		String parts[] = locationString.split(":");
		if (parts.length != 6) return location;
		
		try {
			location.setX(Double.parseDouble(parts[0]));
			location.setY(Double.parseDouble(parts[1]));
			location.setZ(Double.parseDouble(parts[2]));
			location.setPitch(Float.parseFloat(parts[3]));
			location.setYaw(Float.parseFloat(parts[4]));
			location.setWorld(plugin.getServer().getWorld(parts[5]));
		
		} catch (NumberFormatException e) {}
		
		return location;
		
	}
	
	private String locationToString(Location location) {
		if (location == null) return null;
		
		return location.getX() + ":" +location.getY() + ":" +location.getZ() + ":" +location.getPitch() + ":" +location.getYaw();
		
	}

	private String locationToStringFull(Location location) {
		if (location == null) return null;
		
		return location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw() + ":" + location.getWorld().getName();
		
	}

	//===============================================================================
	private Inventory stringToInventory(String inventoryString) {
		if (inventoryString == null) return null;
		
		Inventory inventory = plugin.getServer().createInventory(null, InventoryType.PLAYER);
		
		for (String slotString : inventoryString.split("=")) {
			String parts[] = slotString.split(":");
			if (parts.length != 4) continue;
			
			try {
				int index = Integer.parseInt(parts[0]);
				int typeId = Integer.parseInt(parts[1]);
				short data = Short.parseShort(parts[2]);
				int amount = Integer.parseInt(parts[3]);
				
				ItemStack stack = new ItemStack(typeId);
				stack.setDurability(data);
				stack.setAmount(amount);
				
				for (int i = 4 ; i < parts.length ; i++) {
					stack.addEnchantment(Enchantment.getById(Integer.valueOf(parts[i].split("?")[0])), Integer.valueOf(parts[i].split("?")[1]));
					
				}

				inventory.setItem(index, stack);
				
			} catch (NumberFormatException e) {} catch (ArrayIndexOutOfBoundsException e2) {}
			
		}
		
		return inventory;
		
	}
	
	private String inventoryToString(Inventory inventory) {
		if (inventory == null) return null;
		
		String inventoryString = "";
		
		for (int i = 0 ; i < inventory.getSize() ; i++) {
			ItemStack item = inventory.getItem(i);
			
			if (item != null) {
				inventoryString += i + ":" + item.getTypeId() + ":" + item.getDurability() + ":" + item.getAmount();
				
				if (item.getEnchantments().size() > 0) {
					for (Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
						inventoryString += ":" + e.getKey().getId() + "?" + e.getValue();
						
					}
					
				}
				
				inventoryString += "=";
				
			}
			
		}
		
		return inventoryString;
		
	}

	//===============================================================================
	private Dream getDreamPlayerDream(String playerName) {
		String dreamBase = players.getString(pathPlayers + "." + playerName + "." + pathDreamBase);
		String dreamBaseAuthor = players.getString(pathPlayers + "." + playerName + "." + pathDreamBaseAuthor);
		
		if (dreamBase == null) return null;
		
		Dream dream = new Dream(dreamBase, dreamBaseAuthor);
		
		return dream;
		
	}
	
	public void setDreamPlayerDream(Dream dream, String playerName) {
		if (dream == null) { removeDreamPlayerDream(playerName); return; }
		
		players.set(pathPlayers + "." + playerName + "." + pathDreamBase, dream.getDreamThought());
		players.set(pathPlayers + "." + playerName + "." + pathDreamBaseAuthor, dream.getDreamAuthor());
		savePlayers();
		
	}
	
	private Inventory getDreamPlayerWakeInventory(String playerName) {
		if (players.getString(pathPlayers + "." + playerName + "." + pathDreamWakeInventory) == null) return null;
		return stringToInventory(players.getString(pathPlayers + "." + playerName + "." + pathDreamWakeInventory));
		
	}
	
	private Location getDreamPlayerWakeLocation(String playerName) {
		return stringToLocationFull(players.getString(pathPlayers + "." + playerName + "." + pathDreamWakeLocation));
		
	}
	
	public void setDreamPlayerWakeInfo(String playerName, Inventory inventory, Location location) {
		players.set(pathPlayers + "." + playerName + "." + pathDreamWakeInventory, inventoryToString(inventory));
		players.set(pathPlayers + "." + playerName + "." + pathDreamWakeLocation, locationToStringFull(location));
		
		savePlayers();
		
	}
	
	public void removeDreamPlayerDream(String playerName) {
		players.set(pathPlayers + "." + playerName + "." + pathDream, null);
		
		savePlayers();
		
	}
	
	//===============================================================================
	public void setNotFirstTimeUser(String playerName) { players.set(pathPlayers + "." + playerName + "." + pathPlayerFirstTimeUser, false); savePlayers(); }
	public boolean isFirstTimeUser(String playerName) { return players.getBoolean(pathPlayers + "." + playerName + "." + pathPlayerFirstTimeUser, true); }
	
	//===============================================================================
	private Set<String> playerList() { return list(players, pathPlayers); }
	
	private Set<String> playerThoughtsList(String playerName) { return list(players, pathPlayers + "." + playerName + "." + pathThoughts); }
	
	private Set<String> list(YamlConfiguration config, String path) {
	    if (config.getConfigurationSection(path) != null && config.getConfigurationSection(path).getKeys(false) != null)
		    return config.getConfigurationSection(path).getKeys(false);
	    
	    else return null;

	}
	
	//===============================================================================
	public void recordThoughtCreationDate(String playerName, String thoughtName) { stats.set(playerName + "." + thoughtName + ".createDate", getDateString()); saveStats(); }
	
	public void recordThoughtPublishDate(String playerName, String thoughtName) { stats.set(playerName + "." + thoughtName + ".publishDate", getDateString()); saveStats(); }
	
	public void recordThoughtLastEditDate(String playerName, String thoughtName) { stats.set(playerName + "." + thoughtName + ".lastEditDate", getDateString()); saveStats(); }
	
	public void recordThoughtVisit(String authorName, String thoughtName, String playerName) {
		//Increment number of visits
		stats.set(authorName + "." + thoughtName + ".numberOfVisits", stats.getInt(authorName + "." + thoughtName + ".visits", 0) + 1);
		//Record visitor name and date
		stats.set(authorName + "." + thoughtName + ".visitors." + playerName + ".lastVisit", getDateString());
		//Increment number of visits from this player
		stats.set(authorName + "." + thoughtName + ".visitors." + playerName + ".totalVisits", stats.getInt(authorName + "." + thoughtName + ".visitors." + playerName + ".totalVisits", 0) + 1);
		
		saveStats();
		
	}
	
	public void recordThoughtCompletion(String authorName, String thoughtName, String playerName) {
		//Increment number of visits
		stats.set(authorName + "." + thoughtName + ".numberOfCompletions", stats.getInt(authorName + "." + thoughtName + ".numberOfCompletions", 0) + 1);
		//Record visitor name and date
		stats.set(authorName + "." + thoughtName + ".finalists." + playerName + ".lastCompletion", getDateString());
		//Increment number of visits from this player
		stats.set(authorName + "." + thoughtName + ".finalists." + playerName + ".totalCompletions", stats.getInt(authorName + "." + thoughtName + ".finalists." + playerName + ".totalCompletions", 0) + 1);
		
		saveStats();
		
	}
	
	
	//===============================================================================
	public String getDateString() {
		//Generate File name
		Calendar c = Calendar.getInstance();
		String year = "" + c.get(Calendar.YEAR);
		
		String month = "" + c.get(Calendar.MONTH);
		if (month.length() == 1) month = "0" + month;
		
		String day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) day = "0" + day;
		
		String hour = "" + c.get(Calendar.HOUR_OF_DAY);
		if (hour.length() == 1) hour = "0" + hour;
		
		String minute = "" + c.get(Calendar.MINUTE);
		if (minute.length() == 1) minute = "0" + minute;
		
		String second = "" + c.get(Calendar.SECOND);
		if (second.length() == 1) second = "0" + second;
		
		String millisecond = "" + c.get(Calendar.MILLISECOND);
		if (millisecond.length() == 1) millisecond = "00" + millisecond;
		else if (millisecond.length() == 2) millisecond = "0" + millisecond;
		
		return year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + millisecond;

	}
	
}
