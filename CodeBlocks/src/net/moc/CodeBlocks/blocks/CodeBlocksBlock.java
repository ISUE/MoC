package net.moc.CodeBlocks.blocks;

import net.moc.CodeBlocks.CodeBlocks;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.GenericCuboidBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class CodeBlocksBlock extends GenericCubeCustomBlock {
	private static int textureSize = 128;
	private static int textureSprite = 64;
	private static int[] textureLayout = {2,0,3,2,1,2}; //bottom, face, face, face, face, top
	private static int lightLevel = 10;
	
	public CodeBlocksBlock(JavaPlugin plugin, String name, String texture) {
		super(plugin, "CB " + name, Material.DIRT.getId(), "", textureSize);
		
		//Create design
		setBlockDesign(new GenericCubeBlockDesign(plugin, new Texture(plugin, ((CodeBlocks)plugin).getBlockImageURL() + texture, textureSize, textureSize, textureSprite), textureLayout));
		
		//Set light level
		setLightLevel(lightLevel);
		
	}

	public CodeBlocksBlock(JavaPlugin plugin, String name, String texture, int isFlat) {
		super(plugin, "CB " + name, Material.SNOW.getId(), "", textureSize);
		
		Texture t = new Texture(plugin, ((CodeBlocks)plugin).getBlockImageURL() + texture, textureSize, textureSize, textureSprite);
		
		//Create design
		setBlockDesign(new GenericCuboidBlockDesign(plugin, t, textureLayout, 0f, 0f, 0f, 1f, 0.1f, 1f));
		
		//Set light level
		setLightLevel(lightLevel);
		
	}

	public CodeBlocksBlock(JavaPlugin plugin, String name, String texture, boolean rotate) {
		super(plugin, "CB " + name, Material.DIRT.getId(), "", textureSize, rotate);
		
		//Create design
		setBlockDesign(new GenericCubeBlockDesign(plugin, new Texture(plugin, ((CodeBlocks)plugin).getBlockImageURL() + texture, textureSize, textureSize, textureSprite), textureLayout));
		
		//Set light level
		setLightLevel(lightLevel);
		
	}

}
