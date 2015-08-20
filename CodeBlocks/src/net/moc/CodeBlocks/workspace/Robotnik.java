package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.RobotBlock;
import net.moc.CodeBlocks.workspace.command.InteractionCommand.Interaction;
import net.moc.CodeBlocks.workspace.events.RobotnikAction;
import net.moc.CodeBlocks.workspace.parts.Directive;

public class Robotnik {
	private CodeBlocks plugin;
	private String playerName;
	private Player player;
	
	//Directions for robot
	public static enum Direction {north, south, east, west}
	public static enum RobotSide {front, back, left, right, top, down, around, inventory}
	public static enum RobotQuad {frontrighttop, frontrrightdown, frontlefttop, frontleftdown, backrighttop, backrightdown, backlefttop, backleftdown}
	
	private Material air = Material.AIR;
	
	//Robot physical statistics
	private SpoutBlock robotBlock = null;
	private Direction direction;
	
	private RobotnikStats stats; public RobotnikStats getStats() { return stats; }
	
	//Inventory
	private HashMap<Integer, Integer> inventory = new HashMap<Integer, Integer>();
	private Stack<MaterialData> pickedUpStack = new Stack<MaterialData>();
	private RobotBlock robotCustomBlock;
	private ArrayList<Directive> enabledDirectives = new ArrayList<Directive>();

	//----------------------------------------------------------
	public Robotnik(CodeBlocks plugin, String playerName, Location location, Direction direction, String currentLevels) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.direction = direction;
		
		player = plugin.getServer().getPlayer(playerName);
		
		stats = new RobotnikStats(plugin.getConfiguration().getRobotMaxHealth(), plugin.getConfiguration().getRobotMaxArmor(),
								  plugin.getConfiguration().getRobotMaxPower(), plugin.getConfiguration().getRobotMaxDamage());
		
		if (currentLevels != null) {
			String[] levels = currentLevels.split(";");

			try {
				stats.setPowerCurrent(Double.parseDouble(levels[0]));
				stats.setHealthCurrent(Double.parseDouble(levels[1]));
				stats.setArmorCurrent(Double.parseDouble(levels[2]));
				} catch (NumberFormatException e) {}
			
			if (levels.length == 4) {
				enableDirectives(levels[3].split(":"));
				
			}
			
		}
		
		robotBlock = (SpoutBlock) location.getWorld().getBlockAt(location);
		robotCustomBlock = plugin.getBlocks().getRobotBlock();
	    
