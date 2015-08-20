package net.moc.CodeBlocks.blocks;

import org.bukkit.plugin.java.JavaPlugin;

public class RobotBlock extends CodeBlocksBlock {
	private static String texture = "robot.png";
	private static String name = "Robot";
	
	public RobotBlock(JavaPlugin plugin) {
		super(plugin, name, texture, true);
		
	}

}
