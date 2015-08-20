package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class DownBlock extends MovementBlock {
	private static String texture = "movement/down.png";
	private static String name = "Down";
	
	public DownBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
