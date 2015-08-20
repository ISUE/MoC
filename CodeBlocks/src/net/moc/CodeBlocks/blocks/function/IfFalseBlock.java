package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class IfFalseBlock extends FunctionalBlock {
	private static String texture = "function/iffalse.png";
	private static String name = "If False";
	
	public IfFalseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
