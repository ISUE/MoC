package net.moc.CodeBlocks.gui;

import java.util.ArrayList;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.gui.widgets.MOCListWidget;
import net.moc.CodeBlocks.gui.widgets.MOCListWidgetMS;
import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.Process;
import net.moc.CodeBlocks.workspace.Process.State;
import net.moc.CodeBlocks.workspace.Robotnik.Direction;
import net.moc.CodeBlocks.workspace.RobotnikController;
import net.moc.CodeBlocks.workspace.parts.Directive;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
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
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RobotController extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private RobotnikController rc;
	private Process process;
	private int speed;
	private int screenBufferX = 15, screenBufferY = 15;
	private float scaleLarge = 1.2F, scaleMedium = 0.7F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	private Color buttonOnColor = new Color(250,10,10);
	private Color buttonOffColor = new Color(10,250,10);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle, labelTitleDescription;
	
	//Name
	private GenericTextField textFieldName;
	
	//State
	private GenericTextField textFieldState;
	
	//Speed
	private GenericTextField textFieldSpeed;
	
	//Power level
	private GenericTextField textFieldPowerLevel;
	private GenericTextField textFieldHealth;
	private GenericTextField textFieldArmor;
	
	//Function
	private MOCListWidget listFunction;
	private MOCListWidgetMS listDirectives;
	
	//Window buttons
	private GenericButton buttonClose, buttonStartStop, buttonPause, buttonInventory, buttonRecall, buttonSaveName;
	private GenericButton buttonSpeedUp, buttonSpeedDown, buttonTeleport, buttonSummon, buttonDebug;
	private boolean loading = true;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public RobotController(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel(this.plugin.getDescription().getName());
		this.labelTitle.setScale(this.scaleLarge);
		
		this.labelTitleDescription = new GenericLabel("Robot Controller");
		this.labelTitleDescription.setScale(this.scaleMedium);
		
		//Name
		this.textFieldName = new GenericTextField();
		this.textFieldName.setTooltip("Name");
		this.textFieldName.setFieldColor(this.textFieldColor);
		this.textFieldName.setMaximumLines(1);
		this.textFieldName.setMaximumCharacters(100);
		
		//State
		this.textFieldState = new GenericTextField();
		this.textFieldState.setTooltip("State");
		this.textFieldState.setFieldColor(this.textFieldColor);
		this.textFieldState.setMaximumLines(1);
		this.textFieldState.setMaximumCharacters(100);
		
		//Speed
		this.textFieldSpeed = new GenericTextField();
		this.textFieldSpeed.setTooltip("Speed");
		this.textFieldSpeed.setFieldColor(this.textFieldColor);
		this.textFieldSpeed.setMaximumLines(1);
		this.textFieldSpeed.setMaximumCharacters(100);
		
		//Power Level
		this.textFieldPowerLevel = new GenericTextField();
		this.textFieldPowerLevel.setTooltip("Power Level. Add more by clicking robot block with wood or coal.");
		this.textFieldPowerLevel.setFieldColor(this.textFieldColor);
		this.textFieldPowerLevel.setMaximumLines(1);
		this.textFieldPowerLevel.setMaximumCharacters(100);
		
		this.textFieldHealth = new GenericTextField();
		this.textFieldHealth.setTooltip("Health");
		this.textFieldHealth.setFieldColor(this.textFieldColor);
		this.textFieldHealth.setMaximumLines(1);
		this.textFieldHealth.setMaximumCharacters(100);
		
		this.textFieldArmor = new GenericTextField();
		this.textFieldArmor.setTooltip("Armor");
		this.textFieldArmor.setFieldColor(this.textFieldColor);
		this.textFieldArmor.setMaximumLines(1);
		this.textFieldArmor.setMaximumCharacters(100);
		
		//Function
		this.listFunction = new MOCListWidget();
		this.listFunction.setTooltip("Assign function to a robot to run");
		this.listFunction.setBackgroundColor(this.textFieldColor);
		
		this.listDirectives = new MOCListWidgetMS();
		this.listDirectives.setTooltip("Select active directives");
		this.listDirectives.setBackgroundColor(this.textFieldColor);
		
		//Window buttons
		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonSaveName = new GenericButton("S");
		this.buttonSaveName.setTooltip("Save new name");
		this.buttonSaveName.setHoverColor(this.hoverColor);
		this.buttonSaveName.setPriority(RenderPriority.Lowest);
		
		this.buttonStartStop = new GenericButton("Start");
		this.buttonStartStop.setTooltip("Start Robot");
		this.buttonStartStop.setHoverColor(this.hoverColor);
		
		this.buttonPause = new GenericButton("Pause");
		this.buttonPause.setTooltip("Pause Robot");
		this.buttonPause.setHoverColor(this.hoverColor);
		
		this.buttonInventory = new GenericButton("Inventory");
		this.buttonInventory.setTooltip("Show Robot's Inventory");
		this.buttonInventory.setHoverColor(this.hoverColor);
		
		this.buttonRecall = new GenericButton("Destroy");
		this.buttonRecall.setTooltip("Recall robot from the world");
		this.buttonRecall.setHoverColor(this.hoverColor);
		
		this.buttonTeleport = new GenericButton("Teleport");
		this.buttonTeleport.setTooltip("Teleport to robot's location");
		this.buttonTeleport.setHoverColor(this.hoverColor);
		
		this.buttonSummon = new GenericButton("Summon");
		this.buttonSummon.setTooltip("Teleport robot to you location");
		this.buttonSummon.setHoverColor(this.hoverColor);
		
		this.buttonDebug = new GenericButton("Debug");
		this.buttonDebug.setTooltip("See how the code is executed by the robot");
		this.buttonDebug.setHoverColor(this.hoverColor);
		
		this.buttonSpeedUp = new GenericButton("+");
		this.buttonSpeedUp.setTooltip("Increase time between actions (20 = 1 second)");
		this.buttonSpeedUp.setHoverColor(this.hoverColor);
		
		this.buttonSpeedDown = new GenericButton("-");
		this.buttonSpeedDown.setTooltip("Decrease time between actions (20 = 1 second)");
		this.buttonSpeedDown.setHoverColor(this.hoverColor);
		
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.labelTitle, this.labelTitleDescription);
		attachWidgets(plugin, this.textFieldName, this.textFieldState, this.buttonSaveName, this.textFieldHealth, this.textFieldArmor, this.listDirectives);
		attachWidgets(plugin, this.textFieldSpeed, this.textFieldPowerLevel, this.listFunction, this.buttonClose, this.buttonStartStop, this.buttonSummon, this.buttonTeleport);
		attachWidgets(plugin, this.buttonPause, this.buttonInventory, this.buttonRecall, this.buttonSpeedUp, this.buttonSpeedDown, this.buttonDebug);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() / 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;
        int upRightX = upLeftX + windowWidth;
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
		this.textFieldName.setX(upLeftX+5).setY(upLeftY+25);
		this.textFieldName.setHeight(15).setWidth(windowWidth - 10);
		this.textFieldName.setText("");
		
		//Speed
		this.textFieldSpeed.setX(upLeftX+5).setY(upLeftY+45);
		this.textFieldSpeed.setHeight(15).setWidth(35);
		this.textFieldSpeed.setText("");
		this.textFieldSpeed.setEnabled(false);
		
		//Power Level
		this.textFieldPowerLevel.setX(upLeftX+140).setY(upLeftY+45);
		this.textFieldPowerLevel.setHeight(15).setWidth(68);
		this.textFieldPowerLevel.setText("");
		this.textFieldPowerLevel.setEnabled(false);
		
		this.textFieldHealth.setX(upLeftX+75).setY(upLeftY+5);
		this.textFieldHealth.setHeight(15).setWidth(55);
		this.textFieldHealth.setText("");
		this.textFieldHealth.setEnabled(false);
		
		this.textFieldArmor.setX(upLeftX+135).setY(upLeftY+5);
		this.textFieldArmor.setHeight(15).setWidth(55);
		this.textFieldArmor.setText("");
		this.textFieldArmor.setEnabled(false);
		
		this.buttonSpeedUp.setX(upLeftX+42).setY(upLeftY+44);
		this.buttonSpeedUp.setWidth(30).setHeight(8);
		
		this.buttonSpeedDown.setX(upLeftX+42).setY(upLeftY+53);
		this.buttonSpeedDown.setWidth(30).setHeight(8);
		
		//State
		this.textFieldState.setX(upLeftX+75).setY(upLeftY+45);
		this.textFieldState.setHeight(15).setWidth(62);
		this.textFieldState.setText("");
		this.textFieldState.setEnabled(false);
		
		//Function
		this.listFunction.setX(upLeftX+5).setY(upLeftY+65);
		this.listFunction.setWidth(80).setHeight(125);
		this.listFunction.clearSelection();
		this.listFunction.clear();
		
		this.listDirectives.setX(upLeftX+89).setY(upLeftY+125);
		this.listDirectives.setWidth(120).setHeight(65);
		this.listDirectives.clearSelection();
		this.listDirectives.clear();
		
		//Window buttons
		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		this.buttonSaveName.setX(upRightX-20).setY(upRightY+25);
		this.buttonSaveName.setWidth(15).setHeight(15);
		this.buttonSaveName.setEnabled(false);

		this.buttonStartStop.setX(upLeftX+89).setY(upLeftY+65);
		this.buttonStartStop.setWidth(60).setHeight(15);
		this.buttonStartStop.setEnabled(false);
		
		this.buttonPause.setX(upLeftX+89).setY(upLeftY+80);
		this.buttonPause.setWidth(60).setHeight(15);
		this.buttonPause.setEnabled(false);
		
		this.buttonInventory.setX(upLeftX+89).setY(upLeftY+110);
		this.buttonInventory.setWidth(60).setHeight(15);
		this.buttonInventory.setEnabled(true);
		
		this.buttonRecall.setX(upLeftX+148).setY(upLeftY+110);
		this.buttonRecall.setWidth(60).setHeight(15);
		
		this.buttonTeleport.setX(upLeftX+148).setY(upLeftY+65);
		this.buttonTeleport.setWidth(60).setHeight(15);
		
		this.buttonSummon.setX(upLeftX+148).setY(upLeftY+80);
		this.buttonSummon.setWidth(60).setHeight(15);
		this.buttonSummon.setEnabled(true);
		
		this.buttonDebug.setX(upLeftX+5).setY(upLeftY+190);
		this.buttonDebug.setWidth(80).setHeight(15);
		this.buttonDebug.setEnabled(true);
		
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
			rc.setName(textFieldName.getText());
			plugin.getSQL().saveRobotnik(player.getName(), rc);

			closeWindow();
			return;
			
		}
		
		if (button.equals(buttonTeleport)) {
			plugin.getLog().sendPlayerNormal(player.getName(), "Teleporting to robot's location.");
			player.teleport(rc.getRobotnik().getLocation(), TeleportCause.PLUGIN);
			
			closeWindow();
			return;
			
		}
		
		if (button.equals(buttonSummon)) {
			int y = (int) player.getLocation().getYaw();
			if(y < 0) y += 360;
	        y = y / 45;
	        
	        Direction direction;
	        if(y == 0 || y == 7){ direction = Direction.south; }
	        else if(y == 1 || y == 2){ direction = Direction.west; }
	        else if(y == 3 || y == 4){ direction = Direction.north; }
	        else { direction = Direction.east; }
			plugin.getLog().sendPlayerNormal(player.getName(), "Teleporting robot to your location and setting new heading to " + direction + ".");
	        
			rc.getRobotnik().teleportAndFace(player.getLocation(), direction);
			
			closeWindow();
			return;
			
		}
		
		if (button.equals(buttonSpeedUp)) {
			speed++;
			if (speed > 200) speed = 200;
			this.textFieldSpeed.setText(speed+"");
			
			rc.setSpeed(speed * 50);
			plugin.getSQL().saveRobotnik(player.getName(), rc);

			refresh();
			return;
			
		}
		
		if (button.equals(buttonSpeedDown)) {
			speed--;
			if (speed < 1) speed = 1;
			this.textFieldSpeed.setText(speed+"");
			
			rc.setSpeed(speed * 50);
			plugin.getSQL().saveRobotnik(player.getName(), rc);

			refresh();
			return;
			
		}

		if (button.equals(buttonStartStop)) {
			if (buttonStartStop.getText().equalsIgnoreCase("Run")) plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().run(rc);
			else plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().stop(rc);
			
			closeWindow();
			return;
			
		}
		
		if (button.equals(buttonRecall)) {
			//Stop robotnik
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().stop(rc);
			
			//Remove robot block in world
			SpoutBlock remb = (SpoutBlock) rc.getRobotnik().getLocation().getBlock();
			remb.setCustomBlock(null);
			remb.setType(Material.AIR);
			
			//Drop robots inventory at player's location
			ArrayList<ItemStack> rInv = rc.getRobotnik().getInventory();
			plugin.getLog().sendPlayerNormal(player.getName(), "Destroying robot " + rc.getName() + " ["+ rc.getId() + "].");
			plugin.getLog().sendPlayerNormal(player.getName(), "Extracting inventory before deactivation. Placing anything found at your location.");
			
			//Remove process
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().removeProcess(process);
			
			//Remove robotnik controller
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).removeRobotnik(rc);
			
			//Remove SQL
			plugin.getSQL().removeRobotnik(rc.getId());
			
			for (ItemStack item : rInv) player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.getWorld().dropItemNaturally(player.getLocation(), new SpoutItemStack(plugin.getBlocks().getRobotBlock(), 1));
			
			closeWindow();
			return;
			
		}
		
		if (button.equals(this.buttonSaveName)) {
			rc.setName(textFieldName.getText());
			plugin.getSQL().saveRobotnik(player.getName(), rc);
			
			buttonSaveName.setEnabled(false);
			
			refresh();
			return;
			
		}
		
		if (button.equals(this.buttonPause)) {
			if (buttonPause.getText().equalsIgnoreCase("Pause")) plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().pause(rc);
			else plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().resume(rc);

			closeWindow();
			return;
			
		}
		
		if (button.equals(this.buttonInventory)) {
			closeWindow();
			plugin.getGUI().displayRobotInventory(player, rc);
			
			return;
			
		}
		
		if (button.equals(this.buttonDebug)) {
			if (listFunction.getSelectedItem() == null) return;
			
			if (rc.getDebugBase() != null) {
				plugin.getLog().sendPlayerNormal(player.getName(), "Function debugging turned off.");
				rc.setDebugBase(null);
				buttonDebug.setColor(buttonOffColor);
				return;
				
			}
			
			Function function = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunction(listFunction.getSelectedItem().getText());
			
			if (function == null) return;
			
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).getDebugBaseQueue().put(player.getName(), rc);
			plugin.getLog().sendPlayerNormal(player.getName(), "To start function debugging place a Function Block.");
			
			closeWindow();
			
			return;
			
		}
		
	}
	
	//================================================================================================================
	//On button click
	public void onSelection(ListWidget listWidget) {
		if (listWidget == listFunction) {
			if (listFunction.getSelectedItem() == null) {
				rc.setFunctionName("");
				plugin.getSQL().saveRobotnik(player.getName(), rc);

			} else {
				rc.setFunctionName(this.listFunction.getSelectedItem().getText());
				plugin.getSQL().saveRobotnik(player.getName(), rc);

			}
			
		} else if (listWidget == listDirectives) {
			if (loading) return;
			
			rc.getRobotnik().enableDirectives(listDirectives.getSelectedItems());
			plugin.getSQL().saveRobotnik(player.getName(), rc);
			
		}
		
	}

	private void loadRobotInfo() {
		process = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getVirtualMachine().getProcess(rc);

		//Set run/stop buttons
		if (process == null) {
			buttonPause.setText("Pause");
			buttonPause.setEnabled(false);
			buttonStartStop.setText("Run");
			textFieldState.setText(State.READY.toString());

		} else if (process.getState() == State.READY) {
			buttonPause.setText("Pause");
			buttonPause.setEnabled(false);
			buttonStartStop.setText("Run");
			textFieldState.setText(State.READY.toString());

		} else if (process.getState() == State.RUNNING) {
			buttonPause.setText("Pause");
			buttonPause.setEnabled(true);
			buttonStartStop.setText("Stop");
			textFieldState.setText(State.RUNNING.toString());
			buttonInventory.setEnabled(false);
			buttonSummon.setEnabled(false);

		} else if (process.getState() == State.SUSPENDED) {
			buttonPause.setText("Resume");
			buttonPause.setEnabled(true);
			buttonStartStop.setText("Stop");
			textFieldState.setText(State.SUSPENDED.toString());

		}

		//Set text fields
		textFieldName.setText(rc.getName());
		
		textFieldHealth.setText((int)(rc.getRobotnik().getStats().getHealthCurrent() / rc.getRobotnik().getStats().getHealthMax() * 100) +"%");
		textFieldArmor.setText((int)(rc.getRobotnik().getStats().getArmorCurrent() / rc.getRobotnik().getStats().getArmorMax() * 100) +"%");

		speed = rc.getSpeed() / 50;
		textFieldSpeed.setText(speed+"");
		
		textFieldPowerLevel.setText((int)rc.getRobotnik().getStats().getPowerCurrent()+"");
		
		if (rc != null && rc.getDebugBase() != null) { buttonDebug.setColor(buttonOnColor); }
		else { buttonDebug.setColor(buttonOffColor); }
		
		//Enable buttons
		buttonStartStop.setEnabled(true);
		
		//Set function list
		listFunction.clearSelection();
		listFunction.clear();
		for (Function function : plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunctions()) {
			listFunction.addItem(new ListWidgetItem("", function.getName()));
			if (rc.getFunctionName().equalsIgnoreCase(function.getName())) listFunction.setSelection(listFunction.getSize()-1);
			
		}
		
		listDirectives.clearSelection();
		listDirectives.clear();
		for (Directive d : plugin.getWorkspace().getDirectives()) listDirectives.addItem(new ListWidgetItem("", d.getName()));
		
		loading  = true;
		for (Directive d : rc.getRobotnik().getDirectivesEnabled()) for (int i = 0 ; i < listDirectives.getSize() ; i++) {
			if (listDirectives.getItem(i).getText().equalsIgnoreCase(d.getName())) { listDirectives.onSelected(i, false); }
			
		}
		loading = false;
		
		refresh();
		
	}



	//================================================================================================================
	//Open window
	public void open(SpoutBlock block) {
		if (block == null) return;
		
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		//Get robotnik controller if exists already
		rc = this.plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotnik(block);

		if (rc == null) {
			Direction direction = Direction.east;
			switch (block.getCustomBlockData()) {
			case 0: direction = Direction.north; break;
			case 1: direction = Direction.west; break;
			case 2: direction = Direction.south; break;
			case 3: direction = Direction.east; break;

			}

			rc = new RobotnikController(plugin, player.getName(), block.getLocation(), direction, null);
			plugin.getWorkspace().getPlayerWorkspace(player.getName()).addRobotnikController(rc);
			plugin.getSQL().saveRobotnik(player.getName(), rc);

		}

		loadRobotInfo();
		
		refresh();
		
	}
	
	public void open(RobotnikController rc) {
		if (rc == null) return;
		
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		this.rc = rc;
		
		loadRobotInfo();
		
		refresh();
		
	}
	
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}



	public void onTextChange(TextField textField) {
		if (textFieldName != textField) return;
		
		buttonSaveName.setEnabled(true);
		
	}
	
}
