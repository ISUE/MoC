package net.dmg2.rTPS;

import org.bukkit.Bukkit;

public class rTPSQueue implements Runnable {
	private rTPS plugin;
	private long queueDelta;
	
	public rTPSQueue(rTPS plugin) {
		this.plugin = plugin;
		this.queueDelta = this.plugin.getConfiguration().getQueueDelay();
		
	}
	
	public void run() {
		while(true) {
			//Sleep
			try { Thread.sleep(queueDelta); } catch (InterruptedException e) { e.printStackTrace(); }
			
			Bukkit.getServer().getPluginManager().callEvent(new rTPSQueueEvent());
			
		}
		
	}

}
