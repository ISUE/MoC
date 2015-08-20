package net.moc.CodeBlocks.blocks.attack;

import org.bukkit.plugin.java.JavaPlugin;

public class AttackFarBlock extends AttackBlock {
	private static String texture = "attack/attackfar.png";
	private static String name = "Attack Far";
	
	public AttackFarBlock(JavaPlugin plugin) {
		super(plugin, name, texture);
		
	}

}
