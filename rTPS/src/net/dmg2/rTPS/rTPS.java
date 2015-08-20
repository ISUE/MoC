package net.dmg2.rTPS;

import java.io.File;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class rTPS extends JavaPlugin {
	//=============================================================================================
	private rTPSLogHandler log; public rTPSLogHandler getLog() { return log; }
//	private rTPSCommandExecutor commandExecutor;
	private rTPSConfig config; public rTPSConfig getConfiguration() { return this.config; }
	private rTPSEventListener listener;
	private rTPSQueue queue;
	private String pluginPath;
	private File configFile;
	
	//=============================================================================================

	public void onEnable() {
    	//Log
    	this.log = new rTPSLogHandler(this);

    	//Configuration file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new rTPSConfig(this.configFile);
    	
    	//Events handler
    	this.listener = new rTPSEventListener(this);
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.listener, this);
    	
//    	//Commands handler
//    	this.commandExecutor = new rTPSCommandExecutor(this);
//    	this.getCommand("rtps").setExecutor(this.commandExecutor);

    	//Start Queue
    	this.queue = new rTPSQueue(this);
    	new Thread(this.queue).start();
    	
	}

	public void onDisable() {}

}
