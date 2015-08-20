package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import java.util.Arrays;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.attack.AttackFarBlock;
import net.moc.CodeBlocks.blocks.attack.AttackNearBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.interaction.BuildBlock;
import net.moc.CodeBlocks.gui.widgets.MOCComboBox;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
import net.moc.CodeBlocks.workspace.parts.StackFrame.FunctionVariables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericComboBox;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SignEditWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin; public CodeBlocks getPlugin() { return plugin; }
	private SpoutPlayer player;
	private int screenBufferX = 15, screenBufferY = 40;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Type
	private GenericLabel lableType;
	private GenericTextField textType;
	private MOCComboBox comboboxType;
	
	//Generic Lines
	private GenericLabel labelLine1, labelLine2, labelLine3, labelLine4;
	private GenericTextField textLine1, textLine2, textLine3, textLine4;
	
	//Drop downs for different types
	private GenericComboBox comboboxSidesAll, comboboxSidesNormal, comboboxItems, comboboxNPCAll, comboboxNPCAllRobot, comboboxNPCRobot;
	private GenericComboBox comboboxIsNot;
	private MOCComboBox comboVar1, comboVar2, comboVar3, comboVar4;
	
	//Window buttons
	private GenericButton buttonCancel, buttonSave;
	//----------------------------------------------------------------
	
	private Block signBlock;
	private SpoutBlock block;
	//================================================================================================================
	
	
	
	//================================================================================================================
	public SignEditWindow(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);

		//Type
		lableType = new GenericLabel("Type");
		
		textType = new GenericTextField();
		textType.setMaximumLines(1);
		textType.setMaximumCharacters(128);
		textType.setFieldColor(textFieldColor);
		
		comboboxType = new MOCComboBox();
		comboboxType.setText("");
		comboboxType.setHoverColor(hoverColor);
		comboboxType.setItems(Arrays.asList("Sense Block", "Sense NPC", "Sense Robot"));
		
		comboboxIsNot = new GenericComboBox();
		comboboxIsNot.setText("");
		comboboxIsNot.setHoverColor(hoverColor);
		comboboxIsNot.setItems(Arrays.asList("is", "is not"));
		
		//Sign lines
		labelLine1 = new GenericLabel("");
		labelLine2 = new GenericLabel("");
		labelLine3 = new GenericLabel("");
		labelLine4 = new GenericLabel("");
		
		textLine1 = new GenericTextField();
		textLine1.setMaximumLines(1);
		textLine1.setMaximumCharacters(128);
		textLine1.setFieldColor(textFieldColor);
		
		textLine2 = new GenericTextField();
		textLine2.setMaximumLines(1);
		textLine2.setMaximumCharacters(128);
		textLine2.setFieldColor(textFieldColor);
		
		textLine3= new GenericTextField();
		textLine3.setMaximumLines(1);
		textLine3.setMaximumCharacters(128);
		textLine3.setFieldColor(textFieldColor);
		
		textLine4 = new GenericTextField();
		textLine4.setMaximumLines(1);
		textLine4.setMaximumCharacters(128);
		textLine4.setFieldColor(textFieldColor);
		
		//Drop downs for different types
		ArrayList<String> sides = new ArrayList<String>(); for ( RobotSide rs : RobotSide.values()) sides.add(rs.toString().toLowerCase());
		comboboxSidesAll = new GenericComboBox();
		comboboxSidesAll.setText("");
		comboboxSidesAll.setHoverColor(hoverColor);
		comboboxSidesAll.setItems(sides);
		
		comboboxSidesNormal = new GenericComboBox();
		comboboxSidesNormal.setText("");
		comboboxSidesNormal.setHoverColor(hoverColor);
		comboboxSidesNormal.setItems(sides.subList(0, sides.size() - 2));
		
		ArrayList<String> materials = new ArrayList<String>(); for (Material m : Material.values()) if (m.isBlock()) materials.add(m.getId() + ": " + m.toString().toLowerCase());
		comboboxItems = new GenericComboBox();
		comboboxItems.setText("");
		comboboxItems.setHoverColor(hoverColor);
		comboboxItems.setItems(materials);
		
		comboboxNPCAll = new GenericComboBox();
		comboboxNPCAll.setText("");
		comboboxNPCAll.setHoverColor(hoverColor);
		comboboxNPCAll.setItems(Arrays.asList("Monster", "Animal", "Creature", "Vehicle", "Player", "All"));
		
		comboboxNPCAllRobot = new GenericComboBox();
		comboboxNPCAllRobot.setText("");
		comboboxNPCAllRobot.setHoverColor(hoverColor);
		comboboxNPCAllRobot.setItems(Arrays.asList("Monster", "Animal", "Creature", "Player", "Robot", "All"));
		
		comboboxNPCRobot = new GenericComboBox();
		comboboxNPCRobot.setText("");
		comboboxNPCRobot.setHoverColor(hoverColor);
		comboboxNPCRobot.setItems(Arrays.asList("Entity", "Robot", "All"));
		
		ArrayList<String> fvars = new ArrayList<String>(); fvars.add("Static"); for (FunctionVariables fv : FunctionVariables.values()) fvars.add(fv.toString().toLowerCase());
		comboVar1 = new MOCComboBox();
		comboVar1.setText("");
		comboVar1.setHoverColor(hoverColor);
		comboVar1.setItems(fvars);
		
		comboVar2 = new MOCComboBox();
		comboVar2.setText("");
		comboVar2.setHoverColor(hoverColor);
		comboVar2.setItems(fvars);
		
		comboVar3 = new MOCComboBox();
		comboVar3.setText("");
		comboVar3.setHoverColor(hoverColor);
		comboVar3.setItems(fvars);
		
		comboVar4 = new MOCComboBox();
		comboVar4.setText("");
		comboVar4.setHoverColor(hoverColor);
		comboVar4.setItems(fvars);
		
		//Window buttons
		buttonCancel = new GenericButton("Cancel");
		buttonCancel.setTooltip("Cancel");
		buttonCancel.setHoverColor(hoverColor);

		buttonSave = new GenericButton("Set");
		buttonSave.setTooltip("Set");
		buttonSave.setHoverColor(hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, gradientBackground, lableType, textType, comboboxType, comboboxIsNot);
		attachWidgets(plugin, labelLine1, labelLine2, labelLine3, labelLine4, textLine1, textLine2, textLine3, textLine4);
		attachWidgets(plugin, comboboxSidesAll, comboboxSidesNormal, comboboxItems, comboboxNPCAll, comboboxNPCAllRobot, comboboxNPCRobot);
		attachWidgets(plugin, comboVar1, comboVar2, comboVar3, comboVar4);
		attachWidgets(plugin, buttonCancel, buttonSave);

		//Initialize
		initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = 300;
		int windowHeight = 125;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Type
		lableType.setX(upLeftX+5).setY(upLeftY+5);
		lableType.setHeight(15).setWidth(40);
		
		textType.setX(upLeftX+70).setY(upLeftY+5);
		textType.setHeight(15).setWidth(150);
		textType.setText("");
		textType.setVisible(false);
		textType.setEnabled(false);
		
		comboboxType.setX(upLeftX+70).setY(upLeftY+5);
		comboboxType.setHeight(15).setWidth(100);
		comboboxType.setVisible(false);
		
		comboboxIsNot.setX(upLeftX+170).setY(upLeftY+5);
		comboboxIsNot.setHeight(15).setWidth(50);
		comboboxIsNot.setVisible(false);
		comboboxIsNot.setSelection(0);
		
		//Sign lines
		labelLine1.setText("");
		labelLine1.setVisible(false);
		labelLine1.setX(upLeftX+5).setY(upLeftY+25);
		labelLine1.setHeight(15).setWidth(40);
		
		labelLine2.setText("");
		labelLine2.setVisible(false);
		labelLine2.setX(upLeftX+5).setY(upLeftY+45);
		labelLine2.setHeight(15).setWidth(40);
		
		labelLine3.setText("");
		labelLine3.setVisible(false);
		labelLine3.setX(upLeftX+5).setY(upLeftY+65);
		labelLine3.setHeight(15).setWidth(40);
		
		labelLine4.setText("");
		labelLine4.setVisible(false);
		labelLine4.setX(upLeftX+5).setY(upLeftY+85);
		labelLine4.setHeight(15).setWidth(40);
		
		textLine1.setX(upLeftX+70).setY(upLeftY+25);
		textLine1.setHeight(15).setWidth(150);
		textLine1.setText("");
		textLine1.setVisible(false);
		textLine1.setEnabled(false);
		textLine1.setFocus(false);
		
		textLine2.setX(upLeftX+70).setY(upLeftY+45);
		textLine2.setHeight(15).setWidth(150);
		textLine2.setText("");
		textLine2.setVisible(false);
		textLine2.setEnabled(false);
		textLine2.setFocus(false);
		
		textLine3.setX(upLeftX+70).setY(upLeftY+65);
		textLine3.setHeight(15).setWidth(150);
		textLine3.setText("");
		textLine3.setVisible(false);
		textLine3.setEnabled(false);
		textLine3.setFocus(false);
		
		textLine4.setX(upLeftX+70).setY(upLeftY+85);
		textLine4.setHeight(15).setWidth(150);
		textLine4.setText("");
		textLine4.setVisible(false);
		textLine4.setEnabled(false);
		textLine4.setFocus(false);
		
		//Drop downs for different types
		comboVar1.setX(upLeftX+225).setY(upLeftY+25);
		comboVar1.setHeight(15).setWidth(70);
		comboVar1.setVisible(false);
		
		comboVar2.setX(upLeftX+225).setY(upLeftY+45);
		comboVar2.setHeight(15).setWidth(70);
		comboVar2.setVisible(false);
		
		comboVar3.setX(upLeftX+225).setY(upLeftY+65);
		comboVar3.setHeight(15).setWidth(70);
		comboVar3.setVisible(false);
		
		comboVar4.setX(upLeftX+225).setY(upLeftY+85);
		comboVar4.setHeight(15).setWidth(70);
		comboVar4.setVisible(false);
		
		comboboxSidesAll.setX(upLeftX+70).setY(upLeftY+25);
		comboboxSidesAll.setHeight(15).setWidth(150);
		comboboxSidesAll.setVisible(false);
		
		comboboxSidesNormal.setX(upLeftX+70).setY(upLeftY+25);
		comboboxSidesNormal.setHeight(15).setWidth(150);
		comboboxSidesNormal.setVisible(false);
		
		comboboxItems.setX(upLeftX+70).setY(upLeftY+45);
		comboboxItems.setHeight(15).setWidth(150);
		comboboxItems.setVisible(false);
		
		comboboxNPCAll.setX(upLeftX+70).setY(upLeftY+45);
		comboboxNPCAll.setHeight(15).setWidth(150);
		comboboxNPCAll.setVisible(false);
		
		comboboxNPCAllRobot.setX(upLeftX+70).setY(upLeftY+45);
		comboboxNPCAllRobot.setHeight(15).setWidth(150);
		comboboxNPCAllRobot.setVisible(false);
		
		comboboxNPCRobot.setX(upLeftX+70).setY(upLeftY+45);
		comboboxNPCRobot.setHeight(15).setWidth(150);
		comboboxNPCRobot.setVisible(false);
		
		//Window buttons
		buttonCancel.setX(upLeftX+5).setY(upLeftY+105);
		buttonCancel.setWidth(105).setHeight(15);
		
		buttonSave.setX(upLeftX+115).setY(upLeftY+105);
		buttonSave.setWidth(105).setHeight(15);
		
	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(SpoutBlock block){
		initialize();
		
		this.block = block;
		
		//Get sign block and make sure it is set to sign post
		signBlock = block.getLocation().add(0, 1, 0).getBlock();
		try { ((SpoutBlock)signBlock).setCustomBlock(null); } catch (Exception e) {}
		
		String[] lines = {"","","",""};
		
		if (signBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) signBlock.getState();
			lines[0] = sign.getLine(0);
			lines[1] = sign.getLine(1);
			lines[2] = sign.getLine(2);
			lines[3] = sign.getLine(3);
			sign.update();
			
		}
		
		if (this.block.getCustomBlock() instanceof CaseBlock || this.block.getCustomBlock() instanceof IfBlock || this.block.getCustomBlock() instanceof WhileBlock) {
			comboboxType.setVisible(true);
			comboboxIsNot.setVisible(true);
			
			comboVar1.setVisible(true);
			comboVar2.setVisible(true);
			comboVar3.setVisible(true);
			comboVar4.setVisible(true);
			
			comboVar1.setSelection(0);
			comboVar2.setSelection(0);
			comboVar3.setSelection(0);
			comboVar4.setSelection(0);
			
			labelLine1.setText("Side");
			labelLine1.setTooltip("Robot Side");
			labelLine1.setVisible(true);
			
			labelLine2.setVisible(true);
			
			labelLine3.setText("Dist");
			labelLine3.setTooltip("Distance");
			labelLine3.setVisible(true);
			
			labelLine4.setText("Data");
			labelLine4.setTooltip("Block data value");
			
			//Side
			
			//Target
			
			//Distance
			textLine3.setText(lines[2]);
			textLine3.setFocus(true);
			textLine3.setEnabled(true);
			textLine3.setVisible(true);
			for (int i = 1 ; i < comboVar3.getItems().size() ; i++) if ( lines[2].equalsIgnoreCase(comboVar3.getItems().get(i))) {textLine3.setEnabled(false); comboVar3.setSelection(i); }
			
			//Data
			textLine4.setText(lines[3]);
			textLine4.setEnabled(true);
			textLine4.setVisible(true);
			for (int i = 1 ; i < comboVar4.getItems().size() ; i++) if ( lines[3].equalsIgnoreCase(comboVar4.getItems().get(i))) {textLine4.setEnabled(false); comboVar4.setSelection(i); }
			
			setSideListAll(lines[0]);
			setTargetListTrue(lines[1]);
			
		} else if (this.block.getCustomBlock() instanceof BuildBlock) {
			textType.setText("Build Block");
			textType.setVisible(true);
			
			comboVar1.setVisible(true);
			comboVar2.setVisible(true);
			comboVar3.setVisible(true);
			
			comboVar1.setSelection(0);
			comboVar2.setSelection(0);
			comboVar3.setSelection(0);
			
			labelLine1.setText("Side");
			labelLine2.setText("Block");
			labelLine3.setText("Data");
			
			labelLine1.setVisible(true);
			labelLine2.setVisible(true);
			labelLine3.setVisible(true);
			
			setSideListNormal(lines[0]);
			
			setBlockList(lines[1]);
			
			textLine3.setText(lines[2]);
			textLine3.setFocus(true);
			textLine3.setEnabled(true);
			textLine3.setVisible(true);
			for (int i = 1 ; i < comboVar3.getItems().size() ; i++) if ( lines[2].equalsIgnoreCase(comboVar3.getItems().get(i))) {textLine3.setEnabled(false); comboVar3.setSelection(i); }
			
			textLine4.setVisible(false);
			
		} else if (this.block.getCustomBlock() instanceof AttackFarBlock) {
			//side type
			comboVar1.setVisible(true);
			comboVar2.setVisible(true);
			
			comboVar1.setSelection(0);
			comboVar2.setSelection(0);
			
			textType.setText("Attack Far Side");
			textType.setVisible(true);
			
			labelLine1.setText("Side");
			labelLine1.setVisible(true);
			
			labelLine2.setText("NPC");
			labelLine2.setVisible(true);
			
			setSideListNormal(lines[0]);
			setListEntityRobot(lines[1]);
			
		} else if (this.block.getCustomBlock() instanceof AttackNearBlock) {
			//side type
			comboVar2.setVisible(true);
			
			comboVar2.setSelection(0);
			
			textType.setText("Attack Near");
			textType.setVisible(true);
			
			labelLine2.setText("NPC");
			labelLine2.setVisible(true);
			
			setListEntityAllRobot(lines[1]);
			
		} else {
			///Get rid of the sign as it is in a wrong spot anyways.
			signBlock.setType(Material.AIR);
			
			return;
		}
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}
	
	public void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}

	//================================================================================================================
	private void setTargetListTrue(String value) {
		value = value.trim().toLowerCase();
		if (value.length() == 0) { setBlockList(value); return; }
		
		if (value.startsWith("!")) { value = value.substring(1); comboboxIsNot.setSelection(1); }
		else { comboboxIsNot.setSelection(0); }
		
		for (int i = 1 ; i < comboVar2.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar2.getItems().get(i))) {
				textLine2.setVisible(true);
				textLine2.setEnabled(false);
				textLine2.setText(value);
				comboVar2.setSelection(i);
				comboboxType.setSelection(0);
				
				refresh();
				return;
				
			}
			
		}
		
		try {
			Integer.parseInt(value);
			setBlockList(value);
			
			return;
			
		} catch (NumberFormatException e) {
			if (value.equalsIgnoreCase("Monster") || value.equalsIgnoreCase("Animal") || value.equalsIgnoreCase("Creature") ||
				value.equalsIgnoreCase("Vehicle") || value.equalsIgnoreCase("Player") || value.equalsIgnoreCase("All")) {
				
				setListEntityAll(value);

			} else {
				labelLine4.setVisible(false);
				textLine4.setVisible(false);
				
				textLine2.setVisible(true);
				textLine2.setText("robot");
				textLine2.setEnabled(false);
				
				labelLine2.setText("Robot");
				comboboxType.setSelection(2);
				
				return;
				
			}
			
		}
		
	}
	
	private void setListEntityAll(String value) {
		value = value.trim().toLowerCase();
		comboboxNPCAll.setVisible(true);
		comboboxNPCAll.setSelection(0);
		
		labelLine4.setVisible(false);
		labelLine2.setText("NPC");
		
		textLine4.setVisible(false);
		
		comboboxType.setSelection(1);
		
		for (int i = 0 ; i < comboboxNPCAll.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxNPCAll.getItems().get(i))) {
				comboboxNPCAll.setSelection(i);
				break;
				
			}
			
		}
		
		refresh();
		
	}

	private void setListEntityAllRobot(String value) {
		value = value.trim().toLowerCase();
		
		for (int i = 1 ; i < comboVar2.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar2.getItems().get(i))) {
				comboVar2.setSelection(i);
				textLine2.setVisible(true);
				textLine2.setEnabled(false);
				textLine2.setText(value);
				
				refresh();
				return;
				
			}
			
		}
		
		comboboxNPCAllRobot.setVisible(true);
		comboboxNPCAllRobot.setSelection(0);
		
		for (int i = 0 ; i < comboboxNPCAllRobot.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxNPCAllRobot.getItems().get(i))) {
				comboboxNPCAllRobot.setSelection(i);
				break;
				
			}
			
		}
		
		refresh();
		
	}

	private void setListEntityRobot(String value) {
		value = value.trim().toLowerCase();
		
		for (int i = 1 ; i < comboVar2.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar2.getItems().get(i))) {
				comboVar2.setSelection(i);
				textLine2.setVisible(true);
				textLine2.setEnabled(false);
				textLine2.setText(value);
				
				refresh();
				return;
				
			}
			
		}
		
		comboboxNPCRobot.setVisible(true);
		comboboxNPCRobot.setSelection(0);
		
		for (int i = 0 ; i < comboboxNPCRobot.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxNPCRobot.getItems().get(i))) {
				comboboxNPCRobot.setSelection(i);
				break;
				
			}
			
		}
		
		refresh();
		
	}

	private void setBlockList(String value) {
		value = value.trim().toLowerCase();
		
		for (int i = 1 ; i < comboVar2.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar2.getItems().get(i))) {
				comboVar2.setSelection(i);
				textLine2.setVisible(true);
				textLine2.setEnabled(false);
				textLine2.setText(value);
				
				refresh();
				return;
				
			}
			
		}
		
		comboboxItems.setVisible(true);
		comboboxItems.setSelection(0);
		
		labelLine4.setVisible(true);
		textLine4.setVisible(true);
		
		labelLine2.setText("Block");
		comboboxType.setSelection(0);
		
		for (int i = 0 ; i < comboboxItems.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxItems.getItems().get(i).split(":")[0])) {
				comboboxItems.setSelection(i);

				break;
				
			}
			
		}
		
		refresh();
		
	}

	private void setSideListAll(String value) {
		value = value.trim().toLowerCase();
		
		for (int i = 1 ; i < comboVar1.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar1.getItems().get(i))) {
				comboVar1.setSelection(i);
				textLine1.setVisible(true);
				textLine1.setEnabled(false);
				textLine1.setText(value);
				
				refresh();
				return;
				
			}
			
		}
		
		comboboxSidesAll.setVisible(true);
		comboboxSidesAll.setSelection(0);
		
		for (int i = 0 ; i < comboboxSidesAll.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxSidesAll.getItems().get(i))) {
				comboboxSidesAll.setSelection(i);
				break;
				
			}
			
		}
		
		refresh();
		
	}
	
	private void setSideListNormal(String value) {
		value = value.trim().toLowerCase();
		
		for (int i = 1 ; i < comboVar1.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboVar1.getItems().get(i))) {
				comboVar1.setSelection(i);
				textLine1.setVisible(true);
				textLine1.setEnabled(false);
				textLine1.setText(value);
				
				refresh();
				return;
				
			}
			
		}
		
		comboboxSidesNormal.setVisible(true);
		comboboxSidesNormal.setSelection(0);
		
		for (int i = 0 ; i < comboboxSidesNormal.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(comboboxSidesNormal.getItems().get(i))) {
				comboboxSidesNormal.setSelection(i);
				break;
				
			}
			
		}
		
		refresh();
		
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
		//Cancel buttons
		if (button.equals(buttonCancel)) { closeWindow(); return; }
		
		//Selector change
		if (button.equals(comboboxType)) {
			switch (comboboxType.getSelectedRow()) {
			case 0:
				//Block
				textLine2.setVisible(false);
				comboboxItems.setVisible(false);
				comboboxNPCAll.setVisible(false);
				comboboxNPCAllRobot.setVisible(false);
				comboboxNPCRobot.setVisible(false);
				comboVar1.setVisible(true);
				comboVar2.setVisible(true);
				comboVar3.setVisible(true);
				comboVar4.setVisible(true);
				
				setBlockList("");
				
				break;
				
			case 1:
				//NPC
				textLine2.setVisible(false);
				comboboxItems.setVisible(false);
				comboboxNPCAll.setVisible(false);
				comboboxNPCAllRobot.setVisible(false);
				comboboxNPCRobot.setVisible(false);
				comboVar1.setVisible(true);
				comboVar2.setVisible(true);
				comboVar3.setVisible(true);
				comboVar4.setVisible(false);
				
				setListEntityAll("");
				
				break;
				
			case 2:
				//Robot
				textLine2.setVisible(false);
				comboboxItems.setVisible(false);
				comboboxNPCAll.setVisible(false);
				comboboxNPCAllRobot.setVisible(false);
				comboboxNPCRobot.setVisible(false);
				
				comboVar1.setVisible(true);
				comboVar2.setVisible(true);
				comboVar3.setVisible(true);
				comboVar4.setVisible(false);
				
				labelLine4.setVisible(false);
				textLine4.setVisible(false);
				
				textLine2.setVisible(true);
				textLine2.setText("robot");
				textLine2.setEnabled(false);
				
				labelLine2.setText("Robot");
				comboboxType.setSelection(2);
				
				break;
				
			}
			
			refresh();
			
			return;
			
		}
		
		if (button.equals(comboVar1)) {
			if (comboVar1.getSelectedRow() == 0) {
				textLine1.setVisible(false);
				
				if (comboboxType.isVisible()) comboboxSidesAll.setVisible(true);
				else comboboxSidesNormal.setVisible(true);
				
			} else {
				textLine1.setVisible(true);
				textLine1.setEnabled(false);
				textLine1.setText(comboVar1.getSelectedItem());
				
				comboboxSidesAll.setVisible(false);
				comboboxSidesNormal.setVisible(false);
				
			}
			
			refresh();
			
			return;
			
		}
		
		if (button.equals(comboVar2)) {
			if (comboVar2.getSelectedRow() == 0) {
				textLine2.setVisible(false);
				
				if (comboboxType.isVisible()) {
					switch (comboboxType.getSelectedRow()) {
					case 0:
						comboboxItems.setVisible(true);
						break;
					case 1:
						comboboxNPCAll.setVisible(true);
						break;
					case 2:
						textLine2.setVisible(true);
						textLine2.setText("robot");
						break;
						
					}
					
				} else {
					if (textType.isVisible()) {
						if (textType.getText().equalsIgnoreCase("attack far side")) {
							comboboxNPCRobot.setVisible(true);
							
						} else if (textType.getText().equalsIgnoreCase("attack near")) {
							comboboxNPCAllRobot.setVisible(true);
							
						} else {
							comboboxItems.setVisible(true);
							
						}
						
					}
					
				}
				
			} else {
				textLine2.setVisible(true);
				textLine2.setEnabled(false);
				textLine2.setText(comboVar2.getSelectedItem());
				
				comboboxItems.setVisible(false);
				comboboxNPCAll.setVisible(false);
				comboboxNPCAllRobot.setVisible(false);
				comboboxNPCRobot.setVisible(false);
				
			}
			
			refresh();
			
			return;
			
		}
		
		if (button.equals(comboVar3)) {
			if (comboVar3.getSelectedRow() == 0) {
				textLine3.setVisible(true);
				textLine3.setEnabled(true);
				textLine3.setText("");
				
			} else {
				textLine3.setVisible(true);
				textLine3.setEnabled(false);
				textLine3.setText(comboVar3.getSelectedItem());
				
			}
			
			refresh();
			
			return;
			
		}
		
		if (button.equals(comboVar4)) {
			if (comboVar4.getSelectedRow() == 0) {
				textLine4.setVisible(true);
				textLine4.setEnabled(true);
				textLine4.setText("");
				
			} else {
				textLine4.setVisible(true);
				textLine4.setEnabled(false);
				textLine4.setText(comboVar4.getSelectedItem());
				
			}
			
			refresh();
			
			return;
			
		}
		
		//Save button
		if (button.equals(buttonSave)) {
			if (signBlock == null) closeWindow();
			
			try { ((SpoutBlock)signBlock).setCustomBlock(null); } catch (Exception e) {}
			
			if (signBlock.getType() != Material.SIGN_POST) {
				Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(signBlock, player));
				
				signBlock.setType(Material.SIGN_POST);
				signBlock.setData((byte) 8);
				
			}
			
			
			Sign sign = (Sign) signBlock.getState();
			
			//Set text lines and update
			if (comboboxSidesAll.isVisible()) sign.setLine(0,comboboxSidesAll.getSelectedItem());
			else if (comboboxSidesNormal.isVisible()) sign.setLine(0,comboboxSidesNormal.getSelectedItem());
			else sign.setLine(0, textLine1.getText());
			
			if (comboboxItems.isVisible()) {
				if (comboboxIsNot.isVisible() && comboboxIsNot.getSelectedRow() == 1)
					sign.setLine(1,"!" + comboboxItems.getSelectedItem().split(":")[0]);
				else sign.setLine(1,comboboxItems.getSelectedItem().split(":")[0]);
				
			} else if (comboboxNPCAll.isVisible()) {
				if (comboboxIsNot.isVisible() && comboboxIsNot.getSelectedRow() == 1)
					sign.setLine(1,"!" + comboboxNPCAll.getSelectedItem());
				else sign.setLine(1,comboboxNPCAll.getSelectedItem());
				
			}
			else if (comboboxNPCAllRobot.isVisible()) sign.setLine(1,comboboxNPCAllRobot.getSelectedItem());  
			else if (comboboxNPCRobot.isVisible()) sign.setLine(1,comboboxNPCRobot.getSelectedItem());  
			else if (comboboxIsNot.isVisible() && comboboxIsNot.getSelectedRow() == 1 && textLine2.getText().equalsIgnoreCase("robot"))
				sign.setLine(1, "!" + textLine2.getText());
			else sign.setLine(1, textLine2.getText());
			
			if (textLine3.isVisible()) sign.setLine(2, textLine3.getText()); else sign.setLine(2, "");
			if (textLine4.isVisible()) sign.setLine(3, textLine4.getText()); else sign.setLine(3, "");
			
			sign.update();
			
			closeWindow();
			
		}
		
	}
	
}
