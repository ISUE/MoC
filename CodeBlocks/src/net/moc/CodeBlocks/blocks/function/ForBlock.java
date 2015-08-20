package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class ForBlock  extends FunctionalBlock {
	private static String texture = "function/for.png";
	private static String name = "For";
	
	public ForBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
