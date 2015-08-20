package net.moc.MOCChemistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MOCChemistryConfig {
	//#############################################################################################
	private YamlConfiguration config;
	private File configFile;
	private MOCChemistry plugin; public MOCChemistry getPlugin() { return this.plugin; }
	
	private String pathSettingsAccident = "Settings.Accident";
	private String pathHarvestDamage = "Harvest.Damage";
	private String pathHarvestPunt = "Harvest.Punt";
	private String valueHarvestDamage = "20-20";
	private String valueHarvestPunt = "10-1";
	
	private String pathSplitDamage = "Split.Damage";
	private String pathSplitPunt = "Split.Punt";
	private String valueSplitDamage = "30-30";
	private String valueSplitPunt = "20-2";

	private String pathCombineDamage = "Combine.Damage";
	private String pathCombinePunt = "Combine.Punt";
	private String valueCombineDamage = "30-30";
	private String valueCombinePunt = "20-2";
	//#############################################################################################

	
	//#############################################################################################
	public MOCChemistryConfig(File configFile, MOCChemistry plugin) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.reload();
		
	}
	//#############################################################################################
	
	public int getHavrestDamageChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestDamage, this.valueHarvestDamage).split("-")[0]); }
	public int getHavrestDamageAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestDamage, this.valueHarvestDamage).split("-")[1]); }
	
	public int getHavrestPuntChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestPunt, this.valueHarvestPunt).split("-")[0]); }
	public int getHavrestPuntAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestPunt, this.valueHarvestPunt).split("-")[1]); }
	
	public int getSplitDamageChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitDamage, this.valueSplitDamage).split("-")[0]); }
	public int getSplitDamageAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitDamage, this.valueSplitDamage).split("-")[1]); }
	
	public int getSplitPuntChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitPunt, this.valueSplitPunt).split("-")[0]); }
	public int getSplitPuntAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitPunt, this.valueSplitPunt).split("-")[1]); }
	
	public int getCombineDamageChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathCombineDamage, this.valueCombineDamage).split("-")[0]); }
	public int getCombineDamageAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathCombineDamage, this.valueCombineDamage).split("-")[1]); }
	
	public int getCombinePuntChance() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathCombinePunt, this.valueCombinePunt).split("-")[0]); }
	public int getCombinePuntAmount() { return Integer.parseInt(this.config.getString(this.pathSettingsAccident + "." + this.pathCombinePunt, this.valueCombinePunt).split("-")[1]); }
	
	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()){
			this.load();
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestDamage) == null) this.config.set(this.pathSettingsAccident + "." + this.pathHarvestDamage, this.valueHarvestDamage);
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathHarvestPunt) == null) this.config.set(this.pathSettingsAccident + "." + this.pathHarvestPunt, this.valueHarvestPunt);
			
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitDamage) == null) this.config.set(this.pathSettingsAccident + "." + this.pathSplitDamage, this.valueSplitDamage);
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathSplitPunt) == null) this.config.set(this.pathSettingsAccident + "." + this.pathSplitPunt, this.valueSplitPunt);
			
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathCombineDamage) == null) this.config.set(this.pathSettingsAccident + "." + this.pathCombineDamage, this.valueCombineDamage);
			if(this.config.getString(this.pathSettingsAccident + "." + this.pathCombinePunt) == null) this.config.set(this.pathSettingsAccident + "." + this.pathCombinePunt, this.valueCombinePunt);
			this.save();
			
		} else {
			this.config.set(this.pathSettingsAccident + "." + this.pathHarvestDamage, this.valueHarvestDamage);
			this.config.set(this.pathSettingsAccident + "." + this.pathHarvestPunt, this.valueHarvestPunt);
			
			this.config.set(this.pathSettingsAccident + "." + this.pathSplitDamage, this.valueSplitDamage);
			this.config.set(this.pathSettingsAccident + "." + this.pathSplitPunt, this.valueSplitPunt);
			
			this.config.set(this.pathSettingsAccident + "." + this.pathCombineDamage, this.valueCombineDamage);
			this.config.set(this.pathSettingsAccident + "." + this.pathCombinePunt, this.valueCombinePunt);
			
			this.save();
			
		}
		
	}
	//#############################################################################################
}
