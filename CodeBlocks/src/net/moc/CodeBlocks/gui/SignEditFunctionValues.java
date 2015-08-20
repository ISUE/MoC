package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import java.util.Arrays;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
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

public class SignEditFunctionValues extends GenericPopup {
	//----------------------------------------------------------------
	private CodeBlocks plugin; public CodeBlocks getPlugin() { return plugin; }
	private SpoutPlayer player; public SpoutPlayer getPlayer() { return player; }
	private int screenBufferX = 15, screenBufferY = 40;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	
	//Background
	private Gradient gradientBackground;
	
	//Type
	private GenericLabel labelTitle, labelA1, labelA2, labelA3;
	private MOCComboBox cbA1, cbA2, cbA3;
	private GenericComboBox[] cbVariable, cbBoolean, cbSide, cbType;
	private GenericTextField[] textInteger;
	
	//Window buttons
	private GenericButton buttonCancel, buttonSet;
	
	//Blocks
	private Block signBlock;
	private SpoutBlock baseBlock;
	//----------------------------------------------------------------
	
	public SignEditFunctionValues(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		//Function fields
		labelTitle = new GenericLabel("Function Values");
		labelA1 = new GenericLabel("a1");
		labelA2 = new GenericLabel("a2");
		labelA3 = new GenericLabel("a3");
		
		cbA1 = new MOCComboBox();
		cbA1.setText("");
		cbA1.setHoverColor(hoverColor);
		cbA1.setItems(Arrays.asList("None", "Variable", "Integer", "Boolean", "Type", "Side"));
		
		cbA2 = new MOCComboBox();
		cbA2.setText("");
		cbA2.setHoverColor(hoverColor);
		cbA2.setItems(Arrays.asList("None", "Variable", "Integer", "Boolean", "Type", "Side"));
		
		cbA3 = new MOCComboBox();
		cbA3.setText("");
		cbA3.setHoverColor(hoverColor);
		cbA3.setItems(Arrays.asList("None", "Variable", "Integer", "Boolean", "Type", "Side"));
		
		//Values
		ArrayList<String> fvars = new ArrayList<String>(); for (FunctionVariables fv : FunctionVariables.values()) fvars.add(fv.toString().toLowerCase());
		cbVariable = new GenericComboBox[3];
		for (int i = 0 ; i < cbVariable.length ; i++) {
			cbVariable[i] = new GenericComboBox();
			cbVariable[i].setText("");
			cbVariable[i].setHoverColor(hoverColor);
			cbVariable[i].setItems(fvars);
			
		}
		
		cbBoolean = new GenericComboBox[3];
		for (int i = 0 ; i < cbBoolean.length ; i++) {
			cbBoolean[i] = new GenericComboBox();
			cbBoolean[i].setText("");
			cbBoolean[i].setHoverColor(hoverColor);
			cbBoolean[i].setItems(Arrays.asList("true", "false"));
			
		}
		
		ArrayList<String> sides = new ArrayList<String>(); for ( RobotSide rs : RobotSide.values()) sides.add(rs.toString().toLowerCase());
		cbSide = new GenericComboBox[3];
		for (int i = 0 ; i < cbSide.length ; i++) {
			cbSide[i] = new GenericComboBox();
			cbSide[i].setText("");
			cbSide[i].setHoverColor(hoverColor);
			cbSide[i].setItems(sides);
			
		}
		
		ArrayList<String> materials = new ArrayList<String>();
		materials.addAll(Arrays.asList("Monster", "Animal", "Creature", "Vehicle", "Player", "Entity", "Robot", "All"));
		for (Material m : Material.values()) if (m.isBlock()) materials.add(m.getId() + ": " + m.toString().toLowerCase());
		cbType = new GenericComboBox[3];
		for (int i = 0 ; i < cbType.length ; i++) {
			cbType[i] = new GenericComboBox();
			cbType[i].setText("");
			cbType[i].setHoverColor(hoverColor);
			cbType[i].setItems(materials);
			
		}
		
		textInteger = new GenericTextField[3];
		for (int i = 0 ; i < textInteger.length ; i++) {
			textInteger[i] = new GenericTextField();
			textInteger[i].setFieldColor(textFieldColor);
			
		}
		
		//Window buttons
		buttonCancel = new GenericButton("Cancel");
		buttonCancel.setTooltip("Cancel");
		buttonCancel.setHoverColor(hoverColor);

		buttonSet = new GenericButton("Set");
		buttonSet.setTooltip("Set");
		buttonSet.setHoverColor(hoverColor);
		
		//Attach
		attachWidgets(plugin, gradientBackground, labelTitle, labelA1, labelA2, labelA3);
		attachWidgets(plugin, cbA1, cbA2, cbA3);
		attachWidgets(plugin, cbSide[0], cbSide[1], cbSide[2]);
		attachWidgets(plugin, cbType[0], cbType[1], cbType[2], cbBoolean[0], cbBoolean[1], cbBoolean[2]);
		attachWidgets(plugin, cbVariable[0], cbVariable[1], cbVariable[2], textInteger[0], textInteger[1], textInteger[2]);
		attachWidgets(plugin, buttonCancel, buttonSet);
		
		//Initialize
		initialize();

	}
	
