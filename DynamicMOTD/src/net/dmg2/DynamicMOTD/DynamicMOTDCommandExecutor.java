package net.dmg2.DynamicMOTD;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DynamicMOTDCommandExecutor implements CommandExecutor {
	//============================================================
	private DynamicMOTD plugin;
	public DynamicMOTDCommandExecutor(DynamicMOTD plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.log.info("/dmotd is only available in game.");
			return true;
		}

		//Convert sender to Player
		Player player = (Player) sender;

		//If no arguments display the default help message
		if (args.length == 0) {
			plugin.log.sendPlayerNormal(player, "[" + plugin.config.getString("dmotd.owner") + "] " + ChatColor.AQUA + plugin.config.getString("dmotd.msg"));
			return true;
		}
		
		String newdmotd = "";
		for (String s : args) newdmotd = newdmotd + " " + s;
		
		//Message the player
		plugin.log.sendPlayerNormal(player, "Setting Dynamic MOTD to " + newdmotd);
		//Log
		plugin.log.info(player.getName() + " set DMOTD to " + newdmotd);
		//Write to file
		plugin.config.setProperty("dmotd.msg", newdmotd);
		plugin.config.setProperty("dmotd.owner", player.getName());

    	return true;
		
	}
}
