package net.moc.MOCChemistry;

import java.util.HashMap;
import java.util.Map;

import net.moc.MOCChemistry.GUI.ChemistryBookWindow;
import net.moc.MOCChemistry.GUI.ChemistryRecipeEditor;
import net.moc.MOCChemistry.GUI.ChemistryTableWindow;
import net.moc.MOCChemistry.GUI.TestWindow;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCChemistryGUI {
	//=====================================================
	private MOCChemistry plugin;
	private Map<SpoutPlayer, ChemistryTableWindow> chemistryTableWindows = new HashMap<SpoutPlayer, ChemistryTableWindow>();
	private Map<SpoutPlayer, ChemistryBookWindow> chemistryBookWindows = new HashMap<SpoutPlayer, ChemistryBookWindow>();
	private Map<SpoutPlayer, ChemistryRecipeEditor>  chemistryRecipeEditorWindows = new HashMap<SpoutPlayer, ChemistryRecipeEditor>();
	private Map<SpoutPlayer, TestWindow> testWindows = new HashMap<SpoutPlayer, TestWindow>();
	
	public ChemistryTableWindow getChemistryTableWindow(SpoutPlayer player) { return chemistryTableWindows.get(player); }
	public ChemistryBookWindow getChemistryBookWindow(SpoutPlayer player) { return chemistryBookWindows.get(player); }
	
	public void removePlayerWindows(Player player) { this.chemistryTableWindows.remove(player); this.chemistryBookWindows.remove(player); }
	
	public MOCChemistryGUI(MOCChemistry plugin) { this.plugin = plugin; }
	//=====================================================
	
	public void displayChemistryTableGUI(SpoutPlayer player) {
		//Check if a window was already generated for the player before
		if (!this.chemistryTableWindows.containsKey(player)) { this.chemistryTableWindows.put(player, new ChemistryTableWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.chemistryTableWindows.get(player).open();
		
	}

	public void displayChemistryBookGUI(SpoutPlayer player) {
		//Check if a window was already generated for the player before
		if (!this.chemistryBookWindows.containsKey(player)) { this.chemistryBookWindows.put(player, new ChemistryBookWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.chemistryBookWindows.get(player).open();
		
	}

	public void displayChemistryRecipeEditorGUI(SpoutPlayer player, String recipeName, int type) {
		//Check if a window was already generated for the player before
		if (!this.chemistryRecipeEditorWindows.containsKey(player)) { this.chemistryRecipeEditorWindows.put(player, new ChemistryRecipeEditor(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.chemistryRecipeEditorWindows.get(player).open(recipeName, type);
		
	}

	public void displayTestGUI(SpoutPlayer player) {
		//Check if a window was already generated for the player before
		if (!this.testWindows.containsKey(player)) { this.testWindows.put(player, new TestWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.testWindows.get(player).open();
		
	}

	
}
