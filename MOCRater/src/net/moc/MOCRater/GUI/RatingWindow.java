package net.moc.MOCRater.GUI;

import java.util.Arrays;

import net.moc.MOCRater.GUI.Widgets.MOCComboBox;
import net.moc.MOCRater.GUI.Widgets.MOCListWidget;
import net.moc.MOCRater.SQL.MOCPattern;
import net.moc.MOCRater.MOCRater;
import org.bukkit.Location;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ComboBox;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RatingWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCRater plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color infoBackgroundColor = new Color(20,60,100);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private Gradient background, infoBackground;
	
	//Heading
	private Label headingTitle, headingTitleDescription;
	
	//Rating Type
	private Label ratingTypeLabel;
	private ComboBox ratingTypeComboBox;
	
	//Rating title
	private Label titleLabel;
	private GenericTextField titleTextField;
	
	//Rating
	private ComboBox ratingComboBox;
	
	//Location info
	private Label locationLabelWorld, locationLabelCoordinates, locationLabelHeadingAndPitch;

	//Text field
	private Label textLabel;
	private GenericTextField textTextField;
	
	//Pattern
	private Label patternLabel;
	private Button patternButtonBrowse, patternButtonAdd;
	private ListWidget patternList;
	
	//Window buttons
	private Button buttonCancel, buttonSubmit, buttonClose;
	
	//Error pop up
	private Gradient popupBackground;
	private Label popupLabel;
	private Button popupOKButton;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public RatingWindow(SpoutPlayer player, MOCRater plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		this.infoBackground = new GenericGradient(this.infoBackgroundColor);
		this.infoBackground.setPriority(RenderPriority.High);

		
		//Heading
		this.headingTitle = new GenericLabel(this.plugin.getDescription().getFullName() + ": Provide a Rating");
		this.headingTitle.setScale(this.scaleLarge);

		this.headingTitleDescription = new GenericLabel("Provide feedback and guidance to other Minecraft players");
		this.headingTitleDescription.setScale(this.scaleSmall);
		
		
		//Rating Type
		this.ratingTypeLabel = new GenericLabel("Regarding:");
		this.ratingTypeLabel.setScale(this.scaleNormal);
		this.ratingTypeLabel.setTooltip("Please select the rating type");
		
		this.ratingTypeComboBox = new MOCComboBox();
		this.ratingTypeComboBox.setItems(Arrays.asList("Design Comment", "Design Critique", "Pattern Usage", "Pattern Critique", ""));
		this.ratingTypeComboBox.setHoverColor(this.hoverColor);
		this.ratingTypeComboBox.setScale(this.scaleNormal);
		this.ratingTypeComboBox.setTooltip("Please select the rating type");

		
		//Rating title
		this.titleLabel = new GenericLabel("Title:");
		this.titleLabel.setScale(this.scaleNormal);
		this.titleLabel.setTooltip("Enter comment title here");
		
		this.titleTextField = new GenericTextField();
		this.titleTextField.setTooltip("Enter comment title here");
		this.titleTextField.setTabIndex(1);
		this.titleTextField.setMaximumLines(1);
		this.titleTextField.setMaximumCharacters(128);
		this.titleTextField.setFieldColor(this.textFieldColor);
		
		
		//Rating
		this.ratingComboBox = new MOCComboBox();
		this.ratingComboBox.setItems(Arrays.asList("5-Excellent", "4-Good", "3-Average", "2-Fair", "1-Poor", ""));
		this.ratingComboBox.setHoverColor(this.hoverColor);
		this.ratingComboBox.setScale(this.scaleNormal);
		this.ratingComboBox.setTooltip("Please select the rating value");
		
		
		//Location info
		this.locationLabelWorld = new GenericLabel();
		this.locationLabelWorld.setScale(this.scaleNormal);
		this.locationLabelWorld.setTooltip("World Name");
		this.locationLabelWorld.setScale(this.scaleMedium);
		
		this.locationLabelCoordinates = new GenericLabel();
		this.locationLabelCoordinates.setScale(this.scaleNormal);
		this.locationLabelCoordinates.setTooltip("Coordinates");
		this.locationLabelCoordinates.setScale(this.scaleMedium);
		
		this.locationLabelHeadingAndPitch = new GenericLabel();
		this.locationLabelHeadingAndPitch.setScale(this.scaleNormal);
		this.locationLabelHeadingAndPitch.setTooltip("Heading and Pitch");
		this.locationLabelHeadingAndPitch.setScale(this.scaleMedium);
		
		
		//Text field
		this.textLabel = new GenericLabel("Comments:");
		this.textLabel.setTooltip("Enter your rating comments here");
		this.textLabel.setScale(this.scaleNormal);
		
		this.textTextField = new GenericTextField();
		this.textTextField.setTooltip("Enter your rating comments here");
		this.textTextField.setTabIndex(2);
		this.textTextField.setFieldColor(this.textFieldColor);
		this.textTextField.setMaximumLines(10);
		this.textTextField.setMaximumCharacters(5000);
		
		
		//Pattern
		this.patternLabel = new GenericLabel("Patterns:");
		this.patternLabel.setTooltip("Pattern List");
		this.patternLabel.setScale(this.scaleNormal);
		
		
		this.patternButtonBrowse = new GenericButton("Browse");
		this.patternButtonBrowse.setTooltip("Browse Patterns");
		this.patternButtonBrowse.setHoverColor(this.hoverColor);
		
		this.patternButtonAdd = new GenericButton("Add");
		this.patternButtonAdd.setTooltip("Add Pattern");
		this.patternButtonAdd.setHoverColor(this.hoverColor);
		
		this.patternList = new MOCListWidget();
		this.patternList.setTooltip("Select a pattern from the list");
		this.patternList.setBackgroundColor(this.textFieldColor);
		
		
		//Window buttons
		this.buttonCancel = new GenericButton("Cancel");
		this.buttonCancel.setTooltip("Cancel");
		this.buttonCancel.setHoverColor(this.hoverColor);

		this.buttonSubmit = new GenericButton("Submit");
		this.buttonSubmit.setTooltip("Submit the rating");
		this.buttonSubmit.setHoverColor(this.hoverColor);

		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);

		
		//Error pop up window
		this.popupBackground = new GenericGradient(this.backgroundColor);
		this.popupBackground.setPriority(RenderPriority.Low);
		
		this.popupLabel = new GenericLabel("");
		this.popupLabel.setPriority(RenderPriority.Lowest);
		
		this.popupOKButton = new GenericButton("OK");
		this.popupOKButton.setTooltip("OK");
		this.popupOKButton.setHoverColor(this.hoverColor);
		this.popupOKButton.setPriority(RenderPriority.Lowest);

		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.infoBackground, this.headingTitle, this.headingTitleDescription, this.ratingTypeLabel);
		attachWidgets(plugin, this.ratingTypeComboBox, this.locationLabelWorld, this.locationLabelCoordinates);
		attachWidgets(plugin, this.locationLabelHeadingAndPitch, this.titleLabel, this.titleTextField);
		attachWidgets(plugin, this.ratingComboBox, this.textLabel, this.textTextField, this.patternButtonBrowse, this.patternButtonAdd);
		attachWidgets(plugin, this.patternLabel, this.patternList, this.buttonCancel, this.buttonSubmit, this.buttonClose);
		attachWidgets(plugin, this.popupBackground, this.popupLabel, this.popupOKButton);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBuffer * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBuffer * 2;
        int upLeftX = this.screenBuffer; 
        int upLeftY = this.screenBuffer;
        int upRightX = player.getMainScreen().getWidth() - this.screenBuffer;
        int upRightY = this.screenBuffer;
        int downLeftX = this.screenBuffer;
        int downLeftY = player.getMainScreen().getHeight() - this.screenBuffer;
        int downRightX = player.getMainScreen().getWidth() - this.screenBuffer;
        int downRightY = player.getMainScreen().getHeight() - this.screenBuffer;

        
		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		this.infoBackground.setHeight(60).setWidth(140);
		this.infoBackground.setX(upRightX-145).setY(upRightY+5);
		
		//-------------------------------------------------------------------
		//Upper Left Corner anchor
		//-------------------------------------------------------------------
		//Heading
		this.headingTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.headingTitle.setHeight(15).setWidth(windowWidth);
		
		this.headingTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.headingTitleDescription.setHeight(15).setWidth(windowWidth);
		
		
		
		//Rating Type
		this.ratingTypeLabel.setX(upLeftX+5).setY(upLeftY+30);
		this.ratingTypeLabel.setHeight(15).setWidth(40);
		
		this.ratingTypeComboBox.setX(upLeftX+60).setY(upLeftY+28);
		this.ratingTypeComboBox.setHeight(15).setWidth(100);
		this.ratingTypeComboBox.setSelection(4);


		//Rating title
		this.titleLabel.setX(upLeftX+34).setY(upLeftY+50);
		this.titleLabel.setHeight(15).setWidth(40);
		
		this.titleTextField.setX(upLeftX+61).setY(upLeftY+48);
		this.titleTextField.setHeight(15).setWidth(198);
		this.titleTextField.setText("");
		
		
		//Rating
		this.ratingComboBox.setX(upLeftX+160).setY(upLeftY+28);
		this.ratingComboBox.setHeight(15).setWidth(100);
		this.ratingComboBox.setSelection(5);
		//-------------------------------------------------------------------
		

		//-------------------------------------------------------------------
		//Upper Right Corner anchor
		//-------------------------------------------------------------------
		//Location info
		this.locationLabelWorld.setX(upRightX-140).setY(upRightY+10);
		this.locationLabelWorld.setHeight(15).setWidth(40);
		this.locationLabelWorld.setText("World: " + this.player.getLocation().getWorld().getName());

		this.locationLabelCoordinates.setX(upRightX-140).setY(upRightY+20);
		this.locationLabelCoordinates.setHeight(15).setWidth(40);
		this.locationLabelCoordinates.setText("X: " + this.player.getLocation().getBlockX() +
												" Y: " + this.player.getLocation().getBlockY() + 
												" Z: " + this.player.getLocation().getBlockZ());
		
		this.locationLabelHeadingAndPitch.setX(upRightX-140).setY(upRightY+30);
		this.locationLabelHeadingAndPitch.setHeight(15).setWidth(40);
		this.locationLabelHeadingAndPitch.setText("Heading: " + (int)this.player.getLocation().getYaw() +
													" Pitch: " + (int)this.player.getLocation().getPitch());
		//-------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------
		//Lower Right Corner anchor
		//-------------------------------------------------------------------
		//Text field
		this.textLabel.setX(downRightX-305).setY(downRightY-155);
		this.textLabel.setWidth(40).setHeight(15);
		
		this.textTextField.setX(downRightX-305).setY(downRightY-145);
		this.textTextField.setWidth(300).setHeight(120);
		this.textTextField.setText("");
		//-------------------------------------------------------------------
		
		
		//-------------------------------------------------------------------
		//Lower Left Corner anchor
		//-------------------------------------------------------------------
		//Pattern
		this.patternLabel.setX(downLeftX+5).setY(downLeftY-155);
		this.patternLabel.setWidth(40).setHeight(15);
		this.patternLabel.setVisible(false);
		
		this.patternButtonBrowse.setX(downLeftX+5).setY(downLeftY-20);
		this.patternButtonBrowse.setWidth(50).setHeight(15);
		this.patternButtonBrowse.setEnabled(false);
		this.patternButtonBrowse.setVisible(false);
		
		
		this.patternButtonAdd.setX(downLeftX+55).setY(downLeftY-20);
		this.patternButtonAdd.setWidth(50).setHeight(15);
		this.patternButtonAdd.setEnabled(false);
		this.patternButtonAdd.setVisible(false);
		
		this.patternList.setX(downLeftX+5).setY(downLeftY-145);
		this.patternList.setWidth(100).setHeight(120);
		this.patternList.clear();
		for (MOCPattern pattern : this.plugin.getSQL().getPatterns()) {
			this.patternList.addItem(new ListWidgetItem("", pattern.getName()));
		}
		this.patternList.setEnabled(false);
		this.patternList.setVisible(false);
		this.patternList.clearSelection();
		//-------------------------------------------------------------------
		
		

		//Window buttons
		this.buttonCancel.setX(downRightX-55).setY(downRightY-20);
		this.buttonCancel.setWidth(50).setHeight(15);
		
		this.buttonSubmit.setX(downRightX-205).setY(downRightY-20);
		this.buttonSubmit.setWidth(100).setHeight(15);
		
		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		//Pop up
		this.popupBackground.setHeight(100).setWidth(200);
		this.popupBackground.setX(player.getMainScreen().getWidth()/2 - 100).setY(player.getMainScreen().getHeight()/2 - 50);
		this.popupBackground.setVisible(false);

		this.popupLabel.setHeight(15).setWidth(40);
		this.popupLabel.setX(player.getMainScreen().getWidth()/2 - 60).setY(player.getMainScreen().getHeight()/2 - 30);
		this.popupLabel.setText("Please fill in all the fields\nbefore submitting the comment.");
		this.popupLabel.setVisible(false);

		this.popupOKButton.setHeight(15).setWidth(50);
		this.popupOKButton.setX(player.getMainScreen().getWidth()/2 - 25).setY(player.getMainScreen().getHeight()/2 + 10);
		this.popupOKButton.setVisible(false);
		
	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(boolean takeScreenshot, String patternToCritique){
		//Get the screen shot
		if (takeScreenshot) this.player.sendScreenshotRequest();
		
		this.initialize();
		
		if (patternToCritique != null) this.loadPatternForCritique(patternToCritique);
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			//if (!widget.equals(this.popupBackground) && !widget.equals(this.popupLabel) && !widget.equals(this.popupOKButton) && !widget.equals(this.patternList)) widget.setVisible(true);
			widget.setDirty(true);
			
		}
		
	}
	//================================================================================================================
	

	
	private void loadPatternForCritique(String patternToCritique) {
		this.ratingTypeComboBox.setSelection(3);
		
		int i = 0;
		for ( ListWidgetItem item : this.patternList.getItems()) {
			if (item.getText().equalsIgnoreCase(patternToCritique)) break;
			i++;
		}
		
		this.patternList.setSelection(i);
		
		this.patternButtonAdd.setVisible(true);
		this.patternButtonBrowse.setVisible(true);
		this.patternLabel.setVisible(true);
		this.patternList.setVisible(true);
		
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
		//Close and Cancel buttons
		if (button.equals(this.buttonClose) || button.equals(this.buttonCancel)) {
			closeWindow();
			
			return;
			
		}
		
		//Drop down - Rating Type
		if (button.equals(this.ratingTypeComboBox)) {
			if (this.ratingTypeComboBox.getSelectedRow() == 2 || this.ratingTypeComboBox.getSelectedRow() == 3) {
				this.patternList.setEnabled(true);
				this.patternList.setVisible(true);
				
				//Check permission
				if (this.player.hasPermission("MOCRater.patternsaddedit")) { this.patternButtonAdd.setEnabled(true); this.patternButtonAdd.setVisible(true); }
				else { this.patternButtonAdd.setEnabled(false); this.patternButtonAdd.setVisible(false); }
				
				this.patternButtonBrowse.setEnabled(true);
				this.patternButtonBrowse.setVisible(true);

				this.patternLabel.setVisible(true);

				this.buttonSubmit.setEnabled(false);
				
			} else {
				this.patternList.setEnabled(false);
				this.patternList.setVisible(false);
				this.patternList.clearSelection();
				
				this.patternButtonAdd.setEnabled(false);
				this.patternButtonAdd.setVisible(false);
				
				this.patternLabel.setVisible(false);

				this.patternButtonBrowse.setEnabled(false);
				this.patternButtonBrowse.setVisible(false);
				
				this.buttonSubmit.setEnabled(true);
				
			}
			
			this.patternList.setDirty(true);
			this.patternButtonAdd.setDirty(true);
			this.patternButtonBrowse.setDirty(true);
			this.buttonSubmit.setDirty(true);
			this.patternLabel.setDirty(true);


			
		}
		

		
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		//Submit button
		if (button.equals(this.buttonSubmit)) {
			//Make sure everything is entered
			if (this.ratingTypeComboBox.getSelectedItem().equalsIgnoreCase("") || this.ratingComboBox.getSelectedItem().equalsIgnoreCase("") ||
					this.titleTextField.getText().equalsIgnoreCase("") || this.textTextField.getText().equalsIgnoreCase("")) {
				//Display pop up
				this.popupBackground.setVisible(true);
				this.popupBackground.setDirty(true);

				this.popupLabel.setVisible(true);
				this.popupLabel.setDirty(true);

				this.popupOKButton.setVisible(true);
				this.popupOKButton.setDirty(true);

				this.setDirty(true);
				
				return;
			}
			
			String patternName = "";
			
			if (this.patternList.getSelectedItem() != null) patternName = this.patternList.getSelectedItem().getText();
			
			Location location = player.getLocation();
			
			this.plugin.getSQL().saveComment(patternName, this.titleTextField.getText(), this.ratingTypeComboBox.getSelectedItem(), 5 - this.ratingComboBox.getSelectedRow(),
											location, this.textTextField.getText(), this.player, this.plugin.getGui().getLatestScreenShots().get(this.player).getName(), 1);
			
			closeWindow();
			
			return;
			
		}
		
		if (button.equals(this.popupOKButton)) {
			this.popupBackground.setVisible(false);
			this.popupBackground.setDirty(true);
			
			this.popupLabel.setVisible(false);
			this.popupLabel.setDirty(true);
			
			this.popupOKButton.setVisible(false);
			this.popupOKButton.setDirty(true);
			
			this.setDirty(true);
			
		}
		
		//Browse button
		if (button.equals(this.patternButtonBrowse)) { 
			closeWindow();
			this.plugin.getGui().displayPatternBrowserWindowGUI(this.player, false);
			return;
			
		}
		
		//Add button
		if (button.equals(this.patternButtonAdd)) {
			closeWindow();
			this.plugin.getGui().displayPatternManagerWindowGUI(this.player, null);
			return; 
			
		}
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
	}
	
	public void onSelection(ListWidget listWidget) {
		if (this.patternList != listWidget) return; //Make sure we didn't get something weird
		
		if (this.patternList.getSelectedItem() == null) {
			this.buttonSubmit.setEnabled(false);
			this.buttonSubmit.setDirty(true);
			
		} else {
			this.buttonSubmit.setEnabled(true);
			this.buttonSubmit.setDirty(true);
			
		}

	}
	//================================================================================================================
}
