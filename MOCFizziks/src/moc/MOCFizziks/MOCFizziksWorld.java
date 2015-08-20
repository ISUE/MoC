package moc.MOCFizziks;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class MOCFizziksWorld {
	//=================================================================
	private MOCFizziks plugin;
	private World world; public World getWorld() { return world; } public String getName() { return world.getName(); }
	private ArrayList<MOCFizziksRegion> regions;
	//=================================================================

	
	
	//=================================================================
	public MOCFizziksWorld(MOCFizziks plugin, World w) {
		this.plugin = plugin;
		world = w;
		
		regions = plugin.getConfiguration().getRegions(getName());
		
	}
	//=================================================================
	

	
	//=================================================================
	public ArrayList<MOCFizziksRegion> getRegions() { return regions; }
	
	public MOCFizziksRegion getRegion(String name) {
		for (MOCFizziksRegion r : regions) if (r.getName().equalsIgnoreCase(name)) return r;
		
		return null;
		
	}

	public MOCFizziksRegion getRegion(Block block) {
		for (MOCFizziksRegion r : regions) {
			if ( ( block.getX() >= r.getLowerLeft().getX()-0.5 && block.getX() <= r.getUpperRight().getX()+0.5 ) &&
				     ( block.getY() >= r.getLowerLeft().getY() && block.getY() <= r.getUpperRight().getY()+1 ) &&
				     ( block.getZ() >= r.getLowerLeft().getZ()-0.5 && block.getZ() <= r.getUpperRight().getZ()+0.5 )  ) {
				return r;
				
			}
			
		}
		
		return null;
		
	}
	
	public ArrayList<MOCFizziksRegion> getSwitchRegions(Block block) {
		ArrayList<MOCFizziksRegion> rs = new ArrayList<MOCFizziksRegion>();
		
		for (MOCFizziksRegion r : regions) if (r.isSwitch(block)) rs.add(r);
		
		return rs;
		
	}

	public ArrayList<MOCFizziksRegion> getSignRegions(Block block) {
		ArrayList<MOCFizziksRegion> rs = new ArrayList<MOCFizziksRegion>();
		
		for (MOCFizziksRegion r : regions) if (r.isSign(block)) rs.add(r);
		
		return rs;
		
	}
	//=================================================================
	

	
	//=================================================================
	public boolean removeRegion(String regionName) {
		for (MOCFizziksRegion region : regions) {
			if (region.getName().equalsIgnoreCase(regionName)) {
				regions.remove(region);
				plugin.getConfiguration().removeRegion(getName(), regionName);
				
			}
			
		}
		
		return false;
		
	}
	//=================================================================
	public void removeAllRegions() {
		for (MOCFizziksRegion region : regions)
			plugin.getConfiguration().removeRegion(getName(), region.getName());
		
		regions.clear();
		
	}
	//=================================================================



	//=================================================================
	public void addRegions(ArrayList<MOCFizziksRegion> regions) {
		for (MOCFizziksRegion region : regions) {
			MOCFizziksRegion regionToRemove = null;
			
			//Make sure locations is in this world
			region.getUpperRight().setWorld(world);
			region.getLowerLeft().setWorld(world);
			
			//Remove regions with same name
			for (MOCFizziksRegion r : this.regions) { if (r.getName().equalsIgnoreCase(region.getName())) regionToRemove = r; break; }
			if (regionToRemove != null) { this.regions.remove(regionToRemove); plugin.getConfiguration().removeRegion(getName(), regionToRemove.getName()); }
			
			//Add replacement
			this.regions.add(region);
			plugin.getConfiguration().saveRegion(region);
			
		}
		
	}
	
	public MOCFizziksRegion addRegion(String regionName, Location loc1, Location loc2) {
		MOCFizziksRegion region = new MOCFizziksRegion(regionName, loc1, loc2);
		regions.add(region);
		plugin.getConfiguration().saveRegion(region);
		
		return region;
		
	}
	//=================================================================
}
