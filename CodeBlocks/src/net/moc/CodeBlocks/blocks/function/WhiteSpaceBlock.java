package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class WhiteSpaceBlock  extends FunctionalBlock {
	private static String texture = "function/whitespace.png";
	private static String name = "[space]";
	
	public WhiteSpaceBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
