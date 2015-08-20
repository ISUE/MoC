package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class DestroyBlock extends InteractionBlock {
	private static String texture = "interaction/destroy.png";
	private static String name = "Destroy";
	
	public DestroyBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
