package moc.DreamCrafter.gui.widget;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCComboBoxSelectionEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
    private ListWidget listWidget;
    private String message;
    private SpoutPlayer player;
    private Screen screen;
 
    public MOCComboBoxSelectionEvent(SpoutPlayer player, Screen screen, ListWidget listWidget, String message) {
        this.listWidget = listWidget;
        this.message = message;
        this.player = player;
        this.screen = screen;
    }

    public ListWidget getListWidget() { return this.listWidget; }
    public SpoutPlayer getPlayer() { return player; }
	public Screen getScreen() { return screen; }
	
	public String getMessage() { return this.message; }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

}
