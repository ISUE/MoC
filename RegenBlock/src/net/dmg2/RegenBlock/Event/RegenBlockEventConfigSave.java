package net.dmg2.RegenBlock.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegenBlockEventConfigSave extends Event {
	private static final HandlerList handlers = new HandlerList();
 
    public RegenBlockEventConfigSave() { }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

}

