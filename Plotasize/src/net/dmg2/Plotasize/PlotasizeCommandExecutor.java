package net.dmg2.Plotasize;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotasizeCommandExecutor implements CommandExecutor {

	//============================================================
	private Plotasize plugin;
	private static enum RootCommand { create, settings, number, xyz, buffer, clearance, debug, reload, center }
	public PlotasizeCommandExecutor(Plotasize plugin) {
		this.plugin = plugin;
	}
	//============================================================

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.log.info("/ps is only available in game.");
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
	private void execute(RootCommand rootCommand, String[] args, Player player) {
		switch (rootCommand) {
			case create:
				this.executeCreate(args, player);
				break;
			case settings:
				this.executeSettings(args, player);
				break;
			case number:
				this.executeNumber(args, player);
				this.executeSettings(args, player);
				break;
			case xyz:
				this.executeXYZ(args, player);
				this.executeSettings(args, player);
				break;
			case buffer:
				this.executeBuffer(args, player);
				this.executeSettings(args, player);
				break;
			case clearance:
				this.executeClearance(args, player);
				this.executeSettings(args, player);
				break;
			case debug:
				this.executeDebug(args, player);
				break;
			case reload:
				this.executeReload(args, player);
				this.executeSettings(args, player);
				break;
			case center:
				this.executeCenter(args, player);
				this.executeSettings(args, player);
				break;
		
		}
		
	}
	//###################################################################################################################################
	private void executeCenter(String[] args, Player player) {
		// /ps center value
		
		if (args.length < 2) { 
			this.printUse(player, RootCommand.center);
			return;
		}
		
		int value = 0;
		try { value = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
		
		if (value == 0) this.plugin.config.setCenterOnPlayer(false);
		else this.plugin.config.setCenterOnPlayer(true);
		
	}
	
	private void executeClearance(String[] args, Player player) {
		// /ps clearance value

		if (args.length < 2) { 
			this.printUse(player, RootCommand.clearance);
			return;
		}
		
		int value = 0;
		try { value = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
		if (value == 0) return;
		
		this.plugin.config.setClearance(value);
		
	}

	private void executeBuffer(String[] args, Player player) {
		// /ps buffer value

		if (args.length < 2) { 
			this.printUse(player, RootCommand.buffer);
			return;
		}
		
		int value = 0;
		try { value = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
		if (value == 0) return;
		
		this.plugin.config.setPlotBuffer(value);
		
	}

	private void executeXYZ(String[] args, Player player) {
		// /ps xyz value value value

		if (args.length < 4) { 
			this.printUse(player, RootCommand.xyz);
			return;
		}
		
		int x = 0;
		int y = 0;
		int z = 0;
		try { x = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
		try { y = Integer.parseInt(args[2]); } catch (NumberFormatException e) {}
		try { z = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
		if (x == 0 || y == 0 || z == 0) return;
		
		this.plugin.config.setPlotX(x);
		this.plugin.config.setPlotY(y);
		this.plugin.config.setPlotZ(z);
		
	}

	private void executeNumber(String[] args, Player player) {
		// /ps number value

		if (args.length < 2) { 
			this.printUse(player, RootCommand.number);
			return;
		}
		
		int value = 0;
		try { value = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
		if (value == 0) return;
		
		this.plugin.config.setNumberOfPlots(value);
		
	}

	private void executeSettings(String[] args, Player player) {
		// /ps settings
		
		this.plugin.log.sendPlayerSettings(player);
		
	}

	private void executeCreate(String[] args, Player player) {
		// /ps create
		
		this.plugin.log.sendPlayerNormal(player, "Starting plot generation. Please wait.");
		this.plugin.generateBluePrint(player);
	}
	
	private void executeReload(String[] args, Player player) {
		// /ps reload
		
		if (this.plugin.configFile.exists()) {
			this.plugin.log.sendPlayerNormal(player, "Plotasize is reloading settings.");
			this.plugin.config.load();
			this.plugin.log.sendPlayerNormal(player, "Done.");
		} else {
			this.plugin.log.sendPlayerWarn(player, "Plotasize was unable to find settings file.");
		}

	}

	private void executeDebug(String[] args, Player player) {
		// /ps debug
		
		if (plugin.doDebug) {
			plugin.doDebug = false;
			//Message player
			plugin.log.sendPlayerNormal(player, "Debug mode is OFF");
			plugin.log.info("Debug mode is turned OFF by " + player.getName());
		} else {
			plugin.doDebug = true;
			//Message player
			plugin.log.sendPlayerNormal(player, "Debug mode is ON");
			plugin.log.info("Debug mode is turned ON by " + player.getName());
		}

	}

	//###################################################################################################################################
	private void printUse(Player player, RootCommand command) {
		switch (command) {
		case buffer:
			plugin.log.sendPlayerWarn(player, "Usage: /ps buffer (value) - Sets buffer size between the plots.");
			break;
		case clearance:
			plugin.log.sendPlayerWarn(player, "Usage: /ps clearnace (value) - Sets clearance between the plots and rest of the world.");
			break;
		case number:
			plugin.log.sendPlayerWarn(player, "Usage: /ps number (value) - Sets number of plots. (Will be rounded up to next complete square - 8 to 9 etc).");
			break;
		case xyz:
			plugin.log.sendPlayerWarn(player, "Usage: /ps xyz (value value value) - Sets plot's width, length, and height.");
			break;
		case create:
			plugin.log.sendPlayerWarn(player, "Usage: /ps center (value) - Sets plots' center location. 0 - at the center of the world, 1 - at player's current location.");
			break;
		}
		
	}
	//###################################################################################################################################
	private RootCommand getRootCommand(String command) {
		RootCommand retval = null;
		try { retval = RootCommand.valueOf(command); } catch (IllegalArgumentException e){}
		return retval;
	}
	//###################################################################################################################################

}
