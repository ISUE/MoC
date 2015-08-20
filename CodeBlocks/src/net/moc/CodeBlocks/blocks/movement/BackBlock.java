package net.moc.CodeBlocks.blocks.movement;

import org.bukkit.plugin.java.JavaPlugin;

public class BackBlock extends MovementBlock {
	private static String texture = "movement/back.png";
	private static String name = "Back";
	
	public BackBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
