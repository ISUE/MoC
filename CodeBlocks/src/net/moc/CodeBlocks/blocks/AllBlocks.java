package net.moc.CodeBlocks.blocks;

import java.util.ArrayList;
import org.getspout.spoutapi.material.CustomBlock;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.blocks.attack.AttackBaseBlock;
import net.moc.CodeBlocks.blocks.attack.AttackFarBlock;
import net.moc.CodeBlocks.blocks.attack.AttackNearBlock;
import net.moc.CodeBlocks.blocks.function.BranchBlock;
import net.moc.CodeBlocks.blocks.function.CallFunctionBlock;
import net.moc.CodeBlocks.blocks.function.CaseBlock;
import net.moc.CodeBlocks.blocks.function.ForBlock;
import net.moc.CodeBlocks.blocks.function.FunctionBlock;
import net.moc.CodeBlocks.blocks.function.IfBlock;
import net.moc.CodeBlocks.blocks.function.IfFalseBlock;
import net.moc.CodeBlocks.blocks.function.IfTrueBlock;
import net.moc.CodeBlocks.blocks.function.SwitchBlock;
import net.moc.CodeBlocks.blocks.function.WhileBlock;
import net.moc.CodeBlocks.blocks.function.WhiteSpaceBlock;
import net.moc.CodeBlocks.blocks.interaction.BuildBlock;
import net.moc.CodeBlocks.blocks.interaction.DestroyBlock;
import net.moc.CodeBlocks.blocks.interaction.DigBlock;
import net.moc.CodeBlocks.blocks.interaction.InteractionBaseBlock;
import net.moc.CodeBlocks.blocks.interaction.PickUpBlock;
import net.moc.CodeBlocks.blocks.interaction.PlaceBlock;
import net.moc.CodeBlocks.blocks.math.EvaluateBlock;
import net.moc.CodeBlocks.blocks.math.MathBaseBlock;
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

public class AllBlocks {
	private CodeBlocks plugin;

	//All blocks
	ArrayList<CustomBlock> allBlocks = new ArrayList<CustomBlock>();
	
	//Misc
	private ToolItem toolItem;
    private RobotBlock robotBlock;
    private ProgramCounterBlock programCounterBlock;
    private PointerBlock pointerBlock;
    
    //Bases
    private BranchBlock branchBlock;
    private InteractionBaseBlock interactionBaseBlock;
    private MovementBaseBlock movementBaseBlock;
    private MathBaseBlock mathBaseBlock; 
    private AttackBaseBlock attackBaseBlock;
    
    //Functions
    private CallFunctionBlock callFunctionBlock;
    private CaseBlock caseBlock;
    private ForBlock forBlock;
    private FunctionBlock functionBlock;
    private IfBlock ifBlock;
    private IfFalseBlock ifFalseBlock;
    private IfTrueBlock ifTrueBlock;
    private SwitchBlock switchBlock;
    private WhileBlock whileBlock;
    private WhiteSpaceBlock whiteSpaceBlock;
    
    //Interactions
    private BuildBlock buildBlock;
    private DestroyBlock destroyBlock;
    private DigBlock digBlock;
    private PickUpBlock pickUpBlock;
    private PlaceBlock placeBlock;
    
    //Math
    private SetBlock setBlock;
    private EvaluateBlock evaluateBlock;
    
    //Movements
    private MoveBlock moveBlock;
    private BackBlock backBlock;
    private DownBlock downBlock;
    private ForwardBlock forwardBlock;
    private LeftBlock leftBlock;
    private RightBlock rightBlock;
    private TurnLeftBlock turnLeftBlock;
    private TurnRightBlock turnRightBlock;
    private UpBlock upBlock;
    
    //Attack
    private AttackNearBlock attackNearBlock;
    private AttackFarBlock attackFarBlock;
    
    
    public AllBlocks(CodeBlocks plugin) { this.plugin = plugin; createBlocks(); }

