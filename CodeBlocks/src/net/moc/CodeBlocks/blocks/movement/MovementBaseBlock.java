package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class MovementBaseBlock extends MovementBlock {
	private static String texture = "movement/mbase.png";
	private static String name = "Movement";
	
	public MovementBaseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
