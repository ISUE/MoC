package net.dmg2.ChangeSign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeSignCommandExecutor implements CommandExecutor {
	//============================================================
	private ChangeSign plugin;
	public ChangeSignCommandExecutor(ChangeSign plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/sign is only available in game.");
			return true;
		}

		//Convert sender to Player
		Player player = (Player) sender;

		//If no arguments display the default help message
		if (args.length < 1) { return false; }
		
		//Get the sign
		Block targetBlock = player.getTargetBlock(null, 100);
		if ( !(targetBlock.getType() == Material.SIGN_POST || targetBlock.getType() == Material.WALL_SIGN) ) {
			this.plugin.getLog().sendPlayerWarn(player, "Please target a sign before sending /sign command.");
			return true;
		}
		
		//Get line number
		int lineNumber = 1;
		try { lineNumber = Integer.parseInt(args[0]); } catch(NumberFormatException e) {}
		
		if (lineNumber < 1 || lineNumber > 4) {
			this.plugin.getLog().sendPlayerWarn(player, "Invalid line number, please use 1 through 4");
			return true;
		}
		
		//Get new text for the sign
		String newText = "";
		for (int i = 1 ; i < args.length ; i++) newText = newText + args[i] + " ";
		
		//Set new text
		Sign sign = (Sign) targetBlock.getState();
		
		sign.setLine(0, sign.getLine(0));
		sign.setLine(1, sign.getLine(1));
		sign.setLine(2, sign.getLine(2));
		sign.setLine(3, sign.getLine(3));
		sign.setLine(lineNumber-1, newText);
		
		sign.update();
		
		//Message the player
		plugin.getLog().sendPlayerNormal(player, "Sign text was changed to [" + newText + "] on line " + lineNumber);
		//Log
		plugin.getLog().info(player.getName() + " changed sign text at [" + targetBlock.getX() + " " + targetBlock.getX() + " " + targetBlock.getX() + "]");

    	return true;
		
	}
}
