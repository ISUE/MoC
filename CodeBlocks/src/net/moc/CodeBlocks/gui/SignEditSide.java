package net.moc.CodeBlocks.gui;

import java.util.ArrayList;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.movement.MoveBlock;
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

public class SignEditSide extends GenericPopup {
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
	private MOCComboBox cbVariable;
	private GenericComboBox cbSide;
	private GenericTextField textVariable;
	
	//Window buttons
	private GenericButton buttonCancel, buttonSet;
	
	//Blocks
	private Block signBlock;
	private SpoutBlock baseBlock;
	//----------------------------------------------------------------
	
	public SignEditSide(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		//Type
		labelTitle = new GenericLabel("Robot Side");

		ArrayList<String> fvars = new ArrayList<String>(); fvars.add("Side"); for (FunctionVariables fv : FunctionVariables.values()) fvars.add(fv.toString().toLowerCase());
		cbVariable = new MOCComboBox();
		cbVariable.setText("");
		cbVariable.setHoverColor(hoverColor);
		cbVariable.setItems(fvars);
		
		textVariable = new GenericTextField();
		textVariable.setFieldColor(textFieldColor);

		
		ArrayList<String> sides = new ArrayList<String>();
		for (RobotSide rs : RobotSide.values()) { if (rs != RobotSide.inventory && rs != RobotSide.around) sides.add(rs.toString().toLowerCase()); }
		cbSide = new GenericComboBox();
		cbSide.setText("");
		cbSide.setHoverColor(hoverColor);
		cbSide.setItems(sides);
		
		//Window buttons
		buttonCancel = new GenericButton("Cancel");
		buttonCancel.setTooltip("Cancel");
		buttonCancel.setHoverColor(hoverColor);

		buttonSet = new GenericButton("Set");
		buttonSet.setTooltip("Set");
		buttonSet.setHoverColor(hoverColor);
		
		//Attach
		attachWidgets(plugin, gradientBackground, labelTitle, cbVariable, textVariable, cbSide, buttonCancel, buttonSet);
		
		//Initialize
		initialize();

	}
	
	private void initialize() {
		//Corners and size of the window
		int windowWidth = 150;
		int windowHeight = 60;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        
		//Background
		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Type
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		labelTitle.setHeight(15).setWidth(windowWidth);
		
		cbVariable.setX(upLeftX+5).setY(upLeftY+20);
		cbVariable.setHeight(15).setWidth(70);
		cbVariable.closeList();
		cbVariable.setSelection(0);
		
		textVariable.setX(upLeftX+80).setY(upLeftY+20);
		textVariable.setHeight(15).setWidth(65);
		textVariable.setText("");
		textVariable.setVisible(false);
		
		cbSide.setX(upLeftX+80).setY(upLeftY+20);
		cbSide.setHeight(15).setWidth(65);
		cbSide.closeList();
		cbSide.setSelection(0);
		cbSide.setVisible(false);
		
		//Window buttons
		buttonCancel.setX(upLeftX+5).setY(upLeftY+40);
		buttonCancel.setWidth(windowWidth/2-5).setHeight(15);
		
		buttonSet.setX(upLeftX+windowWidth/2).setY(upLeftY+40);
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
		
		String value = "";
		if (signBlock.getType() == Material.SIGN_POST) { Sign sign = (Sign) signBlock.getState(); value = sign.getLine(0); sign.update(); }
		
		if (baseBlock.getCustomBlock() instanceof MoveBlock || baseBlock.getCustomBlock() instanceof DestroyBlock || baseBlock.getCustomBlock() instanceof DigBlock ||
			baseBlock.getCustomBlock() instanceof PickUpBlock || baseBlock.getCustomBlock() instanceof PlaceBlock) { matchValue(value); }
		else return;
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}

	private void matchValue(String value) {
		value = value.toLowerCase().trim();
		cbVariable.setSelection(0);
		cbSide.setVisible(true);
		cbSide.setSelection(0);
		textVariable.setVisible(false);
		
		if (value.length() == 0) { return; }
		
		for (int i = 0 ; i < cbVariable.getItems().size() ; i++) {
			if (value.equalsIgnoreCase(cbVariable.getItems().get(i))) {
				cbVariable.setSelection(i);
				cbSide.setVisible(false);
				textVariable.setVisible(true);
				textVariable.setText(cbVariable.getItems().get(i));
				break;
				
			}
			
		}
		
		if (!textVariable.isVisible()) for (int i = 0 ; i < cbSide.getItems().size() ; i++) if (value.equalsIgnoreCase(cbSide.getItems().get(i))) cbSide.setSelection(i);
		
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
			if (cbSide.isVisible()) sign.setLine(0, cbSide.getSelectedItem().toLowerCase()); 
			else sign.setLine(0, textVariable.getText());
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
			
			closeWindow();
			return;

		}
		
		if (button.equals(cbVariable) && cbVariable.getSelectedItem() != null) {
			//"Side", "retval", "a1", "a2", "a3", "v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8", "v9", "v10"
			switch (cbVariable.getSelectedItem()) {
			case "Side": textVariable.setVisible(false); cbSide.setVisible(true); break;
			default: textVariable.setVisible(true); textVariable.setText(cbVariable.getSelectedItem()); cbSide.setVisible(false); break;
			}
			
			refresh();
			
		}
		
	}

}
