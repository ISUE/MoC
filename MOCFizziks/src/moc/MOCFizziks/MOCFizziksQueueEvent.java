package moc.MOCFizziks;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MOCFizziksQueueEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
    private String message;
 
    public MOCFizziksQueueEvent(String example) {
        message = example;
    }
 
    public String getMessage() {
        return message;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
