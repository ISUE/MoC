package net.dmg2.Plotasize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlotasizeConfig {

	private YamlConfiguration config;
	private File configFile;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	

	public PlotasizeConfig(File configFile) {
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		//Some default settings
		this.configDefaultsHash.put("settings.numberOfPlots", 16);
		this.configDefaultsHash.put("settings.clearance", 200);
		this.configDefaultsHash.put("settings.plot.x", 60);
		this.configDefaultsHash.put("settings.plot.y", 90);
		this.configDefaultsHash.put("settings.plot.z", 60);
		this.configDefaultsHash.put("settings.plot.buffer", 3);
		this.configDefaultsHash.put("settings.centerOnPlayer", false);
		
		//Check if configuration file exists
		if (configFile.exists()){
			//If does, load it
			this.load();

			if (this.config.getString("settings.numberOfPlots") == null) {
				this.config.set("settings.numberOfPlots", this.configDefaultsHash.get("settings.numberOfPlots"));
			}
			
			if (this.config.getString("settings.clearance") == null) {
				this.config.set("settings.clearance", this.configDefaultsHash.get("settings.clearance"));
			}
			
			if (this.config.getString("settings.centerOnPlayer") == null) {
				this.config.set("settings.centerOnPlayer", this.configDefaultsHash.get("settings.centerOnPlayer"));
			}
			
			if (this.config.getString("settings.plot") == null) {
				this.config.set("settings.plot.x", this.configDefaultsHash.get("settings.plot.x"));
				this.config.set("settings.plot.y", this.configDefaultsHash.get("settings.plot.y"));
				this.config.set("settings.plot.z", this.configDefaultsHash.get("settings.plot.z"));
				this.config.set("settings.plot.buffer", this.configDefaultsHash.get("settings.plot.buffer"));
			}

			this.save();
			
		} else {
			//Otherwise create and populate default values
			for (String key : this.configDefaultsHash.keySet()) {
				this.config.set(key, this.configDefaultsHash.get(key));
			}

			this.save();

		}
		
	}

	
	
	//#############################################################################################
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//############################################################################################


	
	//############################################################################################
	public int getNumberOfPlots() { return this.config.getInt("settings.numberOfPlots", (Integer) this.configDefaultsHash.get("settings.numberOfPlots")); }

	public int getPlotBuffer() { return this.config.getInt("settings.plot.buffer", (Integer) this.configDefaultsHash.get("settings.plot.buffer")); }
	public int getClearance() { return this.config.getInt("settings.clearance", (Integer) this.configDefaultsHash.get("settings.clearance")); }

	public int getPlotX() { return this.config.getInt("settings.plot.x", (Integer) this.configDefaultsHash.get("settings.plot.x")); }
	public int getPlotY() { return this.config.getInt("settings.plot.y", (Integer) this.configDefaultsHash.get("settings.plot.y")); }
	public int getPlotZ() { return this.config.getInt("settings.plot.z", (Integer) this.configDefaultsHash.get("settings.plot.z")); }
	
	public boolean getCenterOnPlayer() { return this.config.getBoolean("settings.centerOnPlayer"); }
	//############################################################################################

	
	
	//############################################################################################
	public void setNumberOfPlots(int value) {
		if (value <= 0) return;
		int root = (int) Math.sqrt(value);
		if (Math.pow(root, 2) < value) value = (int) Math.pow((root + 1),2);
		this.config.set("settings.numberOfPlots", value);
		this.save();
	}

	public void setPlotBuffer(int value) { if (value > 0) this.config.set("settings.plot.buffer", value); this.save(); }
	public void setClearance(int value) { if (value > 0) this.config.set("settings.clearance", value); this.save(); }
	public void setCenterOnPlayer(boolean value) { this.config.set("settings.centerOnPlayer", value); this.save(); }

	public void setPlotX(int value) { if (value > 0) this.config.set("settings.plot.x", value); this.save(); }
	public void setPlotY(int value) { if (value > 0 && value < 254) this.config.set("settings.plot.y", value); this.save();}
	public void setPlotZ(int value) { if (value > 0) this.config.set("settings.plot.z", value); this.save(); }
	//############################################################################################

}
