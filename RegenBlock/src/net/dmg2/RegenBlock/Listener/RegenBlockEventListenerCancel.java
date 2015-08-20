package net.dmg2.RegenBlock.Listener;

import net.dmg2.RegenBlock.RegenBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RegenBlockEventListenerCancel  implements  Listener {
	//============================================================
	private RegenBlock plugin;
	public RegenBlockEventListenerCancel(RegenBlock plugin) { this.plugin = plugin; }
	//============================================================

	//##########################################################################################################
	@EventHandler
	public void onEvent(BlockFadeEvent event) {
		if(event.isCancelled()) return; //========================

		//Check if we should cancel this event if it is in one of the regions
		if (this.getBlockRegion(event.getBlock()) != null) event.setCancelled(true);

	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(BlockFromToEvent event) {
		if(event.isCancelled()) return; //========================
		
		//Check if we should cancel this event if it is in one of the regions
		if (this.getBlockRegion(event.getBlock()) != null) event.setCancelled(true);

	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(BlockGrowEvent event) {
		if(event.isCancelled()) return; //========================

		//Check if we should cancel this event if it is in one of the regions
		if (this.getBlockRegion(event.getBlock()) != null) event.setCancelled(true);

	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(BlockIgniteEvent event) {
		if(event.isCancelled()) return; //========================

		//Check if we should cancel this event if it is in one of the regions
		if (this.getBlockRegion(event.getBlock()) != null) event.setCancelled(true);
		
	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(BlockPhysicsEvent event) {
		if(event.isCancelled()) return; //========================
		
		//Check if block is within a region
		Block block = event.getBlock();
		String region = getBlockRegion(block);
		
		if (region != null) {
			switch (block.getType()) {
			case SAND:
			case GRAVEL:
			case SIGN_POST:
			case SIGN:
			case WALL_SIGN:
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
			case BED:
			case WOOD_DOOR:
			case IRON_DOOR:
			case IRON_DOOR_BLOCK:
			case TORCH:
				event.setCancelled(true);
				break;

			}
			
		}
		
	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(LeavesDecayEvent event) {
		if(event.isCancelled()) return; //========================

		//Check if we should cancel this event if it is in one of the regions
		if (this.getBlockRegion(event.getBlock()) != null) event.setCancelled(true);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(HangingPlaceEvent event) {
		if(event.isCancelled()) return; //========================
		
		//Check if player is in editor mode
		if(plugin.getListenerBlock().isPlayerEditor(event.getPlayer().getName())) return;
		
		if (this.getBlockRegion(event.getEntity().getLocation().getBlock()) != null) event.setCancelled(true);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(HangingBreakEvent event) {
		if(event.isCancelled()) return; //========================
		
		if (this.getBlockRegion(event.getEntity().getLocation().getBlock()) != null) event.setCancelled(true);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(PlayerInteractEntityEvent event) {
		if(event.isCancelled()) return; //========================
		
		//Check if player is in editor mode
		if(plugin.getListenerBlock().isPlayerEditor(event.getPlayer().getName())) return;
		
		if (this.getBlockRegion(event.getRightClicked().getLocation().getBlock()) != null) {
			if (event.getRightClicked().getType() == EntityType.ITEM_FRAME ||
					event.getRightClicked().getType() == EntityType.PAINTING)
				event.setCancelled(true);
			
		}
	
	}
	//##########################################################################################################
	public String getBlockRegion(Block block) {
		
		if (plugin.getConfiguration().listRegions() == null) return null;

		for (String regionName : plugin.getConfiguration().listRegions()) {

			//Get world name
			String worldName = plugin.getConfiguration().getRegionWorldName(regionName);

			//Make sure block is in this region's world before checking further
			if (block.getWorld().getName().equalsIgnoreCase(worldName) == false) continue;

			//Get region coordinates
			int leftX = plugin.getConfiguration().getRegionLeftX(regionName);
			int leftY = plugin.getConfiguration().getRegionLeftY(regionName);
			int leftZ = plugin.getConfiguration().getRegionLeftZ(regionName);

			int rightX = plugin.getConfiguration().getRegionRightX(regionName);
			int rightY = plugin.getConfiguration().getRegionRightY(regionName);
			int rightZ = plugin.getConfiguration().getRegionRightZ(regionName);
			
			//Check if block is within the region
			if (Math.abs(leftX - rightX) == Math.abs(leftX - block.getX()) + Math.abs(rightX - block.getX()) &&
					Math.abs(leftY - rightY) == Math.abs(leftY - block.getY()) + Math.abs(rightY - block.getY()) &&
					Math.abs(leftZ - rightZ) == Math.abs(leftZ - block.getZ()) + Math.abs(rightZ - block.getZ())) {
				
				//Return the re-spawn time
		    	return regionName;

			}
			
		}
		
		return null;
		
	}
	
}
