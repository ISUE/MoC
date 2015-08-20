package net.moc.MOCRater;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.moc.MOCRater.GUI.PatternBrowserWindow;
import net.moc.MOCRater.GUI.PatternManagerWindow;
import net.moc.MOCRater.GUI.RatingBrowserWindow;
import net.moc.MOCRater.GUI.RatingWindow;
import net.moc.MOCRater.SQL.MOCComment;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCRaterGUI {
	//=====================================================
	private MOCRater plugin;
	protected Map<SpoutPlayer, RatingWindow> ratingWindows = new HashMap<SpoutPlayer, RatingWindow>();
	protected Map<SpoutPlayer, RatingBrowserWindow> ratingBrowserWindows = new HashMap<SpoutPlayer, RatingBrowserWindow>();
	protected Map<SpoutPlayer, PatternBrowserWindow> patternBrowserWindows = new HashMap<SpoutPlayer, PatternBrowserWindow>();
	protected Map<SpoutPlayer, PatternManagerWindow> patternManagerWindows = new HashMap<SpoutPlayer, PatternManagerWindow>();
	protected Map<SpoutPlayer, File> latestScreenShots = new HashMap<SpoutPlayer, File>(); public Map<SpoutPlayer, File> getLatestScreenShots() { return this.latestScreenShots; }
	protected TreeMap<File, Boolean> screenShots = new TreeMap<File, Boolean>(); public TreeMap<File, Boolean> getScreenShots() { return this.screenShots; }
	
	public MOCRaterGUI(MOCRater plugin) { this.plugin = plugin; }
	//=====================================================
	
	public void displayRatingWindowGUI(SpoutPlayer player, boolean takeScreenshot, String patternToCritique) {
		//Check if a window was already generated for the player before
		if (!this.ratingWindows.containsKey(player)) { this.ratingWindows.put(player, new RatingWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.ratingWindows.get(player).open(takeScreenshot, patternToCritique);
		
	}
	
	public void displayRatingBrowserWindowGUI(SpoutPlayer player, MOCComment comment) {
		//Check if a window was already generated for the player before
		if (!this.ratingBrowserWindows.containsKey(player)) { this.ratingBrowserWindows.put(player, new RatingBrowserWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.ratingBrowserWindows.get(player).open(comment);
		
	}

	public void displayPatternBrowserWindowGUI(SpoutPlayer player, boolean takeScreenshot) {
		//Check if a window was already generated for the player before
		if (!this.patternBrowserWindows.containsKey(player)) { this.patternBrowserWindows.put(player, new PatternBrowserWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.patternBrowserWindows.get(player).open(takeScreenshot);
		
	}

	public void displayPatternManagerWindowGUI(SpoutPlayer player, String patternName) {
		//Check if a window was already generated for the player before
		if (!this.patternManagerWindows.containsKey(player)) { this.patternManagerWindows.put(player, new PatternManagerWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.patternManagerWindows.get(player).open(patternName);
		
	}
	
	public void deleteUnusedScreenshots() {
		ArrayList<File> toDelete = new ArrayList<File>();
		
		for (File file : this.screenShots.keySet()) {
			if (this.screenShots.get(file) == false) {
				toDelete.add(file);
				file.delete();
				
			}
			
		}
		
		for (File file : toDelete) { this.screenShots.remove(file); }
		
	}

}
