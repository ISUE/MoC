package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import org.bukkit.Location;
import org.getspout.spoutapi.block.SpoutBlock;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;

public class Parser {
	public Parser(CodeBlocks plugin) {}
	
	//x decreasing for main
	//z decreasing for branches
	public ArrayList<CodeBlock> parse(SpoutBlock block, Location baseLocation) {
		ArrayList<CodeBlock> blocks = new ArrayList<CodeBlock>();
		CodeBlock currentBlock;
		
		//Check if current block is valid code block
		while ((currentBlock = new CodeBlock(block)).isValid()) {
			//Add it to the function
			currentBlock.setOffsetX(block.getX() - baseLocation.getBlockX());
			currentBlock.setOffsetY(block.getY() - baseLocation.getBlockY());
			currentBlock.setOffsetZ(block.getZ() - baseLocation.getBlockZ());
			blocks.add(currentBlock);
			
			if (currentBlock.getCustomBlock() instanceof ForBlock) { //For branch
				blocks.addAll(parse((SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1), baseLocation));
				
			} else if (currentBlock.getCustomBlock() instanceof IfBlock) { //If branches
				blocks.addAll(parse((SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1), baseLocation));
				blocks.addAll(parse((SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 2), baseLocation));
				
			} else if (currentBlock.getCustomBlock() instanceof SwitchBlock) { //Switch branches
				//Parse all branches, have to start with case
				SpoutBlock b = (SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
				
				//While next block down is CaseBlock parse each branch
				while (b.getCustomBlock() instanceof CaseBlock) {
					blocks.addAll(parse(b, baseLocation));
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
			} else if (currentBlock.getCustomBlock() instanceof WhileBlock) { //While branch
				//Parse branch
				blocks.addAll(parse((SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1), baseLocation));
				
			} else if (currentBlock.getCustomBlock() instanceof SetBlock) { //Math branch
				//Parse all eval blocks
				SpoutBlock b = (SpoutBlock) block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
				
				//While next block down is CaseBlock parse each branch
				while (b.getCustomBlock() instanceof EvaluateBlock && (currentBlock = new CodeBlock(b)).isValid()) {
					//Add it to the function
					currentBlock.setOffsetX(b.getX() - baseLocation.getBlockX());
					currentBlock.setOffsetY(b.getY() - baseLocation.getBlockY());
					currentBlock.setOffsetZ(b.getZ() - baseLocation.getBlockZ());
					blocks.add(currentBlock);

					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
				//End of a block
				blocks.add(null);
				
			}
			
			//Get next block
			block = (SpoutBlock) block.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ());
			
		}
		
		//End of a block, add null to signify it
		blocks.add(null);
		
		return blocks;
		
	}

}