package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class CaseBlock  extends FunctionalBlock {
	private static String texture = "function/case.png";
	private static String name = "Case";
	
	public CaseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
