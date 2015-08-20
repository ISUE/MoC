package net.dmg2.OP2C;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OP2CEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String playerName;
 
    public OP2CEvent(String playerName) { this.playerName = playerName; }
	public String getPlayerName() { return playerName; }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
	
}
