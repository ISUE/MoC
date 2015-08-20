package net.moc.CodeBlocks.blocks.math;

import org.bukkit.plugin.java.JavaPlugin;

public class MathBaseBlock extends MathBlock {
	private static String texture = "math/m2base.png";
	private static String name = "Math";
	
	public MathBaseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
