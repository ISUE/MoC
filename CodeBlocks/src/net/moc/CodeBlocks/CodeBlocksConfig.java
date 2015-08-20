package net.moc.CodeBlocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class CodeBlocksConfig {
	//========================================================================================
	private CodeBlocks plugin; public CodeBlocks getPlugin() { return plugin; }
	private String pluginPath; public String getPluginPath() { return pluginPath; }

	private String nameMain = "config.yml";
	private String nameDirectives = "directives.yml";
	private String nameDatabase = "database.yml";
	
	private File configMainFile;
	private File configDirectivesFile;
	private File configDatabaseFile;
	
	private YamlConfiguration configMain;
	private YamlConfiguration configDirectives;
	private YamlConfiguration configDatabase;
	//========================================================================================
	private String pathLastLog = "lastLogSubmit";
	private String pathEnableLog = "allowUseStatisticsSubmit";
	private String pathFeedback = "allowFeedbackSubmit";
	private String pathMaxHealth = "robot.maxHealth";
	private String pathMaxArmor = "robot.maxArmor";
	private String pathMaxPower = "robot.maxPower";
	private String pathMaxDamage = "robot.maxDamage";
	private String pathMaxStackSize = "function.maxStackSize";
	private String pathBlockPower = "blockPower";
	
	private String pathPowerCostBasic = "powerCost.basic";
	private String pathPowerCostMath = "powerCost.math";
	private String pathPowerCostInteraction = "powerCost.interaction";
	private String pathPowerCostMovement = "powerCost.movement";
	private String pathPowerCostAttack = "powerCost.attack";
	
	private String pathDirectiveExterminate = "exterminate";
	private String pathDirectiveVacuum = "vacuum";
	
	private String pathMySQL = "UseMySQL";
	private String pathHost = "host";
	private String pathUsername = "username";
	private String pathPassword = "password";
	private String pathDatabase = "database";
	private String pathPort = "port";
	//========================================================================================

	

	//========================================================================================
	public CodeBlocksConfig(CodeBlocks plugin) {
		this.plugin = plugin;
		
		pluginPath = plugin.getDataFolder().getAbsolutePath();
		
		configMainFile = new File(pluginPath + File.separator + nameMain);
    	configDirectivesFile = new File(pluginPath + File.separator + nameDirectives);
    	configDatabaseFile = new File(pluginPath + File.separator + nameDatabase);
    	
    	configMain = new YamlConfiguration();
    	configDirectives = new YamlConfiguration();
    	configDatabase = new YamlConfiguration();
    	
		//Load configurations if files exist
		if (configMainFile.exists()) this.loadMain();
		if (configDirectivesFile.exists()) this.loadDirectives();
		if (configDatabaseFile.exists()) this.loadDatabase();
		
		//Load defaults as needed
		loadDefaultsMain();
		loadDefaultsDirectives();
		loadDefaultsDatabase();
		
		//Save all configurations
		saveMain();
		saveDirectives();
		saveDatabase();
		
	}
	//========================================================================================
	
	
	
	//#############################################################################################
	public void saveMain() { try { this.configMain.save(this.configMainFile); } catch (IOException e) { e.printStackTrace(); } }
	public void loadMain() { try { this.configMain.load(this.configMainFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }

	public void saveDirectives() { try { this.configDirectives.save(this.configDirectivesFile); } catch (IOException e) { e.printStackTrace(); } }
	public void loadDirectives() { try { this.configDirectives.load(this.configDirectivesFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }

	public void saveDatabase() { try { this.configDatabase.save(this.configDatabaseFile); } catch (IOException e) { e.printStackTrace(); } }
	public void loadDatabase() { try { this.configDatabase.load(this.configDatabaseFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//#############################################################################################
	public void loadDefaultsMain() {
		if (configMain.getString(pathLastLog) == null) configMain.set(pathLastLog, 0);
		if (configMain.getString(pathEnableLog) == null) configMain.set(pathEnableLog, true);
		if (configMain.getString(pathFeedback) == null) configMain.set(pathFeedback, true);
		if (configMain.getString(pathMaxHealth) == null) configMain.set(pathMaxHealth, 100);
		if (configMain.getString(pathMaxArmor) == null) configMain.set(pathMaxArmor, 100);
		if (configMain.getString(pathMaxPower) == null) configMain.set(pathMaxPower, 10000);
		if (configMain.getString(pathMaxDamage) == null) configMain.set(pathMaxDamage, 15);
		if (configMain.getString(pathMaxStackSize) == null) configMain.set(pathMaxStackSize, 1000);
		
		if (configMain.getString(pathPowerCostBasic) == null) configMain.set(pathPowerCostBasic, 1);
		if (configMain.getString(pathPowerCostMath) == null) configMain.set(pathPowerCostMath, 2);
		if (configMain.getString(pathPowerCostInteraction) == null) configMain.set(pathPowerCostInteraction, 3);
		if (configMain.getString(pathPowerCostMovement) == null) configMain.set(pathPowerCostMovement, 4);
		if (configMain.getString(pathPowerCostAttack) == null) configMain.set(pathPowerCostAttack, 5);
		
		if (listBlockPower() == null) {
			configMain.set(pathBlockPower + "." + 17, 100);
			configMain.set(pathBlockPower + "." + 263, 1000);
			
		}
		
	}
	
	public void loadDefaultsDirectives() {
		if (getListDirectives() == null) {
			configDirectives.set(pathDirectiveExterminate + ".enabled", true);
			configDirectives.set(pathDirectiveExterminate + ".data", "range:3;attack:monster");
			
			configDirectives.set(pathDirectiveVacuum + ".enabled", true);
			configDirectives.set(pathDirectiveVacuum + ".data", "range:5;pickupitems");
			
		}
		
	}
	
	public void loadDefaultsDatabase() {
		if (configDatabase.getString(pathMySQL) == null) configDatabase.set(pathMySQL, false);
		if (configDatabase.getString(pathHost) == null) configDatabase.set(pathHost, "localhost");
		if (configDatabase.getString(pathUsername) == null) configDatabase.set(pathUsername, "root");
		if (configDatabase.getString(pathPassword) == null) configDatabase.set(pathPassword, "root");
		if (configDatabase.getString(pathDatabase) == null) configDatabase.set(pathDatabase, "minecraft");
		if (configDatabase.getString(pathPort) == null) configDatabase.set(pathPort, 3306);

	}
	//#############################################################################################

	
	//=======================================================================================
	private Set<String> list(YamlConfiguration config, String path) {
	    if (config.getConfigurationSection(path) != null && config.getConfigurationSection(path).getKeys(false) != null)
		    return config.getConfigurationSection(path).getKeys(false);
	    
	    else return null;

	}
	
	private Set<String> listRoot(YamlConfiguration config) { return config.getKeys(false); }
	
	private Set<String> listBlockPower() { return list(configMain, pathBlockPower); }
	//=======================================================================================
	

	
	//=============================================================================================
	public long getLastLogSubmit() { return configMain.getLong(pathLastLog); }
	public void setLastLogSubmit(long v) { configMain.set(pathLastLog, v); }
	public boolean doLogStatistics() { return configMain.getBoolean(pathEnableLog, true); }
	public boolean doFeedback() { return configMain.getBoolean(pathFeedback, true); }
	public int getRobotMaxHealth() { return configMain.getInt(pathMaxHealth, 100); }
	public int getRobotMaxArmor() { return configMain.getInt(pathMaxArmor, 100); }
	public int getRobotMaxPower() { return configMain.getInt(pathMaxPower, 10000); }
	public int getRobotMaxDamage() { return configMain.getInt(pathMaxDamage, 15); }
	public int getMaxStackSize() { return configMain.getInt(pathMaxStackSize, 1000); }
	
	public int getPowerCostBasic() { return configMain.getInt(pathPowerCostBasic, 1); }
	public int getPowerCostMath() { return configMain.getInt(pathPowerCostMath, 2); }
	public int getPowerCostInteraction() { return configMain.getInt(pathPowerCostInteraction, 3); }
	public int getPowerCostMovement() { return configMain.getInt(pathPowerCostMovement, 4); }
	public int getPowerCostAttack() { return configMain.getInt(pathPowerCostAttack, 5); }
	
	public HashMap<Integer, Integer> getBlockPowers() {
		HashMap<Integer, Integer> blockPowers = new HashMap<Integer, Integer>();
		
		Set<String> blocks = listBlockPower();
		
		for (String block : blocks) {
			try {blockPowers.put(Integer.parseInt(block), getPower(block)); } catch (NumberFormatException e) {}
			
		}
		
		return blockPowers;
		
	}
	
	private int getPower(String block) { return configMain.getInt(pathBlockPower + "." + block, 1); }
	//=============================================================================================

	
	
	//=============================================================================================
	public Set<String> getListDirectives() { return listRoot(configDirectives); }
	public boolean isDirectiveEnabled(String name) { return configDirectives.getBoolean(name + ".enabled", true); }
	public String getDirectiveData(String name) { return configDirectives.getString(name + ".data"); }
	//=============================================================================================

	
	
	//=============================================================================================
	public boolean getDatabaseUseMySQL() { return configDatabase.getBoolean(pathMySQL, false); }
	public String getDatabaseHost() { return configDatabase.getString(pathHost, "localhost"); }
	public String getDatabaseUsername() { return configDatabase.getString(pathUsername, "root"); }
	public String getDatabasePassword() { return configDatabase.getString(pathPassword, "root"); }
	public String getDatabaseDatabase() { return configDatabase.getString(pathDatabase, "minecraft"); }
	public int getDatabasePort() { return configDatabase.getInt(pathPort, 3306); }
	//=============================================================================================

}
