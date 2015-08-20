package net.moc.CodeBlocks.gui;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.gui.widgets.MOCListWidget;
import net.moc.CodeBlocks.workspace.Function;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class FunctionBrowser extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 50, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleMedium = 0.7F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	
	private Function function;
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle, labelTitleDescription;
	
	//Function
	private MOCListWidget listFunction;
	
	//Description
	private GenericTextField textName, textDescription;
	
	//Window buttons
	private GenericButton buttonClose, buttonDelete, buttonEdit, buttonSave;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public FunctionBrowser(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel(this.plugin.getDescription().getFullName());
		this.labelTitle.setScale(this.scaleLarge);
		
		this.labelTitleDescription = new GenericLabel("Function Browser");
		this.labelTitleDescription.setScale(this.scaleMedium);
		
		//Name
		this.textName = new GenericTextField();
		this.textName.setTooltip("Name");
		this.textName.setFieldColor(this.textFieldColor);
		this.textName.setMaximumLines(1);
		this.textName.setMaximumCharacters(1000);
		
		//Description
		this.textDescription = new GenericTextField();
		this.textDescription.setTooltip("Description");
		this.textDescription.setFieldColor(this.textFieldColor);
		this.textDescription.setMaximumLines(12);
		this.textDescription.setMaximumCharacters(1000);
		
		//Function
		this.listFunction = new MOCListWidget();
		this.listFunction.setTooltip("Assign function to a robot to run");
		this.listFunction.setBackgroundColor(this.textFieldColor);
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonDelete = new GenericButton("Delete");
		this.buttonDelete.setTooltip("Delete function from database");
		this.buttonDelete.setHoverColor(this.hoverColor);
		
		this.buttonEdit = new GenericButton("Edit");
		this.buttonEdit.setTooltip("Paste function in world for edit");
		this.buttonEdit.setHoverColor(this.hoverColor);
		
		this.buttonSave = new GenericButton("Save");
		this.buttonSave.setTooltip("Save updated description");
		this.buttonSave.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.labelTitle, this.labelTitleDescription, this.textName, this.textDescription);
		attachWidgets(plugin, this.listFunction, this.buttonClose, this.buttonDelete, this.buttonEdit, this.buttonSave);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		function = null;
		
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;
        int upRightX = player.getMainScreen().getWidth() - this.screenBufferX;
        int upRightY = this.screenBufferY;
        
		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		//Title
		this.labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.labelTitle.setHeight(15).setWidth(windowWidth);
		
		this.labelTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.labelTitleDescription.setHeight(15).setWidth(windowWidth);
		
		//Name
		this.textName.setX(upLeftX+175).setY(upLeftY+25);
		this.textName.setHeight(15).setWidth(145);
		this.textName.setText("");
		this.textName.setEnabled(false);
		
		//Description
		this.textDescription.setX(upLeftX+175).setY(upLeftY+40);
		this.textDescription.setHeight(145).setWidth(145);
		this.textDescription.setText("");
		this.textDescription.setEnabled(false);
		
		//Function
		this.listFunction.setX(upLeftX+5).setY(upLeftY+25);
		this.listFunction.setWidth(160).setHeight(160);
		this.listFunction.clear();
		for (Function function : this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunctions())
			this.listFunction.addItem(new ListWidgetItem("", function.getName()));
			
		this.listFunction.setEnabled(true);
		this.listFunction.clearSelection();
		
		//Window buttons
		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		this.buttonDelete.setX(upLeftX+5).setY(upLeftY+190);
		this.buttonDelete.setWidth(80).setHeight(15);
		this.buttonDelete.setEnabled(false);

		this.buttonEdit.setX(upLeftX+85).setY(upLeftY+190);
		this.buttonEdit.setWidth(80).setHeight(15);
		this.buttonEdit.setEnabled(false);

		this.buttonSave.setX(upLeftX+175).setY(upLeftY+190);
		this.buttonSave.setWidth(145).setHeight(15);
		this.buttonSave.setEnabled(false);

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
		
		//Save
		if (button.equals(this.buttonSave)) {
			this.function.setName(this.textName.getText());
			this.function.setDescription(this.textDescription.getText());
			
			//Save to SQL
			this.plugin.getSQL().saveFunction(player, function);
			
			//Update list
			this.listFunction.getSelectedItem().setText(this.textName.getText());
			this.listFunction.setDirty(true);
			
			this.buttonSave.setEnabled(false);
			this.buttonSave.setDirty(true);
			
			return;
			
		}
		
		//Edit
		if (button.equals(this.buttonEdit)) {
			this.function.clean((SpoutBlock) player.getLocation().getBlock());
			this.function.paste((SpoutBlock) player.getLocation().getBlock());
			
			closeWindow();
			
			return;
			
		}
		
		//Delete
		if (button.equals(this.buttonDelete)) {
			this.plugin.getSQL().removeFunction(this.function.getId());
			
			this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunctions().remove(this.function);
			
			this.listFunction.removeItem(this.listFunction.getSelectedItem());
			this.listFunction.clearSelection();
			this.listFunction.setDirty(true);
			
			this.textName.setEnabled(false);
			this.textName.setText("");
			this.textName.setDirty(true);
			
			this.textDescription.setEnabled(false);
			this.textDescription.setText("");
			this.textDescription.setDirty(true);
			
			this.buttonDelete.setEnabled(false);
			this.buttonDelete.setDirty(true);
		
			this.buttonEdit.setEnabled(false);
			this.buttonEdit.setDirty(true);
			
			this.buttonSave.setEnabled(false);
			this.buttonSave.setDirty(true);
			
		}
		
	}
	
	//================================================================================================================
	//On button click
	public void onSelection(ListWidget listWidget) {
		if (this.listFunction != listWidget) return; //Make sure we didn't get something weird
		
		if (listWidget.getSelectedItem() != null) {
			this.function = this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunction(listWidget.getSelectedItem().getText());
			
			this.textName.setText(this.function.getName());
			this.textName.setEnabled(true);
			this.textName.setDirty(true);
			
			this.textDescription.setText(this.function.getDescription());
			this.textDescription.setEnabled(true);
			this.textDescription.setDirty(true);
			
			this.buttonDelete.setEnabled(true);
			this.buttonDelete.setDirty(true);
		
			this.buttonEdit.setEnabled(true);
			this.buttonEdit.setDirty(true);
			
		} else {
			this.function = null;
			
			this.textName.setEnabled(false);
			this.textName.setText("");
			this.textName.setDirty(true);
			
			this.textDescription.setEnabled(false);
			this.textDescription.setText("");
			this.textDescription.setDirty(true);
			
			this.buttonDelete.setEnabled(false);
			this.buttonDelete.setDirty(true);
		
			this.buttonEdit.setEnabled(false);
			this.buttonEdit.setDirty(true);
			
			this.buttonSave.setEnabled(false);
			this.buttonSave.setDirty(true);
			
		}

	}

	//================================================================================================================
	public void onTextChange(TextField textField) {
		if (this.listFunction.getSelectedItem() == null) return;
		
		this.buttonSave.setEnabled(true);
		this.buttonSave.setDirty(true);
		
	}
	//================================================================================================================
	//Open window
	public void open() {
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
}
