package net.moc.CodeBlocks.gui;

import net.moc.CodeBlocks.CodeBlocks;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericSlot;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BlocksWindow  extends GenericPopup {
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 15, screenBufferY = 25;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	private Color slotColor = new Color(150,150,150);
	private Color slotColorHighlight = new Color(200,200,200);
	private SpoutItemStack air = new SpoutItemStack(0);
	private int numberOfSlots = 9;
	//----------------------------------------------------------------
	
	//----------------------------------------------------------------
	//Background
	private GenericGradient gradientBackground;
	
	private GenericLabel labelTitle;
	
	//Buttons
	private GenericButton buttonClose;
	
	//Slots
	private GenericSlot slotBlocksInventory[];
	private GenericGradient gradientSlotBlocksInventory[];
	
	private GenericButton buttonSlot[];
	//----------------------------------------------------------------
	
	
	//================================================================================================================
	public BlocksWindow(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;
        
        //Set window transparent
        this.setTransparent(true);
        
		//Background
		gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		labelTitle = new GenericLabel("Code Blocks");
		labelTitle.setScale(1.2F);

		//Buttons
		buttonClose = new GenericButton("X");
		buttonClose.setTooltip("Close");
		buttonClose.setHoverColor(this.hoverColor);
		
		//Slots
		slotBlocksInventory = new GenericSlot[numberOfSlots];
		gradientSlotBlocksInventory = new GenericGradient[numberOfSlots];
		buttonSlot = new GenericButton[numberOfSlots];
		
		for (int i = 0 ; i < slotBlocksInventory.length ; i++) {
			slotBlocksInventory[i] = new GenericSlot();
			slotBlocksInventory[i].setColor(this.slotColorHighlight);
			slotBlocksInventory[i].setEnabled(false);
			
			gradientSlotBlocksInventory[i] = new GenericGradient(slotColor);			
			gradientSlotBlocksInventory[i].setPriority(RenderPriority.High);
			
			buttonSlot[i] = new GenericButton("+");
			buttonSlot[i].setHoverColor(hoverColor);
			
			attachWidgets(plugin, slotBlocksInventory[i], gradientSlotBlocksInventory[i], buttonSlot[i]);
		
		}
		
		attachWidgets(plugin, gradientBackground, buttonClose, labelTitle);
		
	}

	//================================================================================================================
	private void initialize() {
		//Corners and size of the window
		int windowWidth = 135;
		int windowHeight = 97;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;
        int upRightX = upLeftX + windowWidth; 
        int upRightY = this.screenBufferY;
		
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
		labelTitle.setWidth(40).setHeight(15);
		labelTitle.setX(upLeftX + 5).setY(upLeftY + 5);
		
		buttonClose.setWidth(15).setHeight(15);
		buttonClose.setX(upRightX-20).setY(upRightY+5);
		
		for (int i = 0 ; i < this.slotBlocksInventory.length ; i++) {
			slotBlocksInventory[i].setWidth(20).setHeight(20);
			slotBlocksInventory[i].setX(upLeftX + 5 + (i % 3) * 42).setY(upLeftY + 30 + (i / 3) * 21);
			slotBlocksInventory[i].setItem(air);
			
			gradientSlotBlocksInventory[i].setWidth(20).setHeight(20);
			gradientSlotBlocksInventory[i].setX(upLeftX + 5 + (i % 3) * 42).setY(upLeftY + 30 + (i / 3) * 21);
			
			buttonSlot[i].setWidth(20).setHeight(20);
			buttonSlot[i].setX(upLeftX + 26 + (i % 3) * 42).setY(upLeftY + 30 + (i / 3) * 21);
			
		}
		
		loadBlocks();

	}
	
	//================================================================================================================
	public void open() {
		initialize();
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}
	
	//================================================================================================================
	private void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Re do close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	//================================================================================================================
	public void onClick(Button button) {
		//Close button
		if (button.equals(this.buttonClose)) { closeWindow(); return; }
		
		//Slot buttons
		for (int i = 0 ; i < buttonSlot.length ; i++) {
			if (buttonSlot[i] == button) {
				player.getLocation().getWorld().dropItemNaturally(player.getLocation(), slotBlocksInventory[i].getItem());
				break;
				
			}
			
		}
		
	}
	
	//================================================================================================================
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}

	//================================================================================================================
	public void loadBlocks() {
		slotBlocksInventory[0].setItem(new SpoutItemStack(plugin.getBlocks().getToolItem(), 1));
		slotBlocksInventory[1].setItem(new SpoutItemStack(plugin.getBlocks().getRobotBlock(), 64));
		slotBlocksInventory[2].setItem(new SpoutItemStack(plugin.getBlocks().getAttackBlock(), 64));
		
		slotBlocksInventory[3].setItem(new SpoutItemStack(plugin.getBlocks().getBranchBlock(), 64));
		slotBlocksInventory[4].setItem(new SpoutItemStack(plugin.getBlocks().getInteractionBlock(), 64));
		slotBlocksInventory[5].setItem(new SpoutItemStack(plugin.getBlocks().getMovementBlock(), 64));
		
		slotBlocksInventory[6].setItem(new SpoutItemStack(plugin.getBlocks().getFunctionBlock(), 64));
		slotBlocksInventory[7].setItem(new SpoutItemStack(plugin.getBlocks().getCallFunctionBlock(), 64));
		slotBlocksInventory[8].setItem(new SpoutItemStack(plugin.getBlocks().getWhiteSpaceBlock(), 64));
		
	}
	
}
