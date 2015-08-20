package net.dmg2.RegenBlock;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegenBlockCommandExecutor implements CommandExecutor{

	//============================================================
	private RegenBlock plugin;
	private static enum RootCommand { monitor, test, reload, blacklist, listselection, edit, select, create, remove, modify, list, rblacklist, type, sync, alarm, feedback, spawnblock, info, repop }
	public RegenBlockCommandExecutor(RegenBlock instance) {
		this.plugin = instance;
	}
	//============================================================
	
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/rb is only available in game.");
			return true;
		}
		
		//If no arguments display the default help message
		if (args.length == 0) return false;
		
		//Convert sender to Player
		Player player = (Player) sender;
		
		//Grab root command
		RootCommand rootCommand = this.getRootCommand(args[0].toLowerCase());
		
		//Execute the command
		if (rootCommand != null) {
			this.execute(rootCommand, args, player);
			return true;
		} else {
			return false;
		}

	}
	//###################################################################################################################################


	//###################################################################################################################################
	private void execute(RootCommand rootCommand, String[] args, Player player) {
		switch (rootCommand) {
			case monitor:
				this.executeMonitor(args, player);
				break;
			case test:
				this.executeTest(args, player);
				break;
			case reload:
				this.executeReload(args, player);
				break;
			case blacklist:
				this.executeBlacklist(args, player);
				break;
			case listselection:
				this.executeListselection(args, player);
				break;
			case edit:
				this.executeEdit(args, player);
				break;
			case select:
				this.executeSelect(args, player);
				break;
			case create:
				this.executeCreate(args, player);
				break;
			case remove:
				this.executeRemove(args, player);
				break;
			case modify:
				this.executeModify(args, player);
				break;
			case list:
				this.executeList(args, player);
				break;
			case rblacklist:
				this.executeRblacklist(args, player);
				break;
			case type:
				this.executeType(args, player);
				break;
			case sync:
				this.executeSync(args, player);
				break;
			case alarm:
				this.executeAlarm(args, player);
				break;
			case feedback:
				this.executeFeedback(args, player);
				break;
			case spawnblock:
				this.executeSpawnblock(args, player);
				break;
			case info:
				this.executeInfo(args, player);
				break;
			case repop:
				this.executeRepop(args, player);
				break;
		
		}
		
	}
	//###################################################################################################################################

	


	//###################################################################################################################################
	private void executeRepop(String[] args, Player player) {
		// /rb repop regionName
		if (args.length < 2) {
			this.printUse(player, RootCommand.repop);
			return;
		}
		
		//Get region name
		String regionName = args[1].toLowerCase();

		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		this.plugin.getQueue().regenRegion(regionName);
		
	}
	
	private void executeInfo(String[] args, Player player) {
		// /rb info
		
		//Get targeted block's region name
		Block block = player.getTargetBlock(null, 100);
		if (block == null) { this.plugin.getLog().sendPlayerWarn(player, "No region targeted."); return; }
		
		String regionName = this.plugin.getListenerBlock().getBlockRegion(block.getLocation());
		
		//Check if it's null (i.e. no block does not belong to a region)
		if (regionName == null) { this.plugin.getLog().sendPlayerWarn(player, "No region targeted."); return; }
		
		//Otherwise get region's stats
		this.plugin.getLog().sendPlayerRegionInfo(player, regionName);
		
	}
	
	private void executeSpawnblock(String[] args, Player player) {
		// /rb spawnblock [add/remove] regionName [id chance id chance...]
		
		if (args.length < 2) {
			this.printUse(player, RootCommand.spawnblock);
			return;
		}
		
		if (args[1].equalsIgnoreCase("add")) {
			//Get region name
			String regionName = args[2].toLowerCase();

			//Check if region exists
			if (plugin.getConfiguration().getRegionName(regionName) == null) {
				plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
				return;
			}
			
			if (args.length < 5) {
				this.printUse(player, RootCommand.spawnblock);
				return;
			}
			
			for (int i = 3 ; i + 1 < args.length ; i += 2) {
				int id = 0;
				try { id = Integer.parseInt(args[i]); } catch (NumberFormatException e) {}
				int chance = 0;
				try { chance = Integer.parseInt(args[i+1]); } catch (NumberFormatException e) {}
				if (id > 0 && chance > 0) this.plugin.getConfiguration().setRegionSpawnBlock(regionName, id, chance);
			}
			
			this.plugin.getLog().sendPlayerNormal(player, "Region spawn blocks: " + this.plugin.getConfiguration().getRegionSpawnBlocks(regionName));
			
		} else if (args[1].equalsIgnoreCase("remove")) {
			//Get region name
			String regionName = args[2].toLowerCase();

			//Check if region exists
			if (plugin.getConfiguration().getRegionName(regionName) == null) {
				plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
				return;
			}

			if (args.length < 4) {
				this.printUse(player, RootCommand.spawnblock);
				return;
			}
			
			for (int i = 3 ; i < args.length ; i++) {
				int id = 0;
				try { id = Integer.parseInt(args[i]); } catch (NumberFormatException e) {}
				if (id > 0) this.plugin.getConfiguration().removeRegionSpawnBlock(regionName, id);
			}

			this.plugin.getLog().sendPlayerNormal(player, "Region spawn blocks: " + this.plugin.getConfiguration().getRegionSpawnBlocks(regionName));

		} else {
			//just list

			//Get region name
			String regionName = args[1].toLowerCase();

			//Check if region exists
			if (plugin.getConfiguration().getRegionName(regionName) == null) {
				plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
				return;
			}
			
			this.plugin.getLog().sendPlayerNormal(player, "Region spawn blocks: " + this.plugin.getConfiguration().getRegionSpawnBlocks(regionName));
			
		}
		
		return;
		
	}
	private void executeFeedback(String[] args, Player player) {
		// /rb feedback regionName type
		
		if (args.length < 3) { //Did not specify region name and or type
			this.printUse(player, RootCommand.feedback);
			return;
		}
		
		// /rb feedback set regionName text
		if(args[1].equalsIgnoreCase("set")) {
			String feedbackString = "";
			for (int i = 3 ; i < args.length ; i++) {
				feedbackString += args[i] + " ";
			}
			if (feedbackString.length() > 0) {
				this.plugin.getConfiguration().setFeedbackString(feedbackString);
				this.plugin.getLog().sendPlayerNormal(player, "Feedback string was set to [" + feedbackString + "]");
			} else {
				this.plugin.getLog().sendPlayerWarn(player, "Feedback string was not changed.");
			}
			
			return;
		}
		
		//Get region name
		String regionName = args[1].toLowerCase();
		
		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		//Get region new feedback type
		int feedbackId = 0;
		try { feedbackId = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
		
		//Set region's feedback type
		feedbackId = this.plugin.getConfiguration().setRegionFeedbackID(regionName, feedbackId);
		
		//Message player
		plugin.getLog().sendPlayerNormal(player, "Region " + regionName + " feedback type was set to " + feedbackId);
		return;
		
	}
	//===================================================================================================================================
	private void executeAlarm(String[] args, Player player) {
		// /rb alarm time/message/radius name value
		if (args.length < 4) {
			this.printUse(player, RootCommand.alarm);
			return;
		}
		
		//Get region name
		String regionName = args[2].toLowerCase();

		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		if(args[1].equalsIgnoreCase("time")) {
			int time = 0;
			try { time = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
			this.plugin.getConfiguration().setRegionAlarmTime(regionName, time);
			this.plugin.getLog().sendPlayerNormal(player, "Alarm time of [" + regionName + "] was set to " + time + ".");
		}
		
		if(args[1].equalsIgnoreCase("message")) {
			String message = "";
			for (int i = 4 ; i < args.length ; i++) {
				message += args[i] + " ";
			}
			this.plugin.getConfiguration().setRegionAlarmMessage(regionName, message);
			this.plugin.getLog().sendPlayerNormal(player, "Alarm message of [" + regionName + "] was set to [" + message + "].");
		}
		
		if(args[1].equalsIgnoreCase("radius")) {
			int radius = 0;
			try { radius = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
			this.plugin.getConfiguration().setRegionAlarmRadius(regionName, radius);
			this.plugin.getLog().sendPlayerNormal(player, "Alarm radius of [" + regionName + "] was set to " + radius + ".");
		}
		
		return;
		
	}
	//===================================================================================================================================
	private void executeSync(String[] args, Player player) {
		// /rb sync regionName value
		if (args.length < 2) {
			this.printUse(player, RootCommand.sync);
			return;
		}
		
		//Get region name
		String regionName = args[1].toLowerCase();
		
		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		if (args.length < 3) {
			this.plugin.getLog().sendPlayerNormal(player, "Region [" + regionName + "] sync is " + this.plugin.getConfiguration().getRegionSync(regionName));
			return;
		}
		
		int sync = 0;
		try { sync = Integer.parseInt(args[2]);	} catch (NumberFormatException e) {}
		
		this.plugin.getConfiguration().setRegionSync(regionName, sync);
		
		this.plugin.getLog().sendPlayerNormal(player, "Region [" + regionName + "] sync was set to " + sync);
			
		return;
		
	}
	//===================================================================================================================================
	private void executeType(String[] args, Player player) {
		// /rb type name typeId
		if (args.length < 2) {
			this.printUse(player, RootCommand.type);
			return;
		}
		
		//Get region name
		String regionName = args[1].toLowerCase();

		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		if (args.length < 3) {
			this.plugin.getLog().sendPlayerNormal(player, "Region [" + regionName + "] type is " + this.plugin.getConfiguration().getRegionType(regionName));
			return;
		}
		
		int type = 0;
		try { type = Integer.parseInt(args[2]);	} catch (NumberFormatException e) {}
		
		this.plugin.getConfiguration().setRegionType(regionName, type);
		
		if (type == 1) {
			this.plugin.getConfiguration().regionAddSpawnBlocks(regionName);
		}

		this.plugin.getLog().sendPlayerNormal(player, "Region [" + regionName + "] type was set to " + type);
			
		return;
		
	}
	//===================================================================================================================================
	private void executeRblacklist(String[] args, Player player) {
		// /rb rblacklist regionName add/remove
		if (args.length < 2) {
			this.printUse(player, RootCommand.rblacklist);
			return;
		}

		//Get region name
		String regionName = args[1].toLowerCase();
		
		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}

		//Just wants to see blocks in the list
		if (args.length == 2) {
			this.plugin.getLog().sendPlayerNormal(player, "" + this.plugin.getConfiguration().listRegionBlacklistBlockId(regionName));
			return;
		}

		if (args[2].equalsIgnoreCase("add")) {
			for (int i = 3 ; i < args.length ; i++) { this.plugin.getConfiguration().addRegionBlacklistBlockId(args[2], Integer.parseInt(args[i])); }
		} else if (args[2].equalsIgnoreCase("remove")) {
			for (int i = 3 ; i < args.length ; i++) { this.plugin.getConfiguration().removeRegionBlacklistBlockId(args[2], Integer.parseInt(args[i])); }
		} else {
			this.printUse(player, RootCommand.rblacklist);
			return;
		}

		this.plugin.getLog().sendPlayerNormal(player, "Region's blacklist updated.");
			
		return;
		
	}
	//===================================================================================================================================
	private void executeList(String[] args, Player player) {
		// /rb list
		if (plugin.getConfiguration().listRegions() != null) {
			plugin.getLog().listRegion(player, plugin.getConfiguration().listRegions());
		} else {
			plugin.getLog().sendPlayerNormal(player, "There are no regions.");
		}
		return;
		
	}
	//===================================================================================================================================
	private void executeModify(String[] args, Player player) {
		// /rb modify [time] regionName respawnTime
		
		if(args.length < 2) { //Did not specify anything
			this.printUse(player, RootCommand.modify);
			return;
		}
		
		if (args[1].equalsIgnoreCase("time")){
			// /rb modify time regionName newTime
			if (args.length < 4) { //Did not actually enter name/time
				this.printUse(player, RootCommand.modify);
				return;
			}
			
			//Get name and time
			String regionName = args[2].toLowerCase();
			int respawnTime = 1;
			try { respawnTime = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
			if (respawnTime < 1) respawnTime = plugin.getConfiguration().getRegionDefaultRespawnTime();
			
			//Update time property for the region
			plugin.getConfiguration().setRegionRespawnTime(regionName, respawnTime);
			
			//Message player
			plugin.getLog().sendPlayerNormal(player, "Region " + regionName + " was updated to respawn time of " + respawnTime +"s.");
			
			//Log
			plugin.getLog().info(player.getName() + " updated region " + regionName + " to respawn time of " + respawnTime + "s.");
			return;
			
		} else {
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region name exists
			if (plugin.getConfiguration().getRegionName(regionName) == null) {
				plugin.getLog().sendPlayerWarn(player, "Region name does not exist.");
				return;
			}
			
			//Get re-spawn time for the region
			int respawnTime = 1;
			if (args.length == 3) {
				try { respawnTime = Integer.parseInt(args[2]); } catch (NumberFormatException e) {}
				if (respawnTime < 1) respawnTime = plugin.getConfiguration().getRegionRespawnTime(regionName);
			} else {
				respawnTime = plugin.getConfiguration().getRegionRespawnTime(regionName);
			}
			
			//Record region to configuration file
			plugin.getConfiguration().setRegion(regionName, respawnTime, plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()), plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()));
			
			//Print out the result to the player
			this.plugin.getLog().sendPlayerRegionInfo(player, regionName);
			return;

		}

	}
	//===================================================================================================================================
	private void executeRemove(String[] args, Player player) {
		// /rb remove regionName
		
		if (args.length == 1) { //Did not specify region name
			this.printUse(player, RootCommand.remove);
			return;
		}
		
		//Get region name
		String regionName = args[1].toLowerCase();
		
		//Check if region exists
		if (plugin.getConfiguration().getRegionName(regionName) == null) {
			plugin.getLog().sendPlayerWarn(player, "Region " + regionName + " does not exist.");
			return;
		}
		
		//Remove region
		plugin.getConfiguration().removeRegion(regionName);
		
		//Message player
		plugin.getLog().sendPlayerNormal(player, "Region " + regionName + " was removed.");
		
		//Log event
		plugin.getLog().info(player.getName() + " removed region " + regionName);
		
		return;
				
	}
	//===================================================================================================================================
	private void executeCreate(String[] args, Player player) {
		// /rb create regionName [time]
		
		//Check if both points are set
		if (plugin.getListenerPlayer().getPlayerSelectionLeft().containsKey(player.getName()) && plugin.getListenerPlayer().getPlayerSelectionRight().containsKey(player.getName())) {
			//We have two points
			if (args.length < 2) {
				this.printUse(player, RootCommand.create);
				return;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region name is already taken
			if (plugin.getConfiguration().getRegionName(regionName) != null) {
				plugin.getLog().sendPlayerWarn(player, "Region name is already in use.");
				return;
			}
			
			//Get re-spawn time for the region
			int respawnTime = 1;
			if (args.length == 3) {
				try { respawnTime = Integer.parseInt(args[2]); } catch (NumberFormatException e) {}
				if (respawnTime < 1) respawnTime = plugin.getConfiguration().getRegionDefaultRespawnTime();
			} else {
				respawnTime = plugin.getConfiguration().getRegionDefaultRespawnTime();
			}
			
			//Record region to configuration file
			plugin.getConfiguration().setRegion(regionName, respawnTime, plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()), plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()));
			
			//Print out the result to the player
			this.plugin.getLog().sendPlayerRegionInfo(player, regionName);
			
			return;

		}
		
		//We do not have both points selected
		plugin.getLog().sendPlayerWarn(player, "You need to select two points before creating a region.");
		return;

	}
	//===================================================================================================================================
	private void executeSelect(String[] args, Player player) {
		// /rb select
		
		if (args.length == 1) {
			//Check if player's status
			if (plugin.getListenerPlayer().getPlayerSelectionStatus().contains(player.getName())) {
				//If it does - remove from the list - turn off the mode
				plugin.getListenerPlayer().getPlayerSelectionStatus().remove(player.getName());
				//Message player
				plugin.getLog().sendPlayerNormal(player, "Selection mode is OFF");
			} else {
				//If it does - remove from the list - turn off the mode
				plugin.getListenerPlayer().getPlayerSelectionStatus().add(player.getName());
				//Message player
				plugin.getLog().sendPlayerNormal(player, "Selection mode is ON");
			}
			
		} else {
			if (!plugin.getListenerPlayer().getPlayerSelectionLeft().containsKey(player.getName()) || !plugin.getListenerPlayer().getPlayerSelectionRight().containsKey(player.getName())) {
				plugin.getLog().sendPlayerWarn(player, "Set both left and right points before modifying your selection through commands.");
				return;
				
			}
			
			if (args[1].equalsIgnoreCase("ex")) {
				// /rb select ex
				plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).setX(-1000000);
				plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).setX(1000000);
				this.executeListselection(args, player);
				return;
				
			} else if (args[1].equalsIgnoreCase("ey")) {
				// /rb select ey
				plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).setY(0);
				plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).setY(254);
				this.executeListselection(args, player);
				return;
				
			} else if (args[1].equalsIgnoreCase("ez")) {
				// /rb select ez
				plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).setZ(-1000000);
				plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).setZ(1000000);
				this.executeListselection(args, player);
				return;
				
			}
			
			this.printUse(player, RootCommand.select);
			
		}
	
		return;

	}
	//===================================================================================================================================
	private void executeEdit(String[] args, Player player) {
		// /rb edit
		
		//Check player's status
		if (plugin.getListenerPlayer().getPlayerEditStatus().contains(player.getName())) {
			//In editor mode
			plugin.getListenerPlayer().getPlayerEditStatus().remove(player.getName());
			//Message player
			plugin.getLog().sendPlayerNormal(player, "Edit mode is OFF");
		} else {
			//Not in editor mode
			plugin.getListenerPlayer().getPlayerEditStatus().add(player.getName());
			//Message player
			plugin.getLog().sendPlayerNormal(player, "Edit mode is ON");
		}

		return;
				
	}
	//===================================================================================================================================
	private void executeListselection(String[] args, Player player) {
		// /rb listselection
		
		String right = "Nothing is selected";
		String left = "Nothing is selected";

		if (plugin.getListenerPlayer().getPlayerSelectionLeft().containsKey(player.getName())) {
			left = plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).getBlockX() + " " + plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).getBlockY() + " " + plugin.getListenerPlayer().getPlayerSelectionLeft().get(player.getName()).getBlockZ();
		}

		if (plugin.getListenerPlayer().getPlayerSelectionRight().containsKey(player.getName())) {
			right = plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).getBlockX() + " " + plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).getBlockY() + " " + plugin.getListenerPlayer().getPlayerSelectionRight().get(player.getName()).getBlockZ();
		}
		
		plugin.getLog().sendPlayerNormal(player, "Right: " + right + " Left: " + left);

		return;
				
	}
	//===================================================================================================================================
	private void executeBlacklist(String[] args, Player player) {
		// /rb blacklist [add/remove] id id id id id..
		
		if (args.length < 2) {
			this.plugin.getLog().sendPlayerNormal(player, "" + this.plugin.getConfiguration().listBlacklistBlockId());
			return;
		}

		if (args.length < 3) {
			this.printUse(player, RootCommand.blacklist);
			return;
		}
		
		if (args[1].equalsIgnoreCase("add")) {
			for (int i = 2 ; i < args.length ; i++) { this.plugin.getConfiguration().addBlacklistBlockId(Integer.parseInt(args[i])); }
		} else if (args[1].equalsIgnoreCase("remove")) {
			for (int i = 2 ; i < args.length ; i++) { this.plugin.getConfiguration().removeBlacklistBlockId(Integer.parseInt(args[i])); }
		} else { 
			this.printUse(player, RootCommand.blacklist);
			return;
		}

		this.plugin.getLog().sendPlayerNormal(player, "Blacklist updated. " + this.plugin.getConfiguration().listBlacklistBlockId());
		
		return;
				
	}
	//===================================================================================================================================
	private void executeReload(String[] args, Player player) {
		// /rb reload
		this.plugin.getLog().sendPlayerNormal(player, "RegenBlock is reloading settings.");
		this.plugin.getConfiguration().reload();
		this.plugin.getLog().sendPlayerNormal(player, "Done.");
		
		return;
				
	}
	//===================================================================================================================================
	private void executeTest(String[] args, Player player) {
		// /rb test
	}
	//###################################################################################################################################
	private void executeMonitor(String[] args, Player player) {
		// /rb monitor [break/place] [true/false]
		if (args.length <= 2) {
			plugin.getLog().sendPlayerNormal(player, "Monitor place - " + plugin.getConfiguration().doMonitorPlace() + ", break - " + plugin.getConfiguration().doMonitorBreak());
			
		} else {
			if (args[1].equalsIgnoreCase("break")) {
				if (args[2].equalsIgnoreCase("false")) { plugin.getConfiguration().setMonitorBreak(false); }
				else { plugin.getConfiguration().setMonitorBreak(true); }
				
			} else if (args[1].equalsIgnoreCase("place")) {
				if (args[2].equalsIgnoreCase("false")) { plugin.getConfiguration().setMonitorPlace(false); }
				else { plugin.getConfiguration().setMonitorPlace(true); }
				
			} else {
				plugin.getLog().sendPlayerNormal(player, "Monitor place - " + plugin.getConfiguration().doMonitorPlace() + ", break - " + plugin.getConfiguration().doMonitorBreak());				
			}
			
		}
		
	}

	//###################################################################################################################################
	private RootCommand getRootCommand(String command) {
		RootCommand retval = null;
		try { retval = RootCommand.valueOf(command); } catch (IllegalArgumentException e){}
		return retval;
	}
	//###################################################################################################################################
	private void printUse(Player player, RootCommand command) {
		switch (command) {
		case blacklist:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb blacklist [add/remove] id id id id id.. - lists, adds, removes blacklisted block IDs for all regions.");
			break;
		case create:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb create (name) [re-spawn time] - creates a new region at points selected with optional re-spawn time, default otherwise.");
			break;
		case remove:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb remove (name) - removes region from the list.");
			break;
		case modify:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb modify (name) [re-spawn time] - modify existing region's location and time.");
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb modify time (name) (re-spawn time) - modify existing region's re-spawn time only.");
			break;
		case rblacklist:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb rblacklist (name) [add/remove] id id id id id.. - lists, adds, removes blacklisted block IDs for the region.");
			break;
		case type:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb type (name) (type - 0,1)- changes the region's type. 0 - normal, 1 - regen up only, with randomization based on spawnblocks");
			break;
		case sync:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb sync (name) (0/1/2/3)- changes the region's sync state. 0 - blocks repop separately, 1 - all at once based on first block broken, 2 - all at once based on last block broken, 3 - Same as 2, but preserving the order.");
			break;
		case alarm:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb alarm time/message/radius (name) (value)- changes the region's alarm settings.");
			break;
		case feedback:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb feedback (name) (feedback type [0,1,2])- changes the region's feedback type.");
			break;
		case repop:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb repop (name) - Respawns all blocks in a given region.");
			break;
		case spawnblock:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb spawnblock (name) - lists region's spawn blocks.");
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb spawnblock add (name) [id chance id chance...] - adds new blocks with spawn chance.");
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb spawnblock remove (name) [id id id...] - removes blocks.");
			break;
		case select:
			plugin.getLog().sendPlayerWarn(player, "Usage: /rb select [ex,ey,ez] - Toggles selection mode for the player. If ex/ey/ez is specified selection will be expanded in that direction. Y is vertical.");
			break;
			
		}
		
	}
	//###################################################################################################################################
	
}
