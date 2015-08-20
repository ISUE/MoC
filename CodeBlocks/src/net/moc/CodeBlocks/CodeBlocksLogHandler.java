package net.moc.CodeBlocks;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CodeBlocksLogHandler {

	//==============================================================
	private CodeBlocks plugin;
	private Logger logger;
	public CodeBlocksLogHandler(CodeBlocks plugin){
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
	
	//Log message as DEBUG
	public void debug(String message){
		if (plugin.isDebug()) this.logger.warning(this.buildString(message));
	}
	
	//Log message as WARNING
	public void warn(String message){
		this.logger.warning(this.buildString(message));
	}
	
	
	//Normal message to the player
	public void sendPlayerNormal(String playerName, String message) {
		Player player = plugin.getServer().getPlayer(playerName);
		if (player != null) player.sendMessage(this.buildStringPlayer(message, ChatColor.GREEN));
	}
	
	//Normal message to the player
	public void sendPlayerWarn(String playerName, String message) {
		Player player = plugin.getServer().getPlayer(playerName);
		if (player != null) player.sendMessage(this.buildStringPlayer(message, ChatColor.YELLOW));
		
	}

}
