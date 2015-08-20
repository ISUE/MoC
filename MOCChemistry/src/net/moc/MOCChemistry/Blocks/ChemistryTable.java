package net.moc.MOCChemistry.Blocks;

import net.moc.MOCChemistry.MOCChemistry;

import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class ChemistryTable extends GenericCubeCustomBlock{
	private static String texture = "http://minecraft.dmg2.net/MOCChemistry/chemistrytable.png";
	private static String name = "Chemistry Table";
	private static int textureSize = 64;
	private static int textureSprite = 32;
	private static int[] textureLayout = {2,0,0,0,0,1};
	private static int lightLevel = 10;
	
	public ChemistryTable(MOCChemistry plugin) {
		super(plugin, name, "", textureSize);
		
		this.setBlockDesign(new GenericCubeBlockDesign(plugin, new Texture(plugin, texture, textureSize, textureSize, textureSprite), textureLayout));
		this.setLightLevel(lightLevel);
		
	}

}
