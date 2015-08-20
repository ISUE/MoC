package net.moc.CodeBlocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CodeBlocksCommandExecutor implements CommandExecutor {
	//============================================================
	private CodeBlocks plugin;
	public CodeBlocksCommandExecutor(CodeBlocks plugin) { this.plugin = plugin; }
	//============================================================
	
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		//============================================================
    	//Make sure command was not sent from console
		if (sender instanceof SpoutPlayer == false){ plugin.getLog().info("/cb is only available in game."); return true; }

		SpoutPlayer player = (SpoutPlayer) sender;
		
		if (commandLabel.equalsIgnoreCase("cb")) { if (args.length > 0) { displayHelp(player); return true; } plugin.getGUI().displayMain(player); return true; }
		
		if (commandLabel.equalsIgnoreCase("cbf")) { plugin.getGUI().displayFunctionBrowser(player); return true; }
		
		if (commandLabel.equalsIgnoreCase("cbr")) { plugin.getGUI().displayRobotBrowser(player); return true; }
		
		if (commandLabel.equalsIgnoreCase("cbb")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
				Location loc = player.getLocation();
				World w = loc.getWorld();
				
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getToolItem(), 1));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getRobotBlock(), 64));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getAttackBlock(), 64));
				
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getBranchBlock(), 64));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getInteractionBlock(), 64));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getMovementBlock(), 64));
				
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getFunctionBlock(), 64));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getCallFunctionBlock(), 64));
				w.dropItemNaturally(loc, new SpoutItemStack(plugin.getBlocks().getWhiteSpaceBlock(), 64));
				
				return true;
				
			}
			
			plugin.getGUI().displayBlocks(player);
			
			return true;
			
		}
		//==============================================================================================

		displayHelp(player);

		return true;

    }

    private void displayHelp(SpoutPlayer player) {
		String help = "CodeBlocks Commands:\n" +
				"/cb - Display main GUI window\n" +
				"/cbf - Display functions list\n" +
				"/cbr - Display robots list\n" +
				"/cbb - Display blocks selector\n" +
				"/cbb all - Drop all CB blocks on the ground x64\n";
    	
		plugin.getLog().sendPlayerWarn(player.getName(), help);
		
	}
    
}
