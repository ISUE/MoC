package moc.DreamCrafter;

import java.io.File;
import moc.DreamCrafter.data.PersistentDataHandler;
import moc.DreamCrafter.gui.GuiController;
import moc.DreamCrafter.world.WorldHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MOCDreamCrafter extends JavaPlugin {
	private MOCDreamCrafterLogHandler log; public MOCDreamCrafterLogHandler getLog() { return log; }
	private MOCDreamCrafterCommandExecutor commandExecutor;
	private MOCDreamCrafterConfig config; public MOCDreamCrafterConfig getConfiguration() { return config; }
	private MOCDreamCrafterEventListener eventListener;
	
	private WorldHandler worldHandler; public WorldHandler getWorldHandler() { return worldHandler; }
	private PersistentDataHandler persistentDataHandler; public PersistentDataHandler getPersistentDataHandler() { return persistentDataHandler; }
	private GuiController gui; public GuiController getGui() { return gui; }
	private ExternalPluginHandler externalPluginHandler; public ExternalPluginHandler getExternalPluginHandler() { return externalPluginHandler; }
	private DreamCleanupHandler dreamCleanupHandler; public DreamCleanupHandler getDreamCleanupHandler() { return dreamCleanupHandler; }
	
	public void onEnable() {
		//Log
		log = new MOCDreamCrafterLogHandler(this);
		
		worldHandler = new WorldHandler(this);
		persistentDataHandler = new PersistentDataHandler(this);
		gui = new GuiController(this);
		externalPluginHandler = new ExternalPluginHandler(this);
		dreamCleanupHandler = new DreamCleanupHandler(this);
    	
		//Settings file
    	File configFile = new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		config = new MOCDreamCrafterConfig(configFile);
	
		//Event listener
		eventListener = new MOCDreamCrafterEventListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);
		
		//Command executor
		commandExecutor = new MOCDreamCrafterCommandExecutor(this);
		getCommand("dc").setExecutor(commandExecutor);
		
		//Load depends
		if(!externalPluginHandler.loadExternalPlugins()) getServer().getPluginManager().disablePlugin(this);
		
	}
	
	public void onDisable() { persistentDataHandler.onDisable(); }

}
