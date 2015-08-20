package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import net.moc.CodeBlocks.CodeBlocks;
import org.bukkit.block.Block;

public class PlayerWorkspace {
	//==================================================
	private CodeBlocks plugin;
	private String playerName;
	private VirtualMachine virtualMachine;
	private RobotHistory robotHistory;
	private ArrayList<RobotnikController> robotniks;
	private ArrayList<Function> functions;
	private HashMap<String, RobotnikController> debugBaseQueue; public HashMap<String, RobotnikController> getDebugBaseQueue() { return debugBaseQueue; }
	//==================================================
	
	//==================================================
	public PlayerWorkspace(CodeBlocks plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.virtualMachine = new VirtualMachine(this.plugin, this.playerName);
		debugBaseQueue = new HashMap<String, RobotnikController>();
		
		load();
		
	}
	//==================================================
	
	public void load() {
		this.robotniks = this.plugin.getSQL().getRobotniks(playerName);
		this.functions = this.plugin.getSQL().getFunctions(playerName);
		this.robotHistory = this.plugin.getSQL().getRobotHistory(playerName);
		
	}
	
	public VirtualMachine getVirtualMachine() { return this.virtualMachine; }
	public ArrayList<RobotnikController> getRobotniks() { return robotniks; }
	public ArrayList<Function> getFunctions() { return functions; }
	
	public void addFunction(Function function) { functions.add(function); }
	public void addRobotnikController(RobotnikController robotnikController) { robotniks.add(robotnikController); }
	
	public Function getFunction(String name) {
		if (functions.isEmpty()) return null;
		for (Function function : functions) if (function.getName().equalsIgnoreCase(name)) return function;
		return null;
		
	}

	public RobotnikController getRobotnik(Block block) {
		for (RobotnikController  rc : robotniks) {
			if (block.getWorld().getName().equalsIgnoreCase(rc.getRobotnik().getLocation().getWorld().getName()))
				if (rc.getRobotnik().getLocation().distance(block.getLocation()) < 1) return rc;
			
		}
		
		return null;
		
	}

	public RobotnikController getRobotnik(Block block, int radius) {
		for (RobotnikController  rc : robotniks) {
			if (block.getWorld().getName().equalsIgnoreCase(rc.getRobotnik().getLocation().getWorld().getName()))
				if (rc.getRobotnik().getLocation().distance(block.getLocation()) < radius) return rc;
			
		}
		
		return null;
		
	}

	public RobotnikController getRobotnik(int robotId) {
		for (RobotnikController  rc : robotniks) if (rc.getId() == robotId) return rc;
		
		return null;
		
	}

	public RobotHistory getAchievements() { return robotHistory; }

	public void removeRobotnik(RobotnikController rc) { robotniks.remove(rc); }

}
