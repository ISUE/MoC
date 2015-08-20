package net.dmg2.OP2C;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class OP2CConfig {
	private YamlConfiguration config;
	private File configFile;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	private OP2C plugin;
	
	private String world = "portal.world";
	private String x = "portal.x";
	private String y = "portal.y";
	private String z = "portal.z";
	private String yaw = "portal.yaw";
	private String pitch = "portal.pitch";
	
	public OP2CConfig(OP2C plugin, File configFile) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		//Some default settings
		this.configDefaultsHash.put(world, "world");
		this.configDefaultsHash.put(x, 0);
		this.configDefaultsHash.put(y, 80);
		this.configDefaultsHash.put(z, 0);
		this.configDefaultsHash.put(yaw, 0);
		this.configDefaultsHash.put(pitch, 0);
		
		reload();
		
	}

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	private void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		//Check if configuration file exists
		if (configFile.exists()) {
			//If does, load it
			this.load();

			if (this.config.getString(world) == null) { this.config.set(world, this.configDefaultsHash.get(world)); }
			if (this.config.getString(x) == null) { this.config.set(x, this.configDefaultsHash.get(x)); }
			if (this.config.getString(y) == null) { this.config.set(y, this.configDefaultsHash.get(y)); }
			if (this.config.getString(z) == null) { this.config.set(z, this.configDefaultsHash.get(z)); }
			if (this.config.getString(yaw) == null) { this.config.set(yaw, this.configDefaultsHash.get(yaw)); }
			if (this.config.getString(pitch) == null) { this.config.set(pitch, this.configDefaultsHash.get(pitch)); }
			
		} else {
			//Otherwise create and populate default values
			for (String key : this.configDefaultsHash.keySet()) { this.config.set(key, this.configDefaultsHash.get(key)); }
			
		}
		
		this.save();
		
	}
	
	public Location getPortalLocation() {
		return new Location(plugin.getServer().getWorld(this.config.getString(world)),
							this.config.getDouble(x), this.config.getDouble(y), this.config.getDouble(z),
							(float) this.config.getDouble(yaw), (float) this.config.getDouble(pitch));
	}
	
	public void setPortalLocation(Location location) {
		this.config.set(world, location.getWorld().getName());
		this.config.set(x, location.getX());
		this.config.set(y, location.getY());
		this.config.set(z, location.getZ());
		this.config.set(yaw, location.getYaw());
		this.config.set(pitch, location.getPitch());
		
		this.save();
		
	}
	
}
