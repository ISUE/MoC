package net.dmg2.rTPS;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class rTPSEventListener implements  Listener {
	private rTPS plugin;
	private long timerTicks;
	
	public rTPSEventListener(rTPS plugin) { this.plugin = plugin; this.timerTicks = this.plugin.getConfiguration().getTimerTicks(); }

	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	@EventHandler
	public void onEvent(rTPSQueueEvent event) {
		//Record start time
		final long startTime = System.currentTimeMillis();
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			   public void run() { Bukkit.getServer().getPluginManager().callEvent(new rTPSTimerEvent(startTime)); }
		}, this.timerTicks);
		
	}
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	@EventHandler
	public void onEvent(rTPSTimerEvent event) {
		//Record end time
		long endTime = System.currentTimeMillis();
		
		long delta = endTime - event.getStartTime();
		long ticks = delta / 50;
		
		long tps = this.timerTicks / (delta / 1000);
		
		this.plugin.getLog().info("Ticks Counted/Perfect: " + this.timerTicks + "/" + ticks + " TPS: " + tps + " Delta in ms: " + delta);
		
	}

}
