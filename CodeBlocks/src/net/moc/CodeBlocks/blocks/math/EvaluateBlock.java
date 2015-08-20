package net.moc.CodeBlocks.blocks.math;

import org.bukkit.plugin.java.JavaPlugin;

public class EvaluateBlock extends MathBlock {
	private static String texture = "math/evaluate.png";
	private static String name = "Evaluate";
	
	public EvaluateBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
