package net.dmg2.GravitySheep;

import java.util.ArrayList;
import net.dmg2.GravitySheep.api.GravitySheepRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GravitySheepCommandExecutor implements CommandExecutor{

	//============================================================
	private GravitySheep plugin;
	public GravitySheepCommandExecutor(GravitySheep instance) {
		this.plugin = instance;
	}
	//============================================================
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.getLog().info("/gs is only available in game.");
			return true;
		}
		
		//If no arguments display the default help message
		if (args.length == 0) return false;
		//Convert sender to Player
		Player player = (Player) sender;

		
		//=============================================================================================================================
		//First argument - LISTPOINT - Lists point currently selected
		if (args[0].equalsIgnoreCase("listpoint")) {
			//Make left location is set
			if (plugin.getPlayerSelectionLeft().containsKey(player.getName())) {
				String left = plugin.getPlayerSelectionLeft().get(player.getName()).getBlockX() + " " + plugin.getPlayerSelectionLeft().get(player.getName()).getBlockY() + " " + plugin.getPlayerSelectionLeft().get(player.getName()).getBlockZ();
				plugin.getLog().sendPlayerNormal(player, "Block selected: " + left);
			} else {
				//Warn Player if nothing is selected
				plugin.getLog().sendPlayerWarn(player, "You need to select something first");
			}			
			return true;
		}
		
		//=============================================================================================================================
		//First Argument - SELECT - Turns selection mode ON/OFF
		if (args[0].equalsIgnoreCase("select")) {
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
		}

		//=============================================================================================================================
		// /gs create
		if(args[0].equalsIgnoreCase("create")) {
			//Check if both points are set
			if (this.plugin.getPlayerSelectionLeft().containsKey(player.getName())) {
				//We have a point selected
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
				
				//Record region
				GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).addRegion(regionName, this.plugin.getPlayerSelectionLeft().get(player.getName()));
				
				//Print out the result to the player
				plugin.getLog().sendPlayerRegionCreate(player, r);
				
				return true;

			}
			//We do not have both points selected
			plugin.getLog().sendPlayerWarn(player, "You need to select a point with left click before creating a region.");
			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /gs update
		if(args[0].equalsIgnoreCase("update")) {
			//Check if both points are set
			if (this.plugin.getPlayerSelectionLeft().containsKey(player.getName())) {
				//We have a point
				if (args.length == 1) {
					return false;
				}
				
				//Get region name
				String regionName = args[1].toLowerCase();
				
				//Check if region name exists
				GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
				
				if (r == null) {
					plugin.getLog().sendPlayerWarn(player, "Region name not found.");
					return true;
					
				}
				
				//Record region to configuration file
				r.setBase(plugin.getPlayerSelectionLeft().get(player.getName()));
				plugin.getConfiguration().saveRegion(r);

				//Print out the result to the player
				plugin.getLog().sendPlayerRegionCreate(player, r);
				
				updateSigns(r);
				
				return true;

			}
			//We do not have both points selected
			plugin.getLog().sendPlayerWarn(player, "You need to select a point with left click before creating a region.");
			return true;
		}

		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /gs setvel
		if (args[0].equalsIgnoreCase("setvel"))
		{
			if (args.length < 5) {
				return false;
			}

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region name exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			
			if (r == null) {
				plugin.getLog().sendPlayerWarn(player, "Region name not found.");
				return true;
				
			}
			
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
		// /gs addvel
		if (args[0].equalsIgnoreCase("addvel"))
		{
			if (args.length < 5) {
				return false;
			}

			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs remove
		if(args[0].equalsIgnoreCase("remove")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
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
		// /gs addswitch [name]
		if(args[0].equalsIgnoreCase("addswitch")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs removeswitch [name]
		if(args[0].equalsIgnoreCase("removeswitch")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs clearswitch [name]
		if(args[0].equalsIgnoreCase("clearswitch")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();

			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs listswitch [name]
		if(args[0].equalsIgnoreCase("listswitch")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			//Get region name
			String regionName = args[1].toLowerCase();

			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			String switches = "";
			for (Location l : r.getSwitches()) switches += "[x" + l.getBlockX() + "y" + l.getBlockY() + "z" + l.getBlockZ() + "]";
			
			//Message player
			plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] " + switches);
			
			return true;
		}

		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /gs addsign [name]
		if(args[0].equalsIgnoreCase("addsign")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs removesign [name]
		if(args[0].equalsIgnoreCase("removesign")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs clearsign [name]
		if(args[0].equalsIgnoreCase("clearsign")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
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
		// /gs listsign [name]
		if(args[0].equalsIgnoreCase("listsign")) {
			if (args.length == 1) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Message player
			String signs = "";
			for (Location l : r.getSigns()) signs += "[x" + l.getBlockX() + "y" + l.getBlockY() + "z" + l.getBlockZ() + "]";
			
			plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] " + signs);
			
			return true;
		}
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /gs entitytype [name] [type] (blockTypeId) (blockData)
		if(args[0].equalsIgnoreCase("entitytype")) {
			if (args.length < 3) { //Did not specify region name
				return false;
			}
			
			//Get region name
			String regionName = args[1].toLowerCase();
			
			//Check if region exists
			GravitySheepRegion r = plugin.getAPI().getWorld(player.getWorld()).getRegion(regionName);
			if (r == null) { plugin.getLog().sendPlayerWarn(player, "Region name not found."); return true; }
			
			//Test type supplied
			EntityType entityType = null;
			try { entityType = EntityType.valueOf(args[2].toUpperCase()); }
			catch (IllegalArgumentException e) { plugin.getLog().sendPlayerWarn(player, "Unknown Entity Type - " + args[2].toUpperCase()); return true; }
			if (entityType == null) return true;
			
			if (entityType == EntityType.FALLING_BLOCK) {
				if (args.length < 4) { plugin.getLog().sendPlayerWarn(player, "You are trying to set Entity Type to " + entityType + " block type value must be specified."); return true; }
				
				int typeId = 0;
				try { typeId = Integer.parseInt(args[3]); } catch (NumberFormatException e) {}
				r.setFallingBlockID(typeId);
				r.setFallingBlockData((byte) 0);
				
				if (args.length > 4) {
					byte data = 0;
					try { data = Byte.parseByte(args[4]); } catch (NumberFormatException e) {}
					r.setFallingBlockData(data);
					
				}
				
			}
			
			//Set new entity type
			r.setEntityType(entityType);
			
			plugin.getConfiguration().saveRegion(r);

			//Message player
			if (entityType == EntityType.FALLING_BLOCK) plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] EntityType set to " + entityType + " with type " + Material.getMaterial(r.getFallingBlockID()) + " data " + r.getFallingBlockData());
			else plugin.getLog().sendPlayerNormal(player, "[" + regionName + "] EntityType set to " + entityType);
			
			return true;
			
		}
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// /gs list
		if(args[0].equalsIgnoreCase("list")) {
			plugin.getLog().listRegion(player);
			return true;

		}
		return false;
	}

	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	public void updateSigns(GravitySheepRegion region) {
		ArrayList<Location> signs = region.getSigns();
		
		if (signs == null) return;
		
		for (Location sign : signs) {
			//Fetch the block
			Block block = sign.getBlock();
			
			if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) continue;
			
			Sign s = (Sign) block.getState();
			
			s.setLine(0, "[R]" + region.getName());
			s.setLine(1, "[V]" + region.getVelocity());
			s.setLine(2, "[E]" + region.getEntityType());
			s.setLine(3, "[L]" + sign.getBlockX() + " "+ sign.getBlockY() + " "+ sign.getBlockZ());
			s.update();
			
		}

	}
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
}
