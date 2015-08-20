package net.moc.MOCChemistry.Recipes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class RecipeBook {
	//#############################################################################################
	private YamlConfiguration config;
	private File configFile;
	
	private ArrayList<CombineRecipe> combines = new ArrayList<CombineRecipe>(); public ArrayList<CombineRecipe> getCombines() { return this.combines; }
	private ArrayList<SplitRecipe> splits = new ArrayList<SplitRecipe>(); public ArrayList<SplitRecipe> getSplits() { return this.splits; }
	
	private String pathCombines = "Combines";
	private String pathSplits = "Splits";
	private String pathInput = "Input";
	private String pathInput1 = "Input-1";
	private String pathInput2 = "Input-2";
	private String pathInput3 = "Input-3";
	private String pathInput4 = "Input-4";
	private String pathInput5 = "Input-5";
	private String pathInput6 = "Input-6";
	private String pathInput7 = "Input-7";
	private String pathInput8 = "Input-8";
	private String pathInput9 = "Input-9";
	private String pathOutput1 = "Output-1";
	private String pathOutput2 = "Output-2";
	private String pathOutput3 = "Output-3";
	private String pathOutput4 = "Output-4";
	//#############################################################################################

	
	//#############################################################################################
	public RecipeBook(File configFile) {
		this.config = new YamlConfiguration();
		this.configFile = configFile;
		
		this.reload();
		
	}
	
	private void generateRecipeBook() {
		this.combines.clear();
		this.splits.clear();
		
		if (this.listCombines() != null) {
			for (String name : this.listCombines()) {
				String[] input = new String[9];
				String[] output = new String[4];

				//Get inputs
				for (int i = 0 ; i < 9 ; i++) { input[i] = this.getCombineInput(name, i+1); }

				//Get outputs
				for (int i = 0 ; i < 4 ; i++) { output[i] = this.getCombineOutput(name, i+1); }

				CombineRecipe recipe = new CombineRecipe(name, input, output);

				if (recipe.isValid()) this.combines.add(recipe);

			}
			
		}
		
		if (this.listSplits() != null) {
			for (String name : this.listSplits()) {
				String input;
				String[] output = new String[4];

				//Get input
				input = this.getSplitInput(name);

				//Get outputs
				for (int i = 0 ; i < 4 ; i++) { output[i] = this.getSplitOutput(name, i+1); }

				SplitRecipe recipe = new SplitRecipe(name, input, output);

				if (recipe.isValid()) this.splits.add(recipe);

			}
			
		}
		
		Collections.sort(this.combines);
		Collections.sort(this.splits);
		
	}
	//#############################################################################################
	
	public String getSplitOutput(String name, int i) {
		switch(i) {
		case 1:
			return this.config.getString(this.pathSplits + "." + name + "." + this.pathOutput1);
		case 2:
			return this.config.getString(this.pathSplits + "." + name + "." + this.pathOutput2);
		case 3:
			return this.config.getString(this.pathSplits + "." + name + "." + this.pathOutput3);
		case 4:
			return this.config.getString(this.pathSplits + "." + name + "." + this.pathOutput4);
		default:
			return null;
			
		}
		
	}

	public String getSplitInput(String name) {
		return this.config.getString(this.pathSplits + "." + name + "." + this.pathInput);
	}

	public String getCombineOutput(String name, int i) {
		switch(i) {
		case 1:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathOutput1);
		case 2:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathOutput2);
		case 3:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathOutput3);
		case 4:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathOutput4);
		default:
			return null;
			
		}
		
	}

	public String getCombineInput(String name, int i) {
		switch(i) {
		case 1:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput1);
		case 2:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput2);
		case 3:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput3);
		case 4:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput4);
		case 5:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput5);
		case 6:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput6);
		case 7:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput7);
		case 8:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput8);
		case 9:
			return this.config.getString(this.pathCombines + "." + name + "." + this.pathInput9);
		default:
			return null;

		}
		
	}

	public Set<String> listSplits() { return this.list(this.pathSplits); }
	public Set<String> listCombines() { return this.list(this.pathCombines); }
	private Set<String> list(String path) { if (this.config.getConfigurationSection(path) != null) { return this.config.getConfigurationSection(path).getKeys(false); } return null; }
	
	//#############################################################################################
	//---------------------------------------------------------------------------------------------
	public void save() { try { this.config.save(this.configFile); } catch (IOException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void load() { try { this.config.load(this.configFile); } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (InvalidConfigurationException e) { e.printStackTrace(); } }
	//---------------------------------------------------------------------------------------------
	public void reload() {
		if (configFile.exists()){
			this.load();
			if(this.config.getString(this.pathSplits) == null) { this.setDefaultSplits(); this.save(); }
			if(this.config.getString(this.pathCombines) == null) { this.setDefaultCombines(); this.save(); }
			
		} else {
			this.setDefaultSplits();
			this.setDefaultCombines();
			this.save();
			
		}
		
		this.generateRecipeBook();
		
	}
	
	private void setDefaultSplits() {
		this.config.set(this.pathSplits + ".Split Water." + this.pathInput, "id=326");
		this.config.set(this.pathSplits + ".Split Water." + this.pathOutput1, "name=oxygen q=10");
		this.config.set(this.pathSplits + ".Split Water." + this.pathOutput2, "name=hydrogen q=20");
		this.config.set(this.pathSplits + ".Split Water." + this.pathOutput3, "id=325 q=1");
		
	}

	private void setDefaultCombines() {
		this.config.set(this.pathCombines + ".Enchant Diamond Sword." + this.pathInput1, "id=276");
		this.config.set(this.pathCombines + ".Enchant Diamond Sword." + this.pathInput2, "id=327");
		this.config.set(this.pathCombines + ".Enchant Diamond Sword." + this.pathOutput1, "id=276 ench=16-5,20-2,21-3");
		this.config.set(this.pathCombines + ".Enchant Diamond Sword." + this.pathOutput2, "id=325");
		
	}
	//#############################################################################################
}
