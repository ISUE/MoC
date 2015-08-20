package net.moc.CodeBlocks.blocks.attack;

import org.bukkit.plugin.java.JavaPlugin;

public class AttackBaseBlock extends AttackBlock {
	private static String texture = "attack/abase.png";
	private static String name = "Attack";
	
	public AttackBaseBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
