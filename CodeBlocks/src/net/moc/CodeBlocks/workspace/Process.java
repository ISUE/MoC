package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
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
import net.moc.CodeBlocks.workspace.command.AttackCommand;
import net.moc.CodeBlocks.workspace.command.CaseCommand;
import net.moc.CodeBlocks.workspace.command.CheckCountCommand;
import net.moc.CodeBlocks.workspace.command.Command;
import net.moc.CodeBlocks.workspace.command.ForCommand;
import net.moc.CodeBlocks.workspace.command.FunctionCallCommand;
import net.moc.CodeBlocks.workspace.command.IfCommand;
import net.moc.CodeBlocks.workspace.command.InteractionCommand;
import net.moc.CodeBlocks.workspace.command.JumpCommand;
import net.moc.CodeBlocks.workspace.command.MathCommand;
import net.moc.CodeBlocks.workspace.command.MovementCommand;
import net.moc.CodeBlocks.workspace.command.MovementCommand.Direction;
import net.moc.CodeBlocks.workspace.command.TrueFalseCommand;
import net.moc.CodeBlocks.workspace.command.WhileCommand;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;
import net.moc.CodeBlocks.workspace.parts.EquationCalculator;
import net.moc.CodeBlocks.workspace.parts.StackFrame;
import net.moc.CodeBlocks.workspace.parts.StackFrame.FunctionVariables;

public class Process {
	public enum State {READY, RUNNING, SUSPENDED, ERROR}
	//===================================================
	private CodeBlocks plugin;
	private String playerName;
	private RobotnikController robotnikController;
	private State state; public State getState() { return state; }
	private long lastExecutionTime = -1;
	
	private ArrayList<StackFrame> stack;
	private Function rootFunction = null;
	private int maxStack;
	private SpoutBlock pc = null;
	private EquationCalculator equationCalculator;
	private int stepsWithoutSleep = 0;
	private int maxStepsWithoutSleep = 10;
	
	public Process(CodeBlocks plugin, RobotnikController robotnikController, String playerName) {
		this.plugin = plugin;
		this.robotnikController = robotnikController;
		this.playerName = playerName;
		
		maxStack = plugin.getConfiguration().getMaxStackSize();
		equationCalculator = new EquationCalculator(plugin);
		
		state = State.READY;
		robotnikController.setExecuting(false);
		
	}
	
	private void loadMainFunction() {
		//Reset stack
		if (stack == null) stack = new ArrayList<StackFrame>();
		stack.clear();
		robotnikController.getRobotnik().combineBlocks();
		
		//Get function from player workspace
		Function mainFunction = plugin.getWorkspace().getPlayerWorkspace(playerName).getFunction(robotnikController.getFunctionName());
		rootFunction = mainFunction;
		
		//If function was found, add it to the stack
		if (mainFunction != null) stack.add(new StackFrame(mainFunction));
		
		state = State.READY;
		
		if (pc != null) { pc.setCustomBlock(null); pc.setTypeId(0, false); }
		pc = null;
		
		robotnikController.setExecuting(false);
		
	}
	//===================================================

	//===================================================
	public void run() {
		if (state == State.READY) {
			loadMainFunction();
			
			state = State.RUNNING;
			robotnikController.setExecuting(true);
			
			lastExecutionTime = System.currentTimeMillis();
			
		}
		
	}

	public void stop() {
		if (state == State.RUNNING || state == State.SUSPENDED) {
			loadMainFunction();
			
			lastExecutionTime = -1;
			
		}
		
	}

	public void pause() {
		if (state == State.RUNNING) {
			state = State.SUSPENDED;
			robotnikController.setExecuting(false);
			lastExecutionTime = -1;
			
		}
		
	}

	public void resume() {
		if (state == State.SUSPENDED) {
			state = State.RUNNING;
			robotnikController.setExecuting(true);
			lastExecutionTime = System.currentTimeMillis();
			
		}
		
	}
	//===================================================
	
