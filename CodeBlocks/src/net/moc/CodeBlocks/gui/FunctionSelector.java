package net.moc.CodeBlocks.gui;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.FunctionBlock;
import net.moc.CodeBlocks.gui.widgets.MOCListWidget;
import net.moc.CodeBlocks.workspace.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
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
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class FunctionSelector extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 15, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleMedium = 0.7F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	
	private Function function;
	private SpoutBlock block;
	private boolean isOnBlockPlace;
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle, labelTitleDescription;
	
	//Function
	private MOCListWidget listFunction;
	
	//Text field
	private GenericTextField textNewName;
	
	//Window buttons
	private GenericButton buttonClose, buttonOk, buttonNew;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public FunctionSelector(SpoutPlayer player, CodeBlocks plugin) {
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
		
		this.labelTitleDescription = new GenericLabel("Function Selector");
		this.labelTitleDescription.setScale(this.scaleMedium);
		
		//Function
		this.listFunction = new MOCListWidget();
		this.listFunction.setBackgroundColor(this.textFieldColor);
		
		//Text field
		this.textNewName = new GenericTextField();
		this.textNewName.setMaximumLines(1);
		this.textNewName.setMaximumCharacters(128);
		this.textNewName.setFieldColor(textFieldColor);
		this.textNewName.setTooltip("Enter new function name here");
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonOk = new GenericButton("OK");
		this.buttonOk.setHoverColor(this.hoverColor);
		
		this.buttonNew = new GenericButton("New");
		this.buttonNew.setTooltip("Create new function");
		this.buttonNew.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.labelTitle, this.labelTitleDescription, this.textNewName);
		attachWidgets(plugin, this.listFunction, this.buttonClose, this.buttonOk, this.buttonNew);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		function = null;
		
		//Corners and size of the window
		int windowWidth = 160;
		int windowHeight = 210;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;
        int upRightX = windowWidth + this.screenBufferX;
        int upRightY = this.screenBufferY;
        
		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		//Title
		this.labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.labelTitle.setHeight(15).setWidth(windowWidth);
		
		this.labelTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.labelTitleDescription.setHeight(15).setWidth(windowWidth);
		
		//Function
		this.listFunction.setX(upLeftX+5).setY(upLeftY+25);
		this.listFunction.setWidth(150).setHeight(140);
		this.listFunction.clear();
		for (Function function : this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunctions())
			this.listFunction.addItem(new ListWidgetItem("", function.getName()));
			
		this.listFunction.setEnabled(true);
		this.listFunction.clearSelection();
		
		//Text field
		this.textNewName.setX(upLeftX+5).setY(upLeftY+170);
		this.textNewName.setHeight(15).setWidth(150);
		this.textNewName.setText("");
		
		//Window buttons
		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		this.buttonOk.setX(upLeftX+5).setY(upLeftY+190);
		this.buttonOk.setWidth(75).setHeight(15);
		this.buttonOk.setEnabled(false);

		this.buttonNew.setX(upLeftX+80).setY(upLeftY+190);
		this.buttonNew.setWidth(75).setHeight(15);

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
		if (button.equals(this.buttonClose)) {
			if (isOnBlockPlace) {
				this.block.setCustomBlock(null);
				this.block.setType(Material.AIR);
				
			}
			
			closeWindow();
			
			return;
			
		}
		
		//Ok
		if (button.equals(this.buttonOk)) {
			if (block.getCustomBlock() instanceof FunctionBlock) {
				//Paste function
				this.function.clean(this.block);
				this.function.paste(this.block);
				this.block.setCustomBlock(plugin.getBlocks().getFunctionBlock());
				
				Block b = this.block.getLocation().add(0, 1, 0).getBlock();
				try { ((SpoutBlock)b).setCustomBlock(null); } catch (Exception e) {}
				
				if (b.getType() != Material.SIGN_POST) {
					//Create event for rb, to register sign creation as if user did it
					Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(b, player));
					b.setType(Material.SIGN_POST);
					b.setData((byte) 8);
					
				}
				
				//Update sign
				Sign sign = (Sign) b.getState();
				sign.setLine(0, this.function.getName());
				sign.update();
				
			} else if (block.getCustomBlock() instanceof CallFunctionBlock) {
				//Set function name
				
				//Create sign
				Block b = this.block.getLocation().add(0, 1, 0).getBlock();
				try { ((SpoutBlock)b).setCustomBlock(null); } catch (Exception e) {}
				
				if (b.getType() != Material.SIGN_POST) {
					//Create event for rb, to register sign creation as if user did it
					Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(b, player));
					b.setType(Material.SIGN_POST);
					b.setData((byte) 8);
					
				}
				
				//Update sign
				Sign sign = (Sign) b.getState();
				sign.setLine(0, this.function.getName());
				sign.update();
				
			}
			
			closeWindow();
			
			return;
			
		}
		
		//New
		if (button.equals(this.buttonNew) && textNewName.getText().length() > 0) {
			//Create sign
			Block b = this.block.getLocation().add(0, 1, 0).getBlock();
			
			if (b.getType() != Material.SIGN_POST) {
				Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(b, player));
				
				b.setType(Material.SIGN_POST);
				b.setData((byte) 8);
				
			}
			
			//Update sign
			Sign sign = (Sign) b.getState();
			sign.setLine(0, textNewName.getText());
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
			
			if (block.getCustomBlock() instanceof FunctionBlock) {
				try {
					((SpoutBlock)b).setCustomBlock(null);
					SpoutBlock dBlock = ((SpoutBlock)block.getLocation().add(-1, 0, 0).getBlock());
					Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
					dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());

				} catch (Exception e) {}
				
			}
			
			closeWindow();
			
			return;
			
		}
		
	}
	
	//================================================================================================================
	//On button click
	public void onSelection(ListWidget listWidget) {
		if (this.listFunction != listWidget) return; //Make sure we didn't get something weird
		
		if (listWidget.getSelectedItem() != null) {
			this.function = this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunction(listWidget.getSelectedItem().getText());
			
			this.buttonOk.setEnabled(true);
			this.buttonOk.setDirty(true);
		
		} else {
			this.function = null;
			
			this.buttonOk.setEnabled(false);
			this.buttonOk.setDirty(true);
		
		}

	}

	//================================================================================================================
	//Open window
	public void open(SpoutBlock block, boolean isOnBlockPlace) {
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		this.block = block;
		this.isOnBlockPlace = isOnBlockPlace;
		
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
}
