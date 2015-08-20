package net.moc.MOC3DImporter;

import java.io.File;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MOC3DImporterConverter {
	//----------------------------------------------------
	private MOC3DImporter plugin;
	private TreeMap<String, int[][][]> playerMatrix = new TreeMap<String, int[][][]>();
	private TreeMap<String, Location> playerMatrixLocation = new TreeMap<String, Location>();
	
	public int[][][] getPlayerMatrix(String playerName) { return this.playerMatrix.get(playerName); }
	public Location getPlayerMatrixLocation(String playerName) { return this.playerMatrixLocation.get(playerName); }
	
	public void addToPlayerMatrix(String playerName, Location location, int[][][] matrix) {
		this.playerMatrix.put(playerName, matrix);
		this.playerMatrixLocation.put(playerName, location);
		
	}
	
	public void removePlayer(String playerName) {
		this.playerMatrix.remove(playerName);
		this.playerMatrixLocation.remove(playerName);
	}	
	
	public MOC3DImporterConverter (MOC3DImporter plugin) { this.plugin = plugin; }
	//----------------------------------------------------
	
	public void generateMatrix(File objectFile, Player player, Location location, float top, float bottom, boolean useTexture) {
		//Message the player
		this.plugin.getLog().sendPlayerNormal(player, "Loading file " + objectFile.getName() + ".");
		
		//Start the generation
		new Thread(new MOC3DImporterMatrixGenerator(this.plugin, objectFile, player, location, top, bottom, useTexture)).start();
		this.plugin.getLog().sendPlayerNormal(player, "Starting matrix generation.");
		
	}
	
}
