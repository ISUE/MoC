package net.moc.CodeBlocks.workspace;

import java.util.ArrayList;
import org.getspout.spoutapi.material.CustomBlock;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.attack.AttackBaseBlock;
import net.moc.CodeBlocks.blocks.attack.AttackFarBlock;
import net.moc.CodeBlocks.blocks.attack.AttackNearBlock;
import net.moc.CodeBlocks.blocks.function.BranchBlock;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.IfFalseBlock;
import net.moc.CodeBlocks.blocks.function.IfTrueBlock;
import net.moc.CodeBlocks.blocks.function.WhiteSpaceBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.interaction.BuildBlock;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.InteractionBaseBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.MathBaseBlock;
import net.moc.CodeBlocks.blocks.math.MathBlock;
import net.moc.CodeBlocks.blocks.math.SetBlock;
import net.moc.CodeBlocks.blocks.movement.BackBlock;
import net.moc.CodeBlocks.blocks.movement.DownBlock;
import net.moc.CodeBlocks.blocks.movement.ForwardBlock;
import net.moc.CodeBlocks.blocks.movement.LeftBlock;
import net.moc.CodeBlocks.blocks.movement.MoveBlock;
import net.moc.CodeBlocks.blocks.movement.MovementBaseBlock;
import net.moc.CodeBlocks.blocks.movement.RightBlock;
import net.moc.CodeBlocks.blocks.movement.TurnLeftBlock;
import net.moc.CodeBlocks.blocks.movement.TurnRightBlock;
import net.moc.CodeBlocks.blocks.movement.UpBlock;
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
import net.moc.CodeBlocks.workspace.command.WhileCommand;
import net.moc.CodeBlocks.workspace.command.InteractionCommand.Interaction;
import net.moc.CodeBlocks.workspace.command.MovementCommand.Direction;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;

public class Compiler {
	//==============================================================================================
	public Compiler (CodeBlocks plugin) {}
	//==============================================================================================
	
	//==============================================================================================
	public ArrayList<Command> compile(Function function) {
		ArrayList<Command> code = new ArrayList<Command>();
		ArrayList<CodeBlock> functionBlocks = function.getFunctionBlocks();
		
		block(functionBlocks, code, 0);
		
		code.add(null); //End of the code marker
		
		return code;
			}
	//==============================================================================================

