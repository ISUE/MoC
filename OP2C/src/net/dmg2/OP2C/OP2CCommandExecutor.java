package net.dmg2.OP2C;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class OP2CCommandExecutor implements CommandExecutor {
	//============================================================
	private OP2C plugin;
	private int defaultHelath = 20;
	public OP2CCommandExecutor(OP2C plugin) { this.plugin = plugin; }
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){ return true; }

		//Convert sender to Player
		Player player = (Player) sender;
		
		//Teleport
		if (args.length == 0) { player.teleport(plugin.getConfiguration().getPortalLocation(), TeleportCause.PLUGIN); return true; }
		
		//NoOP return
		if (!player.isOp()) return true;
		
		//Set new location
		if (args[0].equalsIgnoreCase("set")) { plugin.getConfiguration().setPortalLocation(player.getLocation()); return true; }
		
		if (args[0].equalsIgnoreCase("health")) {
			if (args.length > 1) {
				try { player.setMaxHealth(Math.abs(Integer.parseInt(args[1]))); } catch (NumberFormatException e) { player.setMaxHealth(defaultHelath); }
				
			} else { player.setMaxHealth(defaultHelath); }
			
		}
		
		if (args[0].equalsIgnoreCase("boom")) {
			Block block = player.getTargetBlock(null, 200);
			
			float power = 4F; if (args.length == 2) power = Float.parseFloat(args[1]); 
			
			block.getWorld().createExplosion(block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ(), power, false, true);
			
		}
		
    	return true;
		
	}

}
