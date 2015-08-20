package moc.DreamCrafter.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.WorldData;
import moc.DreamCrafter.gui.widget.MOCListWidget;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MainWindow extends GenericPopup {
	private MOCDreamCrafter plugin;
	private SpoutPlayer player;
	private int padding = 5;
	private int buttonWidth = 75; 
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(20,60,100);
	private Color widgetBackgroundColor = new Color(20,55,95);
	private Color hoverColor = new Color(50,110,180);
	private List<Widget> createPanel, myWorldsPanel, dreamPanel, deletePanel; 
	private List<List<Widget>> panels;
	private String worldToDelete = "";

	//Background
	private Gradient gradientBackground, panelBackground;
	
	//Title
	private Label labelTitle, myWorldsTitle, dreamTitle, deleteTitle, createTitle, createWorldNameLabel;
	
	//List
	private GenericListWidget buildWorldList, dreamWorldList, deleteWorldList;
	
	//Buttons
	private Button myWorldsMenuButton, dreamMenuButton, deleteMenuButton, createMenuButton;
	private Button buildButton, dreamButton, deleteButton, createButton;
	
	private MessageBox deleteConfirmation = null;
	
	//Textfields
	private TextField createWorldNameTextField;
	
	public MainWindow(SpoutPlayer player, MOCDreamCrafter plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);
		
		this.panelBackground = new GenericGradient(this.textFieldColor);
		this.panelBackground.setPriority(RenderPriority.High);

		//Title
		this.labelTitle = new GenericLabel("DreamCrafter");
		this.labelTitle.setScale(this.scaleLarge);
		this.labelTitle.setPriority(RenderPriority.Normal);
		
		this.myWorldsTitle = new GenericLabel("Build a World");
		this.myWorldsTitle.setScale(this.scaleNormal);
		this.myWorldsTitle.setPriority(RenderPriority.Normal);
		
		this.dreamTitle = new GenericLabel("Start a Dream");
		this.dreamTitle.setScale(this.scaleNormal);
		this.dreamTitle.setPriority(RenderPriority.Normal);
		
		this.createTitle = new GenericLabel("Create a World");
		this.createTitle.setScale(this.scaleNormal);
		this.createTitle.setPriority(RenderPriority.Normal);
		
		this.createWorldNameLabel = new GenericLabel("World Name");
		this.createWorldNameLabel.setScale(this.scaleMedium);
		this.createWorldNameLabel.setPriority(RenderPriority.Normal);
		
		this.deleteTitle = new GenericLabel("Delete a World");
		this.deleteTitle.setScale(this.scaleNormal);
		this.deleteTitle.setPriority(RenderPriority.Normal);
		
		//List
		this.buildWorldList = new GenericListWidget();
		this.buildWorldList.setTooltip("Select one of your worlds to build");
		this.buildWorldList.setBackgroundColor(this.widgetBackgroundColor);
		this.buildWorldList.setPriority(RenderPriority.Normal);
		
		this.dreamWorldList = new GenericListWidget();
		this.dreamWorldList.setTooltip("Select one of the worlds to dream");
		this.dreamWorldList.setBackgroundColor(this.widgetBackgroundColor);
		this.dreamWorldList.setPriority(RenderPriority.Normal);
		
		this.deleteWorldList = new GenericListWidget();
		this.deleteWorldList.setTooltip("Select one of the worlds to delete");
		this.deleteWorldList.setBackgroundColor(this.widgetBackgroundColor);
		this.deleteWorldList.setPriority(RenderPriority.Normal);

		//Buttons
		this.createMenuButton = new GenericButton("Create >>");
		this.createMenuButton.setTooltip("Create a new world");
		this.createMenuButton.setHoverColor(this.hoverColor);
		
		this.myWorldsMenuButton = new GenericButton("My Worlds >>");
		this.myWorldsMenuButton.setTooltip("View your worlds");
		this.myWorldsMenuButton.setHoverColor(this.hoverColor);

		this.dreamMenuButton = new GenericButton("Dream >>");
		this.dreamMenuButton.setTooltip("Start a Dream");
		this.dreamMenuButton.setHoverColor(this.hoverColor);

//		this.deleteMenuButton = new GenericButton("Delete >>");
//		this.deleteMenuButton.setTooltip("Delete one of your worlds");
//		this.deleteMenuButton.setHoverColor(this.hoverColor);
		
		this.createButton = new GenericButton("Create World");
		this.createButton.setPriority(RenderPriority.Normal);
		
		this.buildButton = new GenericButton("Build World");
		this.buildButton.setPriority(RenderPriority.Normal);
		
		this.dreamButton = new GenericButton("Start Dream");
		this.dreamButton.setPriority(RenderPriority.Normal);
		
		this.deleteButton = new GenericButton("Delete World");
		this.deleteButton.setPriority(RenderPriority.Normal);
		
		this.createWorldNameTextField = new GenericTextField();
		this.createWorldNameTextField.setPriority(RenderPriority.Normal);

		//Attach widgets to the screen
		attachWidgets(plugin, this.gradientBackground, this.panelBackground, this.labelTitle, 
				createMenuButton, myWorldsMenuButton, dreamMenuButton,
				createTitle, createWorldNameLabel, createWorldNameTextField, createButton,
				myWorldsTitle, buildWorldList, buildButton, deleteButton,
				dreamTitle, dreamWorldList, dreamButton
				);
		
		// Panels
		createPanel = new ArrayList<Widget>();
		createPanel.addAll(Arrays.asList(createTitle, createWorldNameLabel, createWorldNameTextField, createButton));
		
		myWorldsPanel = new ArrayList<Widget>();
		myWorldsPanel.addAll(Arrays.asList(myWorldsTitle, buildWorldList, buildButton, deleteButton));
		
		dreamPanel = new ArrayList<Widget>();
		dreamPanel.addAll(Arrays.asList(dreamTitle, dreamWorldList, dreamButton));
		
//		deletePanel = new ArrayList<Widget>();
//		deletePanel.addAll(Arrays.asList(deleteTitle, deleteWorldList, deleteButton));
		
		panels = new ArrayList<List<Widget>>();
		panels.addAll(Arrays.asList(createPanel, myWorldsPanel, dreamPanel));

		//Initialize
		this.initialize();
		
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.padding * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.padding * 2;
        int upLeftX = this.padding; 
        int upLeftY = this.padding;
        int downLeftX = this.padding;
        int downLeftY = player.getMainScreen().getHeight() - this.padding;

		//Background
		this.gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		this.gradientBackground.setX(upLeftX).setY(upLeftY);

		//Title
		this.labelTitle.setX(upLeftX + padding).setY(upLeftY + padding);
		this.labelTitle.setHeight(15).setWidth(40);
		
		//Buttons
		List<Button> buttons = new ArrayList<Button>();
		buttons.addAll(Arrays.asList(dreamMenuButton, createMenuButton, myWorldsMenuButton));
		GuiHelper.PositionVerticalButtonList(buttons, upLeftX + 2*padding, labelTitle.getY() + labelTitle.getHeight() + 2*padding, padding);
		
		for(Button b : buttons) {
			b.setWidth(buttonWidth).setHeight(15);
			b.setEnabled(false);
		}
		
		if(this.player.hasPermission("moc.DreamCrafter.Play")) 
			this.dreamMenuButton.setEnabled(true);
		
		if(this.player.hasPermission("moc.DreamCrafter.Author")) 
			this.myWorldsMenuButton.setEnabled(true);
		
		if(this.player.hasPermission("moc.DreamCrafter.Create")) {
			this.createMenuButton.setEnabled(true);
		}
		
		this.panelBackground.setWidth(310).setHeight((downLeftY - padding) - (labelTitle.getY() + padding));
		this.panelBackground.setX(dreamMenuButton.getX() + dreamMenuButton.getWidth() + 2*padding).setY(labelTitle.getY() + padding);
		
		// Create Panel
		createTitle.setX(panelBackground.getX() + padding).setY(panelBackground.getY() + padding);
		createTitle.setHeight(15).setWidth(40);
		
		createWorldNameLabel.setX(panelBackground.getX() + padding).setY(createTitle.getY() + createTitle.getHeight() + 2*padding);
		createWorldNameLabel.setHeight(15).setWidth(60);
		
		createWorldNameTextField.setX(panelBackground.getX() + padding).setY(createWorldNameLabel.getY() + createWorldNameLabel.getHeight());
		createWorldNameTextField.setHeight(15).setWidth(100);
		
		createButton.setX(panelBackground.getX() + padding).setY(createWorldNameTextField.getY() + createWorldNameTextField.getHeight() + padding);
		createButton.setWidth(buttonWidth).setHeight(15);
		
		// My Worlds Panel
		this.myWorldsTitle.setX(panelBackground.getX() + padding).setY(panelBackground.getY() + padding);
		this.myWorldsTitle.setHeight(15).setWidth(40);
		
		this.buildWorldList.setX(panelBackground.getX() + padding).setY(myWorldsTitle.getY() + 2*padding);
		this.buildWorldList.setWidth(150).setHeight(150);
		this.buildWorldList.clear();			
		fillBuildWorldList();		
		this.buildWorldList.clearSelection();
		
		buildButton.setX(panelBackground.getX() + padding).setY(buildWorldList.getY() + buildWorldList.getHeight() + padding);
		buildButton.setWidth(60).setHeight(15);
		
		deleteButton.setX(buildButton.getX() + buildButton.getWidth() + padding).setY(buildWorldList.getY() + buildWorldList.getHeight() + padding);
		deleteButton.setWidth(60).setHeight(15);
		
		// Dream Panel
		this.dreamTitle.setX(panelBackground.getX() + padding).setY(panelBackground.getY() + padding);
		this.dreamTitle.setHeight(15).setWidth(40);
		
		this.dreamWorldList.setX(panelBackground.getX() + padding).setY(dreamTitle.getY() + 2*padding);
		this.dreamWorldList.setWidth(150).setHeight(150);
		this.dreamWorldList.clear();			
		fillDreamWorldList();
		this.dreamWorldList.clearSelection();
		
		dreamButton.setX(panelBackground.getX() + padding).setY(dreamWorldList.getY() + dreamWorldList.getHeight() + padding);
		dreamButton.setWidth(60).setHeight(15);
		
//		// Delete Panel
//		this.deleteTitle.setX(panelBackground.getX() + padding).setY(panelBackground.getY() + padding);
//		this.deleteTitle.setHeight(15).setWidth(40);
//		
//		this.deleteWorldList.setX(panelBackground.getX() + padding).setY(deleteTitle.getY() + 2*padding);
//		this.deleteWorldList.setWidth(150).setHeight(150);
//		this.deleteWorldList.clear();			
//		fillDeleteWorldList();
//		this.deleteWorldList.clearSelection();
		
		
		
		showPanel(dreamPanel);
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void open(){
		closeWindow();
		initialize();
		player.getMainScreen().attachPopupScreen(this);
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
		}
	}

