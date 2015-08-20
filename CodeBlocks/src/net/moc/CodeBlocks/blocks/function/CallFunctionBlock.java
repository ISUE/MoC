package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class CallFunctionBlock  extends FunctionalBlock {
	private static String texture = "function/callfunction.png";
	private static String name = "Call Function";
	
	public CallFunctionBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
