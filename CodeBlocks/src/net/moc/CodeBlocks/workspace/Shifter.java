package net.moc.CodeBlocks.workspace;

import net.moc.CodeBlocks.blocks.ProgramCodeBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;

public class Shifter {
	public Shifter() {}

	public void shiftLeft(SpoutBlock block) {
		SpoutBlock currentBlock = block;
		SpoutBlock currentBlockAbove = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ());
		
		Sign sign;
		String[] signLines = null;
		if (currentBlockAbove.getType() == Material.SIGN_POST) {
			signLines = ((Sign) currentBlockAbove.getState()).getLines();
		}
		
		SpoutBlock leftBlock = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX() + 1, currentBlock.getY(), currentBlock.getZ());
		SpoutBlock leftBlockAbove = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX() + 1, currentBlock.getY() + 1, currentBlock.getZ());
		
		while (currentBlock.getCustomBlock() instanceof ProgramCodeBlock && leftBlock.getType() == Material.AIR) {
			//Check for possible branches
			if (currentBlock.getCustomBlock() instanceof ForBlock) { //For branch
				shiftLeft((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				
			} else if (currentBlock.getCustomBlock() instanceof IfBlock) { //If branches
				shiftLeft((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				shiftLeft((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 2));
				
			} else if (currentBlock.getCustomBlock() instanceof SwitchBlock) { //Switch branches
				//Parse all branches, have to start with case
				SpoutBlock b = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1);
				
				//While next block down is CaseBlock parse each branch
				while (b.getCustomBlock() instanceof CaseBlock) {
					shiftLeft(b);
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
			} else if (currentBlock.getCustomBlock() instanceof WhileBlock) { //While branch
				//Parse branch
				shiftLeft((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				
			} else if (currentBlock.getCustomBlock() instanceof SetBlock) { //Equals branch
				//Parse all evals
				SpoutBlock b = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1);
				
				while (b.getCustomBlock() instanceof EvaluateBlock) {
					shiftLeft(b);
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
			}

			//Move current block left and set it to air in the old place
			leftBlock.setCustomBlock(currentBlock.getCustomBlock());
			leftBlockAbove.setType(currentBlockAbove.getType());
			leftBlockAbove.setData(currentBlockAbove.getData());
			if (signLines != null) {
				sign = (Sign) leftBlockAbove.getState();
				sign.setLine(0, signLines[0]);
				sign.setLine(1, signLines[1]);
				sign.setLine(2, signLines[2]);
				sign.setLine(3, signLines[3]);
				sign.update();
				
			}
			
			currentBlockAbove.setType(Material.AIR);
			currentBlock.setCustomBlock(null);
			currentBlock.setType(Material.AIR);
			
			//Move to the next block
			leftBlock = currentBlock;
			leftBlockAbove = currentBlockAbove;
			currentBlock = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX() - 1, currentBlock.getY(), currentBlock.getZ());
			currentBlockAbove = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ());
			
			signLines = null;
			if (currentBlockAbove.getType() == Material.SIGN_POST) { signLines = ((Sign) currentBlockAbove.getState()).getLines(); }
			
		}
		
	}

	public void shiftRight(SpoutBlock block) {
		SpoutBlock currentBlock = block, currentAboveBlock = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ());
		
		CustomBlock previousCustomBlock = null, tempCustomBlock;
		Material previousAboveMaterial = Material.AIR, tempAboveMaterial;
		byte previousAboveData = 0, tempAboveData;
		Sign sign;
		String[] previousAboveSignLines = null, tempAboveSignLines;
		
		while (currentBlock.getCustomBlock() instanceof ProgramCodeBlock) {
			//Save away currentBlock values
			tempCustomBlock = currentBlock.getCustomBlock();
			tempAboveMaterial = currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ()).getType();
			tempAboveData = currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ()).getData();
			tempAboveSignLines = null;
			if (tempAboveMaterial == Material.SIGN_POST) tempAboveSignLines = ((Sign) currentAboveBlock.getState()).getLines();
			
			//Check for possible branches
			if (currentBlock.getCustomBlock() instanceof ForBlock) { //For branch
				shiftRight((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				
			} else if (currentBlock.getCustomBlock() instanceof IfBlock) { //If branches
				shiftRight((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				shiftRight((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 2));
				
			} else if (currentBlock.getCustomBlock() instanceof SwitchBlock) { //Switch branches
				//Parse all branches, have to start with case
				SpoutBlock b = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1);
				
				//While next block down is CaseBlock parse each branch
				while (b.getCustomBlock() instanceof CaseBlock) {
					shiftRight(b);
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
			} else if (currentBlock.getCustomBlock() instanceof WhileBlock) { //While branch
				//Parse branch
				shiftRight((SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1));
				
			} else if (currentBlock.getCustomBlock() instanceof SetBlock) { //Equals branch
				SpoutBlock b = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ() - 1);
				while (b.getCustomBlock() instanceof EvaluateBlock) {
					shiftRight(b);
					b = (SpoutBlock) b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
					
				}
				
			}

			//Set current block to previous block's values
			currentAboveBlock.setType(previousAboveMaterial);
			currentAboveBlock.setData(previousAboveData);
			currentBlock.setCustomBlock(previousCustomBlock);
			if (previousCustomBlock == null) currentBlock.setType(Material.AIR);
			if (previousAboveSignLines != null) {
				sign = (Sign) currentAboveBlock.getState();
				sign.setLine(0, previousAboveSignLines[0]);
				sign.setLine(1, previousAboveSignLines[1]);
				sign.setLine(2, previousAboveSignLines[2]);
				sign.setLine(3, previousAboveSignLines[3]);
				sign.update();
			}
			
			//Put saved data into previous variables
			previousCustomBlock = tempCustomBlock;
			previousAboveMaterial = tempAboveMaterial;
			previousAboveData = tempAboveData;
			previousAboveSignLines = tempAboveSignLines;
			
			//Move to the next block
			currentBlock = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX() - 1, currentBlock.getY(), currentBlock.getZ());
			currentAboveBlock = (SpoutBlock) currentBlock.getWorld().getBlockAt(currentBlock.getX(), currentBlock.getY() + 1, currentBlock.getZ());
			
		}
		
		//Set last block
		currentBlock.setCustomBlock(previousCustomBlock);
		if (previousCustomBlock == null) currentBlock.setType(Material.AIR);
		currentAboveBlock.setType(previousAboveMaterial);
		currentAboveBlock.setData(previousAboveData);
		if (previousAboveSignLines != null) {
			sign = (Sign) currentAboveBlock.getState();
			sign.setLine(0, previousAboveSignLines[0]);
			sign.setLine(1, previousAboveSignLines[1]);
			sign.setLine(2, previousAboveSignLines[2]);
			sign.setLine(3, previousAboveSignLines[3]);
			sign.update();
		}
		
	}
	
}
