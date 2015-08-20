package net.dmg2.rTPS;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class rTPSQueueEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
    public rTPSQueueEvent() {}
 	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

}
