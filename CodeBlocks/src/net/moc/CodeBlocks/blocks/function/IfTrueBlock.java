package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class IfTrueBlock extends FunctionalBlock {
	private static String texture = "function/iftrue.png";
	private static String name = "If True";
	
	public IfTrueBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
