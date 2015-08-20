package net.dmg2.Plotasize;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class PlotasizeLogHandler {

	//==============================================================
	private Plotasize plugin;
	private Logger logger;
	
	public PlotasizeLogHandler(Plotasize plugin) {
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
		player.sendMessage(this.buildStringPlayer(message, ChatColor.RED));
	}
	
	//Settings list to the player
	public void sendPlayerSettings(Player player) {
		this.sendPlayerNormal(player, "Plotasize settings:");
		this.sendPlayerNormal(player, "Number of plots - " + this.plugin.config.getNumberOfPlots()); 
		this.sendPlayerNormal(player, "Plot dimensions - [X]" + this.plugin.config.getPlotX() + " [Y]" + this.plugin.config.getPlotY() + " [Z]" + this.plugin.config.getPlotZ());
		this.sendPlayerNormal(player, "Plot buffer - " + this.plugin.config.getPlotBuffer() + ", Clearance - " + this.plugin.config.getClearance() + ", Center on player - " + this.plugin.config.getCenterOnPlayer());

	}
	
}
