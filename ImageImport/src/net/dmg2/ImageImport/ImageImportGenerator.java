package net.dmg2.ImageImport;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ImageImportGenerator {

	//============================================================
	private ImageImport plugin;
	public ImageImportGenerator(ImageImport plugin) { this.plugin = plugin; }
	//============================================================
	
	private TreeMap<String, ImageImportMatrix> imageMatrixQueue = new TreeMap<String, ImageImportMatrix>();
	public boolean playerInQueue(String playerName) { return this.imageMatrixQueue.containsKey(playerName); }
	
	private boolean busy = false;
	private long lastQueueCall = System.currentTimeMillis();
	private long queueDelay = 5000;
	private int maxNumberOfBlocksPerQueue = 1000;
	
	//============================================================
	public void processQueue() {
		if (busy) return;
		busy = true;
		
		//Check if anything to build
		if (this.imageMatrixQueue.isEmpty()) { busy = false; return; }
		
		//Check last queue call
		if (System.currentTimeMillis() - this.lastQueueCall  < this.queueDelay) { busy = false; return; }
		this.lastQueueCall = System.currentTimeMillis();
		
		//Get first iiMatrix in queue
		ImageImportMatrix iiMatrix = this.imageMatrixQueue.get(this.imageMatrixQueue.firstKey());
		
		//Get last i,j values
		int i = iiMatrix.getX();
		int j = iiMatrix.getY();
		
		//Get location
		Location origin = iiMatrix.getLocation();
		World world = origin.getWorld();
		int x = origin.getBlockX();
		int y = origin.getBlockY();
		int z = origin.getBlockZ();
		//Get direction
		int xd = iiMatrix.getXd();
		int zd = iiMatrix.getZd();
		
		//Get matrix
		int[][] matrix = iiMatrix.getMatrix();
		
		//Set blocks
		int blockCounter = this.maxNumberOfBlocksPerQueue;
		ArrayList<ImageImportColorBlock> colorBlocks = this.plugin.getPalette().getColorBlocks();
		
		for (; i < matrix.length ; i++) {
			for ( ; j < matrix[0].length ; j++) {
				//Get block
				Block block = world.getBlockAt(x + i * xd, y + matrix[0].length - j, z + i * zd);
				
				block.setTypeId(colorBlocks.get(matrix[i][j]).getTypeId());
				block.setData(colorBlocks.get(matrix[i][j]).getData());

				//Decrement the per queue counter
				blockCounter--;

				//Return if limit is reached
				if (blockCounter == 0) {
					//Save build state
					iiMatrix.setX(i);
					iiMatrix.setY(j);

					int progress = (int) ((100.0 * (i * matrix[0].length + j)) / (matrix.length * matrix[0].length));
					this.plugin.getLog().info("Building progress for " + this.imageMatrixQueue.firstKey() + ": " + progress + "%.");
					this.plugin.getLog().sendPlayerNormal(this.plugin.getServer().getPlayer(this.imageMatrixQueue.firstKey()), "Building progress: " + progress + "%.");

					busy = false;
					
					return;

				}

			}
			
			j = 0;
			
		}
		
		//Remove the iiMatrix
		this.plugin.getLog().sendPlayerNormal(this.plugin.getServer().getPlayer(this.imageMatrixQueue.firstKey()), "Building is complete.");
		this.plugin.getLog().info("Building for " + this.imageMatrixQueue.firstKey() + " is complete.");
		this.imageMatrixQueue.remove(this.imageMatrixQueue.firstKey());
		busy = false;
		
		
		
	}
	//============================================================
	public void generate(File imageFile, Player player, int height) {
		Location location = player.getLocation();
		
		//Log
		this.plugin.getLog().info("Starting image import for " + player.getName() + " at location " + 
																				location.getBlockX() + " " + 
																				location.getBlockY() + " " + 
																				location.getBlockZ());
		
		//Message the player
		this.plugin.getLog().sendPlayerNormal(player, "Starting image import at your location.");
		
		//Generate blocks in game
		generateBlocks(imageFile, player, height);
		
	}
	//##############################################################################
	private void generateBlocks(File imageFile, Player player, int height) {
		//Create buffered image
		BufferedImage image;
		try { image = ImageIO.read(imageFile); } catch (IOException e) { this.plugin.getLog().warn("Error reading the image file."); this.plugin.getLog().sendPlayerWarn(player, "Error reading the image file"); return;}
		
		//Height and width
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		int top = this.plugin.getConfiguration().getMaxHeight();
		int bottom = player.getLocation().getBlockY();
		if (bottom >= this.plugin.getConfiguration().getMaxHeight() || bottom < this.plugin.getConfiguration().getMinHeight()) {
			bottom = this.plugin.getConfiguration().getMinHeight();
		}
		
		//Resize as needed
		if (height == -1) {
			if (imageHeight > top - bottom) {
				image = resize(image, (top - bottom) * imageWidth / imageHeight, top - bottom);

				//Update
				imageWidth = image.getWidth();
				imageHeight = image.getHeight();

			}

		} else if (height > 0) {
			height = height > top - bottom ? top - bottom : height;
			image = resize(image, height * imageWidth / imageHeight, height);

			//Update
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();

		}
		
		//Get image matrix
		int[][] imageMatrix = getImageMatrix(image);
		
		//Get players direction
		Location location = player.getLocation(); location.setY(bottom);
		float yawn = location.getYaw();
		
		//Yawn can be negative angle, add 360 to convert to positive
		if (yawn < 0) yawn += 360;
		
		//These used to modify direction of block building
		int xd = 0, zd = 0;

		if ((yawn >= 0 && yawn <= 45) || (yawn >= 315 && yawn <= 360)) { //Heading 0
			xd = 0;
			zd = 1;
		}
		
		if (yawn > 45 && yawn <= 135) { //Heading 90
			xd = -1;
			zd = 0;
		}

		if (yawn > 135 && yawn <= 225) { //Heading 180
			xd = 0;
			zd = -1;
		}

		if (yawn > 225 && yawn < 315) { //Heading 270
			xd = 1;
			zd = 0;
		}
		
		//Add matrix to the build queue
		this.plugin.getLog().sendPlayerNormal(player, "Image processing is complete. Adding matrix to the build queue.");
		this.imageMatrixQueue.put(player.getName(), new ImageImportMatrix(imageMatrix, location, xd, zd));

	}
	//##############################################################################
	private int[][] getImageMatrix(BufferedImage image) {
		int[][] imageMatrix = new int[image.getWidth()][image.getHeight()];
		
		int[] imageBlockColors = getBlockColors(image);
		
		//Convert imageBlockColors to image matrix
		for (int i = 0 ; i < imageMatrix.length ; i++) {
			for (int j = 0 ; j < imageMatrix[0].length ; j++) {
				imageMatrix[i][j] = imageBlockColors[j * imageMatrix.length + i];
				
			}
			
		}
		
		return imageMatrix;
		
	}
	
	private int[] getBlockColors(BufferedImage image) {
		//Get image's width and height
		int w = image.getWidth();
		int h = image.getHeight();

		//Read in pixel data from the image
		int[] imageArray = new int[w*h];
		image.getRGB(0, 0, w, h, imageArray, 0, w);

		//Array for image pixels with values of default colors for each
		int[] colorIndex = new int[imageArray.length];

		//Temporary variables for processing
		int colorDelta, colorDeltaOld;

		//Run through pixels and find closest in game color matches
		ArrayList<ImageImportColorBlock> colorBlocks = this.plugin.getPalette().getColorBlocks();
		
		for (int i = 0 ; i < imageArray.length ; i++) {
			colorDeltaOld = 9999999;
			for (int j = 0 ; j < colorBlocks.size() ; j++) {
				Color temp = new Color(imageArray[i]);
				colorDelta = Math.abs(colorBlocks.get(j).getColor().getRed() - temp.getRed()) +
							 Math.abs(colorBlocks.get(j).getColor().getGreen() - temp.getGreen()) +
							 Math.abs(colorBlocks.get(j).getColor().getBlue() - temp.getBlue());
				
				if (colorDeltaOld > colorDelta) {
					colorIndex[i] = j;
					colorDeltaOld = colorDelta;
					
				}
				
			}
			
		}

		//Return the array
		return colorIndex;
			
	}
	//===================================================================
	private BufferedImage resize(BufferedImage image, int width, int height) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		
		Graphics2D g = resizedImage.createGraphics();
		
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		
		return resizedImage;
		
	}
	//===================================================================
}
