package net.moc.MOCRater.GUI;

import java.util.ArrayList;

import net.moc.MOCRater.GUI.Widgets.MOCListWidgetMS;
import net.moc.MOCRater.SQL.MOCPattern;
import net.moc.MOCRater.MOCRater;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
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
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PatternManagerWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCRater plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	private boolean isEdit = false;
	private int editPatternId = -1;
	//----------------------------------------------------------------
	//Background
	private Gradient background;

	//Heading
	private Label headingTitle, headingTitleDescription;

	//Name
	private Label nameLabel;
	private TextField nameTextField;

	//Context
	private Label contextLabel;
	private GenericTextField contextTextField;

	//Problem
	private Label problemLabel;
	private GenericTextField problemTextField;

	//Solution
	private Label solutionLabel;
	private GenericTextField solutionTextField;

	//Pattern
	private ListWidget patternList;

	//Window buttons
	private Button buttonCancel, buttonSave, buttonClose;
	
	//Error pop up
	private Gradient popupBackground;
	private Label popupLabel;
	private Button popupOKButton;
	//----------------------------------------------------------------
	//================================================================================================================



	//================================================================================================================
	public PatternManagerWindow(SpoutPlayer player, MOCRater plugin) {
		this.plugin = plugin;
		this.player = player;

		//Set window transparent
		this.setTransparent(true);


		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);


		//Heading
		this.headingTitle = new GenericLabel(this.plugin.getDescription().getFullName() + ": Pattern Manager");
		this.headingTitle.setScale(this.scaleLarge);

		this.headingTitleDescription = new GenericLabel("Patterns give a structure to design ideas and solutions (Google \"Pattern Language\").");
		this.headingTitleDescription.setScale(this.scaleSmall);


		//Name
		this.nameLabel = new GenericLabel("Name:");
		this.nameLabel.setTooltip("Pattern name");
		this.nameLabel.setScale(this.scaleNormal);

		this.nameTextField = new GenericTextField();
		this.nameTextField.setTooltip("Pattern name");
		this.nameTextField.setFieldColor(this.textFieldColor);
		this.nameTextField.setMaximumLines(1);
		this.nameTextField.setMaximumCharacters(5000);
		this.nameTextField.setTabIndex(1);

		
		//Context
		this.contextLabel = new GenericLabel("Context:");
		this.contextLabel.setTooltip("Pattern context");
		this.contextLabel.setScale(this.scaleNormal);

		this.contextTextField = new GenericTextField();
		this.contextTextField.setTooltip("Pattern context");
		this.contextTextField.setFieldColor(this.textFieldColor);
		this.contextTextField.setMaximumLines(2);
		this.contextTextField.setMaximumCharacters(5000);
		this.contextTextField.setTabIndex(2);

		
		//Problem
		this.problemLabel = new GenericLabel("Problem:");
		this.problemLabel.setTooltip("Problem that the pattern attempts resolve");
		this.problemLabel.setScale(this.scaleNormal);

		this.problemTextField = new GenericTextField();
		this.problemTextField.setTooltip("Problem that the pattern attempts resolve");
		this.problemTextField.setFieldColor(this.textFieldColor);
		this.problemTextField.setMaximumLines(4);
		this.problemTextField.setMaximumCharacters(5000);
		this.problemTextField.setTabIndex(3);

		
		//Solution
		this.solutionLabel = new GenericLabel("Solution:");
		this.solutionLabel.setTooltip("Instructions for creating the pattern");
		this.solutionLabel.setScale(this.scaleNormal);

		this.solutionTextField = new GenericTextField();
		this.solutionTextField.setTooltip("Instructions for creating the pattern");
		this.solutionTextField.setFieldColor(this.textFieldColor);
		this.solutionTextField.setMaximumLines(6);
		this.solutionTextField.setMaximumCharacters(5000);
		this.solutionTextField.setTabIndex(4);

		
		//Pattern
		this.patternList = new MOCListWidgetMS();
		this.patternList.setTooltip("Select patterns to relate this one too");
		this.patternList.setBackgroundColor(this.textFieldColor);

		//Window buttons
		this.buttonCancel = new GenericButton("Cancel");
		this.buttonCancel.setTooltip("Cancel");
		this.buttonCancel.setHoverColor(this.hoverColor);

		this.buttonSave = new GenericButton("Save");
		this.buttonSave.setTooltip("Save the pattern information");
		this.buttonSave.setHoverColor(this.hoverColor);

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
		attachWidgets(plugin, this.background, this.headingTitle, this.headingTitleDescription);
		attachWidgets(plugin, this.nameLabel, this.nameTextField, this.contextLabel, this.contextTextField);
		attachWidgets(plugin, this.problemLabel, this.problemTextField, this.solutionLabel, this.solutionTextField);
		attachWidgets(plugin, this.patternList, this.buttonCancel, this.buttonSave, this.buttonClose);
		attachWidgets(plugin, this.popupBackground, this.popupLabel, this.popupOKButton);

		//Initialize
		this.initialize();

	}
	//================================================================================================================



	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		this.isEdit = false;
		this.editPatternId = -1;
		
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBuffer * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBuffer * 2;
		int upLeftX = this.screenBuffer; 
		int upLeftY = this.screenBuffer;
		int upRightX = player.getMainScreen().getWidth() - this.screenBuffer;
		int upRightY = this.screenBuffer;
		//int downLeftX = this.screenBuffer;
		//int downLeftY = player.getMainScreen().getHeight() - this.screenBuffer;
		int downRightX = player.getMainScreen().getWidth() - this.screenBuffer;
		int downRightY = player.getMainScreen().getHeight() - this.screenBuffer;


		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		
		//-------------------------------------------------------------------
		//Upper Left Corner anchor
		//-------------------------------------------------------------------
		//Heading
		this.headingTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.headingTitle.setHeight(15).setWidth(windowWidth);

		this.headingTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.headingTitleDescription.setHeight(15).setWidth(windowWidth);
		//-------------------------------------------------------------------


		//-------------------------------------------------------------------
		//Upper Left Corner anchor
		//-------------------------------------------------------------------
		//Name field
		this.nameLabel.setX(upLeftX+26).setY(upLeftY+25);
		this.nameLabel.setWidth(40).setHeight(15);

		this.nameTextField.setX(upLeftX+55).setY(upLeftY+25);
		this.nameTextField.setWidth(250).setHeight(15);
		this.nameTextField.setText("");

		//Context field
		this.contextLabel.setX(upLeftX+12).setY(upLeftY+45);
		this.contextLabel.setWidth(40).setHeight(15);

		this.contextTextField.setX(upLeftX+55).setY(upLeftY+45);
		this.contextTextField.setWidth(250).setHeight(25);
		this.contextTextField.setText("");

		//Problem field
		this.problemLabel.setX(upLeftX+10).setY(upLeftY+75);
		this.problemLabel.setWidth(40).setHeight(15);

		this.problemTextField.setX(upLeftX+55).setY(upLeftY+75);
		this.problemTextField.setWidth(250).setHeight(50);
		this.problemTextField.setText("");

		//Solution field
		this.solutionLabel.setX(upLeftX+10).setY(upLeftY+130);
		this.solutionLabel.setWidth(40).setHeight(15);

		this.solutionTextField.setX(upLeftX+55).setY(upLeftY+130);
		this.solutionTextField.setWidth(250).setHeight(75);
		this.solutionTextField.setText("");

		//-------------------------------------------------------------------


		//-------------------------------------------------------------------
		//Up Right Corner anchor
		//-------------------------------------------------------------------
		//Pattern
		this.patternList.setX(upRightX-105).setY(upRightY+25);
		this.patternList.setWidth(100).setHeight(180);
		this.patternList.clear();
		for (MOCPattern pattern : this.plugin.getSQL().getPatterns()) {
			this.patternList.addItem(new ListWidgetItem("", pattern.getName()));
		}
		this.patternList.clearSelection();
		//-------------------------------------------------------------------



		//Window buttons
		this.buttonCancel.setX(downRightX-55).setY(downRightY-20);
		this.buttonCancel.setWidth(50).setHeight(15);

		this.buttonSave.setX(downRightX-windowWidth/2-50).setY(downRightY-20);
		this.buttonSave.setWidth(100).setHeight(15);

		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);
		
		//Pop up
		this.popupBackground.setHeight(100).setWidth(200);
		this.popupBackground.setX(player.getMainScreen().getWidth()/2 - 100).setY(player.getMainScreen().getHeight()/2 - 50);
		this.popupBackground.setVisible(false);

		this.popupLabel.setHeight(15).setWidth(40);
		this.popupLabel.setX(player.getMainScreen().getWidth()/2 - 60).setY(player.getMainScreen().getHeight()/2 - 30);
		this.popupLabel.setText("Pattern name is not unique.");
		this.popupLabel.setVisible(false);

		this.popupOKButton.setHeight(15).setWidth(50);
		this.popupOKButton.setX(player.getMainScreen().getWidth()/2 - 25).setY(player.getMainScreen().getHeight()/2 + 10);
		this.popupOKButton.setVisible(false);

	}
	//================================================================================================================



	//================================================================================================================
	//Open the GUI
	public void open(String patternName){
		this.initialize();
		
		if (patternName != null) this.loadPatternForEdit(patternName);

		this.player.getMainScreen().attachPopupScreen(this);

		this.setDirty(true);

		for(Widget widget : getAttachedWidgets()) {
			if (!widget.equals(this.popupBackground) && !widget.equals(this.popupLabel) && !widget.equals(this.popupOKButton)) widget.setVisible(true);
			widget.setDirty(true);
			
		}

	}
	
	private void loadPatternForEdit(String patternName) {
		this.nameTextField.setText(patternName);
		for (MOCPattern pattern : this.plugin.getSQL().getPatterns()) {
			if (pattern.getName().equalsIgnoreCase(patternName)) {
				this.contextTextField.setText(pattern.getContext());
				this.problemTextField.setText(pattern.getProblem());
				this.solutionTextField.setText(pattern.getSolution());
				
			}
			
		}
		
		ArrayList<String> relatedPatterns = this.plugin.getSQL().getPatternRelatedPatterns(patternName);
		
		for (int i = 0 ; i < this.patternList.getItems().length ; i++) {
			if (relatedPatterns.contains(this.patternList.getItem(i).getText())) {
				this.patternList.onSelected(i, false);
				
			}
			
		}
		
		this.editPatternId = this.plugin.getSQL().getPatternId(patternName);
		this.isEdit = true;
		
	}
	//================================================================================================================



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

		//Critique button
		if (button.equals(this.buttonSave)) {
			for (ListWidgetItem item : this.patternList.getItems()) {
				if (this.nameTextField.getText().equalsIgnoreCase("") || (!this.isEdit && item.getText().equalsIgnoreCase(this.nameTextField.getText())) ||
						(this.isEdit && item.getText().equalsIgnoreCase(this.nameTextField.getText()) && !this.plugin.getSQL().getPatternName(this.editPatternId).equalsIgnoreCase(this.nameTextField.getText()) )){
					//Duplicate pattern name
					this.popupBackground.setVisible(true);
					this.popupBackground.setDirty(true);

					this.popupLabel.setVisible(true);
					this.popupLabel.setDirty(true);

					this.popupOKButton.setVisible(true);
					this.popupOKButton.setDirty(true);

					this.setDirty(true);

					return;

				}

			}

			closeWindow();
			
			this.plugin.getSQL().savePattern(this.editPatternId, this.nameTextField.getText(), this.plugin.getGui().getLatestScreenShots().get(this.player).getName(),
												this.contextTextField.getText(), this.problemTextField.getText(), this.solutionTextField.getText(),
												((MOCListWidgetMS)this.patternList).getSelectedItems(), this.player);
			
			this.plugin.getGui().displayPatternBrowserWindowGUI(this.player, false);
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

	}
	
	public void onSelection(ListWidget listWidget) {
		if (listWidget != this.patternList) return;
		
		if (isEdit) {
			if (((MOCListWidgetMS)this.patternList).getSelectedItems().contains(this.plugin.getSQL().getPatternName(this.editPatternId))) {
				int i;
				for (i = 0 ; i < this.patternList.getItems().length ; i++) {
					if (this.patternList.getItem(i).getText().equalsIgnoreCase(this.plugin.getSQL().getPatternName(this.editPatternId))) break;
				}
				
				this.patternList.onSelected(i, false);
				
			}
			
		}
		
	}
	//================================================================================================================
}
