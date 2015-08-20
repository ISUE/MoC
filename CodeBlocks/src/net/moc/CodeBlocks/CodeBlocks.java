package net.moc.CodeBlocks;

import net.moc.CodeBlocks.SQL.CodeBlocksSQL;
import net.moc.CodeBlocks.blocks.AllBlocks;
import net.moc.CodeBlocks.workspace.RobotnikController;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class CodeBlocks extends JavaPlugin {
	//------------------------------------------------------------------------------
    private CodeBlocksListener eventListener;
	private CodeBlocksCommandExecutor commandExecutor; public CodeBlocksCommandExecutor getcommandExecutor() { return this.commandExecutor; }
	private CodeBlocksLogHandler log; public CodeBlocksLogHandler getLog() { return this.log; }
	private CodeBlocksConfig config; public CodeBlocksConfig getConfiguration() { return this.config; }
	//------------------------------------------------------------------------------
	private AllBlocks blocks; public AllBlocks getBlocks() { return this.blocks; }
	private CodeBlocksSQL sql; public CodeBlocksSQL getSQL() { return sql; }
	private CodeBlocksWorkspace workspace; public CodeBlocksWorkspace getWorkspace() { return this.workspace; }
	private CodeBlocksGUI gui; public CodeBlocksGUI getGUI() { return this.gui; }
	//------------------------------------------------------------------------------
    private String PERMISSION_ALL = "CodeBlocks.All";
    public String getPERMISSION_ALL() { return this.PERMISSION_ALL; }
    
    private boolean isDebug = true;
    public boolean isDebug() { return this.isDebug; }
    
    private String blockImageURL = "http://isue-server.eecs.ucf.edu/codeblocks2/images/";
    //private String blockImageURL = "http://minecraft.dmg2.net/CodeBlocks/";
    public String getBlockImageURL() { return this.blockImageURL; }
	//------------------------------------------------------------------------------
	private CodeBlocksUseLog useLog; public CodeBlocksUseLog getUseLogger() { return useLog; }
	//------------------------------------------------------------------------------

    public void onEnable() {
    	//Log
    	log = new CodeBlocksLogHandler(this);
    	
    	//Configuration
    	config = new CodeBlocksConfig(this);
    	
    	//Commands handler
    	commandExecutor = new CodeBlocksCommandExecutor(this);
    	getCommand("cb").setExecutor(this.commandExecutor);
    	getCommand("cbf").setExecutor(this.commandExecutor);
    	getCommand("cbr").setExecutor(this.commandExecutor);
    	getCommand("cbb").setExecutor(this.commandExecutor);
    	
    	//Events handler
    	eventListener = new CodeBlocksListener(this);
    	PluginManager pm = this.getServer().getPluginManager();
    	pm.registerEvents(this.eventListener, this);
    	
    	//Database
    	sql = new CodeBlocksSQL(this);
    	
    	//Blocks
    	blocks = new AllBlocks(this);
    	
    	//Workspace
    	workspace = new CodeBlocksWorkspace(this);
    	pm.registerEvents(this.workspace.getListener(), this);
    	
    	//GUI
    	gui = new CodeBlocksGUI(this);
    	pm.registerEvents(this.gui.getListener(), this);
    	
    	//Usage Logger
    	useLog = new CodeBlocksUseLog(this);
    	
    }
    
    public void onDisable() { save(); useLog.submitLog(); }
    
    public void save() {
    	for (String playerName : this.workspace.getPlayerWorkspaces().keySet()) sql.saveRobotHistory(playerName);
    	for (String playerName : this.workspace.getPlayerWorkspaces().keySet()) {
    		for (RobotnikController rc : workspace.getPlayerWorkspace(playerName).getRobotniks()) {
    			sql.saveRobotnik(playerName, rc);
    		}
    		
    	}
    	
    }
    
}