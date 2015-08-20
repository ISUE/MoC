package net.dmg2.ChangeSign;

import org.bukkit.plugin.java.JavaPlugin;

public class ChangeSign extends JavaPlugin{
	//=============================================================================================
	private ChangeSignLogHandler log; public ChangeSignLogHandler getLog() { return log; }
	private ChangeSignCommandExecutor commandExecutor;
	//=============================================================================================

	public void onEnable() {
    	//Log
    	this.log = new ChangeSignLogHandler(this);

    	//Commands handler
    	this.commandExecutor = new ChangeSignCommandExecutor(this);
    	this.getCommand("sign").setExecutor(this.commandExecutor);
    	
	}

	public void onDisable() {
	}

}
