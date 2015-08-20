package net.moc.CodeBlocks.workspace;

import java.util.Collection;
import java.util.HashMap;
import net.moc.CodeBlocks.workspace.command.MovementCommand.Direction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RobotHistory {
	//=========================================================================
	private HashMap<Material, Integer> blocksBuild;
	private HashMap<Material, Integer> blocksDig;
	private HashMap<Material, Integer> blocksDestroy;
	private HashMap<Material, Integer> blocksPickUp;
	private HashMap<Material, Integer> blocksPlace;
	private HashMap<Direction, Integer> blocksMove;
	private int powerUps;
	
	//=========================================================================
	public void set(String type, String value) {
		//Types - Build, Dig, Destroy, PickUp, Place, Move
		switch (type) {
		case "Build":
			blocksBuild = parseMaterial(value);
			break;
		case "Dig":
			blocksDig = parseMaterial(value);
			break;
		case "Destroy":
			blocksDestroy = parseMaterial(value);
			break;
		case "PickUp":
			blocksPickUp = parseMaterial(value);
			break;
		case "Place":
			blocksPlace = parseMaterial(value);
			break;
		case "Move":
			blocksMove = parseDirection(value);
			break;
		case "PowerUps":
			powerUps = 0;
			try { powerUps = Integer.parseInt(value); } catch(NumberFormatException e) {}
			break;
			
		}
		
	}
	
	public HashMap<String, String> get() {
		HashMap<String, String> historyString = new HashMap<String, String>();
		
		if (blocksBuild.size() > 0) {
			String value = "";
			
			for (Material mat : blocksBuild.keySet()) {
				value += mat + " " + blocksBuild.get(mat) + "\n";
				
			}
			
			historyString.put("Build", value);
			
		}
		
		if (blocksDig.size() > 0) {
			String value = "";
			
			for (Material mat : blocksDig.keySet()) {
				value += mat + " " + blocksDig.get(mat) + "\n";
				
			}
			
			historyString.put("Dig", value);
			
		}
		
		if (blocksDestroy.size() > 0) {
			String value = "";
			
			for (Material mat : blocksDestroy.keySet()) {
				value += mat + " " + blocksDestroy.get(mat) + "\n";
				
			}
			
			historyString.put("Destroy", value);
			
		}
		
		if (blocksPickUp.size() > 0) {
			String value = "";
			
			for (Material mat : blocksPickUp.keySet()) {
				value += mat + " " + blocksPickUp.get(mat) + "\n";
				
			}
			
			historyString.put("PickUp", value);
			
		}
		
		if (blocksPlace.size() > 0) {
			String value = "";
			
			for (Material mat : blocksPlace.keySet()) {
				value += mat + " " + blocksPlace.get(mat) + "\n";
				
			}
			
			historyString.put("Place", value);
			
		}
		
		if (blocksMove.size() > 0) {
			String value = "";
			
			for (Direction dir : blocksMove.keySet()) {
				value += dir + " " + blocksMove.get(dir) + "\n";
				
			}
			
			historyString.put("Move", value);
			
		}
		
		if (powerUps > 0) historyString.put("PowerUps", powerUps + "");
		
		return historyString;
		
	}

	private HashMap<Direction, Integer> parseDirection(String value) {
		HashMap<Direction, Integer> retval = new HashMap<Direction, Integer>();
		
		String[] parts = value.split("\n");
		
		for (String part : parts) {
			String[] s = part.split(" ");
			retval.put(Direction.valueOf(s[0]), Integer.parseInt(s[1]));
			
		}
		
		return retval;
		
	}

	private HashMap<Material, Integer> parseMaterial(String value) {
		HashMap<Material, Integer> retval = new HashMap<Material, Integer>();
		
		String[] parts = value.split("\n");
		
		for (String part : parts) {
			String[] s = part.split(" ");
			retval.put(Material.valueOf(s[0]), Integer.parseInt(s[1]));
			
		}
		
		return retval;
		
	}

	//=========================================================================
	public RobotHistory() {
		blocksBuild = new HashMap<Material, Integer>();
		blocksDig = new HashMap<Material, Integer>();
		blocksDestroy = new HashMap<Material, Integer>();
		blocksPickUp = new HashMap<Material, Integer>();
		blocksPlace = new HashMap<Material, Integer>();
		blocksMove = new HashMap<Direction, Integer>();
		powerUps = 0;
		
	}

	//=========================================================================
	public void recordBuild(Material material) {
		Integer current = blocksBuild.get(material);
		if (current == null) blocksBuild.put(material, 1);
		else blocksBuild.put(material, current + 1);
		
	}

	//=========================================================================
	public void recordDig(Collection<ItemStack> drops) {
		for (ItemStack drop : drops) {
			Integer current = blocksDig.get(drop.getType());
			if (current == null) blocksDig.put(drop.getType(), 1);
			else blocksDig.put(drop.getType(), current + drop.getAmount());
			
		}
		
	}

	//=========================================================================
	public void recordDestroy(Material material) {
		Integer current = blocksDestroy.get(material);
		if (current == null) blocksDestroy.put(material, 1);
		else blocksDestroy.put(material, current + 1);
		
	}

	//=========================================================================
	public void recordPickup(Material material) {
		Integer current = blocksPickUp.get(material);
		if (current == null) blocksPickUp.put(material, 1);
		else blocksPickUp.put(material, current + 1);
		
	}

	//=========================================================================
	public void recordPlace(Material material) {
		Integer current = blocksPlace.get(material);
		if (current == null) blocksPlace.put(material, 1);
		else blocksPlace.put(material, current + 1);
		
	}

	//=========================================================================
	public void recordMove(Direction direction) {
		Integer current = blocksMove.get(direction);
		if (current == null) blocksMove.put(direction, 1);
		else blocksMove.put(direction, current + 1);
		
	}

	public void recordPowerUp() { powerUps++; }

}
