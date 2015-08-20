package net.dmg2.ImageImport;

import java.util.ArrayList;

public class ImageImportPalette {
	private ArrayList<ImageImportColorBlock> colorBlocks;

	public ImageImportPalette() { this.colorBlocks = new ArrayList<ImageImportColorBlock>(); }
	
	public void addColorBlock(ImageImportColorBlock block) { this.colorBlocks.add(block); }
	public ArrayList<ImageImportColorBlock> getColorBlocks() { return this.colorBlocks; }

}
