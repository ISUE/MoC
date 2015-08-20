package moc.DreamCrafter.gui;

import java.util.HashMap;
import java.util.Map;

import moc.DreamCrafter.MOCDreamCrafter;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class GuiController {
	private MOCDreamCrafter plugin;
	protected Map<SpoutPlayer, MainWindow> mainWindows = new HashMap<SpoutPlayer, MainWindow>();
	protected Map<SpoutPlayer, InBuildWorldWindow> inBuildWorldWindows = new HashMap<SpoutPlayer, InBuildWorldWindow>();
	protected Map<SpoutPlayer, InDreamWorldWindow> inDreamWorldWindows = new HashMap<SpoutPlayer, InDreamWorldWindow>();
	protected Map<SpoutPlayer, WorldPropertiesWindow> worldPropertiesWindows = new HashMap<SpoutPlayer, WorldPropertiesWindow>();
	protected Map<SpoutPlayer, BlacklistWindow> blacklistWindows = new HashMap<SpoutPlayer, BlacklistWindow>();
	
	public GuiController(MOCDreamCrafter plugin) { this.plugin = plugin; }

	public void showMainWindow(SpoutPlayer player) {
		if (!mainWindows.containsKey(player)) { mainWindows.put(player, new MainWindow(player, plugin)); }
		mainWindows.get(player).open();		
	}
	
	public void showInBuildWorldWindowWindow(SpoutPlayer player) {
		if (!inBuildWorldWindows.containsKey(player)) { inBuildWorldWindows.put(player, new InBuildWorldWindow(player, plugin)); }
		inBuildWorldWindows.get(player).open();		
	}
	
	public void showWorldPropertiesWindow(SpoutPlayer player) {
		if (!worldPropertiesWindows.containsKey(player)) { worldPropertiesWindows.put(player, new WorldPropertiesWindow(player, plugin)); }
		worldPropertiesWindows.get(player).open();		
	}
	
	public void showInDreamWorldWindowWindow(SpoutPlayer player) {
		if (!inDreamWorldWindows.containsKey(player)) { inDreamWorldWindows.put(player, new InDreamWorldWindow(player, plugin)); }
		inDreamWorldWindows.get(player).open();		
	}
	
	public void showWorldBlacklistWindow(SpoutPlayer player) {
		if (!blacklistWindows.containsKey(player)) { blacklistWindows.put(player, new BlacklistWindow(player, plugin)); }
		blacklistWindows.get(player).open();		
	}
	
	public void onPlayerDisconnect(Player player) {
		MainWindow mw = mainWindows.get((SpoutPlayer)player);
		if(mw != null)
			mw.closeWindow();
		
		InBuildWorldWindow x = inBuildWorldWindows.get((SpoutPlayer)player);
		if(x != null)
			x.closeWindow();
		
		WorldPropertiesWindow y = worldPropertiesWindows.get((SpoutPlayer)player);
		if(y != null)
			y.closeWindow();
		
		BlacklistWindow z = blacklistWindows.get((SpoutPlayer)player);
		if(z != null)
			z.closeWindow();
		
		InDreamWorldWindow a = inDreamWorldWindows.get((SpoutPlayer)player);
		if(a != null)
			a.closeWindow();
	}
}
