package net.moc.MOCChemistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.moc.MOCChemistry.Blocks.ChemistryTable;
import net.moc.MOCChemistry.Blocks.CustomBlocks;
import net.moc.MOCChemistry.Blocks.Energy;
import net.moc.MOCChemistry.Blocks.EnergyHarvester;
import net.moc.MOCChemistry.Recipes.RecipeBook;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;

public class MOCChemistry extends JavaPlugin {
	//=============================================================================================
	private MOCChemistryCommandExecutor commandExecutor = new MOCChemistryCommandExecutor(this);
	private MOCChemistryEventListener eventListener = new MOCChemistryEventListener(this);
	private MOCChemistryLogHandler log = new MOCChemistryLogHandler(this); public MOCChemistryLogHandler getLog() { return this.log; }
	private MOCChemistryConfig config; public MOCChemistryConfig getConfiguration() { return this.config; }
	private RecipeBook recipes; public RecipeBook getRecipes() { return this.recipes; }
	private String pluginPath; public String getPluginPath() { return this.pluginPath; }
	private File configFile, recipeFile, enchantsFile, customBlocksFile;
	
	private MOCChemistryGUI gui = new MOCChemistryGUI(this); public MOCChemistryGUI getGui() { return gui; }
	private MOCChemistryAccident accident = new MOCChemistryAccident(this); public MOCChemistryAccident getAccident() { return this.accident; }
	
	private ChemistryTable chemistryTableBlock; public ChemistryTable getChemistryTableBlock() { return this.chemistryTableBlock; }
	private EnergyHarvester energyHarvesterBlock; public EnergyHarvester getEnergyHarvesterBlock() { return this.energyHarvesterBlock; }
	private Energy energyBlock; public Energy getEnergyBlock() { return this.energyBlock; }
	
	private CustomBlocks customBlocks; public CustomBlocks getCustomBlocks() { return this.customBlocks; }
	//=============================================================================================

	//=============================================================================================
	public void onEnable() {
    	//Config
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new MOCChemistryConfig(this.configFile, this);
    	
    	//Enchants
    	enchants();
    	
    	//Initialize custom blocks
    	initBlocks();
    	
    	//Custom blocks
    	this.customBlocksFile = new File(this.pluginPath + File.separator + "blocks.yml");
    	this.customBlocks = new CustomBlocks(this.customBlocksFile, this);
    	
    	//Recipes
    	this.recipeFile = new File(this.pluginPath + File.separator + "recipes.yml");
    	this.recipes = new RecipeBook(this.recipeFile);
    	
    	//Commands handler
    	this.getCommand("mc").setExecutor(this.commandExecutor);
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
	}

	public void onDisable() {}
	
	private void initBlocks() {
    	this.chemistryTableBlock = new ChemistryTable(this);
    	this.chemistryTableBlock.setItemDrop(new SpoutItemStack(this.chemistryTableBlock, 1));
    	SpoutShapedRecipe recipe = new SpoutShapedRecipe(new SpoutItemStack(this.chemistryTableBlock, 1));
    	recipe.shape("rbr", "rcr", "gfg");
    	recipe.setIngredient('r', MaterialData.redstone);
    	recipe.setIngredient('b', MaterialData.brewingStandBlock);
    	recipe.setIngredient('c', MaterialData.cauldronBlock);
    	recipe.setIngredient('f', MaterialData.furnace);
    	recipe.setIngredient('g', MaterialData.glass);
    	SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
    	
    	this.energyHarvesterBlock = new EnergyHarvester(this);
    	recipe = new SpoutShapedRecipe(new SpoutItemStack(this.energyHarvesterBlock, 1));
    	recipe.shape("gig", "igi", "gig");
    	recipe.setIngredient('g', MaterialData.goldIngot);
    	recipe.setIngredient('i', MaterialData.ironIngot);
    	SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
		
    	this.energyBlock = new Energy(this);
    	this.energyBlock.setItemDrop(new SpoutItemStack(this.energyBlock, 1));
    	
	}

	private void enchants() {
    	this.enchantsFile = new File(this.pluginPath + File.separator + "enchants.txt");
    	try {
			OutputStream out = new FileOutputStream(this.enchantsFile);
	    	InputStream in =  this.getResource("enchants.txt");
	    	int c;
	    	byte[] buf = new byte[1024];
	    	while ((c = in.read(buf)) > 0) out.write(buf, 0, c);
	    	out.close();
	    	in.close();
	    	
		} catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		
	}
	
}