	private void initialize() {
		//Corners and size of the window
		int windowWidth = 220;
		int windowHeight = 100;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Type
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		labelTitle.setHeight(15).setWidth(windowWidth);
		labelTitle.setText("Function Values");
		
		labelA1.setX(upLeftX+5).setY(upLeftY+24);
		labelA1.setHeight(15).setWidth(windowWidth);
		
		labelA2.setX(upLeftX+5).setY(upLeftY+44);
		labelA2.setHeight(15).setWidth(windowWidth);
		
		labelA3.setX(upLeftX+5).setY(upLeftY+64);
		labelA3.setHeight(15).setWidth(windowWidth);
		
		cbA1.setX(upLeftX+25).setY(upLeftY+20);
		cbA1.setHeight(15).setWidth(60);
		cbA1.closeList();
		cbA1.setSelection(0);
		
		cbA2.setX(upLeftX+25).setY(upLeftY+40);
		cbA2.setHeight(15).setWidth(60);
		cbA2.closeList();
		cbA2.setSelection(0);
		
		cbA3.setX(upLeftX+25).setY(upLeftY+60);
		cbA3.setHeight(15).setWidth(60);
		cbA3.closeList();
		cbA3.setSelection(0);
		
		for (int i = 0 ; i < cbVariable.length ; i++) {
			cbSide[i].setX(upLeftX+90).setY(upLeftY+20*(i+1));
			cbSide[i].setHeight(15).setWidth(120);
			cbSide[i].closeList();
			cbSide[i].setSelection(0);
			cbSide[i].setVisible(false);
			
			cbType[i].setX(upLeftX+90).setY(upLeftY+20*(i+1));
			cbType[i].setHeight(15).setWidth(120);
			cbType[i].closeList();
			cbType[i].setSelection(0);
			cbType[i].setVisible(false);
			
			cbVariable[i].setX(upLeftX+90).setY(upLeftY+20*(i+1));
			cbVariable[i].setHeight(15).setWidth(120);
			cbVariable[i].closeList();
			cbVariable[i].setSelection(0);
			cbVariable[i].setVisible(false);
			
			cbBoolean[i].setX(upLeftX+90).setY(upLeftY+20*(i+1));
			cbBoolean[i].setHeight(15).setWidth(120);
			cbBoolean[i].closeList();
			cbBoolean[i].setSelection(0);
			cbBoolean[i].setVisible(false);
			
			textInteger[i].setX(upLeftX+90).setY(upLeftY+20*(i+1));
			textInteger[i].setHeight(15).setWidth(120);
			textInteger[i].setText("");
			textInteger[i].setVisible(false);
			
		}
		
		//Window buttons
		buttonCancel.setX(upLeftX+5).setY(upLeftY+80);
		buttonCancel.setWidth(windowWidth/2-5).setHeight(15);
		
		buttonSet.setX(upLeftX+windowWidth/2).setY(upLeftY+80);
		buttonSet.setWidth(windowWidth/2-5).setHeight(15);
		
		refresh();
		
	}
	
	public void refresh() { setDirty(true); for(Widget widget : getAttachedWidgets()) widget.setDirty(true); }
	
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	public void open(SpoutBlock block) {
		initialize();
		
		baseBlock = block;
		
		//Get sign block
		signBlock = baseBlock.getLocation().add(0, 1, 0).getBlock(); try { ((SpoutBlock)signBlock).setCustomBlock(null); } catch (Exception e) {}
		
		String value[] = {"","","",""};
		
		if (signBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) signBlock.getState();
			value[0] = sign.getLine(0);//function name
			value[1] = sign.getLine(1);//a1
			value[2] = sign.getLine(2);//a2
			value[3] = sign.getLine(3);//a3
			sign.update();
			
		}
		
