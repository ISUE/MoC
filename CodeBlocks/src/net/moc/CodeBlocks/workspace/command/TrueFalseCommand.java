package net.moc.CodeBlocks.workspace.command;

public class TrueFalseCommand extends Command {
	private String side;
	private String targetType;
	private String distance;
	private String data;
	
	public TrueFalseCommand(String targetType, String data, String side, String distance) {
		this.targetType = targetType;
		this.data = data;
		this.side = side;
		this.distance = distance;
		
	}

	public String getSide() { return side; }
	public String getTargetType() { return targetType; }
	public String getDistance() { return distance; }
	public String getData() { return data; }


}
