package net.dmg2.RegenBlock;

import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class RegenBlockLogHandler {
	//==============================================================
	private RegenBlock plugin;
	private Logger logger;
	public RegenBlockLogHandler(RegenBlock plugin){
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
	public void sendPlayerNormal(Player player, String message) { player.sendMessage(this.buildStringPlayer(message, ChatColor.AQUA)); }
	
	//Warning message to the player
	public void sendPlayerWarn(Player player, String message) { player.sendMessage(this.buildStringPlayer(message, ChatColor.YELLOW)); }
	
	//Region create message to the player
	public void sendPlayerRegionInfo(Player player, String regionName) {
		//Get XYZ for left and right
		String left = plugin.getConfiguration().getRegionLeft(regionName);
		String right = plugin.getConfiguration().getRegionRight(regionName);
		//Get world name
		String worldName = plugin.getConfiguration().getRegionWorldName(regionName);
		//Get re-spawn time
		int respawnTime = plugin.getConfiguration().getRegionRespawnTime(regionName);
		
		int sync = this.plugin.getConfiguration().getRegionSync(regionName);
		int type = this.plugin.getConfiguration().getRegionType(regionName);
		int feedbackId = this.plugin.getConfiguration().getRegionFeedbackID(regionName);
		int alarmTime = this.plugin.getConfiguration().getRegionAlarmTime(regionName);
		int alarmRadius = this.plugin.getConfiguration().getRegionAlarmRadius(regionName);
		String alarmMessage = this.plugin.getConfiguration().getRegionAlarmMessage(regionName);
		
		//Message the player
		sendPlayerNormal(player, regionName + ": [W] " + worldName + "; [L] " + left +"; [R] " + right + "; [T] " + respawnTime + "s.");
		sendPlayerNormal(player, "[Sync] " + sync + "; [Type] " + type + "; [Feedback ID] " + feedbackId);
		sendPlayerNormal(player, "Alarm [Time] " + alarmTime + "; [Radius] " + alarmRadius + "; [Message] " + alarmMessage);
		
	}

	//List all regions to the player
	public void listRegion(Player player, Set<String> listRegion) {
		for (String regionName : listRegion) {
			//Get XYZ for left and right
			String left = plugin.getConfiguration().getRegionLeft(regionName);
			String right = plugin.getConfiguration().getRegionRight(regionName);
			//Get world name
			String worldName = plugin.getConfiguration().getRegionWorldName(regionName);
			//Get re-spawn time
			int respawnTime = plugin.getConfiguration().getRegionRespawnTime(regionName);
			//Message the player
			sendPlayerNormal(player, regionName + ": [W] " + worldName + " [L] " + left +"; [R] " + right + "; [T] " + respawnTime + "s.");
			
		}
		
	}

}
