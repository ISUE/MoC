package net.dmg2.RegenBlock;

import java.io.File;
import net.dmg2.RegenBlock.Listener.RegenBlockEventListenerBlock;
import net.dmg2.RegenBlock.Listener.RegenBlockEventListenerCancel;
import net.dmg2.RegenBlock.Listener.RegenBlockEventListenerPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegenBlock extends JavaPlugin {
	
	//=============================================================================================
	private RegenBlockCommandExecutor commandExecutor;
	private RegenBlockLogHandler log; public RegenBlockLogHandler getLog() { return this.log; } 
	private RegenBlockConfig config; public RegenBlockConfig getConfiguration() { return this.config; }
	private String pluginPath;
	private File configFile, blocksFile;
	
	private RegenBlockEventListenerBlock listenerBlock; public RegenBlockEventListenerBlock getListenerBlock() { return this.listenerBlock; }
	private RegenBlockEventListenerPlayer listenerPlayer; public RegenBlockEventListenerPlayer getListenerPlayer() { return this.listenerPlayer; }
	private RegenBlockEventListenerCancel listenerCancel;

	private RegenBlockQueue queue; public RegenBlockQueue getQueue() { return this.queue; }
	
	//=============================================================================================
	public void onEnable(){
    	//Log
    	this.log = new RegenBlockLogHandler(this);
    	
    	//Listeners
    	listenerBlock = new RegenBlockEventListenerBlock(this);
    	listenerPlayer = new RegenBlockEventListenerPlayer(this);
    	listenerCancel = new RegenBlockEventListenerCancel(this);
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.listenerBlock, this);
    	pm.registerEvents(this.listenerPlayer, this);
    	pm.registerEvents(this.listenerCancel, this);

    	//Settings file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.blocksFile = new File(this.pluginPath + File.separator + "blocks.yml");
    	this.config = new RegenBlockConfig(this, this.configFile, this.blocksFile);
    	
    	//Commands handler
    	this.commandExecutor = new RegenBlockCommandExecutor(this);
    	this.getCommand("rb").setExecutor(this.commandExecutor);
    	
    	//Start RegenBlock queue
    	this.queue = new RegenBlockQueue(this);
    	new Thread(this.queue).start();
    	
    	//Re-queue old blocks
    	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() { public void run() { config.requeue(); } }, 200L);
    	
    }

    public void onDisable() { this.config.save(); this.config.saveBlocks(); }
    
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ INSTANCE STUFF
	public void regenBlock(Location location, Material material, byte data, Player player, Boolean isBreakEvent) { listenerBlock.regenBlock(location, material, data, player, isBreakEvent); }
    public boolean copyWorldRegion(String worldFrom, String worldTo) { return config.copyWorldRegions(worldFrom, worldTo); }
    public boolean removeWorldRegions(String worldName) { return config.removeWorldRegions(worldName); }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ INSTANCE STUFF
    
    
}
