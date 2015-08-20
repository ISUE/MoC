package moc.DreamCrafter;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCDreamCrafterCommandExecutor implements CommandExecutor {
	private MOCDreamCrafter plugin;	
	public MOCDreamCrafterCommandExecutor(MOCDreamCrafter plugin) { this.plugin = plugin; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().warn("/dc is only available in game.");
			return true;
			
		}
		
		final Player player = (Player) sender;
		
		if (args.length == 0) { 
			if(plugin.getWorldHandler().IsPlayerInBuildWorld(player)) plugin.getGui().showInBuildWorldWindowWindow((SpoutPlayer)player);
			else if(plugin.getWorldHandler().IsPlayerInDreamWorld(player)) plugin.getGui().showInDreamWorldWindowWindow((SpoutPlayer)player);
			else plugin.getGui().showMainWindow((SpoutPlayer)player);
			
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("properties")) { 
			if(plugin.getWorldHandler().IsPlayerInBuildWorld(player)) plugin.getGui().showWorldPropertiesWindow((SpoutPlayer)player);
			
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("blacklist")) { 
			if(plugin.getWorldHandler().IsPlayerInBuildWorld(player)) plugin.getGui().showWorldBlacklistWindow((SpoutPlayer)player);
			
		}
		
		if (args[0].equalsIgnoreCase("create")) { 
			// TODO: check permissions
			if ( args.length >= 3 ) {
				//String playerName = args[1];
				
				String tempWorldName = args[2];		// World name might contain spaces 
				for(int i=3;i<args.length;i++) 
					tempWorldName+=" "+args[i];
				final String worldName = tempWorldName;
				
				plugin.getLog().sendPlayerNormal(player, "Creating world.");
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							ActionResult a = plugin.getWorldHandler().CreateBuildWorld(worldName, player);
							player.sendMessage(a.PlayerMessage);
						}
					}, 5
				);
				
			}
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("build") ) { 
			// TODO: check permissions
			if ( args.length >= 2 ) {				
				String tempWorldName = args[1];		// World name might contain spaces 
				for(int i=2;i<args.length;i++) 
					tempWorldName+=" "+args[i];
				final String worldName = tempWorldName;
				
				plugin.getLog().sendPlayerNormal(player, "Loading build world...");
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							ActionResult a = plugin.getWorldHandler().StartBuildingWorld(worldName, player);
							player.sendMessage(a.PlayerMessage);
						}
					}, 5
				);
			}
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("import") ) { 
			// TODO: check permissions
			if ( args.length >= 3 ) {
				final String existingWorldName = args[1];
				
				String tempWorldName = args[3];		// World name might contain spaces 
				for(int i=3;i<args.length;i++) 
					tempWorldName+=" "+args[i];
				final String worldName = tempWorldName;
				
				plugin.getLog().info("Importing World: " + player.getName() + ", " + existingWorldName + ", " + worldName);
				
				plugin.getLog().sendPlayerNormal(player, "Loading world");
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							ActionResult a = plugin.getWorldHandler().CreateBuildWorldFromExistingWorld(existingWorldName, worldName, player);
							player.sendMessage(a.PlayerMessage);
						}
					}, 5
				);
			}
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("dream") ) { 
			// TODO: check permissions
			if ( args.length >= 2 ) {		
				String tempWorldName = args[1];		// World name might contain spaces 
				for(int i=2;i<args.length;i++) 
					tempWorldName+=" "+args[i];
				final String worldName = tempWorldName;
				
				plugin.getLog().info("Dreaming World: " + player.getName() + ", " + worldName);
				
				player.sendMessage("Creating dream...");
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							ActionResult a = plugin.getWorldHandler().StartDreamingWorld(worldName, player);
							player.sendMessage(a.PlayerMessage);
						}
					}, 5
				);				
			}
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("leave") ) { 
			
			if(plugin.getWorldHandler().IsPlayerInDreamWorld(player) || plugin.getWorldHandler().IsPlayerInBuildWorld(player)) {
				final String worldName = player.getWorld().getName();
				plugin.getLog().info("Leaving World: " + player.getName() + ", " + worldName);
				
				player.sendMessage("Leaving world...");
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							plugin.getWorldHandler().ReturnPlayerToNormalWorld(player);
							if(!plugin.getWorldHandler().UnloadWorld(worldName))
								plugin.getLog().warn("Unable to unload world " + worldName);
							if(plugin.getPersistentDataHandler().IsWorldDreamWorld(worldName))
									plugin.getPersistentDataHandler().deleteWorld(worldName);	
						}
					}, 5
				);					
			}
			else plugin.getLog().warn(player.getName() + " not in build or dream world");
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("delete") ) { 
			// TODO: check permissions
			if ( args.length >= 2 ) {	
				String tempWorldName = args[1];		// World name might contain spaces 
				for(int i=2;i<args.length;i++) 
					tempWorldName+=" "+args[i];
				final String worldName = tempWorldName;
				
				plugin.getLog().info("Deleting World: " + player.getName() + ", " + worldName);
				
				player.sendMessage("Deleting world...");
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(
					plugin, 
					new Runnable() {
						public void run() {
							ActionResult a = plugin.getWorldHandler().DeleteWorld(worldName, player);
							player.sendMessage(a.PlayerMessage);
						}
					}, 5
				);								
			}
			
			return true;
		}
		
		if ( args[0].equalsIgnoreCase("end") ) { 
			if(plugin.getWorldHandler().IsPlayerInBuildWorld(player)) {
				Block b = player.getTargetBlock(null, 200);
				if(b != null) {
					plugin.getPersistentDataHandler().setBuildWorldEndPoint(player.getWorld().getName(), b.getLocation());
					player.sendMessage("End point set");
				}
			}
			
			return true;
			
		}
		
		if ( args[0].equalsIgnoreCase("help") ) {
			plugin.getLog().sendPlayerNormal(player, "/dc - shows menu\n"
            + "/dc properties - shows world properties menu when building\n"
            + "/dc blacklist - shows block blacklist when building\n"
            + "/dc create [player] [worldname] - creates a new build world for a player\n"
            + "/dc build {worldname} - enters the given world to build in it\n"
            + "/dc dream {worldname} - enters the world as an instance to play\n"
            + "/dc import [existing worldname] [new worldname] - creates a build world using an existing world\n"
            + "/dc delete [worldname] - deletes a world\n"
            + "/dc leave - leave the build or dream world\n"
            + "/dc end - set world endpoint");
			return true;
			
		} else plugin.getLog().sendPlayerNormal(player, "Unknown Command");
		
		return true;
		
	}
	
}
