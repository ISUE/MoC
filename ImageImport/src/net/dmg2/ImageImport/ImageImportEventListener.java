package net.dmg2.ImageImport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ImageImportEventListener implements Listener {
	//=====================================================
	private ImageImport plugin;
	public ImageImportEventListener(ImageImport plugin) {
		this.plugin = plugin;
	}
	//=====================================================
	
	//##############################################################################################
	@EventHandler
	public void onEvent(EntityDamageEvent event) { this.plugin.getImageGenerator().processQueue(); }
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerMoveEvent event) { this.plugin.getImageGenerator().processQueue(); }
	//##############################################################################################
	@EventHandler
	public void onEvent(BlockPhysicsEvent event) { this.plugin.getImageGenerator().processQueue(); }
	//##############################################################################################

}
