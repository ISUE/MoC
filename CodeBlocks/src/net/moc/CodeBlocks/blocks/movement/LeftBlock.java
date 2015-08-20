package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class LeftBlock extends MovementBlock {
	private static String texture = "movement/left.png";
	private static String name = "Left";
	
	public LeftBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
