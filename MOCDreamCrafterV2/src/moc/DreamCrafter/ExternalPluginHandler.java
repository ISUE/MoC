package moc.DreamCrafter;

import java.io.File;
import moc.DreamCrafter.util.YAMLHelper;
import moc.MOCFizziks.MOCFizziks;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExternalPluginHandler {
	MOCDreamCrafter plugin;
	public MultiverseCore multiverseCore;
	public WorldGuardPlugin worldGuard;
	public PermissionsEx permissionsEx;
	public CommandBook commandBook;
	
	private MOCFizziks mocFizziks = null;
	
	public ExternalPluginHandler(MOCDreamCrafter p) {
		plugin = p;
	}
	
	public void onDreamLoad(String buildWorldName, String dreamWorldName) {
		CopyVariableTriggers(buildWorldName, dreamWorldName);
		CopyKiosks(buildWorldName, dreamWorldName);
//		CopyCitizens(buildWorldName, dreamWorldName);
		CopyMOCFizziks(buildWorldName, dreamWorldName);
	}
	
	public void onDreamDelete(String dreamWorldName) {
		DeleteMOCFizziks(dreamWorldName);
	}
	
	private void DeleteMOCFizziks(String dreamWorldName) {
		if(mocFizziks == null)
			return;
		mocFizziks.getAPI().removeWorld(dreamWorldName);
	}

	/**
	 * Copies the VariableTrigger configuration of the origin world to the dream world  
	 * @param buildWorldName	Name of origin world
	 * @param dreamWorldName	Name of dream world
	 */
	public void CopyVariableTriggers(String buildWorldName, String dreamWorldName) {
		plugin.getLog().info(" Copying Variable Triggers for world " + dreamWorldName);
		
		String baseDir = "plugins/VariableTriggers/";
		String[] triggerFiles = {"AreaTriggers", "ClickTriggers", "CommandTriggers", "EventTriggers", "WalkTriggers" };
		
		// Foreach trigger file
		for(int i = 0; i < triggerFiles.length; i++) {
			File f = new File(baseDir + triggerFiles[i] + ".yml");
			
			if(f.exists()) {
				YamlConfiguration triggerYaml = YAMLHelper.loadYAML(f);				
				YAMLHelper.copyNode(triggerYaml, buildWorldName, dreamWorldName);
				YAMLHelper.saveYaml(triggerYaml, f);
			}
		}
		
		// And finally, reload the Variable Trigger plugin to make the changes live
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vtrigger reloadtriggers");
	}
	
	public void CopyCitizens(String buildWorldName, String dreamWorldName) {
		String baseDir = "C:\\Users\\Chris\\Documents\\Research\\Minds of Chimera\\Eclipse\\Citizens\\";
		
		//this.plugin.log.info(MOCDreamCrafter.namee+" Copying Citizens for world " + dreamWorldName);
		
		//String baseDir = "plugins/Citizens/";
		String[] triggerFiles = { "saves" };
		String prefix = "100";
		
		// Foreach trigger file
		for(int i = 0; i < triggerFiles.length; i++) {
			File f = new File(baseDir + triggerFiles[i] + ".yml");
			
			if(f.exists()) {
				YamlConfiguration triggerYaml = YAMLHelper.loadYAML(f);
				
				for(String rootPath : triggerYaml.getKeys(true)) {
					String world = (String) triggerYaml.get(rootPath + ".traits.location.world");
					
					// If this is true, rootPath is the root and this path contains data
					// for the originWorld
					if(world != null && world.equals(buildWorldName)) {
						String npcNum = rootPath.split("[.]")[1];
						String newNodePath = "npc." + prefix + npcNum;
						
						// Copy the node for the new world
						YAMLHelper.copyNode(triggerYaml, rootPath, newNodePath);
						
						// Change the world name to the new one
						triggerYaml.set(newNodePath + ".traits.location.world", dreamWorldName);
					}
				}
				YAMLHelper.saveYaml(triggerYaml, new File(baseDir + "saves2.yml"));
			}
		}
		
		
		// And finally, reload the Variable Trigger plugin to make the changes live
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vtrigger reloadtriggers");
	}
	
	public void CopyKiosks(String originWorldName, String dreamWorldName) {
		/*if(this.plugin.kioskplugin == null) {
			this.plugin.ELOG("MOCKiosk missing");
			return;
		}
		
		this.plugin.kioskplugin.copyWorldKiosks(originWorldName, dreamWorldName);*/
	}
	
	public void CopyMOCFizziks(String buildWorldName, String dreamWorldName) {
		if(mocFizziks == null)
			return;
		
		if(!mocFizziks.getAPI().copyWorld(Bukkit.getWorld(dreamWorldName), Bukkit.getWorld(buildWorldName)))
			plugin.getLog().info("MocFizziks unable to copyWorld for " + dreamWorldName + ", " + buildWorldName);
	}

	
	/* For local testing
	 */
	public static void main(String[] args) {
		ExternalPluginHandler eph = new ExternalPluginHandler(null);
		eph.onDreamLoad("Leocius", "Leocius2");
	}

	public boolean loadExternalPlugins() {
		PluginManager pm = plugin.getServer().getPluginManager();
		multiverseCore = (MultiverseCore) pm.getPlugin("Multiverse-Core");
		worldGuard = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
		permissionsEx = (PermissionsEx) pm.getPlugin("PermissionsEx");
		commandBook = (CommandBook) pm.getPlugin("CommandBook");
		mocFizziks = (MOCFizziks) pm.getPlugin("MOCFizziks");
		
		if(multiverseCore == null) {
			plugin.getLog().info("Missing Multiverse plugin. Required for DreamCrafter - please install.");
			return false;
		}
		
		if(worldGuard == null) {
			plugin.getLog().info("Missing WorldGuard plugin. Required for DreamCrafter - please install.");
			return false;
		}
		
		if(permissionsEx == null) {
			plugin.getLog().info("Missing PermissionsEx plugin. Required for DreamCrafter - please install.");
			return false;
		}
		
		if(commandBook == null) {
			plugin.getLog().info("Missing CommandBook plugin. Required for DreamCrafter - please install.");
			return false;
		}
		
		if(mocFizziks == null) {
			plugin.getLog().info("Missing MOCFizziks plugin. Optional.");
		}
		
		return true;
	}
}
