package net.dmg2.RegenBlock.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dmg2.RegenBlock.RegenBlock;
import net.dmg2.RegenBlock.RegenBlockBlock;
import net.dmg2.RegenBlock.Event.RegenBlockEventAlarm;
import net.dmg2.RegenBlock.Event.RegenBlockEventConfigSave;
import net.dmg2.RegenBlock.Event.RegenBlockEventRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class RegenBlockEventListenerBlock implements  Listener {
	//============================================================
	private RegenBlock plugin;
	private Random rnd = new Random();
	private static int maxBlocksPerRespawn = 500;
	private static long tryAgainIn = 40L;
	private static long timeBetweenRespawn = 2000;
	private long lastRespawn = System.currentTimeMillis();
	private long respawnCounter = 0;
	public RegenBlockEventListenerBlock(RegenBlock plugin) {
		this.plugin = plugin;
		
	}
	//============================================================

	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(BlockBreakEvent event){
		if (event.isCancelled() || event.getBlock().getType() == Material.AIR) return; //========================
		
		//Restore the block
		regenBlock(event.getBlock().getLocation(), event.getBlock().getType(), event.getBlock().getData(), event.getPlayer(), true);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(BlockPlaceEvent event) {
		if(event.isCancelled() || event.getBlockPlaced().getType() == Material.AIR) return; //========================
		
		//Make sure no physics is applied to the newly placed block
		if (getBlockRegion(event.getBlock().getLocation()) != null) event.getBlock().setData(event.getBlock().getData(), false);

		//Restore the block
		regenBlock(event.getBlock().getLocation(), Material.AIR, (byte) 0, event.getPlayer(), false);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(EntityExplodeEvent event) {
		if (event.isCancelled()) return; //========================
		//Restore blocks
		for (Block b : event.blockList()) regenBlock(b.getLocation(), b.getType(), b.getData(), null, true);
		
	}
	//##########################################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(BlockBurnEvent event) {
		if (event.isCancelled() || event.getBlock().getType() == Material.FIRE) return; //========================
		//Restore the block
		Block b = event.getBlock();
		regenBlock(b.getLocation(), b.getType(), b.getData(), null, true);
		
	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(RegenBlockEventAlarm event) {
		this.plugin.getQueue().broadcastMessage(event.getAlarmRadius(), event.getAlarmMessage(), event.getWorldName(), event.getX(), event.getY(), event.getZ());
		
	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(RegenBlockEventRespawn event) {
		ArrayList<RegenBlockBlock> blocks = event.getBlocks();
		
		//Check to make sure previous re-spawn hasn't been too recently
		if (respawnCounter >= maxBlocksPerRespawn && System.currentTimeMillis() - lastRespawn < timeBetweenRespawn) {
			//Try later
			respawnCounter = 0;
			
			final ArrayList<RegenBlockBlock> b = blocks;
			
			//Re-queue blocks to next event
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable() { public void run() { Bukkit.getServer().getPluginManager().callEvent(new RegenBlockEventRespawn(b)); } }, tryAgainIn);
			
			return;
			
		}
		
		for (int i = 0 ; i < maxBlocksPerRespawn && i < blocks.size() ; i++) {
			//Get next block
			RegenBlockBlock block = blocks.get(i);
			
			//Link it to an actual block in the world
			Block b = block.getLocation().getBlock();
			
			//Set proper material and data to regenerate it
			b.setTypeIdAndData(block.getTypeId(), block.getData(), false);
			
			//Remove it from the regeneration list
			plugin.getConfiguration().removeBlock(b);
			
			//Update counter
			respawnCounter++;
			
		}
		
		//Save time
		lastRespawn = System.currentTimeMillis();

		//Clear re-spawned blocks
		if (blocks.size() > maxBlocksPerRespawn) {
			blocks.subList(0, maxBlocksPerRespawn-1).clear();
			
			final ArrayList<RegenBlockBlock> b = blocks;
			
			//Re-queue remaining blocks to next event
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable() { public void run() { Bukkit.getServer().getPluginManager().callEvent(new RegenBlockEventRespawn(b)); } }, tryAgainIn);
			
		}
		
	}
	//##########################################################################################################
	@EventHandler
	public void onEvent(RegenBlockEventConfigSave event) { if (this.plugin.getConfiguration().isDirty()) this.plugin.getConfiguration().saveBlocks(); }
	//##########################################################################################################
	

	
	
	
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FUNCTIONS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public boolean isPlayerEditor(String playerName) { return this.plugin.getListenerPlayer().getPlayerEditStatus().contains(playerName); }
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public void regenBlock(Location location, Material material, byte data, Player player, Boolean isBreakEvent) {
		//Check if player is in editor mode
		if(player != null && isPlayerEditor(player.getName())) return;
		
		//Do we monitor this event
		if (isBreakEvent) { if (!plugin.getConfiguration().doMonitorBreak()) return; }
		else { if (!plugin.getConfiguration().doMonitorPlace()) return; }
		
		//Make sure the block is not blacklisted, if it is we just ignore the event
		if (plugin.getConfiguration().listBlacklistBlockId() != null && plugin.getConfiguration().listBlacklistBlockId().contains(String.valueOf(material.getId()))) return;
		
		//Make sure mat passed in is not fire ... happens sometimes it seems =.=
		if (material == Material.FIRE) material = Material.AIR;
		
		//Get region name
		String regionName = this.getBlockRegion(location);
		
		//Check if we should ignore the block
		if (ignoreBlock(location, material.getId(), regionName)) return;

		//Get region's re-spawn time
		long respawnTime = plugin.getConfiguration().getRegionRespawnTime(regionName);
		
		//Adjust it to milliseconds system time
		respawnTime = System.currentTimeMillis() + respawnTime*1000;
		
		//Sync regen flag
		int syncType = plugin.getConfiguration().getRegionSync(regionName);
		
		//Get region type
		int regionType = this.plugin.getConfiguration().getRegionType(regionName);
		//----------------------------------------------------------------------------
		// Check what region type this is. Normal will be 0 or null if nothing present
		// but we never check for that.
		if (regionType == 1) {
			//Mine region
			//Randomize regen block if not air
			if (material != Material.AIR) {
				//Get list of spawnBlocks for the region
				HashMap<Integer, Integer> spawnBlocksId = this.plugin.getConfiguration().getRegionSpawnBlocks(regionName);
				
				//Only randomize those on the spawn block list
				if (spawnBlocksId.containsKey(material.getId())) {
					Iterator<Integer> i = spawnBlocksId.keySet().iterator();
					int totalChance = 0;
					while (i.hasNext()) { totalChance += spawnBlocksId.get(i.next()); }
					
					if (totalChance == 0) return;
					
					int roll = rnd.nextInt(totalChance);
					
					int typeId = 1;
					i = spawnBlocksId.keySet().iterator();
					totalChance = 0;
					
					while (i.hasNext()) {
						int blockId = i.next();
						int blockIdChance = spawnBlocksId.get(blockId);
						totalChance += blockIdChance;
						if (roll <= totalChance && roll >= totalChance - blockIdChance) {
							typeId = blockId;
							break;
						}
					}
					
					material = Material.getMaterial(typeId);
					data = 0;
					
				}
				
			}
			
		}
		//----------------------------------------------------------------------------
		

		//Message the player based on region's feedback type
		if (player != null && ((plugin.getConfiguration().getRegionFeedbackID(regionName) == 1 && material == Material.AIR) || plugin.getConfiguration().getRegionFeedbackID(regionName) == 2)) {
			Pattern pat = Pattern.compile("TIME");
			Matcher mat = pat.matcher(plugin.getConfiguration().getFeedbackString());
			plugin.getLog().sendPlayerWarn(player, mat.replaceAll(String.valueOf(plugin.getConfiguration().getRegionRespawnTime(regionName))));					
		}

		//Queue the block for regeneration
		queueBlock(location, material, data, regionName, respawnTime, syncType);

	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public String getBlockRegion(Location location) {
		
		if (plugin.getConfiguration().listRegions() == null) return null;

		for (String regionName : plugin.getConfiguration().listRegions()) {

			//Get world name
			String worldName = plugin.getConfiguration().getRegionWorldName(regionName);

			//Make sure block is in this region's world before checking further
			if (location.getWorld().getName().equalsIgnoreCase(worldName) == false) continue;

			//Get region coordinates
			int leftX = plugin.getConfiguration().getRegionLeftX(regionName);
			int leftY = plugin.getConfiguration().getRegionLeftY(regionName);
			int leftZ = plugin.getConfiguration().getRegionLeftZ(regionName);

			int rightX = plugin.getConfiguration().getRegionRightX(regionName);
			int rightY = plugin.getConfiguration().getRegionRightY(regionName);
			int rightZ = plugin.getConfiguration().getRegionRightZ(regionName);
			
			//Check if block is within the region
			if (Math.abs(leftX - rightX) == Math.abs(leftX - location.getBlockX()) + Math.abs(rightX - location.getBlockX()) &&
					Math.abs(leftY - rightY) == Math.abs(leftY - location.getBlockY()) + Math.abs(rightY - location.getBlockY()) &&
					Math.abs(leftZ - rightZ) == Math.abs(leftZ - location.getBlockZ()) + Math.abs(rightZ - location.getBlockZ())) {
				
				//Return the re-spawn time
		    	return regionName;

			}
			
		}	
		return null;
	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	private boolean ignoreBlock(Location location, int typeId, String regionName) {
		
		//Check if we block was even in a region
		if (regionName == null) return true;
		
		//Check if block TypeId is blacklisted in the region
		if (this.plugin.getConfiguration().listRegionBlacklistBlockId(regionName) != null && this.plugin.getConfiguration().listRegionBlacklistBlockId(regionName).contains(String.valueOf(typeId))) return true;

		//Check if the block is already being regenerated
		if ( plugin.getConfiguration().getBlockToRegen(this.plugin.getConfiguration().getRegionWorldName(regionName), location) != null) return true;

		return false;
	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	private void queueBlock(Location location, Material material, byte data, String regionName, long respawnTime, int syncType) {
		//If sync regen, check if anything else is in the queue and set the repawn time to the same amount
		if (syncType == 1) {
			Long rt = plugin.getQueue().getRegionRespawnTime(regionName);
			if (rt != null) respawnTime = rt;
			
		} else if (syncType == 2) {
			plugin.getQueue().updateRegionRespawnTime(regionName, respawnTime);
			
		} else if (syncType == 3) {
			long delta = plugin.getQueue().shiftRegionRespawnTime(regionName, respawnTime);
			//Update respawn time
			if (delta != 0) respawnTime = delta;
			
		}
		
		//Save block to configuration in case we crash
		plugin.getConfiguration().setBlock(location, material.getId(), data, regionName, respawnTime);
		
		//Add to queue
		plugin.getQueue().addBlock(new RegenBlockBlock(location, material.getId(), data, regionName, respawnTime,
				plugin.getConfiguration().getRegionAlarmTime(regionName), plugin.getConfiguration().getRegionSync(regionName),
				plugin.getConfiguration().getRegionAlarmRadius(regionName), plugin.getConfiguration().getRegionAlarmMessage(regionName),
				plugin.getConfiguration().getRegionType(regionName)));

	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
}
