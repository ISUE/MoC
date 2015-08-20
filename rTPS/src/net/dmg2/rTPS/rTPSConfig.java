package net.dmg2.rTPS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class rTPSConfig {
	private YamlConfiguration config;
	private File configFile;
	
	private String pathQueueDelay = "Settings.Queue Delay";
	private int valueDefaultQueueDelay = 10000;
	
	private String pathTimerTicks = "Settings.Timer Ticks";
	private int valueDefaultDefaultTimerTicks = 200;
	
	public rTPSConfig(File configFile) {
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		reload();
		
	}
	
	//---------------------------------------------------------------------------------------------
	public long getQueueDelay() { return this.config.getInt(this.pathQueueDelay, this.valueDefaultQueueDelay); }
	public void setQueueDelay(long queueDelay) { this.config.set(this.pathQueueDelay, queueDelay); }
	
	public long getTimerTicks() { return this.config.getInt(this.pathTimerTicks, this.valueDefaultDefaultTimerTicks); }
	public void setTimerTicks(long timerTicks) { this.config.set(this.pathTimerTicks, timerTicks); }
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	private void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()) {
			this.load();
			if (this.config.getString(this.pathQueueDelay) == null) { this.config.set(this.pathQueueDelay, this.valueDefaultQueueDelay); this.save(); }
			if (this.config.getString(this.pathTimerTicks) == null) { this.config.set(this.pathTimerTicks, this.valueDefaultDefaultTimerTicks); this.save(); }
			
		} else {
			this.config.set(this.pathQueueDelay, this.valueDefaultQueueDelay);
			this.config.set(this.pathTimerTicks, this.valueDefaultDefaultTimerTicks);
			this.save();
			
		}
		
	}
	//---------------------------------------------------------------------------------------------
}
