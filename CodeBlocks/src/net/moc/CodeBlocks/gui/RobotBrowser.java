package net.moc.CodeBlocks.gui;

import java.util.ArrayList;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.gui.widgets.MOCListWidget;
import net.moc.CodeBlocks.workspace.Process;
import net.moc.CodeBlocks.workspace.RobotnikController;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RobotBrowser extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 15, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleMedium = 0.7F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle, labelTitleDescription;
	
	//Robots
	private MOCListWidget listRobot;
	
	//Window buttons
	private GenericButton buttonClose;
	private GenericButton buttonView, buttonRecall, buttonRecallAll;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public RobotBrowser(SpoutPlayer player, CodeBlocks plugin) {
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
		
		this.labelTitleDescription = new GenericLabel("Robot Browser");
		this.labelTitleDescription.setScale(this.scaleMedium);
		
		//Robots
		this.listRobot = new MOCListWidget();
		this.listRobot.setTooltip("Browse robots");
		this.listRobot.setBackgroundColor(this.textFieldColor);
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonView = new GenericButton("View");
		this.buttonView.setTooltip("Open robot controller screen");
		this.buttonView.setHoverColor(this.hoverColor);
		
		this.buttonRecall = new GenericButton("Destroy");
		this.buttonRecall.setTooltip("Recall selected robot");
		this.buttonRecall.setHoverColor(this.hoverColor);
		
		this.buttonRecallAll = new GenericButton("Destroy All");
		this.buttonRecallAll.setTooltip("Recall all robots");
		this.buttonRecallAll.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.labelTitle, this.labelTitleDescription, this.listRobot);
		attachWidgets(plugin, this.buttonClose, this.buttonView, this.buttonRecall, this.buttonRecallAll);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = 150;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;
        
		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		//Title
		this.labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.labelTitle.setHeight(15).setWidth(windowWidth);
		
		this.labelTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.labelTitleDescription.setHeight(15).setWidth(windowWidth);
		
		//Window buttons
		this.buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		this.buttonView.setX(upLeftX+5).setY(upLeftY+190);
		this.buttonView.setWidth((windowWidth - 10) / 3).setHeight(15);
		this.buttonView.setEnabled(false);
		
		this.buttonRecall.setX(upLeftX+5 + (windowWidth - 10) / 3).setY(upLeftY+190);
		this.buttonRecall.setWidth((windowWidth - 10) / 3).setHeight(15);
		this.buttonRecall.setEnabled(false);
		
		this.buttonRecallAll.setX(upLeftX+5 + ((windowWidth - 10) / 3) * 2).setY(upLeftY+190);
		this.buttonRecallAll.setWidth((windowWidth - 10) / 3).setHeight(15);
		this.buttonRecallAll.setEnabled(false);
		
		//Robots
		this.listRobot.setX(upLeftX+5).setY(upLeftY+30);
		this.listRobot.setWidth(windowWidth - 10).setHeight(155);
		
		loadRobots();
		
	}
	
	private void loadRobots() {
		listRobot.clearSelection();
		listRobot.clear();
		
		for (RobotnikController robot : plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotniks()) {
			//Only show robots in the current world
			if (robot.getRobotnik().getLocation().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
				listRobot.addItem(new ListWidgetItem("ID:"+robot.getId(), robot.getName()));
			}
			
		}
		
		if (listRobot.getSize() > 0) buttonRecallAll.setEnabled(true);
		else buttonRecallAll.setEnabled(false);
		
		refresh();
		
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
		if (button.equals(buttonClose)) { closeWindow(); return; }
		
		if (button.equals(buttonView)) {
			closeWindow();
			
			//Get matching rc
			RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotnik(Integer.parseInt(listRobot.getSelectedItem().getTitle().split(":")[1]));
			
			//Open GUI
			plugin.getGUI().displayRobotController(player, rc);
			
			return;
			
		}
		
		if (button.equals(buttonRecall)) {
			//Get matching rc
			RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotnik(Integer.parseInt(listRobot.getSelectedItem().getTitle().split(":")[1]));
			
			//Stop robotnik
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().stop(rc);
			
			//Remove robot block in world
			SpoutBlock remb = (SpoutBlock) rc.getRobotnik().getLocation().getBlock();
			remb.setCustomBlock(null);
			remb.setType(Material.AIR);
			
			//Drop robots inventory at player's location
			ArrayList<ItemStack> rInv = rc.getRobotnik().getInventory();
			plugin.getLog().sendPlayerNormal(player.getName(), "Recalling robot to your location.");
			plugin.getLog().sendPlayerNormal(player.getName(), "Extracting inventory before deactivation. Placing anything found at your location.");
			for (ItemStack item : rInv) player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.getWorld().dropItemNaturally(player.getLocation(), new SpoutItemStack(plugin.getBlocks().getRobotBlock(), 1));
			
			//Remove process
			Process process = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().getProcess(rc);
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().removeProcess(process);
			
			//Remove robotnik controller
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).removeRobotnik(rc);
			
			//Remove SQL
			plugin.getSQL().removeRobotnik(rc.getId());
			
			buttonRecall.setEnabled(false);
			buttonView.setEnabled(false);
			
			loadRobots();
			
			return;
			
		}
		
		if (button.equals(buttonRecallAll)) {
			for (ListWidgetItem  listItem : listRobot.getItems()) {
				//Get matching rc
				RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotnik(Integer.parseInt(listItem.getTitle().split(":")[1]));
				
				//Stop robotnik
				plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().stop(rc);
				
				//Remove robot block in world
				SpoutBlock remb = (SpoutBlock) rc.getRobotnik().getLocation().getBlock();
				remb.setCustomBlock(null);
				remb.setType(Material.AIR);
				
				//Drop robots inventory at player's location
				ArrayList<ItemStack> rInv = rc.getRobotnik().getInventory();
				plugin.getLog().sendPlayerNormal(player.getName(), "Recalling robot " + rc.getId() + " to your location.");
				plugin.getLog().sendPlayerNormal(player.getName(), "Extracting inventory before deactivation. Placing anything found at your location.");
				for (ItemStack item : rInv) player.getWorld().dropItemNaturally(player.getLocation(), item);
				player.getWorld().dropItemNaturally(player.getLocation(), new SpoutItemStack(plugin.getBlocks().getRobotBlock(), 1));
				
				//Remove process
				Process process = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().getProcess(rc);
				plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().removeProcess(process);
				
				//Remove robotnik controller
				plugin.getWorkspace().getPlayerWorkspace(player.getName()).removeRobotnik(rc);
				
				//Remove SQL
				plugin.getSQL().removeRobotnik(rc.getId());
				
			}
			
			buttonRecall.setEnabled(false);
			buttonView.setEnabled(false);
			
			loadRobots();
			
			return;
			
		}
		
	}
	
	//================================================================================================================
	//On button click
	public void onSelection(ListWidget listWidget) {
		if (listRobot == listWidget) {
			if (listRobot.getSelectedItem() == null) {
				buttonView.setEnabled(false);
				buttonRecall.setEnabled(false);

				refresh();
				
			} else {
				buttonView.setEnabled(true);
				buttonRecall.setEnabled(true);
				
				refresh();
				
			}
			
		}

	}

	//================================================================================================================
	//Open window
	public void open() {
		initialize();
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}
	
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
}
