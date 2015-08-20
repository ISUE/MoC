package net.moc.MOCDreamCatcher;

import java.io.File;
import net.moc.MOCDreamCatcher.Data.DreamNet;
import net.moc.MOCDreamCatcher.GUI.GUI;
import net.moc.MOCDreamCatcher.Generator.DreamGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class MOCDreamCatcher extends JavaPlugin {
	//=============================================================================================================
	private MOCDreamCatcherLogHandler log; public MOCDreamCatcherLogHandler getLog() { return log; }
	private MOCDreamCatcherConfig config; public MOCDreamCatcherConfig getConfiguration() { return config; }
	private MOCDreamCatcherCommandExecutor commandExecutor;
	private MOCDreamCatcherEventListener eventListener; public MOCDreamCatcherEventListener getListener() { return eventListener; }
	
	private DreamGenerator generator;
	
	private DreamNet dreamNet; public DreamNet getDreamNet() { return dreamNet; }
	
	private GUI gui; public GUI getGUI() { return gui; }
	
	private boolean spoutAvailable = false; public boolean isSpoutAvailable() { return spoutAvailable; } public void setSpoutAvailable(boolean value) { spoutAvailable = value; }
	
	//=============================================================================================================
	public void onEnable() {
		//Log
		log = new MOCDreamCatcherLogHandler(this);
		
		//Configuration
    	File configFile = new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml");
    	File playersFile = new File(getDataFolder().getAbsolutePath() + File.separator + "players.yml");
    	File statsFile = new File(getDataFolder().getAbsolutePath() + File.separator + "stats.yml");
		config = new MOCDreamCatcherConfig(configFile, playersFile, statsFile, this);
	
		//Dream Generator
		generator = new DreamGenerator();
		
		//Event listener
		eventListener = new MOCDreamCatcherEventListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);
		
		//Command executor
		commandExecutor = new MOCDreamCatcherCommandExecutor(this);
		getCommand("dc").setExecutor(commandExecutor);
		
		//DreamNet
		dreamNet = new DreamNet(this);
		dreamNet.wakeUpCall();
		
		//GUI
		if (spoutAvailable) {
			gui = new GUI(this);
			getServer().getPluginManager().registerEvents(gui.getListener(), this);
			
		}
		
	}
	
	//=============================================================================================================
	public void onDisable() {
		dreamNet.wakeUpCall();

	}
	
	//=============================================================================================================
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) { return generator; }

}
