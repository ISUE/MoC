package net.moc.CodeBlocks.blocks;

import org.bukkit.plugin.java.JavaPlugin;

public class PointerBlock  extends CodeBlocksBlock {
	private static String texture = "function/pc.png";
	private static String name = "Pointer";
	
	public PointerBlock(JavaPlugin plugin) {
		super(plugin, name, texture, 1);
		
	}

}
