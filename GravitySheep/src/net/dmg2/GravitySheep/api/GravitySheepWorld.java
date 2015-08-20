package net.dmg2.GravitySheep.api;

import java.util.ArrayList;
import net.dmg2.GravitySheep.GravitySheep;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class GravitySheepWorld {
	private GravitySheep plugin;
	private World world;
	private ArrayList<GravitySheepRegion> regions;
	
	//=========================================================================
	public GravitySheepWorld(GravitySheep plugin, World world) {
		this.plugin = plugin;
		this.world = world;
		
		regions = plugin.getConfiguration().getRegions(getName());
		
	}
	//=========================================================================

	
	
	//=========================================================================
	public World getWorld() { return world; }
	public String getName() { return world.getName(); }
	public ArrayList<GravitySheepRegion> getRegions() { return regions; }
	//=========================================================================

	
	
	//=========================================================================
	public GravitySheepRegion getRegion(String name) {
		for (GravitySheepRegion r : regions) if (r.getName().equalsIgnoreCase(name)) return r;
		
		return null;
		
	}

	public GravitySheepRegion getRegion(Block block) {
		for (GravitySheepRegion r : regions) {
			int dx = (r.getBase().getBlockX() - block.getX());
			int dy = (r.getBase().getBlockY() - block.getY());
			int dz = (r.getBase().getBlockZ() - block.getZ());
			if (dx < 1 && dy < 1 && dz < 1) return r;
			
		}
		
		return null;
		
	}
	
	public ArrayList<GravitySheepRegion> getSwitchRegions(Block block) {
		ArrayList<GravitySheepRegion> rs = new ArrayList<GravitySheepRegion>();
		
		for (GravitySheepRegion r : regions) if (r.isSwitch(block)) rs.add(r);
		
		return rs;
		
	}

	public ArrayList<GravitySheepRegion> getSignRegions(Block block) {
		ArrayList<GravitySheepRegion> rs = new ArrayList<GravitySheepRegion>();
		
		for (GravitySheepRegion r : regions) if (r.isSign(block)) rs.add(r);
		
		return rs;
		
	}
	//=================================================================
	

	
	//=================================================================
	public boolean removeRegion(String regionName) {
		for (GravitySheepRegion region : regions) {
			if (region.getName().equalsIgnoreCase(regionName)) {
				regions.remove(region);
				plugin.getConfiguration().removeRegion(getName(), regionName);
				
			}
			
		}
		
		return false;
		
	}
	
	//=================================================================
	public void removeAllRegions() {
		for (GravitySheepRegion region : regions)
			plugin.getConfiguration().removeRegion(getName(), region.getName());
		
		regions.clear();
		
	}
	//=================================================================



	//=================================================================
	public void addRegions(ArrayList<GravitySheepRegion> regions) {
		for (GravitySheepRegion region : regions) {
			GravitySheepRegion regionToRemove = null;
			
			//Make sure locations is in this world
			region.setWorld(world);
			
			//Remove regions with same name
			for (GravitySheepRegion r : this.regions) { if (r.getName().equalsIgnoreCase(region.getName())) regionToRemove = r; break; }
			if (regionToRemove != null) { this.regions.remove(regionToRemove); plugin.getConfiguration().removeRegion(getName(), regionToRemove.getName()); }
			
			//Add replacement
			this.regions.add(region);
			plugin.getConfiguration().saveRegion(region);
			
		}
		
	}
	
	public GravitySheepRegion addRegion(String regionName, Location loc) {
		GravitySheepRegion region = new GravitySheepRegion(regionName, loc);
		regions.add(region);
		plugin.getConfiguration().saveRegion(region);
		
		return region;
		
	}
	//=================================================================
}
