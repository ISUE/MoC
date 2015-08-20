package moc.MOCFizziks;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class MOCFizziksPlayerListener implements Listener {
	
	//============================================================
	private MOCFizziks plugin;
	public MOCFizziksPlayerListener(MOCFizziks plugin) { this.plugin = plugin; }
	//============================================================

	//#######################################################################################################################
	@EventHandler
	public void onEvent(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		
		//Process lever clicks in the region
		togglePower(event.getClickedBlock(), event.getAction());
		
		//If player is not in the selection mode return
		if (plugin.getPlayerSelectionStatus().contains(event.getPlayer().getName()) == false) return;

		//Grab variables from the event
		Player player = event.getPlayer();
		Location loc = event.getClickedBlock().getLocation();
		Action action = event.getAction();
		
		//Check which event was performed
		if (action == Action.LEFT_CLICK_BLOCK) {
			//Save selection
			Location locOld = plugin.getPlayerSelectionLeft().get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				plugin.getPlayerSelectionLeft().put(player.getName(), loc);
				//Message the player
				player.sendMessage(ChatColor.AQUA+"Left Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
		} else if (action == Action.RIGHT_CLICK_BLOCK) {
			//Save selection
			Location locOld = plugin.getPlayerSelectionRight().get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				plugin.getPlayerSelectionRight().put(player.getName(), loc);
				//Message the player
				player.sendMessage(ChatColor.AQUA+"Right Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
			
		}
		
		event.setCancelled(true);
		
	}
	
	//#######################################################################################################################
	@EventHandler
	public void onEvent(PlayerChangedWorldEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	
	@EventHandler
	public void onEvent(PlayerJoinEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	
	@EventHandler
	public void onEvent(PlayerQuitEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	
	public void cleanUpLists(Player player) {
		//Remove player from all lists
		this.plugin.getPlayerSelectionLeft().remove(player.getName());
		this.plugin.getPlayerSelectionRight().remove(player.getName());
		this.plugin.getPlayerSelectionStatus().remove(player.getName());
		this.plugin.getPlayerVelocityQueueVelocity().remove(player);
		this.plugin.getPlayerVelocityQueueAcceleration().remove(player);
		this.plugin.getPlayerVelocityQueueTimeIn().remove(player);
	}
	//#######################################################################################################################
	@EventHandler
	public void onEvent(MOCFizziksQueueEvent event) {
		if (System.currentTimeMillis() > this.plugin.getLastQueueTime() + 10) {
			//Run through all online players and add those in regions to the queue
			for (Player p : this.plugin.getServer().getOnlinePlayers()) addPlayerToVelocityQueue(p);

			//Process the queue
			for (Player p : plugin.getPlayerVelocityQueueVelocity().keySet()) {
				//Calculate new velocity region should apply
				Vector velocity = plugin.getPlayerVelocityQueueVelocity().get(p);
				Vector acceleration = plugin.getPlayerVelocityQueueAcceleration().get(p);
				
				double timeIn = (System.currentTimeMillis() - plugin.getPlayerVelocityQueueTimeIn().get(p)) / 1000.0;
				
				acceleration.multiply(timeIn);
				
				velocity.add(acceleration);
				
				//Update player's velocity
				p.setVelocity(velocity);
				
			}
			
			//Record queue processed time
			plugin.setLastQueueTime(System.currentTimeMillis());
			
		}

	}
	//#######################################################################################################################

	public void addPlayerToVelocityQueue(Player player) {
		MOCFizziksRegion region = getRegion(player.getLocation());
		
		//Check if null was returned or region is OFF
		if (region == null) {
			plugin.getPlayerVelocityQueueTimeIn().remove(player);
			plugin.getPlayerVelocityQueueAcceleration().remove(player);
			plugin.getPlayerVelocityQueueVelocity().remove(player);
			
			return;
			
		}
		
		if (!region.isOn()) {
			plugin.getPlayerVelocityQueueTimeIn().remove(player);
			plugin.getPlayerVelocityQueueAcceleration().remove(player);
			plugin.getPlayerVelocityQueueVelocity().remove(player);
			
			return;
			
		}
		
		//Player is in a region, record time in if he is new to it
		if (!plugin.getPlayerVelocityQueueTimeIn().containsKey(player)) {
			plugin.getPlayerVelocityQueueTimeIn().put(player, System.currentTimeMillis());
		}
		
		Vector vel = region.getVelocity().clone();
		Vector acc = region.getAcceleration().clone();

		//Generate new velocity vector
		plugin.getPlayerVelocityQueueVelocity().put(player, vel.multiply(0.1));

		//Generate new acceleration vector
		plugin.getPlayerVelocityQueueAcceleration().put(player, acc.multiply(0.1));
		
	}

	//#######################################################################################################################
	

	//========================================================================================================
	// Returns region name if location falls in any
	public MOCFizziksRegion getRegion(Location location) { return plugin.getAPI().getWorld(location.getWorld()).getRegion(location.getBlock()); }
	
	//========================================================================================================
	// Processes level clicks in the region
	public void togglePower(Block block, Action action) {
		if ( !(block.getType() == Material.LEVER ||
				block.getType() == Material.STONE_BUTTON ||
				block.getType() == Material.WOOD_BUTTON ||
				block.getType() == Material.STONE_PLATE ||
				block.getType() == Material.WOOD_PLATE) ) return;

		//Get region name if any
		ArrayList<MOCFizziksRegion> regions = plugin.getAPI().getWorld(block.getWorld()).getSwitchRegions(block);
		
		if (regions.size() == 0) return;
		
		for (MOCFizziksRegion r : regions) {
			if (r.usesPower()) {
				//Region uses power, process the click
				if ((block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE) && action == Action.PHYSICAL) {
					r.setOn(!r.isOn());
					plugin.getConfiguration().saveRegion(r);
					
					plugin.getCommandExecutor().updateSigns(r);
				
				} else if (block.getType() == Material.LEVER || block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON) {
					r.setOn(!r.isOn());
					plugin.getConfiguration().saveRegion(r);
					
					plugin.getCommandExecutor().updateSigns(r);
					
				}
				
			}
			
		}
		
	}

}