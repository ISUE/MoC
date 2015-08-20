package net.moc.CodeBlocks.workspace.parts;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
import net.moc.CodeBlocks.workspace.parts.CodeBlockValues.CodeBlockType;

import org.bukkit.Material;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;

public class CodeBlock {
	//Flag if block is valid
	private boolean valid = true; public boolean isValid() { return valid; }
	
	//CodeBlock command
	private CustomBlock customBlock = null; public CustomBlock getCustomBlock() { return customBlock; }
	private CodeBlockValues values = null; public CodeBlockValues getValues() { return values; }
	
	//Offset
	private int offsetX = 0, offsetY = 0, offsetZ = 0;
	public int getOffsetX() { return offsetX; } public void setOffsetX(int n) { offsetX = n; }
	public int getOffsetY() { return offsetY; } public void setOffsetY(int n) { offsetY = n; }
	public int getOffsetZ() { return offsetZ; } public void setOffsetZ(int n) { offsetZ = n; }
	
	//==================================================================================
	public CodeBlock(SpoutBlock block) {
		customBlock = block.getCustomBlock();
		
		if (customBlock == null) { valid = false; }
		else {
			values = new CodeBlockValues(block);
			valid = values.isValid();
			
		}
		
	}
	
	public CodeBlock(CodeBlocks plugin, String customBlockName, String offsetx, String offsety, String offsetz,
						String type, String side, String targetType, String data, String distance, String counter,
						String functionName, String location, String blockType, String blockData, String direction) {
		
		customBlock = plugin.getBlocks().getBlock(customBlockName);
		valid = true;
		
		try { offsetX = Integer.parseInt(offsetx); } catch (NumberFormatException e) { offsetX = 0;}
		try { offsetY = Integer.parseInt(offsety); } catch (NumberFormatException e) { offsetY = 0;}
		try { offsetZ = Integer.parseInt(offsetz); } catch (NumberFormatException e) { offsetZ = 0;}
		
		values = new CodeBlockValues();
		
		try { values.setType(CodeBlockType.valueOf(type)); } catch (IllegalArgumentException e) { values.setType(CodeBlockType.none); }
		
		try { values.setTrueSide(RobotSide.valueOf(side)); } catch (IllegalArgumentException e) { values.setTrueSide(RobotSide.front); }
		values.setTrueTargetType(targetType);
		try { values.setTrueData(Byte.parseByte(data)); } catch (NumberFormatException e) { values.setTrueData((byte) 0); }
		try { values.setTrueDistance(Integer.parseInt(distance)); } catch (NumberFormatException e) { values.setTrueDistance(1); }
		
		values.setCounter(counter);
		
		values.setFunctionName(functionName);

		try { values.setBuildLocation(RobotSide.valueOf(location)); } catch (IllegalArgumentException e) { values.setBuildLocation(RobotSide.front); }
		try { values.setBuildBlockType(Material.valueOf(blockType)); } catch (IllegalArgumentException e) { values.setBuildBlockType(Material.AIR); }
		try { values.setBuildBlockData(Byte.parseByte(blockData)); } catch (NumberFormatException e) { values.setBuildBlockData((byte) 0); }
		
		values.setInteractionDirection(direction);
		
	}
	
}
