package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class RightBlock extends MovementBlock {
	private static String texture = "movement/right.png";
	private static String name = "Right";
	
	public RightBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
