package net.moc.CodeBlocks.gui;

import java.util.Arrays;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.attack.AttackBaseBlock;
import net.moc.CodeBlocks.blocks.function.BranchBlock;
import net.moc.CodeBlocks.blocks.interaction.InteractionBaseBlock;
import net.moc.CodeBlocks.blocks.math.MathBaseBlock;
import net.moc.CodeBlocks.blocks.movement.MovementBaseBlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericComboBox;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BaseBlockRoller extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	private Gradient gradientBackground;
	private GenericComboBox comboboxBlockSelector1, comboboxBlockSelector2, comboboxBlockSelector3, comboboxBlockSelector4, comboboxBlockSelector5;
	private GenericButton buttonOK;
	
	private SpoutBlock block;
	//================================================================================================================
	
	
	
	//================================================================================================================
	public BaseBlockRoller(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		gradientBackground = new GenericGradient(backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);

		//Combo Box
		comboboxBlockSelector1 = new GenericComboBox();
		comboboxBlockSelector1.setText("");
		comboboxBlockSelector1.setHoverColor(hoverColor);
		
		comboboxBlockSelector2 = new GenericComboBox();
		comboboxBlockSelector2.setText("");
		comboboxBlockSelector2.setHoverColor(hoverColor);
		
		comboboxBlockSelector3 = new GenericComboBox();
		comboboxBlockSelector3.setText("");
		comboboxBlockSelector3.setHoverColor(hoverColor);
		
		comboboxBlockSelector4 = new GenericComboBox();
		comboboxBlockSelector4.setText("");
		comboboxBlockSelector4.setHoverColor(hoverColor);
		
		comboboxBlockSelector5 = new GenericComboBox();
		comboboxBlockSelector5.setText("");
		comboboxBlockSelector5.setHoverColor(hoverColor);
		
		//Window button
		buttonOK = new GenericButton("OK");
		buttonOK.setTooltip("OK");
		buttonOK.setHoverColor(hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, gradientBackground, comboboxBlockSelector1, comboboxBlockSelector2, comboboxBlockSelector3, comboboxBlockSelector4, comboboxBlockSelector5, buttonOK);

		//Initialize
		initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = 100;
		int windowHeight = 40;
        int upLeftX = (player.getMainScreen().getWidth() - windowWidth) / 2;
        int upLeftY = (player.getMainScreen().getHeight() - windowHeight) / 2;
        
		//Background
		gradientBackground.setWidth(windowWidth).setHeight(windowHeight);
		gradientBackground.setX(upLeftX).setY(upLeftY);

		//Combo box
		comboboxBlockSelector1.setX(upLeftX+5).setY(upLeftY+5);
		comboboxBlockSelector1.setWidth(windowWidth - 10).setHeight(15);
		comboboxBlockSelector1.setItems(Arrays.asList("1: For", "2: If", "3: True", "4: False", "5: While", "6: Switch", "7: Case"));
		comboboxBlockSelector1.setSelection(0);
		comboboxBlockSelector1.closeList();
		comboboxBlockSelector1.setVisible(false);
		
		comboboxBlockSelector2.setX(upLeftX+5).setY(upLeftY+5);
		comboboxBlockSelector2.setWidth(windowWidth - 10).setHeight(15);
		comboboxBlockSelector2.setItems(Arrays.asList("1: Attack Far", "2: Attack Near"));
		comboboxBlockSelector2.setSelection(0);
		comboboxBlockSelector2.closeList();
		comboboxBlockSelector2.setVisible(false);
		
		comboboxBlockSelector3.setX(upLeftX+5).setY(upLeftY+5);
		comboboxBlockSelector3.setWidth(windowWidth - 10).setHeight(15);
		comboboxBlockSelector3.setItems(Arrays.asList("1: Build", "2: Dig", "3: Destroy", "4: Pick Up", "5: Place"));
		comboboxBlockSelector3.setSelection(0);
		comboboxBlockSelector3.closeList();
		comboboxBlockSelector3.setVisible(false);
		
		comboboxBlockSelector4.setX(upLeftX+5).setY(upLeftY+5);
		comboboxBlockSelector4.setWidth(windowWidth - 10).setHeight(15);
		comboboxBlockSelector4.setItems(Arrays.asList("1: Forward", "2: Right", "3: Left", "4: Back", "5: Up", "6: Down", "7: Turn Left", "8: Turn Right", "9: Variable Move"));
		comboboxBlockSelector4.setSelection(0);
		comboboxBlockSelector4.closeList();
		comboboxBlockSelector4.setVisible(false);
		
		comboboxBlockSelector5.setX(upLeftX+5).setY(upLeftY+5);
		comboboxBlockSelector5.setWidth(windowWidth - 10).setHeight(15);
		comboboxBlockSelector5.setItems(Arrays.asList("1: Set", "2: Evaluate"));
		comboboxBlockSelector5.setSelection(0);
		comboboxBlockSelector5.closeList();
		comboboxBlockSelector5.setVisible(false);
		
		//Window buttons
		buttonOK.setX(upLeftX+5).setY(upLeftY+20);
		buttonOK.setWidth(90).setHeight(15);
		
	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(SpoutBlock block){
		this.block = block;
		
		initialize();
		loadBlocks();
		
		player.getMainScreen().attachPopupScreen(this);
		
		refresh();
		
	}
	
	private void refresh() { setDirty(true); for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); } }
	
	//================================================================================================================
	private void loadBlocks() {
		CustomBlock cb = block.getCustomBlock();
		
		if (cb instanceof BranchBlock) {
			comboboxBlockSelector1.setVisible(true);
			
		} else if (cb instanceof AttackBaseBlock) {
			comboboxBlockSelector2.setVisible(true);
			
		} else if (cb instanceof InteractionBaseBlock) {
			comboboxBlockSelector3.setVisible(true);
			
		} else if (cb instanceof MovementBaseBlock) {
			comboboxBlockSelector4.setVisible(true);
			
		} else if (cb instanceof MathBaseBlock) {
			comboboxBlockSelector5.setVisible(true);
			
		}
		
	}
	//================================================================================================================
	

	
	//================================================================================================================
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Re do close pop up - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Cancel buttons
		SpoutBlock dBlock;
		if (button.equals(this.buttonOK)) {
			if (comboboxBlockSelector1.isVisible()) {
				switch (comboboxBlockSelector1.getSelectedRow()) {
				case 0:
					block.setCustomBlock(plugin.getBlocks().getForBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
						
				    	if (dBlock.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
				    		dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
				    		
				    	}
				    	
					} catch (Exception e) {}
					break;
				case 1:
					block.setCustomBlock(plugin.getBlocks().getIfBlock());
					try {
				    	SpoutBlock c1 = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
				    	SpoutBlock c2 = (SpoutBlock) block.getLocation().add(0, 0, -2).getBlock();
				    	
				    	if (c1.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c1, player));
					    	c1.setCustomBlock(plugin.getBlocks().getIfTrueBlock());
					    	
				    	}
				    	
				    	if (c2.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c2, player));
					    	c2.setCustomBlock(plugin.getBlocks().getIfFalseBlock());
					    	
				    	}
				    	
				    	
				    	c1 = (SpoutBlock) c1.getLocation().add(-1, 0, 0).getBlock();
				    	c2 = (SpoutBlock) c2.getLocation().add(-1, 0, 0).getBlock();

				    	if (c1.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c1, player));
					    	c1.setCustomBlock(plugin.getBlocks().getPointerBlock());
					    	
				    	}
				    	
				    	if (c2.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c2, player));
					    	c2.setCustomBlock(plugin.getBlocks().getPointerBlock());
					    	
				    	}
				    	
				    	
					} catch (Exception e) {}
					break;
				case 2:
					block.setCustomBlock(plugin.getBlocks().getIfTrueBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(-1, 0, 0).getBlock();
				    	
						if (dBlock.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
							dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
				    	}
				    	
					} catch (Exception e) {}
					break;
				case 3:
					block.setCustomBlock(plugin.getBlocks().getIfFalseBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(-1, 0, 0).getBlock();
				    	
						if (dBlock.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
							dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}
						
					} catch (Exception e) {}
					break;
				case 4:
					block.setCustomBlock(plugin.getBlocks().getWhileBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
						
						if (dBlock.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
							dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}

					} catch (Exception e) {}
					break;
				case 5:
					block.setCustomBlock(plugin.getBlocks().getSwitchBlock());
					try {
				    	SpoutBlock c1 = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
				    	SpoutBlock c2 = (SpoutBlock) block.getLocation().add(0, 0, -2).getBlock();
				    	SpoutBlock c3 = (SpoutBlock) block.getLocation().add(0, 0, -3).getBlock();
				    	
						if (c1.getType() == Material.AIR) {
					    	Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c1, player));
					    	c1.setCustomBlock(plugin.getBlocks().getCaseBlock());
							
						}
				    	
						if (c2.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c2, player));
					    	c2.setCustomBlock(plugin.getBlocks().getCaseBlock());
					    	
						}
						
						if (c3.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c3, player));
					    	c3.setCustomBlock(plugin.getBlocks().getCaseBlock());
					    	
						}
				    	
				    	c1 = (SpoutBlock) c1.getLocation().add(-1, 0, 0).getBlock();
				    	c2 = (SpoutBlock) c2.getLocation().add(-1, 0, 0).getBlock();
				    	c3 = (SpoutBlock) c3.getLocation().add(-1, 0, 0).getBlock();
						
				    	if (c1.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c1, player));
					    	c1.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}
						
				    	if (c2.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c2, player));
					    	c2.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}
						
				    	if (c3.getType() == Material.AIR) {
					    	Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(c3, player));
					    	c3.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}
				    	
					} catch (Exception e) {}
					break;
				case 6:
					block.setCustomBlock(plugin.getBlocks().getCaseBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(-1, 0, 0).getBlock();
				    	
						if (dBlock.getType() == Material.AIR) {
				    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
							dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
				    	}
				    	
					} catch (Exception e) {}
					break;
					
				}
				
			}

			if (comboboxBlockSelector2.isVisible()) {
				switch (comboboxBlockSelector2.getSelectedRow()) {
				case 0:
					block.setCustomBlock(plugin.getBlocks().getAttackFarBlock());
					break;
				case 1:
					block.setCustomBlock(plugin.getBlocks().getAttackNearBlock());
					break;
					
				}

			}

			if (comboboxBlockSelector3.isVisible()) {
				switch (comboboxBlockSelector3.getSelectedRow()) {
				case 0:
					block.setCustomBlock(plugin.getBlocks().getBuildBlock());
					break;
				case 1:
					block.setCustomBlock(plugin.getBlocks().getDigBlock());
					break;
				case 2:
					block.setCustomBlock(plugin.getBlocks().getDestroyBlock());
					break;
				case 3:
					block.setCustomBlock(plugin.getBlocks().getPickUpBlock());
					break;
				case 4:
					block.setCustomBlock(plugin.getBlocks().getPlaceBlock());
					break;
					
				}
				
			}

			if (comboboxBlockSelector4.isVisible()) {
				switch (comboboxBlockSelector4.getSelectedRow()) {
				case 0:
					block.setCustomBlock(plugin.getBlocks().getForwardBlock());
					break;
				case 1:
					block.setCustomBlock(plugin.getBlocks().getRightBlock());
					break;
				case 2:
					block.setCustomBlock(plugin.getBlocks().getLeftBlock());
					break;
				case 3:
					block.setCustomBlock(plugin.getBlocks().getBackBlock());
					break;
				case 4:
					block.setCustomBlock(plugin.getBlocks().getUpBlock());
					break;
				case 5:
					block.setCustomBlock(plugin.getBlocks().getDownBlock());
					break;
				case 6:
					block.setCustomBlock(plugin.getBlocks().getTurnLeftBlock());
					break;
				case 7:
					block.setCustomBlock(plugin.getBlocks().getTurnRightBlock());
					break;
				case 8:
					block.setCustomBlock(plugin.getBlocks().getMoveBlock());
					break;
				}
				
			}
			
			//"1: Set", "2: Evaluate"
			if (comboboxBlockSelector5.isVisible()) {
				switch (comboboxBlockSelector5.getSelectedRow()) {
				case 0:
					block.setCustomBlock(plugin.getBlocks().getSetBlock());
					try {
						dBlock = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
						if (dBlock.getType() == Material.AIR) {
							Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
							dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
							
						}
						
					} catch (Exception e) {}
					break;
				case 1:
					block.setCustomBlock(plugin.getBlocks().getEvaluateBlock());
					break;
				}
				
			}
			
			closeWindow();
			
			return;
			
		}
		
	}

	public void onKeyPress(Keyboard key) {
		//1 : 0-6
		//2 : 0-1
		//3 : 0-4
		//4 : 0-7
		//5 : 0-6
		switch (key) {
		case KEY_1:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(0); onClick(buttonOK); }
			else if (comboboxBlockSelector2.isVisible()) { comboboxBlockSelector2.setSelection(0); onClick(buttonOK); }
			else if (comboboxBlockSelector3.isVisible()) { comboboxBlockSelector3.setSelection(0); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(0); onClick(buttonOK); }
			else if (comboboxBlockSelector5.isVisible()) { comboboxBlockSelector5.setSelection(0); onClick(buttonOK); }
			break;
		case KEY_2:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(1); onClick(buttonOK); }
			else if (comboboxBlockSelector2.isVisible()) { comboboxBlockSelector2.setSelection(1); onClick(buttonOK); }
			else if (comboboxBlockSelector3.isVisible()) { comboboxBlockSelector3.setSelection(1); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(1); onClick(buttonOK); }
			else if (comboboxBlockSelector5.isVisible()) { comboboxBlockSelector5.setSelection(1); onClick(buttonOK); }
			break;
		case KEY_3:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(2); onClick(buttonOK); }
			else if (comboboxBlockSelector3.isVisible()) { comboboxBlockSelector3.setSelection(2); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(2); onClick(buttonOK); }
			break;
		case KEY_4:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(3); onClick(buttonOK); }
			else if (comboboxBlockSelector3.isVisible()) { comboboxBlockSelector3.setSelection(3); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(3); onClick(buttonOK); }
			break;
		case KEY_5:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(4); onClick(buttonOK); }
			else if (comboboxBlockSelector3.isVisible()) { comboboxBlockSelector3.setSelection(4); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(4); onClick(buttonOK); }
			break;
		case KEY_6:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(5); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(5); onClick(buttonOK); }
			break;
		case KEY_7:
			if (comboboxBlockSelector1.isVisible()) { comboboxBlockSelector1.setSelection(6); onClick(buttonOK); }
			else if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(6); onClick(buttonOK); }
			break;
		case KEY_8:
			if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(7); onClick(buttonOK); }
			break;
		case KEY_9:
			if (comboboxBlockSelector4.isVisible()) { comboboxBlockSelector4.setSelection(8); onClick(buttonOK); }
			break;
			
		}
		
	}
	
}
