package net.moc.MOC3DImporter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MOC3DImporterEventListener implements Listener {
	//=====================================================
	private MOC3DImporter plugin;
	public MOC3DImporterEventListener(MOC3DImporter plugin) {
		this.plugin = plugin;
	}
	//=====================================================
	
	//##############################################################################################
	@EventHandler
	public void onEvent(MOC3DImporterMatrixFinishedEvent event) {
		//Add finished matrix to converter
		this.plugin.getConverter().addToPlayerMatrix(event.getPlayer().getName(), event.getLocation(), event.getMatrix());
		
		//Message the player
		this.plugin.getLog().sendPlayerNormal(event.getPlayer(), "Matrix generation is finished. Adding to the build queue.");
		
		//Queue builder
		this.plugin.getBuilder().build(event.getPlayer().getName(), event.isUseTexture());
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerQuitEvent event) {
		this.plugin.getConverter().removePlayer(event.getPlayer().getName());
		
	}
	
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerKickEvent event) {
		this.plugin.getConverter().removePlayer(event.getPlayer().getName());
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(EntityDamageEvent event) {
		this.plugin.getBuilder().continueQueue();
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerMoveEvent event) {
		this.plugin.getBuilder().continueQueue();
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(BlockPhysicsEvent event) {
		this.plugin.getBuilder().continueQueue();
		
	}
	//##############################################################################################
}
