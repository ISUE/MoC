package net.moc.MOC3DImporter;

import java.awt.Color;

public class MOC3DImporterColorBlock {
	private Color color;
	private int typeId;
	private byte data;
	
	public MOC3DImporterColorBlock(Color color, int typeId, byte data) {
		this.color = color;
		this.typeId = typeId;
		this.data = data;
		
	}

	public Color getColor() { return color; }
	public int getTypeId() { return typeId; }
	public byte getData() { return data; }
	
}
