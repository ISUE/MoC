package moc.MOCFizziks;

import org.bukkit.Bukkit;

public class MOCFizziksQueue implements Runnable {
	private long queueDelta = 100;

	public void run() {
		while (true) {
			try { Thread.sleep(queueDelta); } catch (InterruptedException e) { e.printStackTrace(); }
			
			//Throw next event
		    Bukkit.getServer().getPluginManager().callEvent(new MOCFizziksQueueEvent("Next!"));
			
		}
		
	}

}
