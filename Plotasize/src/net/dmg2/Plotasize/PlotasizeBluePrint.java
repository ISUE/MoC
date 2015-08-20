package net.dmg2.Plotasize;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlotasizeBluePrint {
	
	private Plotasize plugin;
	private Player player;
	
	private boolean done;

	private Location center; //Center location, 0,0,0 for now
	private World world;
	private Block currentBlock; //Current block as we work through blue print
	private Material currentMaterial;
	private byte currentData;

	private int totalX; //width (0;+)
	private int totalZ; //length (0;+)
	private int totalY; //height (0;254)
	
	private int maxX;
	private int maxY;
	private int maxZ;
	private int minX;
	private int minY;
	private int minZ;
	
	private int numberOfPlots;
	private int clearance;
	private int buffer;
	private int plotX;
	private int plotY;
	private int plotZ;
	
	public PlotasizeBluePrint(Plotasize plugin, Player player) {
		//Save plugin info
		this.plugin = plugin;
		this.player = player;
		this.done = false;
		
		//Get settings from the config file
		this.numberOfPlots = (int) Math.sqrt(this.plugin.config.getNumberOfPlots());
		this.plotX = this.plugin.config.getPlotX();
		this.plotY = this.plugin.config.getPlotY();
		this.plotZ = this.plugin.config.getPlotZ();
		this.buffer = this.plugin.config.getPlotBuffer();
		this.clearance = this.plugin.config.getClearance();
		
		//Store center location
		if (this.plugin.config.getCenterOnPlayer()) {
			this.center = player.getLocation(); //Player location
		} else {
			this.center = new Location(player.getWorld(), 0, 0, 0); //Center of the world	
		}
		
		//World
		this.world = this.center.getWorld();
		
		//Calculate total width and length
		this.totalX = this.clearance * 2 + this.plotX * this.numberOfPlots + this.buffer * (this.numberOfPlots - 1);
		this.totalY = this.world.getMaxHeight() - 2;
		this.totalZ = this.clearance * 2 + this.plotZ * this.numberOfPlots + this.buffer * (this.numberOfPlots - 1);
		
		this.minX = (int) (this.center.getX() - this.totalX / 2);
		this.minY = 0;
		this.minZ = (int) (this.center.getZ() - this.totalZ / 2);
		
		this.maxX = this.minX + this.totalX; 
		this.maxY = this.minY + this.totalY; 
		this.maxZ = this.minZ + this.totalZ; 
		
		//Calculate first block
		this.currentBlock = this.world.getBlockAt(new Location(this.world, this.minX, 150 > this.plotY ? 150 : this.plotY, this.minZ));
		
	}
	
	public boolean setNextBlock() {
		//Make sure we have more blocks to go, if not return false
		if (this.done) {
			this.plugin.log.sendPlayerNormal(this.player, "All plots are created.");
			return false;
		}
		
		//Calculate material and data for the current block
		this.getMaterial();
		
		//Apply material and data
		this.currentBlock.setType(this.currentMaterial);
		this.currentBlock.setData(this.currentData);
		
		//Move to the next block
		this.incrementBlock();
		
		return true;
		
	}
	
	private void incrementBlock() {
		//Check if we are at the last block already
		if (this.currentBlock.getX() == this.maxX && this.currentBlock.getY() == this.minY && this.currentBlock.getZ() == this.maxZ) {
			this.done = true;
			return;
		}
		
		if (this.currentBlock.getX() < this.maxX) {
			//Can still increment on X
			this.currentBlock = this.world.getBlockAt(new Location(this.world, this.currentBlock.getX() + 1, this.currentBlock.getY(), this.currentBlock.getZ()));
		} else {
			//X is maxed, roll over
			if (this.currentBlock.getZ() < this.maxZ) {
				this.currentBlock = this.world.getBlockAt(new Location(this.world, this.minX, this.currentBlock.getY(), this.currentBlock.getZ() + 1));				
			} else {
				//X is maxed and Z is maxed, roll over
				if (this.currentBlock.getY() > this.minY) {
					//Shouldn't need to check this, but just in case!
					this.currentBlock = this.world.getBlockAt(new Location(this.world, this.minX, this.currentBlock.getY() - 1, this.minZ));
				} else {
					//All x, y, and z were too big somehow ! should've beed caught earlier but oh well!
					this.done = true;
					return;
				}
				
			}
			
		}
		
	}

	private void getMaterial() {
		//Get absolute position in the blueprint [0,totalXYZ]
		int x = this.currentBlock.getX() - this.minX;
		int y = this.currentBlock.getY() - this.minY;
		int z = this.currentBlock.getZ() - this.minZ;
		
		int plotNumberX = (x - this.clearance) / (this.plotX + this.buffer);
		int plotNumberZ = (z - this.clearance) / (this.plotZ + this.buffer);
		
		//Air locations
		if (y > this.plotY || x < this.clearance || z < this.clearance || x > this.totalX - this.clearance || z > this.totalZ - this.clearance) {
			this.currentMaterial = Material.AIR;
			this.currentData = 0;
		} else {
			//Not an outside air location, we are inside plots (still some air pockets for buffers)
			//Buffer locations
			if (x > this.clearance + (plotNumberX + 1) * (this.plotX + this.buffer) - this.buffer - 1 || z > this.clearance + (plotNumberZ + 1) * (this.plotZ + this.buffer) - this.buffer - 1) {
				this.currentMaterial = Material.AIR;
				this.currentData = 0;
			} else {
				//Not air, have to put a block
				if (y == this.plotY) {
					//Top layer of the plots, have to color it
					this.currentMaterial = Material.WOOL;
					this.currentData = (byte) ((plotNumberX + this.numberOfPlots * plotNumberZ) % 16); 
				} else if (y < this.minY + 2){
					//Bottom layer of the plots, have to bedrock it
					this.currentMaterial = Material.BEDROCK;
					this.currentData = 0;
				} else {
					//Inside the plot, just generate random rocks
					int rand = (int) (Math.random() * 100);
					if (rand < 60) {
						this.currentMaterial = Material.STONE;
					} else if (rand < 70) {
						this.currentMaterial = Material.COAL_ORE;
					} else if (rand < 90) {
						this.currentMaterial = Material.IRON_ORE;
					} else if (rand < 95) {
						this.currentMaterial = Material.GOLD_ORE;
					} else if (rand < 97) {
						this.currentMaterial = Material.DIAMOND_ORE;
					} else if (rand <= 100) {
						this.currentMaterial = Material.REDSTONE_ORE;
					}
					this.currentData = 0;
				}
				
			}
			
		}

	}
	
	public int getPercentDone() {
		return (int) (100 - (((double)(this.currentBlock.getY()-this.minY)) / ((double)this.maxY)) * 100);
	}
	
	public Player getPlayer() { return this.player; }

}
