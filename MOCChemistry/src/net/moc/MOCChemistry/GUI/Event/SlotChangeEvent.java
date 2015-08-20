package net.moc.MOCChemistry.GUI.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SlotChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	
    public SlotChangeEvent(String playerName) {
    	this.playerName = playerName;
    }
 
    public HandlerList getHandlers() { return handlers; }
 
    public static HandlerList getHandlerList() { return handlers; }

	public String getPlayerName() { return playerName; }
    
}
