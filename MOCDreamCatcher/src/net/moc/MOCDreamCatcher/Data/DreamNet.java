package net.moc.MOCDreamCatcher.Data;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ru.tehkode.permissions.bukkit.PermissionsEx;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.Data.DreamPlayer.PlayerState;
import net.moc.MOCDreamCatcher.External.ExternalPluginManager;
import net.moc.MOCDreamCatcher.World.DreamWorldManager;

public class DreamNet {
	//====================================================================
	MOCDreamCatcher plugin;
	private ArrayList<DreamPlayer> dreamPlayers;
	
	private DreamWorldManager worldManger; public DreamWorldManager getDreamWorldManager() { return worldManger; }
	private ExternalPluginManager pluginManager; public ExternalPluginManager getExternalPluginManager() { return pluginManager; }

	//====================================================================
	public DreamNet(MOCDreamCatcher plugin) {
		this.plugin = plugin;
		
		//Plugin Manager
		pluginManager = new ExternalPluginManager(plugin);
		if (!pluginManager.pluginsPresent()) { plugin.getServer().getPluginManager().disablePlugin(plugin); return; }
		
		//World Manager
		worldManger = new DreamWorldManager(plugin, pluginManager);
		
		loadDreamPlayers();
		
	}

	//====================================================================
	private void loadDreamPlayers() { dreamPlayers = plugin.getConfiguration().getDreamPlayers(); }

	//====================================================================
	public void wakeUpCall() {
		//Wake up dream players
		for (DreamPlayer dp : dreamPlayers) {
			if (dp.getState() == PlayerState.DREAMING || dp.getState() == PlayerState.EDITING) {
				dp.setState(PlayerState.WAKING);
				Player player = plugin.getServer().getPlayer(dp.getName());
				if (player != null) removePlayerPermissions(player, player.getWorld().getName());
				
			}
			
		}
		
		//Unload thoughts and delete dreams
		for (World w : plugin.getServer().getWorlds()) {
			//Check if it's a thought
			if (w.getName().contains(worldManger.getThoughtPrefix())) {
				pluginManager.unloadWorldData(w.getName());
				
				String[] parts = w.getName().split("_");
				String thoughtName = parts[parts.length - 1];
				String userName = w.getName();
				userName = userName.substring(userName.indexOf("_") + 1, userName.lastIndexOf("_"));
				
				worldManger.unloadThought(thoughtName, userName);
				
			}
			
			//Check if it's a dream
			else if (w.getName().contains(worldManger.getDreamPrefix())) {
				pluginManager.deleteWorldData(w.getName());
				
				String[] parts = w.getName().split("_");
				String dreamName = parts[parts.length - 1];
				String userName = w.getName();
				userName = userName.substring(userName.indexOf("_") + 1, userName.lastIndexOf("_"));
				
				worldManger.deleteDream(dreamName, userName);
				
			}
			
		}
		
	}
	
	//====================================================================
	public ArrayList<DreamPlayer> getPlayers() { return dreamPlayers; }
	public DreamPlayer getPlayer(String playerName) { for (DreamPlayer dp : dreamPlayers) { if (dp.getName().equalsIgnoreCase(playerName)) return dp; } return null; }

	//====================================================================
	public DreamPlayer addPlayer(String playerName) {
		//Return if already exists
		if (getPlayer(playerName) != null) return getPlayer(playerName);
		
		//Create new dream player
		DreamPlayer dp = new DreamPlayer(playerName, plugin);
		
		//Add to dream players
		dreamPlayers.add(dp);
		
		//Save dream player to config file
		plugin.getConfiguration().setDreamPlayer(dp);
		
		return dp;
		
	}
	
	//====================================================================
	public void startDream(String playerName, String dreamName, String dreamAuthorName) {
		//Return if player is not found online
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) dp = addPlayer(playerName);
		
		//Get author dream player
		DreamPlayer authorDP = getPlayer(dreamAuthorName);
		if (authorDP == null) return;

