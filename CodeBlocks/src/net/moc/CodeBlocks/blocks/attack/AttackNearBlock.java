package net.moc.CodeBlocks.blocks.attack;

import org.bukkit.plugin.java.JavaPlugin;

public class AttackNearBlock extends AttackBlock {
	private static String texture = "attack/attacknear.png";
	private static String name = "Attack Near";
	
	public AttackNearBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
