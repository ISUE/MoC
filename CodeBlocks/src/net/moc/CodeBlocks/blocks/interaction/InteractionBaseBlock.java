package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class InteractionBaseBlock extends InteractionBlock {
	private static String texture = "interaction/ibase.png";
	private static String name = "Interaction";
	
	public InteractionBaseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
