package net.dmg2.DynamicMOTD;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class DynamicMOTDConfig {

	private YamlConfiguration config;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	private File configFile;
	
	public DynamicMOTDConfig(File file) {
		this.config = new YamlConfiguration();
		this.configFile = file;
		
		//Some default settings
		this.configDefaultsHash.put("dmotd.msg", "Welcome to the Minds of Chimera");
		this.configDefaultsHash.put("dmotd.owner", "Admin");
		
		//Check if configuration file exists
		if (this.configFile.exists()){
			//If does, load it
			try {
				this.config.load(this.configFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//Otherwise create and populate default values
			for (String key : this.configDefaultsHash.keySet()) {
				this.config.set(key, this.configDefaultsHash.get(key));
			}
			try {
				this.config.save(this.configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	//#############################################################################################
	public String getString(String key) {
		return this.config.getString(key);
	}
	
	public void setProperty(String key, Object value) {
		this.config.set(key, value);
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
