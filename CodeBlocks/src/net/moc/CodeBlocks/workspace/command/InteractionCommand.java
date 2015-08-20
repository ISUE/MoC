package net.moc.CodeBlocks.workspace.command;

import org.bukkit.Material;

public class InteractionCommand extends Command {
	public enum Interaction {BUILD, DESTROY, DIG, PICKUP, PLACE, POWER}
	
	private Interaction interaction;
	private String robotSide;
	private Material material;
	private byte data;

	public InteractionCommand(String robotSide, Material material, byte data) {
		this.interaction = Interaction.BUILD;
		this.robotSide = robotSide;
		this.material = material;
		this.data = data;
		
	}

	public InteractionCommand(Interaction interaction, String robotSide) {
		this.interaction = interaction;
		this.robotSide = robotSide;
		this.material = Material.AIR;
		this.data = 0;
		
	}

	public Interaction getInteraction() { return interaction; }
	public String getRobotSide() { return robotSide; }
	public Material getMaterial() { return material; }
	public byte getData() { return data; }

}
