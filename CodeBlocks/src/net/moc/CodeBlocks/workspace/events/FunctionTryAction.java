package net.moc.CodeBlocks.workspace.events;

import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.getspout.spoutapi.block.SpoutBlock;

public class FunctionTryAction {
	private BlockBreakEvent baseEvent;
	private int baseStatus = -1;
	private BlockBreakEvent valueEvent;
	private int valueStatus = -1;
	private SpoutBlock baseBlock;
	private Block valueBlock;
	private CodeBlock codeBlock;
	private Function function;
	
	public FunctionTryAction(Function function, BlockBreakEvent baseEvent, BlockBreakEvent valueEvent, SpoutBlock baseBlock, Block valueBlock, CodeBlock codeBlock) {
		this.function = function;
		this.baseEvent = baseEvent;
		this.valueEvent = valueEvent;
		this.baseBlock = baseBlock;
		this.valueBlock = valueBlock;
		this.codeBlock = codeBlock;
	}
	
	public Function getFunction() { return function; }
	
	public BlockBreakEvent getBaseEvent() { return baseEvent; }
	public BlockBreakEvent getValueEvent() { return valueEvent; }
	
	public SpoutBlock getBaseBlock() { return baseBlock; }
	public Block getValueBlock() { return valueBlock; }
	public CodeBlock getCodeBlock() { return codeBlock; }

	public int getBaseStatus() { return baseStatus; }
	public void setBaseStatus(int baseStatus) { this.baseStatus = baseStatus; }

	public int getValueStatus() { return valueStatus; }
	public void setValueStatus(int valueStatus) { this.valueStatus = valueStatus; }

}
