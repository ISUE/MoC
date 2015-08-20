package net.dmg2.rTPS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class rTPSCommandExecutor implements CommandExecutor {
	//============================================================
	private rTPS plugin;
	public rTPSCommandExecutor(rTPS plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/rtps is only available in game.");
			return true;
		}

		//Convert sender to Player
		Player player = (Player) sender;
		
		this.plugin.getLog().sendPlayerNormal(player, "Hi.");

    	return true;
		
	}
}
