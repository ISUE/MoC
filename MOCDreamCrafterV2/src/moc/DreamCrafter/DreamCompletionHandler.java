package moc.DreamCrafter;

import java.io.File;
import moc.DreamCrafter.util.YAMLHelper;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Keeps track of the end points of each Dream (e.g. the blocks the player
 * interacts with to indicate completion of the Dream) When a player creates and
 * endpoint in build mode in the Origin World When a player interacts with an
 * endpoint in dream mode in the Dream World
 * 
 * YML paths endpoint.<worldName>.location.x endpoint.<worldName>.location.y
 * endpoint.<worldName>.location.z
 * 
 * @author Chris
 * 
 */
public class DreamCompletionHandler {
	
	private MOCDreamCrafter plugin;

	/**
	 * The YML file stored in memory. We want to keep it in memory to avoid
	 * reading the file every time a player interacts with the block because
	 * that occurs frequently.
	 */
	private YamlConfiguration yaml;

	/** The name of the yml file */
	private final String ymlFileName = "dreamEndPoints.yml";

	public DreamCompletionHandler(MOCDreamCrafter plugin) { this.plugin = plugin; }

	/**
	 * Sets the location of the endpoint of the origin world to the specified
	 * location
	 * 
	 * @param originWorldName
	 *            The name of the origin world
	 * @param location
	 *            The location of the block that is to become an endpoint
	 */
	public void createEndPoint(String originWorldName, Location location) {
		if (yaml == null) {
			plugin.getLog().info("DreamCompletionHandler not enabled by plugin");
			return;
		}

		yaml.set("endpoint." + originWorldName + ".location.x",
				location.getBlockX());
		yaml.set("endpoint." + originWorldName + ".location.y",
				location.getBlockY());
		yaml.set("endpoint." + originWorldName + ".location.z",
				location.getBlockZ());

		saveYAML();
	}

	/**
	 * Returns whether the specified location in the dream world is the endpoint
	 * of that dream world
	 * 
	 * @param dreamWorldName
	 *            The name of the dream world
	 * @param location
	 *            The location of the block to be tested
	 * @return
	 */
	public boolean isBlockEndPoint(String worldName, Location location) {
		if (yaml == null) {
			plugin.getLog().info("DreamCompletionHandler not enabled by plugin");
			return false;
		}

		// Is the location set for the world?
		if (yaml.get("endpoint." + worldName + ".location") == null)
			return false;

		int x = yaml.getInt("endpoint." + worldName + ".location.x");
		int y = yaml.getInt("endpoint." + worldName + ".location.y");
		int z = yaml.getInt("endpoint." + worldName + ".location.z");

		// Do the coordinates match?
		return x == location.getBlockX() && y == location.getBlockY()
				&& z == location.getBlockZ();

	}

	/**
	 * Copy the endpoint from the originWorld to the dreamWorld
	 * 
	 * @param originWorldName
	 * @param dreamWorldName
	 */
	public void onDreamStart(String originWorldName, String dreamWorldName) {
		if (yaml == null) {
			plugin.getLog().info("DreamCompletionHandler not enabled by plugin");
			return;
		}

		// If there is no endpoint for this world
		if (yaml.get("endpoint." + originWorldName + ".location") == null)
			return;

		// Get the coords from the originworld
		int x = yaml.getInt("endpoint." + originWorldName + ".location.x");
		int y = yaml.getInt("endpoint." + originWorldName + ".location.y");
		int z = yaml.getInt("endpoint." + originWorldName + ".location.z");

		// Save them under the dream world
		yaml.set("endpoint." + dreamWorldName + ".location.x", x);
		yaml.set("endpoint." + dreamWorldName + ".location.y", y);
		yaml.set("endpoint." + dreamWorldName + ".location.z", z);
		saveYAML();
	}

	/**
	 * Load the end points from the YML file. Called by the owning plugin when
	 * the plugin is enabled
	 */
	public void onPluginEnable() {
		File file = new File(this.ymlFileName);

		if (file.exists())
			this.yaml = YAMLHelper.loadYAML(file);
		else
			this.yaml = new YamlConfiguration();
	}

	/**
	 * Save the end points to file. Called by the owning plugin when the plugin
	 * is disabled
	 */
	public void onPluginDisable() {
		saveYAML();
	}

	private void saveYAML() {
		YAMLHelper.saveYaml(yaml, new File(this.ymlFileName));
	}
	
	
	
	
	
	
	
	
	
	
	/** For local testing
	 */
	public static void main(String[] args) {
		DreamCompletionHandler dch = new DreamCompletionHandler(null);
		dch.onPluginEnable();
		dch.createEndPoint("TestOrigin", new Location(null, 3.0,4.0,5.0));
		dch.createEndPoint("TestOrigin2", new Location(null, 31.0,14.0,53.0));
		dch.onDreamStart("TestOrigin", "TestOrigin_User1");
		System.out.println(dch.isBlockEndPoint("TestOrigin_User1", new Location(null, 31.0,14.0,53.0)));
		System.out.println(dch.isBlockEndPoint("TestOrigin_User1", new Location(null, 3.0, 4.0, 5.0)));
		dch.onPluginDisable();
	}
}
