package net.moc.MOCDreamCatcher.Data;

import org.bukkit.Location;

public class Dreamer {
	private DreamPlayer dreamPlayer;
	private Thought thought;
	private Location endLocation;
	
	public Dreamer(DreamPlayer dreamPlayer, Thought thought) { this.dreamPlayer = dreamPlayer; this.thought = thought; this.endLocation = thought.getEnd(); }
	
	public DreamPlayer getDreamPlayer() { return dreamPlayer; }
	public Location getEndLocation() { return endLocation; }
	public Thought getThought() { return thought; }

}
