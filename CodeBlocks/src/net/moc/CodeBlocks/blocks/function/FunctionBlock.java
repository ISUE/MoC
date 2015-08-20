package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class FunctionBlock  extends FunctionalBlock {
	private static String texture = "function/function.png";
	private static String name = "Function";
	
	public FunctionBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
