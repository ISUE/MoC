package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class TurnLeftBlock extends MovementBlock {
	private static String texture = "movement/turnleft.png";
	private static String name = "Turn Left";
	
	public TurnLeftBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
