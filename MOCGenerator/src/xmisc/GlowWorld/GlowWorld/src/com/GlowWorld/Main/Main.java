package xmisc.GlowWorld.GlowWorld.src.com.GlowWorld.Main;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	public void onEnable() {}
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new xmisc.GlowWorld.GlowWorld.src.com.GlowWorld.ChunkGenerator.ChunkGenerator();
    }
	
}
