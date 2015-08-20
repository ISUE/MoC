package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.attack.AttackFarBlock;
import net.moc.CodeBlocks.blocks.attack.AttackNearBlock;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.FunctionBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.interaction.BuildBlock;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.MathBaseBlock;
import net.moc.CodeBlocks.blocks.math.MathBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import net.moc.CodeBlocks.blocks.movement.MoveBlock;
import net.moc.CodeBlocks.workspace.command.Command;
import net.moc.CodeBlocks.workspace.events.FunctionTryAction;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;

public class Function {
	private CodeBlocks plugin;
	private Player player;
	private String name;
	private int id;
	private String description;
	private ArrayList<CodeBlock> functionBlocks; public ArrayList<CodeBlock> getFunctionBlocks() { return functionBlocks; }
	private ArrayList<Command> code; public ArrayList<Command> getCode() { return code; }
	
	//==========================================================================================
	public Function(CodeBlocks plugin, Player player, String name) {
		this.plugin = plugin;
		this.player = player;
		this.name = name;
		this.description = name + " description.";
		this.functionBlocks = new ArrayList<CodeBlock>();
		this.code = new ArrayList<Command>();
		this.id = -1;
		
	}
	
	//==========================================================================================
	public void setBlocks(ArrayList<CodeBlock> functionBlocks) { this.functionBlocks = functionBlocks;}
	public void clearBlocks() { functionBlocks.clear(); }
	
