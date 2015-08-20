package net.dmg2.LevelPlayerStats;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelPlayerStats extends JavaPlugin {
	//=============================================================================================
	private LPSEventListener eventListener;
	private LPSCommandExecutor commandExecutor;
	private LPSConfig config; public LPSConfig getConfiguration() { return this.config; }
	private String pluginPath;
	private File configFile;
	
	//=============================================================================================

	public void onEnable() {
    	eventListener = new LPSEventListener(this);
    	this.getServer().getPluginManager().registerEvents(eventListener, this);
    	
    	//Settings file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new LPSConfig(this, this.configFile);
    	
    	//Commands handler
    	this.commandExecutor = new LPSCommandExecutor(this);
    	this.getCommand("lps").setExecutor(this.commandExecutor);
    	
	}

	public void onDisable() {}

}
