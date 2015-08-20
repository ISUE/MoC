package net.dmg2.GravitySheep.events;

import java.util.ArrayList;

import net.dmg2.GravitySheep.GravitySheep;
import net.dmg2.GravitySheep.api.GravitySheepRegion;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GravitySheepPlayerListener implements Listener {
	
	//============================================================
	private GravitySheep plugin;
	public GravitySheepPlayerListener(GravitySheep instance) {
		this.plugin = instance;
	}
	//============================================================

	//#######################################################################################################################
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		
		//Process lever clicks in the region
		togglePower(event.getClickedBlock());
		
		
		//If player is not in the selection mode return
		if (!this.plugin.getPlayerSelectionStatus().contains(event.getPlayer().getName())) return;

		//Grab variables from the event
		Player player = event.getPlayer();
		Location loc = event.getClickedBlock().getLocation();
		Action action = event.getAction();
		
		//Check which event was performed
		if (action == Action.LEFT_CLICK_BLOCK) {
			//Save selection
			Location locOld = plugin.getPlayerSelectionLeft().get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				plugin.getPlayerSelectionLeft().put(player.getName(), loc);
				//Message the player
				player.sendMessage(ChatColor.AQUA+"Selected " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}

		}
		
		event.setCancelled(true);
	}
	
	//#######################################################################################################################
	@EventHandler
	public void onPlayerChangedWorld (PlayerChangedWorldEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event) {
		this.cleanUpLists(event.getPlayer());
	}
	public void cleanUpLists(Player player) {
		//Remove player from all lists
		this.plugin.getPlayerSelectionLeft().remove(player.getName());
		//this.plugin.playerSelectionRight.remove(player.getName());
		this.plugin.getPlayerSelectionStatus().remove(player.getName());
	}

	//========================================================================================================
	// Processes level clicks in the region
	public void togglePower(Block block) {
		if ( !(block.getType() == Material.LEVER ||
				block.getType() == Material.STONE_BUTTON ||
				block.getType() == Material.WOOD_BUTTON ||
				block.getType() == Material.STONE_PLATE ||
				block.getType() == Material.WOOD_PLATE) ) return;

		//Get region name if any
		ArrayList<GravitySheepRegion> regions = plugin.getAPI().getWorld(block.getWorld()).getSwitchRegions(block);

		if (regions == null) return;

		for (GravitySheepRegion region : regions) triggerRegion(region);
		
	}
	
	private void triggerRegion(GravitySheepRegion region) {
		EntityType entityType = region.getEntityType();

		if (entityType.isSpawnable()) {
			Entity cannonFodder = region.getWorld().spawn(region.getBase(), entityType.getEntityClass());

			if (cannonFodder instanceof Sheep) { ((Sheep)cannonFodder).setColor(DyeColor.values()[this.plugin.getRandom().nextInt(DyeColor.values().length)]); }

			cannonFodder.setVelocity(region.getVelocity());

		} else if (entityType == EntityType.FALLING_BLOCK) {
			//Falling block
			FallingBlock fb = region.getWorld().spawnFallingBlock(region.getBase(), region.getFallingBlockID(), region.getFallingBlockData());
			fb.setVelocity(region.getVelocity());

		}
		
	}

}