	//==========================================================================================
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	//==========================================================================================
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	//==========================================================================================
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }

	//==========================================================================================
	public boolean compile() {
		code = this.plugin.getWorkspace().compile(this, player);
		
		if (code == null) return false;
		
		return true;
		
	}
	
	//==========================================================================================
	public String toText() {
		String retval = "";
		
		for (CodeBlock cb : functionBlocks) {
			String blockInfo = "";
			
			if (cb != null) {
				blockInfo += cb.getCustomBlock().getName() + ";";
				blockInfo += cb.getOffsetX() + ";";
				blockInfo += cb.getOffsetY() + ";";
				blockInfo += cb.getOffsetZ() + ";";
				
				blockInfo += cb.getValues().getType() + ";";
				
				blockInfo += cb.getValues().getTrueSide() + ";";
				blockInfo += cb.getValues().getTrueTargetType() + ";";
				blockInfo += cb.getValues().getTrueData() + ";";
				blockInfo += cb.getValues().getTrueDistance() + ";";
				
				blockInfo += cb.getValues().getCounter() + ";";
				
				blockInfo += cb.getValues().getFunctionName() + ";";
				
				blockInfo += cb.getValues().getBuildLocation() + ";";
				blockInfo += cb.getValues().getBuildBlockType() + ";";
				blockInfo += cb.getValues().getBuildBlockData() + ";";
				
				blockInfo += cb.getValues().getInteractionDirection() + ";";
				
			} else {
				blockInfo += "NULL";
				
			}
			
			blockInfo += "\n";
			
			retval += blockInfo;
			
		}
		
		return retval;
		
	}
	
	//==========================================================================================
	public static Function toFunction(String playerName, String name, String data, String description, int id, CodeBlocks plugin) {
		Function function = new Function(plugin, plugin.getServer().getPlayer(playerName), name);
		function.setId(id);
		function.setDescription(description);
		
		String[] arrayData = data.split("\n");
		
		ArrayList<CodeBlock> blocks = new ArrayList<CodeBlock>();
		
		for (String blockData : arrayData) {
			if (blockData.equalsIgnoreCase("NULL")) {
				blocks.add(null);
			} else {
				String[] parts = blockData.split(";");
				
				blocks.add(new CodeBlock(plugin, parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6],
												 parts[7], parts[8], parts[9], parts[10], parts[11], parts[12], parts[13], parts[14]));
				
			}
			
		}
		
		function.setBlocks(blocks);
		function.compile();
		
		return function;
		
	}

	//==========================================================================================
	public void paste(SpoutBlock baseBlock) {
		World world = baseBlock.getWorld();

		for (CodeBlock codeBlock : functionBlocks) {
			if (codeBlock != null) {
				//Get blocks
				SpoutBlock block = (SpoutBlock) world.getBlockAt(baseBlock.getLocation().add(codeBlock.getOffsetX(), codeBlock.getOffsetY(), codeBlock.getOffsetZ()));
				SpoutBlock valueBlock = (SpoutBlock) block.getLocation().add(0, 1, 0).getBlock();
				
				if (block.getCustomBlock() != null) block.setCustomBlock(null);
				if (valueBlock.getCustomBlock() != null) block.setCustomBlock(null);
				
				//Create events
				BlockBreakEvent baseEvent = new BlockBreakEvent(block, player);
				BlockBreakEvent valueEvent = new BlockBreakEvent(valueBlock, player);

				//Save them for later use
				plugin.getWorkspace().getListener().getFunctionTryQueue().add(new FunctionTryAction(this, baseEvent, valueEvent, block, valueBlock, codeBlock));

				//Call events - if neither is cancelled, pasteCan will be called
				Bukkit.getServer().getPluginManager().callEvent(baseEvent);
				Bukkit.getServer().getPluginManager().callEvent(valueEvent);
				
			}
			
		}
		
	}
	
	public void pasteCan(SpoutBlock block, Block valueBlock, CodeBlock b) {
		//Set custom block
		CustomBlock customBlock = b.getCustomBlock();
		block.setCustomBlock(customBlock);
		
		if (customBlock instanceof CaseBlock || customBlock instanceof IfBlock || customBlock instanceof  WhileBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line values
			sign.setLine(0, b.getValues().getTrueSide().toString());
			sign.setLine(1, b.getValues().getTrueTargetType().toString());
			sign.setLine(2, b.getValues().getTrueDistance()+"");
			if (!b.getValues().getTrueData().equalsIgnoreCase("-1")) sign.setLine(3, b.getValues().getTrueData());
			else sign.setLine(3, "");
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof ForBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line value
			sign.setLine(0, b.getValues().getCounter()+"");
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof FunctionBlock || customBlock instanceof CallFunctionBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line value
			if (customBlock instanceof FunctionBlock) sign.setLine(0, b.getValues().getFunctionName());
			else {
				String fn = b.getValues().getFunctionName();
				String[] values = {"", "", "", ""};
				
				values[0] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
				values[1] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
				values[2] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
				values[3] = fn;
				
				sign.setLine(0, values[0]);
				sign.setLine(1, values[1]);
				sign.setLine(2, values[2]);
				sign.setLine(3, values[3]);
				
			}
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof MathBlock) {
			if (customBlock instanceof MathBaseBlock) return;
			
			if (customBlock instanceof SetBlock) {
				String var = b.getValues().getFunctionName().split("@")[0]; 
				String eq = "";
				try { eq = b.getValues().getFunctionName().split("@")[1]; } catch (ArrayIndexOutOfBoundsException e) {}
				
				//Set value block to Sign post
				valueBlock.setType(Material.SIGN_POST);
				valueBlock.setData((byte) 8);
				Sign sign = (Sign) valueBlock.getState();
				sign.setLine(0, var);
				sign.setLine(1, eq);
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
				
			} else if (customBlock instanceof EvaluateBlock) {
				//Set value block to Sign post
				valueBlock.setType(Material.SIGN_POST);
				valueBlock.setData((byte) 8);
				Sign sign = (Sign) valueBlock.getState();
				
				String eval = b.getValues().getFunctionName();
				String[] values = {"","","",""};
				eval = eval.replace("eval[", "").replace("]", "");
				values[0] = eval.substring(0, eval.indexOf(",")); eval = eval.substring(eval.indexOf(",")+1);
				values[1] = eval.substring(0, eval.indexOf(",")); eval = eval.substring(eval.indexOf(",")+1);
				values[2] = eval.substring(0, eval.indexOf(",")); eval = eval.substring(eval.indexOf(",")+1);
				values[3] = eval;
				
				sign.setLine(0, values[0]);
				sign.setLine(1, values[1]);
				sign.setLine(2, values[2]);
				sign.setLine(3, values[3]);
				sign.update();
				
			}
			
		} else if (customBlock instanceof BuildBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line values
			sign.setLine(0, b.getValues().getBuildLocation().toString());
			sign.setLine(1, b.getValues().getBuildBlockType().toString());
			sign.setLine(2, "");
			sign.setLine(3, b.getValues().getBuildBlockData()+"");
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof DestroyBlock || customBlock instanceof DigBlock ||
				 customBlock instanceof PickUpBlock || customBlock instanceof PlaceBlock || customBlock instanceof MoveBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line values
			sign.setLine(0, b.getValues().getInteractionDirection().toString());
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof AttackFarBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line values
			sign.setLine(0, b.getValues().getInteractionDirection().toString());
			sign.setLine(1, b.getValues().getTrueTargetType());
			
			//Update sign
			sign.update();
			
		} else if (customBlock instanceof AttackNearBlock) {
			//Set value block to Sign post
			valueBlock.setType(Material.SIGN_POST);
			valueBlock.setData((byte) 8);
			Sign sign = (Sign) valueBlock.getState();
			
			//Set line values
			sign.setLine(1, b.getValues().getTrueTargetType());
			
			//Update sign
			sign.update();
			
		}
		
	}
	
	//==========================================================================================
	public void clean(SpoutBlock block) {
		CodeBlock currentBlock;

		//Check if current block is valid code block
		while ((currentBlock = new CodeBlock(block)).isValid()) {
			//Remove current block
			block.setCustomBlock(null);
			block.setType(Material.AIR);
			SpoutBlock aboveBlock = (SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
			aboveBlock.setCustomBlock(null);
			aboveBlock.setType(Material.AIR);

			if (currentBlock.getCustomBlock() instanceof ForBlock) { //For branch
				clean((SpoutBlock) block.getLocation().add(block.getX(), block.getY(), block.getZ()-1).getBlock());

			} else if (currentBlock.getCustomBlock() instanceof IfBlock) { //If branches
				clean((SpoutBlock) block.getLocation().add(block.getX(), block.getY(), block.getZ()-1).getBlock());
				clean((SpoutBlock) block.getLocation().add(block.getX(), block.getY(), block.getZ()-2).getBlock());

			} else if (currentBlock.getCustomBlock() instanceof SwitchBlock) { //Switch branches
				//Parse all branches, have to start with case
				SpoutBlock b = (SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);

				//While next block down is CaseBlock parse each branch
				while (b.getCustomBlock() instanceof CaseBlock) {
					clean((SpoutBlock) b.getLocation().getBlock());
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);

				}

			} else if (currentBlock.getCustomBlock() instanceof WhileBlock) { //While branch
				//Parse branch
				clean((SpoutBlock) block.getLocation().add(block.getX(), block.getY(), block.getZ()-1).getBlock());

			}

			//Get next block
			block = (SpoutBlock) block.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ());

		}
		
	}
	
}
