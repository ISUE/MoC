package net.dmg2.Plotasize;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plotasize extends JavaPlugin {

	//=============================================================================================
	private PlotasizeCommandExecutor commandExecutor;
	protected PlotasizeLogHandler log;
	protected String pluginPath;
	protected File configFile;
	protected PlotasizeConfig config;
	private final PlotasizePlayerListener playerListener = new PlotasizePlayerListener(this);
	//=============================================================================================
	protected boolean doDebug = false;
	protected ArrayList<PlotasizeBluePrint> blueprints = new ArrayList<PlotasizeBluePrint>();
	//=============================================================================================
	

	
	//=============================================================================================
	public void onEnable(){
    	//Log
    	this.log = new PlotasizeLogHandler(this);
    	this.log.info("Enabled.");
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	
    	pm.registerEvents(this.playerListener, this);

    	//Settings file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new PlotasizeConfig(this.configFile);
    	
    	//Commands handler
    	this.commandExecutor = new PlotasizeCommandExecutor(this);
    	this.getCommand("ps").setExecutor(this.commandExecutor);
    	
    }
	//=============================================================================================


	
	//=============================================================================================
	public void onDisable(){
    	//Save configuration file
    	this.config.save();

    	//Log
    	this.log.info("Disabled.");
    	
    }
	//=============================================================================================
    

	
	//=============================================================================================
	public void generateBluePrint(Player player) { this.blueprints.add(new PlotasizeBluePrint(this, player)); }
	//=============================================================================================

}