		labelTitle.setText(value[0] + " Values");
		
		if (baseBlock.getCustomBlock() instanceof CallFunctionBlock) {
			matchValue(value[1], 0);
			matchValue(value[2], 1);
			matchValue(value[3], 2);
			
		} else return;
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}

	private void matchValue(String value, int line) {
		value = value.toLowerCase().trim();
		if (value.length() == 0) return;
		//"None", "Variable", "Integer", "Boolean", "Type", "Side"
		if (value.startsWith("var:")) {
			if (line == 0) cbA1.setSelection(1);
			else if (line == 1) cbA2.setSelection(1);
			else if (line == 2) cbA3.setSelection(1);
			value = value.replace("var:", "");
			cbVariable[line].setVisible(true);
			for (int i = 0 ; i < cbVariable[line].getItems().size() ; i++)
				if (value.equalsIgnoreCase(cbVariable[line].getItems().get(i))) { cbVariable[line].setSelection(i); break; }
			
		} else if (value.startsWith("bool:")) {
			if (line == 0) cbA1.setSelection(3);
			else if (line == 1) cbA2.setSelection(3);
			else if (line == 2) cbA3.setSelection(3);
			value = value.replace("bool:", "");
			cbBoolean[line].setVisible(true);
			for (int i = 0 ; i < cbBoolean[line].getItems().size() ; i++)
				if (value.equalsIgnoreCase(cbBoolean[line].getItems().get(i))) { cbBoolean[line].setSelection(i); break; }
			
		} else if (value.startsWith("side:")) {
			if (line == 0) cbA1.setSelection(5);
			else if (line == 1) cbA2.setSelection(5);
			else if (line == 2) cbA3.setSelection(5);
			value = value.replace("side:", "");
			cbSide[line].setVisible(true);
			for (int i = 0 ; i < cbSide[line].getItems().size() ; i++)
				if (value.equalsIgnoreCase(cbSide[line].getItems().get(i))) { cbSide[line].setSelection(i); break; }
			
		} else if (value.startsWith("type:")) {
			if (line == 0) cbA1.setSelection(4);
			else if (line == 1) cbA2.setSelection(4);
			else if (line == 2) cbA3.setSelection(4);
			value = value.replace("type:", "");
			cbType[line].setVisible(true);
			for (int i = 0 ; i < cbType[line].getItems().size() ; i++)
				if (value.equalsIgnoreCase(cbType[line].getItems().get(i))) { cbType[line].setSelection(i); break; }
				else if (value.equalsIgnoreCase(cbType[line].getItems().get(i).split(":")[0])) { cbType[line].setSelection(i); break; }
			
		} else if (value.startsWith("int:")) {
			if (line == 0) cbA1.setSelection(2);
			else if (line == 1) cbA2.setSelection(2);
			else if (line == 2) cbA3.setSelection(2);
			value = value.replace("int:", "");
			textInteger[line].setVisible(true);
			int v = 0; try {v = Integer.parseInt(value); } catch (NumberFormatException e) {}
			textInteger[line].setText(v+"");

		}
		
	}
	
	public void onClick(Button button) {
		//Cancel buttons
		if (button.equals(buttonCancel)) { closeWindow(); return; }
		
		//Set button
		if (button.equals(buttonSet)) {
			if (signBlock == null) closeWindow();
			if (signBlock.getType() != Material.SIGN_POST) {
				Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(signBlock, player));
				signBlock.setTypeIdAndData(Material.SIGN_POST.getId(), (byte) 8, false);
				
			}
			
			Sign sign = (Sign) signBlock.getState();
			
			for (int i = 0 ; i < cbVariable.length ; i++) {
				if (cbBoolean[i].isVisible()) sign.setLine(i+1, "bool:" + cbBoolean[i].getSelectedItem());
				else if (cbSide[i].isVisible()) sign.setLine(i+1, "side:" + cbSide[i].getSelectedItem());
				else if (cbType[i].isVisible()) sign.setLine(i+1, "type:" + cbType[i].getSelectedItem().split(":")[0]);
				else if (cbVariable[i].isVisible()) sign.setLine(i+1, "var:" + cbVariable[i].getSelectedItem());
				else if (textInteger[i].isVisible()) sign.setLine(i+1, "int:" + textInteger[i].getText());
				else sign.setLine(i+1, "");
				
			}
			
			sign.update();
			
			closeWindow();
			return;

		}
		
		if (button.equals(cbA1) && cbA1.getSelectedItem() != null) {
			switch (cbA1.getSelectedItem()) {
			case "None":
				cbBoolean[0].setVisible(false); cbSide[0].setVisible(false); cbType[0].setVisible(false); cbVariable[0].setVisible(false); textInteger[0].setVisible(false);
				break;
			case "Variable":
				cbBoolean[0].setVisible(false); cbSide[0].setVisible(false); cbType[0].setVisible(false); cbVariable[0].setVisible(true); textInteger[0].setVisible(false);
				break;
			case "Integer":
				cbBoolean[0].setVisible(false); cbSide[0].setVisible(false); cbType[0].setVisible(false); cbVariable[0].setVisible(false); textInteger[0].setVisible(true);
				break;
			case "Boolean":
				cbBoolean[0].setVisible(true); cbSide[0].setVisible(false); cbType[0].setVisible(false); cbVariable[0].setVisible(false); textInteger[0].setVisible(false);
				break;
			case "Type":
				cbBoolean[0].setVisible(false); cbSide[0].setVisible(false); cbType[0].setVisible(true); cbVariable[0].setVisible(false); textInteger[0].setVisible(false);
				break;
			case "Side":
				cbBoolean[0].setVisible(false); cbSide[0].setVisible(true); cbType[0].setVisible(false); cbVariable[0].setVisible(false); textInteger[0].setVisible(false);
				break;
			}
			
			return;
			
		}
		
		if (button.equals(cbA2) && cbA2.getSelectedItem() != null) {
			switch (cbA2.getSelectedItem()) {
			case "None":
				cbBoolean[1].setVisible(false); cbSide[1].setVisible(false); cbType[1].setVisible(false); cbVariable[1].setVisible(false); textInteger[1].setVisible(false);
				break;
			case "Variable":
				cbBoolean[1].setVisible(false); cbSide[1].setVisible(false); cbType[1].setVisible(false); cbVariable[1].setVisible(true); textInteger[1].setVisible(false);
				break;
			case "Integer":
				cbBoolean[1].setVisible(false); cbSide[1].setVisible(false); cbType[1].setVisible(false); cbVariable[1].setVisible(false); textInteger[1].setVisible(true);
				break;
			case "Boolean":
				cbBoolean[1].setVisible(true); cbSide[1].setVisible(false); cbType[1].setVisible(false); cbVariable[1].setVisible(false); textInteger[1].setVisible(false);
				break;
			case "Type":
				cbBoolean[1].setVisible(false); cbSide[1].setVisible(false); cbType[1].setVisible(true); cbVariable[1].setVisible(false); textInteger[1].setVisible(false);
				break;
			case "Side":
				cbBoolean[1].setVisible(false); cbSide[1].setVisible(true); cbType[1].setVisible(false); cbVariable[1].setVisible(false); textInteger[1].setVisible(false);
				break;
			}
			
			return;
			
		}
		
		if (button.equals(cbA3) && cbA3.getSelectedItem() != null) {
			switch (cbA3.getSelectedItem()) {
			case "None":
				cbBoolean[2].setVisible(false); cbSide[2].setVisible(false); cbType[2].setVisible(false); cbVariable[2].setVisible(false); textInteger[2].setVisible(false);
				break;
			case "Variable":
				cbBoolean[2].setVisible(false); cbSide[2].setVisible(false); cbType[2].setVisible(false); cbVariable[2].setVisible(true); textInteger[2].setVisible(false);
				break;
			case "Integer":
				cbBoolean[2].setVisible(false); cbSide[2].setVisible(false); cbType[2].setVisible(false); cbVariable[2].setVisible(false); textInteger[2].setVisible(true);
				break;
			case "Boolean":
				cbBoolean[2].setVisible(true); cbSide[2].setVisible(false); cbType[2].setVisible(false); cbVariable[2].setVisible(false); textInteger[2].setVisible(false);
				break;
			case "Type":
				cbBoolean[2].setVisible(false); cbSide[2].setVisible(false); cbType[2].setVisible(true); cbVariable[2].setVisible(false); textInteger[2].setVisible(false);
				break;
			case "Side":
				cbBoolean[2].setVisible(false); cbSide[2].setVisible(true); cbType[2].setVisible(false); cbVariable[2].setVisible(false); textInteger[2].setVisible(false);
				break;
			}
			
			return;
			
		}
		
	}

}
