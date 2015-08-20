package net.moc.CodeBlocks.workspace.parts;

import net.moc.CodeBlocks.blocks.ProgramCodeBlock;
import net.moc.CodeBlocks.blocks.attack.AttackFarBlock;
import net.moc.CodeBlocks.blocks.attack.AttackNearBlock;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.FunctionBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.interaction.BuildBlock;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import net.moc.CodeBlocks.blocks.movement.MoveBlock;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
import net.moc.CodeBlocks.workspace.parts.StackFrame.FunctionVariables;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;

public class CodeBlockValues {
	//==================================================================================
	public enum CodeBlockType {none, functionName, counter, trueFalse, direction, blockInfo, attackNear, attackFar, math}
	//==================================================================================

	
	
	//==================================================================================
	//Type
	private CodeBlockType type = CodeBlockType.none;
	public CodeBlockType getType() { return type; }
	public void setType(CodeBlockType v) { type = v; }
	
	private boolean isValid = true;
	public boolean isValid() { return isValid; }
	
	//trueFalse - Case, If, While
	//Type 1 - Block - Direction/Block/Data
	//Type 2 - Sense Directional - Direction/Distance/NPCorBlock/Data
	//Type 3 - Sense Radial - ../Radius/NPCorBlock/Data
	// Total - 1 - direction-location / 2 - npc-block / 3 - distance-radius / 4 - data
	private String side = RobotSide.front.toString();
	public String getTrueSide() { return side; }
	public void setTrueSide(RobotSide v) { side = v.toString(); }
	
	private String targetType = Material.AIR.toString().toLowerCase();
	public String getTrueTargetType() { return targetType; }
	public void setTrueTargetType(String v) { targetType = v;}
	
	private String data = "-1";
	public String getTrueData() { return data; }
	public void setTrueData(byte v) { data = v+""; }
	
	private String distance = "1";
	public String getTrueDistance() { return distance; }
	public void setTrueDistance(int v) { distance = v+""; }
	
	//counter - For
	private String counter = "1";
	public String getCounter() { return counter; }
	public void setCounter(String v) { counter = v; }
	
	//functionName - Function / Call function / Math
	private String functionName = "";
	public String getFunctionName() { return functionName; }
	public void setFunctionName(String v) { functionName = v; }
	
	//blockInfo - Build
	private RobotSide location = RobotSide.front;
	public RobotSide getBuildLocation() { return location; }
	public void setBuildLocation(RobotSide v) { location = v; }
	
	private Material blockType = Material.AIR;
	public Material getBuildBlockType() { return blockType; }
	public void setBuildBlockType(Material v) { blockType = v; }
	
	private byte blockData = -1;
	public byte getBuildBlockData() { return blockData; }
	public void setBuildBlockData(byte v) { blockData = v; }
	
	//direction - Interactions except Build, Sense / Attack
	private String direction = RobotSide.front.toString().toLowerCase();
	public String getInteractionDirection() { return direction; }
	public void setInteractionDirection(String v) { direction = v.toLowerCase(); }
	//==================================================================================

	
	
	//==================================================================================
	public CodeBlockValues () { isValid = true; }
	public CodeBlockValues (SpoutBlock block) {
		CustomBlock cb = block.getCustomBlock();
		
		if (validate(cb)) {
			//Get value block
			SpoutBlock valueBlock = (SpoutBlock) block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));

