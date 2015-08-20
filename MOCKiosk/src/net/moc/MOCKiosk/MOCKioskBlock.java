package net.moc.MOCKiosk;

import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class MOCKioskBlock extends GenericCubeCustomBlock{
	private static String texture = "http://minecraft.dmg2.net/MOCKiosk/kiosk.png";
	private static String name = "Kiosk";
	private static int textureSize = 64;
	private static int textureSprite = 32;
	private static int[] textureLayout = {2,0,0,0,0,1};
	private static int lightLevel = 10;
	
	public MOCKioskBlock(MOCKiosk plugin) {
		super(plugin, name, "", textureSize);
		
		this.setBlockDesign(new GenericCubeBlockDesign(plugin, new Texture(plugin, texture, textureSize, textureSize, textureSprite), textureLayout));
		this.setLightLevel(lightLevel);
		
	}

}
