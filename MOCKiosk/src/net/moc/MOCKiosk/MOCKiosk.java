package net.moc.MOCKiosk;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import moc.DreamCrafter.MOCDreamCrafter;
import moc.MOCDBLib.DBConnector;
import moc.MOCDBLib.MOCDBLib;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;
import net.moc.MOCKiosk.SQL.MOCKioskKioskSlide;
import net.moc.MOCKiosk.SQL.MOCKioskSQLQueries;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.material.MaterialData;

public class MOCKiosk extends JavaPlugin {
	//=============================================================================================
	private MOCKioskCommandExecutor commandExecutor = new MOCKioskCommandExecutor(this);
	private MOCKioskEventListener eventListener = new MOCKioskEventListener(this);
	private MOCKioskLogHandler log = new MOCKioskLogHandler(this); public MOCKioskLogHandler getLog() { return this.log; }
	private MOCKioskGUI gui = new MOCKioskGUI(this); public MOCKioskGUI getGui() { return gui; }
	private MOCKioskKeyListener keyListener = new MOCKioskKeyListener(this);
	private MOCKioskSQLQueries sql; public MOCKioskSQLQueries getSQL() { return sql; }
	private DBConnector databaseConnection;
	private MOCDreamCrafter dreamCrafter; public MOCDreamCrafter getDreamCrafter() { return this.dreamCrafter; }
	private String pluginPath; public String getPluginPath() { return this.pluginPath; }
	private File configFile;
	private MOCKioskConfig config; public MOCKioskConfig getConfiguration() { return this.config; }
	private MOCKioskBlock kioskBlock; public MOCKioskBlock getKioskBlock() { return this.kioskBlock; }
	private HashMap<Player, MOCKioskKiosk> lastestKioskShown = new HashMap<Player, MOCKioskKiosk>(); public HashMap<Player, MOCKioskKiosk> getLatestKioskShown() { return this.lastestKioskShown; }; 
	//=============================================================================================

	//=============================================================================================
	public void onEnable() {
    	//Config
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new MOCKioskConfig(this.configFile, this);
    	
    	//Commands handler
    	this.getCommand("kiosk").setExecutor(this.commandExecutor);
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
    	//Dreamcrafter
    	this.dreamCrafter = (MOCDreamCrafter) pm.getPlugin("MOCDreamCrafter");
    	
    	//Database
    	this.databaseConnection = ((MOCDBLib) pm.getPlugin("MOCDBLib")).getMineCraftDB(this.getName(), Logger.getLogger("Minecraft"));
    	this.sql = new MOCKioskSQLQueries(this, this.databaseConnection);

    	//Key binding
    	SpoutManager.getKeyBindingManager().registerBinding("Open MOCKiosk Kiosk Browser", Keyboard.KEY_K, "Open MOCKiosk Kiosk Browser", keyListener, this);
    	
    	//Kiosk block
    	kioskBlock = new MOCKioskBlock(this);
    	
    	//Kiosk block recipe
    	SpoutShapedRecipe recipe = new SpoutShapedRecipe(new SpoutItemStack(this.kioskBlock, 1));
    	recipe.shape(" r ",		// _r_
    				" g ",		// _g_
    				" i ");		// _i_
    	recipe.setIngredient('i', MaterialData.ironIngot);
    	recipe.setIngredient('g', MaterialData.glass);
    	recipe.setIngredient('r', MaterialData.redstone);
    	SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
    	
	}
	//=============================================================================================

	//=============================================================================================
	public void onDisable() {
    	//Log
    	this.log.info("Disabled.");
	}
	//=============================================================================================
	//Copies all kiosks from one world to another
	 public boolean copyWorldKiosks(String worldFrom, String worldTo) {
		 if (this.getServer().getWorld(worldFrom) == null || this.getServer().getWorld(worldTo) == null) return false;
		 
		 for (MOCKioskKiosk kiosk : this.sql.getKiosks()) {
			 //Check for matching world name
			 if (kiosk.getLocation().getWorld().getName().equalsIgnoreCase(worldFrom)) {
				 //Set block to Kiosk block
				 ((SpoutBlock) this.getServer().getWorld(worldTo).getBlockAt(kiosk.getLocation())).setCustomBlock(kioskBlock);
				 
				 //Get slide
				 MOCKioskKioskSlide slide = this.sql.getSlide(kiosk.getPopup_deck_id());
				 
				 if (slide != null) {
					 this.sql.saveKiosk(kiosk.getName(), kiosk.getNeartext(), kiosk.getNearurl(), kiosk.getClicktext(), kiosk.getClickurl(),
							 true, slide.getTitle(), slide.getText(), slide.getUrl(), slide.getImage(), slide.getImage_size(),
							 kiosk.getOwnerName(), this.getServer().getWorld(worldTo).getBlockAt(kiosk.getLocation()), null, null);
					 
				 } else {
					 this.sql.saveKiosk(kiosk.getName(), kiosk.getNeartext(), kiosk.getNearurl(), kiosk.getClicktext(), kiosk.getClickurl(),
							 false, "", "", "", "", 1,
							 kiosk.getOwnerName(), this.getServer().getWorld(worldTo).getBlockAt(kiosk.getLocation()), null, null);
					 
				 }
				 
			 }
			 
		 }
		 
		 return true;
		 
	 }
	 
	 //Deletes kiosks from the world specified
	 public boolean deleteWorldKiosks(String worldName) {
		 if (this.getServer().getWorld(worldName) == null) return false;
		 
		 for (MOCKioskKiosk kiosk : this.sql.getKiosks()) {
			 if (kiosk.getLocation().getWorld().getName().equalsIgnoreCase(worldName)) {
				 this.sql.deleteKiosk(kiosk);
				 
			 }
			 
		 }
		 
		 return true;
		 
	 }

}
