package net.moc.MOCKiosk;

import java.util.HashMap;
import java.util.Map;
import net.moc.MOCKiosk.GUI.BrowserWindow;
import net.moc.MOCKiosk.GUI.DisplayWindow;
import net.moc.MOCKiosk.GUI.ManagerWindow;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;
import org.bukkit.block.Block;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCKioskGUI {
	//=====================================================
	private MOCKiosk plugin;
	protected Map<SpoutPlayer, DisplayWindow> displayWindows = new HashMap<SpoutPlayer, DisplayWindow>();
	protected Map<SpoutPlayer, BrowserWindow> browserWindows = new HashMap<SpoutPlayer, BrowserWindow>();
	protected Map<SpoutPlayer, ManagerWindow> managerWindows = new HashMap<SpoutPlayer, ManagerWindow>();
	
	public MOCKioskGUI(MOCKiosk plugin) { this.plugin = plugin; }
	//=====================================================
	
	//-----------------------------------------------------------------------------------------------------
	public void displayDisplayWindowGUI(SpoutPlayer player, MOCKioskKiosk kiosk) {
		//Check if a window was already generated for the player before
		if (!this.displayWindows.containsKey(player)) { this.displayWindows.put(player, new DisplayWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.displayWindows.get(player).open(kiosk);
		
	}
	//-----------------------------------------------------------------------------------------------------
	
	//-----------------------------------------------------------------------------------------------------
	public void displayBrowserWindowGUI(SpoutPlayer player) {
		//Check if a window was already generated for the player before
		if (!this.browserWindows.containsKey(player)) { this.browserWindows.put(player, new BrowserWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.browserWindows.get(player).open();
		
	}
	//-----------------------------------------------------------------------------------------------------

	//-----------------------------------------------------------------------------------------------------
	public void displayManagerWindowGUI(SpoutPlayer player, MOCKioskKiosk kiosk) {
		//Check if a window was already generated for the player before
		if (!this.managerWindows.containsKey(player)) { this.managerWindows.put(player, new ManagerWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.managerWindows.get(player).open(kiosk);
		
	}

	public void displayManagerWindowGUI(SpoutPlayer player, Block block) {
		//Check if a window was already generated for the player before
		if (!this.managerWindows.containsKey(player)) { this.managerWindows.put(player, new ManagerWindow(player, this.plugin)); }
		
		//Get the rating window from the hash map and open it
		this.managerWindows.get(player).open(block);
		
	}
	//-----------------------------------------------------------------------------------------------------
}
