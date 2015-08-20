package net.moc.CodeBlocks.gui.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.gui.BaseBlockRoller;
import net.moc.CodeBlocks.gui.BlocksWindow;
import net.moc.CodeBlocks.gui.Feedback;
import net.moc.CodeBlocks.gui.FunctionBrowser;
import net.moc.CodeBlocks.gui.FunctionSelector;
import net.moc.CodeBlocks.gui.MainWindow;
import net.moc.CodeBlocks.gui.RobotBrowser;
import net.moc.CodeBlocks.gui.RobotController;
import net.moc.CodeBlocks.gui.RobotInventory;
import net.moc.CodeBlocks.gui.SignEditCounter;
import net.moc.CodeBlocks.gui.SignEditFunctionValues;
import net.moc.CodeBlocks.gui.SignEditMathAssign;
import net.moc.CodeBlocks.gui.SignEditMathEvaluate;
import net.moc.CodeBlocks.gui.SignEditSide;
import net.moc.CodeBlocks.gui.SignEditWindow;
import net.moc.CodeBlocks.gui.widgets.MOCComboBoxSelectionEvent;

public class GUIEventListener implements Listener {
	//=============================================
	private CodeBlocks plugin;
	public GUIEventListener(CodeBlocks plugin) { this.plugin = plugin; }
	//=============================================

	//*****************************************************************************
	@EventHandler
	public void onEvent(KeyPressedEvent event) {
		Screen screen = event.getPlayer().getMainScreen().getActivePopup();
		
		//See what kind of screen it is
		if(screen instanceof BaseBlockRoller) { ((BaseBlockRoller) screen).onKeyPress(event.getKey()); return; }
		
		if (event.getPlayer().getMainScreen().getActivePopup() == null && event.getScreenType() == ScreenType.GAME_SCREEN && event.getKey() == Keyboard.KEY_C) {
			plugin.getGUI().displayMain(event.getPlayer());
			
		}
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof SignEditWindow) { ((SignEditWindow) screen).onClick(event.getButton()); }
		else if(screen instanceof SignEditMathEvaluate) { ((SignEditMathEvaluate) screen).onClick(event.getButton()); }
		else if(screen instanceof SignEditMathAssign) { ((SignEditMathAssign) screen).onClick(event.getButton()); }
		else if(screen instanceof SignEditFunctionValues) { ((SignEditFunctionValues) screen).onClick(event.getButton()); }
		else if(screen instanceof SignEditCounter) { ((SignEditCounter) screen).onClick(event.getButton()); }
		else if(screen instanceof SignEditSide) { ((SignEditSide) screen).onClick(event.getButton()); }
		else if(screen instanceof RobotBrowser) { ((RobotBrowser) screen).onClick(event.getButton()); }
		else if(screen instanceof RobotController) { ((RobotController) screen).onClick(event.getButton()); }
		else if(screen instanceof MainWindow) { ((MainWindow) screen).onClick(event.getButton()); }
		else if(screen instanceof FunctionBrowser) { ((FunctionBrowser) screen).onClick(event.getButton()); }
		else if(screen instanceof FunctionSelector) { ((FunctionSelector) screen).onClick(event.getButton()); }
		else if(screen instanceof RobotInventory) { ((RobotInventory) screen).onClick(event.getButton()); }
		else if(screen instanceof BlocksWindow) { ((BlocksWindow) screen).onClick(event.getButton()); }
		else if(screen instanceof BaseBlockRoller) { ((BaseBlockRoller) screen).onClick(event.getButton()); }
		else if(screen instanceof Feedback) { ((Feedback) screen).onClick(event.getButton()); }
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(TextFieldChangeEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof FunctionBrowser) { ((FunctionBrowser) screen).onTextChange(event.getTextField()); }
		else if(screen instanceof RobotController) { ((RobotController) screen).onTextChange(event.getTextField()); }
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(MOCComboBoxSelectionEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof RobotBrowser) { ((RobotBrowser) screen).onSelection(event.getListWidget()); }
		else if(screen instanceof RobotController) { ((RobotController) screen).onSelection(event.getListWidget()); }
		else if(screen instanceof FunctionBrowser) { ((FunctionBrowser) screen).onSelection(event.getListWidget()); }
		else if(screen instanceof FunctionSelector) { ((FunctionSelector) screen).onSelection(event.getListWidget()); }
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(ScreenCloseEvent event) {
		Screen screen = event.getScreen();
		if(screen instanceof RobotInventory) { ((RobotInventory) screen).syncInventories(); }
		
	}
	
	//*****************************************************************************
}
