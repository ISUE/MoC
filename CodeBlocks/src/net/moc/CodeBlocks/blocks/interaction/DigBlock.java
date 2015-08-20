package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class DigBlock extends InteractionBlock {
	private static String texture = "interaction/dig.png";
	private static String name = "Dig";
	
	public DigBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
