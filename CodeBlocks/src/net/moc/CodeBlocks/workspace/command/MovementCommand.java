package net.moc.CodeBlocks.workspace.command;

public class MovementCommand extends Command {
	public enum Direction {BACK, DOWN, FORWARD, LEFT, RIGHT, UP, TURN_LEFT, TURN_RIGHT}
	
	private Direction direction;
	private String variableDirection = null;
	
	public MovementCommand(Direction direction) { this.direction = direction; }
	
	public MovementCommand(String variableDirection) { this.variableDirection = variableDirection; }

	public Direction getDirection() { return direction; }
	public String getVariableDirection() { return variableDirection; }

}
