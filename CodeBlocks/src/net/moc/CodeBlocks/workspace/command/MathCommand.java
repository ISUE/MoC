package net.moc.CodeBlocks.workspace.command;

public class MathCommand extends Command {
	private String equation;
	private String variable;

	public MathCommand(String variable, String equation) { this.variable = variable; this.equation = equation; }
	
	public String getEquation() { return equation; }
	public String getVariable() { return variable; }

}
