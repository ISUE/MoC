package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import java.util.Arrays;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.math.SetBlock;
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

public class SignEditMathAssign extends GenericPopup {
	//----------------------------------------------------------------
	private CodeBlocks plugin; public CodeBlocks getPlugin() { return plugin; }
	private SpoutPlayer player; public SpoutPlayer getPlayer() { return player; }
	private int screenBufferX = 15, screenBufferY = 20;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	
	//Background
	private Gradient gradientBackground;
	
	//Type
	private GenericLabel labelTitle;
	private GenericComboBox cbVariable;
	private GenericTextField textEquation;
	
	//Window buttons
	private GenericButton buttonCancel, buttonSet;
	
	//Insert combo and buttons
	private GenericButton buttonInsertOperation, buttonInsertVariable, buttonInsertBoolean, buttonInsertType, buttonInsertSide, buttonInsertBlock;
	private GenericComboBox cbInsertOperation, cbInsertVariable, cbInsertBoolean, cbInsertType, cbInsertSide, cbInsertBlock;
	
	//Blocks
	private Block signBlock;
	private SpoutBlock baseBlock;
	//----------------------------------------------------------------
	
	public SignEditMathAssign(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		//Type
		labelTitle = new GenericLabel("Assign");

		ArrayList<String> fvars = new ArrayList<String>(); for (FunctionVariables fv : FunctionVariables.values()) fvars.add(fv.toString().toLowerCase());
		cbVariable = new GenericComboBox();
		cbVariable.setText("");
		cbVariable.setHoverColor(hoverColor);
		cbVariable.setItems(fvars);
		
		textEquation = new GenericTextField();
		textEquation.setFieldColor(textFieldColor);
		textEquation.setMaximumLines(0);
		textEquation.setMaximumCharacters(1024);
		
		ArrayList<String> sides = new ArrayList<String>(); for ( RobotSide rs : RobotSide.values()) sides.add(rs.toString().toLowerCase());
		ArrayList<String> materials = new ArrayList<String>(); materials.addAll(Arrays.asList("Monster", "Animal", "Creature", "Vehicle", "Player", "Entity", "Robot", "All"));
		for (Material m : Material.values()) if (m.isBlock()) materials.add(m.getId() + ": " + m.toString().toLowerCase());
		
		//Window buttons
		buttonCancel = new GenericButton("Cancel");
		buttonCancel.setTooltip("Cancel");
		buttonCancel.setHoverColor(hoverColor);

		buttonSet = new GenericButton("Set");
		buttonSet.setTooltip("Set");
		buttonSet.setHoverColor(hoverColor);
		
		//Inserts
		cbInsertOperation = new GenericComboBox();
		cbInsertOperation.setText("");
		cbInsertOperation.setHoverColor(hoverColor);
		cbInsertOperation.setItems(Arrays.asList("+", "-", "*", "/", "%", "^", "sqrt", "(", ")", "not", "and", "or", "<", "<=", ">", ">=", "==", "<>"));
		buttonInsertOperation = new GenericButton("+");
		buttonInsertOperation.setTooltip("Add selected value to the text");
		buttonInsertOperation.setHoverColor(hoverColor);

		cbInsertVariable = new GenericComboBox();
		cbInsertVariable.setText("");
		cbInsertVariable.setHoverColor(hoverColor);
		cbInsertVariable.setItems(fvars);
		buttonInsertVariable = new GenericButton("+");
		buttonInsertVariable.setTooltip("Add selected value to the text");
		buttonInsertVariable.setHoverColor(hoverColor);
		
		cbInsertBoolean = new GenericComboBox();
		cbInsertBoolean.setText("");
		cbInsertBoolean.setHoverColor(hoverColor);
		cbInsertBoolean.setItems(Arrays.asList("true", "false"));
		buttonInsertBoolean = new GenericButton("+");
		buttonInsertBoolean.setTooltip("Add selected value to the text");
		buttonInsertBoolean.setHoverColor(hoverColor);
		
		cbInsertSide = new GenericComboBox();
		cbInsertSide.setText("");
		cbInsertSide.setHoverColor(hoverColor);
		cbInsertSide.setItems(sides);
		buttonInsertSide = new GenericButton("+");
		buttonInsertSide.setTooltip("Add selected value to the text");
		buttonInsertSide.setHoverColor(hoverColor);
		
		cbInsertType = new GenericComboBox();
		cbInsertType.setText("");
		cbInsertType.setHoverColor(hoverColor);
		cbInsertType.setItems(materials);
		buttonInsertType = new GenericButton("+");
		buttonInsertType.setTooltip("Add selected value to the text");
		buttonInsertType.setHoverColor(hoverColor);
		
		cbInsertBlock = new GenericComboBox();
		cbInsertBlock.setText("");
		cbInsertBlock.setHoverColor(hoverColor);
		cbInsertBlock.setItems(Arrays.asList("#b1#", "#b2#", "#b3#", "#b4#", "#b5#", "#b6#", "#b7#", "#b8#", "#b9#"));
		buttonInsertBlock = new GenericButton("+");
		buttonInsertBlock.setTooltip("Add selected value to the text");
		buttonInsertBlock.setHoverColor(hoverColor);
		
		//Attach
		attachWidgets(plugin, gradientBackground, labelTitle, cbVariable, textEquation, buttonCancel, buttonSet);
		attachWidgets(plugin, cbInsertBoolean, cbInsertOperation, cbInsertSide, cbInsertType, cbInsertVariable, cbInsertBlock);
		attachWidgets(plugin, buttonInsertBoolean, buttonInsertOperation, buttonInsertSide, buttonInsertType, buttonInsertVariable, buttonInsertBlock);
		
		//Initialize
		initialize();

	}
	
