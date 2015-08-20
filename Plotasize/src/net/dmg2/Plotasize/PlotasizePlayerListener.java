package net.dmg2.Plotasize;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlotasizePlayerListener implements Listener {

	//============================================================
	private Plotasize plugin;
	private long lastPersentUpdate = System.currentTimeMillis();
	private long lastBluePrintRun = System.currentTimeMillis();
	private boolean busy = false;
	public PlotasizePlayerListener(Plotasize plugin) {
		this.plugin = plugin;
	}
	//============================================================
	
	//#######################################################################################################################
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		this.processBluePrint();
	}
	//#######################################################################################################################
	@EventHandler
	public void onEvent(BlockPhysicsEvent event) {
		this.processBluePrint();
	}
	@EventHandler
	public void onEvent(EntityDamageEvent event) {
		this.processBluePrint();
	}
	@EventHandler
	public void onEvent(EntityDeathEvent event) {
		this.processBluePrint();
	}
	//#######################################################################################################################
	
	private void processBluePrint() {
		if (busy) return;
		busy = true;
		
		if (this.plugin.blueprints.isEmpty()) { busy = false; return; }
		if (System.currentTimeMillis() - this.lastBluePrintRun < 20000) { busy = false; return; }
		
		PlotasizeBluePrint blueprint = this.plugin.blueprints.get(0);
		
		for (int i = 0 ; i < 200000 ; i++) {
			if (!blueprint.setNextBlock()) {
				//Blue print is done, discard it and stop the loop
				this.plugin.blueprints.remove(0);
				break;
			}
			
		}
		
		if (System.currentTimeMillis() - this.lastPersentUpdate > 60000) {
			this.lastPersentUpdate = System.currentTimeMillis();
			this.plugin.log.sendPlayerNormal(blueprint.getPlayer(), 100 - blueprint.getPercentDone() + "% done.");
			this.plugin.log.info("Plot generation for " + blueprint.getPlayer().getName() + " " + blueprint.getPercentDone() + "% done.");
			
		}
		
		busy = false;
		
	}
	
}
