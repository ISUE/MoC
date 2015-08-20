package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import java.util.Arrays;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.gui.widgets.MOCComboBox;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
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

public class SignEditMathEvaluate extends GenericPopup {
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
	private GenericLabel labelTitle;
	private MOCComboBox cbType;
	private GenericComboBox cbIsNot;
	
	//Values
	private GenericLabel labelLine1, labelLine2, labelLine3, labelLine4;
	private GenericTextField textLine3, textLine4;
	private GenericComboBox cbSides, cbBlocks, cbTargets;
	
	//Window buttons
	private GenericButton buttonCancel, buttonSet;
	
	//Blocks
	private Block signBlock;
	private SpoutBlock baseBlock;
	//----------------------------------------------------------------
	
	public SignEditMathEvaluate(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		//Type
		labelTitle = new GenericLabel("Evaluate");
		
		cbType = new MOCComboBox();
		cbType.setText("");
		cbType.setHoverColor(hoverColor);
		cbType.setItems(Arrays.asList("Sense Block", "Sense Entity", "Count Block"));
		
		cbIsNot = new GenericComboBox();
		cbIsNot.setText("");
		cbIsNot.setHoverColor(hoverColor);
		cbIsNot.setItems(Arrays.asList("is", "is not"));
		
		//Sign lines
		labelLine1 = new GenericLabel("Side");
		labelLine2 = new GenericLabel("Target");
		labelLine3 = new GenericLabel("Dist");
		labelLine4 = new GenericLabel("Data");
		
		textLine3 = new GenericTextField();
		textLine3.setMaximumLines(1);
		textLine3.setMaximumCharacters(128);
		textLine3.setFieldColor(textFieldColor);
		
		textLine4 = new GenericTextField();
		textLine4.setMaximumLines(1);
		textLine4.setMaximumCharacters(128);
		textLine4.setFieldColor(textFieldColor);
		
		//Drop downs for different types
		ArrayList<String> sides = new ArrayList<String>(); for ( RobotSide rs : RobotSide.values()) sides.add(rs.toString().toLowerCase());
		cbSides = new GenericComboBox();
		cbSides.setText("");
		cbSides.setHoverColor(hoverColor);
		cbSides.setItems(sides);
		
		ArrayList<String> materials = new ArrayList<String>(); for (Material m : Material.values()) if (m.isBlock()) materials.add(m.getId() + ": " + m.toString().toLowerCase());
		cbBlocks = new GenericComboBox();
		cbBlocks.setText("");
		cbBlocks.setHoverColor(hoverColor);
		cbBlocks.setItems(materials);
		
		cbTargets = new GenericComboBox();
		cbTargets.setText("");
		cbTargets.setHoverColor(hoverColor);
		cbTargets.setItems(Arrays.asList("Monster", "Animal", "Creature", "Vehicle", "Player", "Robot", "All"));
		
		//Window buttons
		buttonCancel = new GenericButton("Cancel");
		buttonCancel.setTooltip("Cancel");
		buttonCancel.setHoverColor(hoverColor);

		buttonSet = new GenericButton("Set");
		buttonSet.setTooltip("Set");
		buttonSet.setHoverColor(hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, gradientBackground, labelTitle, cbType, cbIsNot);
		attachWidgets(plugin, labelLine1, labelLine2, labelLine3, labelLine4, textLine3, textLine4);
		attachWidgets(plugin, cbSides, cbBlocks, cbTargets);
		attachWidgets(plugin, buttonCancel, buttonSet);
		
		//Initialize
		initialize();

	}
	
