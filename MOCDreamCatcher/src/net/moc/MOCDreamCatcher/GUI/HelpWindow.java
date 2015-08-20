package net.moc.MOCDreamCatcher.GUI;

import net.moc.MOCDreamCatcher.MOCDreamCatcher;

import org.bukkit.ChatColor;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class HelpWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCDreamCatcher plugin;
	private SpoutPlayer player;
	private int screenBufferX = 5, screenBufferY = 5;
	private float scaleLarge = 1.0F;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;

	//Title
	private GenericLabel labelTitle;

	//Image
	private GenericTexture textureHelpInformation;
	private String imageURL = "http://isue-server.eecs.ucf.edu/mocdreamcatcher/help.jpg";
	private String tutorialURL = "http://www.mindsofchimera.com/plugin_info";
	private String helpChatMessage = "Please visit [ " + ChatColor.GOLD + tutorialURL + ChatColor.RESET + ChatColor.AQUA + " ] for a complete plugin command list. Click the link to open it in the browser.";
	
	//Window buttons
	private GenericButton buttonClose;
	
	//----------------------------------------------------------------
	//================================================================================================================

	public HelpWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
		this.plugin = plugin;
		this.player = player;

		//Set window transparent
		this.setTransparent(true);

		//Background
		this.background = new GenericGradient(backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel(plugin.getDescription().getName());
		this.labelTitle.setScale(scaleLarge);
		
		//Image
		this.textureHelpInformation = new GenericTexture(imageURL);

		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, textureHelpInformation, buttonClose);

		//Initialize
		this.initialize();

	}

	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - screenBufferY * 2;
		int upLeftX = screenBufferX; 
		int upLeftY = screenBufferY;

		//Background
		background.setHeight(windowHeight).setWidth(windowWidth);
		background.setX(upLeftX).setY(upLeftY);

		//Title
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		labelTitle.setHeight(15).setWidth(windowWidth);

		//Image
		textureHelpInformation.setX(upLeftX+5).setY(upLeftY+20);
		textureHelpInformation.setHeight(windowHeight - 25).setWidth(windowWidth-10);
		
		//Window buttons
		buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		buttonClose.setWidth(15).setHeight(15);

	}

	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonClose)) { closeWindow(); return; }

	}

	//================================================================================================================
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);

		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		plugin.getLog().sendPlayerNormal(player.getName(), helpChatMessage );

	}


	//================================================================================================================
	public void open() {
		initialize();
		
		if (plugin.getConfiguration().isFirstTimeUser(player.getName())) plugin.getConfiguration().setNotFirstTimeUser(player.getName());
		
		player.getMainScreen().attachPopupScreen(this);
		
		plugin.getLog().sendPlayerNormal(player.getName(), helpChatMessage);

		refresh();

	}

	//================================================================================================================
	private void refresh() { setDirty(true); for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); } }
	

}
