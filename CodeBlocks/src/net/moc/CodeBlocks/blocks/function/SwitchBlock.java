package net.moc.CodeBlocks.blocks.function;

import org.bukkit.plugin.java.JavaPlugin;

public class SwitchBlock  extends FunctionalBlock {
	private static String texture = "function/switch.png";
	private static String name = "Switch";
	
	public SwitchBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
