package net.moc.CodeBlocks.blocks.math;

import org.bukkit.plugin.java.JavaPlugin;

public class SetBlock extends MathBlock {
	private static String texture = "math/set.png";
	private static String name = "Set";
	
	public SetBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
