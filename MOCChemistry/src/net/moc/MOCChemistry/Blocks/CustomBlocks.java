package net.moc.MOCChemistry.Blocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class CustomBlocks {
	private YamlConfiguration config;
	private File configFile;
	private JavaPlugin plugin;
	private ArrayList<GenericCubeCustomBlock> blocks = new ArrayList<GenericCubeCustomBlock>(); public ArrayList<GenericCubeCustomBlock> getBlocks() { return this.blocks; }

	private String pathCustomBlocks = "Blocks";
	//#############################################################################################
	public CustomBlocks(File configFile, JavaPlugin plugin) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.reload();
		
	}
	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()){
			this.load();
			if(listCustomBlocks() == null) { loadDefaultCustomBlocks(); this.save(); }
			
		} else {
			loadDefaultCustomBlocks();
			this.save();
			
		}
		
		this.generateCustomBlocks();
		
	}
	
	private void generateCustomBlocks() {
		this.blocks.clear();
		
		for (String name : this.listCustomBlocks()) {
			this.blocks.add(new BlankCustomBlock(this.plugin, name, this.getCustomBlockURL(name)));
			
		}
		
	}
	
	private void loadDefaultCustomBlocks() {
		this.config.set(this.pathCustomBlocks + ".Beryllium", "http://minecraft.dmg2.net/MOCChemistry/beryllium.png");
		this.config.set(this.pathCustomBlocks + ".Boron", "http://minecraft.dmg2.net/MOCChemistry/boron.png");
		this.config.set(this.pathCustomBlocks + ".Carbon", "http://minecraft.dmg2.net/MOCChemistry/carbon.png");
		this.config.set(this.pathCustomBlocks + ".Helium", "http://minecraft.dmg2.net/MOCChemistry/helium.png");
		this.config.set(this.pathCustomBlocks + ".Hydrogen", "http://minecraft.dmg2.net/MOCChemistry/hydrogen.png");
		this.config.set(this.pathCustomBlocks + ".Lithium", "http://minecraft.dmg2.net/MOCChemistry/lithium.png");
		this.config.set(this.pathCustomBlocks + ".Nitrogen", "http://minecraft.dmg2.net/MOCChemistry/nitrogen.png");
		this.config.set(this.pathCustomBlocks + ".Oxygen", "http://minecraft.dmg2.net/MOCChemistry/oxygen.png");
		
	}
	//#############################################################################################
	private String getCustomBlockURL(String name) { return this.config.getString(this.pathCustomBlocks + "." + name); }
	private Set<String> listCustomBlocks() { return this.list(this.pathCustomBlocks); }
	private Set<String> list(String path) { if (this.config.getConfigurationSection(path) != null) { return this.config.getConfigurationSection(path).getKeys(false); } return null; }

}
