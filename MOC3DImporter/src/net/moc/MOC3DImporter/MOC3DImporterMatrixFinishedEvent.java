package net.moc.MOC3DImporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MOC3DImporterMatrixFinishedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
    private Player player;
	private String objectFileName;
	private Location location;
	private int[][][] matrix;
	private boolean useTexture;
 
    public MOC3DImporterMatrixFinishedEvent(Player player, String objectFileName, Location location, int[][][] matrix, boolean useTexture) {
        this.player = player;
        this.objectFileName = objectFileName;
        this.location = location;
        this.matrix = matrix;
        this.useTexture = useTexture;
        
    }
 
    public Location getLocation() { return location; }
	public int[][][] getMatrix() { return matrix; }
	public Player getPlayer() { return player; }
	public String getObjectFileName() { return objectFileName; }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

	public boolean isUseTexture() { return useTexture; }
	public void setUseTexture(boolean useTexture) { this.useTexture = useTexture; }

}
