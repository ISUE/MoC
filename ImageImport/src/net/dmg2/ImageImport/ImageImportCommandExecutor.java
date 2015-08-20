package net.dmg2.ImageImport;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ImageImportCommandExecutor implements CommandExecutor {

	//============================================================
	private ImageImport plugin;
	public ImageImportCommandExecutor(ImageImport instance) {
		this.plugin = instance;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/ii is only available in game.");
			return true;
		}

		//Convert sender to Player
		Player player = (Player) sender;
		
		//If no arguments display the default help message
		if (args.length == 0) { this.plugin.getLog().sendPlayerNormal(player, "Usage: /ii image [height] - Imports image file into the world based on your location and heading."); return true; }
		
		if (this.plugin.getImageGenerator().playerInQueue(player.getName())) {
			this.plugin.getLog().sendPlayerWarn(player, "You already have an image in build queue. Please wait.");
			return true;
			
		}
		
		//=============================================================================================================================
		// /ii fileName [height]
    	//File imageFile = new File(plugin.getPluginPath() + File.separator + args[0]);
    	//File imageFile = new File(args[0]);
    	File imageFile;
		imageFile = new File(this.plugin.getPluginPath() + File.separator + "data" + File.separator + args[0]);

		if (!imageFile.canRead()) {
			this.plugin.getLog().sendPlayerWarn(player, "Unable to open image file. Make sure it is in the ImageImport/data folder");
			return true;
		}

    	//Start the generator
    	if (args.length > 1) {
    		int height = -1;
    		try { height = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
    		plugin.getImageGenerator().generate(imageFile, player, height);

    	} else {
    		plugin.getImageGenerator().generate(imageFile, player, -1);

    	}

    	return true;
		
	}

}
