package net.moc.MOCDreamCatcher.GUI;

import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.Data.Thought;
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

public class ThoughtWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCDreamCatcher plugin;
	private SpoutPlayer player;
	private int screenBufferX = 150, screenBufferY = 68;
	private float scaleLarge = 1.0F;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;

	//Title
	private GenericLabel labelTitle;

	//Thought buttons
	private GenericButton buttonStopThinking, buttonSetInventory, buttonSetStart, buttonSetEnd;

	//Window buttons
	private GenericButton buttonClose, buttonHelp;

	private Thought thought;

	//----------------------------------------------------------------
	//================================================================================================================

	public ThoughtWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
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

		//Thought buttons
		this.buttonSetInventory = new GenericButton("Set Inventory");
		this.buttonSetInventory.setTooltip("Sets initial inventory for the dream");
		this.buttonSetInventory.setHoverColor(hoverColor);

		this.buttonSetStart = new GenericButton("Set Start");
		this.buttonSetStart.setTooltip("Sets start location");
		this.buttonSetStart.setHoverColor(hoverColor);

		this.buttonSetEnd = new GenericButton("Set End");
		this.buttonSetEnd.setTooltip("Sets end location");
		this.buttonSetEnd.setHoverColor(hoverColor);

		this.buttonStopThinking = new GenericButton("Stop thinking");
		this.buttonStopThinking.setTooltip("Return back to the real world");
		this.buttonStopThinking.setHoverColor(hoverColor);

		//Window buttons
		this.buttonClose = new GenericButton("Cancel");
		this.buttonClose.setTooltip("Close Window");
		this.buttonClose.setHoverColor(hoverColor);

		this.buttonHelp = new GenericButton("?");
		this.buttonHelp.setTooltip("Help");
		this.buttonHelp.setHoverColor(hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, buttonSetInventory, buttonSetStart, buttonSetEnd);
		attachWidgets(plugin, buttonStopThinking, buttonClose, buttonHelp);

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

		//Thought buttons
		buttonSetInventory.setX(upLeftX + 5).setY(upLeftY + 20);
		buttonSetInventory.setWidth(windowWidth - 10).setHeight(15);

		buttonSetStart.setX(upLeftX + 5).setY(upLeftY + 35);
		buttonSetStart.setWidth(windowWidth - 10).setHeight(15);

		buttonSetEnd.setX(upLeftX + 5).setY(upLeftY + 50);
		buttonSetEnd.setWidth(windowWidth - 10).setHeight(15);

		buttonStopThinking.setX(upLeftX + 5).setY(upLeftY + 65);
		buttonStopThinking.setWidth(windowWidth - 10).setHeight(15);

		//Window buttons
		buttonClose.setX(upLeftX + 5).setY(upLeftY + 80);
		buttonClose.setWidth(windowWidth - 10).setHeight(15);

		buttonHelp.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		buttonHelp.setWidth(15).setHeight(15);
		
	}

	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonClose)) { closeWindow(); return; }

		if (button.equals(buttonHelp)) { closeWindow(); plugin.getGUI().displayHelpWindow(player); return; }
		
		if (button.equals(buttonSetInventory)) {
			closeWindow();
			
			thought.setInventory(player.getInventory());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			plugin.getLog().sendPlayerNormal(player.getName(), "Inventory set");
			
			return;
			
		}

		if (button.equals(buttonSetStart)) {
			closeWindow();
			
			thought.setStart(player.getLocation());
			plugin.getDreamNet().getExternalPluginManager().getMultiverse().getMVWorldManager().getMVWorld(player.getLocation().getWorld()).setSpawnLocation(player.getLocation());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			
			plugin.getLog().sendPlayerNormal(player.getName(), "Start location set");
			
			return;
			
		}

		if (button.equals(buttonSetEnd)) {
			closeWindow();
			
			thought.setEnd(player.getLocation());
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), thought);
			
			plugin.getLog().sendPlayerNormal(player.getName(), "End location set");
			
			return;
			
		}

		if (button.equals(buttonStopThinking)) {
			closeWindow();
			
			plugin.getDreamNet().endThought(player.getName(), thought.getName());
			
			return;
			
		}

	}

	//================================================================================================================
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);

		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));

	}


	//================================================================================================================
	public void open() {
		try { thought = plugin.getDreamNet().getPlayer(player.getName()).getThought(player.getLocation().getWorld().getName().split("_")[player.getLocation().getWorld().getName().split("_").length-1]); }
		catch (ArrayIndexOutOfBoundsException e) { return; }
		
		initialize();
		
		player.getMainScreen().attachPopupScreen(this);

		refresh();

	}

	//================================================================================================================
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }

	}
	
}
