package net.moc.MOCGenerator;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class MOCGeneratorChunkGenerator extends ChunkGenerator {
	//---------------------------------------------------
	//---------------------------------------------------
	
	//---------------------------------------------------
	public MOCGeneratorChunkGenerator () {
	}
	//---------------------------------------------------

	//---------------------------------------------------
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomes) {
		byte[][] result = new byte[16][];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int y = (int) Math.abs((1 /(Math.sin(Math.abs(x) + x) - Math.cos(Math.abs(z) + z))) % 10) + 60;
				setBlock(result, x, y, z, (byte) Material.GRASS.getId());
				
				for (int i = 0 ; i < y ; i++) {
					setBlock(result, x, i, z, (byte) Material.STONE.getId());
					
				}
			}
		}
		
		return result;
		
	}
	//---------------------------------------------------
	
	
	
	//---------------------------------------------------
    private void setBlock(byte[][] result, int x, int y, int z, byte blockId) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blockId;
    }
    
    @SuppressWarnings("unused")
	private byte getBlock(byte[][] result, int x, int y, int z) {
        if (result[y >> 4] == null) {
            return (byte)0;
        }
        return result[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
    }
	//---------------------------------------------------
}
