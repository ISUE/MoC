package net.moc.CodeBlocks.workspace.command;

public class JumpCommand extends Command {
	int location;
	
	public JumpCommand(int location) {this.location = location; }
	
	public int getLocation() { return this.location; }
	public void setLocation(int location) { this.location = location; }

}
