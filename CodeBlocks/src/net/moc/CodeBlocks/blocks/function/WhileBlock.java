package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class WhileBlock  extends FunctionalBlock {
	private static String texture = "function/while.png";
	private static String name = "While";
	
	public WhileBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
