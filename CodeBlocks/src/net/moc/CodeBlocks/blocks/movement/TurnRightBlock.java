package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class TurnRightBlock extends MovementBlock {
	private static String texture = "movement/turnright.png";
	private static String name = "Turn Right";
	
	public TurnRightBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