	private void initialize() {
		//Corners and size of the window
		int windowWidth = 225;
		int windowHeight = 125;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Type
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		labelTitle.setHeight(15).setWidth(40);
		
		cbType.setX(upLeftX+70).setY(upLeftY+5);
		cbType.setHeight(15).setWidth(100);
		cbType.closeList();
		cbType.setSelection(0);
		
		cbIsNot.setX(upLeftX+170).setY(upLeftY+5);
		cbIsNot.setHeight(15).setWidth(50);
		cbIsNot.closeList();
		cbIsNot.setSelection(0);
		cbIsNot.setVisible(false);
		
		//Values
		labelLine1.setX(upLeftX+5).setY(upLeftY+25);
		labelLine1.setHeight(15).setWidth(40);
		
		cbSides.setX(upLeftX+70).setY(upLeftY+25);
		cbSides.setHeight(15).setWidth(150);
		cbSides.closeList();
		cbSides.setSelection(0);
		cbSides.setVisible(false);
		
		labelLine2.setX(upLeftX+5).setY(upLeftY+45);
		labelLine2.setHeight(15).setWidth(40);
		
		cbBlocks.setX(upLeftX+70).setY(upLeftY+45);
		cbBlocks.setHeight(15).setWidth(150);
		cbBlocks.closeList();
		cbBlocks.setSelection(0);
		cbBlocks.setVisible(false);
		
		cbTargets.setX(upLeftX+70).setY(upLeftY+45);
		cbTargets.setHeight(15).setWidth(150);
		cbTargets.closeList();
		cbTargets.setSelection(0);
		cbTargets.setVisible(false);
		
		labelLine3.setX(upLeftX+5).setY(upLeftY+65);
		labelLine3.setHeight(15).setWidth(40);
		labelLine3.setVisible(false);
		
		textLine3.setX(upLeftX+70).setY(upLeftY+65);
		textLine3.setHeight(15).setWidth(150);
		textLine3.setText("");
		textLine3.setVisible(false);
		
		labelLine4.setX(upLeftX+5).setY(upLeftY+85);
		labelLine4.setHeight(15).setWidth(40);
		labelLine4.setVisible(false);
		
		textLine4.setX(upLeftX+70).setY(upLeftY+85);
		textLine4.setHeight(15).setWidth(150);
		textLine4.setText("");
		textLine4.setVisible(false);
		
		//Window buttons
		buttonCancel.setX(upLeftX+5).setY(upLeftY+105);
		buttonCancel.setWidth(105).setHeight(15);
		
		buttonSet.setX(upLeftX+115).setY(upLeftY+105);
		buttonSet.setWidth(105).setHeight(15);
		
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
		
		String[] values = {"","","",""};
		
		if (signBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) signBlock.getState();
			values[0] = sign.getLine(0).trim().toLowerCase();
			values[1] = sign.getLine(1).trim().toLowerCase();
			values[2] = sign.getLine(2).trim().toLowerCase();
			values[3] = sign.getLine(3).trim().toLowerCase();
			sign.update();
			
		}
		
