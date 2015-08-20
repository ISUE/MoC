package net.moc.CodeBlocks.workspace.command;

public class FunctionCallCommand extends Command {
	private String functionName;
	
	public FunctionCallCommand(String functionName) { this.functionName = functionName; }
	
	public String getFunctionName() { return this.functionName; }

}