	public void step() {
		if (state == State.RUNNING) {
			//Check if enough time has passed
			if (System.currentTimeMillis() - lastExecutionTime >= robotnikController.getSpeed()) {
				lastExecutionTime = System.currentTimeMillis();
				
				if (stack.size() == 0) { functionEnd(); lastExecutionTime = -1; return; }
				
				//Get command
				Command command = stack.get(stack.size() - 1).getCommand();
				if (command == null) { functionEnd(); lastExecutionTime = -1; return; }
				
				//Check if robot has power and is alive
				if (brokenOrNoPower()) return;
				
				//Set PC counter if needed
				updatePC(command);
				
				//--------------------------------------------------------------------------
				if (command instanceof ForCommand) {
					executeForCommand(command);
					//robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;
					stepsWithoutSleep++; if (stepsWithoutSleep <= maxStepsWithoutSleep) step(); else stepsWithoutSleep = 0;
					
				//--------------------------------------------------------------------------
				} else if (command instanceof CheckCountCommand) {
					executeCheckCountCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;

				//--------------------------------------------------------------------------
				} else if (command instanceof FunctionCallCommand) {
					executeFunctionCallCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;
					stepsWithoutSleep++; if (stepsWithoutSleep <= maxStepsWithoutSleep) step(); else stepsWithoutSleep = 0;
					
				//--------------------------------------------------------------------------
				} else if (command instanceof AttackCommand) {
					executeAttackCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostAttack());
					
				//--------------------------------------------------------------------------
				} else if (command instanceof InteractionCommand) {
					executeInteractionCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostInteraction());
					
				//--------------------------------------------------------------------------
				} else if (command instanceof JumpCommand) {
					executeJumpCommand(command);
					//robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;
					stepsWithoutSleep++; if (stepsWithoutSleep <= maxStepsWithoutSleep) step(); else stepsWithoutSleep = 0;
					
				//--------------------------------------------------------------------------
				} else if (command instanceof MathCommand) {
					executeMathCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostMath());
					lastExecutionTime = -1;
					
				//--------------------------------------------------------------------------
				} else if (command instanceof MovementCommand) {
					executeMovementCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostMovement());
					
