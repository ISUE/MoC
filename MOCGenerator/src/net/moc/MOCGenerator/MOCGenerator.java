package net.moc.MOCGenerator;

//import java.io.File;
//import org.bukkit.plugin.PluginManager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class MOCGenerator extends JavaPlugin {
	//=============================================================================================
	private MOCGeneratorChunkGenerator chunkGenerator;
	//=============================================================================================

	
	
	//=============================================================================================
	public void onEnable() {
		chunkGenerator = new MOCGeneratorChunkGenerator();
    	
	}
	public void onDisable() {}
	//=============================================================================================

	
	
	//=============================================================================================
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		System.out.println("Request for default world generator: world name - " + worldName + ", id - " + id);
		
		return chunkGenerator;
		
	}
	//=============================================================================================
}