//-----------------------------------------------------------------------------------------------------------
	
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	//On button click
	public void onClick(Button button) {
		plugin.getLog().info("click " + button.getText());
		if (button.equals(this.myWorldsMenuButton)) {
			showPanel(myWorldsPanel);
		}
		else if(button.equals(this.dreamMenuButton)) {
			showPanel(dreamPanel);
		}
		else if(button.equals(this.createMenuButton)) {
			showPanel(createPanel);
		}
		else if(button.equals(this.deleteMenuButton)) {
			showPanel(deletePanel);
		}
		else if(button.equals(createButton)) {
			String worldName = createWorldNameTextField.getText(); 
			if(!worldName.equals("")) {
				close();
				player.performCommand("dc create " + player.getName() + " " + worldName);
			}
		}
		else if(button.equals(buildButton)) {
			if(buildWorldList.getSelectedItem() != null) {
				String selectedWorld = buildWorldList.getSelectedItem().getTitle();
				close();
				player.performCommand("dc build " + selectedWorld);
			}
		}
		else if(button.equals(dreamButton)) {
			if(dreamWorldList.getSelectedItem() != null) {
				String selectedWorld = dreamWorldList.getSelectedItem().getTitle();
				close();
				player.performCommand("dc dream " + selectedWorld);
			}
		}
		else if(button.equals(deleteButton)) {
			if(buildWorldList.getSelectedItem() != null) {
				worldToDelete = buildWorldList.getSelectedItem().getTitle();
				deleteConfirmation = new MessageBox(player, plugin, this,
						"Delete World?", 
						"Are you sure you want to delete '" + worldToDelete + "'?");
				deleteConfirmation.open();
			}
		}
		else if(deleteConfirmation != null && button.equals(deleteConfirmation.OkayButton)) {
				close();
				player.performCommand("dc delete " + worldToDelete);	
				deleteConfirmation = null;
		}
		
	}
	
