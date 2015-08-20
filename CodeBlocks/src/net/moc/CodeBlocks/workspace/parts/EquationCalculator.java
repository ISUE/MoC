package net.moc.CodeBlocks.workspace.parts;

import java.util.ArrayList;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.getspout.spoutapi.block.SpoutBlock;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.RobotBlock;
import net.moc.CodeBlocks.workspace.Robotnik.RobotSide;
import net.moc.CodeBlocks.workspace.RobotnikController;

public class EquationCalculator {
	enum EquationResultType {bool, number, side, target}
	
	private CodeBlocks plugin; public CodeBlocks getPlugin() { return plugin; }
	
	public EquationCalculator(CodeBlocks plugin) { this.plugin = plugin; }
	
	//=============================================================================
	public String evaluate(String equation, StackFrame stackFrame, RobotnikController robotnikController) {
		ArrayList<String> postfix = infixToPostfix(equation);
		
		return evaluatePostfix(postfix, stackFrame, robotnikController);
		
	}
	
	//=============================================================================
	private String evaluatePostfix(ArrayList<String> postfix, StackFrame stackFrame, RobotnikController robotnikController) {
		String value = "1";
		
		Stack<String> stack = new Stack<String>();
        for (String token : postfix) {
            // If the token is a value push it onto the stack  
            if (!isOperator(token) && !token.startsWith("eval[")) {
            	token = getVariableValue(token, stackFrame);
            	stack.push(token);     
            	
            } else if (isOperator(token)) {
            	String v1 = "", v2 = "", result = "";
            	
        		switch (token) {
        		case "+" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "-" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "*" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "/" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "%" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "^" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "and" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "or" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case ">" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case ">=" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "<" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "<=" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "==" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "<>" : v2 = stack.pop(); v1 = stack.pop(); result = evaluateBinary(stackFrame, token, v1, v2); break;
        		case "not" : v1 = stack.pop(); result = evaluateUnary(stackFrame, token, v1); break;
        		case "sqrt" : v1 = stack.pop(); result = evaluateUnary(stackFrame, token, v1); break;
        		}
                  
                stack.push(result);
                
            } else if (token.startsWith("eval[")) {
            	String result = eval(token, robotnikController);
                stack.push(result);
                
            }
            
        }
          
        if (!stack.isEmpty()) value = stack.pop();
		
		return value;
		
	}

