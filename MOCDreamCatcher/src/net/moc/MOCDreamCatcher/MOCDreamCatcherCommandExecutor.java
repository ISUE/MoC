package net.moc.MOCDreamCatcher;

import net.moc.MOCDreamCatcher.Data.DreamPlayer;
import net.moc.MOCDreamCatcher.Data.DreamPlayer.PlayerState;
import net.moc.MOCDreamCatcher.Data.Thought;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MOCDreamCatcherCommandExecutor implements CommandExecutor {
	private static enum DCCommand {admin, list, create, edit, dream, leave, delete, publish, set, unknown}

	private MOCDreamCatcher plugin;	
	public MOCDreamCatcherCommandExecutor(MOCDreamCatcher plugin) { this.plugin = plugin; }
	
	//=========================================================================================
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){ plugin.getLog().warn("/dc is only available in game."); return true; }
		
		//Grab the player
		Player player = (Player) sender;
		
		//No arguments - display GUI
		if (args.length == 0) { displayGUI(player); return true; }
		
		//Get command
		DCCommand command = getCommand(args[0]);
		
		switch (command) {
		case admin: displayAdminGUI(player); break;
		case list: executeList(player, args); break;
		case create: executeCreate(player, args); break;
		case edit: executeEdit(player, args); break;
		case dream: executeDream(player, args); break;
		case leave: executeLeave(player, args); break;
		case delete: executeDelete(player, args); break;
		case publish: executePublish(player, args); break;
		case set: executeSet(player, args); break;
		case unknown: printHelp(player); break;
			
		}
		
		return true;
		
	}

	//=========================================================================================
	private void displayGUI(Player player) {
		if (!plugin.isSpoutAvailable()) { printHelp(player); return; }
		
		if (plugin.getConfiguration().isFirstTimeUser(player.getName())) { plugin.getGUI().displayHelpWindow(player); }
		else plugin.getGUI().displayGUI(player);
		
	}

	//=========================================================================================
	private void displayAdminGUI(Player player) {
		if (!plugin.isSpoutAvailable()) { printHelp(player); return; }
		
		if (player.hasPermission("DreamCatcher.Admin")) plugin.getGUI().displayAdminWindow(player);
		else displayGUI(player);
		
	}

	//=========================================================================================
	private void executeList(Player player, String[] args) {
		String list = "";
		
		if (args.length == 1) {
			//List Dreams
			list += "List of available Dreams: [dream name - author name]\n";
			for (DreamPlayer dp : plugin.getDreamNet().getPlayers()) {
				for (Thought thought : dp.getThoughts()) {
					if (thought.isPublished()) list += thought.getName() + " - " +  dp.getName() + "; ";
					
				}
				
			}
			
		} else {
			//List Thoughts
			list += "List of Thoughts:\n";
			String statePublished = "Published", statePrivate = "Private";
			
			for (Thought thought : plugin.getDreamNet().getPlayer(player.getName()).getThoughts()) {
				String publishState;
				if (thought.isPublished()) publishState = statePublished ; else publishState = statePrivate;
				list += thought.getName() + " - " + publishState + "; ";
				
			}
			
		}
		
		plugin.getLog().sendPlayerNormal(player.getName(), list);

	}

	//=========================================================================================
	private void executeCreate(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName()); 
		if (dp == null) return;
		
		if (dp.getThoughts().size() >= plugin.getConfiguration().getMaxDreamsPerPerson()) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You already have a maximum number of worlds [" + dp.getThoughts().size() + "/" + plugin.getConfiguration().getMaxDreamsPerPerson() + "]");
			return;
		}
		
		if (dp.getState() == PlayerState.EDITING || dp.getState() == PlayerState.DREAMING) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You have to be awake to use this command. Use /dc leave first");
			return;
			
		}
		
		//Get thought name
		String newThoughtName = args[1].trim();
		newThoughtName = newThoughtName.replaceAll("[^A-Za-z0-9]", "");
		
		//Make sure not empty
		if (newThoughtName.length() == 0) { plugin.getLog().sendPlayerWarn(player.getName(), "Invalid thought name, please try again"); return; }
		
		//Make sure name is not taken
		if (dp.getThought(newThoughtName) != null) { plugin.getLog().sendPlayerWarn(player.getName(), "You already have a Thought by the name of " + args[1]); return; }
		
		//Start new thought
		plugin.getDreamNet().createThought(player.getName(), newThoughtName);
		
	}

	//=========================================================================================
	private void executeEdit(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName()); 
		if (dp == null) return;
		
		if (dp.getState() == PlayerState.EDITING || dp.getState() == PlayerState.DREAMING) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You have to be awake to use this command. Use /dc leave first");
			return;
			
		}
		
		if (dp.getThought(args[1]) != null) plugin.getDreamNet().startThought(player.getName(), args[1]);
		else plugin.getLog().sendPlayerWarn(player.getName(), "Thought " + args[1] + " was not found");

		
	}

	//=========================================================================================
	private void executeDream(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName()); 
		if (dp == null) return;
		
		if (dp.getState() == PlayerState.EDITING || dp.getState() == PlayerState.DREAMING) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You have to be awake to use this command. Use /dc leave first");
			return;
			
		}
		
		if (args.length == 2) {
			//Get author dream player
			DreamPlayer authorDP = dp;
			if (authorDP.getThought(args[1]) == null) { plugin.getLog().sendPlayerWarn(player.getName(), "Dream " + args[1] + " by " + args[2] + " was not found."); return; }

			plugin.getDreamNet().startDream(player.getName(), args[1], player.getName());
			
		} else {
			DreamPlayer authorDP = plugin.getDreamNet().getPlayer(args[2]);
			if (authorDP == null) { plugin.getLog().sendPlayerWarn(player.getName(), "Author " + args[2] + " was not found."); return; }
			if (authorDP.getThought(args[1]) == null) { plugin.getLog().sendPlayerWarn(player.getName(), "Dream " + args[1] + " by " + args[2] + " was not found."); return; }
			
			plugin.getDreamNet().startDream(player.getName(), args[1], args[2]);
			
		}
		
	}

	//=========================================================================================
	private void executeLeave(Player player, String[] args) {
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName());
		if (dp == null) return;
		
		if (dp.getState() == PlayerState.DREAMING) { plugin.getDreamNet().endDream(player.getName()); return; }
		
		if (dp.getState() == PlayerState.EDITING) {
			Thought thought = null;
			
			try { thought = plugin.getDreamNet().getPlayer(player.getName()).getThought(player.getLocation().getWorld().getName().split("_")[player.getLocation().getWorld().getName().split("_").length-1]); }
			catch (ArrayIndexOutOfBoundsException e) { plugin.getDreamNet().endThought(player.getName()); return; }
			
			if (thought == null) { plugin.getDreamNet().endThought(player.getName()); return; }
			
			plugin.getDreamNet().endThought(player.getName(), thought.getName());
			
		}
		
		plugin.getLog().sendPlayerWarn(player.getName(), "You are already awake");
		
	}

	//=========================================================================================
	private void executeDelete(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName()); 
		if (dp == null) return;
		
		if (dp.getState() == PlayerState.EDITING || dp.getState() == PlayerState.DREAMING) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You have to be awake to use this command. Use /dc leave first");
			return;
			
		}
		
		if (dp.getThought(args[1]) != null)
			plugin.getDreamNet().removeThought(player.getName(), args[1]);
		else 
			plugin.getLog().sendPlayerWarn(player.getName(), "Thought " + args[1] + " was not found.");
		
	}

	//=========================================================================================
	private void executePublish(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		DreamPlayer dp = plugin.getDreamNet().getPlayer(player.getName()); 
		if (dp == null) return;
		
		if (dp.getState() == PlayerState.EDITING || dp.getState() == PlayerState.DREAMING) {
			plugin.getLog().sendPlayerWarn(player.getName(), "You have to be awake to use this command. Use /dc leave first");
			return;
			
		}
		
		//Look up thought
		Thought t = plugin.getDreamNet().getPlayer(player.getName()).getThought(args[1]);
		if (t == null) { plugin.getLog().sendPlayerWarn(player.getName(), "Thought " + args[1] + " was not found."); return; }
		
		//Get new state
		boolean newState = !t.isPublished();
		
		t.setPublished(newState);
		plugin.getConfiguration().setDreamPlayerThought(player.getName(), t);
		
		if (newState) {
			//Record publish date
			plugin.getConfiguration().recordThoughtPublishDate(player.getName(), t.getName());
			
			//Display survey
			if (plugin.isSpoutAvailable()) plugin.getGUI().displaySurveyCreation(player);
			
			plugin.getLog().sendPlayerNormal(player.getName(), "Thought " + args[1] + " is published and visible to other players");
		
		} else {
			plugin.getLog().sendPlayerNormal(player.getName(), "Thought " + args[1] + " was set to private");

		}
		
	}

	//=========================================================================================
	private void executeSet(Player player, String[] args) {
		if (args.length < 2) { printHelp(player); return; }
		
		if (plugin.getDreamNet().getPlayer(player.getName()) == null) return;
		
		if (plugin.getDreamNet().getPlayer(player.getName()).getState() != PlayerState.EDITING) { plugin.getLog().sendPlayerWarn(player.getName(), "You have to be in a Thought to use this command"); return; }
		
		Thought thought = null;
		try { thought = plugin.getDreamNet().getPlayer(player.getName()).getThought(player.getLocation().getWorld().getName().split("_")[player.getLocation().getWorld().getName().split("_").length-1]); } catch (ArrayIndexOutOfBoundsException e) { return; }
		if (thought == null) return;
		
		if (args[1].equalsIgnoreCase("start")) {
			thought.setStart(player.getLocation());
			plugin.getDreamNet().getExternalPluginManager().getMultiverse().getMVWorldManager().getMVWorld(player.getLocation().getWorld()).setSpawnLocation(player.getLocation());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			plugin.getLog().sendPlayerNormal(player.getName(), "Start location set");
			return;
			
		}
		
		if (args[1].equalsIgnoreCase("end")) {
			thought.setEnd(player.getLocation());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			plugin.getLog().sendPlayerNormal(player.getName(), "End location set");
			return;
			
		}
		
		if (args[1].equalsIgnoreCase("inventory")) {
			thought.setInventory(player.getInventory());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			plugin.getLog().sendPlayerNormal(player.getName(), "Inventory set");
			return;
			
		}
		
		printHelp(player);
		
	}

	//=========================================================================================
	private DCCommand getCommand(String command) {
		for (DCCommand c : DCCommand.values()) { if (command.equalsIgnoreCase(c.toString())) return c; }
		
		return DCCommand.unknown;
		
	}

	//=========================================================================================
	private void printHelp(Player player) {
		String helpMessage = "\n";
		helpMessage += "/dc list - lists all available Dreams\n";
		helpMessage += "/dc list thoughts - list all player's Thoughts\n";
		helpMessage += "/dc create [name] - creates a new Thought\n";
		helpMessage += "/dc edit [name] - edit Thought world\n";
		helpMessage += "/dc dream [name] (player name) - dream a thought. Add player name if though is not yours\n";
		helpMessage += "/dc leave - exit Dream/Thought\n";
		helpMessage += "/dc delete [name] - delete Thought\n";
		helpMessage += "/dc publish [name] - un/publish Thought\n";
		helpMessage += "/dc set start - set start point for the Thought\n";
		helpMessage += "/dc set end - set end point for the Thought\n";
		helpMessage += "/dc set inventory - set start inventory for the Thought\n";
		
		plugin.getLog().sendPlayerNormal(player.getName(), helpMessage);
		
	}

}
