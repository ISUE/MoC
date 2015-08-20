package net.moc.MOCRater;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCRaterCommandExecutor implements CommandExecutor {
	//============================================================
	private MOCRater plugin;
	public MOCRaterCommandExecutor(MOCRater plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof SpoutPlayer == false){
			plugin.log.info("/rate is only available in game.");
			return true;
		}
		
		SpoutPlayer player = (SpoutPlayer) sender;
		
		if (label.equalsIgnoreCase("rate")) {
			return this.onRate(player, args);
		}
		
		if (label.equalsIgnoreCase("patterns")) {
			return this.onPatterns(player, args);
		}
		
    	return false;
		
	}
	
	private boolean onRate(SpoutPlayer player, String[] args) {
		//Display Rating GUI
		if (args.length == 0) {
			this.plugin.gui.displayRatingWindowGUI(player, true, null);
			return true;
			
		} else if (args[0].equalsIgnoreCase("browse") ){
			this.plugin.gui.displayRatingBrowserWindowGUI(player, null);
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean onPatterns(SpoutPlayer player, String[] args) {
		//Display Pattern browse GUI
		this.plugin.gui.displayPatternBrowserWindowGUI(player, true);
		return true;
		
	}
	
}
