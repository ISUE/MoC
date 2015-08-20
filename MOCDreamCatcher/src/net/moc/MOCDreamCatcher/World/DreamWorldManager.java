package net.moc.MOCDreamCatcher.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.External.ExternalPluginManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class DreamWorldManager {
	//========================================================
	private MOCDreamCatcher plugin;
	private ExternalPluginManager pluginManager;
	private MultiverseCore multiverse;
	
	private String thoughtPrefix = "thought"; public String getThoughtPrefix() { return thoughtPrefix; }
	private String dreamPrefix = "dream"; public String getDreamPrefix() { return dreamPrefix; }
	
	private Environment enviroment = Environment.NORMAL;
	private WorldType worldType = WorldType.NORMAL;
	private String seed = "0";
	private String generator = "MOCDreamCatcher";
	
	//========================================================
	public DreamWorldManager(MOCDreamCatcher plugin, ExternalPluginManager pluginManager) {
		this.plugin = plugin;
		this.pluginManager = pluginManager;
		multiverse = pluginManager.getMultiverse();
		
	}
	
	//========================================================
	public String generateThoughtName(String thoughtName, String playerName) { return thoughtPrefix + "_" + playerName + "_" + thoughtName; }
	public String generateDreamName(String thoughtName, String playerName) { return dreamPrefix + "_" + playerName + "_" + thoughtName; }

	
	
	
	//*********************************************************************************************************************************
	//========================================================
	private boolean createWorld(String worldName) {
		//Check if world is already present
		if (multiverse.getMVWorldManager().isMVWorld(worldName) || plugin.getServer().getWorld(worldName) != null) return false;
		
		//Attempt to create a world
		boolean retval = multiverse.getMVWorldManager().addWorld(worldName, enviroment, seed, worldType, false, generator);
		
		MultiverseWorld world = multiverse.getMVWorldManager().getMVWorld(worldName);
		if (world != null && retval) {
			world.setAllowMonsterSpawn(false);
			world.setEnableWeather(false);
			Bukkit.setSpawnRadius(0);
		}
		
		return retval; 
		
	}
	
	//========================================================
	private boolean loadWorld(String worldName) { return multiverse.getMVWorldManager().loadWorld(worldName); }

	//========================================================
	private boolean unloadWorld(String worldName) { return multiverse.getMVWorldManager().unloadWorld(worldName); }

	//========================================================
	private boolean deleteWorld(String worldName) { loadWorld(worldName); return multiverse.getMVWorldManager().deleteWorld(worldName); }
	
	//*********************************************************************************************************************************
	
	
	
	//*********************************************************************************************************************************
	//========================================================
	public boolean createThought(String thoughtName, String playerName) {
		//Generate world name
		String worldName = generateThoughtName(thoughtName, playerName);
		
		//Create thought
		if (!createWorld(worldName)) return false;
		
		multiverse.getMVWorldManager().getMVWorld(worldName).setGameMode("creative");
		
		return true;
		
	}

	//========================================================
	public boolean loadThought(String thoughtName, String playerName) {
		//Generate full name
		String worldName = generateThoughtName(thoughtName, playerName);
		
		//Load it if needed
		if (!loadWorld(worldName)) return false;
		
		return true;
		
	}
	
	//========================================================
	public boolean unloadThought(String thoughtName, String playerName) { return unloadWorld(generateThoughtName(thoughtName, playerName)); }

	//========================================================
	public boolean deleteThought(String thoughtName, String playerName) { return deleteWorld(generateThoughtName(thoughtName, playerName)); }
	
	//*********************************************************************************************************************************


	
	//*********************************************************************************************************************************
	//========================================================
	public boolean createDream(String thoughtName, String playerName, String authorName) {
		//Generate full name
		String thoughtWorldName = generateThoughtName(thoughtName, authorName);
		String dreamWorldName = generateDreamName(thoughtName, playerName);
		
		//Load thought
		if (!loadWorld(thoughtWorldName)) return false;
		plugin.getServer().getWorld(thoughtWorldName).save();
		
		//Check if dream world is already online and remove it
		if (loadWorld(dreamWorldName)) deleteWorld(dreamWorldName);
		
		//Attempt to create a dream world
		if (!copyWorld(thoughtWorldName, dreamWorldName)) return false;
		
		//Add newly copied world to Multiverse
		if (!createWorld(dreamWorldName)) return false;
		
		return true;
		
	}

	//========================================================
	public boolean unloadDream(String thoughtName, String playerName) { return unloadWorld(generateDreamName(thoughtName, playerName)); }
	
	//========================================================
	public boolean deleteDream(String thoughtName, String playerName) { return deleteWorld(generateDreamName(thoughtName, playerName));	}
	
	//*********************************************************************************************************************************
	

	
	//========================================================
	public boolean copyWorld(String worldName, String duplicateWorldName) {
		// Copy the world files
		File dir = new File(duplicateWorldName);
		if (!dir.exists()) {
			try { FileUtils.copyDirectory(new File(worldName), new File(duplicateWorldName)); }
			catch (IOException e) { e.printStackTrace(); return false; }

			(new File(duplicateWorldName + "/uid.dat")).delete();
			
			//TODO
			//try { FileUtils.deleteDirectory(new File(duplicateWorldName + "/spout_meta")); }
			//catch (IOException e) {}
			//TODO
			
		}
			
		// Create WorldGuard files
		File wgdata, wf, dest;
		FileWriter fw;
		wgdata = pluginManager.getWorldGuard().getDataFolder();

		// Create config.yml
		wf = new File(wgdata.getPath() + File.separator + "worlds" + File.separator + duplicateWorldName + File.separator + "config.yml");
		wf.getParentFile().mkdirs(); // make sure directories exist
		
		try {
			fw = new FileWriter(wf);
			fw.write("{}");
			fw.close();
		} catch (IOException e) { e.printStackTrace(); return false; }

		// copy over regions.yml from existing world
		wf = new File(wgdata.getPath() + File.separator + "worlds" + File.separator + worldName + File.separator + "regions.yml");
		dest = new File(wgdata.getPath() + File.separator + "worlds" + File.separator + duplicateWorldName + File.separator + "regions.yml");

		if (!dest.exists()) {
			try {
				dest.createNewFile();
				FileChannel in = new FileInputStream(wf).getChannel();
				FileChannel out = new FileOutputStream(dest).getChannel();
				out.transferFrom(in, 0, in.size());
				in.close();
				out.close();
			} catch (IOException e) { e.printStackTrace(); return false; }
			
		}

		pluginManager.getWorldGuard().reloadConfig();

		return true;
		
	}
	
}
