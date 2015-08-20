package net.dmg2.DynamicMOTD;

import java.io.File;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicMOTD extends JavaPlugin{
	//=============================================================================================
	protected DynamicMOTDLogHandler log;
	private DynamicMOTDCommandExecutor commandExecutor;
	public String pluginPath;
	public File configFile;
	public DynamicMOTDConfig config;
	private final DynamicMOTDPlayerListener playerListener = new DynamicMOTDPlayerListener(this);
	//=============================================================================================

	public void onEnable() {
    	//Log
    	this.log = new DynamicMOTDLogHandler(this);
    	this.log.info("Enabled. Good Day.");

    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();

    	pm.registerEvents(this.playerListener, this);
    	
    	//Settings file - [Update API]
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new DynamicMOTDConfig(this.configFile);

    	//Commands handler
    	this.commandExecutor = new DynamicMOTDCommandExecutor(this);
    	this.getCommand("dmotd").setExecutor(this.commandExecutor);

	}

	public void onDisable() {
    	//Save configuration file
    	this.config.save();

    	//Log
    	this.log.info("Disabled. Good Bye.");
	}

}
