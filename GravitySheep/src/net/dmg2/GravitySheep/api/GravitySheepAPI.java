package net.dmg2.GravitySheep.api;

import java.util.ArrayList;
import org.bukkit.World;
import net.dmg2.GravitySheep.GravitySheep;

public class GravitySheepAPI {
	private GravitySheep plugin;
	private ArrayList<GravitySheepWorld> worlds;
	
	public GravitySheepAPI(GravitySheep plugin) {
		this.plugin = plugin;
		
		worlds = new ArrayList<GravitySheepWorld>();
		
	}
	
	public ArrayList<GravitySheepWorld> getWorlds() { return worlds; }
	
	public GravitySheepWorld getWorld(World w) {
		if (w == null) return null;
		
		//Check if already loaded
		for (GravitySheepWorld world : worlds) if (world.getName().equalsIgnoreCase(w.getName())) return world;
		
		//Load new world if that world is online
		GravitySheepWorld newWorld = new GravitySheepWorld(plugin, w);
		worlds.add(newWorld);
		return newWorld;
			
	}
	
	//=============================================================
	/**
	 * Removes world and its regions
	 * 
	 * @param worldName Name of the world to remove
	 * 
	 * @return True if removal was successful
	 */
	public boolean removeWorld(String worldName) {
		for (GravitySheepWorld world : worlds) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				plugin.getConfiguration().removeWorld(world.getName());
				worlds.remove(world);
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	//=============================================================
	/**
	 * Copies GravitySheep regions from one world to another.
	 * TO world's regions are kept unless same name.
	 * Both worlds have to be online
	 * 
	 * @param to - Name of the destination world
	 * @param from - Name of the source world
	 * @return True on success, False on fail
	 */
	public boolean copyWorld(World to, World from) {
		GravitySheepWorld fromWorld = getWorld(from);
		GravitySheepWorld toWorld = getWorld(to);
		
		if (fromWorld == null || toWorld == null) return false;
		
		toWorld.addRegions(fromWorld.getRegions());
		
		return true;
		
	}

}
