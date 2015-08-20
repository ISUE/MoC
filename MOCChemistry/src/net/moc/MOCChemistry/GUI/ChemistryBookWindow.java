package net.moc.MOCChemistry.GUI;

import net.moc.MOCChemistry.MOCChemistry;
import net.moc.MOCChemistry.GUI.Widget.MOCItemWidget;
import net.moc.MOCChemistry.GUI.Widget.MOCListWidget;
import net.moc.MOCChemistry.Recipes.CombineRecipe;
import net.moc.MOCChemistry.Recipes.SplitRecipe;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ChemistryBookWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCChemistry plugin;
	private SpoutPlayer player;
	private int screenBufferX = 115, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F;//, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(0,76,52);
	private Color hoverColor = new Color(0,96,72);
	private Color slotColor = new Color(150,150,150);
	private Color fieldColor = new Color(0,106,82);
	private SpoutItemStack air = new SpoutItemStack(0);
	private SpoutItemStack energy;
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Title
	private Label labelTitle, labelInput, labelOutput, labelEnergy;
	
	//Buttons
	private Button buttonClose, buttonSplits, buttonCombines;
	
	//Slots
	private MOCItemWidget slotInput[], slotEnergy;
	private MOCItemWidget slotOutput[];
	private Gradient gradientSlotInputBackground[], gradientSlotOutputBackground[], gradientSlotEnergy;
	
	//List
	private ListWidget listCombines, listSplits;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public ChemistryBookWindow(SpoutPlayer player, MOCChemistry plugin) {
        this.plugin = plugin;
        this.player = player;
        this.energy = new SpoutItemStack(this.plugin.getEnergyBlock(), 1);
        
        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Chemistry Recipe Book");
		this.labelTitle.setScale(this.scaleLarge);
		
		this.labelInput = new GenericLabel("Input:");
		this.labelInput.setScale(this.scaleNormal);
		
		this.labelOutput = new GenericLabel("Output:");
		this.labelOutput.setScale(this.scaleNormal);
		
		this.labelEnergy = new GenericLabel("Energy:");
		this.labelEnergy.setScale(this.scaleNormal);
		
		//Buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close Chemistry Table");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonSplits = new GenericButton("Splits");
		this.buttonSplits.setTooltip("Click to see split recipes");
		this.buttonSplits.setHoverColor(this.hoverColor);
		
		this.buttonCombines = new GenericButton("Combines");
		this.buttonCombines.setTooltip("Click to see combine recipes");
		this.buttonCombines.setHoverColor(this.hoverColor);
		
		//Lists
		this.listCombines = new MOCListWidget();
		this.listCombines.setTooltip("Select a combine recipe from the list");
		this.listCombines.setBackgroundColor(this.fieldColor);
		
		this.listSplits = new MOCListWidget();
		this.listSplits.setTooltip("Select a split recipe from the list");
		this.listSplits.setBackgroundColor(this.fieldColor);
		

		//Attach widgets to the screen
		attachWidgets(plugin, this.gradientBackground, this.labelTitle, this.labelInput, this.labelOutput, this.labelEnergy, this.buttonClose);
		attachWidgets(plugin, this.buttonCombines, this.buttonSplits, this.labelTitle, this.listSplits, this.listCombines);
		
		//Slots
		this.slotEnergy = new MOCItemWidget();
		this.slotEnergy.setItem(energy);
		
		this.gradientSlotEnergy = new GenericGradient(this.slotColor);
		this.gradientSlotEnergy.setPriority(RenderPriority.High);
		
		attachWidgets(plugin, this.slotEnergy, this.gradientSlotEnergy);
		
		this.slotInput = new MOCItemWidget[9];
		this.gradientSlotInputBackground = new GenericGradient[9];
		
		for (int i = 0 ; i < this.slotInput.length ; i++) {
			this.slotInput[i] = new MOCItemWidget();
			this.slotInput[i].setItem(air);
			attachWidget(plugin, this.slotInput[i]);
			
			this.gradientSlotInputBackground[i] = new GenericGradient(this.slotColor);			
			this.gradientSlotInputBackground[i].setPriority(RenderPriority.High);
			attachWidget(plugin, this.gradientSlotInputBackground[i]);
		
		}
		
		this.slotOutput = new MOCItemWidget[4];
		this.gradientSlotOutputBackground = new GenericGradient[4];
		
		for (int i = 0 ; i < this.slotOutput.length ; i++) {
			this.slotOutput[i] = new MOCItemWidget();
			this.slotOutput[i].setItem(air);
			attachWidget(plugin, this.slotOutput[i]);
			
			this.gradientSlotOutputBackground[i] = new GenericGradient(this.slotColor);			
			this.gradientSlotOutputBackground[i].setPriority(RenderPriority.High);
			attachWidget(plugin, this.gradientSlotOutputBackground[i]);
			
		}

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
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
		
		this.labelInput.setWidth(40).setHeight(15);
		this.labelInput.setX(upLeftX + 120).setY(upLeftY + 30);
		
		this.labelOutput.setWidth(40).setHeight(15);
		this.labelOutput.setX(upLeftX + 120).setY(upLeftY + 148);
		
		this.labelEnergy.setWidth(40).setHeight(15);
		this.labelEnergy.setX(upLeftX + 120).setY(upLeftY + 109);
		
		//Buttons
		this.buttonClose.setWidth(15).setHeight(15);
		this.buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY + 5);

		this.buttonSplits.setWidth(50).setHeight(15);
		this.buttonSplits.setX(upLeftX + 5).setY(upLeftY + 20);

		this.buttonCombines.setWidth(50).setHeight(15);
		this.buttonCombines.setX(upLeftX + 55).setY(upLeftY + 20);

		//Slots
		slotEnergy.setWidth(20).setHeight(20);
		slotEnergy.setX(upLeftX + 120).setY(upLeftY + 119);
		slotEnergy.setItem(energy);
		
		gradientSlotEnergy.setWidth(20).setHeight(20);
		gradientSlotEnergy.setX(upLeftX + 120).setY(upLeftY + 119);
		
		for (int i = 0 ; i < this.slotInput.length ; i++) {
			slotInput[i].setItem(this.air);
			slotInput[i].setWidth(20).setHeight(20);
			slotInput[i].setX(upLeftX + 120 + (i % 3) * 21).setY(upLeftY + 40 + (i / 3) * 21);
			
			gradientSlotInputBackground[i].setWidth(20).setHeight(20);
			gradientSlotInputBackground[i].setX(upLeftX + 120 + (i % 3) * 21).setY(upLeftY + 40 + (i / 3) * 21);
			
		}
		
		for (int i = 0 ; i < this.slotOutput.length ; i++) {
			slotOutput[i].setItem(this.air);
			slotOutput[i].setWidth(20).setHeight(20);
			slotOutput[i].setX(upLeftX + 120 + (i % 2) * 21).setY(upLeftY + 158 + (i / 2) * 21);
			
			gradientSlotOutputBackground[i].setWidth(20).setHeight(20);
			gradientSlotOutputBackground[i].setX(upLeftX + 120 + (i % 2) * 21).setY(upLeftY + 158 + (i / 2) * 21);
			
		}
		
		//Lists
		this.listCombines.setX(upLeftX + 5).setY(upLeftY + 40);
		this.listCombines.setWidth(100).setHeight(160);
		this.listCombines.clear();
		for (CombineRecipe recipe : this.plugin.getRecipes().getCombines()) { this.listCombines.addItem(new ListWidgetItem("", recipe.getName())); }
		this.listCombines.clearSelection();
		this.listCombines.setVisible(false);

		this.listSplits.setX(upLeftX + 5).setY(upLeftY + 40);
		this.listSplits.setWidth(100).setHeight(160);
		this.listSplits.clear();
		for (SplitRecipe recipe : this.plugin.getRecipes().getSplits()) { this.listSplits.addItem(new ListWidgetItem("", recipe.getName())); }
		this.listSplits.clearSelection();
		this.listSplits.setVisible(true);

	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open() {
		this.initialize();
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
	//================================================================================================================
	


	//================================================================================================================
	private void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		if (button.equals(this.buttonClose)) { closeWindow(); return; }
		
		if (button.equals(this.buttonCombines)) {
			this.listCombines.setVisible(true);
			this.listSplits.setVisible(false);
			
			this.listCombines.clearSelection();
			this.listSplits.clearSelection();
			
			this.listCombines.setDirty(true);
			this.listSplits.setDirty(true);
			
			for (MOCItemWidget slot : this.slotInput) { slot.setItem(air); slot.setDirty(true); }
			for (MOCItemWidget slot : this.slotOutput) { slot.setItem(air); slot.setDirty(true); }
			
			return;
			
		}
		
		if (button.equals(this.buttonSplits)) { 
			this.listCombines.setVisible(false);
			this.listSplits.setVisible(true);
			
			this.listCombines.clearSelection();
			this.listSplits.clearSelection();
			
			this.listCombines.setDirty(true);
			this.listSplits.setDirty(true);
			
			for (MOCItemWidget slot : this.slotInput) { slot.setItem(air); slot.setDirty(true); }
			for (MOCItemWidget slot : this.slotOutput) { slot.setItem(air); slot.setDirty(true); }
			
			return;
			
		}
		
	}
	//================================================================================================================
	public void onSelection(ListWidget listWidget) {
		this.slotEnergy.setItem(energy);
		
		if (listWidget == this.listCombines) {
			if (this.listCombines.getSelectedItem() == null) return;
			
			String name = this.listCombines.getSelectedItem().getText();
			
			CombineRecipe match = null;
			for (CombineRecipe recipe : this.plugin.getRecipes().getCombines()) {
				if (recipe.getName().equalsIgnoreCase(name)) {
					match = recipe;
					break;
					
				}
				
			}
			
			if (match == null) return;
			
			for (int i = 0 ; i < this.slotInput.length ; i++) { this.slotInput[i].setItem(match.getInput()[i]); this.slotInput[i].setDirty(true); }
			for (int i = 0 ; i < this.slotOutput.length ; i++) { this.slotOutput[i].setItem(match.getOutput()[i]); this.slotOutput[i].setDirty(true); }
			
		} else if (listWidget == this.listSplits) {
			if (this.listSplits.getSelectedItem() == null) return;
			
			String name = this.listSplits.getSelectedItem().getText();
			
			SplitRecipe match = null;
			for (SplitRecipe recipe : this.plugin.getRecipes().getSplits()) {
				if (recipe.getName().equalsIgnoreCase(name)) {
					match = recipe;
					break;
					
				}
				
			}
			
			if (match == null) return;
			
			for (int i = 0 ; i < this.slotInput.length ; i++) { this.slotInput[i].setItem(this.air); }
			this.slotInput[4].setItem(match.getInput());
			for (int i = 0 ; i < this.slotOutput.length ; i++) { this.slotOutput[i].setItem(match.getOutput()[i]); this.slotOutput[i].setDirty(true); }
			
			
		}
		
	}
	
}
