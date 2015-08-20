package net.moc.MOCChemistry.Blocks;

import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class BlankCustomBlock extends GenericCubeCustomBlock{
	private static int textureSize = 64;
	private static int textureSprite = 32;
	private static int[] textureLayout = {2,0,0,0,0,1};
	
	public BlankCustomBlock(JavaPlugin plugin, String name, String texture) {
		super(plugin, name, "", textureSize);
		
		this.setBlockDesign(new GenericCubeBlockDesign(plugin, new Texture(plugin, texture, textureSize, textureSize, textureSprite), textureLayout));
		
	}

}

