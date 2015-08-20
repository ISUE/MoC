package net.moc.CodeBlocks.workspace;

import java.util.HashMap;
import org.bukkit.Bukkit;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.workspace.events.VirtualMachineTickEvent;

public class VirtualMachine {
	//==========================================================
	private CodeBlocks plugin;
	private HashMap<RobotnikController, Process> processes;
	private String playerName;
	private long tickDelta = 1;
	//==========================================================
	
	//==========================================================
	public VirtualMachine(CodeBlocks plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
		processes = new HashMap<RobotnikController, Process>();
		
	}
	//==========================================================
	
	public Process getProcess(RobotnikController rc) { return processes.get(rc); }
	
	public boolean run(RobotnikController robotnikController) {
		//Check if robotnik is already running
		if (robotnikController.isExecuting()) return false;
		
		plugin.getLog().sendPlayerNormal(playerName, "Running " + robotnikController.getFunctionName());
		
		//Create new process if needed
		if (!processes.containsKey(robotnikController)) processes.put(robotnikController, new Process(plugin, robotnikController, playerName));
		
		processes.get(robotnikController).run();
		
		scheduleNextTick();
		
		return true;
		
	}
	
	public boolean stop(RobotnikController robotnik) {
		//Check if robotnik is running
		if (!processes.containsKey(robotnik)) return false;
		
		processes.get(robotnik).stop();
		
		return true;
		
	}

	public boolean pause(RobotnikController robotnik) {
		//Check if robotnik is running
		if (!robotnik.isExecuting() || !processes.containsKey(robotnik)) return false;
		
		processes.get(robotnik).pause();
		
		return true;
		
	}
	
	public boolean resume(RobotnikController robotnik) {
		//Check if robotnik is running
		if (!processes.containsKey(robotnik)) return false;
		
		processes.get(robotnik).resume();
		
		scheduleNextTick();
		
		return true;
		
	}
	
	//==========================================================================
	private void scheduleNextTick() {
		final String playerName = this.playerName;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(new VirtualMachineTickEvent(playerName));
				
			}
			
		}, tickDelta);
		
	}
	//==========================================================================

	public void tick() {
		//Check if there is anything to do
		if (this.processes.isEmpty()) return;
		
		//Step in all processes
		//TODO - add a limit of how many should be executed at max. Select randomly
		for (Process process : this.processes.values()) {
			process.step();
			
		}
		
		//Schedule next tick if at least one robotnik is still executing
		for (RobotnikController robotnik : this.processes.keySet()) {
			if (robotnik.isExecuting()) { scheduleNextTick(); return; }
			
		}
		
	}

	public void removeProcess(Process process) { processes.remove(process); }

}
