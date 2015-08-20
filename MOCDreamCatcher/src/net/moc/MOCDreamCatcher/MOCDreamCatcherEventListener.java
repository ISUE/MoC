package net.moc.MOCDreamCatcher;

import java.util.HashMap;
import net.moc.MOCDreamCatcher.Data.DreamPlayer;
import net.moc.MOCDreamCatcher.Data.Dreamer;
import net.moc.MOCDreamCatcher.Data.DreamPlayer.PlayerState;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MOCDreamCatcherEventListener implements Listener {
	private MOCDreamCatcher plugin;
	private HashMap<Player, Dreamer> dreamers = new HashMap<Player, Dreamer>();
	
	public MOCDreamCatcherEventListener(MOCDreamCatcher plugin) { this.plugin = plugin; }
	
	public void addDreamer(Player player, Dreamer dreamer) { dreamers.put(player, dreamer); }
	public void removeDreamer(Player player) { dreamers.remove(player); }
	
	//-----------------------------------------------------------------------------	
	@EventHandler 
	public void onEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		DreamPlayer dp = plugin.getDreamNet().addPlayer(player.getName());
		
		//Finish waking up if needed
		if (dp.getState() == PlayerState.WAKING) {
			//Teleport player back to the old location
			if (dp.getWakeLocation() != null) player.teleport(dp.getWakeLocation());
			
			//Update inventory
			if (dp.getWakeInventory() != null) player.getInventory().setContents(dp.getWakeInventory().getContents());
			
			//Update saved statistics
			dp.setWakeInformation(null, null);
			dp.setState(PlayerState.AWAKE);
			
			//Update configuration file
			plugin.getConfiguration().setDreamPlayer(dp);
			
		}
		
	}
	
	//-----------------------------------------------------------------------------	
	@EventHandler 
	public void onEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		if (dreamers.containsKey(player)) {
			Dreamer dreamer = dreamers.get(player);
			
			if (dreamer.getDreamPlayer().getDream().isFinished()) return;
			
			Location loc1 = dreamer.getEndLocation(), loc2 = player.getLocation();
			if (loc1 == null || loc2 == null) return;
			
			if (loc1.getBlockX() - loc2.getBlockX() < 2 && loc1.getBlockX() - loc2.getBlockX() > -2 &&
				loc1.getBlockY() - loc2.getBlockY() < 2 && loc1.getBlockY() - loc2.getBlockY() > -2 &&
				loc1.getBlockZ() - loc2.getBlockZ() < 2 && loc1.getBlockZ() - loc2.getBlockZ() > -2) {
				
				dreamer.getDreamPlayer().getDream().setFinished(true);
				
				//Record completion date
				plugin.getConfiguration().recordThoughtCompletion(dreamer.getThought().getAuthor(), dreamer.getThought().getName(), player.getName());
				
				//Display survey
				if (plugin.isSpoutAvailable()) plugin.getGUI().displaySurveyDream(player);
				
			}
			
		}
		
	}

}
