package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class PickUpBlock extends InteractionBlock {
	private static String texture = "interaction/pickup.png";
	private static String name = "Pick Up";
	
	public PickUpBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
