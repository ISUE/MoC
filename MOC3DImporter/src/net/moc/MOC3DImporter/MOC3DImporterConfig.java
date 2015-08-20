package net.moc.MOC3DImporter;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MOC3DImporterConfig {
	//#############################################################################################
	private YamlConfiguration config;
	private File configFile;
	private String pathBlocksPerQueue = "Settings.BlocksPerQueue";
	private String pathQueueDelay = "Settings.QueueDelay";
	private String pathStep = "Settings.StepDONTCHANGE";
	private String pathBlockTypeId = "Settings.Block.TypeId";
	private String pathBlockData = "Settings.Block.Data";
	
	private int defaultBlocksPerQueue = 10000;
	private int defaultQueueDelay = 5000;
	private double defaultStep = 0.2;
	private int defaultBlockTypeId = 1;
	private int defaultBlockData = 0;
	
	private String pathColors = "Colors";
	private String pathColorsTypeId = "TypeId";
	private String pathColorsData = "Data";
	private String pathColorsR = "R";
	private String pathColorsG = "G";
	private String pathColorsB = "B";
	
	private MOC3DImporter plugin;
	//#############################################################################################
	
	//#############################################################################################
	public MOC3DImporterConfig(File configFile, MOC3DImporter plugin) {
		this.plugin = plugin;
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.reload();
		
	}
	//#############################################################################################
	
	public int getBlocksPerQueue() { return this.config.getInt(this.pathBlocksPerQueue); }
	public void setBlocksPerQueue(int blocks) { this.config.set(this.pathBlocksPerQueue, blocks); this.save(); }
	
	public int getQueueDelay() { return this.config.getInt(this.pathQueueDelay); }
	public void setQueueDelay(int delay) { this.config.set(this.pathQueueDelay, delay); this.save(); }
	
	public float getStep() { return (float) this.config.getDouble(this.pathStep); }
	
	public int getBlockTypeId() { int typeId = this.config.getInt(this.pathBlockTypeId); if (Material.getMaterial(typeId) == null) return 1; else return typeId; }
	public void setBlockTypeId(int typeId) { this.config.set(this.pathBlockTypeId, typeId); this.save(); }
	
	public byte getBlockData() { byte data = (byte) this.config.getInt(this.pathBlockData); if (data < 0 || data > 15) return 0; else return data; }
	public void setBlockData(int data) { this.config.set(this.pathBlockData, data); this.save(); }

	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()) {
			this.load();
			if(this.config.getString(this.pathBlocksPerQueue) == null) this.config.set(this.pathBlocksPerQueue, this.defaultBlocksPerQueue);
			if(this.config.getString(this.pathQueueDelay) == null) this.config.set(this.pathQueueDelay, this.defaultQueueDelay);
			if(this.config.getString(this.pathStep) == null) this.config.set(this.pathStep, this.defaultStep);
			if(this.config.getString(this.pathBlockTypeId) == null) this.config.set(this.pathBlockTypeId, this.defaultBlockTypeId);
			if(this.config.getString(this.pathBlockData) == null) this.config.set(this.pathBlockData, this.defaultBlockData);
			if(this.config.getString(this.pathColors) == null) this.loadDefaultColors();
			this.save();
			
		} else {
			this.config.set(this.pathBlocksPerQueue, this.defaultBlocksPerQueue);
			this.config.set(this.pathQueueDelay, this.defaultQueueDelay);
			this.config.set(this.pathStep, this.defaultStep);
			this.config.set(this.pathBlockTypeId, this.defaultBlockTypeId);
			this.config.set(this.pathBlockData, this.defaultBlockData);
			this.loadDefaultColors();
			this.save();
			
		}
		
		this.buildPalette();
		
	}
	//---------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------
	private void buildPalette() {
		for (String s : this.list(this.pathColors)) {
			int typeId = this.config.getInt(this.pathColors + "." + s + "." + this.pathColorsTypeId);
			byte data = (byte) this.config.getInt(this.pathColors + "." + s + "." + this.pathColorsData);
			int r = this.config.getInt(this.pathColors + "." + s + "." + this.pathColorsR);
			int g = this.config.getInt(this.pathColors + "." + s + "." + this.pathColorsG);
			int b = this.config.getInt(this.pathColors + "." + s + "." + this.pathColorsB);
			
			if (Material.getMaterial(typeId) != null) this.plugin.getPalette().add(new MOC3DImporterColorBlock(new Color(r, g, b), typeId, data));
			
		}
		
	}
	//#############################################################################################
	private Set<String> list(String path) {
	    if (this.config.getConfigurationSection(path) != null && this.config.getConfigurationSection(path).getKeys(false) != null) {
		    return this.config.getConfigurationSection(path).getKeys(false);
	    } else {
	    	return null;
	    }

	}

	private void loadDefaultColors() {
		this.config.set(this.pathColors + ".1-0." + this.pathColorsTypeId, 1);
		this.config.set(this.pathColors + ".1-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".1-0." + this.pathColorsR, 125);
		this.config.set(this.pathColors + ".1-0." + this.pathColorsG, 125);
		this.config.set(this.pathColors + ".1-0." + this.pathColorsB, 125);
		
		this.config.set(this.pathColors + ".3-0." + this.pathColorsTypeId, 3);
		this.config.set(this.pathColors + ".3-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".3-0." + this.pathColorsR, 88);
		this.config.set(this.pathColors + ".3-0." + this.pathColorsG, 60);
		this.config.set(this.pathColors + ".3-0." + this.pathColorsB, 40);
		
		this.config.set(this.pathColors + ".4-0." + this.pathColorsTypeId, 4);
		this.config.set(this.pathColors + ".4-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".4-0." + this.pathColorsR, 167);
		this.config.set(this.pathColors + ".4-0." + this.pathColorsG, 167);
		this.config.set(this.pathColors + ".4-0." + this.pathColorsB, 167);
		
		this.config.set(this.pathColors + ".5-0." + this.pathColorsTypeId, 5);
		this.config.set(this.pathColors + ".5-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".5-0." + this.pathColorsR, 186);
		this.config.set(this.pathColors + ".5-0." + this.pathColorsG, 150);
		this.config.set(this.pathColors + ".5-0." + this.pathColorsB, 97);
		
		this.config.set(this.pathColors + ".5-1." + this.pathColorsTypeId, 5);
		this.config.set(this.pathColors + ".5-1." + this.pathColorsData, 1);
		this.config.set(this.pathColors + ".5-1." + this.pathColorsR, 126);
		this.config.set(this.pathColors + ".5-1." + this.pathColorsG, 93);
		this.config.set(this.pathColors + ".5-1." + this.pathColorsB, 53);
		
		this.config.set(this.pathColors + ".5-2." + this.pathColorsTypeId, 5);
		this.config.set(this.pathColors + ".5-2." + this.pathColorsData, 2);
		this.config.set(this.pathColors + ".5-2." + this.pathColorsR, 212);
		this.config.set(this.pathColors + ".5-2." + this.pathColorsG, 201);
		this.config.set(this.pathColors + ".5-2." + this.pathColorsB, 139);
		
		this.config.set(this.pathColors + ".5-3." + this.pathColorsTypeId, 5);
		this.config.set(this.pathColors + ".5-3." + this.pathColorsData, 3);
		this.config.set(this.pathColors + ".5-3." + this.pathColorsR, 182);
		this.config.set(this.pathColors + ".5-3." + this.pathColorsG, 133);
		this.config.set(this.pathColors + ".5-3." + this.pathColorsB, 99);
		
		this.config.set(this.pathColors + ".7-0." + this.pathColorsTypeId, 7);
		this.config.set(this.pathColors + ".7-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".7-0." + this.pathColorsR, 86);
		this.config.set(this.pathColors + ".7-0." + this.pathColorsG, 86);
		this.config.set(this.pathColors + ".7-0." + this.pathColorsB, 86);
		
		this.config.set(this.pathColors + ".14-0." + this.pathColorsTypeId, 14);
		this.config.set(this.pathColors + ".14-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".14-0." + this.pathColorsR, 115);
		this.config.set(this.pathColors + ".14-0." + this.pathColorsG, 115);
		this.config.set(this.pathColors + ".14-0." + this.pathColorsB, 115);
		
		this.config.set(this.pathColors + ".15-0." + this.pathColorsTypeId, 15);
		this.config.set(this.pathColors + ".15-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".15-0." + this.pathColorsR, 103);
		this.config.set(this.pathColors + ".15-0." + this.pathColorsG, 103);
		this.config.set(this.pathColors + ".15-0." + this.pathColorsB, 103);
		
		this.config.set(this.pathColors + ".16-0." + this.pathColorsTypeId, 16);
		this.config.set(this.pathColors + ".16-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".16-0." + this.pathColorsR, 51);
		this.config.set(this.pathColors + ".16-0." + this.pathColorsG, 51);
		this.config.set(this.pathColors + ".16-0." + this.pathColorsB, 51);
		
		this.config.set(this.pathColors + ".17-0." + this.pathColorsTypeId, 17);
		this.config.set(this.pathColors + ".17-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".17-0." + this.pathColorsR, 66);
		this.config.set(this.pathColors + ".17-0." + this.pathColorsG, 53);
		this.config.set(this.pathColors + ".17-0." + this.pathColorsB, 33);
		
		this.config.set(this.pathColors + ".17-1." + this.pathColorsTypeId, 17);
		this.config.set(this.pathColors + ".17-1." + this.pathColorsData, 1);
		this.config.set(this.pathColors + ".17-1." + this.pathColorsR, 46);
		this.config.set(this.pathColors + ".17-1." + this.pathColorsG, 31);
		this.config.set(this.pathColors + ".17-1." + this.pathColorsB, 13);
		
		this.config.set(this.pathColors + ".17-2." + this.pathColorsTypeId, 17);
		this.config.set(this.pathColors + ".17-2." + this.pathColorsData, 2);
		this.config.set(this.pathColors + ".17-2." + this.pathColorsR, 150);
		this.config.set(this.pathColors + ".17-2." + this.pathColorsG, 147);
		this.config.set(this.pathColors + ".17-2." + this.pathColorsB, 141);
		
		this.config.set(this.pathColors + ".17-3." + this.pathColorsTypeId, 17);
		this.config.set(this.pathColors + ".17-3." + this.pathColorsData, 3);
		this.config.set(this.pathColors + ".17-3." + this.pathColorsR, 38);
		this.config.set(this.pathColors + ".17-3." + this.pathColorsG, 30);
		this.config.set(this.pathColors + ".17-3." + this.pathColorsB, 12);
		
		this.config.set(this.pathColors + ".19-0." + this.pathColorsTypeId, 19);
		this.config.set(this.pathColors + ".19-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".19-0." + this.pathColorsR, 195);
		this.config.set(this.pathColors + ".19-0." + this.pathColorsG, 195);
		this.config.set(this.pathColors + ".19-0." + this.pathColorsB, 60);
		
		this.config.set(this.pathColors + ".20-0." + this.pathColorsTypeId, 20);
		this.config.set(this.pathColors + ".20-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".20-0." + this.pathColorsR, 190);
		this.config.set(this.pathColors + ".20-0." + this.pathColorsG, 242);
		this.config.set(this.pathColors + ".20-0." + this.pathColorsB, 251);
		
		this.config.set(this.pathColors + ".21-0." + this.pathColorsTypeId, 21);
		this.config.set(this.pathColors + ".21-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".21-0." + this.pathColorsR, 50);
		this.config.set(this.pathColors + ".21-0." + this.pathColorsG, 95);
		this.config.set(this.pathColors + ".21-0." + this.pathColorsB, 131);
		
		this.config.set(this.pathColors + ".22-0." + this.pathColorsTypeId, 22);
		this.config.set(this.pathColors + ".22-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".22-0." + this.pathColorsR, 41);
		this.config.set(this.pathColors + ".22-0." + this.pathColorsG, 78);
		this.config.set(this.pathColors + ".22-0." + this.pathColorsB, 138);
		
		this.config.set(this.pathColors + ".23-0." + this.pathColorsTypeId, 23);
		this.config.set(this.pathColors + ".23-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".23-0." + this.pathColorsR, 138);
		this.config.set(this.pathColors + ".23-0." + this.pathColorsG, 138);
		this.config.set(this.pathColors + ".23-0." + this.pathColorsB, 138);
		
		this.config.set(this.pathColors + ".24-0." + this.pathColorsTypeId, 24);
		this.config.set(this.pathColors + ".24-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".24-0." + this.pathColorsR, 143);
		this.config.set(this.pathColors + ".24-0." + this.pathColorsG, 138);
		this.config.set(this.pathColors + ".24-0." + this.pathColorsB, 114);
		
		this.config.set(this.pathColors + ".24-1." + this.pathColorsTypeId, 24);
		this.config.set(this.pathColors + ".24-1." + this.pathColorsData, 1);
		this.config.set(this.pathColors + ".24-1." + this.pathColorsR, 139);
		this.config.set(this.pathColors + ".24-1." + this.pathColorsG, 133);
		this.config.set(this.pathColors + ".24-1." + this.pathColorsB, 104);
		
		this.config.set(this.pathColors + ".24-2." + this.pathColorsTypeId, 24);
		this.config.set(this.pathColors + ".24-2." + this.pathColorsData, 2);
		this.config.set(this.pathColors + ".24-2." + this.pathColorsR, 136);
		this.config.set(this.pathColors + ".24-2." + this.pathColorsG, 131);
		this.config.set(this.pathColors + ".24-2." + this.pathColorsB, 99);
		
		this.config.set(this.pathColors + ".25-0." + this.pathColorsTypeId, 25);
		this.config.set(this.pathColors + ".25-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".25-0." + this.pathColorsR, 153);
		this.config.set(this.pathColors + ".25-0." + this.pathColorsG, 101);
		this.config.set(this.pathColors + ".25-0." + this.pathColorsB, 74);
		
		this.config.set(this.pathColors + ".35-0." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".35-0." + this.pathColorsR, 232);
		this.config.set(this.pathColors + ".35-0." + this.pathColorsG, 232);
		this.config.set(this.pathColors + ".35-0." + this.pathColorsB, 232);
		
		this.config.set(this.pathColors + ".35-1." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-1." + this.pathColorsData, 1);
		this.config.set(this.pathColors + ".35-1." + this.pathColorsR, 215);
		this.config.set(this.pathColors + ".35-1." + this.pathColorsG, 119);
		this.config.set(this.pathColors + ".35-1." + this.pathColorsB, 53);
		
		this.config.set(this.pathColors + ".35-2." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-2." + this.pathColorsData, 2);
		this.config.set(this.pathColors + ".35-2." + this.pathColorsR, 175);
		this.config.set(this.pathColors + ".35-2." + this.pathColorsG, 67);
		this.config.set(this.pathColors + ".35-2." + this.pathColorsB, 184);
		
		this.config.set(this.pathColors + ".35-3." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-3." + this.pathColorsData, 3);
		this.config.set(this.pathColors + ".35-3." + this.pathColorsR, 125);
		this.config.set(this.pathColors + ".35-3." + this.pathColorsG, 151);
		this.config.set(this.pathColors + ".35-3." + this.pathColorsB, 206);
		
		this.config.set(this.pathColors + ".35-4." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-4." + this.pathColorsData, 4);
		this.config.set(this.pathColors + ".35-4." + this.pathColorsR, 159);
		this.config.set(this.pathColors + ".35-4." + this.pathColorsG, 149);
		this.config.set(this.pathColors + ".35-4." + this.pathColorsB, 35);
		
		this.config.set(this.pathColors + ".35-5." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-5." + this.pathColorsData, 5);
		this.config.set(this.pathColors + ".35-5." + this.pathColorsR, 72);
		this.config.set(this.pathColors + ".35-5." + this.pathColorsG, 187);
		this.config.set(this.pathColors + ".35-5." + this.pathColorsB, 60);
		
		this.config.set(this.pathColors + ".35-6." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-6." + this.pathColorsData, 6);
		this.config.set(this.pathColors + ".35-6." + this.pathColorsR, 213);
		this.config.set(this.pathColors + ".35-6." + this.pathColorsG, 150);
		this.config.set(this.pathColors + ".35-6." + this.pathColorsB, 167);
		
		this.config.set(this.pathColors + ".35-7." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-7." + this.pathColorsData, 7);
		this.config.set(this.pathColors + ".35-7." + this.pathColorsR, 69);
		this.config.set(this.pathColors + ".35-7." + this.pathColorsG, 69);
		this.config.set(this.pathColors + ".35-7." + this.pathColorsB, 69);
		
		this.config.set(this.pathColors + ".35-8." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-8." + this.pathColorsData, 8);
		this.config.set(this.pathColors + ".35-8." + this.pathColorsR, 166);
		this.config.set(this.pathColors + ".35-8." + this.pathColorsG, 172);
		this.config.set(this.pathColors + ".35-8." + this.pathColorsB, 172);
		
		this.config.set(this.pathColors + ".35-9." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-9." + this.pathColorsData, 9);
		this.config.set(this.pathColors + ".35-9." + this.pathColorsR, 43);
		this.config.set(this.pathColors + ".35-9." + this.pathColorsG, 102);
		this.config.set(this.pathColors + ".35-9." + this.pathColorsB, 126);
		
		this.config.set(this.pathColors + ".35-10." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-10." + this.pathColorsData, 10);
		this.config.set(this.pathColors + ".35-10." + this.pathColorsR, 135);
		this.config.set(this.pathColors + ".35-10." + this.pathColorsG, 69);
		this.config.set(this.pathColors + ".35-10." + this.pathColorsB, 192);
		
		this.config.set(this.pathColors + ".35-11." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-11." + this.pathColorsData, 11);
		this.config.set(this.pathColors + ".35-11." + this.pathColorsR, 49);
		this.config.set(this.pathColors + ".35-11." + this.pathColorsG, 59);
		this.config.set(this.pathColors + ".35-11." + this.pathColorsB, 150);
		
		this.config.set(this.pathColors + ".35-12." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-12." + this.pathColorsData, 12);
		this.config.set(this.pathColors + ".35-12." + this.pathColorsR, 85);
		this.config.set(this.pathColors + ".35-12." + this.pathColorsG, 55);
		this.config.set(this.pathColors + ".35-12." + this.pathColorsB, 34);
		
		this.config.set(this.pathColors + ".35-13." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-13." + this.pathColorsData, 13);
		this.config.set(this.pathColors + ".35-13." + this.pathColorsR, 56);
		this.config.set(this.pathColors + ".35-13." + this.pathColorsG, 75);
		this.config.set(this.pathColors + ".35-13." + this.pathColorsB, 30);
		
		this.config.set(this.pathColors + ".35-14." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-14." + this.pathColorsData, 14);
		this.config.set(this.pathColors + ".35-14." + this.pathColorsR, 161);
		this.config.set(this.pathColors + ".35-14." + this.pathColorsG, 55);
		this.config.set(this.pathColors + ".35-14." + this.pathColorsB, 52);
		
		this.config.set(this.pathColors + ".35-15." + this.pathColorsTypeId, 35);
		this.config.set(this.pathColors + ".35-15." + this.pathColorsData, 15);
		this.config.set(this.pathColors + ".35-15." + this.pathColorsR, 21);
		this.config.set(this.pathColors + ".35-15." + this.pathColorsG, 17);
		this.config.set(this.pathColors + ".35-15." + this.pathColorsB, 17);
		
		this.config.set(this.pathColors + ".41-0." + this.pathColorsTypeId, 41);
		this.config.set(this.pathColors + ".41-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".41-0." + this.pathColorsR, 252);
		this.config.set(this.pathColors + ".41-0." + this.pathColorsG, 238);
		this.config.set(this.pathColors + ".41-0." + this.pathColorsB, 67);
		
		this.config.set(this.pathColors + ".42-0." + this.pathColorsTypeId, 42);
		this.config.set(this.pathColors + ".42-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".42-0." + this.pathColorsR, 218);
		this.config.set(this.pathColors + ".42-0." + this.pathColorsG, 218);
		this.config.set(this.pathColors + ".42-0." + this.pathColorsB, 218);
		
		this.config.set(this.pathColors + ".43-0." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".43-0." + this.pathColorsR, 166);
		this.config.set(this.pathColors + ".43-0." + this.pathColorsG, 166);
		this.config.set(this.pathColors + ".43-0." + this.pathColorsB, 166);
		
		this.config.set(this.pathColors + ".43-1." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-1." + this.pathColorsData, 1);
		this.config.set(this.pathColors + ".43-1." + this.pathColorsR, 148);
		this.config.set(this.pathColors + ".43-1." + this.pathColorsG, 143);
		this.config.set(this.pathColors + ".43-1." + this.pathColorsB, 113);
		
		this.config.set(this.pathColors + ".43-2." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-2." + this.pathColorsData, 2);
		this.config.set(this.pathColors + ".43-2." + this.pathColorsR, 100);
		this.config.set(this.pathColors + ".43-2." + this.pathColorsG, 83);
		this.config.set(this.pathColors + ".43-2." + this.pathColorsB, 48);
		
		this.config.set(this.pathColors + ".43-3." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-3." + this.pathColorsData, 3);
		this.config.set(this.pathColors + ".43-3." + this.pathColorsR, 90);
		this.config.set(this.pathColors + ".43-3." + this.pathColorsG, 90);
		this.config.set(this.pathColors + ".43-3." + this.pathColorsB, 90);
		
		this.config.set(this.pathColors + ".43-4." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-4." + this.pathColorsData, 4);
		this.config.set(this.pathColors + ".43-4." + this.pathColorsR, 97);
		this.config.set(this.pathColors + ".43-4." + this.pathColorsG, 54);
		this.config.set(this.pathColors + ".43-4." + this.pathColorsB, 42);
		
		this.config.set(this.pathColors + ".43-5." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-5." + this.pathColorsData, 5);
		this.config.set(this.pathColors + ".43-5." + this.pathColorsR, 76);
		this.config.set(this.pathColors + ".43-5." + this.pathColorsG, 76);
		this.config.set(this.pathColors + ".43-5." + this.pathColorsB, 76);
		
		this.config.set(this.pathColors + ".43-6." + this.pathColorsTypeId, 43);
		this.config.set(this.pathColors + ".43-6." + this.pathColorsData, 6);
		this.config.set(this.pathColors + ".43-6." + this.pathColorsR, 103);
		this.config.set(this.pathColors + ".43-6." + this.pathColorsG, 103);
		this.config.set(this.pathColors + ".43-6." + this.pathColorsB, 103);
		
		this.config.set(this.pathColors + ".45-0." + this.pathColorsTypeId, 45);
		this.config.set(this.pathColors + ".45-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".45-0." + this.pathColorsR, 123);
		this.config.set(this.pathColors + ".45-0." + this.pathColorsG, 68);
		this.config.set(this.pathColors + ".45-0." + this.pathColorsB, 53);
		
		this.config.set(this.pathColors + ".48-0." + this.pathColorsTypeId, 48);
		this.config.set(this.pathColors + ".48-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".48-0." + this.pathColorsR, 57);
		this.config.set(this.pathColors + ".48-0." + this.pathColorsG, 99);
		this.config.set(this.pathColors + ".48-0." + this.pathColorsB, 57);
		
		this.config.set(this.pathColors + ".49-0." + this.pathColorsTypeId, 49);
		this.config.set(this.pathColors + ".49-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".49-0." + this.pathColorsR, 59);
		this.config.set(this.pathColors + ".49-0." + this.pathColorsG, 47);
		this.config.set(this.pathColors + ".49-0." + this.pathColorsB, 85);
		
		this.config.set(this.pathColors + ".56-0." + this.pathColorsTypeId, 56);
		this.config.set(this.pathColors + ".56-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".56-0." + this.pathColorsR, 92);
		this.config.set(this.pathColors + ".56-0." + this.pathColorsG, 233);
		this.config.set(this.pathColors + ".56-0." + this.pathColorsB, 242);
		
		this.config.set(this.pathColors + ".57-0." + this.pathColorsTypeId, 57);
		this.config.set(this.pathColors + ".57-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".57-0." + this.pathColorsR, 133);
		this.config.set(this.pathColors + ".57-0." + this.pathColorsG, 226);
		this.config.set(this.pathColors + ".57-0." + this.pathColorsB, 222);
		
		this.config.set(this.pathColors + ".60-0." + this.pathColorsTypeId, 60);
		this.config.set(this.pathColors + ".60-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".60-0." + this.pathColorsR, 125);
		this.config.set(this.pathColors + ".60-0." + this.pathColorsG, 84);
		this.config.set(this.pathColors + ".60-0." + this.pathColorsB, 50);
		
		this.config.set(this.pathColors + ".73-0." + this.pathColorsTypeId, 73);
		this.config.set(this.pathColors + ".73-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".73-0." + this.pathColorsR, 141);
		this.config.set(this.pathColors + ".73-0." + this.pathColorsG, 3);
		this.config.set(this.pathColors + ".73-0." + this.pathColorsB, 3);
		
		this.config.set(this.pathColors + ".87-0." + this.pathColorsTypeId, 87);
		this.config.set(this.pathColors + ".87-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".87-0." + this.pathColorsR, 164);
		this.config.set(this.pathColors + ".87-0." + this.pathColorsG, 88);
		this.config.set(this.pathColors + ".87-0." + this.pathColorsB, 88);
		
		this.config.set(this.pathColors + ".88-0." + this.pathColorsTypeId, 88);
		this.config.set(this.pathColors + ".88-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".88-0." + this.pathColorsR, 120);
		this.config.set(this.pathColors + ".88-0." + this.pathColorsG, 96);
		this.config.set(this.pathColors + ".88-0." + this.pathColorsB, 81);
		
		this.config.set(this.pathColors + ".89-0." + this.pathColorsTypeId, 89);
		this.config.set(this.pathColors + ".89-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".89-0." + this.pathColorsR, 113);
		this.config.set(this.pathColors + ".89-0." + this.pathColorsG, 110);
		this.config.set(this.pathColors + ".89-0." + this.pathColorsB, 72);
		
		this.config.set(this.pathColors + ".123-0." + this.pathColorsTypeId, 123);
		this.config.set(this.pathColors + ".123-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".123-0." + this.pathColorsR, 118);
		this.config.set(this.pathColors + ".123-0." + this.pathColorsG, 66);
		this.config.set(this.pathColors + ".123-0." + this.pathColorsB, 35);
		
		this.config.set(this.pathColors + ".129-0." + this.pathColorsTypeId, 129);
		this.config.set(this.pathColors + ".129-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".129-0." + this.pathColorsR, 23);
		this.config.set(this.pathColors + ".129-0." + this.pathColorsG, 218);
		this.config.set(this.pathColors + ".129-0." + this.pathColorsB, 97);
		
		this.config.set(this.pathColors + ".133-0." + this.pathColorsTypeId, 133);
		this.config.set(this.pathColors + ".133-0." + this.pathColorsData, 0);
		this.config.set(this.pathColors + ".133-0." + this.pathColorsR, 60);
		this.config.set(this.pathColors + ".133-0." + this.pathColorsG, 195);
		this.config.set(this.pathColors + ".133-0." + this.pathColorsB, 92);
		
	}
	
	//#############################################################################################
}
