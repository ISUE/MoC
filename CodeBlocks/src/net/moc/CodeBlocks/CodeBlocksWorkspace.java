package net.moc.CodeBlocks;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.block.SpoutBlock;
import net.moc.CodeBlocks.workspace.Compiler;
import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.Parser;
import net.moc.CodeBlocks.workspace.PlayerWorkspace;
import net.moc.CodeBlocks.workspace.RobotnikController;
import net.moc.CodeBlocks.workspace.Shifter;
import net.moc.CodeBlocks.workspace.command.Command;
import net.moc.CodeBlocks.workspace.events.WorkspaceListener;
import net.moc.CodeBlocks.workspace.parts.CodeBlock;
import net.moc.CodeBlocks.workspace.parts.Directive;

public class CodeBlocksWorkspace {
	private CodeBlocks plugin;
	
	private Parser parser;
	private Shifter shifter; public Shifter getCodeBlocksShifter() { return shifter; }
	private Compiler compiler;
	
	private HashMap<String, PlayerWorkspace> playersWorkspace; public HashMap<String, PlayerWorkspace> getPlayerWorkspaces() { return playersWorkspace; }
	private ArrayList<Directive> directives; public ArrayList<Directive> getDirectives() { return directives; }
	
	private WorkspaceListener listener; public WorkspaceListener getListener() { return listener; }
	
	public CodeBlocksWorkspace(CodeBlocks plugin) {
		this.plugin = plugin;
		
		listener = new WorkspaceListener(plugin);
    	
		shifter = new Shifter();
		parser = new Parser(plugin);
		compiler = new Compiler(plugin);
		
		playersWorkspace = new HashMap<String, PlayerWorkspace>();
		
		loadDirectives();
		
	}
	
	private void loadDirectives() {
		directives = new ArrayList<Directive>();
		
		for (String d : plugin.getConfiguration().getListDirectives()) {
			if (plugin.getConfiguration().isDirectiveEnabled(d)) {
				String data = plugin.getConfiguration().getDirectiveData(d);
				directives.add(new Directive(d, data));
				
			}
			
		}
		
	}

	public ArrayList<CodeBlock> parse(SpoutBlock block) { return parser.parse(block, block.getLocation()); }
	
	public ArrayList<Command> compile(Function function, Player player) { return compiler.compile(function); }
	
	public void loadPlayerWorkspace(String playerName) {
		if (playersWorkspace.containsKey(playerName)) return;
		
		PlayerWorkspace pw =  new PlayerWorkspace(plugin, playerName);
		playersWorkspace.put(playerName, pw);
		
	}
	
	public RobotnikController getRobotnikController(Block block) {
		for (String  name : playersWorkspace.keySet()) {
			RobotnikController rc = playersWorkspace.get(name).getRobotnik(block);
			if (rc != null) return rc;
			
		}
		
		return null;
		
	}
	
	public PlayerWorkspace getPlayerWorkspace(String playerName) { loadPlayerWorkspace(playerName); return playersWorkspace.get(playerName); }

}
