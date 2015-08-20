package net.moc.MOC3DImporter;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MOC3DImporterCommandExecutor implements CommandExecutor {
	//============================================================
	private MOC3DImporter plugin;
	public MOC3DImporterCommandExecutor(MOC3DImporter plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/3di is only available in game.");
			return true;
			
		}
		
		//Grab player
		Player player = (Player) sender;
		
		//Check for arguments
		// /3di name.obj maxHeight minHeight
		if (args.length < 1) { this.displayHelp(player); return true; }
		
		//==================================================================
		if (args[0].equalsIgnoreCase("reload")) {
			this.plugin.getLog().sendPlayerNormal(player, "Reloading settings.");
			this.plugin.getConfiguration().reload();
			this.plugin.getLog().sendPlayerNormal(player, "Done.");
			return true;
			
		}
		
		//==================================================================
		if (args[0].equalsIgnoreCase("settings")) {
			this.plugin.getLog().sendPlayerNormal(player, "---------------------------------------------------");
			this.plugin.getLog().sendPlayerNormal(player, "Queue Delay: " + this.plugin.getConfiguration().getQueueDelay());
			this.plugin.getLog().sendPlayerNormal(player, "Blocks per queue: " + this.plugin.getConfiguration().getBlocksPerQueue());
			this.plugin.getLog().sendPlayerNormal(player, "Block type: " + Material.getMaterial(this.plugin.getConfiguration().getBlockTypeId()));
			this.plugin.getLog().sendPlayerNormal(player, "Block data: " + this.plugin.getConfiguration().getBlockData());
			this.plugin.getLog().sendPlayerNormal(player, "---------------------------------------------------");
			return true;
			
		}
		
		//==================================================================
		if (args[0].equalsIgnoreCase("setblocks")) {
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int blocks = -1;
			try { blocks = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; };
			
			if (blocks <= 0) blocks = 1000;
			
			this.plugin.getConfiguration().setBlocksPerQueue(blocks);
			
			this.plugin.getLog().sendPlayerNormal(player, "Updated blocks per queue to " + blocks + ".");
			
			return true;
			
		}
		
		//==================================================================
		if (args[0].equalsIgnoreCase("setdelay")) {
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int delay = -1;
			try { delay = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; };
			
			if (delay < 1000) delay = 1000;
			
			this.plugin.getConfiguration().setQueueDelay(delay);
			
			this.plugin.getLog().sendPlayerNormal(player, "Updated queue delay to " + delay + ".");
			
			return true;
			
		}
		
		//==================================================================
		if (args[0].equalsIgnoreCase("setid")) {
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int id = -1;
			try { id = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; };
			
			if (Material.getMaterial(id) == null) { this.plugin.getLog().sendPlayerNormal(player, "Invalid material"); return true; }
			
			this.plugin.getConfiguration().setBlockTypeId(id);
			this.plugin.getConfiguration().setBlockData(0);
			
			this.plugin.getLog().sendPlayerNormal(player, "Updated default block to " + Material.getMaterial(id) + ". Data value reset to 0.");
			
			return true;
			
		}
		
		//==================================================================
		if (args[0].equalsIgnoreCase("setdata")) {
			if (args.length < 2) { this.displayHelp(player); return true; }
			
			int data = -1;
			try { data = Integer.parseInt(args[1]); } catch (NumberFormatException e) { this.displayHelp(player); return true; };
			
			//Fix input errors
			if (data < 0 || data > 15) data = 0;
			
			this.plugin.getConfiguration().setBlockData(data);
			this.plugin.getLog().sendPlayerNormal(player, "Updated default block data to " + data);
			
			return true;
			
		}
		
		//==================================================================
		//Check queue for the player
		if (this.plugin.getConverter().getPlayerMatrixLocation(player.getName()) != null) {
			//Something is already being built for the player
			this.plugin.getLog().sendPlayerWarn(player, "You can only queue one OBJ at a time for building. Please wait.");
			return true;
			
		}
		
		//Check to make sure file exists
		File objFile = new File(this.plugin.getDataPath().getAbsolutePath() + File.separator + args[0]);
		if (!objFile.exists()) { this.plugin.getLog().sendPlayerWarn(player, objFile.getName() + " file not found."); return true; }
		
		//Grab location
		Location location = player.getLocation();
		
		float minHeight;
		
		if (location.getBlockY() < 0 || location.getBlockY() > this.plugin.getMaxHeight()) {
			//Player is in invalid location
			minHeight = this.plugin.getMinHeight();
			location.setY(minHeight);
			
		} else {
			//Player's location is good
			minHeight = location.getBlockY();
		}
			
		float maxHeight = this.plugin.getMaxHeight();
		
		boolean useTexture = false;
		
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("-t")) {
				useTexture = true;
				if (args.length > 2) {
					//Max height was suggested
					int argHeight = -1;
					try { argHeight = Integer.parseInt(args[2]); } catch (NumberFormatException e) {};
					if (argHeight != -1) {
						if (argHeight + minHeight <= maxHeight && argHeight > 0) {
							maxHeight = argHeight + minHeight;

						}

					}
					
				}

			} else {
				//Max height was suggested
				int argHeight = -1;
				try { argHeight = Integer.parseInt(args[1]); } catch (NumberFormatException e) {};
				if (argHeight != -1) {
					if (argHeight + minHeight <= maxHeight && argHeight > 0) {
						maxHeight = argHeight + minHeight;

					}

				}
				
			}

		}
		
		//Generate matrix
		this.plugin.getConverter().generateMatrix(objFile, player, location, maxHeight, minHeight, useTexture);
		
    	return true;
		
	}
	
	private void displayHelp(Player player) {
		if (player.hasPermission("MOC3DImporter.use")) {
			this.plugin.getLog().sendPlayerNormal(player, "/3di reload - Reloads config.yml");
			this.plugin.getLog().sendPlayerNormal(player, "/3di setblocks [value] - Sets amount of blocks per queue");
			this.plugin.getLog().sendPlayerNormal(player, "/3di setdelay [value] - Sets time between queue calls in milliseconds");
			this.plugin.getLog().sendPlayerNormal(player, "/3di setid [value] - Sets default block type id.");
			this.plugin.getLog().sendPlayerNormal(player, "/3di setdata [value] - Sets default block data value (for wool colors etc.)");
			this.plugin.getLog().sendPlayerNormal(player, "/3di settings - Displays plugin settings");
			this.plugin.getLog().sendPlayerNormal(player, "/3di [file name] (-t) (height) - Load Wavefront OBJ file and build it in the world starting at your location.");
			return;
		}
		
	}
	
}
