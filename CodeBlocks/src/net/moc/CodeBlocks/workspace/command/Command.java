package net.moc.CodeBlocks.workspace.command;

public abstract class Command {
	private int codeBlockNumber = 0;
	
	public int getCodeBlockNumber() { return codeBlockNumber; }
	public void setCodeBlockNumber(int number) { codeBlockNumber = number; }
	
}