		//Get base thought
		Thought baseThought = getPlayer(dreamAuthorName).getThought(dreamName);
		if (baseThought == null) return;
		
		//All in place, create dream world
		if (worldManger.createDream(dreamName, playerName, dreamAuthorName)) {
			//Get world
			World dreamWorld = plugin.getServer().getWorld(worldManger.generateDreamName(dreamName, playerName));
			if (dreamWorld == null) return;
			
			//Copy world data
			pluginManager.loadWorldData(worldManger.generateThoughtName(dreamName, dreamAuthorName));
			pluginManager.copyWorldData(worldManger.generateThoughtName(dreamName, dreamAuthorName), dreamWorld.getName());
			
			//Save player wake info and update state
			dp.setDream(new Dream(dreamName, dreamAuthorName));
			dp.setWakeInformation(player.getLocation(), player.getInventory());
			dp.setState(PlayerState.DREAMING);
			
			//Teleport player to the dream world
			if (baseThought.getStart() == null) queueTeleportPlayer(player, dreamWorld.getSpawnLocation());
			else {
				Location loc = baseThought.getStart();
				loc.setWorld(dreamWorld);
				queueTeleportPlayer(player, loc);
				
			}
			
			//Update player's inventory
			queueSetInventory(player, baseThought.getInventory());
			
			//Add to dreamers
			plugin.getListener().addDreamer(player, new Dreamer(dp, baseThought));
			
			//Record thought visit
			plugin.getConfiguration().recordThoughtVisit(dreamAuthorName, dreamName, playerName);
			
			
		}
		
	}
	
	//===========================================================================
	public void endDream(String playerName) {
		//Get player
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) { dp = addPlayer(playerName); return; }
		
		//Teleport player back to the old location
		if (dp.getWakeLocation() != null) teleportPlayer(player, dp.getWakeLocation());
		
		//Update inventory
		if (dp.getWakeInventory() != null) player.getInventory().setContents(dp.getWakeInventory().getContents());
		
		//Remove dream world
		pluginManager.deleteWorldData(worldManger.generateDreamName(dp.getDream().getDreamThought(), playerName));
		worldManger.deleteDream(dp.getDream().getDreamThought(), playerName);
		
		//Update saved statistics
		dp.setWakeInformation(null, null);
		dp.setState(PlayerState.AWAKE);
		dp.clearDream();
		
		//Update configuration file
		plugin.getConfiguration().setDreamPlayer(dp);
		
		//Remove from dreamers
		plugin.getListener().removeDreamer(player);
		
	}

	//====================================================================
	public void createThought(String playerName, String thoughtName) {
		//Get player
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) dp = addPlayer(playerName);
		
		//Create thought world
		if (!worldManger.createThought(thoughtName, playerName)) return;
		
		//Create thought
		Thought thought = new Thought(thoughtName, playerName);
		
		//Add thought to the dream player
		dp.addThought(thought);
		
		//Record creation date
		plugin.getConfiguration().recordThoughtCreationDate(playerName, thoughtName);
		
		//Start thought
		startThought(playerName, thoughtName);
		
	}
	
	//====================================================================
	public void startThought(String playerName, String thoughtName) {
		//Get player
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) { dp = addPlayer(playerName); return; }
		
		//Get thought
		Thought thought = dp.getThought(thoughtName);
		if (thought == null) return;
		
		if (worldManger.loadThought(thoughtName, playerName)) {
			//Get world
			World thoughtWorld = plugin.getServer().getWorld(worldManger.generateThoughtName(thoughtName, playerName));
			if (thoughtWorld == null) return;
			
			//Load world plugin data
			pluginManager.loadWorldData(thoughtWorld.getName());
			
			//Save player wake info and update state
			dp.setWakeInformation(player.getLocation(), player.getInventory());
			dp.setState(PlayerState.EDITING);
			
			//Teleport player to the dream world
			if (thought.getStart() == null) queueTeleportPlayer(player, thoughtWorld.getSpawnLocation());
			else {
				Location loc = thought.getStart();
				loc.setWorld(thoughtWorld);
				queueTeleportPlayer(player, loc);
				
			}
			
			//Update player's inventory
			queueSetInventory(player, thought.getInventory());
			
			//Add permissions
			addPlayerPermissions(player, thoughtWorld.getName());
			
			//Record edit date
			plugin.getConfiguration().recordThoughtLastEditDate(playerName, thoughtName);
			
		}
		
	}

	//===========================================================================
	public void endThought(String playerName, String thoughtName) {
		//Get player
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) { dp = addPlayer(playerName); return; }
		
		//Teleport player back to the old location
		if (dp.getWakeLocation() != null) teleportPlayer(player, dp.getWakeLocation());
		
		//Update inventory
		if (dp.getWakeInventory() != null) player.getInventory().setContents(dp.getWakeInventory().getContents());
		
		//Remove permissions
		removePlayerPermissions(player, worldManger.generateThoughtName(thoughtName, playerName));
		
		//Remove dream world
		pluginManager.unloadWorldData(worldManger.generateThoughtName(thoughtName, playerName));
		worldManger.unloadThought(thoughtName, playerName);
		
		//Update saved statistics
		dp.setWakeInformation(null, null);
		dp.setState(PlayerState.AWAKE);
		
		//Update configuration file
		plugin.getConfiguration().setDreamPlayer(dp);
		
	}

	//===========================================================================
	public void endThought(String playerName) {
		//Get player
		Player player = plugin.getServer().getPlayer(playerName);
		if (player == null) return;
		
		//Get dream player
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) { dp = addPlayer(playerName); return; }
		
		//Teleport player back to the old location
		if (dp.getWakeLocation() != null) teleportPlayer(player, dp.getWakeLocation());
		
		//Update inventory
		if (dp.getWakeInventory() != null) player.getInventory().setContents(dp.getWakeInventory().getContents());
		
		//Update saved statistics
		dp.setWakeInformation(null, null);
		dp.setState(PlayerState.AWAKE);
		
		//Update configuration file
		plugin.getConfiguration().setDreamPlayer(dp);
		
	}

	//===========================================================================
	public void removeThought(String playerName, String thoughtName) {
		DreamPlayer dp = getPlayer(playerName);
		if (dp == null) return;
		
		Thought thought = dp.getThought(thoughtName);
		if (thought == null) return;
		
		worldManger.loadThought(thoughtName, playerName);
		pluginManager.deleteWorldData(worldManger.generateThoughtName(thoughtName, playerName));
		worldManger.deleteThought(thoughtName, playerName);
		
		dp.removeThought(thoughtName);
				
	}

	//===========================================================================
	private void queueSetInventory(Player player, Inventory inventory) {
		final Inventory inv = inventory;
		final String playerName = player.getName();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Player p = Bukkit.getServer().getPlayer(playerName);
				
				if (p == null) return;
				
				if (inv != null) p.getInventory().setContents(inv.getContents());
				else p.getInventory().clear();
				
			}
			
		}, 60L);
		
	}

	private void queueTeleportPlayer(Player player, Location location) {
		final String playerName = player.getName();
		final Location loc = location.clone();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Player p = Bukkit.getServer().getPlayer(playerName);
				if (p != null) p.teleport(loc);
				
			}
			
		}, 40L);
		
	}
	
	private void teleportPlayer(Player player, Location location) {
		player.teleport(location);
		
	}
	
	//===========================================================================
	private void addPlayerPermissions(Player player, String world) {
		for (String permission : plugin.getConfiguration().getDefaultWorldPermissions())
			PermissionsEx.getPermissionManager().getUser(player).addPermission(permission, world);
		
	}

	private void removePlayerPermissions(Player player, String world) {
		for (String permission : plugin.getConfiguration().getDefaultWorldPermissions())
			PermissionsEx.getPermissionManager().getUser(player).removePermission(permission, world);
		
	}

	//===========================================================================
	public void debug(String message) { plugin.getLog().info(message + " " + this); }

}