    private void createBlocks() {
    	toolItem = new ToolItem(plugin);
    	
    	pointerBlock = new PointerBlock(plugin);
    	allBlocks.add(pointerBlock);
    	
		programCounterBlock = new ProgramCounterBlock(plugin);
		allBlocks.add(programCounterBlock);
		
		whiteSpaceBlock = new WhiteSpaceBlock(plugin);
		allBlocks.add(whiteSpaceBlock);
		
		robotBlock = new RobotBlock(plugin);
		robotBlock.setFullRotate(true);
		allBlocks.add(robotBlock);
		
		branchBlock = new BranchBlock(plugin);
		allBlocks.add(branchBlock);
		
	    attackBaseBlock = new AttackBaseBlock(plugin);
		allBlocks.add(attackBaseBlock);
		
	    interactionBaseBlock = new InteractionBaseBlock(plugin);
		allBlocks.add(interactionBaseBlock);
		
	    movementBaseBlock = new MovementBaseBlock(plugin);
		allBlocks.add(movementBaseBlock);
		
		mathBaseBlock = new MathBaseBlock(plugin);
		allBlocks.add(mathBaseBlock);
		
		caseBlock = new CaseBlock(plugin);
		allBlocks.add(caseBlock);
		
		forBlock = new ForBlock(plugin);
		allBlocks.add(forBlock);
		
		callFunctionBlock = new CallFunctionBlock(plugin);
		allBlocks.add(callFunctionBlock);
		
		functionBlock = new FunctionBlock(plugin);
		allBlocks.add(functionBlock);
		
		ifBlock = new IfBlock(plugin);
		allBlocks.add(ifBlock);
		
		ifFalseBlock = new IfFalseBlock(plugin);
		allBlocks.add(ifFalseBlock);
		
		ifTrueBlock = new IfTrueBlock(plugin);
		allBlocks.add(ifTrueBlock);
		
		switchBlock = new SwitchBlock(plugin);
		allBlocks.add(switchBlock);
		
		whileBlock = new WhileBlock(plugin);
		allBlocks.add(whileBlock);
		
		buildBlock = new BuildBlock(plugin);
		allBlocks.add(buildBlock);
		
		destroyBlock = new DestroyBlock(plugin);
		allBlocks.add(destroyBlock);
		
		digBlock = new DigBlock(plugin);
		allBlocks.add(digBlock);
		
		pickUpBlock = new PickUpBlock(plugin);
		allBlocks.add(pickUpBlock);
		
		placeBlock = new PlaceBlock(plugin);
		allBlocks.add(placeBlock);

		
		moveBlock = new MoveBlock(plugin);
		allBlocks.add(moveBlock);
		
		backBlock = new BackBlock(plugin);
		allBlocks.add(backBlock);
		
		downBlock = new DownBlock(plugin);
		allBlocks.add(downBlock);
		
		forwardBlock = new ForwardBlock(plugin);
		allBlocks.add(forwardBlock);
		
		leftBlock = new LeftBlock(plugin);
		allBlocks.add(leftBlock);
		
		rightBlock = new RightBlock(plugin);
		allBlocks.add(rightBlock);
		
		turnLeftBlock = new TurnLeftBlock(plugin);
		allBlocks.add(turnLeftBlock);
		
		turnRightBlock = new TurnRightBlock(plugin);
		allBlocks.add(turnRightBlock);
		
		upBlock = new UpBlock(plugin);
		allBlocks.add(upBlock);
		
	    attackNearBlock = new AttackNearBlock(plugin);
		allBlocks.add(attackNearBlock);
		
	    attackFarBlock = new AttackFarBlock(plugin);
		allBlocks.add(attackFarBlock);
		
		setBlock = new SetBlock(plugin);
		allBlocks.add(setBlock);
		
		evaluateBlock = new EvaluateBlock(plugin);
		allBlocks.add(evaluateBlock);
		
	}

	public ToolItem getToolItem() { return toolItem; }
	public RobotBlock getRobotBlock() { return robotBlock; }
	public PointerBlock getPointerBlock() { return pointerBlock; }
	public ProgramCounterBlock getProgramCounterBlock() { return programCounterBlock; }
	public CaseBlock getCaseBlock() { return caseBlock; }
	public ForBlock getForBlock() { return forBlock; }
	public CallFunctionBlock getCallFunctionBlock() {return callFunctionBlock; }
	public FunctionBlock getFunctionBlock() { return functionBlock; }
	public IfBlock getIfBlock() { return ifBlock; }
	public IfFalseBlock getIfFalseBlock() { return ifFalseBlock; }
	public IfTrueBlock getIfTrueBlock() { return ifTrueBlock; }
	public SwitchBlock getSwitchBlock() { return switchBlock; }
	public WhileBlock getWhileBlock() { return whileBlock; }
	public WhiteSpaceBlock getWhiteSpaceBlock() { return whiteSpaceBlock; }
	public BuildBlock getBuildBlock() { return buildBlock; }
	public DestroyBlock getDestroyBlock() { return destroyBlock; }
	public DigBlock getDigBlock() { return digBlock; }
	public PickUpBlock getPickUpBlock() { return pickUpBlock; }
	public PlaceBlock getPlaceBlock() { return placeBlock; }
	public MoveBlock getMoveBlock() { return moveBlock; }
	public BackBlock getBackBlock() { return backBlock; }
	public DownBlock getDownBlock() { return downBlock; }
	public ForwardBlock getForwardBlock() { return forwardBlock; }
	public LeftBlock getLeftBlock() { return leftBlock; }
	public RightBlock getRightBlock() { return rightBlock; }
	public TurnLeftBlock getTurnLeftBlock() { return turnLeftBlock; }
	public TurnRightBlock getTurnRightBlock() { return turnRightBlock; }
	public UpBlock getUpBlock() { return upBlock; }
	
	public InteractionBaseBlock getInteractionBlock() { return interactionBaseBlock; }
	public MovementBaseBlock getMovementBlock() { return movementBaseBlock; }
	public BranchBlock getBranchBlock() { return branchBlock; }
	
	public MathBaseBlock getMathBlock() { return mathBaseBlock; }
	
	public SetBlock getSetBlock() { return setBlock; }
	public EvaluateBlock getEvaluateBlock() { return evaluateBlock; }
	
	public AttackBaseBlock getAttackBlock() { return attackBaseBlock; }
	public AttackNearBlock getAttackNearBlock() { return attackNearBlock; }
	public AttackFarBlock getAttackFarBlock() { return attackFarBlock; }

	public ArrayList<CustomBlock> getAll() { return allBlocks; }

	public CustomBlock getBlock(String customBlockName) {
		for (CustomBlock cb : allBlocks) if (cb.getName().equalsIgnoreCase(customBlockName)) return cb;
		return null;
		
	}

}
