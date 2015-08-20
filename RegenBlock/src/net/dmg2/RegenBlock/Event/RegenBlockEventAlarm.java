package net.dmg2.RegenBlock.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegenBlockEventAlarm extends Event {
	private static final HandlerList handlers = new HandlerList();
    private String alarmMessage;
    private String worldName;
    private int alarmRadius, x, y, z;
 
    public RegenBlockEventAlarm(int alarmRadius, String alarmMessage, String worldName, int x, int y, int z) {
        this.alarmRadius = alarmRadius;
        this.alarmMessage = alarmMessage;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        
    }
 
    public String getAlarmMessage() { return alarmMessage; }
	public String getWorldName() { return worldName; }
	public int getAlarmRadius() { return alarmRadius; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