		if (baseBlock.getCustomBlock() instanceof EvaluateBlock) matchValues(values);
		else return;
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}

	private void matchValues(String[] values) {
		//Type
		//"Sense Block", "Sense Entity", "Count Block"
		
		if (values[0].length() > 0) {
			//Side
			for (int i = 0 ; i < cbSides.getItems().size() ; i++) if (values[0].equalsIgnoreCase(cbSides.getItems().get(i))) { cbSides.setSelection(i); break; }
			cbSides.setVisible(true);
			
			if (values[1].startsWith("!")) { values[1] = values[1].substring(1); cbIsNot.setSelection(1); }
			cbIsNot.setVisible(true);
			
			try {
				//Target
				Integer.parseInt(values[1]);
				for (int i = 0 ; i < cbBlocks.getItems().size() ; i++) if (values[1].equalsIgnoreCase(cbBlocks.getItems().get(i).split(":")[0])) { cbBlocks.setSelection(i); break; }
				cbBlocks.setVisible(true);
				
				//Sense Block
				cbType.setSelection(0);
				
				//Distance
				textLine3.setText(values[2]);
				textLine3.setVisible(true);
				labelLine3.setVisible(true);
				
				//Data
				textLine4.setText(values[3]);
				textLine4.setVisible(true);
				labelLine4.setVisible(true);
				
			} catch (NumberFormatException e) {
				//Sense NPC
				cbType.setSelection(1);
				
				//Target
				for (int i = 0 ; i < cbTargets.getItems().size() ; i++) if (values[1].equalsIgnoreCase(cbTargets.getItems().get(i))) { cbTargets.setSelection(i); break; }
				cbTargets.setVisible(true);
				
				//Distance
				textLine3.setText(values[2]);
				textLine3.setVisible(true);
				labelLine3.setVisible(true);
				
			}
			
		} else if (values[1].length() > 0) {
			//Count block type
			cbType.setSelection(2);
			
			//Target
			for (int i = 0 ; i < cbBlocks.getItems().size() ; i++) if (values[1].equalsIgnoreCase(cbBlocks.getItems().get(i).split(":")[0])) { cbBlocks.setSelection(i); break; }
			
			cbBlocks.setVisible(true);
			
			//Data
			byte data = -1; try { data = Byte.parseByte(values[3]); } catch (NumberFormatException e) {} if (data > -1) textLine4.setText(data+"");
			textLine4.setVisible(true);
			labelLine4.setVisible(true);
			
		} else {
			//Default
			cbIsNot.setVisible(true);
			cbSides.setVisible(true);
			cbBlocks.setVisible(true);
			labelLine3.setVisible(true);
			textLine3.setVisible(true);
			labelLine4.setVisible(true);
			textLine4.setVisible(true);
			
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
			
			//Side
			if (cbSides.isVisible()) sign.setLine(0, cbSides.getSelectedItem().toLowerCase());
			else sign.setLine(0, "");
			
			//Target
			if (cbBlocks.isVisible())
				if (cbIsNot.getSelectedRow() == 1) sign.setLine(1, "!"+cbBlocks.getSelectedItem().split(":")[0].toLowerCase());
				else sign.setLine(1, cbBlocks.getSelectedItem().split(":")[0].toLowerCase());
			
			else if (cbTargets.isVisible())
				if (cbIsNot.getSelectedRow() == 1) sign.setLine(1, "!"+cbTargets.getSelectedItem().toLowerCase());
				else sign.setLine(1, cbTargets.getSelectedItem().split(":")[0].toLowerCase());
			
			else if (cbIsNot.getSelectedRow() == 1) sign.setLine(1, "!"+Material.AIR.toString()); else sign.setLine(1, Material.AIR.toString());
			
			//Distance
			if (textLine3.isVisible()) {
				int distance = 1;
				try { distance = Math.abs(Integer.parseInt(textLine3.getText().trim())); } catch (NumberFormatException e) {}
				sign.setLine(2, distance+"");
				
			} else sign.setLine(2, "");
			
			//Data
			if (textLine4.isVisible() && textLine4.getText().length() > 0) {
				byte data = -1;
				try { data = Byte.parseByte(textLine3.getText().trim()); } catch (NumberFormatException e) {}
				if (data > -1) sign.setLine(3, data+"");
				
			} else sign.setLine(3, "");
			
			sign.update();
			
			closeWindow();
			
			return;

		}
		
		//"Sense Block", "Sense Entity", "Count Block"
		if (button.equals(cbType)) {
			switch (cbType.getSelectedItem()) {
			case "Sense Block":
				cbIsNot.setVisible(true);
				cbSides.setVisible(true);
				
				cbBlocks.setVisible(true);
				cbTargets.setVisible(false);
				
				labelLine3.setVisible(true);
				textLine3.setVisible(true);
				
				labelLine4.setVisible(true);
				textLine4.setVisible(true);
				break;
			case "Sense Entity":
				cbIsNot.setVisible(true);
				cbSides.setVisible(true);
				
				cbBlocks.setVisible(false);
				cbTargets.setVisible(true);
				
				labelLine3.setVisible(true);
				textLine3.setVisible(true);
				
				labelLine4.setVisible(false);
				textLine4.setVisible(false);
				break;
			case "Count Block":
				cbIsNot.setVisible(false);
				cbSides.setVisible(false);
				
				cbBlocks.setVisible(true);
				cbTargets.setVisible(false);
				
				labelLine3.setVisible(false);
				textLine3.setVisible(false);
				
				labelLine4.setVisible(true);
				textLine4.setVisible(true);
				break;
			}
			
		}
		
	}

}
