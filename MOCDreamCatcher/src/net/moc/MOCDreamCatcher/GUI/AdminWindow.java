package net.moc.MOCDreamCatcher.GUI;

import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.Data.DreamPlayer;
import net.moc.MOCDreamCatcher.Data.Thought;
import net.moc.MOCDreamCatcher.GUI.widgets.MOCListWidget;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class AdminWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCDreamCatcher plugin;
	private SpoutPlayer player;
	private int screenBufferX = 120, screenBufferY = 15;
	private float scaleLarge = 1.2F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle;
	
	//Thoughts
	private MOCListWidget listThoughts;
	private GenericButton buttonForget;
	
	//Window buttons
	private GenericButton buttonClose, buttonHelp;
	
	private String statePublished = "Published";
	private String statePrivate = "Private";
	
	//----------------------------------------------------------------
	//================================================================================================================

	public AdminWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel(plugin.getDescription().getFullName());
		this.labelTitle.setScale(scaleLarge);
		
		//Thoughts
		this.listThoughts = new MOCListWidget();
		this.listThoughts.setTooltip("Browse your thoughts");
		this.listThoughts.setBackgroundColor(textFieldColor);
		
		this.buttonForget = new GenericButton("Forget");
		this.buttonForget.setTooltip("Remove a thought");
		this.buttonForget.setHoverColor(hoverColor);
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(hoverColor);
		
		this.buttonHelp = new GenericButton("?");
		this.buttonHelp.setTooltip("Help");
		this.buttonHelp.setHoverColor(hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle);
		attachWidgets(plugin, listThoughts, buttonForget, buttonClose, buttonHelp);
		
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
		
		//Thoughts
		listThoughts.setX(upLeftX+5).setY(upLeftY+25);
		listThoughts.setWidth(windowWidth - 10).setHeight(windowHeight - 50);
		
		buttonForget.setX(upLeftX + 5).setY(upLeftY + windowHeight - 20);
		buttonForget.setWidth(windowWidth - 10).setHeight(15);
		buttonForget.setEnabled(false);
		
		//Window buttons
		buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		buttonClose.setWidth(15).setHeight(15);
		
		buttonHelp.setX(upLeftX + windowWidth - 35).setY(upLeftY+5);
		buttonHelp.setWidth(15).setHeight(15);
		
		fillThoughts();
		
	}
	
	//================================================================================================================
	private void fillThoughts() {
		listThoughts.clearSelection();
		listThoughts.clear();
		
		for (DreamPlayer dp : plugin.getDreamNet().getPlayers()) {
			for (Thought thought : dp.getThoughts()) {
				String publishState;
				if (thought.isPublished()) publishState = statePublished ; else publishState = statePrivate;
				listThoughts.addItem(new ListWidgetItem(thought.getName(), dp.getName() + " " + publishState));
				
				
			}
			
		}
		
		refresh();
		
	}

	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonClose)) { closeWindow(); return; }
		
		if (button.equals(buttonHelp)) { closeWindow(); plugin.getGUI().displayHelpWindow(player); return; }
		
		//Remove thought
		if (button.equals(buttonForget)) {
			plugin.getDreamNet().removeThought(listThoughts.getSelectedItem().getText().split(" ")[0], listThoughts.getSelectedItem().getTitle());
			buttonForget.setEnabled(false);
			
			fillThoughts();
			return;
			
		}
		
	}

	//================================================================================================================
	public void onSelection(ListWidget listWidget) {
		if (listThoughts == listWidget) {
			if (listThoughts.getSelectedItem() == null) { buttonForget.setEnabled(false); }
			else { buttonForget.setEnabled(true); }
			
			refresh();
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
