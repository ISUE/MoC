package net.moc.CodeBlocks;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.ProgramCodeBlock;
import net.moc.CodeBlocks.blocks.RobotBlock;
import net.moc.CodeBlocks.blocks.attack.AttackBaseBlock;
import net.moc.CodeBlocks.blocks.function.BranchBlock;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.FunctionBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.IfFalseBlock;
import net.moc.CodeBlocks.blocks.function.IfTrueBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.InteractionBaseBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.MathBaseBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import net.moc.CodeBlocks.blocks.movement.MoveBlock;
import net.moc.CodeBlocks.blocks.movement.MovementBaseBlock;
import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.RobotnikController;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;

public class CodeBlocksListener implements Listener {
	//=============================================
	private CodeBlocks plugin;
	private long saveDelta = 6000;
	public CodeBlocksListener(CodeBlocks plugin) { this.plugin = plugin; }
	public void debug(String message) { plugin.getLog().debug(message); }
	//=============================================
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(SaveDataEvent event) {
		//Call save
		plugin.save();
		
		//Schedule next save
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() { Bukkit.getServer().getPluginManager().callEvent(new SaveDataEvent()); } }, saveDelta );
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(BlockPlaceEvent event) {
	    if (!codeBlocksUser(event.getPlayer()) || event.isCancelled()) return;
	    
	    //==========================================================================================================
	    //Get information
	    SpoutPlayer player = (SpoutPlayer) event.getPlayer();
	    SpoutBlock block = (SpoutBlock) event.getBlock();
	    CustomBlock customBlock = block.getCustomBlock();
	    
	    
	    
	    //==========================================================================================================
	    //robot
	    if (customBlock instanceof RobotBlock) { plugin.getGUI().displayRobotController(player, block); return; }
	    
	    
	    
	    //==========================================================================================================
	    //function block possible debug
	    if(customBlock instanceof FunctionBlock && plugin.getWorkspace().getPlayerWorkspace(player.getName()).getDebugBaseQueue().containsKey(player.getName())) {
	    	//Get controller
	    	RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getDebugBaseQueue().get(player.getName());
	    	//Remove player from queue
	    	plugin.getWorkspace().getPlayerWorkspace(player.getName()).getDebugBaseQueue().remove(player.getName());
	    	
	    	//Get function
	    	Function function = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunction(rc.getFunctionName());
	    	if (function != null) {
	    		function.paste(block);
				rc.setDebugBase(block.getLocation());
				plugin.getLog().sendPlayerNormal(player.getName(), "Function debugging base set.");
		    	return;
				
	    	}
	    	
	    }
	    
	    
	    //==========================================================================================================
	    //function/call block
	    if(customBlock instanceof FunctionBlock || customBlock instanceof CallFunctionBlock) {
	    	this.plugin.getGUI().displayFunctionSelector(player, block, true);
	    	return;
	    	
	    }
	    

	    
	    //==========================================================================================================
	    //Sign
	    if (block.getType() == Material.SIGN_POST &&
	    		( ((SpoutBlock) block.getLocation().add(0, -1, 0).getBlock()).getCustomBlock() instanceof FunctionBlock ||
	    		  ((SpoutBlock) block.getLocation().add(0, -1, 0).getBlock()).getCustomBlock() instanceof CallFunctionBlock)) {
	    	
	    	plugin.getGUI().displayFunctionSelector(player, (SpoutBlock) block.getLocation().add(0, -1, 0).getBlock(), false); return;
	    	
	    }
	    
	    
	    
	    //==========================================================================================================
	    //Sign on top of a program code block
	    SpoutBlock sp = null;
	    try { sp = (SpoutBlock) block.getLocation().add(0, -1, 0).getBlock(); } catch (Exception e) {}
	    if (block.getType() == Material.SIGN_POST && sp != null && sp.getCustomBlock() instanceof ProgramCodeBlock) {
	    	if (sp.getCustomBlock() instanceof SetBlock) { plugin.getGUI().displaySignEditMathAssign(player, sp); return; }
	    	else if (sp.getCustomBlock() instanceof EvaluateBlock) { plugin.getGUI().displaySignEditMathEvaluate(player, sp); return; }
	    	else if (sp.getCustomBlock() instanceof ForBlock) { plugin.getGUI().displaySignEditCounter(player, sp); return; }
	    	else if (sp.getCustomBlock() instanceof DestroyBlock || sp.getCustomBlock() instanceof DigBlock ||
	    			 sp.getCustomBlock() instanceof PlaceBlock || sp.getCustomBlock() instanceof PickUpBlock) { plugin.getGUI().displaySignEditCounter(player, sp); return; }
	    	else { plugin.getGUI().displaySignEdit(player, sp); return; }
	    	
	    }
	    
	    
	    
	    //==========================================================================================================
	    if (customBlock instanceof BranchBlock || customBlock instanceof AttackBaseBlock ||
	    	customBlock instanceof InteractionBaseBlock || customBlock instanceof MovementBaseBlock ||
	    	customBlock instanceof MathBaseBlock) { plugin.getGUI().displayBaseBlockRoller(player, block); return; }
	    
	    
	    
	    //==========================================================================================================
	    //One pc block for branch
	    if (customBlock instanceof ForBlock || customBlock instanceof WhileBlock || customBlock instanceof SetBlock) {
	    	SpoutBlock dBlock = (SpoutBlock) block.getLocation().add(0, 0, -1).getBlock();
	    	
	    	if (dBlock.getType() == Material.AIR) {
	    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
		    	dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
		    	
	    	}
	    	
	    	return;
	    	
	    }
 	    
	    
	    
	    //==========================================================================================================
	    //One pc block for line
	    if (customBlock instanceof IfTrueBlock || customBlock instanceof IfFalseBlock || customBlock instanceof CaseBlock) {
	    	SpoutBlock dBlock = (SpoutBlock) block.getLocation().add(-1, 0, 0).getBlock();
	    	
	    	if (dBlock.getType() == Material.AIR) {
	    		Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(dBlock, player));
		    	dBlock.setCustomBlock(plugin.getBlocks().getPointerBlock());
		    	
	    	}
	    	
	    	return;
	    	
	    }
 	    
	    
	    
	    //==========================================================================================================
	    //Two pc blocks
	    if (customBlock instanceof IfBlock) {
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
	    	
	    	
	    	return;
	    	
	    }
	    
	    //==========================================================================================================
	    //Two pc blocks
	    if (customBlock instanceof SwitchBlock) {
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
	    	
	    	return;
	    	
	    }
 	    
	}
	//*****************************************************************************
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(PlayerInteractEvent event) {
	    if (event.getClickedBlock() == null || event.isCancelled()) return;
	    if (!codeBlocksUser(event.getPlayer())) return;
	    
	    //==========================================================================================================
	    SpoutBlock block = null;
	    try { block = (SpoutBlock) event.getClickedBlock(); } catch (Exception e) { return; }
	    SpoutPlayer player = (SpoutPlayer) event.getPlayer();
	    SpoutItemStack itemInHand = new SpoutItemStack(event.getPlayer().getItemInHand());

	    
	    
	    
	    //==========================================================================================================
	    //Check if code blocks tool in hand
	    boolean toolEquiped = false;
	    boolean whiteSpaceEquiped = false;
	    if (itemInHand.getMaterial() != null && this.plugin.getBlocks().getToolItem().getName().equalsIgnoreCase(itemInHand.getMaterial().getName())) { toolEquiped = true; }
	    if (itemInHand.getMaterial() != null && this.plugin.getBlocks().getWhiteSpaceBlock().getName().equalsIgnoreCase(itemInHand.getMaterial().getName())) { whiteSpaceEquiped = true; }


	    
	    
	    //==========================================================================================================
	    //Case of a sign above one of the CB blocks
	    if (block.getType() == Material.SIGN_POST && toolEquiped) {
	    	//Get block under the sign
	    	SpoutBlock underBlock = null;
	    	try { underBlock = (SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()); } catch (Exception e) { return; }

	    	if (underBlock.getCustomBlock() instanceof FunctionBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displayFunctionSelector(player, underBlock, false);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof CallFunctionBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEditFunctionValues(player, underBlock);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof SetBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEditMathAssign(player, underBlock);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof EvaluateBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEditMathEvaluate(player, underBlock);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof ForBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEditCounter(player, underBlock);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof MoveBlock || underBlock.getCustomBlock() instanceof DigBlock || underBlock.getCustomBlock() instanceof DestroyBlock ||
	    		underBlock.getCustomBlock() instanceof PickUpBlock || underBlock.getCustomBlock() instanceof PlaceBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEditSide(player, underBlock);
	    		return;
	    		
	    	}
	    	
	    	if (underBlock.getCustomBlock() instanceof ProgramCodeBlock) {
	    		event.setCancelled(true);
	    		plugin.getGUI().displaySignEdit(player, underBlock);
	    		return;

	    	}

	    	return;

	    }
	    
	    
	    
	    
	    //==========================================================================================================
	    //Top face of a block clicked
	    if (event.getBlockFace() == BlockFace.UP && block.getCustomBlock() instanceof FunctionBlock && toolEquiped) {
	    	event.setCancelled(true);
	    	this.plugin.getGUI().displayFunctionSelector(player, block, false);
	    	return;
	    	
	    }
	    
	    
	    
	    
	    
	    //==========================================================================================================
	    if (block.getCustomBlock() instanceof CallFunctionBlock && toolEquiped) {
	    	event.setCancelled(true);
	    	this.plugin.getGUI().displayFunctionSelector(player, block, false);
	    	return;
	    	
	    }
	    
	    
	    
	    
	    
	    //==========================================================================================================
	    //Clicking function block with tool to compile/save
	    if (block.getCustomBlock() instanceof FunctionBlock && toolEquiped) {
	    	//Parse function
	    	ArrayList<CodeBlock> codeBlocks = this.plugin.getWorkspace().parse(block);
	    	
	    	//Return if nothing useful was parsed
	    	if (codeBlocks == null) { event.setCancelled(true); return; }
	    	if (codeBlocks.get(0) == null) { event.setCancelled(true); return; }
	    	
	    	String functionName = codeBlocks.get(0).getValues().getFunctionName();

	    	//Check if function name is already present
	    	Function function = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getFunction(functionName);

	    	//Create new function, if no match was found
	    	if (function == null) {
	    		function = new Function(plugin, player, functionName);
	    		plugin.getWorkspace().getPlayerWorkspace(player.getName()).addFunction(function);

	    	}

	    	//Set code blocks
	    	function.setBlocks(codeBlocks);

	    	//Compile the function
	    	Boolean result = function.compile();
	    	if (result) {
	    		plugin.getLog().sendPlayerNormal(player.getName(), "Function " + functionName + " was compiled. [b" + function.getFunctionBlocks().size() + "|c" + function.getCode().size() + "]");
	    		plugin.getSQL().saveFunction(player, function);

	    	} else {
	    		plugin.getLog().sendPlayerWarn(player.getName(), "Function " + functionName + " was not compiled due to errors.");

	    	}

	    	event.setCancelled(true);
	    	return;

	    }

	    
	    
	    
	    //==========================================================================================================
	    //Clicking any CB block with tool
    	if (block.getCustomBlock() instanceof SetBlock && toolEquiped) {
    		event.setCancelled(true);
    		plugin.getGUI().displaySignEditMathAssign(player, block);
    		return;
    		
    	}
    	if (block.getCustomBlock() instanceof EvaluateBlock && toolEquiped) {
    		event.setCancelled(true);
    		plugin.getGUI().displaySignEditMathEvaluate(player, block);
    		return;
    		
    	}

	    
    	
    	
    	
    	//==========================================================================================================
    	if (block.getCustomBlock() instanceof ForBlock && toolEquiped) {
    		event.setCancelled(true);
    		plugin.getGUI().displaySignEditCounter(player, block);
    		return;
    		
    	}

	    
    	
    	
    	
    	//==========================================================================================================
    	if ((block.getCustomBlock() instanceof MoveBlock || block.getCustomBlock() instanceof DigBlock || block.getCustomBlock() instanceof DestroyBlock ||
    		block.getCustomBlock() instanceof PickUpBlock || block.getCustomBlock() instanceof PlaceBlock) && toolEquiped) {
	    	event.setCancelled(true);
	    	plugin.getGUI().displaySignEditSide(player, block);
	    	return;
	    		
	    }
    	
	    
    	
    	
    	
    	//==========================================================================================================
	    if (block.getCustomBlock() instanceof ProgramCodeBlock && toolEquiped) {
	    	//If top of the block - try to set sign
	    	event.setCancelled(true);
	    	plugin.getGUI().displaySignEdit(player, block);
	    	return;

	    }
	    
	    
	    
	    
	    
	    //==========================================================================================================
	    //Clicking any CB block with white space
	    if (block.getCustomBlock() instanceof ProgramCodeBlock && whiteSpaceEquiped) {
	    	if (event.getAction() == Action.LEFT_CLICK_BLOCK) plugin.getWorkspace().getCodeBlocksShifter().shiftLeft(block);
	    	else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) plugin.getWorkspace().getCodeBlocksShifter().shiftRight(block);
	    	event.setCancelled(true);
	    	return;

	    }

	    
	    
	    
	    
	    //==========================================================================================================
	    //Clicking robot block
	    if (block.getCustomBlock() instanceof RobotBlock) {
	    	//Try to find matching rc
	    	RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(player.getName()).getRobotnik(block);

	    	//If found open controller window
	    	if (rc != null) {
	    		//Let's see what player has in hand, maybe this is fuel input
	    		HashMap<Integer, Integer> bp = plugin.getConfiguration().getBlockPowers();
	    		
	    		if (bp.containsKey(itemInHand.getTypeId()) && (rc.getRobotnik().getStats().getPowerCurrent() != rc.getRobotnik().getStats().getPowerMax())) {
	    			//Power item
	    			//Calculate power amount supplied
	    			double delta = rc.getRobotnik().getStats().getPowerMax() - rc.getRobotnik().getStats().getPowerCurrent();
	    			Integer blockValue = bp.get(itemInHand.getTypeId());
	    			
	    			int blocksUsed = (int) Math.ceil(delta / blockValue);
	    			
	    			if (itemInHand.getAmount() > blocksUsed) {
	    				int power = blocksUsed * bp.get(itemInHand.getTypeId());
	    				plugin.getLog().sendPlayerNormal(player.getName(), "Added: [" + power + "] [" + itemInHand.getMaterial().getName() + " / " + bp.get(itemInHand.getTypeId()) + "]");
		    			rc.getRobotnik().getStats().addPowerCurrent(power);
	    				plugin.getLog().sendPlayerNormal(player.getName(), "Robot Power Level: [" + (int)rc.getRobotnik().getStats().getPowerCurrent() + " / " + (int)rc.getRobotnik().getStats().getPowerMax() + "]");
		    			
		    			itemInHand.setAmount(itemInHand.getAmount() - blocksUsed);
		    			player.setItemInHand(itemInHand);
	    				
	    			} else {
		    			int power = itemInHand.getAmount() * bp.get(itemInHand.getTypeId());
		    			
		    			//Clear player's item in hand
		    			player.setItemInHand(new ItemStack(0));
		    			
		    			//Add power to robotnik
	    				plugin.getLog().sendPlayerNormal(player.getName(), "Added: [" + power + "] [" + itemInHand.getMaterial().getName() + " / " + bp.get(itemInHand.getTypeId()) + "]");
		    			rc.getRobotnik().getStats().addPowerCurrent(power);
	    				plugin.getLog().sendPlayerNormal(player.getName(), "Robot Power Level: [" + (int)rc.getRobotnik().getStats().getPowerCurrent() + " / " + (int)rc.getRobotnik().getStats().getPowerMax() + "]");
	    				
	    			}
	    			
			    	event.setCancelled(true);
			    	
	    			return;
	    			
	    		}
	    		
	    		plugin.getGUI().displayRobotController(player, rc);
		    	event.setCancelled(true);
		    	
	    	}

	    	return;

	    }

	    
	}
	//*****************************************************************************
	
	//===========================================================================
	private boolean codeBlocksUser(Player player) {
		if (player.hasPermission(this.plugin.getPERMISSION_ALL())) return true;
		else return false;
	}
	//===========================================================================
}
