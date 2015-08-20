package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class MoveBlock extends MovementBlock {
	private static String texture = "movement/move.png";
	private static String name = "Move";
	
	public MoveBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
