package net.moc.CodeBlocks.workspace.command;

public class ForCommand extends Command {
	private String count;
	
	public ForCommand(String count) { this.count = count; }
	
	public String getCount() { return count; }

}
