package moc.MOCFizziks;

import java.util.ArrayList;

import org.bukkit.World;

public class MOCFizziksAPI {
	private MOCFizziks plugin;
	private ArrayList<MOCFizziksWorld> worlds;

	//=============================================================
	public MOCFizziksAPI(MOCFizziks plugin) {
		this.plugin = plugin;
		
		worlds = new ArrayList<MOCFizziksWorld>();
		
	}
	
	//=============================================================
	public ArrayList<MOCFizziksWorld> getWorlds() { return worlds; }
	
	public MOCFizziksWorld getWorld(World w) {
		if (w == null) return null;
		
		//Check if already loaded
		for (MOCFizziksWorld world : worlds) if (world.getName().equalsIgnoreCase(w.getName())) return world;
		
		//Load new world if that world is online
		MOCFizziksWorld newWorld = new MOCFizziksWorld(plugin, w);
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
		for (MOCFizziksWorld world : worlds) {
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
	 * Copies MOCFizziks regions from one world to another.
	 * TO world's regions are kept unless same name.
	 * Both worlds have to be online
	 * 
	 * @param to - Name of the destination world
	 * @param from - Name of the source world
	 * @return True on success, False on fail
	 */
	public boolean copyWorld(World to, World from) {
		MOCFizziksWorld fromWorld = getWorld(from);
		MOCFizziksWorld toWorld = getWorld(to);
		
		if (fromWorld == null || toWorld == null) return false;
		
		toWorld.addRegions(fromWorld.getRegions());
		
		return true;
		
	}

}
