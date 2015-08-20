package net.moc.CodeBlocks;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SaveDataEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	public SaveDataEvent() {}
	
	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    
}
