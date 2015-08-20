package net.dmg2.GravitySheep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import net.dmg2.GravitySheep.api.GravitySheepRegion;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class GravitySheepConfig {
	private YamlConfiguration config;
	private File configFile;
	private GravitySheep plugin;

	public GravitySheepConfig(GravitySheep plugin) {
		this.config = new YamlConfiguration();
    	this.configFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		this.plugin = plugin;
		
		this.reload();
		
	}

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	//#############################################################################################
	public void reload() {
		if (configFile.exists()){
			this.load();
			
			if(this.config.getString("settings.defaultVelocity.X") == null) this.config.set("settings.defaultVelocity.X", 0);
			if(this.config.getString("settings.defaultVelocity.Y") == null) this.config.set("settings.defaultVelocity.Y", 0);
			if(this.config.getString("settings.defaultVelocity.Z") == null) this.config.set("settings.defaultVelocity.Z", 0);
			this.save();
			
		} else {
			this.config.set("settings.defaultVelocity.X", 0);
			this.config.set("settings.defaultVelocity.Y", 0);
			this.config.set("settings.defaultVelocity.Z", 0);
			
			this.save();
			
		}
		
	}
	
	public Vector getDefaultVelocity() {
		double x = config.getDouble("settings.defaultVelocity.X", 0);
		double y = config.getDouble("settings.defaultVelocity.Y", 0);
		double z = config.getDouble("settings.defaultVelocity.Z", 0);
		
		Vector v = new Vector(x, y, z);
		
		return v;
		
	}
	//#############################################################################################

	//=======================================================================================
	public void removeRegion(String worldName, String regionName) { config.set(worldName + "." + regionName, null); save(); }
	public void removeWorld(String worldName) { config.set(worldName, null); this.save(); }
	//=======================================================================================
	public void saveRegion(GravitySheepRegion region) {
		String worldName = region.getWorld().getName();
		String regionName = region.getName();

		config.set(worldName + "." + regionName + ".base.X", region.getBase().getX());
		config.set(worldName + "." + regionName + ".base.Y", region.getBase().getY());
		config.set(worldName + "." + regionName + ".base.Z", region.getBase().getZ());

		config.set(worldName + "." + regionName + ".velocity.X", region.getVelocity().getX());
		config.set(worldName + "." + regionName + ".velocity.Y", region.getVelocity().getY());
		config.set(worldName + "." + regionName + ".velocity.Z", region.getVelocity().getZ());

		config.set(worldName + "." + regionName + ".entityType", region.getEntityType().toString());
		config.set(worldName + "." + regionName + ".fallingBlockID", region.getFallingBlockID());
		config.set(worldName + "." + regionName + ".fallingBlockData", region.getFallingBlockData());

		for (Location s : region.getSwitches()) config.set(worldName + "." + regionName + ".switches.x" + s.getBlockX() + "y" + s.getBlockY() + "z" + s.getBlockZ(), s.getBlockX() + " " + s.getBlockY() + " " + s.getBlockZ());
		for (Location s : region.getSigns()) config.set(worldName + "." + regionName + ".signs.x" + s.getBlockX() + "y" + s.getBlockY() + "z" + s.getBlockZ(), s.getBlockX() + " " + s.getBlockY() + " " + s.getBlockZ());
		
		save();

	}
	//=======================================================================================

	
	
	//=======================================================================================
	public ArrayList<GravitySheepRegion> getRegions(String worldName) {
		//Empty list of regions
		ArrayList<GravitySheepRegion> regions = new ArrayList<GravitySheepRegion>();
		
		//Get all region names
		Set<String> regionNames = listWorldRegions(worldName);
		
		if (regionNames == null) return regions;
		
		//Run through all region names
		for (String regionName : regionNames) {
			//Create region object
			GravitySheepRegion region = new GravitySheepRegion(regionName,
					getRegionBase(worldName, regionName),
					getRegionVelocity(worldName, regionName),
					getRegionEntityType(worldName, regionName),
					getRegionTypeId(worldName, regionName),
					getRegionData(worldName, regionName),
					getRegionSwitches(worldName, regionName),
					getRegionSigns(worldName, regionName));
			
			//Add region to array
			regions.add(region);
			
		}
		
		return regions;
		
	}
	//=======================================================================================
	
	
	
	//=======================================================================================
	private Location getRegionBase(String worldName, String regionName) {
		double x = config.getDouble(worldName + "." + regionName + ".base.X", 0);
		double y = config.getDouble(worldName + "." + regionName + ".base.Y", 0);
		double z = config.getDouble(worldName + "." + regionName + ".base.Z", 0);
		
		return new Location(plugin.getServer().getWorld(worldName), x, y, z);
		
	}
	//=======================================================================================
	
	
	
	//=======================================================================================
	private Vector getRegionVelocity(String worldName, String regionName) {
		double x = config.getDouble(worldName + "." + regionName + ".velocity.X");
		double y = config.getDouble(worldName + "." + regionName + ".velocity.Y");
		double z = config.getDouble(worldName + "." + regionName + ".velocity.Z");
		
		Vector velocity = new Vector(x, y, z);
		
		return velocity;
		
	}
	//=======================================================================================
	
	
	
	//=======================================================================================
	private EntityType getRegionEntityType(String worldName, String regionName) {
		try { return EntityType.valueOf(config.getString(worldName + "." + regionName + ".entityType", "SHEEP")); }
		catch (IllegalArgumentException e) { return EntityType.SHEEP; }
		
	}
	
	private int getRegionTypeId(String worldName, String regionName) {
		return config.getInt(worldName + "." + regionName + ".fallingBlockID", 0);
		
	}
	
	private byte getRegionData(String worldName, String regionName) {
		return (byte)config.getInt(worldName + "." + regionName + ".fallingBlockData", 0);
		
	}
	//=======================================================================================
	
	
	
	//=======================================================================================
	private ArrayList<Location> getRegionSwitches(String worldName, String regionName) {
		ArrayList<Location> switches = new ArrayList<Location>();
		
		if (listRegionsSwitches(worldName, regionName) != null) {
			for (String s : listRegionsSwitches(worldName, regionName)) {
				String[] xyz = config.getString(worldName + "." + regionName + ".switches." + s).split(" ");
				switches.add(new Location(plugin.getServer().getWorld(worldName), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])));

			}
			
		}
		
		return switches;
		
	}
	
	private ArrayList<Location> getRegionSigns(String worldName, String regionName) {
		ArrayList<Location> signs = new ArrayList<Location>();
		
		if (listRegionsSigns(worldName, regionName) != null) {
			for (String s : listRegionsSigns(worldName, regionName)) {
				String[] xyz = config.getString(worldName + "." + regionName + ".signs." + s).split(" ");
				signs.add(new Location(plugin.getServer().getWorld(worldName), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])));

			}
			
		}
		
		return signs;
		
	}
	//=======================================================================================
	

	
	//=======================================================================================
	private Set<String> list(String path) {
	    if (config.getConfigurationSection(path) != null && config.getConfigurationSection(path).getKeys(false) != null)
		    return config.getConfigurationSection(path).getKeys(false);
	    
	    else return null;

	}

	private Set<String> listWorldRegions(String worldName) { return list(worldName); }
	private Set<String> listRegionsSwitches(String worldName, String regionName) { return list(worldName + "." + regionName + ".switches"); }
	private Set<String> listRegionsSigns(String worldName, String regionName) { return list(worldName + "." + regionName + ".signs"); }
	//=======================================================================================
}
