package net.moc.CodeBlocks.workspace.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VirtualMachineTickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String playerName;
	
	public VirtualMachineTickEvent(String playerName) { this.playerName = playerName; }
	
	public String getPlayerName() { return this.playerName; }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    
}
