package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class ForwardBlock extends MovementBlock {
	private static String texture = "movement/forward.png";
	private static String name = "Forward";

	public ForwardBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
