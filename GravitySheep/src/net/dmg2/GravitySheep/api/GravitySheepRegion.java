package net.dmg2.GravitySheep.api;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class GravitySheepRegion {
	private String name;
	private Location base;
	private Vector velocity;
	private EntityType entityType;
	private int typeId;
	private byte data;
	private ArrayList<Location> switches;
	private ArrayList<Location> signs;
	
	public GravitySheepRegion(String name, Location base)	{
		this.name = name;
		this.base = base;
		
		velocity = new Vector();
		entityType = EntityType.SHEEP;
		typeId = 0;
		data = 0;
		
		switches = new ArrayList<Location>();
		signs = new ArrayList<Location>();

	}
	
	public GravitySheepRegion(String name, Location base, Vector velocity, EntityType entityType, int typeId, byte data, ArrayList<Location> switches, ArrayList<Location> signs) {
		this.name = name;
		this.base = base;
		this.velocity = velocity;
		this.entityType = entityType;
		this.typeId = typeId;
		this.data = data;
		this.switches = switches;
		this.signs = signs;
		
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public Location getBase() { return base; }
	public void setBase(Location base) { this.base = base; }
	
	public World getWorld() { return base.getWorld(); }
	public void setWorld(World world) { base.setWorld(world); }
	
	public Vector getVelocity() { return this.velocity; }
	public void setVelocity(Vector velocity) { this.velocity = velocity; }
	public void setVelocity(double _x, double _y, double _z) { this.velocity.setX(_x); this.velocity.setY(_y); this.velocity.setZ(_z); }
	
	public EntityType getEntityType() { return entityType; }
	public void setEntityType(EntityType entityType) { this.entityType = entityType; }

	public byte getFallingBlockData() { return data; }
	public void setFallingBlockData(byte v) { data = v; }

	public int getFallingBlockID() { return typeId; }
	public void setFallingBlockID(int v) { typeId = v; }

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
