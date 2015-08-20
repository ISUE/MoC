package net.moc.CodeBlocks.workspace.events;

import net.moc.CodeBlocks.workspace.Robotnik;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
import net.moc.CodeBlocks.workspace.command.InteractionCommand.Interaction;

import org.bukkit.Location;
import org.bukkit.Material;

public class RobotnikAction {
	private Robotnik robotnik;
	private Interaction interaction;
	private RobotSide side;
	private Material material;
	private Byte data;
	private Location location;

	public RobotnikAction(Robotnik robotnik, Interaction interaction, RobotSide side, Material material, Byte data) {
		this.robotnik = robotnik;
		this.interaction = interaction;
		this.side = side;
		this.material = material;
		this.data = data;
		
	}

	public RobotnikAction(Robotnik robotnik, Location location) {
		this.robotnik = robotnik;
		this.location = location;
		this.interaction = null;
		
	}

	public Location getLocation() { return location; }
	public Robotnik getRobotnik() { return robotnik; }
	public Interaction getInteraction() { return interaction; }
	public RobotSide getSide() { return side; }
	public Material getMaterial() { return material; }
	public Byte getData() { return data; }
	
}
