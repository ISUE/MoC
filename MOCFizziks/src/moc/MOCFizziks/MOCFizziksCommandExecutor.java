package moc.MOCFizziks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MOCFizziksCommandExecutor implements CommandExecutor{

	//============================================================
	private MOCFizziks plugin;
	public MOCFizziksCommandExecutor(MOCFizziks plugin) { this.plugin = plugin; }
	//============================================================
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/mf is only available in game.");
			return true;
		}
		
		//If no arguments display the default help message
		if (args.length == 0) return false;
		
		//Convert sender to Player
		Player player = (Player) sender;
		
		//=============================================================================================================================
		//First argument - LISTPOINTS - Lists points currently selected
		if (args[0].equalsIgnoreCase("listpoints")) {
			//Make sure both locations are recorded
			if (plugin.getPlayerSelectionLeft().containsKey(player.getName()) && plugin.getPlayerSelectionRight().containsKey(player.getName())) {
				//If both locations has been set - Message player
				String right = plugin.getPlayerSelectionRight().get(player.getName()).getBlockX() + " " + plugin.getPlayerSelectionRight().get(player.getName()).getBlockY() + " " + plugin.getPlayerSelectionRight().get(player.getName()).getBlockZ();
				String left = plugin.getPlayerSelectionLeft().get(player.getName()).getBlockX() + " " + plugin.getPlayerSelectionLeft().get(player.getName()).getBlockY() + " " + plugin.getPlayerSelectionLeft().get(player.getName()).getBlockZ();
				plugin.getLog().sendPlayerNormal(player, "Right: " + right + " Left: " + left);
			} else {
				//Warn Player if not both locations are set
				plugin.getLog().sendPlayerWarn(player, "You need to set both spots first");
			}			
			return true;
		}

		//=============================================================================================================================
		//First argument - INFO
		if (args[0].equalsIgnoreCase("info")) {
			// /mf info
			
			//Get targeted block's region name
			Block tblock = player.getTargetBlock(null, 100);
			
			if (tblock == null) { this.plugin.getLog().sendPlayerWarn(player, "No region targeted."); return true; }
			
			MOCFizziksRegion region = plugin.getAPI().getWorld(player.getWorld()).getRegion(tblock);
			
			
			//Check if it's null (i.e. no block does not belong to a region)
			if (region == null) { this.plugin.getLog().sendPlayerWarn(player, "No region targeted."); return true; }
			
			//Otherwise get region's stats
			this.plugin.getLog().sendPlayerRegionInfo(player, region);
			return true;
		}
		
		//=============================================================================================================================
		//First Argument - SELECT - Turns selection mode ON/OFF
		if (args[0].equalsIgnoreCase("select")) {
			if (args.length == 1) {
				//Check player's status
				if (plugin.getPlayerSelectionStatus().contains(player.getName())) {
					//If it does - remove from the list - turn off the mode
					plugin.getPlayerSelectionStatus().remove(player.getName());
					//Message player
					plugin.getLog().sendPlayerNormal(player, "Selection mode is OFF");
				} else {
					//If it does - remove from the list - turn off the mode
					plugin.getPlayerSelectionStatus().add(player.getName());
					//Message player
					plugin.getLog().sendPlayerNormal(player, "Selection mode is ON");
				}
				return true;
			
			} else {
				if (!plugin.getPlayerSelectionLeft().containsKey(player.getName()) || !plugin.getPlayerSelectionRight().containsKey(player.getName())) {
					plugin.getLog().sendPlayerWarn(player, "Set both left and right points before modifying your selection through commands.");
					return true;
					
				}
				
				if (args[1].equalsIgnoreCase("ex")) {
					// /rb select ex
					plugin.getPlayerSelectionLeft().get(player.getName()).setX(-1000000);
					plugin.getPlayerSelectionRight().get(player.getName()).setX(1000000);
					return true;
					
				} else if (args[1].equalsIgnoreCase("ey")) {
					// /rb select ey
					plugin.getPlayerSelectionLeft().get(player.getName()).setY(0);
					plugin.getPlayerSelectionRight().get(player.getName()).setY(254);
					return true;
					
				} else if (args[1].equalsIgnoreCase("ez")) {
					// /rb select ez
					plugin.getPlayerSelectionLeft().get(player.getName()).setZ(-1000000);
					plugin.getPlayerSelectionRight().get(player.getName()).setZ(1000000);
					return true;
					
				}
				
				this.plugin.getLog().sendPlayerWarn(player, "/rb select (ex/ey/ez) - Toggles selection mode for the player. If ex/ey/ez is specified, selection will be extended in that direction.");
				return true;
				
			}
			
		}

		//=============================================================================================================================
		// /mf create
		if(args[0].equalsIgnoreCase("create")) {
			//Check if both points are set
			if (this.plugin.getPlayerSelectionLeft().containsKey(player.getName()) && this.plugin.getPlayerSelectionRight().containsKey(player.getName())) {
				//We have two points
				if (args.length == 1) {
					return false;
				}
				
				//Get region name
				String regionName = args[1].toLowerCase();
				
				//Check if region name is already taken
				if (plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName) != null) {
					plugin.getLog().sendPlayerWarn(player, "Region name is already in use.");
					return true;
					
				}
				
				//Record region to configuration file
				MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).addRegion(regionName, this.plugin.getPlayerSelectionLeft().get(player.getName()), this.plugin.getPlayerSelectionRight().get(player.getName()));
				
				//Print out the result to the player
				this.plugin.getLog().sendPlayerRegionCreate(player, r);
				
				return true;

			}
			
			//We do not have both points selected
			plugin.getLog().sendPlayerWarn(player, "You need to select two points before creating a region.");
			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf update
		if(args[0].equalsIgnoreCase("update")) {
			//Check if both points are set
			if (this.plugin.getPlayerSelectionLeft().containsKey(player.getName()) && this.plugin.getPlayerSelectionRight().containsKey(player.getName())) {
				//We have two points
				if (args.length == 1) {
					return false;
				}
				
				//Get region name
				String regionName = args[1].toLowerCase();
				
				//Check if region name exists
				MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
				
				if (r == null) {
					plugin.getLog().sendPlayerWarn(player, "Region name not found.");
					return true;
					
				}
				
				//Record region to configuration file
				r.setLocations(plugin.getPlayerSelectionLeft().get(player.getName()), plugin.getPlayerSelectionRight().get(player.getName()));
				plugin.getConfiguration().saveRegion(r);

				//Print out the result to the player
				plugin.getLog().sendPlayerRegionCreate(player, r);
				
				updateSigns(r);
				
				return true;

			}
			
			//We do not have both points selected
			plugin.getLog().sendPlayerWarn(player, "You need to select two points before creating a region.");
			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf toggle
		if (args[0].equalsIgnoreCase("toggle")) {
			if (args.length == 1) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			r.setOn(!r.isOn());
			plugin.getConfiguration().saveRegion(r);
			
			if (r.isOn()) plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] was turned on");
			else plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] was turned off");
			
			updateSigns(r);
			
			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf togglepower
		if (args[0].equalsIgnoreCase("togglepower")) {
			if (args.length == 1) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			r.setUsesPower(!r.usesPower());
			plugin.getConfiguration().saveRegion(r);
			
			if (r.usesPower()) plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] was set to use power switches");
			else plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] was set to not use power switches");
			
			updateSigns(r);

			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf setvel
		if (args[0].equalsIgnoreCase("setvel")) {
			if (args.length < 5) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			double x = 0.0, y = 0.0, z = 0.0;
			try { x = Double.parseDouble(args[2]); } catch(NumberFormatException e) {}
			try { y = Double.parseDouble(args[3]); } catch(NumberFormatException e) {}
			try { z = Double.parseDouble(args[4]); } catch(NumberFormatException e) {}
			
			r.setVelocity(x, y, z);
			plugin.getConfiguration().saveRegion(r);

			plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] New Velocity [ " + r.getVelocity() + " ]");
			
			updateSigns(r);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf addvel
		if (args[0].equalsIgnoreCase("addvel")) {
			if (args.length < 5) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name is not found."); return true; }
			
			double x = 0.0, y = 0.0, z = 0.0;
			try { x = Double.parseDouble(args[2]); } catch(NumberFormatException e) {}
			try { y = Double.parseDouble(args[3]); } catch(NumberFormatException e) {}
			try { z = Double.parseDouble(args[4]); } catch(NumberFormatException e) {}
			
			r.setVelocity(r.getVelocity().add(new Vector(x, y, z)));
			plugin.getConfiguration().saveRegion(r);

			plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] New Velocity [ " + r.getVelocity() + " ]");
			
			updateSigns(r);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf setacc
		if (args[0].equalsIgnoreCase("setacc")) {
			if (args.length < 5) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			double x = 0.0, y = 0.0, z = 0.0;
			try { x = Double.parseDouble(args[2]); } catch(NumberFormatException e) {}
			try { y = Double.parseDouble(args[3]); } catch(NumberFormatException e) {}
			try { z = Double.parseDouble(args[4]); } catch(NumberFormatException e) {}
			
			r.setAcceleration(x, y, z);
			plugin.getConfiguration().saveRegion(r);

			plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] New Acceleration [ " + r.getAcceleration() + " ]");
			
			updateSigns(r);

			return true;

		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf addacc
		if (args[0].equalsIgnoreCase("addacc")) {
			if (args.length < 5) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			double x = 0.0, y = 0.0, z = 0.0;
			try { x = Double.parseDouble(args[2]); } catch(NumberFormatException e) {}
			try { y = Double.parseDouble(args[3]); } catch(NumberFormatException e) {}
			try { z = Double.parseDouble(args[4]); } catch(NumberFormatException e) {}
			
			r.setAcceleration(r.getAcceleration().add(new Vector(x, y, z)));
			plugin.getConfiguration().saveRegion(r);
			
			plugin.getLog().sendPlayerNormal(player, "[" + regionName +"] New Acceleration [ " + r.getAcceleration() + " ]");
			
			updateSigns(r);

			return true;

		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf remove
		if(args[0].equalsIgnoreCase("remove")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Remove region
			if (!plugin.getAPI().getWorld(player.getWorld()).removeRegion(regionName)) {
				plugin.getLog().sendPlayerWarn(player, "Region name not found.");
				return true;
				
			}
				
			//Message player
			plugin.getLog().sendPlayerNormal(player, "Region " + regionName + " was removed.");
			
			//Log event
			plugin.getLog().info(player.getName() + " removed region " + regionName);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf addswitch [name]
		if(args[0].equalsIgnoreCase("addswitch")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Get player's target block
			Block targetBlock = player.getTargetBlock(null, 100);
			
			if ( !(targetBlock.getType() == Material.LEVER ||
					targetBlock.getType() == Material.STONE_BUTTON ||
					targetBlock.getType() == Material.WOOD_BUTTON ||
					targetBlock.getType() == Material.STONE_PLATE ||
					targetBlock.getType() == Material.WOOD_PLATE) ) {
				//No valid target present
				plugin.getLog().sendPlayerWarn(player, "Please target lever, button, or pressure plate when issuing this command");
				return true;
				
			}
			
			//Add toggle to the region
			r.addSwitch(targetBlock);
			plugin.getConfiguration().saveRegion(r);

			//Message player
			plugin.getLog().sendPlayerNormal(player, "Added switch of type [" + targetBlock.getType() + "] to the region [" + regionName + "]");
			
			//Log event
			plugin.getLog().info(player.getName() + " added switch to " + regionName);

			updateSigns(r);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf removeswitch [name]
		if(args[0].equalsIgnoreCase("removeswitch")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Get player's target block
			Block targetBlock = player.getTargetBlock(null, 100);
			
			if ( !(targetBlock.getType() == Material.LEVER ||
					targetBlock.getType() == Material.STONE_BUTTON ||
					targetBlock.getType() == Material.STONE_PLATE ||
					targetBlock.getType() == Material.WOOD_PLATE) ) {
				//No valid target present
				plugin.getLog().sendPlayerWarn(player, "Please target lever, button, or pressure plate when issuing this command");
				return true;
			}
			
			//Remove toggle from the region
			boolean retval = r.removeSwitch(targetBlock);
			plugin.getConfiguration().saveRegion(r);

			if (retval) {
				//Message player
				plugin.getLog().sendPlayerNormal(player, "Removed switch of type [" + targetBlock.getType() + "] from the region [" + regionName + "]");
				//Log event
				plugin.getLog().info(player.getName() + " removed switch from " + regionName);
				
				updateSigns(r);
				
			} else {
				plugin.getLog().sendPlayerWarn(player, "The switch is not assigned to the region [" + regionName + "]");
				
			}

			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf clearswitch [name]
		if(args[0].equalsIgnoreCase("clearswitch")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Remove all
			r.removeSwitches();
			plugin.getConfiguration().saveRegion(r);

			//Message player
			plugin.getLog().sendPlayerNormal(player, "Cleared all switches from the region [" + regionName + "]");
			
			//Log event
			plugin.getLog().info(player.getName() + " Cleared all switches from " + regionName);
			
			updateSigns(r);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf listswitch [name]
		if(args[0].equalsIgnoreCase("listswitch")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			String switches = "";
			for (Location l : r.getSwitches()) switches += "[x" + l.getBlockX() + "y" + l.getBlockY() + "z" + l.getBlockZ() + "]";
			
			//Message player
			plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] " + switches);
			
			return true;
			
		}

		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf addsign [name]
		if(args[0].equalsIgnoreCase("addsign")) {
			if (args.length == 1) return false;

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Get player's target block
			Block targetBlock = player.getTargetBlock(null, 100);
			
			if ( !(targetBlock.getType() == Material.SIGN_POST ||
					targetBlock.getType() == Material.WALL_SIGN) ) {
				//No valid target present
				plugin.getLog().sendPlayerWarn(player, "Please target a sign when issuing this command");
				return true;
			}
			
			//Add toggle to the region
			r.addSign(targetBlock);
			plugin.getConfiguration().saveRegion(r);

			//Message player
			plugin.getLog().sendPlayerNormal(player, "Added sign to the region [" + regionName + "]");

			//Log event
			plugin.getLog().info(player.getName() + " added sign to " + regionName);
			
			updateSigns(r);

			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf removesign [name]
		if(args[0].equalsIgnoreCase("removesign")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Get player's target block
			Block targetBlock = player.getTargetBlock(null, 100);
			
			if ( !(targetBlock.getType() == Material.SIGN_POST ||
					targetBlock.getType() == Material.WALL_SIGN) ) {
				//No valid target present
				plugin.getLog().sendPlayerWarn(player, "Please target a sign when issuing this command");
				return true;
			}
			
			//Remove toggle from the region
			boolean retval = r.removeSign(targetBlock);
			plugin.getConfiguration().saveRegion(r);

			if (retval) {
				//Message player
				plugin.getLog().sendPlayerNormal(player, "Removed sign from the region [" + regionName + "]");
				//Log event
				plugin.getLog().info(player.getName() + " removed sign from " + regionName);
				
				updateSigns(r);
				
			} else {
				plugin.getLog().sendPlayerWarn(player, "This sign is not assigned to the region [" + regionName + "]");
				
			}

			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf clearsign [name]
		if(args[0].equalsIgnoreCase("clearsign")) {
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Remove all
			r.removeSigns();
			plugin.getConfiguration().saveRegion(r);
			
			//Message player
			plugin.getLog().sendPlayerNormal(player, "Cleared all signs from the region [" + regionName + "]");
			
			//Log event
			plugin.getLog().info(player.getName() + " Cleared all signs from " + regionName);
			
			return true;
			
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf listsign [name]
		if(args[0].equalsIgnoreCase("listsign")) {
			//Did not specify region name
			if (args.length == 1) return false;
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			MOCFizziksRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Message player
			String signs = "";
			for (Location l : r.getSigns()) signs += "[x" + l.getBlockX() + "y" + l.getBlockY() + "z" + l.getBlockZ() + "]";
			
			plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] " + signs);
			
			return true;
			
		}
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /mf list
		if(args[0].equalsIgnoreCase("list")) {
			plugin.getLog().listRegions(player);
			
			return true;

		}
		
		return false;
		
	}

	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	public void updateSigns(MOCFizziksRegion r) {
		if (r.getSigns().size() == 0) return;
		
		for (Location l : r.getSigns()) {
			//Fetch the block
			Block block = l.getBlock();
			
			Sign sign = (Sign) block.getState();
			
			sign.setLine(0, "[R] " + r.getName());
			sign.setLine(1, "[V] " + r.getVelocity());
			sign.setLine(2, "[A] " + r.getAcceleration());
			sign.setLine(3, "[S] " + r.isOn());
			
			sign.update();
			
		}

	}
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
}
