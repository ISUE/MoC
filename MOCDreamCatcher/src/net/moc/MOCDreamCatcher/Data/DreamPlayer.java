package net.moc.MOCDreamCatcher.Data;

import java.util.ArrayList;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class DreamPlayer {
	//====================================================
	public enum PlayerState{AWAKE, DREAMING, EDITING, WAKING}
	
	private MOCDreamCatcher plugin;
	private String playerName;

	private PlayerState state;
	private ArrayList<Thought> thoughts;
	private Dream dream;
	private Inventory wakeInventory;
	private Location wakeLocation;
	//====================================================
	
	//###########################################################################
	public DreamPlayer(String playerName, MOCDreamCatcher plugin, ArrayList<Thought> thoughts, Dream dream, PlayerState state, Inventory wakeInventory, Location wakeLocation) {
		this.playerName = playerName;
		this.plugin = plugin;
		
		this.thoughts = thoughts;
		this.dream = dream;
		this.state = state;
		
		this.wakeInventory = wakeInventory;
		this.wakeLocation = wakeLocation;
		
	}
	
	public DreamPlayer(String playerName, MOCDreamCatcher plugin) {
		this.playerName = playerName;
		this.plugin = plugin;
		
		this.thoughts = new ArrayList<Thought>();
		this.dream = null;
		this.state = PlayerState.AWAKE;
		
	}
	//###########################################################################

	//______________________________________________________
	public String getName() { return playerName; }
	
	//______________________________________________________
	public ArrayList<Thought> getThoughts() { return thoughts; }
	public Thought getThought(String thoughtName) { for (Thought t : thoughts) if (t.getName().equalsIgnoreCase(thoughtName)) { return t; } return null; }
	public void removeThought(String name) { for (Thought t : thoughts) if (t.getName().equalsIgnoreCase(name)) { thoughts.remove(t); plugin.getConfiguration().removeDreamPlayerThought(playerName, t.getName()); break; } }
	public void addThought(Thought thought) { thoughts.add(thought); plugin.getConfiguration().setDreamPlayerThought(playerName, thought); }
	
	//______________________________________________________
	public Dream getDream() { return dream; }
	public void setDream(Dream dream) { this.dream = dream; plugin.getConfiguration().setDreamPlayerDream(dream, playerName); }
	public void clearDream() { dream = null; plugin.getConfiguration().removeDreamPlayerDream(playerName); }

	//______________________________________________________
	public PlayerState getState() { return state; }
	public void setState(PlayerState state) { this.state = state; plugin.getConfiguration().setDreamPlayerState(playerName, state);	}
	
	//______________________________________________________
	public Inventory getWakeInventory() { return wakeInventory; }
	public Location getWakeLocation() { return wakeLocation; }
	
	public void setWakeInformation(Location wakeLocation, Inventory wakeInventory) {
		this.wakeLocation = wakeLocation;
		if (wakeInventory == null) this.wakeInventory = null;
		else {
			this.wakeInventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER); 
			this.wakeInventory.setContents(wakeInventory.getContents());
			
		}
		
		plugin.getConfiguration().setDreamPlayerWakeInfo(playerName, this.wakeInventory, this.wakeLocation);
		
	}

}
