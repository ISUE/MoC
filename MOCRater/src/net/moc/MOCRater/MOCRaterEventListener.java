package net.moc.MOCRater;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.io.IOException;
import java.util.Calendar;
import javax.imageio.ImageIO;
import net.moc.MOCRater.GUI.PatternBrowserWindow;
import net.moc.MOCRater.GUI.PatternManagerWindow;
import net.moc.MOCRater.GUI.RatingBrowserWindow;
import net.moc.MOCRater.GUI.RatingWindow;
import net.moc.MOCRater.GUI.Widgets.MOCComboBoxSelectionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenshotReceivedEvent;
import org.getspout.spoutapi.gui.Screen;
public class MOCRaterEventListener implements Listener{
	//=====================================================
	private MOCRater plugin;
	public MOCRaterEventListener(MOCRater plugin) {
		this.plugin = plugin;
	}
	//=====================================================
	
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerQuitEvent event) {
		//Clean up all open windows
		if (this.plugin.gui.patternBrowserWindows.containsKey(event.getPlayer())) { this.plugin.gui.patternBrowserWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.patternManagerWindows.containsKey(event.getPlayer())) { this.plugin.gui.patternManagerWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.ratingBrowserWindows.containsKey(event.getPlayer())) { this.plugin.gui.ratingBrowserWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.ratingWindows.containsKey(event.getPlayer())) { this.plugin.gui.ratingWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.latestScreenShots.containsKey(event.getPlayer())) { this.plugin.gui.latestScreenShots.remove(event.getPlayer()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerKickEvent event) {
		//Clean up all open windows
		if (this.plugin.gui.patternBrowserWindows.containsKey(event.getPlayer())) { this.plugin.gui.patternBrowserWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.patternManagerWindows.containsKey(event.getPlayer())) { this.plugin.gui.patternManagerWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.ratingBrowserWindows.containsKey(event.getPlayer())) { this.plugin.gui.ratingBrowserWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.ratingWindows.containsKey(event.getPlayer())) { this.plugin.gui.ratingWindows.remove(event.getPlayer()); }
		if (this.plugin.gui.latestScreenShots.containsKey(event.getPlayer())) { this.plugin.gui.latestScreenShots.remove(event.getPlayer()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof RatingBrowserWindow) { ((RatingBrowserWindow) screen).onClick(event.getButton()); }
		if(screen instanceof RatingWindow) { ((RatingWindow) screen).onClick(event.getButton()); }
		if(screen instanceof PatternBrowserWindow) { ((PatternBrowserWindow) screen).onClick(event.getButton()); }
		if(screen instanceof PatternManagerWindow) { ((PatternManagerWindow) screen).onClick(event.getButton()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(MOCComboBoxSelectionEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof RatingWindow) { ((RatingWindow) screen).onSelection(event.getListWidget()); }
		if(screen instanceof RatingBrowserWindow) { ((RatingBrowserWindow) screen).onSelection(event.getListWidget()); }
		if(screen instanceof PatternBrowserWindow) { ((PatternBrowserWindow) screen).onSelection(event.getListWidget()); }
		if(screen instanceof PatternManagerWindow) { ((PatternManagerWindow) screen).onSelection(event.getListWidget()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(ScreenshotReceivedEvent event) {
		//Generate File name
		Calendar c = Calendar.getInstance();
		String year = "" + c.get(Calendar.YEAR);
		
		String month = "" + c.get(Calendar.MONTH);
		if (month.length() == 1) month = "0" + month;
		
		String day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) day = "0" + day;
		
		String hour = "" + c.get(Calendar.HOUR_OF_DAY);
		if (hour.length() == 1) hour = "0" + hour;
		
		String minute = "" + c.get(Calendar.MINUTE);
		if (minute.length() == 1) minute = "0" + minute;
		
		String second = "" + c.get(Calendar.SECOND);
		if (second.length() == 1) second = "0" + second;
		
		String millisecond = "" + c.get(Calendar.MILLISECOND);
		if (millisecond.length() == 1) millisecond = "00" + millisecond;
		else if (millisecond.length() == 2) millisecond = "0" + millisecond;
		
		String fileName = year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + millisecond + "-" + event.getPlayer().getName() + ".jpg";
		
		//Open file
		File folder = new File(this.plugin.screenShotFolder.getAbsolutePath() + File.separator + event.getPlayer().getName());
		folder.mkdir();
		
		File screenShot = new File(folder.getAbsolutePath() + File.separator + fileName);
	    
		//Write to file
		try {
			//Create file
			screenShot.createNewFile();
			//Write image data
			ImageIO.write(event.getScreenshot(), "jpg", screenShot);
			
			//Record player and screen shot received
			this.plugin.gui.latestScreenShots.put(event.getPlayer(), screenShot);
			this.plugin.gui.screenShots.put(screenShot, false);

			//Set owner
			if (!System.getProperty("os.name").contains("Windows")) {
				//File permissions
				Files.setPosixFilePermissions(screenShot.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
				
			}

		} catch (IOException e) { e.printStackTrace(); }
		
	}
	//##############################################################################################
}
