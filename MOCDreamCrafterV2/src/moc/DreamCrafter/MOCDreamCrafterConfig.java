package moc.DreamCrafter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MOCDreamCrafterConfig {
	private YamlConfiguration config;
	private File configFile;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	
	private String pathWorldCost = "dream.worldCost";
	private String pathMaxWorlds = "dream.maxWorlds";
	private String pathAllowUserCreate = "dream.allowUserCreate";
	private String pathWorldSaveTime = "dream.worldSaveTime";
	private String pathWorldPermissions = "dream.worldPermissions";
	
	public MOCDreamCrafterConfig(File configFile) {
		this.configFile = configFile;
		this.config = new YamlConfiguration();
		
		//Some default settings
		configDefaultsHash.put(pathWorldCost, 10000);
		configDefaultsHash.put(pathMaxWorlds, 1);
		configDefaultsHash.put(pathAllowUserCreate, true);
		configDefaultsHash.put(pathWorldSaveTime, 300);
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
	
	//---------------------------------------------------------------------------------------------
	public void save() { try { config.save(configFile); } catch (IOException e) { e.printStackTrace(); } }
	private void load() { try { config.load(configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	public void reload() {
		//Check if configuration file exists
		if (configFile.exists()) {
			//If does, load it
			load();

			if (config.getString(pathWorldCost) == null) { config.set(pathWorldCost, configDefaultsHash.get(pathWorldCost)); }
			if (config.getString(pathMaxWorlds) == null) { config.set(pathMaxWorlds, configDefaultsHash.get(pathMaxWorlds)); }
			if (config.getString(pathAllowUserCreate) == null) { config.set(pathAllowUserCreate, configDefaultsHash.get(pathAllowUserCreate)); }
			if (config.getString(pathWorldSaveTime) == null) { config.set(pathWorldSaveTime, configDefaultsHash.get(pathWorldSaveTime)); }
			if (config.getString(pathWorldPermissions) == null) { config.set(pathWorldPermissions, configDefaultsHash.get(pathWorldPermissions)); }
			
			save();
			
		} else {
			//Otherwise create and populate default values
			for (String key : configDefaultsHash.keySet()) { config.set(key, configDefaultsHash.get(key)); }

			save();
			
		}
		
	}

	//---------------------------------------------------------------------------------------------
	public int getBuildWorldCost() { return config.getInt(pathWorldCost, 10000); }
	public int getMaxDreamsPerPerson() { return config.getInt(pathMaxWorlds, 1); }
	public boolean allowUserCreate() {return config.getBoolean(pathAllowUserCreate, false); }
	public int getPeriodicSaveTime() { return config.getInt(pathWorldSaveTime, 300); }
	public List<String> getDefaultWorldPermissions() { return config.getStringList(pathWorldPermissions); }
	
}
