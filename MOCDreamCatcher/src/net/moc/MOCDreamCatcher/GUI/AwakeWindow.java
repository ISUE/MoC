package net.moc.MOCDreamCatcher.GUI;

import java.util.ArrayList;
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
import org.getspout.spoutapi.gui.GenericWidget;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class AwakeWindow extends GenericPopup {
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
	
	//Tab buttons
	private GenericButton buttonDreams, buttonThoughts;
	
	//Dreams
	private MOCListWidget listDreams;
	private GenericButton buttonStartDream;
	
	//Thoughts
	private MOCListWidget listThoughts;
	private GenericButton buttonThink, buttonForget, buttonInvent, buttonPublish;
	
	//Window buttons
	private GenericButton buttonClose, buttonHelp;
	
	//Dream widgets
	private ArrayList<GenericWidget> dreamWidgets = new ArrayList<GenericWidget>();
	
	//Thought widgets
	private ArrayList<GenericWidget> thoughtWidgets = new ArrayList<GenericWidget>();
	
	private String statePublished = "Published";
	private String statePrivate = "Private";
	
	//----------------------------------------------------------------
	//================================================================================================================

	public AwakeWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
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
		
		//Tab buttons
		this.buttonDreams = new GenericButton("Dreams");
		this.buttonDreams.setTooltip("Dream thoughts into existance");
		this.buttonDreams.setHoverColor(hoverColor);
		
		this.buttonThoughts = new GenericButton("Thoughts");
		this.buttonThoughts.setTooltip("Invent new or refine thoughts");
		this.buttonThoughts.setHoverColor(hoverColor);
		
		//Dreams
		this.listDreams = new MOCListWidget();
		this.listDreams.setTooltip("Browse available dreams");
		this.listDreams.setBackgroundColor(textFieldColor);
		
		this.buttonStartDream = new GenericButton("Start");
		this.buttonStartDream.setTooltip("Begin a dream");
		this.buttonStartDream.setHoverColor(hoverColor);
		
		//Thoughts
		this.listThoughts = new MOCListWidget();
		this.listThoughts.setTooltip("Browse your thoughts");
		this.listThoughts.setBackgroundColor(textFieldColor);
		
		this.buttonThink = new GenericButton("Think");
		this.buttonThink.setTooltip("Continue working on developing a thought");
		this.buttonThink.setHoverColor(hoverColor);
		
		this.buttonForget = new GenericButton("Forget");
		this.buttonForget.setTooltip("Remove a thought");
		this.buttonForget.setHoverColor(hoverColor);
		
		this.buttonInvent = new GenericButton("Invent");
		this.buttonInvent.setTooltip("Invent something new");
		this.buttonInvent.setHoverColor(hoverColor);
		
		this.buttonPublish = new GenericButton("Un/Publish");
		this.buttonPublish.setTooltip("Publish your work for others to see or hide it");
		this.buttonPublish.setHoverColor(hoverColor);
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(hoverColor);
		
		this.buttonHelp = new GenericButton("?");
		this.buttonHelp.setTooltip("Help");
		this.buttonHelp.setHoverColor(hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, buttonDreams, buttonThoughts);
		attachWidgets(plugin, listDreams, buttonStartDream, listThoughts, buttonThink, buttonForget, buttonInvent, buttonPublish, buttonClose, buttonHelp);
		
		dreamWidgets.add(listDreams);
		dreamWidgets.add(buttonStartDream);
		
		thoughtWidgets.add(listThoughts);
		thoughtWidgets.add(buttonThink);
		thoughtWidgets.add(buttonForget);
		thoughtWidgets.add(buttonInvent);
		thoughtWidgets.add(buttonPublish);

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
		
		//Tab buttons
		buttonDreams.setX(upLeftX + 5).setY(upLeftY + 20);
		buttonDreams.setWidth((windowWidth - 10) / 2).setHeight(15);
		
		buttonThoughts.setX(upLeftX + 5 + (windowWidth - 10) / 2).setY(upLeftY + 20);
		buttonThoughts.setWidth((windowWidth - 10) / 2).setHeight(15);
		if (player.hasPermission("DreamCatcher.Create")) buttonThoughts.setVisible(true);
		else buttonThoughts.setVisible(false);
		
		//Dreams
		listDreams.setX(upLeftX+5).setY(upLeftY+40);
		listDreams.setWidth(windowWidth - 10).setHeight(windowHeight - 65);
		
		buttonStartDream.setX(upLeftX + 5).setY(upLeftY + windowHeight - 25);
		buttonStartDream.setWidth(windowWidth - 10).setHeight(15);
		buttonStartDream.setEnabled(false);
		
		//Thoughts
		listThoughts.setX(upLeftX+5).setY(upLeftY+40);
		listThoughts.setWidth(windowWidth - 10).setHeight(windowHeight - 80);
		
		buttonInvent.setX(upLeftX + 5).setY(upLeftY + windowHeight - 40);
		buttonInvent.setWidth((windowWidth - 10) / 2).setHeight(15);
		if (plugin.getDreamNet().getPlayer(player.getName()).getThoughts().size() < plugin.getConfiguration().getMaxDreamsPerPerson()) buttonInvent.setEnabled(true);
		else buttonInvent.setEnabled(false);
		
		buttonForget.setX(upLeftX + 5).setY(upLeftY + windowHeight - 25);
		buttonForget.setWidth((windowWidth - 10) / 2).setHeight(15);
		buttonForget.setEnabled(false);
		
		buttonThink.setX(upLeftX + 5 + (windowWidth - 10) / 2).setY(upLeftY + windowHeight - 40);
		buttonThink.setWidth((windowWidth - 10) / 2).setHeight(15);
		buttonThink.setEnabled(false);
		
		buttonPublish.setX(upLeftX + 5 + (windowWidth - 10) / 2).setY(upLeftY + windowHeight - 25);
		buttonPublish.setWidth((windowWidth - 10) / 2).setHeight(15);
		buttonPublish.setEnabled(false);
		
		//Window buttons
		buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		buttonClose.setWidth(15).setHeight(15);
		
		buttonHelp.setX(upLeftX + windowWidth - 35).setY(upLeftY+5);
		buttonHelp.setWidth(15).setHeight(15);
		
		fillThoughtsAndDreams();
		
		showDreams();
		
	}
	
	//================================================================================================================
	private void fillThoughtsAndDreams() {
		listDreams.clearSelection();
		listDreams.clear();
		
		for (DreamPlayer dp : plugin.getDreamNet().getPlayers()) {
			for (Thought thought : dp.getThoughts()) {
				if (thought.isPublished()) listDreams.addItem(new ListWidgetItem(thought.getName(), dp.getName()));
				
			}
			
		}
		
		listThoughts.clearSelection();
		listThoughts.clear();
		
		for (Thought thought : plugin.getDreamNet().getPlayer(player.getName()).getThoughts()) {
			String publishState;
			if (thought.isPublished()) publishState = statePublished ; else publishState = statePrivate;
			listThoughts.addItem(new ListWidgetItem(thought.getName(), publishState));
			
		}
		
		refresh();
		
	}

	//================================================================================================================
	private void showDreams() {
		for (Widget widget : dreamWidgets) { widget.setVisible(true); }
		for (Widget widget : thoughtWidgets) { widget.setVisible(false); }
		
		buttonDreams.setEnabled(false);
		buttonThoughts.setEnabled(true);
		
		refresh();
		
	}

	//================================================================================================================
	private void showThoughts() {
		for (Widget widget : dreamWidgets) { widget.setVisible(false); }
		for (Widget widget : thoughtWidgets) { widget.setVisible(true); }
		
		buttonDreams.setEnabled(true);
		buttonThoughts.setEnabled(false);
		
		refresh();
		
	}

	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonClose)) { closeWindow(); return; }
		
		if (button.equals(buttonHelp)) { closeWindow(); plugin.getGUI().displayHelpWindow(player); return; }
		
		if (button.equals(buttonDreams)) { showDreams(); return; }
		
		if (button.equals(buttonThoughts)) { showThoughts(); return; }
		
		//Load and teleports player to the dream world
		if (button.equals(buttonStartDream)) {
			plugin.getDreamNet().startDream(player.getName(), listDreams.getSelectedItem().getTitle(), listDreams.getSelectedItem().getText());
			
			closeWindow();
			return;
			
		}
		
		//Add new thought
		if (button.equals(buttonInvent)) {
			closeWindow();
			
			plugin.getGUI().displayNewThoughtWindow(player);
			return;
			
		}
		
		//Remove thought
		if (button.equals(buttonForget)) {
			plugin.getDreamNet().removeThought(player.getName(), listThoughts.getSelectedItem().getTitle());
			
			if (plugin.getDreamNet().getPlayer(player.getName()).getThoughts().size() < plugin.getConfiguration().getMaxDreamsPerPerson()) buttonInvent.setEnabled(true);
			else buttonInvent.setEnabled(false);
			
			fillThoughtsAndDreams();
			return;
			
		}
		
		if (button.equals(buttonPublish)) {
			Thought t = plugin.getDreamNet().getPlayer(player.getName()).getThought(listThoughts.getSelectedItem().getTitle());
			boolean newState = !t.isPublished();
			
			t.setPublished(newState);
			plugin.getConfiguration().setDreamPlayerThought(player.getName(), t);
			
			if (newState) {
				closeWindow();
				
				//Record publish date
				plugin.getConfiguration().recordThoughtPublishDate(player.getName(), t.getName());
				
				//Display survey
				plugin.getGUI().displaySurveyCreation(player);
				
				return;
			
			} else {
				listThoughts.getSelectedItem().setText(statePrivate);
				fillThoughtsAndDreams();
				refresh();
				
			}
			
			return;
			
		}
		
		//Loads and teleports player to the build world
		if (button.equals(buttonThink)) {
			plugin.getDreamNet().startThought(player.getName(), listThoughts.getSelectedItem().getTitle());
			
			closeWindow();
			return;
			
		}
		
	}

	//================================================================================================================
	public void onSelection(ListWidget listWidget) {
		if (listDreams == listWidget) {
			if (listDreams.getSelectedItem() == null) { buttonStartDream.setEnabled(false); }
			else { buttonStartDream.setEnabled(true); }
			
			refresh();
			return;
			
		}

		if (listThoughts == listWidget) {
			if (listThoughts.getSelectedItem() == null) { buttonForget.setEnabled(false); buttonPublish.setEnabled(false); buttonThink.setEnabled(false); }
			else { buttonForget.setEnabled(true); buttonPublish.setEnabled(true); buttonThink.setEnabled(true); }
			
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
