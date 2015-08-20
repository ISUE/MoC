package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class BranchBlock  extends FunctionalBlock {
	private static String texture = "function/branch.png";
	private static String name = "Branch";
	
	public BranchBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}