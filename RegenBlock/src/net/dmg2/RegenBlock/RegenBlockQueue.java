package net.dmg2.RegenBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import net.dmg2.RegenBlock.Event.RegenBlockEventAlarm;
import net.dmg2.RegenBlock.Event.RegenBlockEventConfigSave;
import net.dmg2.RegenBlock.Event.RegenBlockEventRespawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RegenBlockQueue implements Runnable {
	//========================================================================
	private RegenBlock plugin;
	private long queueDelta = 1000;
	private long nextConfigSave = System.currentTimeMillis();
	private long configSaveDelta = 60000;
	
	private HashMap<String, Long> alarmWentOffAt = new HashMap<String, Long>();
	private Vector<RegenBlockBlock> blockQueue = new Vector<RegenBlockBlock>();
	//========================================================================
	
	
	
	//========================================================================
	public RegenBlockQueue(RegenBlock plugin) { this.plugin = plugin; }
	
	public void run() {
		while (true) {
			//Sleep
			try { Thread.sleep(queueDelta); } catch (InterruptedException e) { e.printStackTrace(); }
			
			//Check Queue
			checkQueue();

		}
		
	}
	//========================================================================
	
	
	
	//========================================================================
	public void addBlock(RegenBlockBlock block) { blockQueue.add(block); }
	
	public void regenRegion(String regionName) {
		long time = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		Vector<RegenBlockBlock> cloneBlockQueue = (Vector<RegenBlockBlock>) blockQueue.clone();
		for (RegenBlockBlock b : cloneBlockQueue) if (b.getRegionName().equalsIgnoreCase(regionName)) {
			b.setRespawnTime(time);
			
			//Update config file
			plugin.getConfiguration().setBlock(b.getLocation(), b.getTypeId(), b.getData(), regionName, time);
			
		}
		
	}
	
	public void updateRegionRespawnTime(String regionName, long respawnTime) {
		@SuppressWarnings("unchecked")
		Vector<RegenBlockBlock> cloneBlockQueue = (Vector<RegenBlockBlock>) blockQueue.clone();
		
		for (RegenBlockBlock rb : cloneBlockQueue) if (rb.getRegionName().equalsIgnoreCase(regionName)) {
			rb.setRespawnTime(respawnTime);
			
			//Update config file
			plugin.getConfiguration().setBlock(rb.getLocation(), rb.getTypeId(), rb.getData(), regionName, respawnTime);
			
		}
		
	}
	
	public long shiftRegionRespawnTime(String regionName, long respawnTime) {
		@SuppressWarnings("unchecked")
		Vector<RegenBlockBlock> cloneBlockQueue = (Vector<RegenBlockBlock>) blockQueue.clone();
		
		long time = System.currentTimeMillis();
		long earliestRespawn = 99999999999999L;
		long latestRespawn = 0;
		
		//Find earliest block to repop
		for (RegenBlockBlock rb : cloneBlockQueue) if (rb.getRegionName().equalsIgnoreCase(regionName)) if (rb.getRespawnTime() > time){
			if (earliestRespawn > rb.getRespawnTime()) earliestRespawn = rb.getRespawnTime();
			if (latestRespawn < rb.getRespawnTime()) latestRespawn = rb.getRespawnTime();
			
		}
		
		//No blocks present or non matched anything..
		if (earliestRespawn == 99999999999999L) return 0;
		
		//Calculate shift needed
		long dt = respawnTime - earliestRespawn;
		
		//Shift all blocks
		for (RegenBlockBlock rb : cloneBlockQueue) if (rb.getRegionName().equalsIgnoreCase(regionName)) if (rb.getRespawnTime() > time) {
			rb.setRespawnTime(rb.getRespawnTime() + dt);
			
			//Update config file
			plugin.getConfiguration().setBlock(rb.getLocation(), rb.getTypeId(), rb.getData(), regionName, rb.getRespawnTime());
			
		}
		
		return latestRespawn + dt + dt;
		
	}
	
	public Long getRegionRespawnTime(String regionName) {
		@SuppressWarnings("unchecked")
		Vector<RegenBlockBlock> cloneBlockQueue = (Vector<RegenBlockBlock>) blockQueue.clone();
		
		long time = System.currentTimeMillis();
		for (RegenBlockBlock rb : cloneBlockQueue) {
			if (rb.getRegionName().equalsIgnoreCase(regionName) && rb.getRespawnTime() > time) return rb.getRespawnTime();
			
		}
		
		return null;
		
	}
	
	public void checkQueue() {
		ArrayList<RegenBlockBlock> respawnedBlocks = new ArrayList<RegenBlockBlock>();
		long time = System.currentTimeMillis();
		
		@SuppressWarnings("unchecked")
		Vector<RegenBlockBlock> cloneBlockQueue = (Vector<RegenBlockBlock>) blockQueue.clone();
		
		for (RegenBlockBlock block : cloneBlockQueue) {
			//Get XYZ and TypeId
			long respawnTime = block.getRespawnTime();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			int typeId = block.getTypeId();
			byte data = block.getData();
			String regionName = block.getRegionName();
			int sync = block.getSync();
			int alarmTime = block.getAlarmTime();
			String worldName = block.getLocation().getWorld().getName();
			int regionType = block.getRegionType();

			//Alarm if needed
			if (sync == 1 && alarmTime != 0 && respawnTime - alarmTime * 1000 < time) {
				//Block is within alarm range and in a sync re-spawn region
				//Check if alarm already went off for this region
				if (!this.alarmWentOffAt.containsKey(regionName)) {
					//Add region name with current time to the map
					this.alarmWentOffAt.put(regionName, time);
					//Broadcast the alarm
					Bukkit.getServer().getPluginManager().callEvent(new RegenBlockEventAlarm(block.getAlarmRadius(), block.getAlarmMessage(), worldName, x, y, z));

				} else {
					//Clean up alarms perhaps
					//Check if alarm went off more than 10 seconds after blocks have re-spawned
					if (this.alarmWentOffAt.get(regionName) + block.getAlarmTime()*1000 + 10000 < time) {
						//remove it from the list
						this.alarmWentOffAt.remove(regionName);

					}

				}

			}

			//Regen if needed
			if (this.readyForRegen(worldName, regionName, respawnTime, x, y, z, typeId, data, regionType)) { respawnedBlocks.add(block); }

		}
		
		if (respawnedBlocks.size() > 0) {
			//Remove re-spawned blocks
			for (RegenBlockBlock block : respawnedBlocks) { blockQueue.remove(block); }
			Bukkit.getServer().getPluginManager().callEvent(new RegenBlockEventRespawn(respawnedBlocks));

		}
		
		if (this.nextConfigSave < time) {
			this.nextConfigSave = time + this.configSaveDelta;
			Bukkit.getServer().getPluginManager().callEvent(new RegenBlockEventConfigSave());

		}
		
	}
	//========================================================================

	

	//========================================================================
	private boolean readyForRegen(String worldName, String regionName, long respawnTime, int x, int y, int z, int typeId, byte data, int regionType) {
		//Get time
		long time = System.currentTimeMillis();
		
		//Too long in queue
		if (time - respawnTime > 86400000) return true;

		//Normal region, time is up
		if (respawnTime < time && regionType == 0 ) {
			//Make sure if it's gravel or sand to re-spawn only if there is a block underneath
			if ((Material.getMaterial(typeId) == Material.SAND || Material.getMaterial(typeId) == Material.GRAVEL) && plugin.getServer().getWorld(worldName).getBlockAt(x, y - 1, z).getType() == Material.AIR) return false;
			
			//Otherwise it is ready
			return true;
		}
		
		//Sync region, time is up
		if (respawnTime < time && regionType == 1 && plugin.getServer().getWorld(worldName).getBlockAt(x, y - 1, z).getType() != Material.AIR) return true;

		return false;
		
	}
	//========================================================================
	
	
	
	//========================================================================
	public void broadcastMessage(int regionAlarmRadius, String regionAlarmMessage, String worldName, int x, int y, int z) {
		//Get location
		Location location = new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
		
		//Run through all players online and send message as needed
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			//Not sure how distance between locations is calculated if it's in two different worlds, so check if same world first
			if (player.getWorld().getName().equalsIgnoreCase(worldName) && player.getLocation().distance(location) <= regionAlarmRadius) {
				//Player within same area, send the message
				this.plugin.getLog().sendPlayerWarn(player, regionAlarmMessage);
				
			}
			
		}
		
	}
	//========================================================================
}
