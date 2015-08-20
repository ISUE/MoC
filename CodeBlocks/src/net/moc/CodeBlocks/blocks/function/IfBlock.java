package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class IfBlock  extends FunctionalBlock {
	private static String texture = "function/if.png";
	private static String name = "If";
	
	public IfBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