		teleportAndFace(location, direction);
		
	}
	//----------------------------------------------------------
	public void consumePower(int v) { stats.addPowerCurrent(-v); executeDirectives(null); }
	
	public ArrayList<Directive> getDirectivesEnabled() { return enabledDirectives; }
	public void enableDirectives(ArrayList<String> ds) {
		enabledDirectives.clear();
		for (String dName : ds) for (Directive d : plugin.getWorkspace().getDirectives()) if (d.getName().equalsIgnoreCase(dName)) enabledDirectives.add(d); 
		
	}
	
	public void enableDirectives(String[] ds) {
		enabledDirectives.clear();
		for (String dName : ds) for (Directive d : plugin.getWorkspace().getDirectives()) if (d.getName().equalsIgnoreCase(dName)) enabledDirectives.add(d); 
		
	}
	
	//##################################################################################
	// STATS
	//
	public void takeDamage(double amount) {
		stats.addArmorCurrent(-amount);
		if (stats.getArmorCurrent() == 0) stats.addHealthCurrent(-amount);
		
	}
	
	public void smash(String targetType) {
		int damage = (int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage());
		
		if (targetType.equalsIgnoreCase("Robot")) {
			for (int x = -1 ; x <= 1 ; x++) {
				for (int y = -1 ; y <= 1 ; y++) {
					for (int z = -1 ; z <= 1 ; z++) {
						if (x == 0 && y == 0 && z == 0) continue;
						
						try {
							//Get next block
							SpoutBlock b = (SpoutBlock) robotBlock.getLocation().add(x, y, z).getBlock();
							
							//If it's a robot block, try to get matching RC
							if (b.getCustomBlock() instanceof RobotBlock) {
								RobotnikController rc = plugin.getWorkspace().getRobotnikController(b);
								//If got RC, send damage
								if (rc != null) {
									rc.getRobotnik().takeDamage(damage);
									robotBlock.getWorld().playEffect(rc.getRobotnik().getLocation(), Effect.SMOKE, 0, 3);
									
								}
								
							}
							
						} catch (Exception e) {}
						
					}
					
				}
				
			}
			
		} else if (targetType.equalsIgnoreCase("Monster")) {
			for (Monster e : robotBlock.getWorld().getEntitiesByClass(Monster.class)) if (e.getLocation().distance(robotBlock.getLocation()) < 2) e.damage(damage);
			
		} else if (targetType.equalsIgnoreCase("Animal")) {
			for (Animals e : robotBlock.getWorld().getEntitiesByClass(Animals.class)) if (e.getLocation().distance(robotBlock.getLocation()) < 2) e.damage(damage);
			
		} else if (targetType.equalsIgnoreCase("Creature")) {
			for (Creature e : robotBlock.getWorld().getEntitiesByClass(Creature.class)) if (e.getLocation().distance(robotBlock.getLocation()) < 2) e.damage(damage);
			
		} else if (targetType.equalsIgnoreCase("Player")) {
			for (Player e : robotBlock.getWorld().getEntitiesByClass(Player.class)) if (e.getLocation().distance(robotBlock.getLocation()) < 2) e.damage(damage);
			
		} else {
			//Attack all entities and robots
			for (Entity e : robotBlock.getWorld().getEntitiesByClass(Entity.class))
				if (e.getLocation().distance(robotBlock.getLocation()) < 2)
					if (e instanceof Damageable) ((Damageable)e).damage(damage);
			
			for (int x = -1 ; x <= 1 ; x++) {
				for (int y = -1 ; y <= 1 ; y++) {
					for (int z = -1 ; z <= 1 ; z++) {
						if (x == 0 && y == 0 && z == 0) continue;
						
						try {
							//Get next block
							SpoutBlock b = (SpoutBlock) robotBlock.getLocation().add(x, y, z).getBlock();
							
							//If it's a robot block, try to get matching RC
							if (b.getCustomBlock() instanceof RobotBlock) {
								RobotnikController rc = plugin.getWorkspace().getRobotnikController(b);
								//If got RC, send damage
								if (rc != null) {
									rc.getRobotnik().takeDamage(damage);
									robotBlock.getWorld().playEffect(rc.getRobotnik().getLocation(), Effect.SMOKE, 0, 3);
									
								}
								
							}
							
						} catch (Exception e) {}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public void shoot(RobotSide s, String targetType) {
		Block b = getBlockAt(s);
		Vector v = new Vector(b.getX() - robotBlock.getLocation().getBlockX(),
				  b.getY() - robotBlock.getLocation().getBlockY(),
				  b.getZ() - robotBlock.getLocation().getBlockZ());
		
		if (targetType.equalsIgnoreCase("robot")) {
			///Attack just robot
			int damage = (int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage());
			for (int i = 0 ; i < 15 ; i++) {
				RobotnikController rc = plugin.getWorkspace().getRobotnikController(b.getLocation().add(v.getX()*i, v.getY()*i, v.getZ()*i).getBlock());
				if (rc != null) {
					rc.getRobotnik().takeDamage(damage);
					robotBlock.getWorld().playEffect(rc.getRobotnik().getLocation(), Effect.SMOKE, 0, 3);
					
				}
				
			}
			
			return;
		} else if (targetType.equalsIgnoreCase("all")) {
			//Attack both robot and entities
			int damage = (int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage());
			for (int i = 0 ; i < 15 ; i++) {
				RobotnikController rc = plugin.getWorkspace().getRobotnikController(b.getLocation().add(v.getX()*i, v.getY()*i, v.getZ()*i).getBlock());
				if (rc != null) {
					rc.getRobotnik().takeDamage(damage);
					robotBlock.getWorld().playEffect(rc.getRobotnik().getLocation(), Effect.SMOKE, 0, 3);
					
				}
				
			}
			
		}
		
		robotBlock.getWorld().spawnArrow(robotBlock.getLocation().add(0.5, 1.2, 0.5), v, 2, 8);
		robotBlock.getWorld().spawnArrow(robotBlock.getLocation().add(0.5, 1.2, 0.5), v, 3, 8);
		robotBlock.getWorld().spawnArrow(robotBlock.getLocation().add(0.5, 1.2, 0.5), v, 4, 8);
		robotBlock.getWorld().spawnArrow(robotBlock.getLocation().add(0.5, 1.2, 0.5), v, 5, 8);
		
	}
	
	public boolean isAlive() {
		if (stats.getHealthCurrent() > 0) return true;
		return false;
		
	}
	
	public boolean hasPower() {
		if (stats.getPowerCurrent() > 0) return true;
		return false;
		
	}
	
	//##################################################################################
	// MOVEMENT
	//
	public void moveForward() { moveRobotToTry(getBlockAt(RobotSide.front)); }
	public void moveBack() { moveRobotToTry(getBlockAt(RobotSide.back)); }
	public void moveLeft() { moveRobotToTry(getBlockAt(RobotSide.left)); }
	public void moveRight() { moveRobotToTry(getBlockAt(RobotSide.right)); }
	public void moveUp() { moveRobotToTry(getBlockAt(RobotSide.top)); }
	public void moveDown() { moveRobotToTry(getBlockAt(RobotSide.down)); }
	
	private void moveRobotToTry(Block block) {
		if (!isAlive() || !hasPower()) return;
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, block.getLocation()));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public boolean moveRobotToCan(Block block) {
		if (block.getType() == air) {
			//We can move
			//Clear current location
			robotBlock.setCustomBlock(null);
			robotBlock.setType(air);
			
			//Set robot block in the new location
			robotBlock = (SpoutBlock) block;
			
			//Set facing
			setFacing(direction);
			
			return true;
			
		} else {
			//We can't move
			plugin.getLog().sendPlayerWarn(playerName, "Robot [" + plugin.getWorkspace().getPlayerWorkspace(playerName).getRobotnik(robotBlock).getId() + "] bumped into a wall.");
			
			return false;
			
		}
		
	}
	
	private boolean teleport(Location location) {
		//Get new location's block
		SpoutBlock block = (SpoutBlock) location.getWorld().getBlockAt(location);
		if (block.getType() != air) return false;
		
		//Erase old location
		if (robotBlock != null) { robotBlock.setCustomBlock(null); robotBlock.setType(air); }
		
		//Set it to robot block
		block.setCustomBlock(robotCustomBlock);
		robotBlock = block;
		
		return true;
		
	}

	public void teleportAndFace(Location location, Direction direction) {
		teleport(location);
		setFacing(direction);
		
	}
	
	public boolean turnLeft() { return turn(-1); }
	public boolean turnRight() { return turn(1); }
	
	private boolean turn(int direction) {
		if (!isAlive() || !hasPower()) return false;
		
		if (direction == 1) {
			switch(this.direction) {
				case north: this.direction = Direction.east; break;
				case south: this.direction = Direction.west; break;
				case east: this.direction = Direction.south; break;
				case west: this.direction = Direction.north; break;
				
			}
			
		} else {
			switch(this.direction) {
				case north: this.direction = Direction.west; break;
				case south: this.direction = Direction.east; break;
				case east: this.direction = Direction.north; break;
				case west: this.direction = Direction.south; break;
			
			}
			
		}
		
		setFacing(this.direction);
		
		return true;
		
	}
	
	private void setFacing(Direction direction) {
		this.direction = direction;
		SimpleMaterialManager materialManager = (SimpleMaterialManager)SpoutManager.getMaterialManager();
		
		switch (this.direction) {
			case north:
				robotBlock.setTypeIdAndData(robotCustomBlock.getBlockId(), (byte) robotCustomBlock.getBlockData(), false);
				materialManager.overrideBlock(robotBlock, robotCustomBlock, (byte) 0);
				robotBlock.setData((byte) 3);
				break;
			case west:
				robotBlock.setTypeIdAndData(robotCustomBlock.getBlockId(), (byte) robotCustomBlock.getBlockData(), false);
				materialManager.overrideBlock(robotBlock, robotCustomBlock, (byte) 1);
				robotBlock.setData((byte) 1);
				break;
			case south: 
				robotBlock.setTypeIdAndData(robotCustomBlock.getBlockId(), (byte) robotCustomBlock.getBlockData(), false);
				materialManager.overrideBlock(robotBlock, robotCustomBlock, (byte) 2);
				robotBlock.setData((byte) 2);
				break;
			case east:
				robotBlock.setTypeIdAndData(robotCustomBlock.getBlockId(), (byte) robotCustomBlock.getBlockData(), false);
				materialManager.overrideBlock(robotBlock, robotCustomBlock, (byte) 3);
				robotBlock.setData((byte) 0);
				break;
			
		}
		
	}
	//##################################################################################
	public int getMaxPickUpRange() {
		int max = 0;
		for (Directive d : enabledDirectives) {
			if (max < d.getRange() && d.doPickUpItems()) max = d.getRange();
			
		}
		
		return max;
		
	}
	
	public int getMaxExterminateRange() {
		int max = 0;
		for (Directive d : enabledDirectives) {
			if (max < d.getRange() && d.doAttack()) max = d.getRange();
			
		}
		
		return max;
		
	}
	
	public void executeDirectives(Item item) {
		if (getMaxExterminateRange() > 0) exterminate();
		if (getMaxPickUpRange() > 0) vacuum(item);
		
	}
	
	private void vacuum(Item item) {
		if (item == null) {
			Collection<Item> entities = robotBlock.getWorld().getEntitiesByClass(Item.class);
			if (entities == null) return; //just in case...
			if (entities.size() == 0) return;

			//Run through all items, see if any near by for vacuum
			for (Item i : entities) {
				if (i.getItemStack().getType() == Material.FLINT && i.getItemStack().getData().getData() > 0) continue;
				
				if (robotBlock.getLocation().distance(i.getLocation()) < getMaxPickUpRange()) {
					addInventory(i.getItemStack());
					i.remove();

				}

			}
			
		} else {
			if (item.getLocation().distance(robotBlock.getLocation()) > getMaxPickUpRange()) return;
			if (item.getItemStack().getType() == Material.FLINT && item.getItemStack().getData().getData() > 0) return;
			
			addInventory(item.getItemStack());
			item.remove();
			
		}
		
	}
	
	private void exterminate() {
		for (Directive d : enabledDirectives) {
			TreeSet<String> targets = new TreeSet<String>();
			for (String t : d.getAttackTargets()) if (t.length() > 0) targets.add(t.toLowerCase().trim());
			
			for (String t : targets) {
				switch (t) {
				case "monster":
					for (Monster entity : robotBlock.getWorld().getEntitiesByClass(Monster.class)) if (robotBlock.getLocation().distance(entity.getLocation()) < d.getRange()) {
						entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0, 3);
						entity.damage((int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage()));
							
					}
					
					break;
				case "animal":
					for (Animals entity : robotBlock.getWorld().getEntitiesByClass(Animals.class)) if (robotBlock.getLocation().distance(entity.getLocation()) < d.getRange()) {
						entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0, 3);
						entity.damage((int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage()));
							
					}
					
					break;
				case "creature":
					for (Creature entity : robotBlock.getWorld().getEntitiesByClass(Creature.class)) if (robotBlock.getLocation().distance(entity.getLocation()) < d.getRange()) {
						entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0, 3);
						entity.damage((int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage()));
							
					}
					
					break;
				case "player":
					for (Player entity : robotBlock.getWorld().getEntitiesByClass(Player.class)) if (robotBlock.getLocation().distance(entity.getLocation()) < d.getRange()) {
						entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0, 3);
						entity.damage((int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage()));
							
					}
					
					break;
				case "all":
					for (Damageable entity : robotBlock.getWorld().getEntitiesByClass(Damageable.class)) if (robotBlock.getLocation().distance(entity.getLocation()) < d.getRange()) {
						entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0, 3);
						entity.damage((int) (Math.random() * plugin.getConfiguration().getRobotMaxDamage()));
							
					}
					
					break;
				}
				
			}
			
			
		}
		
	}
	//##################################################################################

	
	
	
	//##################################################################################
	// POWER
	//
	public void powerUpBlockTry(RobotSide side) {
		if (!isAlive() || !hasPower()) return;

		Block block = getBlockAt(side);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.POWER, side, null, (byte) 0));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public void powerUpBlockCan(RobotSide side) {
		Block block = getBlockAt(side);
		Material mat = block.getType();
		if (mat != air) return;
		
		block.setType(Material.REDSTONE_WIRE);
		block.setData((byte) 15, true);
		block.setData((byte) 15, false);
		
		//Record for achievements
		plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordPowerUp();

	}
	
	
	
	//##################################################################################
	//INTERACTION
	//
	
	/**
	 * Robotnik will build a block on the side specified
	 * @param side - side of the robotnik where to build the block
	 * @param material - material of the block to built
	 * @param data - data value for the block to be built
	 * @return True if robotnik was able to build a block
	 */
	public void buildTry(RobotSide s, Material material, Byte data) {
		if (!isAlive() || !hasPower()) return;
		
		Block block = getBlockAt(s);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.BUILD, s, material, data));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public boolean buildCan(RobotSide side, Material material, Byte data) {
		Block block = getBlockAt(side);
		
		int md = material.getId() * 10000 + data;
		 
		if (data != -1 && inventory.containsKey(md) && inventory.get(md) > 0) {
			//Build
			block.setTypeIdAndData(material.getId(), data, false);
			
			//Update inventory
			inventory.put(md, inventory.get(md) - 1);
			if (inventory.get(md) < 1) inventory.remove(md);
			
			//Record for achievements
			plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordBuild(material);
			
			return true;
			
		} else {
			for (Integer item : inventory.keySet()) if (material.getId() == item / 10000) {
				//Build
				block.setTypeIdAndData(material.getId(), (byte) (item % 10000), false);
				
				//Update inventory
				inventory.put(item, inventory.get(item) - 1);
				if (inventory.get(item) < 1) inventory.remove(item);
				
				//Record for achievements
				plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordBuild(material);
				
				return true;
				
			}
			
		}
		
		plugin.getWorkspace().getPlayerWorkspace(playerName).getVirtualMachine().getProcess(plugin.getWorkspace().getPlayerWorkspace(playerName).getRobotnik(robotBlock)).pause();
		plugin.getLog().sendPlayerWarn(playerName, "Robot [" + plugin.getWorkspace().getPlayerWorkspace(playerName).getRobotnik(robotBlock).getId() + "] ran out of " + material.toString().toLowerCase() + ", pausing.");
		
		return false;
		
	}
	
	/**
	 * Robotnik will destroy a block on the side specified
	 * @param side - side where to destroy the block
	 * @return True is robotnik was able to destroy a block
	 */
	public void destroyTry(RobotSide s) {
		if (!isAlive() || !hasPower()) return;

		Block block = getBlockAt(s);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.DESTROY, s, null, (byte) 0));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public Material destroyCan(RobotSide side) {
		Block block = getBlockAt(side);
		Material mat = block.getType();
		
		if (mat == air) return null;
		
		block.setTypeIdAndData(air.getId(), (byte) 0, false);
		
		//Record for achievements
		if (mat != null) plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordDestroy(mat);
		
		return mat;
		
	}
	
	/**
	 * Robotnik will pick up a block and place it in to the pick up stack to be placed later on the side specified
	 * @param side - side of the robotnik where to pickup block from
	 * @return True if robotnik was able to pick something up
	 */
	public void pickUpTry(RobotSide s) {
		if (!isAlive() || !hasPower()) return;
		
		Block block = getBlockAt(s);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.PICKUP, s, null, (byte) 0));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public Material pickUpCan(RobotSide side) {
		Block block = getBlockAt(side);
		if (block.getType() == air) return null;
		
		//Push on stack
		pickedUpStack.push(new MaterialData(block.getTypeId(), block.getData()));
		block.setTypeIdAndData(air.getId(), (byte) 0, false);
		
		//Record for achievements
		if (block.getType() != null) plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordPickup(block.getType());
		
		return block.getType();
		
	}
	
	/**
	 * Robotnik will place a block from the pick up stack on the side specified
	 * @param side - side of the robotnik where to place the block
	 * @return True if block was successfully placed.
	 */
	public void placeTry(RobotSide s) {
		if (!isAlive() || !hasPower()) return;

		Block block = getBlockAt(s);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.PLACE, s, null, (byte) 0));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public Material placeCan(RobotSide side) {
		Block block = getBlockAt(side);
		//Check if block is not empty
		if (block.getType() != air) return null;
		
		MaterialData mat = new MaterialData(air.getId());
		
		//Try to get something off the item stack
		try { mat = pickedUpStack.pop(); }
		catch (EmptyStackException e) { return null; }
		
		block.setTypeIdAndData(mat.getItemTypeId(), mat.getData(), false);
		
		//Record for achievements
		if (mat != null && mat.getItemType() != air) plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordPlace(mat.getItemType());
		
		return mat.getItemType();
	
	}
	
	/**
	 * Robotnik will dig and collect drops on the specified side
	 * 
	 * @param side - Side at which to dig
	 * @return True if robotnik was able to dig something up
	 */
	public void digTry(RobotSide s) {
		if (!isAlive() || !hasPower()) return;

		Block block = getBlockAt(s);
		
		BlockBreakEvent newEvent = new BlockBreakEvent(block, player);
		plugin.getWorkspace().getListener().getRobotTryQueue().put(newEvent, new RobotnikAction(this, Interaction.DIG, s, null, (byte) 0));
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		
	}
	
	public Collection<ItemStack> digCan(RobotSide side) {
		Block block = getBlockAt(side);
		Material material = block.getType();
		if (material == air) return null;
		
		Collection<ItemStack> drops = block.getDrops();
		
		for (ItemStack item : drops) {
			int md = item.getTypeId() * 10000 + item.getData().getData();
			int amount = item.getAmount();
			
			if (inventory.containsKey(md)) {
				int count = inventory.get(md);
				
				inventory.put(md, count + amount);
				
			} else {
				inventory.put(md, amount);
				
			}
			
		}
		
		block.setTypeIdAndData(air.getId(), (byte) 0, false);

		//Record for achievements
		if (drops != null) plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordDig(drops);
		
		return drops;
		
	}
	//##################################################################################
	
	
	
	
	//##################################################################################
	public Location getLocation() { return robotBlock.getLocation(); }
	public Direction getDirection() { return direction; }
	
	/**
	 * Returns a blocks next to the robotnik on the side specified
	 * @param side - side to get the block from
	 * @return SpoutBlock next to the robotnik on the side specified
	 */
	public Block getBlockAt(RobotSide side) {
		//Get current location
		Location location = this.robotBlock.getLocation();
		
		//x+ east, y+ up, z- north
		switch(side) {
			case top: location.add(0, 1, 0); break;
			case down: location.add(0, -1, 0); break;
			case front:
				switch(direction) {
					case north: location.add(0, 0, -1); break;
					case east: location.add(1, 0, 0); break;
					case west: location.add(-1, 0, 0); break;
					case south: location.add(0, 0, 1); break;
					
				}
				break;
				
			case back:
				switch(direction) {
					case north: location.add(0, 0, 1); break;
					case east: location.add(-1, 0, 0); break;
					case west: location.add(1, 0, 0); break;
					case south: location.add(0, 0, -1); break;
					
				}
				break;
				
			case left:
				switch(direction) {
					case north: location.add(-1, 0, 0); break;
					case east: location.add(0, 0, -1); break;
					case west: location.add(0, 0, 1); break;
					case south: location.add(1, 0, 0); break;
				
				}
				break;
				
			case right:
				switch(direction) {
					case north: location.add(1, 0, 0); break;
					case east: location.add(0, 0, 1); break;
					case west: location.add(0, 0, -1); break;
					case south: location.add(-1, 0, 0); break;
				
				}
				break;
				
		}
		
		//Get the block
		Block block = null;
		
		try { block = plugin.getServer().getWorld(location.getWorld().getName()).getBlockAt(location); }
		catch (NullPointerException e) {}
		
		return block;
		
	}
	
	/**
	 * Returns robotnik's inventory
	 * 
	 * @param complete - true will move all the items from the pick up stack into inventory and return a complete inventory
	 * 
	 * @return ArrayList of ItemStacks representing robotnik's inventory
	 */
	public ArrayList<ItemStack> getInventory() {
		//if (complete) combineBlocks();
		
		ArrayList<ItemStack> inventoryStacks = new ArrayList<ItemStack>();
		
		for (Integer md : inventory.keySet()) {
			int count = inventory.get(md);
			MaterialData mat = new MaterialData(md / 10000, (byte) (md % 10000));
			
			while (count >= 64) { inventoryStacks.add(mat.toItemStack(64)); count -= 64; }
			
			if (count > 0) { inventoryStacks.add(mat.toItemStack(count)); }
			
		}
		
		return inventoryStacks;
		
	}
	
	public String getInventoryHash() {
		//if (complete) combineBlocks();

		String invHash = "";
		
		for (Integer md : inventory.keySet()) if (md != Material.AIR.getId()) invHash += md + " " + inventory.get(md) + "\n";
		
		return invHash;
		
	}
	
	/**
	 * Sets robotnik's inventory
	 * 
	 * @param robotInventory - ArrayList of ItemStacks to set inventory to
	 */
	public void setInventory(ArrayList<ItemStack> robotInventory) {
		//Clear inventory
		inventory.clear();
		addInventory(robotInventory);
		
	}

	public void setInventory(String invHash) {
		//Clear inventory
		inventory.clear();
		
		String[] items = invHash.split("\n");
		
		for (String s : items) {
			String[] smn = s.split(" ");
			
			if (smn.length == 2) {
				try { if ((Integer.parseInt(smn[0]) / 10000) != Material.AIR.getId()) inventory.put(Integer.parseInt(smn[0]), Integer.parseInt(smn[1])); }
				catch (NumberFormatException e) {}
				
			}
			
		}
		
	}
	
	/**
	 * Adds stacks to robotnik's inventory
	 * @param robotInventory - ItemStacks to be added
	 */
	public void addInventory(ArrayList<ItemStack> robotInventory) {
		for (ItemStack stack : robotInventory) {
			addInventory(stack);
			
		}
		
	}
	
	/**
	 * Clears all items on robotnik
	 */
	public void clearInventory() {
		pickedUpStack.clear();
		inventory.clear();
		
	}
	
	/**
	 * Dumps picked up stack of blocks into main inventory
	 */
	public void combineBlocks() {
		while (!pickedUpStack.empty()) {
			MaterialData md = pickedUpStack.pop();
			ItemStack item = new ItemStack(md.getItemTypeId());
			item.setData(md);

			addInventory(item);
			
		}
		
	}
	
	/**
	 * Adds a single stack of items to the inventory
	 * @param stack - ItemStack to be added
	 */
	public void addInventory(ItemStack stack) {
		int md = stack.getTypeId() * 10000 + stack.getData().getData();
		if (inventory.containsKey(md)) {
			inventory.put(md, inventory.get(md) + stack.getAmount());
			
		} else {
			inventory.put(md, stack.getAmount());
			
		}
		
	}
	//##################################################################################
	public boolean hasItem(int typeId, byte data) {
		//If data is -1 means we ignore data value
		if (data != -1) return inventory.containsKey(typeId * 10000 + data);
		
		for (Integer item : inventory.keySet()) if (typeId == item / 10000) return true;
		
		return false;
		
	}
	
	public int countItem(int typeId, byte data) {
		//If data is -1 means we ignore data value
		if (data != -1) return inventory.get(typeId * 10000 + data);
		
		int count = 0;
		for (Integer item : inventory.keySet()) if (typeId == item / 10000) count += inventory.get(item);
		
		return count;
		
	}
	//##################################################################################
}
