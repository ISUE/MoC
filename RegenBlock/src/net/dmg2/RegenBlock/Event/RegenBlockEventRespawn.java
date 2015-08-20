package net.dmg2.RegenBlock.Event;

import java.util.ArrayList;

import net.dmg2.RegenBlock.RegenBlockBlock;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegenBlockEventRespawn extends Event {
	private static final HandlerList handlers = new HandlerList();
	private ArrayList<RegenBlockBlock> blocks;
 
    public RegenBlockEventRespawn(ArrayList<RegenBlockBlock> blocks) { this.blocks = blocks; }

	public ArrayList<RegenBlockBlock> getBlocks() { return blocks; }

	public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

}
