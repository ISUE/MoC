package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class UpBlock extends MovementBlock {
	private static String texture = "movement/up.png";
	private static String name = "Up";
	
	public UpBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
