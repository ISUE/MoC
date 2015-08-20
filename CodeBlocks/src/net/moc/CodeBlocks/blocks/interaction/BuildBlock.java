package net.moc.CodeBlocks.blocks.interaction;

import org.bukkit.plugin.java.JavaPlugin;

public class BuildBlock extends InteractionBlock {
	private static String texture = "interaction/build.png";
	private static String name = "Build";
	
	public BuildBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
