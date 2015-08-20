package net.moc.CodeBlocks.gui;

import net.moc.CodeBlocks.CodeBlocks;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MainWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 160, screenBufferY = 60;
	private float scaleLarge = 1.2F;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle;
	
	//Window buttons
	private GenericButton buttonFunctions, buttonRobots, buttonBlocks, buttonFeedback;
	private GenericButton buttonClose;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public MainWindow(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel(this.plugin.getDescription().getName());
		this.labelTitle.setScale(this.scaleLarge);
		
		//Buttons
		this.buttonFunctions = new GenericButton("Functions");
		this.buttonFunctions.setTooltip("Show player's functions");
		this.buttonFunctions.setHoverColor(this.hoverColor);
		
		this.buttonRobots = new GenericButton("Robots");
		this.buttonRobots.setTooltip("Show player's robots");
		this.buttonRobots.setHoverColor(this.hoverColor);
		
		this.buttonBlocks = new GenericButton("Blocks");
		this.buttonBlocks.setTooltip("Get blocks for function building");
		this.buttonBlocks.setHoverColor(this.hoverColor);
		
		this.buttonFeedback = new GenericButton("Feedback");
		this.buttonFeedback.setTooltip("Submit feedback");
		this.buttonFeedback.setHoverColor(this.hoverColor);
		
		this.buttonClose = new GenericButton("Close");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.labelTitle, this.buttonFunctions, this.buttonFeedback);
		attachWidgets(plugin, this.buttonRobots, this.buttonBlocks , this.buttonClose);

		//Initialize
		this.initialize();
		
	}

	//================================================================================================================
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX + windowWidth / 2; 
        int upLeftY = this.screenBufferY;
        
		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX - windowWidth / 2).setY(upLeftY);
		
		//Title
		this.labelTitle.setHeight(15).setWidth(windowWidth);
		this.labelTitle.setX(upLeftX+5 - windowWidth / 2).setY(upLeftY+5);
		
		//Window buttons
		this.buttonFunctions.setWidth(100).setHeight(15);
		this.buttonFunctions.setX(upLeftX-50).setY(upLeftY+20);
		
		this.buttonRobots.setWidth(100).setHeight(15);
		this.buttonRobots.setX(upLeftX-50).setY(upLeftY+40);
		
		this.buttonBlocks.setWidth(100).setHeight(15);
		this.buttonBlocks.setX(upLeftX-50).setY(upLeftY+60);
		
		this.buttonFeedback.setWidth(100).setHeight(15);
		this.buttonFeedback.setX(upLeftX-50).setY(upLeftY+80);
		if (plugin.getConfiguration().doFeedback()) buttonFeedback.setVisible(true);
		else buttonFeedback.setVisible(false);
		
		this.buttonClose.setWidth(100).setHeight(15);
		this.buttonClose.setX(upLeftX-50).setY(upLeftY+100);
		
	}

	//================================================================================================================
	//Open the GUI
	public void open(){
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
	//================================================================================================================
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close buttons
		if (button.equals(this.buttonClose)) { closeWindow(); return; }
		
		//Functions button
		if (button.equals(this.buttonFunctions)) { closeWindow(); this.plugin.getGUI().displayFunctionBrowser(player); return; }
		
		//Robots button
		if (button.equals(this.buttonRobots)) { closeWindow(); this.plugin.getGUI().displayRobotBrowser(player); return; }
		
		//Blocks button
		if (button.equals(this.buttonBlocks)) { closeWindow(); this.plugin.getGUI().displayBlocks(player); return; }
		
		//Feedback button
		if (button.equals(this.buttonFeedback)) { closeWindow(); this.plugin.getGUI().displayFeedback(player); return; }
		
	}
	
}
