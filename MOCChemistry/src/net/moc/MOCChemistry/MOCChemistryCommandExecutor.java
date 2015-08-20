package net.moc.MOCChemistry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCChemistryCommandExecutor implements CommandExecutor {
	//============================================================
	private MOCChemistry plugin;
	public MOCChemistryCommandExecutor(MOCChemistry plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof SpoutPlayer == false){
			plugin.getLog().info("/mc is only available in game.");
			return true;
			
		}
		
		SpoutPlayer player = (SpoutPlayer) sender;
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				this.plugin.getLog().sendPlayerNormal(player, "Reloading recipes, settings ...");
				this.plugin.getConfiguration().reload();
				this.plugin.getRecipes().reload();
				this.plugin.getLog().sendPlayerNormal(player, "Reloading complete.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("123")) {
				this.plugin.getGui().displayChemistryRecipeEditorGUI(player, null, 0);
				return true;
				
			}
			
		}
		
		this.plugin.getGui().displayChemistryBookGUI(player);
		
		return true;
		
	}
	
}
