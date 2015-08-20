package moc.DreamCrafter.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import moc.DreamCrafter.ActionResult;
import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.InventoryHandler;
import moc.DreamCrafter.data.PlayerData;
import moc.DreamCrafter.data.WorldData;

public class WorldHandler {
	MOCDreamCrafter plugin;
	
	public WorldHandler(MOCDreamCrafter plugin) { this.plugin = plugin; }

	public ActionResult CreateBuildWorld(String worldName, Player player) {
		ActionResult actionResult = new ActionResult();
		
		// Does World exist?
		if(WorldHandler.Exists(worldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = worldName + " has already been created.";
			return actionResult;
		}
		
		// Is it already loaded?
		if (plugin.getServer().getWorld(worldName) != null) {
			actionResult.Success = false;
			actionResult.PlayerMessage = worldName + " is already loaded.";
			return actionResult;
		}
		
		// TODO: Check if isMVWorld
		
		// Can the player create another?
		List<WorldData> worlds = plugin.getPersistentDataHandler().getBuildWorldsOwnedByPlayer(player);
		if(worlds.size() >= plugin.getConfiguration().getMaxDreamsPerPerson()) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You already own the max number of build worlds.";
			return actionResult;
		}
		
		// TODO: Check if player has enough money
		
		if (plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().addWorld(worldName,Environment.NORMAL, "0", WorldType.FLAT, false,"")) {
			MultiverseWorld mvw = plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().getMVWorld(worldName);

			if (mvw == null) {
				actionResult.Success = false;
				actionResult.PlayerMessage = "Can't find world yet.";
				return actionResult;
			}
			else {
				mvw.setKeepSpawnInMemory(false);
				mvw.setGameMode("creative");
			}
		} else {
			actionResult.Success = false;
			plugin.getLog().info("MVC could not create world");
			return actionResult;
		}
		
		plugin.getPersistentDataHandler().newBuildWorld(worldName, player.getName());
				
		// Add permissions
		for(String permission : plugin.getConfiguration().getDefaultWorldPermissions()) 
			PermissionsEx.getUser(player.getName()).addPermission(permission, permission);
		
		plugin.getLog().info(player.getName() + " created new build world '" + worldName + "'");

		actionResult.Success = true;
		actionResult.PlayerMessage = worldName + " created.";
		return actionResult;
	}
	
//-----------------------------------------------------------------------------	
	
