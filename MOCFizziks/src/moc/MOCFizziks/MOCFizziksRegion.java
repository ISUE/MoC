package moc.MOCFizziks;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class MOCFizziksRegion {
	private String name;
	private Location lowerLeft, upperRight;
	private Vector velocity;
	private Vector acceleration;
	private Boolean isOn;
	private Boolean usesPower;
	private ArrayList<Location> switches;
	private ArrayList<Location> signs;
	
	
	//========================================================
	public MOCFizziksRegion(String name, Location loc1, Location loc2)	{
		this.name = name;
		isOn = true;
		usesPower = true;
		
		velocity = new Vector();
		acceleration = new Vector();
		
		switches = new ArrayList<Location>();
		signs = new ArrayList<Location>();
		
		setLocations(loc1,loc2);

	}
	
	public MOCFizziksRegion(String regionName, Location regionLowerLeft, Location regionUpperRight, Vector regionVelocity, Vector regionAcceleration,
			boolean regionIsOn, boolean regionUsesPower, ArrayList<Location> regionSwitches, ArrayList<Location> regionSigns) {
		
		this.name = regionName;
		isOn = regionIsOn;
		usesPower = regionUsesPower;
		
		velocity = regionVelocity;
		acceleration = regionAcceleration;
		
		switches = regionSwitches;
		signs = regionSigns;
		
		setLocations(regionLowerLeft,regionUpperRight);

	}

	//========================================================
	public String getName() { return this.name; }
	
	//========================================================
	public Boolean isOn() { return isOn; }
	public void setOn(boolean value) { isOn = value; }
	
	public Boolean usesPower() { return usesPower; }
	public void setUsesPower(boolean value) { usesPower = value; }
	
	//========================================================
	public Vector getVelocity() { return velocity; }
	public Vector getAcceleration() { return acceleration; }
	
	//========================================================
	public void setVelocity(Vector velocity) { this.velocity = velocity; }
	public void setVelocity(double x, double y, double z) { velocity.setX(x); velocity.setY(y); velocity.setZ(z); }

	//========================================================
	public void setAcceleration(Vector acceleration) { this.acceleration = acceleration; }
	public void setAcceleration(double x, double y, double z) { acceleration.setX(x); acceleration.setY(y); acceleration.setZ(z); }
	
	//========================================================
	public World getWorld() { return lowerLeft.getWorld(); }
	public Location getLowerLeft() { return this.lowerLeft; }
	public Location getUpperRight() { return this.upperRight; }
	
	//========================================================
	public void setLocations(Location loc1, Location loc2) {
		lowerLeft = new Location(loc1.getWorld(), 0, 0, 0);
		upperRight = new Location(loc1.getWorld(), 0, 0, 0);
		
		if (loc1.getBlockX() < loc2.getBlockX()) { this.lowerLeft.setX(loc1.getX()); this.upperRight.setX(loc2.getX()); }
		else { this.lowerLeft.setX(loc2.getX()); this.upperRight.setX(loc1.getX()); }
		
		if (loc1.getBlockY() < loc2.getBlockY()) { this.lowerLeft.setY(loc1.getY()); this.upperRight.setY(loc2.getY()); }
		else { this.lowerLeft.setY(loc2.getY()); this.upperRight.setY(loc1.getY()); }
		
		if (loc1.getBlockZ() < loc2.getBlockZ()) { this.lowerLeft.setZ(loc1.getZ()); this.upperRight.setZ(loc2.getZ()); }
		else { this.lowerLeft.setZ(loc2.getZ()); this.upperRight.setZ(loc1.getZ()); }
		
	}
	
	//========================================================
	public ArrayList<Location> getSwitches() { return switches; }
	public void removeSwitches() { switches.clear(); }
	public boolean removeSwitch(Block block) {
		Location rs = null;
		
		//Try to find switch
		for (Location s : switches) if (s.distance(block.getLocation()) < 1) { rs = s; break; }
		
		if (rs != null) { switches.remove(rs); return true; }
		
		return false;
		
	}
	
	public void addSwitch(Block block) {
		//If already a switch return
		if (isSwitch(block)) return;
		
		//Add new switch
		switches.add(block.getLocation());
		
	}
	
	public boolean isSwitch(Block block) {
		for (Location s : switches) if (s.distance(block.getLocation()) < 1) return true;
		
		return false;
		
	}
	
	//========================================================
	public ArrayList<Location> getSigns() { return signs; }
	public void removeSigns() { signs.clear(); }
	public boolean removeSign(Block block) {
		Location rs = null;
		
		//Try to find sign
		for (Location s : signs) if (s.distance(block.getLocation()) < 1) { rs = s; break; }
		
		if (rs != null) { signs.remove(rs); return true; }
		
		return false;
		
	}
	
	public void addSign(Block block) {
		//If already sign - return
		if (isSign(block)) return;
		
		//Add new sign
		signs.add(block.getLocation());
		
	}
	
	public boolean isSign(Block block) {
		for (Location s : signs) if (s.distance(block.getLocation()) < 1) return true;
		
		return false;
		
	}
	
}
