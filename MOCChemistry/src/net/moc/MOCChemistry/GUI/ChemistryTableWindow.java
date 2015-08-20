package net.moc.MOCChemistry.GUI;

import net.moc.MOCChemistry.MOCChemistry;
import net.moc.MOCChemistry.GUI.Widget.MOCItemWidget;
import net.moc.MOCChemistry.Recipes.CombineRecipe;
import net.moc.MOCChemistry.Recipes.SplitRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericSlot;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Slot;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ChemistryTableWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCChemistry plugin;
	private SpoutPlayer player;
	private int screenBufferX = 115, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F;//, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(0,76,52);
	private Color hoverColor = new Color(0,96,72);
	private Color slotColor = new Color(150,150,150);
	private Color slotColorHighlight = new Color(200,200,200);
	
	private SpoutItemStack air = new SpoutItemStack(0);
	
	private CombineRecipe selectedCombine = null;
	private SplitRecipe selectedSplit = null;
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Title
	private Label labelTitle, labelInput, labelOutput, labelEnergy;
	
	//Buttons
	private Button buttonClear, buttonClose, buttonCombine, buttonSplit;
	
	//Slots
	private Slot slotInventory[], slotInput[], slotEnergy;
	private MOCItemWidget slotOutput[];
	private Gradient gradientSlotInventoryBackground[], gradientSlotInputBackground[], gradientSlotOutputBackground[], gradientSlotEnergy;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public ChemistryTableWindow(SpoutPlayer player, MOCChemistry plugin) {
        this.plugin = plugin;
        this.player = player;
        
        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Chemistry Table");
		this.labelTitle.setScale(this.scaleLarge);
		
		this.labelInput = new GenericLabel("Input:");
		this.labelInput.setScale(this.scaleNormal);
		
		this.labelOutput = new GenericLabel("Output:");
		this.labelOutput.setScale(this.scaleNormal);
		
		this.labelEnergy = new GenericLabel("Energy:");
		this.labelEnergy.setScale(this.scaleNormal);
		
		//Buttons
		this.buttonClear = new GenericButton("Clear");
		this.buttonClear.setTooltip("Clear Chemistry Table");
		this.buttonClear.setHoverColor(this.hoverColor);

		this.buttonClose = new GenericButton("Close");
		this.buttonClose.setTooltip("Close Chemistry Table");
		this.buttonClose.setHoverColor(this.hoverColor);

		this.buttonCombine = new GenericButton("Combine");
		this.buttonCombine.setTooltip("Combines blocks using energy. Output will be added to your inventory.");
		this.buttonCombine.setHoverColor(this.hoverColor);
		
		this.buttonSplit = new GenericButton("Split");
		this.buttonSplit.setTooltip("Splits single block using energy. Output will be added to your inventory.");
		this.buttonSplit.setHoverColor(this.hoverColor);
		
		//Slots
		this.slotEnergy = new GenericSlot();
		this.slotEnergy.setColor(slotColorHighlight);
		this.gradientSlotEnergy = new GenericGradient(this.slotColor);
		this.gradientSlotEnergy.setPriority(RenderPriority.High);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.gradientBackground, this.labelTitle, this.labelInput, this.labelOutput, this.labelEnergy, this.buttonClear, this.buttonClose, this.buttonCombine, this.buttonSplit, this.slotEnergy, this.gradientSlotEnergy);

		this.slotInventory = new GenericSlot[36];
		this.gradientSlotInventoryBackground = new GenericGradient[36];
		
		for (int i = 0 ; i < this.slotInventory.length ; i++) {
			this.slotInventory[i] = new GenericSlot();
			this.slotInventory[i].setColor(this.slotColorHighlight);
			attachWidget(plugin, this.slotInventory[i]);
			
			this.gradientSlotInventoryBackground[i] = new GenericGradient(this.slotColor);			
			this.gradientSlotInventoryBackground[i].setPriority(RenderPriority.High);
			attachWidget(plugin, this.gradientSlotInventoryBackground[i]);
		
		}
		
		this.slotInput = new GenericSlot[9];
		this.gradientSlotInputBackground = new GenericGradient[9];
		
		for (int i = 0 ; i < this.slotInput.length ; i++) {
			this.slotInput[i] = new GenericSlot();
			this.slotInput[i].setColor(this.slotColorHighlight);
			attachWidget(plugin, this.slotInput[i]);
			
			this.gradientSlotInputBackground[i] = new GenericGradient(this.slotColor);			
			this.gradientSlotInputBackground[i].setPriority(RenderPriority.High);
			attachWidget(plugin, this.gradientSlotInputBackground[i]);
		
		}
		
		this.slotOutput = new MOCItemWidget[4];
		this.gradientSlotOutputBackground = new GenericGradient[4];
		
		for (int i = 0 ; i < this.slotOutput.length ; i++) {
			this.slotOutput[i] = new MOCItemWidget();
//			this.slotOutput[i].setColor(this.slotColorHighlight);
//			this.slotOutput[i].setEnabled(false);
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
		this.labelInput.setX(upLeftX + 10).setY(upLeftY + 20);
		
		this.labelOutput.setWidth(40).setHeight(15);
		this.labelOutput.setX(upLeftX + 130).setY(upLeftY + 20);
		
		this.labelEnergy.setWidth(40).setHeight(15);
		this.labelEnergy.setX(upLeftX + 80).setY(upLeftY + 20);
		
		//Buttons
		this.buttonClose.setWidth(40).setHeight(15);
		this.buttonClose.setX(upLeftX + windowWidth / 2 - 20).setY(upLeftY + windowHeight - 20);

		this.buttonCombine.setWidth(40).setHeight(15);
		this.buttonCombine.setX(upLeftX + windowWidth / 2 - 20).setY(upLeftY + 51);
		this.buttonCombine.setEnabled(false);
		
		this.buttonSplit.setWidth(40).setHeight(15);
		this.buttonSplit.setX(upLeftX + windowWidth / 2 - 20).setY(upLeftY + 66);
		this.buttonSplit.setEnabled(false);
		
		this.buttonClear.setWidth(40).setHeight(15);
		this.buttonClear.setX(upLeftX + windowWidth / 2 - 20).setY(upLeftY + 81);
		this.buttonClear.setEnabled(false);

		//Slots
		slotEnergy.setWidth(20).setHeight(20);
		slotEnergy.setX(upLeftX + windowWidth / 2 - 10).setY(upLeftY + 30);
		gradientSlotEnergy.setWidth(20).setHeight(20);
		gradientSlotEnergy.setX(upLeftX + windowWidth / 2 - 10).setY(upLeftY + 30);
		
		PlayerInventory inventory = this.player.getInventory();
		for (int i = 0 ; i < this.slotInventory.length ; i++) {
			slotInventory[i].setWidth(20).setHeight(20);
			slotInventory[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 100 + (i / 9) * 21);
			slotInventory[i].setItem(inventory.getItem(i));
			
			gradientSlotInventoryBackground[i].setWidth(20).setHeight(20);
			gradientSlotInventoryBackground[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 100 + (i / 9) * 21);
			
		}
		
		for (int i = 0 ; i < this.slotInput.length ; i++) {
			slotInput[i].setWidth(20).setHeight(20);
			slotInput[i].setX(upLeftX + 10 + (i % 3) * 21).setY(upLeftY + 30 + (i / 3) * 21);
			
			gradientSlotInputBackground[i].setWidth(20).setHeight(20);
			gradientSlotInputBackground[i].setX(upLeftX + 10 + (i % 3) * 21).setY(upLeftY + 30 + (i / 3) * 21);
			
		}
		
		for (int i = 0 ; i < this.slotOutput.length ; i++) {
			slotOutput[i].setWidth(20).setHeight(20);
			slotOutput[i].setX(upLeftX + 130 + (i % 2) * 21).setY(upLeftY + 30 + (i / 2) * 21);
			slotOutput[i].setItem(this.air);
			
			gradientSlotOutputBackground[i].setWidth(20).setHeight(20);
			gradientSlotOutputBackground[i].setX(upLeftX + 130 + (i % 2) * 21).setY(upLeftY + 30 + (i / 2) * 21);
			
		}

	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open() {
		this.initialize();
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
		}
		
	}
	
	//================================================================================================================
	


	//================================================================================================================
	private void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(this.buttonClose)) { clearInputSyncInventory(); closeWindow(); return; }
		
		//Clear button
		if (button.equals(this.buttonClear)) {
			clearInputSyncInventory();
			
			this.buttonClear.setEnabled(false);
			this.buttonClear.setDirty(true);
			
			this.buttonCombine.setEnabled(false);
			this.buttonCombine.setDirty(true);
			
			this.buttonSplit.setEnabled(false);
			this.buttonSplit.setDirty(true);
			
			return;
			
		}
		
		//Combine button
		if (button.equals(this.buttonCombine)) {
			if (this.selectedCombine == null) return;
			
			//Decrement item count
			int newAmount = this.slotEnergy.getItem().getAmount() - 1;
			if (newAmount == 0) { this.slotEnergy.setItem(air); this.buttonCombine.setEnabled(false); this.buttonCombine.setDirty(true); }
			else { ItemStack stack = this.slotEnergy.getItem(); stack.setAmount(newAmount); this.slotEnergy.setItem(stack); }
			this.slotEnergy.setDirty(true);
			
			for (Slot slot : this.slotInput) {
				if (slot.getItem().getType() != Material.AIR) {
					newAmount = slot.getItem().getAmount() - 1;
					if (newAmount == 0) { slot.setItem(air); this.buttonCombine.setEnabled(false); this.buttonCombine.setDirty(true);  }
					else { ItemStack stack = slot.getItem(); stack.setAmount(newAmount); slot.setItem(stack); }
					slot.setDirty(true);
					
				}
				
			}
			
			//Sync inventory
			for (int i = 0 ; i < this.slotInventory.length ; i++) { this.player.getInventory().setItem(i, this.slotInventory[i].getItem()); }
			
			for (int i = 0 ; i < this.slotOutput.length ; i++) {
				int roll = (int) (Math.random() * 100);
				if (roll <= this.selectedCombine.getOutputChance()[i]) {
					//Add created item to inventory
					this.player.getInventory().addItem(this.selectedCombine.getOutput()[i]);
					
				}
				
			}
			
			//Sync inventory
			for (int i = 0 ; i < this.slotInventory.length ; i++) { this.slotInventory[i].setItem(this.player.getInventory().getItem(i)); this.slotInventory[i].setDirty(true); }
			
			// >:)
			this.plugin.getAccident().combineAccident(player);
			
			return;
			
		}
		
		//Split button
		if (button.equals(this.buttonSplit)) {
			Slot slot = null;
			for (Slot s : this.slotInput) { if (s.getItem().getType() != Material.AIR) { slot = s; break; } }
			if (slot == null || this.selectedSplit == null) return;
			
			//Decrement item count
			int newAmount = this.slotEnergy.getItem().getAmount() - 1;
			if (newAmount == 0) { this.slotEnergy.setItem(air); this.buttonSplit.setEnabled(false); this.buttonSplit.setDirty(true); }
			else { ItemStack stack = this.slotEnergy.getItem(); stack.setAmount(newAmount); this.slotEnergy.setItem(stack); }
			this.slotEnergy.setDirty(true);
			
			newAmount = slot.getItem().getAmount() - 1;
			if (newAmount == 0) { slot.setItem(air);  this.buttonSplit.setEnabled(false); this.buttonSplit.setDirty(true); }
			else { ItemStack stack = slot.getItem(); stack.setAmount(newAmount); slot.setItem(stack); }
			slot.setDirty(true);
			
			
			//Sync inventory
			for (int i = 0 ; i < this.slotInventory.length ; i++) { this.player.getInventory().setItem(i, this.slotInventory[i].getItem()); }
			
			for (int i = 0 ; i < this.slotOutput.length ; i++) {
				int roll = (int) (Math.random() * 100);
				if (roll <= this.selectedSplit.getOutputChance()[i]) {
					//Add created item to inventory
					this.player.getInventory().addItem(this.selectedSplit.getOutput()[i]);
					
				}
				
			}
			
			//Sync inventory
			for (int i = 0 ; i < this.slotInventory.length ; i++) { this.slotInventory[i].setItem(this.player.getInventory().getItem(i)); this.slotInventory[i].setDirty(true); }
			
			// >:)
			this.plugin.getAccident().splitAccident(player);
			return;
			
		}
		
	}
	//================================================================================================================
	public void clearInputSyncInventory() {
		//Sync inventory
		for (int i = 0 ; i < this.slotInventory.length ; i++) { this.player.getInventory().setItem(i, this.slotInventory[i].getItem()); }
		
		//Move items from input to inventory
		for (Slot slot : this.slotInput) {
			if (slot.getItem().getType() != Material.AIR) {
				this.player.getInventory().addItem(slot.getItem());
				slot.setItem(this.air);
				slot.setDirty(true);
				
			}
			
		}
		
		//Move energy from energy slot to inventory
		if (slotEnergy.getItem().getType() != Material.AIR) {
			this.player.getInventory().addItem(slotEnergy.getItem());
			slotEnergy.setItem(this.air);
			slotEnergy.setDirty(true);
			
		}
		
		//Sync inventory
		for (int i = 0 ; i < this.slotInventory.length ; i++) { this.slotInventory[i].setItem(this.player.getInventory().getItem(i)); this.slotInventory[i].setDirty(true); }
		
	}

	public void onSlotChange() {
		this.buttonClear.setEnabled(false);
		this.buttonCombine.setEnabled(false);
		this.buttonSplit.setEnabled(false);
		
		int numberOfItems = 0;
		for (Slot slot : this.slotInput) { if (slot.getItem().getType() != Material.AIR) { numberOfItems++; } }
		
		SpoutItemStack item = new SpoutItemStack(slotEnergy.getItem());
		
		if (this.plugin.getEnergyBlock().getBlockItem().getName().equalsIgnoreCase(item.getMaterial().getName())) {
			//Energy in energy slot
			if (numberOfItems > 1) {
				this.buttonClear.setEnabled(true);
				this.selectedCombine = findMatchingCombine();
				if (this.selectedCombine != null) this.buttonCombine.setEnabled(true);
				this.selectedSplit = null;
				updateSlots();

			} else if (numberOfItems == 1) {
				this.buttonClear.setEnabled(true);
				this.selectedCombine = null;
				this.selectedSplit = findMatchingSplit();
				if (this.selectedSplit != null) this.buttonSplit.setEnabled(true);
				updateSlots();
				
			} else {
				this.selectedCombine = null;
				this.selectedSplit = null;
				updateSlots();
				
			}
			
		}
		
		this.buttonClear.setDirty(true);
		this.buttonCombine.setDirty(true);
		this.buttonSplit.setDirty(true);
		
	}

	private SplitRecipe findMatchingSplit() {
		Slot slot = null;
		for (Slot s : this.slotInput) { if (s.getItem().getType() != Material.AIR) { slot = s; break; } }
		
		if (slot == null) return null;
		
		for (SplitRecipe recipe : this.plugin.getRecipes().getSplits()) {
			if (recipe.getInput().getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(slot.getItem())).getMaterial().getName())) { //TODO more checks ench etc
				//Found matching recipe
				return recipe;
				
			}
			
		}
		
		return null;
		
	}

	private CombineRecipe findMatchingCombine() {
		for (CombineRecipe recipe : this.plugin.getRecipes().getCombines()) {
			if (recipe.getInput()[0].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[0].getItem())).getMaterial().getName()) &&
				recipe.getInput()[1].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[1].getItem())).getMaterial().getName()) &&
				recipe.getInput()[2].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[2].getItem())).getMaterial().getName()) &&
				recipe.getInput()[3].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[3].getItem())).getMaterial().getName()) &&
				recipe.getInput()[4].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[4].getItem())).getMaterial().getName()) &&
				recipe.getInput()[5].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[5].getItem())).getMaterial().getName()) &&
				recipe.getInput()[6].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[6].getItem())).getMaterial().getName()) &&
				recipe.getInput()[7].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[7].getItem())).getMaterial().getName()) &&
				recipe.getInput()[8].getMaterial().getName().equalsIgnoreCase((new SpoutItemStack(this.slotInput[8].getItem())).getMaterial().getName())) { //TODO more checks ench etc
				//Found matching recipe
				return recipe;
				
			}
			
		}
		
		return null;
	}
	
	private void updateSlots() {
		//Clear
		for (MOCItemWidget slot : this.slotOutput) { slot.setItem(air); slot.setDirty(true); }
		
		//Set
		if (this.selectedCombine != null) { for (int i = 0 ; i < this.slotOutput.length ; i++) { this.slotOutput[i].setItem(this.selectedCombine.getOutput()[i]); this.slotOutput[i].setDirty(true); } }
		if (this.selectedSplit != null) { for (int i = 0 ; i < this.slotOutput.length ; i++) { this.slotOutput[i].setItem(this.selectedSplit.getOutput()[i]); this.slotOutput[i].setDirty(true); } }
		
	}

	
}
