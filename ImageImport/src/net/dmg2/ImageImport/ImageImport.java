package net.dmg2.ImageImport;

import java.io.File;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ImageImport extends JavaPlugin {

	//=============================================================================================
	private ImageImportLogHandler log; public ImageImportLogHandler getLog() { return log; }
	private ImageImportEventListener eventListener = new ImageImportEventListener(this);
	private ImageImportCommandExecutor commandExecutor;
	private String pluginPath; public String getPluginPath() {return this.pluginPath; }
	private File configFile;
	private ImageImportConfig config; public ImageImportConfig getConfiguration() { return config; }
	private ImageImportGenerator imageGenerator= new ImageImportGenerator(this); public ImageImportGenerator getImageGenerator() { return imageGenerator; }
	private ImageImportPalette palette = new ImageImportPalette(); public ImageImportPalette getPalette() { return this.palette; }
	//=============================================================================================

	public void onEnable() {
    	//Log
    	this.log = new ImageImportLogHandler(this);

    	//Settings file
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new ImageImportConfig(this, this.configFile);

    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
    	//Commands handler
    	this.commandExecutor = new ImageImportCommandExecutor(this);
    	this.getCommand("ii").setExecutor(this.commandExecutor);

	}

	public void onDisable() { this.config.save(); }

}
