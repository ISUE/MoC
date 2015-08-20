package net.dmg2.RegenBlock.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import net.dmg2.RegenBlock.RegenBlock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegenBlockEventListenerPlayer implements  Listener {
	//============================================================
	private RegenBlock plugin;
	private HashMap<String, Location> playerSelectionLeft = new HashMap<String, Location>(); public HashMap<String, Location> getPlayerSelectionLeft() { return this.playerSelectionLeft; }
	private HashMap<String, Location> playerSelectionRight = new HashMap<String, Location>(); public HashMap<String, Location> getPlayerSelectionRight() { return this.playerSelectionRight; }
	private ArrayList<String> playerSelectionStatus = new ArrayList<String>(); public ArrayList<String> getPlayerSelectionStatus() { return this.playerSelectionStatus; }
	private ArrayList<String> playerEditStatus = new ArrayList<String>(); public ArrayList<String> getPlayerEditStatus() { return this.playerEditStatus; }
	public RegenBlockEventListenerPlayer(RegenBlock plugin) { this.plugin = plugin; }
	//============================================================
	
	//#######################################################################################################################
	@EventHandler
	public void onEvent(PlayerInteractEvent event) {		
		if (event.isCancelled()) return; //=======================
		
		//If player is not in the selection mode return
		if (this.playerSelectionStatus.contains(event.getPlayer().getName()) == false) return;

		//Grab variables from the event
		Player player = event.getPlayer();
		Location loc = event.getClickedBlock().getLocation();
		Action action = event.getAction();
		
		//Check which event was performed
		if (action == Action.LEFT_CLICK_BLOCK) {
			//Save selection
			Location locOld = this.playerSelectionLeft.get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				this.playerSelectionLeft.put(player.getName(), loc);
				//Message the player
				this.plugin.getLog().sendPlayerNormal(player, "Left Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
		} else if (action == Action.RIGHT_CLICK_BLOCK) {
			//Save selection
			Location locOld = this.playerSelectionRight.get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				this.playerSelectionRight.put(player.getName(), loc);
				//Message the player
				this.plugin.getLog().sendPlayerNormal(player, "Right Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
		}
		
		event.setCancelled(true);
		
	}
	//#######################################################################################################################$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	@EventHandler
	public void onEvent(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		playerSelectionLeft.remove(player.getName());
		playerSelectionRight.remove(player.getName());
		playerSelectionStatus.remove(player.getName());
		playerEditStatus.remove(player.getName());
	}
	@EventHandler
	public void onEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		playerSelectionLeft.remove(player.getName());
		playerSelectionRight.remove(player.getName());
		playerSelectionStatus.remove(player.getName());
		playerEditStatus.remove(player.getName());
	}
	@EventHandler
	public void onEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		playerSelectionLeft.remove(player.getName());
		playerSelectionRight.remove(player.getName());
		playerSelectionStatus.remove(player.getName());
		playerEditStatus.remove(player.getName());
	}
	//#######################################################################################################################$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
}
