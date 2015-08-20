package net.moc.MOC3DImporter;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MOC3DImporter extends JavaPlugin {
	//==============================================================
	private MOC3DImporterCommandExecutor commandExecutor;
	private MOC3DImporterEventListener eventListener;
	private MOC3DImporterLogHandler log; public MOC3DImporterLogHandler getLog() { return this.log; }
	private MOC3DImporterConfig config; public MOC3DImporterConfig getConfiguration() { return this.config; }
	private MOC3DImporterConverter converter; public MOC3DImporterConverter getConverter() { return this.converter; }
	private MOC3DImporterBuilder builder; public MOC3DImporterBuilder getBuilder() { return this.builder; }
	
	private String pluginPath; public String getPluginPath() { return this.pluginPath; }
	private File configFile;
	private File dataPath; public File getDataPath() { return this.dataPath; }

	private ArrayList<MOC3DImporterColorBlock> palette = new ArrayList<MOC3DImporterColorBlock>(); public ArrayList<MOC3DImporterColorBlock> getPalette() { return this.palette; }
	
	private float minHeight = 0; public float getMinHeight() { return this.minHeight; }
	private float maxHeight = 254; public float getMaxHeight() { return this.maxHeight; }
	//==============================================================
	
	public void onEnable() {
    	//Configuration
    	pluginPath = this.getDataFolder().getAbsolutePath();
    	dataPath = new File(this.pluginPath + File.separator + "data" + File.separator);
    	dataPath.mkdir();
    	configFile = new File(this.pluginPath + File.separator + "config.yml");
    	config = new MOC3DImporterConfig(this.configFile, this);
    	
    	commandExecutor = new MOC3DImporterCommandExecutor(this);
    	eventListener = new MOC3DImporterEventListener(this);
    	log = new MOC3DImporterLogHandler(this);
    	converter = new MOC3DImporterConverter(this);
    	builder = new MOC3DImporterBuilder(this);
    	
    	//Commands handler
    	this.getCommand("3di").setExecutor(this.commandExecutor);
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
	}
	
	public void onDisable() { this.config.save(); }
	
}
