package net.moc.MOCKiosk;

import net.moc.MOCKiosk.SQL.MOCKioskKiosk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCKioskCommandExecutor implements CommandExecutor {
	//============================================================
	private MOCKiosk plugin;
	public MOCKioskCommandExecutor(MOCKiosk plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof SpoutPlayer == false){
			plugin.getLog().info("/kiosk is only available in game.");
			return true;
			
		}
		
		SpoutPlayer player = (SpoutPlayer) sender;
		
		if (args.length < 1) { this.displayHelp(player); return true; }
		
		//====================================================================================================
		// /kiosk edit [id]
		if (args[0].equalsIgnoreCase("edit")) {
			if (!player.hasPermission("MOCKiosk.manage") || !player.hasPermission("MOCKiosk.admin")) { this.plugin.getLog().sendPlayerWarn(player, "You do not have permission to use this command."); return true;}
			
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int kioskId;
			
			try{ kioskId = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; }
			
			//Get matching kiosk
			MOCKioskKiosk kiosk = this.plugin.getSQL().getKiosk(kioskId);
			
			//Make sure we have found one
			if (kiosk == null) {
				this.plugin.getLog().sendPlayerWarn(player, "Kiosk ID " + kioskId + " not found.");
				return true;
				
			}
			
			if (kiosk.getOwnerName().equalsIgnoreCase(player.getName()) || player.hasPermission("MOCKiosk.admin")) {
				this.plugin.getGui().displayManagerWindowGUI(player, kiosk);
				
			} else {
				this.plugin.getLog().sendPlayerWarn(player, "Kiosk does not belong to you.");
				
			}
			
			return true;
			
		}

		//====================================================================================================
		// /kiosk delete [id]
		if (args[0].equalsIgnoreCase("delete")) {
			if (!player.hasPermission("MOCKiosk.manage") || !player.hasPermission("MOCKiosk.admin")) { this.plugin.getLog().sendPlayerWarn(player, "You do not have permission to use this command."); return true;}
			
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int kioskId;
			
			try{ kioskId = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; }
			
			//Get matching kiosk
			MOCKioskKiosk kiosk = this.plugin.getSQL().getKiosk(kioskId);
			
			//Make sure we have found one
			if (kiosk == null) {
				this.plugin.getLog().sendPlayerWarn(player, "Kiosk ID " + kioskId + " not found.");
				return true;
				
			}
			
			if (kiosk.getOwnerName().equalsIgnoreCase(player.getName()) || player.hasPermission("MOCKiosk.admin")) {
				this.plugin.getSQL().deleteKiosk(kiosk);
				
			} else {
				this.plugin.getLog().sendPlayerWarn(player, "Kiosk does not belong to you.");
				
			}
			
			return true;
			
		}
		
		//====================================================================================================
		// /kiosk tp [id]
		if (args[0].equalsIgnoreCase("tp")) {
			if (!player.hasPermission("MOCKiosk.tp") || !player.hasPermission("MOCKiosk.admin")) { this.plugin.getLog().sendPlayerWarn(player, "You do not have permission to use this command."); return true;}
			
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int kioskId;
			
			try{ kioskId = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; }
			
			//Get matching kiosk
			MOCKioskKiosk kiosk = this.plugin.getSQL().getKiosk(kioskId);
			
			//Make sure we have found one
			if (kiosk == null) {
				this.plugin.getLog().sendPlayerWarn(player, "Kiosk ID " + kioskId + " not found.");
				return true;
			}
			
			//Teleport player
			player.teleport(kiosk.getLocation());
			
			return true;
			
		}
		
		//====================================================================================================
		// /kiosk browse
		if (args[0].equalsIgnoreCase("browse")) {
			if (!player.hasPermission("MOCKiosk.manage") || !player.hasPermission("MOCKiosk.admin")) { this.plugin.getLog().sendPlayerWarn(player, "You do not have permission to use this command."); return true;}
			
			this.plugin.getGui().displayBrowserWindowGUI(player);
			
			return true;
			
		}
		
		//====================================================================================================
		// /kiosk reload
		if (args[0].equalsIgnoreCase("reload")) {
			if (!player.hasPermission("MOCKiosk.admin")) { this.plugin.getLog().sendPlayerWarn(player, "You do not have permission to use this command."); return true;}
			
			this.plugin.getConfiguration().reload();
			this.plugin.getLog().sendPlayerNormal(player, "Reload finished.");
			
			return true;
			
		}
		
		this.displayHelp(player);
		
    	return true;
		
	}
	
	private void displayHelp(SpoutPlayer player) {
		if (player.hasPermission("MOCKiosk.admin")) {
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk edit|delete|tp [id] - Edit|Delete|Teloport to Kiosk.");
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk browse - Browse Kiosks in the area.");
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk reload - Reloads the configuration file for the plugin.");
			return;
		}
		
		if (player.hasPermission("MOCKiosk.manage") && player.hasPermission("MOCKiosk.tp")) {
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk edit|delete|tp [id] - Edit|Delete|Teloport to Kiosk");
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk browse - Browse Kiosks in the area");
			return;
		}
		
		if (player.hasPermission("MOCKiosk.manage")) {
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk edit|delete [id] - Edit|Delete Kiosk");
			this.plugin.getLog().sendPlayerNormal(player, "/kiosk browse - Browse Kiosks in the area");
			return;
		}
		
	}
	
}
