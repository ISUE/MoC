package net.dmg2.ImageImport;

import java.awt.Color;

public class ImageImportColorBlock {
	private Color color;
	private int typeId;
	private byte data;
	
	public ImageImportColorBlock(Color color, int typeId, byte data) {
		this.color = color;
		this.typeId = typeId;
		this.data = data;
		
	}

	public Color getColor() { return color; }
	public int getTypeId() { return typeId; }
	public byte getData() { return data; }
	
}
