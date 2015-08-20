package net.moc.MOC3DImporter;

import java.util.TreeMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class MOC3DImporterBuilder {
	//------------------------------------------------------
	private MOC3DImporter plugin;
	private boolean busy = false;
	private TreeMap<String, MOC3DImporterBluePrint> buildQueue = new TreeMap<String, MOC3DImporterBluePrint>();
	private long lastQueueCall = System.currentTimeMillis();
	public MOC3DImporterBuilder(MOC3DImporter plugin) {
		this.plugin = plugin;
	}
	//------------------------------------------------------
	
	public void build(String playerName, boolean useTexture) {
		//Get the matrix
		int[][][] matrix = this.plugin.getConverter().getPlayerMatrix(playerName);
		Location origin = this.plugin.getConverter().getPlayerMatrixLocation(playerName);
		
		if (this.buildQueue.containsKey(playerName)) {
			this.plugin.getLog().sendPlayerWarn(this.plugin.getServer().getPlayer(playerName), "You already have one matrix in the build queue.");
			return;
			
		}
		
		//Add it to the build queue
		this.buildQueue.put(playerName, new MOC3DImporterBluePrint(matrix, origin, useTexture));
		this.plugin.getLog().info("Adding matrix for " + playerName + " to the build queue.");
		this.plugin.getLog().sendPlayerNormal(this.plugin.getServer().getPlayer(playerName), "Adding matrix to the build queue.");
		
	}
	
	public void continueQueue() {
		if (busy) return;
		busy = true;
		
		//Check if anything to build
		if (this.buildQueue.isEmpty()) { busy = false; return; }
		if (System.currentTimeMillis() - this.lastQueueCall  < this.plugin.getConfiguration().getQueueDelay()) { busy = false; return; }
		this.lastQueueCall = System.currentTimeMillis();
		
		//Get the first blueprint in queue
		MOC3DImporterBluePrint blueprint = this.buildQueue.get(this.buildQueue.firstKey());
		
		//Get matrix
		int[][][] matrix = blueprint.getMatrix();
		
		//Get last i,j,k values
		int i = blueprint.getX();
		int j = blueprint.getY();
		int k = blueprint.getZ();
		
		//Get world name
		Location origin = blueprint.getOrigin();
		World world = origin.getWorld();
		int x = origin.getBlockX();
		int y = origin.getBlockY();
		int z = origin.getBlockZ();
		
		//Max blocks to be changed
		int maxNumberOfBlocksPerQueue = this.plugin.getConfiguration().getBlocksPerQueue();
		int typeId = this.plugin.getConfiguration().getBlockTypeId();
		byte data = this.plugin.getConfiguration().getBlockData();
		
		//Set blocks
		for (; i < matrix.length ; i++) {
			for ( ; j < matrix[0].length ; j++) {
				for ( ; k < matrix[0][0].length ; k++) {
					if (matrix[i][j][k] > 0) {
						//Get next block location and set base block type/data
						Block block = world.getBlockAt(x + i - matrix.length / 2, y + j, z + k - matrix[0][0].length / 2);
						
						//Check if we are using texture
						if (blueprint.getUseTexture()) block.setTypeIdAndData(matrix[i][j][k] % 1000, (byte) (matrix[i][j][k] / 1000), false);
						else block.setTypeIdAndData(typeId, data, false);
						
						//Decrement the per queue counter
						maxNumberOfBlocksPerQueue--;
						
						//Return if limit is reached
						if (maxNumberOfBlocksPerQueue == 0) {
							//Save build state
							blueprint.setX(i);
							blueprint.setY(j);
							blueprint.setZ(k);
							
							int progress = (int) ((100.0 * (i * matrix[0].length * matrix[0][0].length + j * matrix[0][0].length + k)) / (matrix.length * matrix[0].length * matrix[0][0].length));
							this.plugin.getLog().info("Building progress for " + this.buildQueue.firstKey() + ": " + progress + "%.");
							this.plugin.getLog().sendPlayerNormal(this.plugin.getServer().getPlayer(this.buildQueue.firstKey()), "Building progress: " + progress + "%.");
							
							busy = false;
							return;
							
						}
						
					}
					
				}
				
				k = 0;
				
			}
			
			j = 0;
			
		}
		
		//Remove the blueprint
		this.plugin.getLog().sendPlayerNormal(this.plugin.getServer().getPlayer(this.buildQueue.firstKey()), "Building is complete.");
		this.plugin.getLog().info("Building for " + this.buildQueue.firstKey() + " is complete.");
		this.plugin.getConverter().removePlayer(this.buildQueue.firstKey());
		this.buildQueue.remove(this.buildQueue.firstKey());
		busy = false;
		
	}
	
	public class MOC3DImporterBluePrint {
		private int[][][] matrix;
		private int x, y, z;
		private Location origin;
		private boolean useTexture;
		
		public MOC3DImporterBluePrint(int[][][] matrix, Location origin, boolean useTexture) {
			this.matrix = matrix;
			this.origin = origin;
			this.x = 0;
			this.y = 0;
			this.z = 0;
			this.useTexture = useTexture;
			
		}

		public int[][][] getMatrix() { return matrix; }
		public Location getOrigin() { return origin; }

		public int getX() { return x; }
		public void setX(int x) { this.x = x; }

		public int getY() { return y; }
		public void setY(int y) { this.y = y; }

		public int getZ() { return z; }
		public void setZ(int z) { this.z = z; }
		
		public boolean getUseTexture() { return this.useTexture; }
		public void setUseTexture(boolean useTexture) { this.useTexture = useTexture; }
		
	}

}
