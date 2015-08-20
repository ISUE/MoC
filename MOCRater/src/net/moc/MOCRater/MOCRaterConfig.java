package net.moc.MOCRater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MOCRaterConfig {
	//#############################################################################################
	private YamlConfiguration config;
	private File configFile;
	@SuppressWarnings("unused")
	private MOCRater plugin;
	//#############################################################################################

	
	//#############################################################################################
	public MOCRaterConfig(File configFile, MOCRater plugin) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		if (configFile.exists()){
			this.load();
			if (this.config.getString("Screenshot.FileFolder") == null) this.config.set("Screenshot.FileFolder", "/home/httpd/html/chimera/assets/images/profile/");
			if (this.config.getString("Screenshot.URLFolder") == null) this.config.set("Screenshot.URLFolder", "http://isue-server.eecs.ucf.edu/chimera/assets/images/profile/");
			this.save();
			
		} else {
			this.config.set("Screenshot.FileFolder", "/home/httpd/html/chimera/assets/images/profile/");
			this.config.set("Screenshot.URLFolder", "http://isue-server.eecs.ucf.edu/chimera/assets/images/profile/");
			
			this.save();
		}
		
	}
	//#############################################################################################
	

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() {
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//---------------------------------------------------------------------------------------------
	public void load() {
		try {
			this.config.load(this.configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	//---------------------------------------------------------------------------------------------
	//#############################################################################################

	public String getScreenshotFileFolder() { return this.config.getString("Screenshot.FileFolder"); }
	public String getScreenshotURLFolder() { return this.config.getString("Screenshot.URLFolder"); }
	
}
