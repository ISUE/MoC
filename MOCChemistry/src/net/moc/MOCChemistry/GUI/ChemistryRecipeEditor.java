package net.moc.MOCChemistry.GUI;

import net.moc.MOCChemistry.MOCChemistry;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ChemistryRecipeEditor extends GenericPopup {
	MOCChemistry plugin;
	SpoutPlayer player;
	private int screenBufferX = 5, screenBufferY = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F;//, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(0,76,52);
	private Color hoverColor = new Color(0,96,72);
	private Color fieldColor = new Color(0,106,82);
	
	private String editRecipeName;
	private int editRecipeType;
	
	//Background
	GenericGradient gradientBackground;
	
	//Title
	GenericLabel labelTitle;
	
	//Buttons
	GenericButton buttonClose, buttonSave;
	
	//Name
	GenericLabel labelName;
	GenericTextField textName;
	
	//Input
	GenericLabel[] labelInput = new GenericLabel[9];
	GenericTextField[] textInput = new GenericTextField[9];
	
	//Output
	GenericLabel[] labelOutput = new GenericLabel[4];
	GenericTextField[] textOutput = new GenericTextField[4];
	
	public ChemistryRecipeEditor(SpoutPlayer player, MOCChemistry plugin) {
		this.plugin = plugin;
		this.player = player;
		
		//Set transparency
		this.setTransparent(true);
		
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Chemistry Recipe Editor");
		this.labelTitle.setScale(this.scaleLarge);
		
		//Buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonSave = new GenericButton("Save");
		this.buttonSave.setTooltip("Save recipe");
		this.buttonSave.setHoverColor(this.hoverColor);
		
		//Name
		this.labelName = new GenericLabel("Name:");
		this.labelName.setScale(this.scaleNormal);
		
		this.textName = new GenericTextField();
		this.textName.setTooltip("Recipe name");
		this.textName.setTabIndex(1);
		this.textName.setFieldColor(this.fieldColor);
		this.textName.setMaximumLines(1);
		this.textName.setMaximumCharacters(256);
		
		this.attachWidgets(plugin, this.gradientBackground, this.labelTitle, this.buttonClose, this.buttonSave, this.labelName, this.textName);
		
		//Input
		for (int i = 0 ; i < this.labelInput.length ; i++) {
			this.labelInput[i] = new GenericLabel("Input " + (i + 1) + ":");
			this.labelInput[i].setScale(this.scaleNormal);

			this.textInput[i] = new GenericTextField();
			this.textInput[i].setTooltip("Input " + i);
			this.textInput[i].setTabIndex(i + 1);
			this.textInput[i].setFieldColor(this.fieldColor);
			this.textInput[i].setMaximumLines(1);
			this.textInput[i].setMaximumCharacters(256);
			
			this.attachWidgets(plugin, this.labelInput[i], this.textInput[i]);

		}
		
		for (int i = 0 ; i < this.labelOutput.length ; i++) {
			this.labelOutput[i] = new GenericLabel("Output " + (i + 1) + ":");
			this.labelOutput[i].setScale(this.scaleNormal);

			this.textOutput[i] = new GenericTextField();
			this.textOutput[i].setTooltip("Output " + i);
			this.textOutput[i].setTabIndex(this.labelInput.length + i + 1);
			this.textOutput[i].setFieldColor(this.fieldColor);
			this.textOutput[i].setMaximumLines(1);
			this.textOutput[i].setMaximumCharacters(256);

			this.attachWidgets(plugin, this.labelOutput[i], this.textOutput[i]);
			
		}
		
		//Initialize
		this.initialize();

	}

	private void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;

		//Background
		this.gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		this.gradientBackground.setX(upLeftX).setY(upLeftY);
		
		//Title
		this.labelTitle.setWidth(40).setHeight(15);
		this.labelTitle.setX(upLeftX + 5).setY(upLeftY + 5);
		
		//Buttons
		this.buttonClose.setWidth(15).setHeight(15);
		this.buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY + 5);
		
		this.buttonSave.setWidth(50).setHeight(15);
		this.buttonSave.setX(upLeftX + windowWidth / 2 - 25).setY(upLeftY + windowHeight - 20);
		
		//Name
		this.labelName.setWidth(40).setHeight(15);
		this.labelName.setX(upLeftX + 5).setY(upLeftY + 20);
		
		this.textName.setX(upLeftX + 50).setY(upLeftY + 20);
		this.textName.setWidth(250).setHeight(15);
		this.textName.setText("");
		
		//Input
		for (int i = 0 ; i < this.labelInput.length ; i++) {
			this.labelInput[i].setWidth(40).setHeight(15);
			this.labelInput[i].setX(upLeftX + 5).setY(upLeftY + 40 + i * 20);

			this.textInput[i].setX(upLeftX + 50).setY(upLeftY + 40 + i * 20);
			this.textInput[i].setWidth(250).setHeight(15);
			this.textInput[i].setText("");

		}
		
		for (int i = 0 ; i < this.labelOutput.length ; i++) {
			this.labelOutput[i].setWidth(40).setHeight(15);
			this.labelOutput[i].setX(upLeftX + 5).setY(upLeftY + 240 + i * 20);

			this.textOutput[i].setX(upLeftX + 50).setY(upLeftY + 240 + i * 20);
			this.textOutput[i].setWidth(250).setHeight(15);
			this.textOutput[i].setText("");
			
		}

	}
	
	public void open(String recipeName, int type) {
		this.initialize();
		
		this.editRecipeName = recipeName;
		this.editRecipeType = type;
		
		if (recipeName != null) loadRecipe();
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}

	private void loadRecipe() {
		this.textName.setText(editRecipeName);
		
		if (this.editRecipeType == 1) {
			//Split
			this.textInput[0].setText(this.plugin.getRecipes().getSplitInput(editRecipeName));
			for (int i = 0 ; i < this.textOutput.length ; i++) { if (this.plugin.getRecipes().getSplitOutput(editRecipeName, i + 1) != null) this.textOutput[i].setText(this.plugin.getRecipes().getSplitOutput(editRecipeName, i + 1)); }
			
		} else if (this.editRecipeType == 2) {
			//Combine
			for (int i = 0 ; i < this.textInput.length ; i++) { if (this.plugin.getRecipes().getCombineInput(editRecipeName, i + 1) != null) this.textInput[i].setText(this.plugin.getRecipes().getCombineInput(editRecipeName, i + 1)); }
			for (int i = 0 ; i < this.textOutput.length ; i++) { if (this.plugin.getRecipes().getCombineOutput(editRecipeName, i + 1) != null) this.textOutput[i].setText(this.plugin.getRecipes().getCombineOutput(editRecipeName, i + 1)); }
			
		}
		
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
		//Close button
		if (button.equals(this.buttonClose)) { closeWindow(); return; }

		//Save button
		if (button.equals(this.buttonSave)) {
			
			
		}
		
	}

}
