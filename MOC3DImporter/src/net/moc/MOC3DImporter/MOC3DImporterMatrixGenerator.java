package net.moc.MOC3DImporter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.obj.Face;
import com.obj.Group;
import com.obj.TextureCoordinate;
import com.obj.Vertex;
import com.obj.WavefrontObject;

public class MOC3DImporterMatrixGenerator implements Runnable {
	//--------------------------------------------------------------------
	private MOC3DImporter plugin;
	private File objectFile;
	private WavefrontObject wfo;
	private Player player;
	private float top;
	private float bottom;
	private int[][][] matrix;
	private Location location;
	private float step;
	private boolean useTexture;
	
	public MOC3DImporterMatrixGenerator(MOC3DImporter plugin, File objectFile, Player player, Location location, float top, float bottom, boolean useTexture) {
		this.plugin = plugin;
		this.step = this.plugin.getConfiguration().getStep();
		this.player = player;
		this.top = top;
		this.bottom = bottom;
		this.location = location;
		this.useTexture = useTexture;
		
		this.objectFile = objectFile;
		this.wfo = new WavefrontObject(objectFile.getAbsolutePath());
		
	}
	//--------------------------------------------------------------------

	public void run() {
		//Figure out frame
		float maxX = -1000000000, minX = 1000000000;
		float maxY = -1000000000, minY = 1000000000;
		float maxZ = -1000000000, minZ = 1000000000;
		for (Vertex vertex : wfo.getVertices()) {
			float x = vertex.getX();
			float y = vertex.getY();
			float z = vertex.getZ();
			
			if (x > maxX) maxX = x;
			if (x < minX) minX = x;
			
			if (y > maxY) maxY = y;
			if (y < minY) minY = y;
			
			if (z > maxZ) maxZ = z;
			if (z < minZ) minZ = z;
			
		}
		
		//Scale of the frame versus top bottom supplied
		float scale = (top - bottom) / (maxY - minY);
		
		this.plugin.getLog().info("Starting Matrix Generation - top " + top + " bottom " + bottom + " scale " + scale);
		
		//Shift and scale all vertices
		for (Vertex vertex : wfo.getVertices()) {
			if (vertex == null) continue;
			
			vertex.setX((vertex.getX() - minX) * scale);
			vertex.setY((vertex.getY() - minY) * scale);
			vertex.setZ((vertex.getZ() - minZ) * scale);
			
		}
		
		//Create matrix to hold the generated values
		this.matrix = new int[Math.round((maxX - minX) * scale) + 2][Math.round((maxY - minY) * scale) + 2][Math.round((maxZ - minZ) * scale) + 2];
		this.plugin.getLog().info("MATRIX " + matrix.length + " " + matrix[0].length + " " + matrix[0][0].length);
		//Set everything to -1
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix[0].length ; j++) {
				for (int k = 0 ; k < matrix[0][0].length ; k++) {
					matrix[i][j][k] = -1;
				}
				
			}
			
		}
		
		//Record all vertices as valid blocks
		for (Vertex vertex : wfo.getVertices()) { if (vertex == null) continue; matrix[(int) vertex.getX()][(int) vertex.getY()][(int) vertex.getZ()] = 1; }
		
		//Fill up the matrix
		for (Group group : wfo.getGroups()) {
			for (Face face : group.getFaces()) {
				int block;
				BufferedImage texture = null;
				TextureCoordinate textureC[] = new TextureCoordinate[4];
				if (face.getMaterial() != null) {
					if (face.getMaterial().getMap() != null) {
						//Have texture
						texture = face.getMaterial().getMap();
						textureC = face.getTextures();
						block = -1;
					} else {
						//Just a single RGB value
						if (face.getMaterial().getKd() != null) {
							block = findMatchingBlock(new Color(face.getMaterial().getKd().getX(), face.getMaterial().getKd().getY(), face.getMaterial().getKd().getZ()));
							
						} else {
							block = this.plugin.getConfiguration().getBlockTypeId() + 1000 * this.plugin.getConfiguration().getBlockData();
							
						}
						
					}
					
				} else {
					//Otherwise use default block
					block = this.plugin.getConfiguration().getBlockTypeId() + 1000 * this.plugin.getConfiguration().getBlockData();
					
				}
				
				Vertex vertex[] = face.getVertices();
				for (int i = 2 ; i < vertex.length ; i++) {
					try { processFace(vertex[0], vertex[i-1], vertex[i], block, texture, textureC[0], textureC[i-1], textureC[i]); }
					catch (ArrayIndexOutOfBoundsException e) {this.plugin.getLog().info("Vertex length - " + vertex.length + "/" + textureC.length + " i=" + i); }
				}

			}
			
		}
		
		//Throw an event that the matrix is done
		MOC3DImporterMatrixFinishedEvent event = new MOC3DImporterMatrixFinishedEvent(this.player, this.objectFile.getName(), this.location, this.matrix, this.useTexture);
	    Bukkit.getServer().getPluginManager().callEvent(event);
	    
	}
	
	private int findMatchingBlock(Color color) {
		MOC3DImporterColorBlock block = null;
		int colorDelta = 99999999;
		for (MOC3DImporterColorBlock b : this.plugin.getPalette()) {
			int cd = Math.abs(b.getColor().getRed() - color.getRed()) +
						Math.abs(b.getColor().getGreen() - color.getGreen()) +
						Math.abs(b.getColor().getBlue() - color.getBlue());
			
			if (colorDelta > cd) {
				colorDelta = cd;
				block = b;
				
			}
			
		}
		
		if (block == null) return this.plugin.getConfiguration().getBlockTypeId() + 1000 * this.plugin.getConfiguration().getBlockData();
		return block.getTypeId() + 1000 * block.getData();
		
	}
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	private void processFace(Vertex v1, Vertex v2, Vertex v3, int block, BufferedImage texture, TextureCoordinate textureC1, TextureCoordinate textureC2, TextureCoordinate textureC3) {
		Matrix rotationMatrix = null;
		
		if (texture != null) {
			Matrix vM = new Matrix(3);
			Matrix vtM = new Matrix(3);
			
			vM.set(0, 0, v1.getX());
			vM.set(1, 0, v1.getY());
			vM.set(2, 0, v1.getZ());
			
			vM.set(0, 1, v2.getX());
			vM.set(1, 1, v2.getY());
			vM.set(2, 1, v2.getZ());
			
			vM.set(0, 2, v3.getX());
			vM.set(1, 2, v3.getY());
			vM.set(2, 2, v3.getZ());
			
			vtM.set(0, 0, textureC1.getU() * texture.getWidth());
			vtM.set(1, 0, textureC1.getV() * texture.getHeight());
			vtM.set(2, 0, 0);
			
			vtM.set(0, 1, textureC2.getU() * texture.getWidth());
			vtM.set(1, 1, textureC2.getV() * texture.getHeight());
			vtM.set(2, 1, 0);
			
			vtM.set(0, 2, textureC3.getU() * texture.getWidth());
			vtM.set(1, 2, textureC3.getV() * texture.getHeight());
			vtM.set(2, 2, 0);
			
			//Inverse and multiply to generate final maxtrix
			rotationMatrix = vtM.mul(vM.inverse());

		}
		
		
		//A
		float x1 = v1.getX();
		float y1 = v1.getY();
		float z1 = v1.getZ();
		
		//B
		float x2 = v2.getX();
		float y2 = v2.getY();
		float z2 = v2.getZ();
		
		//C
		float x3 = v3.getX();
		float y3 = v3.getY();
		float z3 = v3.getZ();
		
		//Calculate deltas for each line
		float dxAB = Math.abs(x1 - x2);
		float dyAB = Math.abs(y1 - y2);
		float dzAB = Math.abs(z1 - z2);
		
		float dxAC = Math.abs(x1 - x3);
		float dyAC = Math.abs(y1 - y3);
		float dzAC = Math.abs(z1 - z3);
		
		//Line 1 - AB
		float x1x2 = x2 - x1;
		float y1y2 = y2 - y1;
		float z1z2 = z2 - z1;
		
		//Line 2 - AC
		float x1x3 = x3 - x1;
		float y1y3 = y3 - y1;
		float z1z3 = z3 - z1;
		
		if (dxAB >= dyAB &&
			dxAB >= dzAB &&
			dxAC >= dyAC &&
			dxAC >= dzAC) {
			//dxAB dxAC
			//Figure out increment sign
			float incrementAB = x1 < x2 ? -step : step;
			float incrementAC = x1 < x3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) {
				startAB = x2;
				endAB = x1;
			
			} else {
				startAB = x2;
				endAB = x1;
				
			}
			
			if (incrementAC > 0) {
				startAC = x3;
				endAC = x1;
			
			} else {
				startAC = x3;
				endAC = x1;
				
			}
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - x1) / x1x2;
				float t3 = (j - x1) / x1x3;
				
				float yt2 = y1 + y1y2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float yt3 = y1 + y1y3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(i, yt2, zt2, j, yt3, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}
			
		} else if (dxAB >= dyAB &&
				   dxAB >= dzAB &&
				   dyAC >= dxAC &&
				   dyAC >= dzAC) {
			//dxAB dyAC
			//Figure out increment sign
			float incrementAB = x1 < x2 ? -step : step;
			float incrementAC = y1 < y3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) {
				startAB = x2;
				endAB = x1;
			
			} else {
				startAB = x2;
				endAB = x1;
				
			}
			
			if (incrementAC > 0) {
				startAC = y3;
				endAC = y1;
			
			} else {
				startAC = y3;
				endAC = y1;
				
			}

			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - x1) / x1x2;
				float t3 = (j - y1) / y1y3;
				
				float yt2 = y1 + y1y2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(i, yt2, zt2, xt3, j, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}
			
		} else if (dxAB >= dyAB &&
				   dxAB >= dzAB &&
				   dzAC >= dxAC &&
				   dzAC >= dyAC) {
			//dxAB dzAC
			//Figure out increment sign
			float incrementAB = x1 < x2 ? -step : step;
			float incrementAC = z1 < z3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = x2; endAB = x1; }
			else { startAB = x2; endAB = x1; }
			
			if (incrementAC > 0) { startAC = z3; endAC = z1; }
			else { startAC = z3; endAC = z1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - x1) / x1x2;
				float t3 = (j - z1) / z1z3;
				
				float yt2 = y1 + y1y2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float yt3 = y1 + y1y3 * t3;
				
				//Generate voxels for the line BC
				generateLine(i, yt2, zt2, xt3, yt3, j, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}
			
		} else if (dyAB >= dxAB &&
				   dyAB >= dzAB &&
				   dxAC >= dyAC &&
				   dxAC >= dzAC) {
			//dyAB dxAC
			//Figure out increment sign
			float incrementAB = y1 < y2 ? -step : step;
			float incrementAC = x1 < x3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = y2; endAB = y1; }
			else { startAB = y2; endAB = y1; }
			
			if (incrementAC > 0) { startAC = x3; endAC = x1; }
			else { startAC = x3; endAC = x1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - y1) / y1y2;
				float t3 = (j - x1) / x1x3;
				
				float xt2 = x1 + x1x2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float yt3 = y1 + y1y3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, i, zt2, j, yt3, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		} else if (dyAB >= dxAB &&
				   dyAB >= dzAB &&
				   dyAC >= dxAC &&
				   dyAC >= dzAC) {
			//dyAB dyAC
			//Figure out increment sign
			float incrementAB = y1 < y2 ? -step : step;
			float incrementAC = y1 < y3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = y2; endAB = y1; }
			else { startAB = y2; endAB = y1; }
			
			if (incrementAC > 0) { startAC = y3; endAC = y1; }
			else { startAC = y3; endAC = y1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - y1) / y1y2;
				float t3 = (j - y1) / y1y3;
				
				float xt2 = x1 + x1x2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, i, zt2, xt3, j, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		} else if (dyAB >= dxAB &&
				   dyAB >= dzAB &&
				   dzAC >= dxAC &&
				   dzAC >= dyAC) {
			//dyAB dzAC
			//Figure out increment sign
			float incrementAB = y1 < y2 ? -step : step;
			float incrementAC = z1 < z3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = y2; endAB = y1; }
			else { startAB = y2; endAB = y1; }
			
			if (incrementAC > 0) { startAC = z3; endAC = z1; }
			else { startAC = z3; endAC = z1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - y1) / y1y2;
				float t3 = (j - z1) / z1z3;
				
				float xt2 = x1 + x1x2 * t2;
				float zt2 = z1 + z1z2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float yt3 = y1 + y1y3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, i, zt2, xt3, yt3, j, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		} else if (dzAB >= dxAB &&
				   dzAB >= dyAB &&
				   dxAC >= dyAC &&
				   dxAC >= dzAC) {
			//dzAB dxAC
			//Figure out increment sign
			float incrementAB = z1 < z2 ? -step : step;
			float incrementAC = x1 < x3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = z2; endAB = z1; }
			else { startAB = z2; endAB = z1; }
			
			if (incrementAC > 0) { startAC = x3; endAC = x1; }
			else { startAC = x3; endAC = x1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - z1) / z1z2;
				float t3 = (j - x1) / x1x3;
				
				float xt2 = x1 + x1x2 * t2;
				float yt2 = y1 + y1y2 * t2;
				
				float yt3 = y1 + y1y3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, yt2, i, j, yt3, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		} else if (dzAB >= dxAB &&
				   dzAB >= dyAB &&
				   dyAC >= dxAC &&
				   dyAC >= dzAC) {
			//dzAB dyAC
			//Figure out increment sign
			float incrementAB = z1 < z2 ? -step : step;
			float incrementAC = y1 < y3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = z2; endAB = z1; }
			else { startAB = z2; endAB = z1; }
			
			if (incrementAC > 0) { startAC = y3; endAC = y1; }
			else { startAC = y3; endAC = y1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - z1) / z1z2;
				float t3 = (j - y1) / y1y3;
				
				float xt2 = x1 + x1x2 * t2;
				float yt2 = y1 + y1y2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float zt3 = z1 + z1z3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, yt2, i, xt3, j, zt3, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		} else { //if (dzAB > dxAB && dzAB > dyAB && dzAC > dxAC && dzAC > dyAC) {
			//dzAB dzAC
			//Figure out increment sign
			float incrementAB = z1 < z2 ? -step : step;
			float incrementAC = z1 < z3 ? -step : step;
			
			float startAB, startAC, endAB, endAC;
			
			if (incrementAB > 0) { startAB = z2; endAB = z1; }
			else { startAB = z2; endAB = z1; }
			
			if (incrementAC > 0) { startAC = z3; endAC = z1; }
			else { startAC = z3; endAC = z1; }
			
			//Generate new lines parallel to BC until A is reached
			for (float i = startAB, j = startAC ; Math.abs(i - endAB) > step && Math.abs(j - endAC) > step ; ) {
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				
				//Calculate t
				float t2 = (i - z1) / z1z2;
				float t3 = (j - z1) / z1z3;
				
				float xt2 = x1 + x1x2 * t2;
				float yt2 = y1 + y1y2 * t2;
				
				float xt3 = x1 + x1x3 * t3;
				float yt3 = y1 + y1y3 * t3;
				
				//Generate voxels for the line BC
				generateLine(xt2, yt2, i, xt3, yt3, j, block, texture, rotationMatrix);
				
				//Increment
				i += incrementAB;
				j += incrementAC;
				
			}

		}

	}
	
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	private void generateLine(float x1, float y1, float z1, float x2, float y2, float z2, int block, BufferedImage texture, Matrix rotationMatrix) {
		float dx = Math.abs(x1 - x2);
		float dy = Math.abs(y1 - y2);
		float dz = Math.abs(z1 - z2);
		
		if (dx > dy && dx > dz) {
			//dx is biggest
			//Figure out start finish for the X
			float start, finish;
			if (x1 > x2) { start = x2; finish = x1; }
			else { start = x1; finish = x2; }
			
			//BC calculations
			float x2x1 = x2 - x1;
			float y2y1 = y2 - y1;
			float z2z1 = z2 - z1;
			
			//Run through BC line generating voxels in the matrix
			for (float i = start ; i <= finish ; i += step) {
				//Figure out matching Y and Z
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				float t = (i - x1) / x2x1;
				float yt = y1 + y2y1 * t;
				float zt = z1 + z2z1 * t;
				
				int x = (int) i;
				int y = (int) yt;
				int z = (int) zt;

				if (x < 0) x = 0;
				if (y < 0) y = 0;
				if (z < 0) z = 0;
				
				if (texture != null) {
					Matrix curPoint = new Matrix(3, 1);
					curPoint.set(0, 0, x);
					curPoint.set(1, 0, y);
					curPoint.set(2, 0, z);
					curPoint = rotationMatrix.mul(curPoint);
					
					float xtex = curPoint.get(0, 0);
					float ytex = curPoint.get(1, 0);
					
					if (xtex > texture.getWidth()-1) xtex = texture.getWidth()-1;
					if (xtex < 0) xtex = 0;
					if (ytex > texture.getHeight()-1) ytex = texture.getHeight()-1;
					if (ytex < 0) ytex = 0;
					
					try { block = this.findMatchingBlock(new Color(texture.getRGB((int) xtex, (int) ytex))); }
					catch (ArrayIndexOutOfBoundsException e) { debug("X/Y " + xtex + "/" + ytex + " [" + (int)xtex + "/" + (int)ytex + "]"); }
					
				}
				
				//Add it to matrix
				try { this.matrix[x][y][z] = block; } catch (ArrayIndexOutOfBoundsException e) { this.plugin.getLog().warn("i" + x + ", yt" + y + ", zt" + z + " [ " + this.matrix.length + " " + this.matrix[0].length + " " + this.matrix[0][0].length); } 
				
			}
			
		} else if (dy > dx && dy > dz) {
			//dy is biggest
			//Figure out start finish for the Y
			float start, finish;
			if (y1 > y2) { start = y2; finish = y1; }
			else { start = y1; finish = y2; }
			
			//BC calculations
			float x2x1 = x2 - x1;
			float y2y1 = y2 - y1;
			float z2z1 = z2 - z1;
			
			//Run through BC line generating voxels in the matrix
			for (float i = start ; i <= finish ; i += step) {
				//Figure out matching X and Z
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				float t = (i - y1) / y2y1;
				float xt = x1 + x2x1 * t;
				float zt = z1 + z2z1 * t;
				
				int x = (int) xt;
				int y = (int) i;
				int z = (int) zt;

				if (x < 0) x = 0;
				if (y < 0) y = 0;
				if (z < 0) z = 0;

				if (texture != null) {
					Matrix curPoint = new Matrix(3, 1);
					curPoint.set(0, 0, x);
					curPoint.set(1, 0, y);
					curPoint.set(2, 0, z);
					curPoint = rotationMatrix.mul(curPoint);
					
					float xtex = curPoint.get(0, 0);
					float ytex = curPoint.get(1, 0);
					
					if (xtex > texture.getWidth()-1) xtex = texture.getWidth()-1;
					if (xtex < 0) xtex = 0;
					if (ytex > texture.getHeight()-1) ytex = texture.getHeight()-1;
					if (ytex < 0) ytex = 0;
					
					try { block = this.findMatchingBlock(new Color(texture.getRGB((int) xtex, (int) ytex))); }
					catch (ArrayIndexOutOfBoundsException e) { debug("X/Y " + xtex + "/" + ytex + " [" + (int)xtex + "/" + (int)ytex + "]"); }
					
				}
				
				//Add it to matrix
				try { this.matrix[x][y][z] = block; } catch (ArrayIndexOutOfBoundsException e) { this.plugin.getLog().warn("xt" + x + ", i" + y + ", zt" + z + " [ " + matrix.length + " " + matrix[0].length + " " + matrix[0][0].length); } 
				
			}
			
		} else {
			//dz is biggest
			//Figure out start finish for the Z
			float start, finish;
			if (z1 > z2) { start = z2; finish = z1; }
			else { start = z1; finish = z2; }
			
			//BC calculations
			float x2x1 = x2 - x1;
			float y2y1 = y2 - y1;
			float z2z1 = z2 - z1;
			
			//Run through BC line generating voxels in the matrix
			for (float i = start ; i <= finish ; i += step) {
				//Figure out matching X and Z
				// x-x1/x2-x1 = y-y1/y2-y1 = z-z1/z2-z1
				// x = x1 + (x2 - x1)*t
				// y = y1 + (y2 - y1)*t
				// z = z1 + (z2 - z1)*t
				float t = (i - z1) / z2z1;
				float xt = x1 + x2x1 * t;
				float yt = y1 + y2y1 * t;
				
				int x = (int) xt;
				int y = (int) yt;
				int z = (int) i;
				
				if (x < 0) x = 0;
				if (y < 0) y = 0;
				if (z < 0) z = 0;
				
				if (texture != null) {
					Matrix curPoint = new Matrix(3, 1);
					curPoint.set(0, 0, x);
					curPoint.set(1, 0, y);
					curPoint.set(2, 0, z);
					curPoint = rotationMatrix.mul(curPoint);
					
					float xtex = curPoint.get(0, 0);
					float ytex = curPoint.get(1, 0);
					
					if (xtex > texture.getWidth()-1) xtex = texture.getWidth()-1;
					if (xtex < 0) xtex = 0;
					if (ytex > texture.getHeight()-1) ytex = texture.getHeight()-1;
					if (ytex < 0) ytex = 0;
					
					try { block = this.findMatchingBlock(new Color(texture.getRGB((int) xtex, (int) ytex))); }
					catch (ArrayIndexOutOfBoundsException e) { debug("X/Y " + xtex + "/" + ytex + " [" + (int)xtex + "/" + (int)ytex + "]"); }
					
				}
				
				//Add it to matrix
				try { this.matrix[x][y][z] = block; } catch (ArrayIndexOutOfBoundsException e) { this.plugin.getLog().warn("xt" + x + ", yt" + y + ", i" + z + " [ " + matrix.length + " " + matrix[0].length + " " + matrix[0][0].length); } 
				
			}
			
		}

	}
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	private void debug(String message) { plugin.getLog().warn("##################################################> " + message + " []" + this); }
	
}
