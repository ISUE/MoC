package net.moc.CodeBlocks.workspace.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.workspace.RobotnikController;

public class WorkspaceListener implements Listener {
	//=============================================
	private CodeBlocks plugin;
	private HashMap<BlockBreakEvent, RobotnikAction> robotTryQueue; public HashMap<BlockBreakEvent, RobotnikAction> getRobotTryQueue() { return robotTryQueue; }
	private ArrayList<FunctionTryAction> functionTryQueue; public ArrayList<FunctionTryAction> getFunctionTryQueue() { return functionTryQueue; }
	
	public WorkspaceListener(CodeBlocks plugin) {
		this.plugin = plugin;
		robotTryQueue = new HashMap<BlockBreakEvent, RobotnikAction>();
		functionTryQueue = new ArrayList<FunctionTryAction>();
		
	}
	//=============================================
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(VirtualMachineTickEvent event) {
		//Execute next VM tick
		this.plugin.getWorkspace().getPlayerWorkspace(event.getPlayerName()).getVirtualMachine().tick();
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(ItemSpawnEvent event) {
		//Make sure we got something useful
		if (event.isCancelled()) return; if (event.getEntityType() != EntityType.DROPPED_ITEM) return;
		Item item = null; if (event.getEntity() instanceof Item) item = event.getEntity(); else return;
		if (item == null) return;
		
		//Get location
		Location location = event.getLocation();
		
		//Find a matching robot
		for ( String  playerName : plugin.getWorkspace().getPlayerWorkspaces().keySet()) {
			RobotnikController rc = plugin.getWorkspace().getPlayerWorkspace(playerName).getRobotnik(location.getBlock(), 5);
			
			if (rc != null) { rc.getRobotnik().executeDirectives(item); break; }
			
		}
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(PlayerJoinEvent event) { plugin.getWorkspace().loadPlayerWorkspace(event.getPlayer().getName()); }
	
	//*****************************************************************************
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(BlockBreakEvent event) {
		if (robotTryQueue.containsKey(event)) {
			//Our event
			if (event.isCancelled()) {
				//Cancelled, robot not allowed to perform action
				robotTryQueue.remove(event);
				
			} else {
				RobotnikAction action = robotTryQueue.get(event);
				
				if (action.getInteraction() == null) {
					//Movement command
					action.getRobotnik().moveRobotToCan(action.getLocation().getWorld().getBlockAt(action.getLocation()));
					
				} else {
					//Interaction command
					switch (action.getInteraction()) {
					case BUILD:
						action.getRobotnik().buildCan(action.getSide(), action.getMaterial(), action.getData());
						break;
					case DESTROY:
						action.getRobotnik().destroyCan(action.getSide());
						break;
					case DIG:
						action.getRobotnik().digCan(action.getSide());
						break;
					case PICKUP:
						action.getRobotnik().pickUpCan(action.getSide());
						break;
					case PLACE:
						action.getRobotnik().placeCan(action.getSide());
						break;

					}

				}
				
				robotTryQueue.remove(event);
				
			}
			
		} else {
			FunctionTryAction temp = null;
			for (FunctionTryAction fta : functionTryQueue) {
				if (fta.getBaseEvent() == event) {
					//Caught base event
					temp = fta;
					if (event.isCancelled()) fta.setBaseStatus(0);
					else fta.setBaseStatus(1);
					break;
					
				} else if (fta.getValueEvent() == event) {
					temp = fta;
					if (event.isCancelled()) fta.setValueStatus(0);
					else fta.setValueStatus(1);
					break;
					
				}
				
			}
			
			if (temp != null) {
				if (temp.getBaseStatus() == 0 && temp.getValueStatus() == 0) {
					//Both were cancelled - function try failed
					functionTryQueue.remove(temp);
				} else if (temp.getBaseStatus() == 1 && temp.getValueStatus() == 1) {
					//Both went through ok - call paste can
					temp.getFunction().pasteCan(temp.getBaseBlock(), temp.getValueBlock(), temp.getCodeBlock());
					//Remove temp
					functionTryQueue.remove(temp);
					
				}
				
			}
			
		}
		
	}
	//*****************************************************************************
}