//-----------------------------------------------------------------------------------------------------------

	private void fillBuildWorldList() {
		List<WorldData> worlds = plugin.getPersistentDataHandler().getBuildWorldsOwnedByPlayer((Player)player);
		for(WorldData w : worlds)
			this.buildWorldList.addItem(new ListWidgetItem(w.name, " by " + w.ownerName));
	}
		
//-----------------------------------------------------------------------------------------------------------

	private void fillDreamWorldList() {
		List<WorldData> worlds = plugin.getPersistentDataHandler().getAllBuildWorlds();
		for(WorldData w : worlds) {
			if(w.isPublished)
				dreamWorldList.addItem(new ListWidgetItem(w.name, " by " + w.ownerName));
		}
	}
		
//-----------------------------------------------------------------------------------------------------------
	
	private void fillDeleteWorldList() {
		List<WorldData> worlds = plugin.getPersistentDataHandler().getBuildWorldsOwnedByPlayer((Player)player);
		for(WorldData w : worlds)
			deleteWorldList.addItem(new ListWidgetItem(w.name, " by " + w.ownerName));
	}
		
//-----------------------------------------------------------------------------------------------------------
	
	private void showPanel(List<Widget> panel) {
		hideAllPanels();
		for(Widget w : panel) {
			w.setVisible(true);
			w.setDirty(true);
		}
	}
		
//-----------------------------------------------------------------------------------------------------------
	
	private void hidePanel(List<Widget> panel) {
		for(Widget w : panel) {
			w.setVisible(false);
			w.setDirty(true);
		}
	}
			
//-----------------------------------------------------------------------------------------------------------

	private void hideAllPanels() {
		for(List<Widget> p : panels)
			hidePanel(p);
	}
	
}
