package net.moc.MOCChemistry.Recipes;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

public class SplitRecipe implements Comparable<SplitRecipe> {
	private String name;
	private boolean valid = true;
	
	private SpoutItemStack input;
	private SpoutItemStack[] output = new SpoutItemStack[4];
	private int[] outputChance = {100, 100, 100, 100};
	
	public SplitRecipe(String name, String inputString, String[] outputStrings) {
		this.name = name;
		
		this.valid = parseInput(inputString);
		
		for (int i = 0 ; i < 4 ; i++) {
			this.valid = parseOutput(outputStrings[i], i);
			if (!this.valid) break;
			
		}
		
	}

	private boolean parseOutput(String outputString, int i) {
		//output - id=x data=x enchant=s,s,s customName=s q=x
		if (outputString == null) {
			this.output[i] = new SpoutItemStack(0);
			return true;
			
		}
		
		//Clean up
		outputString = outputString.trim().replaceAll("  +", " ");
		String[] split = outputString.split(" ");

		//Parts
		int typeId = -1;
		byte data = 0;
		String customBlockName = null;
		String[] enchants = {null, null, null};
		int quantity = 1;
		int chance = 100;

		//Find parts
		for (int j = 0 ; j < split.length ; j++) {
			String[] part = split[j].split("=");
			if (part.length != 2) return false;
			
			if (part[0].equalsIgnoreCase("id")) { try { typeId = Integer.parseInt(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("data")) { try { data = Byte.parseByte(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("name")) { customBlockName = part[1]; }
			else if (part[0].equalsIgnoreCase("chance")) { try { chance = Integer.parseInt(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("q")) { try { quantity = Integer.parseInt(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("ench")) {
				String[] enchs = part[1].split(",");
				for (int k = 0 ; k < enchs.length && k < enchants.length; k++) {
					enchants[k] = enchs[k];
					
				}
				
			}
			
		}

		//Generate item stack
		if (customBlockName != null) {
			for (CustomBlock block : MaterialData.getCustomBlocks()) {
				if (block.getName().equalsIgnoreCase(customBlockName)) {
					if (quantity < 1) quantity = 1;
					this.output[i] = new SpoutItemStack(block, quantity);
					if (chance > 0) this.outputChance[i] = chance;
					return true;
					
				}
				
			}
			
			//No match found
			return false;
			
		} else {
			//Check type id
			if (Material.getMaterial(typeId) == null) return false;
			
			//Check data
			if (data < 0) data = 0;
			
			if (chance > 0) this.outputChance[i] = chance;
			if (quantity < 1) quantity = 1;
			
			this.output[i] = new SpoutItemStack(typeId, data);
			this.output[i].setAmount(quantity);
			
			try {
				for (String ench : enchants) { if (ench != null && ench.split("-").length == 2) { this.output[i].addEnchantment(Enchantment.getById(Integer.parseInt(ench.split("-")[0])), Integer.parseInt(ench.split("-")[1])); } }
			} catch (NumberFormatException e) {} catch (NullPointerException e) {} catch (NullArgumentException e) {} catch (IllegalArgumentException e) {}
			
			return true;
			
		}
		
	}

	private boolean parseInput(String inputString) {
		//input - id=x data=x enchant=s,s,s name=s
		if (inputString == null) return false;
		
		//Clean up
		inputString = inputString.trim().replaceAll("  +", " ");
		String[] split = inputString.split(" ");
		
		//Parts
		int typeId = -1;
		byte data = 0;
		String customBlockName = null;
		String[] enchants = {null, null, null};
		
		//Find parts
		for (int j = 0 ; j < split.length ; j++) {
			String[] part = split[j].split("=");
			if (part.length != 2) return false;
			if (part[0].equalsIgnoreCase("id")) { try { typeId = Integer.parseInt(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("data")) { try { data = Byte.parseByte(part[1]); } catch (NumberFormatException e) { return false; }	}
			else if (part[0].equalsIgnoreCase("name")) { customBlockName = name; }
			else if (part[0].equalsIgnoreCase("ench")) {
				String[] enchs = part[1].split(",");
				for (int k = 0 ; k < enchs.length && k < enchants.length; k++) {
					enchants[k] = enchs[k];
					
				}
				
			}
			
		}
		
		//Generate item stack
		if (customBlockName != null) {
			for (CustomBlock block : MaterialData.getCustomBlocks()) {
				if (block.getName().equalsIgnoreCase(customBlockName)) {
					this.input = new SpoutItemStack(block, 1);
					return true;
					
				}
				
			}
			
			//No match found
			return false;
			
		} else {
			//Check type id
			if (Material.getMaterial(typeId) == null) return false;
			
			//Check data
			if (data < 0) data = 0;
			
			this.input = new SpoutItemStack(typeId, data);
			
			try {
				for (String ench : enchants) { if (ench != null && ench.split("-").length == 2) this.input.addEnchantment(Enchantment.getById(Integer.parseInt(ench.split("-")[0])), Integer.parseInt(ench.split("-")[1])); }
			} catch (NumberFormatException e) {} catch (NullPointerException e) {} catch (NullArgumentException e) {} catch (IllegalArgumentException e) {}
			
			return true;
			
		}
		
	}

	public String getName() { return name; }
	public boolean isValid() { return this.valid; }
	
	public SpoutItemStack getInput() { return this.input; }
	
	public SpoutItemStack[] getOutput() { return this.output; }
	public int[] getOutputChance() { return outputChance; }
	
	public int compareTo(SplitRecipe o) { return this.name.compareTo(o.name); }
	
}
