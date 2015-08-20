package net.moc.CodeBlocks.workspace.command;

public class AttackCommand extends Command {
	private String robotSide;
	private boolean isNear;
	private String targetType;

	public AttackCommand(String robotSide, String targetType, boolean isNear) {
		this.robotSide = robotSide;
		this.targetType = targetType;
		this.isNear = isNear;
		
	}

	public String getRobotSide() { return robotSide; }
	public String getTargetType() { return targetType; }
	public boolean isNear() { return isNear; }

}
