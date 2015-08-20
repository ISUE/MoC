package net.dmg2.GravitySheep;

import java.util.logging.Logger;
import net.dmg2.GravitySheep.api.GravitySheepRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class GravitySheepLogHandler {
	//==============================================================
	private GravitySheep plugin;
	private Logger logger;
	public GravitySheepLogHandler(GravitySheep plugin){
		this.plugin = plugin;
		this.logger = Logger.getLogger("Minecraft");
	}
	//==============================================================
	//Generate message for console
	private String buildString(String message) {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return "[" + pdFile.getName() +"] (" + pdFile.getVersion() + ") " + message;
	}
	//Generate message for player
	private String buildStringPlayer(String message, ChatColor color) {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return color + "[" + pdFile.getName() +"] " + message;
	}
	

	//Log message as INFO
	public void info(String message){
		this.logger.info(this.buildString(message));
	}
	//Log message as WARNING
	public void warn(String message){
		this.logger.warning(this.buildString(message));
	}
	
	
	//Normal message to the player
	public void sendPlayerNormal(Player player, String message) {
		player.sendMessage(this.buildStringPlayer(message, ChatColor.AQUA));
	}
	//Normal message to the player
	public void sendPlayerWarn(Player player, String message) {
		player.sendMessage(this.buildStringPlayer(message, ChatColor.YELLOW));
	}
	
	//Region create message to the player
	public void sendPlayerRegionCreate(Player player, GravitySheepRegion r) {
		sendPlayerNormal(player, "Region " + r.getName() + " is saved.");
		String loc = r.getBase().getBlockX() + " " + r.getBase().getBlockY() + " " + r.getBase().getBlockZ();
		sendPlayerNormal(player, "Location: " + loc);

	}
	
	//List all regions to the player
	public void listRegion(Player player) {
		for (GravitySheepRegion region : plugin.getAPI().getWorld(player.getWorld()).getRegions()) {
			//Get XYZ for left and right
			String base = region.getBase().getBlockX() + " " + region.getBase().getBlockY() + " " + region.getBase().getBlockZ();
			
			//Get world name
			String worldName = region.getWorld().getName();
			
			//Get velocity
			String velocity = region.getVelocity().toString();
			
			//Get entity type
			String entityType = region.getEntityType().toString();
			
			//Message the player
			sendPlayerNormal(player, region.getName() + ": [W]" + worldName + " [L]" + base + " [V]" + velocity + " [C]" + entityType);
			
		}
		
	}

}
