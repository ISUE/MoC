package moc.DreamCrafter;

import moc.DreamCrafter.gui.BlacklistWindow;
import moc.DreamCrafter.gui.InBuildWorldWindow;
import moc.DreamCrafter.gui.InDreamWorldWindow;
import moc.DreamCrafter.gui.MainWindow;
import moc.DreamCrafter.gui.WorldPropertiesWindow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Screen;

public class MOCDreamCrafterEventListener implements Listener {
	MOCDreamCrafter plugin;
	public MOCDreamCrafterEventListener(MOCDreamCrafter plugin) { this.plugin = plugin; }
	
	//-----------------------------------------------------------------------------	
	@EventHandler 
	public void onEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		plugin.getPersistentDataHandler().onPlayerJoinServer(player);
		plugin.getDreamCleanupHandler().OnPlayerJoinWorld(player);
	}
	//-----------------------------------------------------------------------------	
	@EventHandler
	public void onEvent(PlayerQuitEvent e) { 
		playerCleanup(e.getPlayer());
		
	}
	//-----------------------------------------------------------------------------	
	@EventHandler
	public void onEvent(PlayerKickEvent e) {
		playerCleanup(e.getPlayer());
	}
	//-----------------------------------------------------------------------------	
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		Screen screen = event.getScreen();
		
		if(screen instanceof MainWindow) ((MainWindow) screen).onClick(event.getButton());	
		if(screen instanceof WorldPropertiesWindow) ((WorldPropertiesWindow) screen).onClick(event.getButton());	
		if(screen instanceof InBuildWorldWindow) ((InBuildWorldWindow) screen).onClick(event.getButton());
		if(screen instanceof BlacklistWindow) ((BlacklistWindow) screen).onClick(event.getButton());
		if(screen instanceof InDreamWorldWindow) ((InDreamWorldWindow) screen).onClick(event.getButton());
		
	}
	//-----------------------------------------------------------------------------	
	@EventHandler
	public void onEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();
		
		if(plugin.getWorldHandler().IsPlayerInDreamWorld(player) && plugin.getWorldHandler().isBlockEndPoint(clickedBlock))
			player.performCommand("dc leave");
	}
	//-----------------------------------------------------------------------------	
	private void playerCleanup(Player player) {
		if(plugin.getWorldHandler().IsPlayerInDreamWorld(player) || plugin.getWorldHandler().IsPlayerInBuildWorld(player)){
			//player.performCommand("leave");
			plugin.getDreamCleanupHandler().OnPlayerLeaveWorld(player);
			plugin.getGui().onPlayerDisconnect(player);
		}
	}
}
