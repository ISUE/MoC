package moc.MOCFizziks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class MOCFizziksConfig {
	//=======================================================================================================
	private YamlConfiguration config;
	private YamlConfiguration configRegions;
	private File configFile;
	private File regionsFile;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	private MOCFizziks plugin;
	//=======================================================================================================

	
	
	//=======================================================================================================
	public MOCFizziksConfig(File configFile, File regionsFile, MOCFizziks plugin) {
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.configRegions = new YamlConfiguration();
		this.regionsFile = regionsFile;
		this.plugin = plugin;
		
		//Some default settings
		this.configDefaultsHash.put("settings.defaultVelocity.X", 0);
		this.configDefaultsHash.put("settings.defaultVelocity.Y", 0);
		this.configDefaultsHash.put("settings.defaultVelocity.Z", 0);
		this.configDefaultsHash.put("settings.defaultAcceleration.X", 0);
		this.configDefaultsHash.put("settings.defaultAcceleration.Y", 0);
		this.configDefaultsHash.put("settings.defaultAcceleration.Z", 0);
		this.configDefaultsHash.put("settings.defaultIsOn", true);
		this.configDefaultsHash.put("settings.defaultUsesPower", true);
		
		//Check if configuration file exists
		if (configFile.exists()) this.load();
		else { for (String key : this.configDefaultsHash.keySet()) { this.config.set(key, this.configDefaultsHash.get(key)); } this.save(); }
		
		if (regionsFile.exists()) this.loadRegions();
		else this.saveRegions();
		
	}
	//=======================================================================================================

	
	
	//=======================================================================================================
	public Vector getDefaultVelocity() {
		double x = config.getDouble("settings.defaultVelocity.X", 0);
		double y = config.getDouble("settings.defaultVelocity.Y", 0);
		double z = config.getDouble("settings.defaultVelocity.Z", 0);
		
		Vector v = new Vector(x, y, z);
		
		return v;
		
	}

	public Vector getDefaultAcceleration() {
		double x = config.getDouble("settings.defaultAcceleration.X", 0);
		double y = config.getDouble("settings.defaultAcceleration.Y", 0);
		double z = config.getDouble("settings.defaultAcceleration.Z", 0);
		
		Vector v = new Vector(x, y, z);
		
		return v;
		
	}
	//=======================================================================================================

	
	
	//=======================================================================================================
	public boolean getDefaultOn() { return config.getBoolean("settings.defaultIsOn", true); }
	public boolean getDefaultUsesPower() { return config.getBoolean("settings.defaultUsesPower", true); }
	//=======================================================================================================

	
	
	//#############################################################################################
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	
	public void saveRegions() { try { this.configRegions.save(this.regionsFile); } catch (IOException e) { e.printStackTrace(); } }
	public void loadRegions() { try { this.configRegions.load(this.regionsFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//#############################################################################################


	
	//=======================================================================================
	public void removeRegion(String worldName, String regionName) { this.configRegions.set(worldName + "." + regionName, null); this.saveRegions(); }
	public void removeWorld(String worldName) { this.configRegions.set(worldName, null); this.saveRegions(); }
	//=======================================================================================
	public void saveRegion(MOCFizziksRegion region) {
		String worldName = region.getWorld().getName();
		
		configRegions.set(worldName + "." + region.getName() + ".lowerLeft.X", region.getLowerLeft().getX());
		configRegions.set(worldName + "." + region.getName() + ".lowerLeft.Y", region.getLowerLeft().getY());
		configRegions.set(worldName + "." + region.getName() + ".lowerLeft.Z", region.getLowerLeft().getZ());

		configRegions.set(worldName + "." + region.getName() + ".upperRight.X", region.getUpperRight().getX());
		configRegions.set(worldName + "." + region.getName() + ".upperRight.Y", region.getUpperRight().getY());
		configRegions.set(worldName + "." + region.getName() + ".upperRight.Z", region.getUpperRight().getZ());
		
		configRegions.set(worldName + "." + region.getName() + ".velocity.X", region.getVelocity().getX());
		configRegions.set(worldName + "." + region.getName() + ".velocity.Y", region.getVelocity().getY());
		configRegions.set(worldName + "." + region.getName() + ".velocity.Z", region.getVelocity().getZ());

		configRegions.set(worldName + "." + region.getName() + ".acceleration.X", region.getAcceleration().getX());
		configRegions.set(worldName + "." + region.getName() + ".acceleration.Y", region.getAcceleration().getY());
		configRegions.set(worldName + "." + region.getName() + ".acceleration.Z", region.getAcceleration().getZ());
		
		configRegions.set(worldName + "." + region.getName() + ".isOn", region.isOn());
		configRegions.set(worldName + "." + region.getName() + ".usesPower", region.usesPower());
		
		for (Location s : region.getSwitches()) configRegions.set(worldName + "." + region.getName() + ".switches.x" + (int)s.getX() + "y" + (int)s.getY() + "z" + (int)s.getZ(), (int)s.getX() + " " + (int)s.getY() + " " + (int)s.getZ());
		for (Location s : region.getSigns()) configRegions.set(worldName + "." + region.getName() + ".signs.x" + (int)s.getX() + "y" + (int)s.getY() + "z" + (int)s.getZ(), (int)s.getX() + " " + (int)s.getY() + " " + (int)s.getZ());
		
		//Save the configuration file
		this.saveRegions();

	}
	//=======================================================================================

	
	
	//=======================================================================================
	public ArrayList<MOCFizziksRegion> getRegions(String worldName) {
		//Empty list of regions
		ArrayList<MOCFizziksRegion> regions = new ArrayList<MOCFizziksRegion>();
		
		//Get all region names
		Set<String> regionNames = listWorldRegions(worldName);
		
		if (regionNames == null) return regions;
		
		//Run through all region names
		for (String regionName : regionNames) {
			//Create region object
			MOCFizziksRegion region = new MOCFizziksRegion(regionName,
					getRegionLowerLeft(worldName, regionName),
					getRegionUpperRight(worldName, regionName),
					getRegionVelocity(worldName, regionName),
					getRegionAcceleration(worldName, regionName),
					getRegionIsOn(worldName, regionName),
					getRegionUsesPower(worldName, regionName),
					getRegionSwitches(worldName, regionName),
					getRegionSigns(worldName, regionName));
			
			//Add region to array
			regions.add(region);
			
		}
		
		return regions;
		
	}
	//=======================================================================================

	private ArrayList<Location> getRegionSwitches(String worldName, String regionName) {
		ArrayList<Location> switches = new ArrayList<Location>();
		
		if (listRegionsSwitches(worldName, regionName) != null) {
			for (String s : listRegionsSwitches(worldName, regionName)) {
				String[] xyz = configRegions.getString(worldName + "." + regionName + ".switches." + s).split(" ");
				switches.add(new Location(plugin.getServer().getWorld(worldName), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])));

			}
			
		}
		
		return switches;
		
	}
	
	private ArrayList<Location> getRegionSigns(String worldName, String regionName) {
		ArrayList<Location> signs = new ArrayList<Location>();
		
		if (listRegionsSigns(worldName, regionName) != null) {
			for (String s : listRegionsSigns(worldName, regionName)) {
				String[] xyz = configRegions.getString(worldName + "." + regionName + ".signs." + s).split(" ");
				signs.add(new Location(plugin.getServer().getWorld(worldName), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])));

			}
			
		}
		
		return signs;
		
	}
	
	//=======================================================================================
	private boolean getRegionUsesPower(String worldName, String regionName) { return configRegions.getBoolean(worldName + "." + regionName + ".isOn", true); }
	private boolean getRegionIsOn(String worldName, String regionName) { return configRegions.getBoolean(worldName + "." + regionName + ".usesPower", true); }
	//=======================================================================================

	
	
	//=======================================================================================
	private Vector getRegionVelocity(String worldName, String regionName) {
		double x = configRegions.getDouble(worldName + "." + regionName + ".velocity.X");
		double y = configRegions.getDouble(worldName + "." + regionName + ".velocity.Y");
		double z = configRegions.getDouble(worldName + "." + regionName + ".velocity.Z");
		
		Vector velocity = new Vector(x, y, z);
		
		return velocity;
		
	}

	private Vector getRegionAcceleration(String worldName, String regionName) {
		double x = configRegions.getDouble(worldName + "." + regionName + ".acceleration.X");
		double y = configRegions.getDouble(worldName + "." + regionName + ".acceleration.Y");
		double z = configRegions.getDouble(worldName + "." + regionName + ".acceleration.Z");
		
		Vector acceleration = new Vector(x, y, z);
		
		return acceleration;
		
	}
	//=======================================================================================

	
	
	//=======================================================================================
	private Location getRegionLowerLeft(String worldName, String regionName) {
		double x = configRegions.getDouble(worldName + "." + regionName + ".lowerLeft.X", 0);
		double y = configRegions.getDouble(worldName + "." + regionName + ".lowerLeft.Y", 0);
		double z = configRegions.getDouble(worldName + "." + regionName + ".lowerLeft.Z", 0);
		
		return new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
		
	}

	private Location getRegionUpperRight(String worldName, String regionName) {
		double x = configRegions.getDouble(worldName + "." + regionName + ".upperRight.X", 0);
		double y = configRegions.getDouble(worldName + "." + regionName + ".upperRight.Y", 0);
		double z = configRegions.getDouble(worldName + "." + regionName + ".upperRight.Z", 0);
		
		return new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
		
	}
	//=======================================================================================
	

	
	//=======================================================================================
	private Set<String> list(String path) {
	    if (configRegions.getConfigurationSection(path) != null && configRegions.getConfigurationSection(path).getKeys(false) != null)
		    return configRegions.getConfigurationSection(path).getKeys(false);
	    
	    else return null;

	}

	private Set<String> listWorldRegions(String worldName) { return this.list(worldName); }
	private Set<String> listRegionsSwitches(String worldName, String regionName) { return this.list(worldName + "." + regionName + ".switches"); }
	private Set<String> listRegionsSigns(String worldName, String regionName) { return this.list(worldName + "." + regionName + ".signs"); }
	//=======================================================================================
}