	public ActionResult CreateBuildWorldFromExistingWorld(String existingWorldName, String newWorldName, Player player) {
		ActionResult actionResult = new ActionResult();
		
		// Does World exist?
		if(!WorldHandler.Exists(existingWorldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = existingWorldName + " does not exist.";
			return actionResult;
		}
		
		if(WorldHandler.Exists(newWorldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = newWorldName + " already exists.";
			return actionResult;
		}
		
		// Can the player build another?
		List<WorldData> worlds = plugin.getPersistentDataHandler().getBuildWorldsOwnedByPlayer(player);
		if(worlds.size() >= plugin.getConfiguration().getMaxDreamsPerPerson()) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You already own the max number of build worlds.";
			return actionResult;
		}
		
		// TODO: Check if player has enough money
		
		if(!DuplicateWorld( existingWorldName, newWorldName )) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Couldn't copy existing world";
			return actionResult;
		}
		
		// Load the world
		if(!ImportWorld(newWorldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Unable to import " + newWorldName;
			return actionResult;
		}
		
		plugin.getPersistentDataHandler().newBuildWorld(newWorldName, player.getName());
				
		// Add permissions
		for(String permission : plugin.getConfiguration().getDefaultWorldPermissions()) 
			PermissionsEx.getUser(player.getName()).addPermission(permission, permission);
		
		plugin.getLog().info(player.getName() + " imported new build world '" + newWorldName + "'");

		actionResult.Success = true;
		actionResult.PlayerMessage = newWorldName + " imported.";
		return actionResult;
	}
	
//-----------------------------------------------------------------------------	
	
	public ActionResult DeleteWorld(String worldName, Player player) {
		ActionResult actionResult = new ActionResult();
		
		// Does Player own world?
		if(!IsPlayerWorldOwner(worldName, player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You are not the owner of " + worldName;
			return actionResult;
		}
		
		// Does World exist?
		if(!WorldHandler.Exists(worldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = worldName + " doesn't exist.";
			return actionResult;
		}
		
		if(!plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().deleteWorld(worldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Unable to delete " + worldName;
			return actionResult;
		}
		
		plugin.getPersistentDataHandler().deleteWorld(worldName);
		
		plugin.getLog().info(player.getName() + " deleted world '" + worldName + "'");
		
		actionResult.PlayerMessage = worldName + " deleted.";
		return actionResult;
	}
	
//-----------------------------------------------------------------------------	

	public ActionResult StartBuildingWorld(String worldName, Player player) {
		ActionResult actionResult = new ActionResult();
		
		// Does World exist?
		if(!WorldHandler.Exists(worldName)) {
			plugin.getLog().info("Attempt to build non-existent world");
			actionResult.Success = false;
			actionResult.PlayerMessage = worldName + " does not exist.";
			return actionResult;
		}

		// Is the player the owner?
		if(!IsPlayerWorldOwner(worldName, player)) {
			plugin.getLog().info("Attempt to build world not owned");
			actionResult.Success = false;
			actionResult.PlayerMessage = "You are not the owner of " + worldName;
			return actionResult;
		}
		
		// Is the player currently dreaming?
		if(IsPlayerInDreamWorld(player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You must leave the current dream before building a world";
			return actionResult;
		}
		
		// Is the player currently building?
		if(IsPlayerInBuildWorld(player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You must leave the current build world before building another";
			return actionResult;
		}
		
		// Load the world
		if(!LoadWorld(worldName)) {
			plugin.getLog().info("World can't be loaded");
			ImportWorld(worldName);
		}
		
		Location normalWorldLocation = player.getLocation();
		
		// Teleport player
		if(!TeleportPlayerToWorld(worldName, player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Unable to teleport to " + worldName;
			return actionResult;
		}
		
		plugin.getPersistentDataHandler().onPlayerStartBuild(player, normalWorldLocation);
		
		LoadWorldProperties(worldName, player);
		
		// Set to creative mode
		SetWorldGamemode(worldName, GameMode.CREATIVE);
		
		plugin.getLog().info(player.getName() + " starting build world '" + worldName + "'");
		
		return actionResult;
	}
	
//-----------------------------------------------------------------------------	
	
	public void SetWorldGamemode(String worldName, GameMode gameMode) {
		plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().getMVWorld(worldName).setGameMode(gameMode.toString().toLowerCase());
	}

	public ActionResult StartDreamingWorld(String worldName, Player player) {
		ActionResult actionResult = new ActionResult();
		actionResult.PlayerMessage = "success!";
		
		// Is the player currently dreaming?
		if(IsPlayerInDreamWorld(player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You must leave the current dream before starting another";
			return actionResult;
		}
		
		// Is the player currently building?
		if(IsPlayerInBuildWorld(player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "You must leave the current build world before starting a dream";
			return actionResult;
		}
		
		// Does Build World exist?
		if(!WorldHandler.Exists(worldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = worldName + " does not exist.";
			return actionResult;
		}
		
		// Make sure the build world is loaded. It doesn't work unless it is
		boolean loadWorld = LoadWorld(worldName);
		plugin.getLog().info("Dream load world " + loadWorld);
		
		String dreamWorldName = GenerateDreamWorldName(worldName, player.getName());
		
		if(!DuplicateWorld( worldName, dreamWorldName )) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Couldn't create dream world";
			return actionResult;
		}
		
		// Load the world
		if(!ImportWorld(dreamWorldName)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Unable to import dream for " + worldName;
			return actionResult;
		}
		
		Location priorLocation = player.getLocation();
		plugin.getPersistentDataHandler().onPlayerStartDream(player, priorLocation);
		plugin.getPersistentDataHandler().newDreamWorld(worldName, dreamWorldName, player.getName());
		
		// Teleport player
		if(!TeleportPlayerToWorld(dreamWorldName, player)) {
			actionResult.Success = false;
			actionResult.PlayerMessage = "Unable to teleport to " + dreamWorldName;
			plugin.getPersistentDataHandler().deleteWorld(dreamWorldName);
			return actionResult;
		}

//		plugin.persistentDataHandler.onPlayerStartDream(player, priorLocation);
//		plugin.persistentDataHandler.newDreamWorld(worldName, dreamWorldName, player.getName());
		
		plugin.getExternalPluginHandler().onDreamLoad(worldName, dreamWorldName);
		
		LoadWorldProperties(dreamWorldName, player);
		
		plugin.getLog().info(player.getName() + " started dream of world '" + worldName + "'");
		
		return actionResult;
	}
	
//-----------------------------------------------------------------------------	
	
	public boolean LoadWorld(String worldName) {
		plugin.getLog().info("LoadWorld("+worldName+")");
		return plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().loadWorld(worldName);
	}
	
//-----------------------------------------------------------------------------	
	
	public boolean ImportWorld(String worldName) {
		plugin.getLog().info("ImportWorld("+worldName+")");
		return plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().addWorld(
				worldName, Environment.NORMAL, "0", WorldType.FLAT, false, "");
	}
	
	public boolean UnloadWorld(String worldName) {
		if(plugin.getPersistentDataHandler().IsWorldDreamWorld(worldName)) {
			LoadWorld(worldName);
			return plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().deleteWorld(worldName);
		}
		else if(plugin.getPersistentDataHandler().IsWorldBuildWorld(worldName)) {
			return plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().unloadWorld(worldName);
		}
		plugin.getLog().info("Attempt to unload normal world: " + worldName);
		return false;
	}
	
//-----------------------------------------------------------------------------	
	
	public boolean TeleportPlayerToWorld(String worldName, Player player) {
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(worldName);
		World w = plugin.getServer().getWorld(worldName);		
		plugin.getLog().info(worldData.spawnpoint.getX() + " " + worldData.spawnpoint.getY());
		return player.teleport(new Location(w, worldData.spawnpoint.getX(), worldData.spawnpoint.getY(), worldData.spawnpoint.getZ()));
	}
		
//-----------------------------------------------------------------------------	

	public void ReturnPlayerToNormalWorld(Player player) {
		PlayerData playerData = plugin.getPersistentDataHandler().getPlayerData(player);
		if(playerData.lastWorldLocation != null) {
			player.teleport(playerData.lastWorldLocation);
			InventoryHandler.restorePlayerInventory(player, playerData.lastWorldInventory);
		}
		else
			plugin.getLog().info("Cant return " + player.getName() + " to NormalWorld, invalid location");
	}
		
//-----------------------------------------------------------------------------	

	public boolean IsPlayerInDreamWorld(Player player) {
		return plugin.getPersistentDataHandler().IsWorldDreamWorld( player.getWorld().getName() );
	}
		
//-----------------------------------------------------------------------------	

	public boolean IsPlayerInBuildWorld(Player player) {
		return plugin.getPersistentDataHandler().IsWorldBuildWorld( player.getWorld().getName() );
	}
		
//-----------------------------------------------------------------------------	

	public boolean IsPlayerWorldOwner(String worldName, Player player) {
		return plugin.getPersistentDataHandler().IsPlayerWorldOwner( worldName, player );
	}
		
//-----------------------------------------------------------------------------	

	public String GenerateDreamWorldName(String worldName, String playerName) {
		return "d_" + playerName + "_" + worldName;
	}
		
//-----------------------------------------------------------------------------
	
	public void LoadWorldProperties(String worldName, Player player) {
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(worldName);
		World world = plugin.getServer().getWorld(worldName);
		
		// Set thunder
		world.setThundering(worldData.isThunderEnabled);
		world.setThunderDuration(999999);
		
		// Set Weather
		world.setStorm(worldData.isStormyWeatherEnabled);
		world.setWeatherDuration(999999);
		
		// Set time of day
		player.setOp(true);
		player.performCommand("time -l " + worldData.time);
		player.setOp(false);
		
		// Set difficulty
		world.setDifficulty(worldData.difficulty);
		
		// Set gamemode using multiverse
		plugin.getExternalPluginHandler().multiverseCore.getMVWorldManager().getMVWorld(worldName).setGameMode(worldData.gameMode.toString().toLowerCase());
		
		// Set starting inventory
		player.getInventory().setContents(worldData.inventory.getContents());
		
		plugin.getLog().info("Setting world properties");
		plugin.getLog().info("thunder: " + worldData.isThunderEnabled);
		plugin.getLog().info("weather: " + worldData.isStormyWeatherEnabled);
		plugin.getLog().info("time: " + worldData.time);
		plugin.getLog().info("difficulty: " + worldData.difficulty);
		plugin.getLog().info("gamemode: " + worldData.gameMode);
		plugin.getLog().info("inventory: " + worldData.inventory);
		plugin.getLog().info(player.getWorld().getName());
	}
	
//-----------------------------------------------------------------------------	

	public boolean DuplicateWorld(String worldName, String duplicateWorldName) {
		plugin.getLog().info("DuplicateWorld("+worldName+", "+duplicateWorldName+")");
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(worldName);
		
		// Copy the world files
		File dir = new File(duplicateWorldName);
		if (!dir.exists()) {
			try {
				FileUtils.copyDirectory(new File(worldName), new File(duplicateWorldName));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			(new File(duplicateWorldName + "/uid.dat")).delete();
		}
			
		// Create WorldGuard files
		File wgdata, wf, dest;
		FileWriter fw;
		wgdata = plugin.getExternalPluginHandler().worldGuard.getDataFolder();

		// Create config.yml
		wf = new File(wgdata.getPath() + File.separator + "worlds"
				+ File.separator + duplicateWorldName + File.separator + "config.yml");
		plugin.getLog().info("creating config.yml in " + wf.getPath());
		wf.getParentFile().mkdirs(); // make sure directories exist
		try {
			fw = new FileWriter(wf);
			fw.write("{}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// copy over regions.yml from existing world
		wf = new File(wgdata.getPath() + File.separator + "worlds"
				+ File.separator + worldName + File.separator
				+ "regions.yml");
		dest = new File(wgdata.getPath() + File.separator + "worlds"
				+ File.separator + duplicateWorldName + File.separator + "regions.yml");
		plugin.getLog().info("copying regions.yml from " + wf.getPath() + " to " + dest.getPath());
		if (!dest.exists()) {
			try {
				dest.createNewFile();
				FileChannel in = new FileInputStream(wf).getChannel();
				FileChannel out = new FileOutputStream(dest).getChannel();
				out.transferFrom(in, 0, in.size());
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		// create the blacklist.txt file
		wf = new File(wgdata.getPath() + File.separator + "worlds"
				+ File.separator + duplicateWorldName + File.separator
				+ "blacklist.txt");
		plugin.getLog().info("creating blacklist.txt in " + wf.getPath());
		try {
			// TODO: Get block blacklist
			
			String bl = worldData.getCommaSeparatedBlacklist();
			plugin.getLog().info("Blacklist: " + bl);
			String s;

			fw = new FileWriter(wf);
			fw.write("# setup the allow/block structure\n");
			if (!bl.equalsIgnoreCase("")) {
				s = "["
						+ bl
						+ "]\non-break=deny\non-destroy-with=allow\non-place=deny\non-use=deny\non-interact=allow\non-drop=deny\non-acquire=deny\n";
				plugin.getLog().info("creating blacklist.txt with blacklist string '" + s+ "'");
				fw.write(s);
			}

			// TODO: Get block whitelist
			bl = "";//_owi.getBlockWhiteList();
			if (!bl.equalsIgnoreCase("")) {
				s = "["
						+ bl
						+ "]\non-break=allow\non-destroy-with=allow\non-place=allow\non-use=allow\non-interact=allow\non-drop=allow\non-acquire=allow\n";
				plugin.getLog().info("creating blacklist.txt with whitelist string '" + s + "'");
				fw.write(s);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
		
		plugin.getExternalPluginHandler().worldGuard.reloadConfig();
		
		return true;
	}

	
//-----------------------------------------------------------------------------	
	
	public static boolean Exists(String worldName) {
		return (new File(worldName)).exists();
	}

	public boolean isBlockEndPoint(Block block) {
		if(block == null)
			return false;
		
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(block.getWorld().getName());
		if(worldData == null)
			return false;
		if(worldData.endpoint == null)
			return false;
		
		return worldData.endpoint.getBlockX() == block.getX()
				&& worldData.endpoint.getBlockY() == block.getY()
				&&worldData.endpoint.getBlockZ() == block.getZ();
	}
	
}
