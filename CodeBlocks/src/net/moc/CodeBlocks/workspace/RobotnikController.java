package net.moc.CodeBlocks.workspace;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.workspace.Robotnik.Direction;
import org.bukkit.Location;

public class RobotnikController {
	private CodeBlocks plugin;
	private String playerName;
	private Robotnik robotnik;
	private String functionName = "";
	private Location debugBase = null;
	private boolean isExecuting;
	private int speed = 1000;
	private int id = -1;
	private String name = "Robot Name";
	
	public RobotnikController(CodeBlocks plugin, String playerName, Location location, Direction direction, String currentLevels) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.robotnik = new Robotnik(this.plugin, this.playerName, location, direction, currentLevels);
		this.isExecuting = false;
		
	}
	
	public void setFunctionName(String functionName) { this.functionName = functionName; }
	public String getFunctionName() { return this.functionName; }

	public Robotnik getRobotnik() { return robotnik; }
	public void setRobotnik(Robotnik robotnik) { this.robotnik = robotnik; }

	public boolean isExecuting() { return this.isExecuting; }
	public void setExecuting(boolean value) { this.isExecuting = value; }
	public void toggleExecuting() { this.isExecuting = !this.isExecuting; }
	
	public int getSpeed() { return this.speed; }
	public void setSpeed(int value) { this.speed = value; }

	public void setId(int id) { this.id = id; }
	public int getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public void setDebugBase(Location location) { debugBase = location; }
	public Location getDebugBase() { return debugBase; }

}
