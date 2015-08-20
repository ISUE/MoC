package net.moc.CodeBlocks.blocks;

import org.bukkit.plugin.java.JavaPlugin;

public class ProgramCounterBlock  extends CodeBlocksBlock {
	private static String texture = "function/pc.png";
	private static String name = "Position";
	
	public ProgramCounterBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
