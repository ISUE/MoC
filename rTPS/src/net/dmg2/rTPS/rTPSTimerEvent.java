package net.dmg2.rTPS;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class rTPSTimerEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private long startTime;
	
    public rTPSTimerEvent(long startTime) { this.startTime = startTime; }
    
    public long getStartTime() { return this.startTime; }
 	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    
}
