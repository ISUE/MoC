package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class PlaceBlock extends InteractionBlock {
	private static String texture = "interaction/place.png";
	private static String name = "Place";
	
	public PlaceBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