				//--------------------------------------------------------------------------
				} else if (command instanceof IfCommand) {
					executeIfCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;
					stepsWithoutSleep++; if (stepsWithoutSleep <= maxStepsWithoutSleep) step(); else stepsWithoutSleep = 0;

				//--------------------------------------------------------------------------
				} else if (command instanceof CaseCommand) {
					executeCaseCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;					
					stepsWithoutSleep++; if (stepsWithoutSleep <= maxStepsWithoutSleep) step(); else stepsWithoutSleep = 0;
					
				//--------------------------------------------------------------------------
				} else if (command instanceof WhileCommand) {
					executeWhileCommand(command);
					robotnikController.getRobotnik().consumePower(plugin.getConfiguration().getPowerCostBasic());
					lastExecutionTime = -1;
					
				}
				
			}
			
		}
		
	}
	
	private boolean brokenOrNoPower() {
		if (!robotnikController.getRobotnik().hasPower()) {
			plugin.getLog().sendPlayerWarn(playerName, "Robot " + robotnikController.getName() + " [" + robotnikController.getId() + "] has ran out of power. Pausing execution.");
			pause();
			return true;

		} else if (!robotnikController.getRobotnik().isAlive()) {
			plugin.getLog().sendPlayerWarn(playerName, "Robot " + robotnikController.getName() + " [" + robotnikController.getId() + "] is broken. Stopping execution.");
			loadMainFunction();
			return true;

		}
		
		return false;
		
	}

	//================================================================================================
	private void executeWhileCommand(Command command) {
		if (evaluate((WhileCommand) command)) {
			//True - Skip False Jump
			stack.get(stack.size() - 1).incrementPc(1);
			
		}//False Jump next command
		
	}

	//================================================================================================
	private void executeCaseCommand(Command command) {
		if (evaluate((CaseCommand) command)) {
			//True - Skip False Jump
			stack.get(stack.size() - 1).incrementPc(1);
			
		}//False Jump next command
		
	}

	//================================================================================================
	private void executeIfCommand(Command command) {
		if (!evaluate((IfCommand) command)) {
			//FALSE - Move to False Jump command
			stack.get(stack.size() - 1).incrementPc(1);
			
		}//TRUE Jump is next command otherwise
		
	}

	//================================================================================================
	private void executeMovementCommand(Command command) {
		Direction direction = ((MovementCommand) command).getDirection();
		String variableDirection = ((MovementCommand) command).getVariableDirection();
		
		if (variableDirection != null) {
			String dir = getValue(stack.get(stack.size() - 1), "var:" + variableDirection);
			if (dir == null) dir = variableDirection;
			if (dir.startsWith("side:")) dir = dir.replace("side:", "").toLowerCase();
			
			if (dir.equalsIgnoreCase(Direction.BACK.toString())) direction = Direction.BACK;
			else if (dir.equalsIgnoreCase(Direction.DOWN.toString())) direction = Direction.DOWN;
			else if (dir.equalsIgnoreCase(Direction.LEFT.toString())) direction = Direction.LEFT;
			else if (dir.equalsIgnoreCase(Direction.RIGHT.toString())) direction = Direction.RIGHT;
			else if (dir.equalsIgnoreCase(Direction.UP.toString())) direction = Direction.UP;
			else direction = Direction.FORWARD;
			
		}
			
		//Record for achieves
		plugin.getWorkspace().getPlayerWorkspace(playerName).getAchievements().recordMove(direction);

		switch (direction) {
		case BACK: robotnikController.getRobotnik().moveBack(); break;
		case DOWN: robotnikController.getRobotnik().moveDown(); break;
		case FORWARD: robotnikController.getRobotnik().moveForward(); break;
		case LEFT: robotnikController.getRobotnik().moveLeft(); break;
		case RIGHT: robotnikController.getRobotnik().moveRight(); break;
		case UP: robotnikController.getRobotnik().moveUp(); break;
		case TURN_LEFT: robotnikController.getRobotnik().turnLeft(); break;
		case TURN_RIGHT: robotnikController.getRobotnik().turnRight(); break;
		}
		
	}

	//================================================================================================
	private void executeMathCommand(Command command) {
		String variable = ((MathCommand) command).getVariable();
		String equation = ((MathCommand) command).getEquation();
		
		String result = equationCalculator.evaluate(equation, stack.get(stack.size() - 1), robotnikController);
		
		plugin.getLog().sendPlayerWarn(playerName, variable + " = " + equation + " = " + result);
		
		try { Integer.parseInt(result); result = "int:" + result; } catch (NumberFormatException e) {
			if (result.equalsIgnoreCase("true") || result.equalsIgnoreCase("false")) result = "bool:" + result;
			else if (getSide(result) != null) result = "side:" + result;
			
		}
		
		switch (variable) {
		case "arg1": stack.get(stack.size() - 1).setArg(0, result); break;
		case "arg2": stack.get(stack.size() - 1).setArg(1, result); break;
		case "arg3": stack.get(stack.size() - 1).setArg(2, result); break;
		case "var1": stack.get(stack.size() - 1).setVar(0, result);	break;
		case "var2": stack.get(stack.size() - 1).setVar(1, result);	break;
		case "var3": stack.get(stack.size() - 1).setVar(2, result); break;
		case "var4": stack.get(stack.size() - 1).setVar(3, result);	break;
		case "var5": stack.get(stack.size() - 1).setVar(4, result);	break;
		case "var6": stack.get(stack.size() - 1).setVar(5, result);	break;
		case "var7": stack.get(stack.size() - 1).setVar(6, result);	break;
		case "var8": stack.get(stack.size() - 1).setVar(7, result);	break;
		case "var9": stack.get(stack.size() - 1).setVar(8, result);	break;
		case "var10": stack.get(stack.size() - 1).setVar(9, result); break;
		case "fvar": stack.get(stack.size() - 1).setVar(9, result); break;
		case "retval": stack.get(stack.size() - 1).setRetval(result); break;
		}
		
	}

	//================================================================================================
	private void executeJumpCommand(Command command) {
		stack.get(stack.size() - 1).setPc(((JumpCommand) command).getLocation());
		
	}

	//================================================================================================
	private void executeInteractionCommand(Command command) {
		InteractionCommand ic = (InteractionCommand) command;
		
		RobotSide s = RobotSide.front;
		String robotSide = ic.getRobotSide();
		//Try to match it to a side
		try { s = RobotSide.valueOf(robotSide); }
		catch (Exception e) {
			//Failed, maybe it's a variable
			String vs = getValue(stack.get(stack.size() - 1), "var:" + robotSide);
			if (vs != null && vs.startsWith("side:")) {
				//We got a side from the variable, let's set it
				robotSide = vs.replace("side:", "");
				try { s = RobotSide.valueOf(robotSide); } catch (Exception e2) {}
				
			}
			
		}//If all fails - default remains front side
		
		switch (ic.getInteraction()) {
		case BUILD: robotnikController.getRobotnik().buildTry(s, ic.getMaterial(), ic.getData()); break;
		case DIG: robotnikController.getRobotnik().digTry(s); break;
		case DESTROY: robotnikController.getRobotnik().destroyTry(s); break;
		case PICKUP: robotnikController.getRobotnik().pickUpTry(s); break;
		case PLACE: robotnikController.getRobotnik().placeTry(s); break;
			
		}
		
	}

	//================================================================================================
	private void executeAttackCommand(Command command) {
		AttackCommand ac = (AttackCommand) command;
		
		RobotSide s = RobotSide.front;
		String robotSide = ac.getRobotSide();
		
		//Try to match it to a side
		try { s = RobotSide.valueOf(robotSide); }
		catch (Exception e) {
			//Failed, maybe it's a variable
			String vs = getValue(stack.get(stack.size() - 1), "var:" + robotSide);
			if (vs != null && vs.startsWith("side:")) {
				//We got a side from the variable, let's set it
				robotSide = vs.replace("side:", "");
				try { s = RobotSide.valueOf(robotSide); } catch (Exception e2) {}
				
			}
			
		}//If all fails - default remains front side
		
		if (ac.isNear()) robotnikController.getRobotnik().smash(ac.getTargetType());
		else robotnikController.getRobotnik().shoot(s, ac.getTargetType());
		
	}

	//================================================================================================
	private void executeFunctionCallCommand(Command command) {
		FunctionCallCommand fcc = (FunctionCallCommand) command;
		
		//Get function from player workspace
		String fn = fcc.getFunctionName();
		String[] values = {"", "", "", ""};
		
		values[0] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
		values[1] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
		values[2] = fn.substring(0, fn.indexOf("#")); fn = fn.substring(fn.indexOf("#")+1);
		values[3] = fn;
		
		Function newFunction = plugin.getWorkspace().getPlayerWorkspace(playerName).getFunction(values[0]);
		StackFrame newFrame = new StackFrame(newFunction);
		
		if (values[1].startsWith("var:")) newFrame.setArg(0, getValue(stack.get(stack.size() - 1), values[1]));
		else newFrame.setArg(0, values[1]);
		
		if (values[2].startsWith("var:")) newFrame.setArg(1, getValue(stack.get(stack.size() - 1), values[2]));
		else newFrame.setArg(1, values[2]);
		
		if (values[3].startsWith("var:")) newFrame.setArg(2, getValue(stack.get(stack.size() - 1), values[3]));
		else newFrame.setArg(2, values[3]);
		
		//If function was found, add it to the stack
		if (newFunction != null && stack.size() <= maxStack ) stack.add(newFrame);
		
	}

	//================================================================================================
	private void executeCheckCountCommand(Command command) {
		Integer count = stack.get(stack.size() - 1).look();
		if (count != null && count > 0) {
			//Skip the jump to the end of for loop command, thus running the loop
			stack.get(stack.size() - 1).incrementPc(1);
			stack.get(stack.size() - 1).decrement(1);
			
		} else {
			//Otherwise we move on to jump end command and stop executing the loop
			stack.get(stack.size() - 1).pop(); // remove counter
			
		}
		
	}

	//================================================================================================
	private void executeForCommand(Command command) {
		ForCommand fc = (ForCommand) command;
		
		//Add counter to the stack
		String count = fc.getCount();
		try { stack.get(stack.size() - 1).push(Integer.parseInt(count)); }
		catch (NumberFormatException e) {
			count = getValue(stack.get(stack.size() - 1), "var:" + count);
			
			if (count != null && count.startsWith("int:")) count = count.replace("int:", "");
			else count = "1";
			
			try { stack.get(stack.size() - 1).push(Integer.parseInt(count)); }
			catch (NumberFormatException e2) { stack.get(stack.size() - 1).push(1); }
			
		}
		
	}

	//================================================================================================
	private void updatePC(Command command) {
		if (rootFunction == stack.get(stack.size() - 1).getFunction() && robotnikController.getDebugBase() != null) {
			CodeBlock codeBlock = rootFunction.getFunctionBlocks().get(command.getCodeBlockNumber());
			
			if (codeBlock != null) {
				//Get new PC location
				SpoutBlock newPC = (SpoutBlock) robotnikController.getDebugBase().getBlock().getLocation().add(codeBlock.getOffsetX(), codeBlock.getOffsetY()+2, codeBlock.getOffsetZ()).getBlock();
				
				//Old pc exists and it's not the same block - clear old pc and set new pc to newPC
				if (pc != null && pc.getLocation().distance(newPC.getLocation()) >= 1) { pc.setCustomBlock(null); pc.setTypeId(0, false); pc = newPC; }
				
				//PC is null
				if (pc == null) { pc = newPC; }
				
				//Update pc to pc block
				if (pc.getType() == Material.AIR) {
					pc.setCustomBlock(plugin.getBlocks().getProgramCounterBlock());

				}
				
			}
			
		}
		
	}

	//================================================================================================
	private RobotSide getSide(String v1) {
		if (v1 == null) return null;
		
		if (v1.equalsIgnoreCase(RobotSide.around.toString())) return RobotSide.around;
		if (v1.equalsIgnoreCase(RobotSide.back.toString())) return RobotSide.back;
		if (v1.equalsIgnoreCase(RobotSide.down.toString())) return RobotSide.down;
		if (v1.equalsIgnoreCase(RobotSide.front.toString())) return RobotSide.front;
		if (v1.equalsIgnoreCase(RobotSide.inventory.toString())) return RobotSide.inventory;
		if (v1.equalsIgnoreCase(RobotSide.left.toString())) return RobotSide.left;
		if (v1.equalsIgnoreCase(RobotSide.right.toString())) return RobotSide.right;
		if (v1.equalsIgnoreCase(RobotSide.top.toString())) return RobotSide.top;
		
		if (v1.equalsIgnoreCase(FunctionVariables.arg1.toString())) return getSide(stack.get(stack.size() - 1).getArgs()[0]);
		if (v1.equalsIgnoreCase(FunctionVariables.arg2.toString())) return getSide(stack.get(stack.size() - 1).getArgs()[1]);
		if (v1.equalsIgnoreCase(FunctionVariables.arg3.toString())) return getSide(stack.get(stack.size() - 1).getArgs()[2]);
		
		if (v1.equalsIgnoreCase(FunctionVariables.var1.toString())) return getSide(stack.get(stack.size() - 1).getVars()[0]);
		if (v1.equalsIgnoreCase(FunctionVariables.var2.toString())) return getSide(stack.get(stack.size() - 1).getVars()[1]);
		if (v1.equalsIgnoreCase(FunctionVariables.var3.toString())) return getSide(stack.get(stack.size() - 1).getVars()[2]);
		if (v1.equalsIgnoreCase(FunctionVariables.var4.toString())) return getSide(stack.get(stack.size() - 1).getVars()[3]);
		if (v1.equalsIgnoreCase(FunctionVariables.var5.toString())) return getSide(stack.get(stack.size() - 1).getVars()[4]);
		if (v1.equalsIgnoreCase(FunctionVariables.var6.toString())) return getSide(stack.get(stack.size() - 1).getVars()[5]);
		if (v1.equalsIgnoreCase(FunctionVariables.var7.toString())) return getSide(stack.get(stack.size() - 1).getVars()[6]);
		if (v1.equalsIgnoreCase(FunctionVariables.var8.toString())) return getSide(stack.get(stack.size() - 1).getVars()[7]);
		if (v1.equalsIgnoreCase(FunctionVariables.var9.toString())) return getSide(stack.get(stack.size() - 1).getVars()[8]);
		if (v1.equalsIgnoreCase(FunctionVariables.var10.toString())) return getSide(stack.get(stack.size() - 1).getVars()[9]);
		
		if (v1.equalsIgnoreCase(FunctionVariables.fvar.toString())) return getSide(stack.get(stack.size() - 1).getVars()[10]);
		
		if (v1.equalsIgnoreCase(FunctionVariables.retvar.toString())) return getSide(stack.get(stack.size() - 1).getRetval());
		
		return null;
		
	}

	//================================================================================================
	private String getValue(StackFrame stackFrame, String value) {
		switch (value) {
		case "var:arg1": return stackFrame.getArgs()[0];
		case "var:arg2": return stackFrame.getArgs()[1];
		case "var:arg3": return stackFrame.getArgs()[2];
		case "var:var1": return stackFrame.getVars()[0];
		case "var:var2": return stackFrame.getVars()[1];
		case "var:var3": return stackFrame.getVars()[2];
		case "var:var4": return stackFrame.getVars()[3];
		case "var:var5": return stackFrame.getVars()[4];
		case "var:var6": return stackFrame.getVars()[5];
		case "var:var7": return stackFrame.getVars()[6];
		case "var:var8": return stackFrame.getVars()[7];
		case "var:var9": return stackFrame.getVars()[8];
		case "var:var10": return stackFrame.getVars()[9];
		case "var:fvar": return stackFrame.getVars()[10];
		case "var:retval": return stackFrame.getRetval();
		}
		
		return null;
		
	}

	//================================================================================================
	private int getInt(String value) {
		try { return Integer.parseInt(value); }
		catch (NumberFormatException e) {
			String varValue = getValue(stack.get(stack.size() - 1), "var:"+value);
			if (varValue == null) return 1;
			
			try { return Integer.parseInt(varValue); }
			catch (NumberFormatException e2) { return 1; }
			
		}
		
	}
	
	private byte getByte(String value) {
		try { return Byte.parseByte(value); }
		catch (NumberFormatException e) {
			String varValue = getValue(stack.get(stack.size() - 1), "var:"+value);
			if (varValue == null) return -1;
			
			try { return Byte.parseByte(varValue); }
			catch (NumberFormatException e2) { return -1; }
			
		}
		
	}
	
	private String getTarget(String value) {
		boolean isNot = false;
		if (value.startsWith("!")) { isNot = true; value = value.substring(1); }
		
		for (FunctionVariables fv : FunctionVariables.values()) {
			if (fv.toString().equalsIgnoreCase(value)) {
				String varValue = getValue(stack.get(stack.size() - 1), "var:"+value);
				if (varValue == null) {
					if (isNot) return "!0";
					else return "0";
					
				} else {
					if (isNot) return "!" + varValue;
					else return varValue;
					
				}
				
			}
			
		}
		
		return value;
			
	}
	
	private boolean evaluate(TrueFalseCommand command) {
		boolean retval;
		
		//Extract values
		System.out.println(command.getSide() + " " + command.getTargetType() + " " + command.getDistance() + " " + command.getData() + " before============= " + this);
		
		RobotSide side = getSide(command.getSide()); if (side == null) side = RobotSide.front;
		String targetType = getTarget(command.getTargetType()).replaceAll("int:", "");
		int distance = getInt(command.getDistance());
		byte data = getByte(command.getData());
		
		System.out.println(side + " " + targetType + " " + distance + " " + data + " ============= " + this);
		
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
		
		return retval;
		
	}

	private void functionEnd() {
		//Only one function on stack, thus we have reached the end of main function
		if (stack.size() <= 1) {
			if (rootFunction != null) plugin.getLog().sendPlayerNormal(playerName, "Robot " + robotnikController.getName() + " [" + robotnikController.getId() + "] has finished executing function " + rootFunction.getName());
			loadMainFunction();
		}
		//Otherwise step up one step on the stack
		else {
			String retval = stack.get(stack.size() - 1).getRetval();
			stack.remove(stack.size() - 1);
			stack.get(stack.size() - 1).setArg(10, retval);
			
		}
		
	}

}
