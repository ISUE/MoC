package net.moc.CodeBlocks.gui;

import net.moc.CodeBlocks.CodeBlocks;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Tutorial {
	//================================================================================================================
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private Color backgroundColor = new Color(20,70,110);
	
	private Gradient gradientBackground;
	//================================================================================================================
	
	
	
	//================================================================================================================
	public Tutorial(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);

		//Initialize
		initialize();

	}
	
	//========================================================================================================================
	public void initialize() {
        int upLeftX = 15;
        int upLeftY = 40;
        int windowWidth = 120;
        int windowHeight = 100;
        
		//Background
		gradientBackground.setWidth(windowWidth).setHeight(windowHeight);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
	}
	
	//========================================================================================================================
	public void open(){
		initialize();
		
		player.getMainScreen().attachWidget(plugin, gradientBackground);
		
	}
	
}