	private void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - screenBufferY * 2;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        int upRightX = screenBufferX + windowWidth;
        
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Type
		labelTitle.setX(upLeftX+10).setY(upLeftY+9);
		labelTitle.setHeight(15).setWidth(windowWidth);
		
		cbVariable.setX(upLeftX+50).setY(upLeftY+5);
		cbVariable.setHeight(15).setWidth(70);
		cbVariable.closeList();
		cbVariable.setSelection(0);
		
		textEquation.setX(upLeftX+5).setY(upLeftY+25);
		textEquation.setHeight(windowHeight - 50).setWidth(windowWidth - 130);
		textEquation.setText("");
		textEquation.setFocus(true);
		
		//Window buttons
		buttonCancel.setX(upLeftX+5).setY(upLeftY + windowHeight - 20);
		buttonCancel.setWidth(windowWidth/2-5).setHeight(15);
		
		buttonSet.setX(upLeftX+windowWidth/2).setY(upLeftY + windowHeight - 20);
		buttonSet.setWidth(windowWidth/2-5).setHeight(15);
		
		//Inserts
		cbInsertType.setX(upRightX-120).setY(upLeftY+25);
		cbInsertType.setHeight(15).setWidth(100);
		cbInsertType.closeList();
		cbInsertType.setSelection(0);
		buttonInsertType.setX(upRightX-20).setY(upLeftY+25);
		buttonInsertType.setWidth(15).setHeight(15);
		
		cbInsertVariable.setX(upRightX-120).setY(upLeftY+45);
		cbInsertVariable.setHeight(15).setWidth(100);
		cbInsertVariable.closeList();
		cbInsertVariable.setSelection(0);
		buttonInsertVariable.setX(upRightX-20).setY(upLeftY+45);
		buttonInsertVariable.setWidth(15).setHeight(15);
		
		cbInsertOperation.setX(upRightX-120).setY(upLeftY+65);
		cbInsertOperation.setHeight(15).setWidth(100);
		cbInsertOperation.closeList();
		cbInsertOperation.setSelection(0);
		buttonInsertOperation.setX(upRightX-20).setY(upLeftY+65);
		buttonInsertOperation.setWidth(15).setHeight(15);
		
		cbInsertSide.setX(upRightX-120).setY(upLeftY+85);
		cbInsertSide.setHeight(15).setWidth(100);
		cbInsertSide.closeList();
		cbInsertSide.setSelection(0);
		buttonInsertSide.setX(upRightX-20).setY(upLeftY+85);
		buttonInsertSide.setWidth(15).setHeight(15);
		
		cbInsertBoolean.setX(upRightX-120).setY(upLeftY+105);
		cbInsertBoolean.setHeight(15).setWidth(100);
		cbInsertBoolean.closeList();
		cbInsertBoolean.setSelection(0);
		buttonInsertBoolean.setX(upRightX-20).setY(upLeftY+105);
		buttonInsertBoolean.setWidth(15).setHeight(15);
		
		cbInsertBlock.setX(upRightX-120).setY(upLeftY+125);
		cbInsertBlock.setHeight(15).setWidth(100);
		cbInsertBlock.closeList();
		cbInsertBlock.setSelection(0);
		buttonInsertBlock.setX(upRightX-20).setY(upLeftY+125);
		buttonInsertBlock.setWidth(15).setHeight(15);
		
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
		signBlock = baseBlock.getLocation().add(0, 1, 0).getBlock(); try { ((SpoutBlock)signBlock).setCustomBlock(null); } catch (Exception e) {}
		String[] values = {"","","",""};
		if (signBlock.getType() == Material.SIGN_POST) { Sign sign = (Sign) signBlock.getState(); values = sign.getLines(); sign.update(); }
		
		if (baseBlock.getCustomBlock() instanceof SetBlock) { loadData(values); }
		else return;
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}

	private void loadData(String[] values) {
		cbVariable.setSelection(0);
		cbVariable.setVisible(true);
		
		if (values[0].length() == 0) return;
		
		for (int i = 0 ; i < cbVariable.getItems().size() ; i++) if (values[0].equalsIgnoreCase(cbVariable.getItems().get(i))) { cbVariable.setSelection(i); break; }
		
		textEquation.setText(values[1]);
		
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
			sign.setLine(0, cbVariable.getSelectedItem());
			sign.setLine(1, textEquation.getText());
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
			
			closeWindow();
			
			return;

		}
		
		//Inserts
		if (button.equals(buttonInsertBoolean)) { insertText(cbInsertBoolean); return; } 
		if (button.equals(buttonInsertOperation)) { insertText(cbInsertOperation); return; } 
		if (button.equals(buttonInsertSide)) { insertText(cbInsertSide); return; } 
		if (button.equals(buttonInsertType)) { insertText(cbInsertType); return; } 
		if (button.equals(buttonInsertVariable)) { insertText(cbInsertVariable); return; } 
		if (button.equals(buttonInsertBlock)) { insertText(cbInsertBlock); return; } 
		
	}

	private void insertText(GenericComboBox cb) {
		if (cb.getSelectedItem() != null) {
			//Get what we are inserting
			String in = cb.getSelectedItem(); if (cb.equals(cbInsertType)) in = "type:" + in.split(":")[0];
			
			//Get cursor position and current text
			int cp = textEquation.getCursorPosition();
			String text = textEquation.getText();
			
			//Modify text by adding insert
			if (text.length() > 0) text = text.substring(0, cp) + in + text.substring(cp);
			else text = in;
			
			//Set new text and update current cursor position
			textEquation.setText(text);
			textEquation.setCursorPosition(cp+in.length());
			textEquation.setFocus(true);
			
		}
		
	}

}