	//==============================================================================================
	private int block(ArrayList<CodeBlock> functionBlocks, ArrayList<Command> code, int i) {
		for ( ; i < functionBlocks.size() ; i++) {
			CodeBlock block = functionBlocks.get(i);
			
			if (block == null) return i;
			
			CustomBlock customBlock = block.getCustomBlock();
			
			//White space
			if (customBlock instanceof WhiteSpaceBlock) { continue; }
			
			//Bases
			if (customBlock instanceof BranchBlock) { continue; }
			if (customBlock instanceof AttackBaseBlock) { continue; }
			if (customBlock instanceof InteractionBaseBlock) { continue; }
			if (customBlock instanceof MovementBaseBlock) { continue; }
			if (customBlock instanceof MathBaseBlock) { continue; }
			
			//Math
			if (customBlock instanceof SetBlock) {
				String baseEquation = block.getValues().getFunctionName();
				ArrayList<String> evals = getEvals(functionBlocks, i+1);
				
				//Replace all $b1$ type things with evaluate functions
				for (int n = 0 ; n < evals.size() ; n++) baseEquation = baseEquation.replaceAll("#b"+(n+1)+"#", evals.get(n));
				
				try { code.add(new MathCommand(baseEquation.split("@")[0], baseEquation.split("@")[1])); code.get(code.size() - 1).setCodeBlockNumber(i); }
				catch (ArrayIndexOutOfBoundsException e) { code.add(new MathCommand(baseEquation.split("=")[0], "1")); code.get(code.size() - 1).setCodeBlockNumber(i); }
				
				//Skip math branch
				while (block != null) { i++; block = functionBlocks.get(i);}
				continue;
				
			}
			//Skip any other math blocks
			if (customBlock instanceof MathBlock) { continue; }
			
			//Attack
			if (customBlock instanceof AttackFarBlock) { code.add(new AttackCommand(block.getValues().getInteractionDirection(), block.getValues().getTrueTargetType(), false)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof AttackNearBlock) { code.add(new AttackCommand(block.getValues().getInteractionDirection(), block.getValues().getTrueTargetType(), true)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			
			//Interaction
			if (customBlock instanceof BuildBlock) { code.add(new InteractionCommand(block.getValues().getBuildLocation().toString(), block.getValues().getBuildBlockType(), block.getValues().getBuildBlockData())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof DestroyBlock) { code.add(new InteractionCommand(Interaction.DESTROY, block.getValues().getInteractionDirection())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof DigBlock) { code.add(new InteractionCommand(Interaction.DIG, block.getValues().getInteractionDirection())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof PickUpBlock) { code.add(new InteractionCommand(Interaction.PICKUP, block.getValues().getInteractionDirection())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof PlaceBlock) { code.add(new InteractionCommand(Interaction.PLACE, block.getValues().getInteractionDirection())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			
			//Movement
			if (customBlock instanceof MoveBlock) { code.add(new MovementCommand(block.getValues().getInteractionDirection())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof BackBlock) { code.add(new MovementCommand(Direction.BACK)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof DownBlock) { code.add(new MovementCommand(Direction.DOWN)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof ForwardBlock) { code.add(new MovementCommand(Direction.FORWARD)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof LeftBlock) { code.add(new MovementCommand(Direction.LEFT)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof RightBlock) { code.add(new MovementCommand(Direction.RIGHT)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof TurnLeftBlock) { code.add(new MovementCommand(Direction.TURN_LEFT)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof TurnRightBlock) { code.add(new MovementCommand(Direction.TURN_RIGHT)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			if (customBlock instanceof UpBlock) { code.add(new MovementCommand(Direction.UP)); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }
			
			//Branches
			if (customBlock instanceof CallFunctionBlock) { code.add(new FunctionCallCommand(block.getValues().getFunctionName())); code.get(code.size() - 1).setCodeBlockNumber(i); continue; }

			if (customBlock instanceof ForBlock) {
				//Add for command, set end in code later
				code.add(new ForCommand(block.getValues().getCounter()));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				int forStart = code.size();
				code.add(new CheckCountCommand());
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//Add end of the for loop jump command, set correct location later
				int endJump = code.size();
				code.add(new JumpCommand(code.size()));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//Read in the for block
				i++; i = block(functionBlocks, code, i);
				
				//Add jump back to the start of the loop
				code.add(new JumpCommand(forStart));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//Update out of loop jump location
				((JumpCommand) code.get(endJump)).setLocation(code.size());
				
				continue;
				
			}
			
			if (customBlock instanceof IfBlock) {
				//Add If command
				code.add(new IfCommand(block.getValues().getTrueTargetType(), block.getValues().getTrueData(), block.getValues().getTrueSide(), block.getValues().getTrueDistance()));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//True branch jump command
				int trueJumpCommandLocation = code.size();
				code.add(new JumpCommand(code.size() + 2));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//False branch jump command
				int falseJumpCommandLocation = code.size();
				code.add(new JumpCommand(code.size() + 2));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//Get next block
				i++; block = functionBlocks.get(i);
				
				if (block != null && block.getCustomBlock() instanceof IfTrueBlock) {
					//True branch
					//Set jump location
					((JumpCommand)code.get(trueJumpCommandLocation)).setLocation(code.size());
					
					//Compile the code block
					i++; i = block(functionBlocks, code, i);
					
					//Add end of true branch jump command
					int endIfJumpCommandLocation = code.size();
					code.add(new JumpCommand(code.size() + 1));
					code.get(code.size() - 1).setCodeBlockNumber(i);
					
					//Set false jump location
					((JumpCommand)code.get(falseJumpCommandLocation)).setLocation(code.size());
					
					//Check if we have false block
					if (functionBlocks.get(i+1) != null && functionBlocks.get(i+1).getCustomBlock() instanceof IfFalseBlock) {
						//False branch
						//Compile the code block
						i += 2; i = block(functionBlocks, code, i);
						
					} else {
						i += 1;
						
					}

					//Update jump location from true branch
					((JumpCommand)code.get(endIfJumpCommandLocation)).setLocation(code.size());
					
				} else if (block != null && block.getCustomBlock() instanceof IfFalseBlock) {
					//False branch
					//Set jump location
					((JumpCommand)code.get(falseJumpCommandLocation)).setLocation(code.size());
					
					//Compile the code block
					i++; i= block(functionBlocks, code, i);
					
					//Add end of false branch jump command
					int endIfJumpCommandLocation = code.size();
					code.add(new JumpCommand(code.size() + 1));
					code.get(code.size() - 1).setCodeBlockNumber(i);

					//Set true jump location
					((JumpCommand)code.get(trueJumpCommandLocation)).setLocation(code.size());
					
					//Check if we have true block
					if (functionBlocks.get(i+1) != null && functionBlocks.get(i+1).getCustomBlock() instanceof IfTrueBlock) {
						//True branch
						//Compile the code block
						i += 2; i = block(functionBlocks, code, i);
						
					} else {
						i += 1;
						
					}
					
					//Update jump location from true branch
					((JumpCommand)code.get(endIfJumpCommandLocation)).setLocation(code.size());
					
				}
				
				continue;
				
			}
			
			if (customBlock instanceof SwitchBlock) {
				//Move to the next block - should be case
				i++;
				
				int caseDoneJumpLocation = -1;
				
				//Run through all cases
				while (i < functionBlocks.size() && functionBlocks.get(i) != null && functionBlocks.get(i).getCustomBlock() instanceof CaseBlock) {
					//Get the case block
					block = functionBlocks.get(i);
					
					//Create new case command with case block's properties
					code.add(new CaseCommand(block.getValues().getTrueTargetType(), block.getValues().getTrueData(), block.getValues().getTrueSide(), block.getValues().getTrueDistance()));
					code.get(code.size() - 1).setCodeBlockNumber(i);
					
					//Add FALSE case jump location - Skip
					int skipCaseLocation = code.size();
					code.add(new JumpCommand(code.size()));//Fix location later
					code.get(code.size() - 1).setCodeBlockNumber(i);

					//Read in the case block
					i++; i = block(functionBlocks, code, i);
					
					//Update exit jump from a previous case if needed 
					if (caseDoneJumpLocation != -1) { ((JumpCommand)code.get(caseDoneJumpLocation)).setLocation(code.size()); }
					
					//Record new case exit jump location
					caseDoneJumpLocation = code.size();
					
					//Add jump out of the whole switch command once a case was executed.
					code.add(new JumpCommand(code.size() + 1)); //By default just jumps to the next command, fixed in the next iteration if needed
					code.get(code.size() - 1).setCodeBlockNumber(i);
					
					//Update false jump location
					((JumpCommand)code.get(skipCaseLocation)).setLocation(code.size());
					
					//Move to next block and check if there is another case block
					i++;
					
				}
				
				//Back up so not to consume the block
				i--;
				
				continue;
				
			}
			
			if (customBlock instanceof WhileBlock) {
				//Create new while command with block's properties
				int whileStart = code.size();
				code.add(new WhileCommand(block.getValues().getTrueTargetType(), block.getValues().getTrueData(), block.getValues().getTrueSide(), block.getValues().getTrueDistance()));
				code.get(code.size() - 1).setCodeBlockNumber(i);

				int jumpEnd = code.size();
				code.add(new JumpCommand(code.size() + 1));//Update this later to a proper location
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//get while loop commands
				i++; i = block(functionBlocks, code, i);
				
				//Jump back command
				code.add(new JumpCommand(whileStart));
				code.get(code.size() - 1).setCodeBlockNumber(i);
				
				//Update jump end
				((JumpCommand)code.get(jumpEnd)).setLocation(code.size());
				
				continue;
				
			}
			
		}
		
		return i;
		
	}
	//==============================================================================================

	private ArrayList<String> getEvals(ArrayList<CodeBlock> functionBlocks, int i) {
		ArrayList<String> bes = new ArrayList<String>();
		
		while (functionBlocks.get(i) != null) {
			CodeBlock cb = functionBlocks.get(i);
			i++;
			CustomBlock cust = cb.getCustomBlock();
			
			if (cust instanceof EvaluateBlock) {
				bes.add(cb.getValues().getFunctionName());
				
			}
			
		}
		
		return bes;
		
	}
	
}