			if (cb instanceof CaseBlock || cb instanceof IfBlock || cb instanceof  WhileBlock) getTrueValue(valueBlock);
			else if (cb instanceof ForBlock) getCounterValue(valueBlock);
			else if (cb instanceof FunctionBlock || cb instanceof CallFunctionBlock) getNameValue(cb, valueBlock);
			else if (cb instanceof BuildBlock) getBuildBlockValue(valueBlock);
			else if (cb instanceof MoveBlock || cb instanceof DestroyBlock || cb instanceof DigBlock || cb instanceof PickUpBlock || cb instanceof PlaceBlock) getLocationValue(valueBlock);
			else if (cb instanceof AttackFarBlock || cb instanceof AttackNearBlock) getAttackValue(cb, valueBlock);
			else if (cb instanceof SetBlock) getSetValue(block, valueBlock);
			else if (cb instanceof EvaluateBlock) getEvaluateValue(valueBlock);
			
		}
		
	}
	//==================================================================================

	
	
	//==================================================================================
	private void getTrueValue(SpoutBlock valueBlock) {
		type = CodeBlockType.trueFalse;
		
		//Defaults
		targetType = valueBlock.getType().toString();
		data = valueBlock.getData()+"";
		side = RobotSide.front.toString();
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			//Clear sign data
			//material = Material.AIR;
			targetType = Material.AIR.toString();
			data = "-1";
			
			//Get sign state
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			//1st line - side
			for (FunctionVariables fv : FunctionVariables.values()) {
				if (lines[0].equalsIgnoreCase(fv.toString())) { side = lines[0]; break; }
				else {
					RobotSide rs = getSide(lines[0].trim());
					if (rs != null) side = rs.toString();
					else side = RobotSide.front.toString();
					
				}
				
			}
			
			if (side == null) side = RobotSide.front.toString();
			
			//2nd line - material
			targetType = lines[1].trim().toLowerCase();
			if (targetType == null || targetType == "") targetType = Material.AIR.toString();
			
			//3rd line - distance
			for (FunctionVariables fv : FunctionVariables.values()) {
				if (lines[2].equalsIgnoreCase(fv.toString())) { distance = lines[2]; break; }
				else try { distance = Integer.parseInt(lines[2].trim())+""; } catch (NumberFormatException e) { distance = "1"; }
			}
			
			//4th line - data
			for (FunctionVariables fv : FunctionVariables.values()) if (lines[3].equalsIgnoreCase(fv.toString())) { data = lines[3]; break; }
			else try { data = Byte.parseByte(lines[3].trim())+""; } catch (NumberFormatException e) { data = "-1"; }
			
			System.out.println("side " + side + " target " + targetType + " distance " + distance + " data " + data + " ========= " + this);
			
		}
		
	}

	//==================================================================================
	private void getCounterValue(SpoutBlock valueBlock) {
		type = CodeBlockType.counter;
		
		//Default
		counter = "1";
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			counter = lines[0];
			
		}
		
	}

	//==================================================================================
	private void getNameValue(CustomBlock cb, SpoutBlock valueBlock) {
		type = CodeBlockType.functionName;
		
		//Default
		functionName = "";
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			for (String line : lines) if (line.length() > 0) { functionName = line; break; }
			if (functionName == null) functionName = "";
			
			if (cb instanceof CallFunctionBlock) functionName += "#" + lines[1] + "#" + lines[2] + "#" + lines[3];
			
		}
		
	}
	
	//==================================================================================
	private void getSetValue(SpoutBlock block, SpoutBlock valueBlock) {
		type = CodeBlockType.math;
		
		//Default
		functionName = "";
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			functionName = lines[0] + "@" + lines[1];
			
			if (functionName == null) functionName = "";
			
		}
		
		if (functionName.length() == 0) return;
		
	}

	private void getEvaluateValue(SpoutBlock valueBlock) {
		type = CodeBlockType.math;
		
		//Default
		functionName = "1";
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			//1st line - side
			String eSide = lines[0].trim(); if (eSide.length() > 0 && getSide(eSide) == null) eSide = RobotSide.front.toString();
			
			//2nd line - material
			String eType = lines[1].trim().toLowerCase(); if (eType == null || eType.length() == 0) eType = Material.AIR.toString();
			
			//3rd line - distance
			String eDistance = ""; if (lines[2].length() > 0) try { eDistance = Integer.parseInt(lines[2].trim())+""; } catch (NumberFormatException e) { eDistance = "1"; }
			
			//4th line - data
			byte eData = -1; try { eData = Byte.parseByte(lines[3].trim()); } catch (NumberFormatException e) { eData = -1; } if (eData < -1) eData = -1;
			
			//Combine
			functionName = "eval[" + eSide + "," + eType + "," + eDistance + "," + eData + "]";
			
		}
		
		if (functionName.length() == 0) { functionName = "1"; return; }
		
	}

	//==================================================================================
	private void getBuildBlockValue(SpoutBlock valueBlock) {
		type = CodeBlockType.blockInfo;
		
		//Defaults
		location = RobotSide.front;
		blockType = valueBlock.getType();
		blockData = valueBlock.getData();
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			//Clear sign data
			blockType = Material.AIR;
			blockData = -1;
			
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			//1st line - side
			location = getSide(lines[0]);
			if (location == null) location = RobotSide.front;
			
			//2nd line - material
			try { blockType = Material.getMaterial(Integer.parseInt(lines[1])); }
			catch (NumberFormatException e) { blockType = Material.AIR; }
			if (blockType == null) blockType = Material.AIR;
			
			//3rd line - data
			try { blockData = Byte.parseByte(lines[2]); }
			catch (NumberFormatException e) { blockData = -1; }
			if (blockData < -1) blockData = -1;

		}
		
	}

	//==================================================================================
	private void getLocationValue(SpoutBlock valueBlock) {
		type = CodeBlockType.direction;
		
		//Default
		direction = RobotSide.front.toString().toLowerCase();
		
		if (valueBlock.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) valueBlock.getState();
			String[] lines = sign.getLines();
			sign.update();
			
			direction = lines[0];

		}
		
		if (direction == null) direction = RobotSide.front.toString().toLowerCase();
		
	}
	
	private void getAttackValue(CustomBlock cb, SpoutBlock valueBlock) {
		if (cb instanceof AttackFarBlock) {
			type = CodeBlockType.attackFar;
			
			direction = RobotSide.front.toString().toLowerCase();
			
			if (valueBlock.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) valueBlock.getState();
				String[] lines = sign.getLines();
				sign.update();
				
				direction = lines[0];
				targetType = lines[1];

			}
			
			if (direction == null) direction = RobotSide.front.toString().toLowerCase();
			if (!targetType.equalsIgnoreCase("Entity") && !targetType.equalsIgnoreCase("Robot") && !targetType.equalsIgnoreCase("All")) {
				targetType = "Entity";
				
			}
			
		} else {
			type = CodeBlockType.attackNear;
			
			if (valueBlock.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) valueBlock.getState();
				String[] lines = sign.getLines();
				sign.update();
				
				targetType = lines[1];

			}
			
			if (!targetType.equalsIgnoreCase("Monster") && !targetType.equalsIgnoreCase("Animal") && !targetType.equalsIgnoreCase("Creature") && 
				!targetType.equalsIgnoreCase("Player") && !targetType.equalsIgnoreCase("Robot") && !targetType.equalsIgnoreCase("All")) {
					
				targetType = "monster";
					
			}
			
		}
		
	}

	//==================================================================================
	private boolean validate(CustomBlock cb) {
		if (cb != null && cb instanceof ProgramCodeBlock) isValid = true;
		else isValid = false;
		
		return isValid;
		
	}
	
	//==================================================================================
	private RobotSide getSide(String line) {
		for (RobotSide side : RobotSide.values()) if (line.equalsIgnoreCase(side.toString())) return side;
		
		return null;
		
	}

}