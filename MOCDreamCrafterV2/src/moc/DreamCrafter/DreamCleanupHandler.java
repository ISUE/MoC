package moc.DreamCrafter;

import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 * Keeps track of the dreams that are empty because of player disconnections.
 * If the player doesn't reconnect after a certain number of minutes, the worlds are cleaned up
 *
 */
public class DreamCleanupHandler {

	private MOCDreamCrafter plugin;										// Pointer to plugin
	private ArrayList<WaitingDreamKillerTaskInfo> waitingDreams;		// The world names of the dreams that are empty
	
	private final int ticksPerSecond = 20;
	private final int minutesToWait = 5;
	
	public DreamCleanupHandler(MOCDreamCrafter plugin) {
		this.plugin = plugin;
		waitingDreams = new ArrayList<WaitingDreamKillerTaskInfo>();
	}
	
	/**
	 * Schedules (or re-schedules) a task to delete the world
	 * @param dreamWorldName
	 */
	public void OnPlayerLeaveWorld(Player player) {	
		if(plugin.getWorldHandler().IsPlayerInDreamWorld(player)) {
			String dreamWorldName = player.getWorld().getName();
			UnScheduleWorldDeletion(dreamWorldName);
			ScheduleWorldDeletion(dreamWorldName, player);
			plugin.getLog().info(player.getName() + " left world " + dreamWorldName + ". Scheduling world deletion.");
		}
	}
	
	/**
	 * If a player joins a dream world that is scheduled for deletion, unschedule it
	 * @param dreamWorldName
	 */
	public void OnPlayerJoinWorld(Player player) {
		if(plugin.getWorldHandler().IsPlayerInDreamWorld(player)) {
			String dreamWorldName = player.getWorld().getName();
			UnScheduleWorldDeletion(dreamWorldName);
			plugin.getLog().info(player.getName() + " joined world " + dreamWorldName + ". Unscheduling world deletion.");
		}
	}
	
	/**
	 * Schedules the dream world to be deleted
	 * @param dreamWorldName
	 */
	private void ScheduleWorldDeletion(String dreamWorldName, Player player) {
		// Schedule task
		int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(
				plugin, 
				new CleanupDreamTask(player), 
				minutesToWait * ticksPerSecond * 60);

		// Keep track of the world in case the player returns or leaves again
		WaitingDreamKillerTaskInfo w = new WaitingDreamKillerTaskInfo();
			w.DreamWorldName = dreamWorldName;
			w.TaskId = taskId;
		waitingDreams.add(w);
	}
	
	/**
	 * Unschedules deletion of the dream world if it is currently scheduled
	 * @param dreamWorldName
	 */
	private void UnScheduleWorldDeletion(String dreamWorldName) {
		for(int i = 0; i < waitingDreams.size(); i++) {
			WaitingDreamKillerTaskInfo w = waitingDreams.get(i);
			if(w.DreamWorldName.equals(dreamWorldName))
				plugin.getServer().getScheduler().cancelTask(w.TaskId);
		}
	}
	
	/**
	 * Structure to keep track of a Dream scheduled for deletion
	 */
	private class WaitingDreamKillerTaskInfo {
		public String DreamWorldName;
		public int TaskId;
	}
	
	/**
	 * Deletes world data for specified world
	 */
	private class CleanupDreamTask implements Runnable {
		private Player player;
		
		public CleanupDreamTask(Player player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			plugin.getLog().info(player.getName() + " abandoned dream. Cleaning up dream world.");
			player.performCommand("dc leave");
		}
	}
}



