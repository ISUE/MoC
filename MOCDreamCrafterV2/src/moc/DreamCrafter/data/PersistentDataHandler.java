package moc.DreamCrafter.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.WorldData.WorldType;
import moc.DreamCrafter.util.InventoryStringDeSerializer;
import moc.DreamCrafter.util.YAMLHelper;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class PersistentDataHandler {
	private final String worldsFileName = "worlds.yml";
	private final String playersFileName = "players.yml";
	private final String worldsFilePath, playersFilePath;
	private final String baseDirectory;
	
	private MOCDreamCrafter plugin;
	
	private YamlConfiguration worldsYaml;
	private YamlConfiguration playersYaml;
	
	private HashMap<String, WorldData> worlds;
	private HashMap<String, PlayerData> players;
	
	public PersistentDataHandler(MOCDreamCrafter plugin) {
		this.plugin = plugin;
		baseDirectory = plugin.getDataFolder().getAbsolutePath();
		worldsFilePath = baseDirectory + File.separator + worldsFileName;
		playersFilePath = baseDirectory + File.separator + playersFileName;
		
		worldsYaml = YAMLHelper.initYaml(worldsFilePath);
		playersYaml = YAMLHelper.initYaml(playersFilePath);
		
		loadWorldsFromYaml();
		loadPlayersFromYaml();
	}

//-----------------------------------------------------------------------------	

	public boolean newBuildWorld(String worldName, String ownerName) {
		if(worlds.containsKey(worldName))
			return false;
		
		WorldData wd = new WorldData();
		wd.name = worldName;
		wd.ownerName = ownerName;
		wd.worldType = WorldType.Build;
		wd.spawnpoint = plugin.getServer().getWorld(worldName).getSpawnLocation();
//		wd.inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
		worlds.put(worldName, wd);
		
		plugin.getLog().info("New Build World "+worldName);		
		//saveAll();
		
		return true;
	}
	
//-----------------------------------------------------------------------------	

	public boolean newDreamWorld(String buildWorldName, String worldName, String ownerName) {		
		WorldData buildWorldData = worlds.get(buildWorldName);
		
		WorldData wd = new WorldData();
		wd.name = worldName;
		wd.ownerName = ownerName;
		wd.worldType = WorldType.Dream;
		
		// Copy properties from build world
		wd.blacklist = buildWorldData.blacklist;
		wd.gameMode = buildWorldData.gameMode;
		wd.time = buildWorldData.time;
		wd.difficulty = buildWorldData.difficulty;
		wd.isStormyWeatherEnabled = buildWorldData.isStormyWeatherEnabled;
		wd.isThunderEnabled = buildWorldData.isThunderEnabled;
		wd.endpoint = buildWorldData.endpoint;
		wd.inventory = buildWorldData.inventory;
		wd.spawnpoint = buildWorldData.spawnpoint;
		
		worlds.put(worldName, wd);		
		plugin.getLog().info("New Dream World: " + worldName);
		
		saveAll();
		
		return true;
	}

//-----------------------------------------------------------------------------	
	
	public boolean newPlayer(Player player) {
		if(players.containsKey(player.getName()))
			return false;
		
		PlayerData pd = new PlayerData();
		pd.name = player.getName();
		pd.lastWorldLocation = player.getLocation();
		pd.lastWorldInventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
		pd.lastWorldInventory.setContents(player.getInventory().getContents());
		players.put(player.getName(), pd);
		plugin.getLog().info("New player: " + player.getName());
		
		saveAll();
		
		return true;
	}

//-----------------------------------------------------------------------------	
	
	public void saveAll() {
		worldsYaml = new YamlConfiguration();
		
		for(String key : worlds.keySet()) {
			WorldData wd = worlds.get(key);
			worldsYaml.set("worlds." + wd.name + ".description", wd.description);
			worldsYaml.set("worlds." + wd.name + ".owner", wd.ownerName);
			worldsYaml.set("worlds." + wd.name + ".type", wd.worldType.toString());
			worldsYaml.set("worlds." + wd.name + ".isPublished", wd.isPublished);
			worldsYaml.set("worlds." + wd.name + ".isThunderEnabled", wd.isThunderEnabled);
			worldsYaml.set("worlds." + wd.name + ".isStormyWeatherEnabled", wd.isStormyWeatherEnabled);
			worldsYaml.set("worlds." + wd.name + ".time", wd.time);
			worldsYaml.set("worlds." + wd.name + ".blacklist", wd.blacklist);
			worldsYaml.set("worlds." + wd.name + ".difficulty", wd.difficulty.toString());
			worldsYaml.set("worlds." + wd.name + ".gameMode", wd.gameMode.toString());
			worldsYaml.set("worlds." + wd.name + ".endpoint.x", (wd.endpoint == null) ? 0 : wd.endpoint.getBlockX());
			worldsYaml.set("worlds." + wd.name + ".endpoint.y", (wd.endpoint == null) ? 0 : wd.endpoint.getBlockY());
			worldsYaml.set("worlds." + wd.name + ".endpoint.z", (wd.endpoint == null) ? 0 : wd.endpoint.getBlockZ());
//			Util.DLOG(wd.name);
//			Util.DLOG(String.valueOf(wd.spawnpoint));
//			Util.DLOG(String.valueOf(plugin.getServer().getWorld(wd.name)));
//			Util.DLOG( String.valueOf(plugin.getServer().getWorld(wd.name).getSpawnLocation()));
//			Util.DLOG( String.valueOf(plugin.getServer().getWorld(wd.name).getSpawnLocation().getBlockZ()));
			if(plugin.getServer().getWorld(wd.name) == null) {
				plugin.getLog().info("Can't find world " + wd.name + " to save");
			}
			else {
				worldsYaml.set("worlds." + wd.name + ".spawnpoint.x", (wd.spawnpoint == null) ? plugin.getServer().getWorld(wd.name).getSpawnLocation().getBlockX() : wd.spawnpoint.getBlockX());
				worldsYaml.set("worlds." + wd.name + ".spawnpoint.y", (wd.spawnpoint == null) ? plugin.getServer().getWorld(wd.name).getSpawnLocation().getBlockY() : wd.spawnpoint.getBlockY());
				worldsYaml.set("worlds." + wd.name + ".spawnpoint.z", (wd.spawnpoint == null) ? plugin.getServer().getWorld(wd.name).getSpawnLocation().getBlockZ() : wd.spawnpoint.getBlockZ());
			}
			worldsYaml.set("worlds." + wd.name + ".inventory", InventoryStringDeSerializer.InventoryToString(wd.inventory));
		}
		
		playersYaml = new YamlConfiguration();
		
		for(String key : players.keySet()) {
			PlayerData pd = players.get(key);
			playersYaml.set("players." + pd.name + ".lastNormalWorld", pd.lastWorldLocation.getWorld().getName());
			playersYaml.set("players." + pd.name + ".lastWorldLocation.x", pd.lastWorldLocation.getBlockX());
			playersYaml.set("players." + pd.name + ".lastWorldLocation.y", pd.lastWorldLocation.getBlockY());
			playersYaml.set("players." + pd.name + ".lastWorldLocation.z", pd.lastWorldLocation.getBlockZ());
			playersYaml.set("players." + pd.name + ".lastWorldInventory", InventoryStringDeSerializer.InventoryToString(pd.lastWorldInventory));
		}
		
		YAMLHelper.saveYaml(worldsYaml, worldsFilePath);
		YAMLHelper.saveYaml(playersYaml, playersFilePath);
	}

//-----------------------------------------------------------------------------	
	
	private void loadWorldsFromYaml() {
		String type, inv;
		int x, y, z;
		
		worlds = new HashMap<String, WorldData>();
		
		MemorySection m = (MemorySection) worldsYaml.get("worlds");
		if(m == null)
			return;
		
		plugin.getLog().info("Loading worlds");
		
		Set<String> paths = m.getKeys(true);
		for(String path : paths) {
			
			if(path.indexOf(".") == -1) {		// If this path is a root
				WorldData wd = new WorldData();
				try {
					wd.name = path;
					
					type = worldsYaml.getString("worlds." + path + ".type");
					if(type.equals(WorldType.Dream.toString()))
						wd.worldType = WorldType.Dream;
					else if(type.equals(WorldType.Build.toString()))
						wd.worldType = WorldType.Build;
					else
						wd.worldType = WorldType.Normal;
					
					wd.description = 				worldsYaml.getString("worlds." + path + ".description");
					wd.ownerName = 					worldsYaml.getString("worlds." + path + ".owner");
					wd.isPublished = 				worldsYaml.getBoolean("worlds." + path + ".isPublished");
					wd.isThunderEnabled = 			worldsYaml.getBoolean("worlds." + path + ".isThunderEnabled");
					wd.isStormyWeatherEnabled = 	worldsYaml.getBoolean("worlds." + path + ".isStormyWeatherEnabled");
					wd.time = 						worldsYaml.getString("worlds." + path + ".time");
					wd.blacklist = 					worldsYaml.getString("worlds." + path + ".blacklist");
					wd.gameMode = 					GameMode.valueOf(worldsYaml.getString("worlds." + path + ".gameMode"));
					wd.difficulty =					Difficulty.valueOf(worldsYaml.getString("worlds." + path + ".difficulty"));
					
					x = worldsYaml.getInt("worlds." + path + ".endpoint.x");
					y = worldsYaml.getInt("worlds." + path + ".endpoint.y");
					z = worldsYaml.getInt("worlds." + path + ".endpoint.z");
					wd.endpoint = new Location(Bukkit.getWorld(wd.name), x, y, z);
					
					inv = worldsYaml.getString("worlds." + path + ".inventory");
					wd.inventory = InventoryStringDeSerializer.StringToInventory(inv);
					
					x = worldsYaml.getInt("worlds." + path + ".spawnpoint.x");
					y = worldsYaml.getInt("worlds." + path + ".spawnpoint.y");
					z = worldsYaml.getInt("worlds." + path + ".spawnpoint.z");
					wd.spawnpoint = new Location(Bukkit.getWorld(wd.name), x, y, z);
				
				} catch(Exception e) { e.printStackTrace(); }
				
				worlds.put(path, wd);
				plugin.getLog().info("Initializing World Data for " + wd.name + " (" + wd.worldType + ")");
			}
		}
	}

//-----------------------------------------------------------------------------	
	
	private void loadPlayersFromYaml() {
		players = new HashMap<String, PlayerData>();
		
		MemorySection m = (MemorySection) playersYaml.get("players");
		if(m == null)
			return;
		
		Set<String> paths = m.getKeys(true);
		for(String path : paths) {
			
			if(path.indexOf(".") == -1) {		// If this path is a root
				PlayerData pd = new PlayerData();
				try {
					pd.name = path;
					String worldName = playersYaml.getString("players." + path + ".lastNormalWorld");
					
					int x = playersYaml.getInt("players." + path + ".lastWorldLocation.x");
					int y = playersYaml.getInt("players." + path + ".lastWorldLocation.y");
					int z = playersYaml.getInt("players." + path + ".lastWorldLocation.z");
					pd.lastWorldLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
					
					String inv = playersYaml.getString("players." + path + ".lastWorldInventory");
					pd.lastWorldInventory = InventoryStringDeSerializer.StringToInventory(inv);
				} catch(Exception e) { e.printStackTrace(); }
				players.put(path, pd);
				plugin.getLog().info("Initializing Player Data for " + pd.name);
			}
		}
	}

//-----------------------------------------------------------------------------	
	
	public Location getPlayerNormalWorldLocation(Player player) {
		PlayerData pd = players.get(player.getName());
		if(pd != null)
			return pd.lastWorldLocation;
		return null;
	}

//-----------------------------------------------------------------------------	
	
	public void onPlayerStartDream(Player player, Location prior) {
		savePlayerLocation(player, prior);
		savePlayerInventory(player);
	}

//-----------------------------------------------------------------------------	
	
	public void onPlayerStartBuild(Player player, Location priorLocation) {
		savePlayerLocation(player, priorLocation);
		savePlayerInventory(player);
	}

//-----------------------------------------------------------------------------	
	
	public void onPlayerLeaveDreamWorld(Player player) {
		
	}

//-----------------------------------------------------------------------------	
	
	public void onPlayerLeaveBuildWorld(Player player) {
		
	}

//-----------------------------------------------------------------------------	
	
	public void onPlayerJoinServer(Player player) {
		newPlayer(player);
	}

//-----------------------------------------------------------------------------	
	
	public void savePlayerInventory(Player player) {
		PlayerData pd = players.get(player.getName());
		pd.lastWorldInventory = InventoryHandler.savePlayerInventory(player);
		plugin.getLog().info("Saving " + player.getName() + "'s inventory: " + InventoryStringDeSerializer.InventoryToString(pd.lastWorldInventory));
	}
	
//-----------------------------------------------------------------------------	
	
	public void savePlayerLocation(Player player, Location location) {
		PlayerData pd = players.get(player.getName());
		pd.lastWorldLocation = location;
	}

//-----------------------------------------------------------------------------	

	public boolean IsWorldDreamWorld(String worldName) {
		WorldData wd = worlds.get(worldName);
		if(wd == null)
			return false;
		return wd.worldType == WorldType.Dream;
	}

//-----------------------------------------------------------------------------	
	
	public boolean IsWorldBuildWorld(String worldName) {
		WorldData wd = worlds.get(worldName);
		if(wd == null)
			return false;
		return wd.worldType == WorldType.Build;
	}
	
//-----------------------------------------------------------------------------	

	public boolean IsPlayerWorldOwner(String worldName, Player player) {
		WorldData wd = worlds.get(worldName);
		if(wd == null)
			return false;
		return wd.ownerName.equals(player.getName());
	}

//-----------------------------------------------------------------------------
	
	public void onDisable() {
		saveAll();
	}

	public List<WorldData> getBuildWorldsOwnedByPlayer(Player player) {
		List<WorldData> worldNames = new ArrayList<WorldData>();
		
		for(String key : worlds.keySet()) {
			WorldData wd = worlds.get(key);
			if(wd.ownerName.equals(player.getName()) && wd.worldType == WorldType.Build)
				worldNames.add(wd);
		}
		
		return worldNames;
	}

	public List<WorldData> getDreamWorldsByPlayer(Player player) {
		List<WorldData> worldNames = new ArrayList<WorldData>();
		
		for(String key : worlds.keySet()) {
			WorldData wd = worlds.get(key);
			if(wd.ownerName.equals(player.getName()) && wd.worldType == WorldType.Dream)
				worldNames.add(wd);
		}
		
		return worldNames;
	}

	public List<WorldData> getAllBuildWorlds() {
		List<WorldData> worldNames = new ArrayList<WorldData>();
		
		for(String key : worlds.keySet()) {
			WorldData wd = worlds.get(key);
			if(wd.worldType == WorldType.Build)
				worldNames.add(wd);
		}
		
		return worldNames;
	}
	
	public List<WorldData> getAllDreamWorlds() {
		List<WorldData> worldNames = new ArrayList<WorldData>();
		
		for(String key : worlds.keySet()) {
			WorldData wd = worlds.get(key);
			if(wd.worldType == WorldType.Dream)
				worldNames.add(wd);
		}
		
		return worldNames;
	}

	public void deleteWorld(String worldName) {
		plugin.getLog().info("Deleting world data " + worldName);
		if(worlds.containsKey(worldName))
			worlds.remove(worldName);
	}

	public WorldData getWorldDataByName(String worldName) {
		return worlds.get(worldName);
	}

	public void setBuildWorldEndPoint(String worldName, Location location) {
		WorldData worldData = worlds.get(worldName);
		if(worldData == null)
			return;
		
		worldData.endpoint = location;
		plugin.getLog().info("End point set for " + worldName + ": " + location.toString());
		saveAll();
	}

	public PlayerData getPlayerData(Player player) {
		return players.get(player.getName());
	}
	
}
