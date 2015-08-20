package net.dmg2.OP2C;


import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.plugin.java.JavaPlugin;

public class OP2C extends JavaPlugin {
	//=============================================================================================
	private OP2CEventListener eventListener;
	private OP2CCommandExecutor commandExecutor;
	private OP2CConfig config; public OP2CConfig getConfiguration() { return this.config; }
	private String pluginPath;
	private File configFile;
	private Logger logger; public Logger getLog() { return logger; }
	
	//=============================================================================================

	public void onEnable() {
    	eventListener = new OP2CEventListener(this);
    	this.getServer().getPluginManager().registerEvents(eventListener, this);
    	
    	//Settings file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new OP2CConfig(this, this.configFile);
    	
    	//Log file
		logger = Logger.getLogger("OP2C");
    	try {
    		FileHandler fileHandler = new FileHandler(this.pluginPath + File.separator + "server.log", true);
			SimpleFormatter formatter = new SimpleFormatter();  
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) { e.printStackTrace(); }
    	
    	//Commands handler
    	this.commandExecutor = new OP2CCommandExecutor(this);
    	this.getCommand("portal").setExecutor(this.commandExecutor);
    	
	}

	public void onDisable() {}

}
