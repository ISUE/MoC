package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.workspace.RobotnikController;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Button;
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

public class RobotInventory extends GenericPopup {
	//----------------------------------------------------------------
	//private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 15, screenBufferY = 15;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	private Color slotColor = new Color(150,150,150);
	private Color slotColorHighlight = new Color(200,200,200);
	private SpoutItemStack air = new SpoutItemStack(0);
	private RobotnikController rc;
	//----------------------------------------------------------------
	
	//----------------------------------------------------------------
	//Background
	private GenericGradient gradientBackground;
	
	private GenericLabel labelTitle;
	
	//Buttons
	private GenericButton buttonClose, buttonClear;
	
	//Slots
	private GenericSlot slotPlayerInventory[], slotRobotInventory[];
	private GenericGradient gradientSlotPlayerInventory[], gradientSlotRobotInventory[];
	//----------------------------------------------------------------
	
	
	//================================================================================================================
	public RobotInventory(SpoutPlayer player, CodeBlocks plugin) {
        //this.plugin = plugin;
        this.player = player;
        
        //Set window transparent
        this.setTransparent(true);
        
		//Background
		gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		labelTitle = new GenericLabel("Robot Inventory");
		labelTitle.setScale(1.2F);

		//Buttons
		buttonClear = new GenericButton("C");
		buttonClear.setTooltip("Move all items to your inventory");
		buttonClear.setHoverColor(this.hoverColor);

		buttonClose = new GenericButton("X");
		buttonClose.setTooltip("Close");
		buttonClose.setHoverColor(this.hoverColor);
		
		//Slots
		slotPlayerInventory = new GenericSlot[36];
		slotRobotInventory = new GenericSlot[36];
		
		gradientSlotPlayerInventory = new GenericGradient[36];
		gradientSlotRobotInventory = new GenericGradient[36];
		
		for (int i = 0 ; i < slotPlayerInventory.length ; i++) {
			slotPlayerInventory[i] = new GenericSlot();
			slotPlayerInventory[i].setColor(this.slotColorHighlight);
			
			gradientSlotPlayerInventory[i] = new GenericGradient(this.slotColor);			
			gradientSlotPlayerInventory[i].setPriority(RenderPriority.High);
			
			slotRobotInventory[i] = new GenericSlot();
			slotRobotInventory[i].setColor(this.slotColorHighlight);
			
			gradientSlotRobotInventory[i] = new GenericGradient(this.slotColor);			
			gradientSlotRobotInventory[i].setPriority(RenderPriority.High);
			
			attachWidgets(plugin, slotPlayerInventory[i], slotRobotInventory[i], gradientSlotPlayerInventory[i], gradientSlotRobotInventory[i]);
		
		}
		
		attachWidgets(plugin, gradientBackground, buttonClear, buttonClose, labelTitle);
		
	}

	//================================================================================================================
	private void initialize() {
		//Corners and size of the window
		int windowWidth = 200;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
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
		
		buttonClear.setWidth(15).setHeight(15);
		buttonClear.setX(upRightX-35).setY(upRightY+5);
		
		PlayerInventory inventory = this.player.getInventory();
		ArrayList<ItemStack> robotInventory = rc.getRobotnik().getInventory();
		
		for (int i = 0 ; i < this.slotPlayerInventory.length ; i++) {
			slotPlayerInventory[i].setWidth(20).setHeight(20);
			slotPlayerInventory[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 120 + (i / 9) * 21);
			slotPlayerInventory[i].setItem(inventory.getItem(i));
			
			gradientSlotPlayerInventory[i].setWidth(20).setHeight(20);
			gradientSlotPlayerInventory[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 120 + (i / 9) * 21);
			
			slotRobotInventory[i].setWidth(20).setHeight(20);
			slotRobotInventory[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 25 + (i / 9) * 21);
			
			if (i < robotInventory.size()) slotRobotInventory[i].setItem(robotInventory.get(i));
			else slotRobotInventory[i].setItem(air);
			
			gradientSlotRobotInventory[i].setWidth(20).setHeight(20);
			gradientSlotRobotInventory[i].setX(upLeftX + 5 + (i % 9) * 21).setY(upLeftY + 25 + (i / 9) * 21);
			
		}

	}
	
	//================================================================================================================
	public void open(RobotnikController rc) {
		this.rc = rc;
		
		this.initialize();
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
	//================================================================================================================
	private void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	//================================================================================================================
	public void onClick(Button button) {
		//Close button
		if (button.equals(this.buttonClose)) { closeWindow(); return; }
		
		if (button.equals(this.buttonClear)) {
			for (GenericSlot rs : slotRobotInventory) {
				if (rs.getItem().getType() != Material.AIR)
					for (GenericSlot ps : slotPlayerInventory) if (ps.getItem().getType() == Material.AIR) { ps.setItem(rs.getItem()); rs.setItem(air); break; }
				
			}
			
			refresh();
			
			return;
			
		}
		
	}
	
	//================================================================================================================
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}

	//================================================================================================================
	public void syncInventories() {
		//Set robots inventory
		ArrayList<ItemStack> robotInventory = new ArrayList<ItemStack>();
		for (GenericSlot slot : slotRobotInventory) if (slot.getItem().getType() != Material.AIR) robotInventory.add(slot.getItem());
		rc.getRobotnik().setInventory(robotInventory);
		
		
		
		//Update player's inventory
		//Get item on cursor
		ItemStack itemOnCursor = player.getItemOnCursor();
		//Copy items from GUI inv to real inv
		for (int i = 0 ; i < slotPlayerInventory.length ; i++) player.getInventory().setItem(i, slotPlayerInventory[i].getItem());
		//Add item from cursor
		player.getInventory().addItem(itemOnCursor);
		
	}
	
}
