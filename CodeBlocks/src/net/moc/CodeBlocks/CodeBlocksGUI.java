package net.moc.CodeBlocks;

import java.util.HashMap;
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
import net.moc.CodeBlocks.gui.events.GUIEventListener;
import net.moc.CodeBlocks.workspace.RobotnikController;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CodeBlocksGUI {
	//=====================================================
	private CodeBlocks plugin;
	private GUIEventListener listener; public GUIEventListener getListener() { return listener; }
	private HashMap<SpoutPlayer, RobotBrowser> robotBrowserWindows = new HashMap<SpoutPlayer, RobotBrowser>();
	private HashMap<SpoutPlayer, RobotController> robotControllerWindows = new HashMap<SpoutPlayer, RobotController>();
	private HashMap<SpoutPlayer, RobotInventory> robotInventoryWindows = new HashMap<SpoutPlayer, RobotInventory>();
	
	private HashMap<SpoutPlayer, FunctionBrowser> functionBrowserWindows = new HashMap<SpoutPlayer, FunctionBrowser>();
	private HashMap<SpoutPlayer, FunctionSelector> functionSelectorWindows = new HashMap<SpoutPlayer, FunctionSelector>();
	
	private HashMap<SpoutPlayer, MainWindow> mainWindows = new HashMap<SpoutPlayer, MainWindow>();
	private HashMap<SpoutPlayer, BlocksWindow> blocksWindows = new HashMap<SpoutPlayer, BlocksWindow>();
	private HashMap<SpoutPlayer, BaseBlockRoller>  baseBlockRollerWindows = new HashMap<SpoutPlayer, BaseBlockRoller>();
	private HashMap<SpoutPlayer, Feedback>  feedbackWindows = new HashMap<SpoutPlayer, Feedback>();
	
	private HashMap<SpoutPlayer, SignEditWindow> signEditWindows = new HashMap<SpoutPlayer, SignEditWindow>();
	private HashMap<SpoutPlayer, SignEditMathEvaluate> signEditMathEvaluateWindows = new HashMap<SpoutPlayer, SignEditMathEvaluate>();
	private HashMap<SpoutPlayer, SignEditFunctionValues> signEditFunctionValuesWindows = new HashMap<SpoutPlayer, SignEditFunctionValues>();
	private HashMap<SpoutPlayer, SignEditCounter> signEditCounterWindows = new HashMap<SpoutPlayer, SignEditCounter>();
	private HashMap<SpoutPlayer, SignEditSide> signEditSideWindows = new HashMap<SpoutPlayer, SignEditSide>();
	private HashMap<SpoutPlayer, SignEditMathAssign> signEditMathAssignWindows = new HashMap<SpoutPlayer, SignEditMathAssign>();
	
	public CodeBlocksGUI(CodeBlocks plugin) { this.plugin = plugin; listener = new GUIEventListener(plugin); }
	//=====================================================
	
	public void displayRobotInventory(SpoutPlayer player, RobotnikController rc) {
		if (!robotInventoryWindows.containsKey(player)) { robotInventoryWindows .put(player, new RobotInventory(player, plugin)); }
		robotInventoryWindows.get(player).open(rc);
		
	}

	public void displayRobotBrowser(SpoutPlayer player) {
		if (!this.robotBrowserWindows.containsKey(player)) { this.robotBrowserWindows.put(player, new RobotBrowser(player, this.plugin)); }
		robotBrowserWindows.get(player).open();
		
	}

	public void displayFeedback(SpoutPlayer player) {
		if (!this.feedbackWindows.containsKey(player)) { this.feedbackWindows.put(player, new Feedback(player, this.plugin)); }
		feedbackWindows.get(player).open();
		
	}

	public void displayRobotController(SpoutPlayer player, SpoutBlock block) {
		if (!this.robotControllerWindows.containsKey(player)) { this.robotControllerWindows.put(player, new RobotController(player, this.plugin)); }
		robotControllerWindows.get(player).open(block);
		
	}

	public void displayRobotController(SpoutPlayer player, RobotnikController rc) {
		if (!this.robotControllerWindows.containsKey(player)) { this.robotControllerWindows.put(player, new RobotController(player, this.plugin)); }
		robotControllerWindows.get(player).open(rc);
		
	}

	public void displayFunctionBrowser(SpoutPlayer player) {
		if (!this.functionBrowserWindows.containsKey(player)) { this.functionBrowserWindows.put(player, new FunctionBrowser(player, this.plugin)); }
		functionBrowserWindows.get(player).open();
		
	}

	public void displayFunctionSelector(SpoutPlayer player, SpoutBlock block, boolean isOnBlockPlace) {
		if (!this.functionSelectorWindows.containsKey(player)) { this.functionSelectorWindows.put(player, new FunctionSelector(player, this.plugin)); }
		functionSelectorWindows.get(player).open(block, isOnBlockPlace);
		
	}

	public void displayBaseBlockRoller(SpoutPlayer player, SpoutBlock block) {
		if (!this.baseBlockRollerWindows.containsKey(player)) { this.baseBlockRollerWindows.put(player, new BaseBlockRoller(player, this.plugin)); }
		baseBlockRollerWindows.get(player).open(block);
		
	}

	public void displayBlocks(SpoutPlayer player) {
		if (!this.blocksWindows.containsKey(player)) { this.blocksWindows .put(player, new BlocksWindow(player, this.plugin)); }
		blocksWindows.get(player).open();
		
	}

	public void displayMain(SpoutPlayer player) {
		if (!this.mainWindows.containsKey(player)) { this.mainWindows .put(player, new MainWindow(player, this.plugin)); }
		mainWindows.get(player).open();
		
	}
	
	public void displaySignEdit(SpoutPlayer player, SpoutBlock block) {
		if (!signEditWindows.containsKey(player)) { signEditWindows.put(player, new SignEditWindow(player, plugin)); }
		signEditWindows.get(player).open(block);
		
	}

	public void displaySignEditMathEvaluate(SpoutPlayer player, SpoutBlock block) {
		if (!signEditMathEvaluateWindows.containsKey(player)) { signEditMathEvaluateWindows.put(player, new SignEditMathEvaluate(player, plugin)); }
		signEditMathEvaluateWindows.get(player).open(block);
		
	}

	public void displaySignEditFunctionValues(SpoutPlayer player, SpoutBlock block) {
		if (!signEditFunctionValuesWindows.containsKey(player)) { signEditFunctionValuesWindows.put(player, new SignEditFunctionValues(player, plugin)); }
		signEditFunctionValuesWindows.get(player).open(block);
		
	}

	public void displaySignEditCounter(SpoutPlayer player, SpoutBlock block) {
		if (!signEditCounterWindows.containsKey(player)) { signEditCounterWindows.put(player, new SignEditCounter(player, plugin)); }
		signEditCounterWindows.get(player).open(block);
		
	}

	public void displaySignEditSide(SpoutPlayer player, SpoutBlock block) {
		if (!signEditSideWindows.containsKey(player)) { signEditSideWindows.put(player, new SignEditSide(player, plugin)); }
		signEditSideWindows.get(player).open(block);
		
	}

	public void displaySignEditMathAssign(SpoutPlayer player, SpoutBlock block) {
		if (!signEditMathAssignWindows.containsKey(player)) { signEditMathAssignWindows.put(player, new SignEditMathAssign(player, plugin)); }
		signEditMathAssignWindows.get(player).open(block);
		
	}

}
