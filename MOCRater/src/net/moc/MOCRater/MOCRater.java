package net.moc.MOCRater;

import java.io.File;
import java.util.logging.Logger;
import moc.MOCDBLib.DBConnector;
import moc.MOCDBLib.MOCDBLib;
import net.moc.MOCRater.SQL.MOCRaterSQLQueries;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MOCRater extends JavaPlugin {
	//=============================================================================================
	private MOCRaterCommandExecutor commandExecutor = new MOCRaterCommandExecutor(this);
	private MOCRaterEventListener eventListener = new MOCRaterEventListener(this);
	protected MOCRaterLogHandler log = new MOCRaterLogHandler(this);
	protected MOCRaterGUI gui = new MOCRaterGUI(this); public MOCRaterGUI getGui() { return gui; }
	protected MOCRaterKeyListener keyListener = new MOCRaterKeyListener(this);
	protected MOCRaterSQLQueries sql; public MOCRaterSQLQueries getSQL() { return sql; }
	protected DBConnector databaseConnection;
	protected WorldGuardPlugin worldguard; public WorldGuardPlugin getWorldGuard() { return this.worldguard; }
	protected String pluginPath;
	protected File configFile;
	protected File screenShotFolder; public File getScreenshotFolder() { return this.screenShotFolder; }
	protected String screenShotURL; public String getScreenshotURL() { return this.screenShotURL; }
	protected MOCRaterConfig config;
	//=============================================================================================

	//=============================================================================================
	public void onEnable() {
		//Configuration
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new MOCRaterConfig(this.configFile, this);
    	
    	//Commands handler
    	this.getCommand("rate").setExecutor(this.commandExecutor);
    	this.getCommand("patterns").setExecutor(this.commandExecutor);
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
    	//World guard
    	worldguard = ((WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard"));
    	
    	//Database
    	this.databaseConnection = ((MOCDBLib) pm.getPlugin("MOCDBLib")).getMineCraftDB(this.getName(), Logger.getLogger("Minecraft"));
    	this.sql = new MOCRaterSQLQueries(this, this.databaseConnection);

    	//Screen shots
    	this.screenShotFolder = new File(this.config.getScreenshotFileFolder());
    	this.screenShotFolder.mkdir();
    	this.screenShotURL = this.config.getScreenshotURLFolder();

    	//Key binding
    	SpoutManager.getKeyBindingManager().registerBinding("Open MOCRater GUI", Keyboard.KEY_R, "Open MOCRater GUI", keyListener, this);
    	
	}
	//=============================================================================================

	//=============================================================================================
	public void onDisable() {
		this.gui.deleteUnusedScreenshots();
		
	}
	//=============================================================================================
}
