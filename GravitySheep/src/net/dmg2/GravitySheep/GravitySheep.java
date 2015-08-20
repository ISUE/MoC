package net.dmg2.GravitySheep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.dmg2.GravitySheep.api.GravitySheepAPI;
import net.dmg2.GravitySheep.events.GravitySheepBlockListener;
import net.dmg2.GravitySheep.events.GravitySheepPlayerListener;

import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GravitySheep extends JavaPlugin {

	//=============================================================================================
	private GravitySheepLogHandler log; public GravitySheepLogHandler getLog() { return this.log; }
	private GravitySheepCommandExecutor commandExecutor;
	private GravitySheepConfig config; public GravitySheepConfig getConfiguration() { return this.config; } 
	private GravitySheepBlockListener blockListener = new GravitySheepBlockListener(this);
	private GravitySheepPlayerListener playerListener = new GravitySheepPlayerListener(this);
	private Random rnd = new Random(); public Random getRandom() { return this.rnd; }
	private GravitySheepAPI api; public GravitySheepAPI getAPI() { return api; }
	//=============================================================================================
	
	//---------------------------------------------------------------------------------------------
	//HashMaps to store players
	private HashMap<String, Location> playerSelectionLeft = new HashMap<String, Location>(); public HashMap<String, Location> getPlayerSelectionLeft() { return playerSelectionLeft; }
	private ArrayList<String> playerSelectionStatus = new ArrayList<String>(); public ArrayList<String> getPlayerSelectionStatus() { return playerSelectionStatus; }
	//---------------------------------------------------------------------------------------------

	public void onEnable() {
		//Log
    	log = new GravitySheepLogHandler(this);
    	
    	//Events handler
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(blockListener, this);
    	pm.registerEvents(playerListener, this);

    	//Settings file
    	config = new GravitySheepConfig(this);
    	
    	//API
    	api = new GravitySheepAPI(this);
    	
    	//Commands handler
    	commandExecutor = new GravitySheepCommandExecutor(this);
    	getCommand("gs").setExecutor(commandExecutor);
    	
	}
	
	public void onDisable() {}
	
}
