package net.moc.MOCKiosk;

import net.moc.MOCKiosk.GUI.BrowserWindow;
import net.moc.MOCKiosk.GUI.DisplayWindow;
import net.moc.MOCKiosk.GUI.ManagerWindow;
import net.moc.MOCKiosk.GUI.Widgets.MOCComboBoxSelectionEvent;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCKioskEventListener implements Listener{
	//=====================================================
	private MOCKiosk plugin;
	public MOCKioskEventListener(MOCKiosk plugin) {
		this.plugin = plugin;
	}
	//=====================================================
	
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerQuitEvent event) {
		//Clean up all open windows
		if (this.plugin.getGui().displayWindows.containsKey(event.getPlayer())) { this.plugin.getGui().displayWindows.remove(event.getPlayer()); }
		if (this.plugin.getGui().managerWindows.containsKey(event.getPlayer())) { this.plugin.getGui().managerWindows.remove(event.getPlayer()); }
		if (this.plugin.getGui().browserWindows.containsKey(event.getPlayer())) { this.plugin.getGui().browserWindows.remove(event.getPlayer()); }
		if (this.plugin.getLatestKioskShown().containsKey(event.getPlayer())) { this.plugin.getLatestKioskShown().remove(event.getPlayer()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerKickEvent event) {
		//Clean up all open windows
		if (this.plugin.getGui().displayWindows.containsKey(event.getPlayer())) { this.plugin.getGui().displayWindows.remove(event.getPlayer()); }
		if (this.plugin.getGui().managerWindows.containsKey(event.getPlayer())) { this.plugin.getGui().managerWindows.remove(event.getPlayer()); }
		if (this.plugin.getGui().browserWindows.containsKey(event.getPlayer())) { this.plugin.getGui().browserWindows.remove(event.getPlayer()); }
		if (this.plugin.getLatestKioskShown().containsKey(event.getPlayer())) { this.plugin.getLatestKioskShown().remove(event.getPlayer()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof DisplayWindow) { ((DisplayWindow) screen).onClick(event.getButton()); }
		if(screen instanceof ManagerWindow) { ((ManagerWindow) screen).onClick(event.getButton()); }
		if(screen instanceof BrowserWindow) { ((BrowserWindow) screen).onClick(event.getButton()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(MOCComboBoxSelectionEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof BrowserWindow) { ((BrowserWindow) screen).onSelection(event.getListWidget()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(BlockPlaceEvent event) {
		//Check if it was a Kiosk block
		if (((SpoutBlock) event.getBlock()).getCustomBlock() instanceof MOCKioskBlock) {
			Block block = event.getBlock();
			
			//Check if dreamcrafter instance
			if (this.plugin.getDreamCrafter() != null) {
				if (this.plugin.getDreamCrafter().IsPlayerInInstance(event.getPlayer())) {
					//Player is in instance cancel
					event.setCancelled(true);
					
					this.plugin.getLog().sendPlayerWarn(event.getPlayer(), "To add a kiosk to an instance you have to be in the Origin world.");
					
					return;
					
				}
				
			}
			
			//Open create GUI
			this.plugin.getGui().displayManagerWindowGUI((SpoutPlayer) event.getPlayer(), block);
			
		}
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		
		//Check if it was a Kiosk block
		if (((SpoutBlock) event.getClickedBlock()).getCustomBlock() instanceof MOCKioskBlock) {
			MOCKioskKiosk kiosk = getKiosk(event.getClickedBlock().getLocation());
			if (kiosk == null) return;
			
			if (kiosk.getClicktext().length() > 0) this.plugin.getLog().sendPlayerNormal(event.getPlayer(), kiosk.getClicktext());
			if (kiosk.getClickurl().length() > 0); //TODO
			
			//Cancel the event
			event.setCancelled(true);
			
		}
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerMoveEvent event) {
		MOCKioskKiosk kiosk = getNearKiosk(event.getPlayer().getLocation());
		if (kiosk == null) {
			if (this.plugin.getLatestKioskShown().containsKey(event.getPlayer())) {
				this.plugin.getLatestKioskShown().remove(event.getPlayer());
			}
			
			return;
			
		}
		
		if (this.plugin.getLatestKioskShown().get(event.getPlayer()) == kiosk) return;
		
		this.plugin.getLatestKioskShown().put(event.getPlayer(), kiosk);
		
		if (kiosk.getNeartext().length() > 0) this.plugin.getLog().sendPlayerNormal(event.getPlayer(), kiosk.getNeartext());
		if (kiosk.getNearurl().length() > 0); //TODO
		
		if (kiosk.getPopup_deck_id() != -1) {
			this.plugin.getGui().displayDisplayWindowGUI((SpoutPlayer) event.getPlayer(), kiosk);
			
		}
		
	}
	//##############################################################################################
	
	private MOCKioskKiosk getNearKiosk(Location location) {
		int radius = this.plugin.getConfiguration().getShoutRadius();
		
		for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
			if (kiosk.getLocation().getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
				if (Math.abs(kiosk.getLocation().getBlockX() - location.getBlockX()) <= radius &&
						Math.abs(kiosk.getLocation().getBlockY() - location.getBlockY()) <= radius  &&
						Math.abs(kiosk.getLocation().getBlockZ() - location.getBlockZ()) <= radius ) {
					return kiosk;
				}

			}

		}
		return null;
	}
	
	private MOCKioskKiosk getKiosk(Location location) {
		for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
			if (kiosk.getLocation().getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
				if (kiosk.getLocation().getBlockX() == location.getBlockX() &&
						kiosk.getLocation().getBlockY() == location.getBlockY()  &&
						kiosk.getLocation().getBlockZ() == location.getBlockZ()) {
					return kiosk;

				}

			}

		}
		
		return null;
	}
	//##############################################################################################
}
