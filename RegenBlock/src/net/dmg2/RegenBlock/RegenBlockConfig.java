package net.dmg2.RegenBlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class RegenBlockConfig {
	
	private YamlConfiguration config, blocks;
	private File configFile, blocksFile;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	private RegenBlock plugin;
	private boolean dirty; public boolean isDirty() { return this.dirty; } private void setDirty() { this.dirty = true; }
	
	public RegenBlockConfig(RegenBlock plugin, File configFile, File blocksFile) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.blocks = new YamlConfiguration();
		this.configFile = configFile;
		this.blocksFile = blocksFile;
		
		//Some default settings
		this.configDefaultsHash.put("settings.defaultSpawnTime", 5);
		this.configDefaultsHash.put("settings.monitorPlace", true);
		this.configDefaultsHash.put("settings.monitorBreak", true);
		this.configDefaultsHash.put("settings.feedbackString", "This block will be restored to its original state in TIMEs.");
		this.configDefaultsHash.put("settings.defaultSpawnBlocks.1", 80);
		this.configDefaultsHash.put("settings.defaultSpawnBlocks.15", 15);
		this.configDefaultsHash.put("settings.defaultSpawnBlocks.14", 5);
		
		reload();
		reloadBlocks();
		
	}

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	public void saveBlocks() { try { this.blocks.save(this.blocksFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	private void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	private void loadBlocks() { try { this.blocks.load(this.blocksFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		//Check if configuration file exists
		if (configFile.exists()) {
			//If does, load it
			this.load();

			if (this.config.getString("settings.feedbackString") == null) { this.config.set("settings.feedbackString", this.configDefaultsHash.get("settings.feedbackString")); }
			if (this.config.getString("settings.defaultSpawnTime") == null) { this.config.set("settings.defaultSpawnTime", this.configDefaultsHash.get("settings.defaultSpawnTime")); }
			if (this.config.getString("settings.monitorBreak") == null) { this.config.set("settings.monitorBreak", this.configDefaultsHash.get("settings.monitorBreak")); }
			if (this.config.getString("settings.monitorPlace") == null) { this.config.set("settings.monitorPlace", this.configDefaultsHash.get("settings.monitorPlace")); }
			if (this.config.getString("settings.defaultSpawnBlocks") == null) {
				this.config.set("settings.defaultSpawnBlocks.1", this.configDefaultsHash.get("settings.defaultSpawnBlocks.1"));
				this.config.set("settings.defaultSpawnBlocks.15", this.configDefaultsHash.get("settings.defaultSpawnBlocks.15"));
				this.config.set("settings.defaultSpawnBlocks.14", this.configDefaultsHash.get("settings.defaultSpawnBlocks.14"));
				
			}

			this.save();
			
		} else {
			//Otherwise create and populate default values
			for (String key : this.configDefaultsHash.keySet()) { this.config.set(key, this.configDefaultsHash.get(key)); }

			this.save();
			
		}
		
	}
	public void reloadBlocks() { if (blocksFile.exists()) { this.loadBlocks(); } else { this.saveBlocks(); } }
	
	public boolean doMonitorPlace() { return this.config.getBoolean("settings.monitorPlace"); }
	public boolean doMonitorBreak() { return this.config.getBoolean("settings.monitorBreak"); }
	public void setMonitorPlace(boolean val) { this.config.set("settings.monitorPlace", val); }
	public void setMonitorBreak(boolean val) { this.config.set("settings.monitorBreak", val); }
	//############################################################################################
	public void setRegion(String regionName, int respawnTime, Location right, Location left) {
		//Record all the properties
		this.config.set("region." + regionName + ".respawnTime", respawnTime);
		this.config.set("region." + regionName + ".left.X", left.getBlockX());
		this.config.set("region." + regionName + ".left.Y", left.getBlockY());
		this.config.set("region." + regionName + ".left.Z", left.getBlockZ());
		this.config.set("region." + regionName + ".right.X", right.getBlockX());
		this.config.set("region." + regionName + ".right.Y", right.getBlockY());
		this.config.set("region." + regionName + ".right.Z", right.getBlockZ());
		this.config.set("region." + regionName + ".world", right.getWorld().getName());
		this.config.set("region." + regionName + ".feedbackID", 0);
		this.config.set("region." + regionName + ".type", 0);
		this.config.set("region." + regionName + ".sync", 0);
		this.config.set("region." + regionName + ".alarmTime", 0);
		this.config.set("region." + regionName + ".alarmRadius", 100);
		this.config.set("region." + regionName + ".alarmMessage", "This area is about to respawn. Please move away.");
		
		
		//Save the configuration file
		this.save();
		
	}
	
	public void removeRegion(String regionName) {
		this.config.set("region." + regionName, null);
		//Save the configuration file
		this.save();
		
	}
	
	public void setBlock(Location location, int typeId, byte data, String regionName, long respawnTime) {
		//Save block to configuration in case we crash before restoring
		String blockName = "x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ();
		String worldName = location.getWorld().getName();
		
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".X", location.getBlockX());
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".Y", location.getBlockY());
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".Z", location.getBlockZ());

		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".TypeID", typeId);
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".Data", data);
		
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".RegionName", regionName);
		this.blocks.set("blocksToRegen." + worldName + "." + blockName + ".RespawnTime", respawnTime);
		
		this.setDirty();
		
	}

	public void removeBlock(Block block) {
		//Remove block after we have restored it from the configuration file
		String blockName = "x" + block.getX() + "y" + block.getY() + "z" + block.getZ();
		this.blocks.set("blocksToRegen." + block.getWorld().getName() + "." + blockName, null);
		//Remove world entry is it's empty of blocks
		if (this.blocks.getString("blocksToRegen." + block.getWorld().getName()) == "{}") {
			this.blocks.set("blocksToRegen." + block.getWorld().getName(), null);
			
		}
		
		this.setDirty();
		
	}
	//############################################################################################

	//############################################################################################
	public int getBlockX(String worldName, String blockName) { return this.blocks.getInt("blocksToRegen." + worldName + "." + blockName + ".X"); }
	public int getBlockY(String worldName, String blockName) { return this.blocks.getInt("blocksToRegen." + worldName + "." + blockName + ".Y"); }
	public int getBlockZ(String worldName, String blockName) { return this.blocks.getInt("blocksToRegen." + worldName + "." + blockName + ".Z"); }
	public int getBlockTypeId(String worldName, String blockName) { return this.blocks.getInt("blocksToRegen." + worldName + "." + blockName + ".TypeID"); }
	public byte getBlockData(String worldName, String blockName) { return (byte)this.blocks.getInt("blocksToRegen." + worldName + "." + blockName + ".Data"); }
	public String getBlockRegionName(String worldName, String blockName) { return this.blocks.getString("blocksToRegen." + worldName + "." + blockName + ".RegionName"); }
	public long getBlockRespawnTime(String worldName, String blockName) { return this.blocks.getLong("blocksToRegen." + worldName + "." + blockName + ".RespawnTime"); }
	
	public String getBlockToRegen(String worldName, Location location) { return this.blocks.getString("blocksToRegen." + worldName + "." + "x" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ()); }
	//############################################################################################

	//############################################################################################
	public String getRegionWorldName(String regionName) { return this.config.getString("region." + regionName + ".world"); }
	public String getRegionName(String regionName) { return this.config.getString("region." + regionName); }
	
	public void setRegionRespawnTime(String regionName, int respawnTime) { this.config.set("region." + regionName + ".respawnTime", respawnTime); }
	public int getRegionRespawnTime(String regionName) { return this.config.getInt("region." + regionName + ".respawnTime", 0); }
	public int getRegionDefaultRespawnTime() { return this.config.getInt("settings.defaultSpawnTime"); }
	
	public int getRegionFeedbackID(String regionName) { return this.config.getInt("region." + regionName + ".feedbackID"); }
	public int setRegionFeedbackID(String regionName, int feedbackId) {
		if (feedbackId < 0 || feedbackId > 2) feedbackId = 0;
		this.config.set("region." + regionName + ".feedbackID", feedbackId);
		this.save();
		return feedbackId;
		
	}
	
	public String getFeedbackString() { return this.config.getString("settings.feedbackString"); }
	public void setFeedbackString(String feedbackString) { this.config.set("settings.feedbackString", feedbackString); this.save(); }

	public String getRegionLeft(String regionName) {return this.config.getString("region." + regionName + ".left.X") + " " + this.config.getString("region." + regionName + ".left.Y") + " "+ this.config.getString("region." + regionName + ".left.Z"); }
	public int getRegionLeftX(String regionName) { return this.config.getInt("region." + regionName + ".left.X"); }
	public int getRegionLeftY(String regionName) { return this.config.getInt("region." + regionName + ".left.Y"); }
	public int getRegionLeftZ(String regionName) { return this.config.getInt("region." + regionName + ".left.Z"); }
	
	public String getRegionRight(String regionName) {return this.config.getString("region." + regionName + ".right.X") + " " + this.config.getString("region." + regionName + ".right.Y") + " "+ this.config.getString("region." + regionName + ".right.Z"); }
	public int getRegionRightX(String regionName) { return this.config.getInt("region." + regionName + ".right.X"); }
	public int getRegionRightY(String regionName) { return this.config.getInt("region." + regionName + ".right.Y"); }
	public int getRegionRightZ(String regionName) { return this.config.getInt("region." + regionName + ".right.Z"); }

	public Set<String> listRegionBlacklistBlockId(String regionName) { return this.list("region." + regionName + ".blacklist.TypeId"); }

	public void addRegionBlacklistBlockId(String regionName, int id) {
		this.config.set("region." + regionName + ".blacklist.TypeId." + id, id);
		this.save();
	}

	public void removeRegionBlacklistBlockId(String regionName, int id) {
		this.config.set("region." + regionName + ".blacklist.TypeId." + id, null);
		this.save();
	}
	
	public void setRegionType(String regionName, int type) {
		this.config.set("region." + regionName + ".type", type);
		this.save();
	}
	
	public int getRegionType(String regionName) { 
		return this.config.getInt("region." + regionName + ".type", 0);
	}


	public int getRegionSync(String regionName) {
		return this.config.getInt("region." + regionName + ".sync", 0);
	}

	public void setRegionSync(String regionName, int sync) {
		this.config.set("region." + regionName + ".sync", sync);
		this.save();
	}
	
	public HashMap<Integer, Integer> getRegionSpawnBlocks(String regionName) {
		HashMap<Integer, Integer> spawnBlocks = new HashMap<Integer, Integer>();
		Set<String> spawnBlocksId = this.list("region." + regionName + ".spawnBlocks");
		if (spawnBlocksId != null) {
			for (String spawnBlockId : spawnBlocksId) {
				spawnBlocks.put(Integer.parseInt(spawnBlockId), this.config.getInt("region." + regionName + ".spawnBlocks." + spawnBlockId));
			}
			
		}
		return spawnBlocks;
	}
	
	public void setRegionSpawnBlock(String regionName, int typeId, int chance) {
		this.config.set("region." + regionName + ".spawnBlocks." + typeId, chance);
		this.save();
	}

	public int getRegionSpawnBlock(String regionName, int typeId) { return this.config.getInt("region." + regionName + ".spawnBlocks." + typeId); }

	public void removeRegionSpawnBlock(String regionName, int typeId) {
		this.config.set("region." + regionName + ".spawnBlocks." + typeId, null);
		this.save();
	}
	
	public void regionAddSpawnBlocks(String regionName) {
		if (this.config.getString("region." + regionName + ".spawnBlocks") == null) {
			Set<String> defaultSpawnBlocks = this.list("settings.defaultSpawnBlocks");
			if (defaultSpawnBlocks != null) {
				for (String typeId : defaultSpawnBlocks) {
					this.setRegionSpawnBlock(regionName, Integer.parseInt(typeId), this.config.getInt("settings.defaultSpawnBlocks." + typeId, 50));
					
				}

			}
		
		}
	
	}
	//############################################################################################
	
	//############################################################################################
	public int getRegionAlarmTime(String regionName) { return this.config.getInt("region." + regionName + ".alarmTime", 0); }
	public void setRegionAlarmTime(String regionName, int alarmTime) { this.config.set("region." + regionName + ".alarmTime", alarmTime); this.save(); }

	public int getRegionAlarmRadius(String regionName) { return this.config.getInt("region." + regionName + ".alarmRadius", 0); }
	public void setRegionAlarmRadius(String regionName, int alarmRadius) { this.config.set("region." + regionName + ".alarmRadius", alarmRadius); this.save(); }

	public String getRegionAlarmMessage(String regionName) { return this.config.getString("region." + regionName + ".alarmMessage", "This area is about to respawn. Please move away."); }
	public void setRegionAlarmMessage(String regionName, String alarmMessage) { this.config.set("region." + regionName + ".alarmMessage", alarmMessage); this.save(); }
	//############################################################################################

	//############################################################################################
	public Set<String> list(String path) {
	    if (this.config.getConfigurationSection(path) != null && this.config.getConfigurationSection(path).getKeys(false) != null) {
		    return this.config.getConfigurationSection(path).getKeys(false);
	    } else {
	    	return null;
	    }

	}

	public Set<String> listB(String path) {
	    if (this.blocks.getConfigurationSection(path) != null && this.blocks.getConfigurationSection(path).getKeys(false) != null) {
		    return this.blocks.getConfigurationSection(path).getKeys(false);
	    } else {
	    	return null;
	    }

	}

	public Set<String> listRegions() { return this.list("region"); }
	public Set<String> listWorldsToRegen() { return this.listB("blocksToRegen"); }
	public Set<String> listBlacklistBlockId() { return this.list("blacklist.TypeId"); } 
	public Set<String> listBlocksToRegen(String worldName) { return this.listB("blocksToRegen." + worldName); }

	public void removeBlacklistBlockId(int id) {
		this.config.set("blacklist.TypeId." + id, null);
		this.save();
	}

	public void addBlacklistBlockId(int id) {
		this.config.set("blacklist.TypeId." + id, id);
		this.save();
	}
	//############################################################################################
	
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ INSTANCE STUFF
    public boolean copyWorldRegions(String worldFrom, String worldTo) {
		//Check if there is anything at all
    	if (this.listRegions() == null) return false;
    	
    	//Run through regions and find ones with matching world
    	for (String regionName : this.listRegions()) {
    		if (worldFrom.equalsIgnoreCase(this.getRegionWorldName(regionName))) {
    			//Copy over
    			this.config.set("region." + regionName + "-" + worldTo, this.config.getConfigurationSection("region." + regionName));
    			
    			//Change the world name
    			this.config.set("region." + regionName + "-" + worldTo + ".world", worldTo);
    			
    		}
    		
    	}
    	
		this.save();
		
		return true;
    	
    }
    
    public boolean removeWorldRegions(String worldName) {
		//Check if there is anything at all
    	if (this.listRegions() == null) return false;
    	
    	//Run through regions and find ones with matching world
    	for (String regionName : this.listRegions()) {
    		if (worldName.equalsIgnoreCase(this.getRegionWorldName(regionName))) {
    			this.config.set("region." + regionName, null);
    			
    		}
    		
    	}
    	
		this.save();
		
		return true;
    	
    }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ INSTANCE STUFF

	public void requeue() {
		if (listWorldsToRegen() == null) return;
		
		for (String worldName : listWorldsToRegen()) {
			if (listBlocksToRegen(worldName) != null) {
				for (String blockName : listBlocksToRegen(worldName)) {
					//Get XYZ and TypeId
					int x = getBlockX(worldName, blockName);
					int y = getBlockY(worldName, blockName);
					int z = getBlockZ(worldName, blockName);
					int typeId = getBlockTypeId(worldName, blockName);
					byte data = getBlockData(worldName, blockName);
					String regionName = getBlockRegionName(worldName, blockName);
					long respawnTime = getBlockRespawnTime(worldName, blockName);
					String message = getRegionAlarmMessage(regionName);
					int alarmRadius = getRegionAlarmRadius(regionName);
					int alarmTime = getRegionAlarmTime(regionName);
					int sync = getRegionSync(regionName);
					int regionType = getRegionType(regionName);
					
					Location location = new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
					RegenBlockBlock block = new RegenBlockBlock(location, typeId, data, regionName, respawnTime, alarmTime, sync, alarmRadius, message, regionType);
					this.plugin.getQueue().addBlock(block);
					
				}
				
			}
			
		}
		
	}

}
