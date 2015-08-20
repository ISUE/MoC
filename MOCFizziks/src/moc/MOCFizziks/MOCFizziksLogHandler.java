package moc.MOCFizziks;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class MOCFizziksLogHandler {
	//==============================================================
	private MOCFizziks plugin;
	private Logger logger;
	public MOCFizziksLogHandler(MOCFizziks plugin){
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
	public void info(String message) { this.logger.info(this.buildString(message)); }
	//Log message as WARNING
	public void warn(String message) { this.logger.warning(this.buildString(message)); }
	
	//Normal message to the player
	public void sendPlayerNormal(Player player, String message) { if (player != null) player.sendMessage(this.buildStringPlayer(message, ChatColor.AQUA)); }
	//Normal message to the player
	public void sendPlayerWarn(Player player, String message) { if (player != null) player.sendMessage(this.buildStringPlayer(message, ChatColor.YELLOW)); }
	
	//Region create message to the player
	public void sendPlayerRegionCreate(Player player, MOCFizziksRegion region) {
		sendPlayerNormal(player, "Region " + region.getName() + " is saved.");
		info(player.getName() + " created/updated region " + region.getName() + ".");
		
		sendPlayerRegionInfo(player, region);
		
	}
	
	//List all world regions to the player
	public void listRegions(Player player) {
		ArrayList<MOCFizziksRegion> regions = plugin.getAPI().getWorld(player.getWorld()).getRegions();
		
		for (MOCFizziksRegion region : regions) sendPlayerRegionInfo(player, region);
		
	}

	//Send player region info
	public void sendPlayerRegionInfo(Player player, MOCFizziksRegion region) {
		//Get XYZ for left and right
		String left = region.getLowerLeft().getX() + " " + region.getLowerLeft().getY() + " " + region.getLowerLeft().getZ();
		String right = region.getUpperRight().getX() + " " + region.getUpperRight().getY() + " " + region.getUpperRight().getZ();
		
		//Get world name
		String worldName = region.getWorld().getName();
		
		//Get Acceleration
		String acceleration = region.getAcceleration().toString();
		String velocity = region.getVelocity().toString();
		
		//Get IsOn/UsesPower
		String onAndPower = region.isOn() + " " + region.usesPower(); 
		
		//Message the player
		sendPlayerNormal(player, region.getName() + ": [W]" + worldName + " [L]" + left +" [R]" + right);
		sendPlayerNormal(player, region.getName() + ": [A]" + acceleration + " [V]" + velocity + " [On/Power]" + onAndPower);
		
	}

}
