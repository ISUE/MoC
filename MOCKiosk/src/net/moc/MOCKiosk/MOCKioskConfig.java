package net.moc.MOCKiosk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MOCKioskConfig {
	//#############################################################################################
	private YamlConfiguration config;
	private File configFile;
	@SuppressWarnings("unused")
	private MOCKiosk plugin;
	//#############################################################################################

	
	//#############################################################################################
	public MOCKioskConfig(File configFile, MOCKiosk plugin) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.reload();
		
	}
	//#############################################################################################
	
	public int getShoutRadius() { return this.config.getInt("Kiosk.ShoutRadius", 5); }

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()){
			this.load();
			if(this.config.getString("Kiosk.ShoutRadius") == null) this.config.set("Kiosk.ShoutRadius", 5);
			this.save();
			
		} else {
			this.config.set("Kiosk.ShoutRadius", 5);
			this.save();
			
		}
		
	}
	//#############################################################################################
}