	private String eval(String token, RobotnikController robotnikController) {
		token = token.replace("eval[", "").replace("]", "");
		String[] parts = {"","","",""};
		
		parts[0] = token.substring(0, token.indexOf(",")); token = token.substring(token.indexOf(",")+1);
		parts[1] = token.substring(0, token.indexOf(",")); token = token.substring(token.indexOf(",")+1);
		parts[2] = token.substring(0, token.indexOf(",")); token = token.substring(token.indexOf(",")+1);
		parts[3] = token;
		
		if (getSide(parts[0]) != null) {
			boolean retval;
			
			//Extract values
			RobotSide side = getSide(parts[0]);
			String targetType = parts[1];
			int distance = 1; try { distance = Integer.parseInt(parts[2]); } catch (NumberFormatException e) {}
			byte data = -1; try { data = Byte.parseByte(parts[3]); } catch (NumberFormatException e) {}
			
			boolean evaluatedTrue = true;
			if (targetType.startsWith("!")) { evaluatedTrue = false; targetType = targetType.substring(1); }
			retval = !evaluatedTrue;
			
			//Figure out targetType - type (int) / npc type (string) / robot (string)
			boolean isEntity = false; Integer typeId = null; boolean isRobot = false;
			//"Monster", "Animal", "Creature", "Vehicle", "Player", "All"
			if (targetType.equalsIgnoreCase("Monster") || targetType.equalsIgnoreCase("Animal") || targetType.equalsIgnoreCase("Creature") ||
					targetType.equalsIgnoreCase("Vehicle") || targetType.equalsIgnoreCase("Player") || targetType.equalsIgnoreCase("All")) {
				isEntity = true;
			}
			
			try { typeId = Integer.parseInt(targetType); } catch (NumberFormatException e) {}
			if (targetType.equalsIgnoreCase("robot")) { isRobot = true; }
			
			if (side == RobotSide.inventory) {
				//Blocks only
				if (typeId == null) { retval = evaluatedTrue; }
				else if (robotnikController.getRobotnik().hasItem(typeId, data)) { retval = evaluatedTrue; }
				
			} else if (side == RobotSide.around) {
				//Block/Robot/NPC
				if (!isEntity && !isRobot) {
					//Looking for block
					if (typeId == null) typeId = 0;
					
					Block center = robotnikController.getRobotnik().getLocation().getBlock();
					boolean done = false;
					
					//Try to find a matching block
					for (int x = -distance ; x <= distance && !done ; x++) {
						for (int y = -distance ; y <= distance && !done ; y++) {
							for (int z = -distance ; z <= distance && !done ; z++) {
								if (x == 0 && y == 0 && z == 0) continue;
								//Get next block
								Block b = center.getLocation().add(x, y, z).getBlock();
								
								//See if it's a match
								if (b.getTypeId() == typeId && (data == -1 || b.getData() == data)) {
									retval = evaluatedTrue;
									done = true;
									
								}
						
							}
							
						}
						
					}
					
				} else if (!isEntity && isRobot) {
					//Looking for robot
					Block center = robotnikController.getRobotnik().getLocation().getBlock();
					boolean done = false;
					
					//Try to find a robot
					for (int x = -distance ; x <= distance && !done ; x++) {
						for (int y = -distance ; y <= distance && !done ; y++) {
							for (int z = -distance ; z <= distance && !done ; z++) {
								if (x == 0 && y == 0 && z == 0) continue;
								
								try {
									SpoutBlock b = (SpoutBlock) center.getLocation().add(x, y, z).getBlock();
									
									if (b.getCustomBlock() instanceof RobotBlock) {
										//Found a robot
										retval = evaluatedTrue;
										done = true;
										
									}
							
								} catch (Exception e) {} 
								
							}
							
						}
						
					}
					
				} else {
					//Looking for NPC
					Location center = robotnikController.getRobotnik().getLocation();
					
					if (targetType.equalsIgnoreCase("Monster")) for (Entity e : center.getWorld().getEntitiesByClass(Monster.class)) if (e.getLocation().distance(center) <= distance) { retval = true; break; }
					if (targetType.equalsIgnoreCase("Animal")) for (Entity e : center.getWorld().getEntitiesByClass(Animals.class)) if (e.getLocation().distance(center) <= distance) { retval = true; break; }
					if (targetType.equalsIgnoreCase("Creature")) for (Entity e : center.getWorld().getEntitiesByClass(Creature.class)) if (e.getLocation().distance(center) <= distance) { retval = true; break; }
					if (targetType.equalsIgnoreCase("Vehicle")) for (Entity e : center.getWorld().getEntitiesByClass(Vehicle.class)) if (e.getLocation().distance(center) <= distance) { retval = true; break; }
					if (targetType.equalsIgnoreCase("Player")) for (Entity e : center.getWorld().getEntitiesByClass(Player.class)) if (e.getLocation().distance(center) <= distance) { retval = true; break; }
					if (targetType.equalsIgnoreCase("All")) for (Entity e : center.getWorld().getEntitiesByClass(Entity.class))
						if ((e instanceof Monster || e instanceof Animals || e instanceof Creature || e instanceof Vehicle || e instanceof Player)
								&& e.getLocation().distance(center) <= distance) { retval = evaluatedTrue; break; }
					
				}
				
			} else {
				//Single robot side
				if (!isEntity && !isRobot) {
					//Looking for block
					if (typeId == null) typeId = 0;

					Block center = robotnikController.getRobotnik().getLocation().getBlock();
					Block block = robotnikController.getRobotnik().getBlockAt(side);
					int dx = block.getLocation().getBlockX() - center.getLocation().getBlockX();
					int dy = block.getLocation().getBlockY() - center.getLocation().getBlockY();
					int dz = block.getLocation().getBlockZ() - center.getLocation().getBlockZ();
					
					for (int i = 1 ; i <= distance ; i++) {
						Block b = center.getLocation().add(dx*i, dy*i, dz*i).getBlock();
						if (b.getTypeId() == typeId && (data == -1 || b.getData() == data)) {
							retval = evaluatedTrue;
							break;
							
						}
						
					}
					
				} else if (!isEntity && isRobot) {
					//Looking for robot
					Block center = robotnikController.getRobotnik().getLocation().getBlock();
					Block block = robotnikController.getRobotnik().getBlockAt(side);
					int dx = block.getLocation().getBlockX() - center.getLocation().getBlockX();
					int dy = block.getLocation().getBlockY() - center.getLocation().getBlockY();
					int dz = block.getLocation().getBlockZ() - center.getLocation().getBlockZ();

					for (int i = 1 ; i <= distance ; i++) {
						try {
							SpoutBlock b = (SpoutBlock) center.getLocation().add(dx*i, dy*i, dz*i).getBlock();

							if (b.getCustomBlock() instanceof RobotBlock) {
								retval = evaluatedTrue;
								break;

							}
							
						} catch (Exception e) {}

					}
					
				} else {
					//Looking for NPC
					Block center = robotnikController.getRobotnik().getLocation().getBlock();
					Block block = robotnikController.getRobotnik().getBlockAt(side);
					int dx = block.getLocation().getBlockX() - center.getLocation().getBlockX();
					int dy = block.getLocation().getBlockY() - center.getLocation().getBlockY();
					int dz = block.getLocation().getBlockZ() - center.getLocation().getBlockZ();
					
					if (targetType.equalsIgnoreCase("Monster")) for (Entity e : center.getWorld().getEntitiesByClass(Monster.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					} else if (targetType.equalsIgnoreCase("Animal")) for (Entity e : center.getWorld().getEntitiesByClass(Animals.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					} else if (targetType.equalsIgnoreCase("Creature")) for (Entity e : center.getWorld().getEntitiesByClass(Creature.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					} else if (targetType.equalsIgnoreCase("Vehicle")) for (Entity e : center.getWorld().getEntitiesByClass(Vehicle.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					} else if (targetType.equalsIgnoreCase("Player")) for (Entity e : center.getWorld().getEntitiesByClass(Player.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					} else if (targetType.equalsIgnoreCase("All")) for (Entity e : center.getWorld().getEntitiesByClass(Entity.class)) {
						int edx = e.getLocation().getBlockX() - center.getLocation().getBlockX();
						int edy = e.getLocation().getBlockY() - center.getLocation().getBlockY();
						int edz = e.getLocation().getBlockZ() - center.getLocation().getBlockZ();
						if (edx == 0 && edy == 0 && edz == 0) { retval = evaluatedTrue; break;}
						
						if (dx != 0 && edy == 0 && edz == 0) {
							if  (edx > 0 && dx > 0) { if (edx <= dx*distance) { retval = evaluatedTrue; break; } }
							else if (edx < 0 && dx < 0) { if (edx >= dx*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dy != 0 && edx == 0 && edz == 0) {
							if  (edy > 0 && dy > 0) { if (edy <= dy*distance) { retval = evaluatedTrue; break; } }
							else if (edy < 0 && dy < 0) { if (edy >= dy*distance) { retval = evaluatedTrue; break; } }
							
						}
						
						if (dz != 0 && edx == 0 && edy == 0) {
							if  (edz > 0 && dz > 0) { if (edz <= dz*distance) { retval = evaluatedTrue; break; } }
							else if (edz < 0 && dz < 0) { if (edz >= dz*distance) { retval = evaluatedTrue; break; } }
							
						}
						
					}
					
				}
				
			}
			
			return retval+"";
			
		} else {
			//Count
			int typeId = 0; try { typeId = Integer.parseInt(parts[1]); } catch (NumberFormatException e) {}
			byte data = -1; try { data = Byte.parseByte(parts[3]); } catch (NumberFormatException e) {}
			
			return robotnikController.getRobotnik().countItem(typeId, data)+"";
			
		}
		
	}
	
	private String getVariableValue(String v, StackFrame stackFrame) {
		if (v.startsWith("arg")) {
			switch (v) {
			case "arg1":
				v = stackFrame.getArgs()[0];
				break;
			case "arg2":
				v = stackFrame.getArgs()[1];
				break;
			case "arg3":
				v = stackFrame.getArgs()[2];
				break;
			}
		} else if (v.startsWith("var")) {
			switch (v) {
			case "var1":
				v = stackFrame.getVars()[0];
				break;
			case "var2":
				v = stackFrame.getVars()[1];
				break;
			case "var3":
				v = stackFrame.getVars()[2];
				break;
			case "var4":
				v = stackFrame.getVars()[3];
				break;
			case "var5":
				v = stackFrame.getVars()[4];
				break;
			case "var6":
				v = stackFrame.getVars()[5];
				break;
			case "var7":
				v = stackFrame.getVars()[6];
				break;
			case "var8":
				v = stackFrame.getVars()[7];
				break;
			case "var9":
				v = stackFrame.getVars()[8];
				break;
			case "var10":
				v = stackFrame.getVars()[9];
				break;
			}
			
		} else if (v.equalsIgnoreCase("fvar")) {
			v = stackFrame.getVars()[10];
			
		} else if (v.equalsIgnoreCase("retval")) {
			v = stackFrame.getRetval();
			
		}
		
		if (v.split(":").length > 1) return v.split(":")[1];
		else return v;
		
	}
	//=============================================================================
	private String evaluateUnary(StackFrame stackFrame, String token, String v1) {
		v1 = getVariableValue(v1, stackFrame);
		
		String result = "1";
		if (v1 == null) v1 = "1";
		if (v1.length() == 0) v1 = "1";
		
		switch (token) {
		case "not":
			if (v1.equalsIgnoreCase("true")) result = "false";
			else result = "true";
			break;
		case "sqrt":
			try { result = ((int)Math.sqrt(Integer.parseInt(v1)))+""; }
			catch (NumberFormatException e) { result = "1"; }
			break;
		}
		
		return result;
		
	}

	//=============================================================================
	private String evaluateBinary(StackFrame stackFrame, String token, String v1, String v2) {
		v1 = getVariableValue(v1, stackFrame);
		v2 = getVariableValue(v2, stackFrame);
		
		String v1is = "int";
		String v2is = "int";
		
		String result = "1";
		if (v1 == null) v1 = "1";
		if (v2 == null) v2 = "1";
		if (v1.length() == 0) v1 = "1";
		if (v2.length() == 0) v2 = "1";
		
		if (v1.equalsIgnoreCase("true") || v1.equalsIgnoreCase("false")) v1is = "bool";
		else if (getSide(v1) != null) v1is = "side";
		else {
			try { Integer.parseInt(v1); v1is = "int"; }
			catch (NumberFormatException e) { v1 = "1"; v1is = "int"; }
		}
		
		if (v2.equalsIgnoreCase("true") || v2.equalsIgnoreCase("false")) v2is = "bool";
		else if (getSide(v2) != null) v2is = "side";
		else {
			try { Integer.parseInt(v2); v2is = "int"; }
			catch (NumberFormatException e) { v2 = "1"; v2is = "int"; }
		}
		
		switch (token) {
		case "+" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) + Integer.parseInt(v2))+"";
			else if ((v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("side")) || (v1is.equalsIgnoreCase("side") && v2is.equalsIgnoreCase("int"))) result = evaluateSide(v1, v2, 1);
			else result = "1";
			break;
		case "-" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) - Integer.parseInt(v2))+"";
			else if ((v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("side")) || (v1is.equalsIgnoreCase("side") && v2is.equalsIgnoreCase("int"))) result = evaluateSide(v1, v2, 1);
			else result = "1";
			break;
		case "*" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) * Integer.parseInt(v2))+"";
			else result = "1";
			break;
		case "/" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) { System.out.println (v1 + " / " + v2); result = (Integer.parseInt(v1) / Integer.parseInt(v2))+""; }
			else result = "1";
			break;
		case "%" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) % Integer.parseInt(v2))+"";
			else result = "1";
			break;
		case "^" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = ((int)Math.pow(Integer.parseInt(v1), Integer.parseInt(v2)))+"";
			else result = "1";
			break;
		case "and" :
			if (v1is.equalsIgnoreCase("bool") && v2is.equalsIgnoreCase("bool")) result = (Boolean.parseBoolean(v1) && Boolean.parseBoolean(v2))+"";
			else result = "true";
			break;
		case "or" :
			if (v1is.equalsIgnoreCase("bool") && v2is.equalsIgnoreCase("bool")) result = (Boolean.parseBoolean(v1) || Boolean.parseBoolean(v2))+"";
			else result = "true";
			break;
		case ">" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) > Integer.parseInt(v2))+"";
			else result = "true";
			break;
		case ">=" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) >= Integer.parseInt(v2))+"";
			else result = "true";
			break;
		case "<" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) < Integer.parseInt(v2))+"";
			else result = "true";
			break;
		case "<=" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) <= Integer.parseInt(v2))+"";
			else result = "true";
			break;
		case "==" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) == Integer.parseInt(v2))+"";
			else result = "true";
			break;
		case "<>" :
			if (v1is.equalsIgnoreCase("int") && v2is.equalsIgnoreCase("int")) result = (Integer.parseInt(v1) != Integer.parseInt(v2))+"";
			else result = "true";
			break;
			
		}
		
		return result;
		
	}

	private RobotSide getSide(String v1) {
		if (v1.equalsIgnoreCase(RobotSide.around.toString())) return RobotSide.around;
		if (v1.equalsIgnoreCase(RobotSide.back.toString())) return RobotSide.back;
		if (v1.equalsIgnoreCase(RobotSide.down.toString())) return RobotSide.down;
		if (v1.equalsIgnoreCase(RobotSide.front.toString())) return RobotSide.front;
		if (v1.equalsIgnoreCase(RobotSide.inventory.toString())) return RobotSide.inventory;
		if (v1.equalsIgnoreCase(RobotSide.left.toString())) return RobotSide.left;
		if (v1.equalsIgnoreCase(RobotSide.right.toString())) return RobotSide.right;
		if (v1.equalsIgnoreCase(RobotSide.top.toString())) return RobotSide.top;
		
		return null;
		
	}

	private String evaluateSide(String v1, String v2, int sign) {
		//front, back, left, right
		String[] sides = {"front", "right", "back", "left"};
		
		if (getSide(v1) != null) {
			int v2i = Integer.parseInt(v2) % 4;
			int v1i = 0;
			
			switch (v1) {
			case "front":
				v1i = 0;
				break;
			case "right":
				v1i = 1;
				break;
			case "back":
				v1i = 2;
				break;
			case "left":
				v1i = 3;
				break;
			}
			
			return sides[(v1i + v2i * sign) % 4];
			
		}
		
		if (getSide(v2) != null) {
			int v1i = Integer.parseInt(v1) % 4;
			int v2i = 0;
			
			switch (v2) {
			case "front":
				v2i = 0;
				break;
			case "right":
				v2i = 1;
				break;
			case "back":
				v2i = 2;
				break;
			case "left":
				v2i = 3;
				break;
			}
			
			return sides[(v1i + v2i * sign) % 4];
			
		}
		
		return "front";
		
	}

	//=============================================================================
    public ArrayList<String> infixToPostfix(String equation) {  
        ArrayList<String> out = new ArrayList<String>();  
        Stack<String> stack = new Stack<String>();  
        
        for (String token : tokenize(equation)) {  
            // If token is an operator
            if (isOperator(token)) {    
                // While stack not empty AND stack top element is an operator  
                while (!stack.empty() && isOperator(stack.peek())) {                      
                    if ((isAssociative(token, true) && comparePrecedence(token, stack.peek()) <= 0) ||
                    	(isAssociative(token, false) && comparePrecedence(token, stack.peek()) < 0)) {  
                        out.add(stack.pop());
                        
                        continue;
                        
                    }
                    
                    break;
                    
                }
                
                // Push the new operator on the stack
                stack.push(token);
                
            }
            
            // If token is a left bracket '('
            else if (token.equals("(")) {  
                stack.push(token); 
            }
            
            // If token is a right bracket ')'  
            else if (token.equals(")")) {                  
                while (!stack.empty() && !stack.peek().equals("(")) out.add(stack.pop());
                
                stack.pop();
                
            }
            
            // If token is a number  
            else out.add(token);
            
        }
        
        while (!stack.empty()) out.add(stack.pop());
        
        return out;
        
    }
    
    private ArrayList<String> tokenize(String equation) {
    	ArrayList<String> retval = new ArrayList<String>();
    	equation = equation.replaceAll(" ", "");
    	equation = equation.replaceAll("\\r\\n|\\r|\\n", "");
    	
		String regex = "(?<=op)|(?=op)|(?<=vars)|(?=vars)".replace("op", "[-+*/()%^]").replace("vars", "sqrt|false|true|and|or|not|<=|>=|<>|==|<|>");
		String[] splitEq = equation.split(regex);
		
		for (int i = 0 ; i < splitEq.length ; i++) {
			if (splitEq[i].equalsIgnoreCase(">") && i+1 < splitEq.length && splitEq[i+1].equalsIgnoreCase("=")) { retval.add(splitEq[i] + splitEq[i+1]); i++; }
			else if (splitEq[i].equalsIgnoreCase("<") && i+1 < splitEq.length && splitEq[i+1].equalsIgnoreCase("=")) { retval.add(splitEq[i] + splitEq[i+1]); i++; }
			else if (splitEq[i].equalsIgnoreCase("<") && i+1 < splitEq.length && splitEq[i+1].equalsIgnoreCase(">")) { retval.add(splitEq[i] + splitEq[i+1]); i++; }
			else if (splitEq[i].startsWith("eval[") && !splitEq[i].endsWith("]")) {
				//eval[x,x,x,x] was split, recombine
				String temp = splitEq[i];
				i++;
				while (i < splitEq.length) {
					temp += splitEq[i];
					if (splitEq[i].endsWith("]")) break;
					i++;
					
				}
				
				retval.add(temp);
				
			} else { retval.add(splitEq[i]); }
			
		}
		
		return retval;
		
	}

	//==================================================================================================
    //==================================================================================================
    //==================================================================================================
	private boolean isAssociative(String operator, boolean isLeftAssociative) {
		boolean isLeft = true;
		
		switch (operator) {
		case "+" :
		case "-" :
		case "*" :
		case "/" :
		case "%" :
		case "and" :
		case "or" :
		case "not" :
		case "(" :
		case ">" :
		case ">=" :
		case "<" :
		case "<=" :
		case "==" :
		case "<>" :
			isLeft = true;
			break;
		case "^" :
		case "sqrt" :
			isLeft = false;
			break;
		}
		
		return isLeft == isLeftAssociative;
	}

	private int comparePrecedence(String o1, String o2) {
		if (isOperator(o1) && isOperator(o2)) return precedence(o1) - precedence(o2);
		return -1;
		
	}

	private boolean isOperator(String token) {
		switch (token) {
		case "+" :
		case "-" :
		case "*" :
		case "/" :
		case "%" :
		case "^" :
		case "not" :
		case "sqrt" :
		case "and" :
		case "or" :
		case ">" :
		case ">=" :
		case "<" :
		case "<=" :
		case "==" :
		case "<>" :
			return true;
		}
		
		return false;
		
	}

	private int precedence(String operation) {
		if (!isOperator(operation)) return -1;
		int retval = 0;

		switch (operation) {
		case "+" :
		case "-" :
			retval = 3;
			break;
		case "*" :
		case "/" :
		case "%" :
			retval = 4;
			break;
		case "^" :
			retval = 5;
			break;
		case "not" :
		case "sqrt" :
			retval = 6;
			break;
		case "(" :
			retval = 100;
			break;
		case "and" :
		case "or" :
			retval = 1;
			break;
		case ">" :
		case ">=" :
		case "<" :
		case "<=" :
		case "==" :
		case "<>" :
			retval = 2;
			break;
		default :
			retval = 0;
			break;
		}
		
		return retval;
		
	}

}

