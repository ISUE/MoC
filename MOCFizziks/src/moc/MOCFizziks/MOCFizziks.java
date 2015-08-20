package moc.MOCFizziks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MOCFizziks extends JavaPlugin {
	//=============================================================================================
	private MOCFizziksLogHandler log; 					public MOCFizziksLogHandler getLog() { return log; }
	private MOCFizziksCommandExecutor commandExecutor; 	public MOCFizziksCommandExecutor getCommandExecutor() { return this.commandExecutor; }
	private MOCFizziksConfig config; 					public MOCFizziksConfig getConfiguration() { return this.config; }
	private MOCFizziksPlayerListener playerListener; 	public MOCFizziksPlayerListener getPlayerListener() { return playerListener; }
	
	private String pluginPath;
	private File configFile;
	private File regionsFile;
	
	private MOCFizziksAPI api;							public MOCFizziksAPI getAPI() { return api; }
	//=============================================================================================
	
	//---------------------------------------------------------------------------------------------
	//HashMaps to store players
	private long lastQueueTime = System.currentTimeMillis();
	public long getLastQueueTime() { return this.lastQueueTime; }
	public void setLastQueueTime(long value) { this.lastQueueTime = value; }
	
	private HashMap<String, Location> playerSelectionLeft = new HashMap<String, Location>();
	public HashMap<String, Location> getPlayerSelectionLeft() { return playerSelectionLeft; }
	
	private HashMap<String, Location> playerSelectionRight = new HashMap<String, Location>();
	public HashMap<String, Location> getPlayerSelectionRight() { return playerSelectionRight; }
	
	private ArrayList<String> playerSelectionStatus = new ArrayList<String>();
	public ArrayList<String> getPlayerSelectionStatus() { return playerSelectionStatus; }
	
	private ArrayList<String> playerEditStatus = new ArrayList<String>();
	public ArrayList<String> getPlayerEditStatus() { return playerEditStatus; }
	
	private HashMap<Player, Vector> playerVelocityQueueVelocity = new HashMap<Player, Vector>();
	public HashMap<Player, Vector> getPlayerVelocityQueueVelocity() { return playerVelocityQueueVelocity; }
	
	private HashMap<Player, Vector> playerVelocityQueueAcceleration = new HashMap<Player, Vector>();
	public HashMap<Player, Vector> getPlayerVelocityQueueAcceleration() { return playerVelocityQueueAcceleration; }
	
	private HashMap<Player, Long> playerVelocityQueueTimeIn = new HashMap<Player, Long>();
	public HashMap<Player, Long> getPlayerVelocityQueueTimeIn() { return playerVelocityQueueTimeIn; }
	//---------------------------------------------------------------------------------------------

	public void onEnable() {
		//Log
    	this.log = new MOCFizziksLogHandler(this);
    	
    	//Events handler
    	playerListener = new MOCFizziksPlayerListener(this);
    	getServer().getPluginManager().registerEvents(this.playerListener, this);

    	//Settings file
    	pluginPath = this.getDataFolder().getAbsolutePath();
    	configFile = new File(pluginPath + File.separator + "config.yml");
    	regionsFile = new File(pluginPath + File.separator + "regions.yml");
    	config = new MOCFizziksConfig(configFile, regionsFile, this);
    	
    	//Commands handler
    	commandExecutor = new MOCFizziksCommandExecutor(this);
    	getCommand("mf").setExecutor(this.commandExecutor);
    	
    	//API
    	api = new MOCFizziksAPI(this);
    	
    	//Start tick queue
    	new Thread(new MOCFizziksQueue()).start();

	}
	
	public void onDisable() { this.config.save(); }

}
