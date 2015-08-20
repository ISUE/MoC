package net.moc.MOCDreamCatcher.GUI;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.Data.DreamPlayer;

public class GUI {
	//=============================================================
	private MOCDreamCatcher plugin;
	private GUIEventListener listener; public GUIEventListener getListener() { return listener; }

	private HashMap<Player, AwakeWindow> awakeWindows = new HashMap<Player, AwakeWindow>();
	private HashMap<Player, DreamWindow> dreamWindows = new HashMap<Player, DreamWindow>();
	private HashMap<Player, ThoughtWindow> thoughtWindows = new HashMap<Player, ThoughtWindow>();
	private HashMap<Player, NewThoughtWindow> newThoughtWindows = new HashMap<Player, NewThoughtWindow>();
	private HashMap<Player, SurveyCreationWindow> surveyCreationWindows = new HashMap<Player, SurveyCreationWindow>();
	private HashMap<Player, SurveyDreamWindow> surveyDreamWindows = new HashMap<Player, SurveyDreamWindow>();
	private HashMap<Player, HelpWindow> helpWindows = new HashMap<Player, HelpWindow>();
	private HashMap<Player, AdminWindow> adminWindows = new HashMap<Player, AdminWindow>();
	
	//=============================================================
	public GUI(MOCDreamCatcher plugin) { this.plugin = plugin; listener = new GUIEventListener(plugin); }

	//=============================================================
	public void displayGUI(Player player) {
		//Outside, Dreaming, Editing
		DreamPlayer dreamPlayer = plugin.getDreamNet().getPlayer(player.getName());
		
		if (dreamPlayer == null) { displayAwakeWindow(player); return; }
		
		switch (dreamPlayer.getState()) {
		case AWAKE:
			displayAwakeWindow(player);
			break;
		case DREAMING:
			displayDreamWindow(player);
			break;
		case EDITING:
			displayThoughtWindow(player);
			break;
		}
		
	}
	
	//==========================================================================
	private void displayThoughtWindow(Player player) {
		if (!thoughtWindows.containsKey(player)) { thoughtWindows.put(player, new ThoughtWindow((SpoutPlayer)player, plugin)); }
		thoughtWindows.get(player).open();
		
	}

	//==========================================================================
	private void displayDreamWindow(Player player) {
		if (!dreamWindows.containsKey(player)) { dreamWindows.put(player, new DreamWindow((SpoutPlayer)player, plugin)); }
		dreamWindows.get(player).open();
		
	}

	//==========================================================================
	private void displayAwakeWindow(Player player) {
		if (!awakeWindows.containsKey(player)) { awakeWindows.put(player, new AwakeWindow((SpoutPlayer)player, plugin)); }
		awakeWindows.get(player).open();
		
	}

	//==========================================================================
	public void displayNewThoughtWindow(Player player) {
		if (!newThoughtWindows .containsKey(player)) { newThoughtWindows.put(player, new NewThoughtWindow((SpoutPlayer)player, plugin)); }
		newThoughtWindows.get(player).open();
		
	}

	//==========================================================================
	public void displaySurveyCreation(Player player) {
		if (!surveyCreationWindows .containsKey(player)) { surveyCreationWindows.put(player, new SurveyCreationWindow((SpoutPlayer)player, plugin)); }
		surveyCreationWindows.get(player).open();
		
	}

	//==========================================================================
	public void displaySurveyDream(Player player) {
		if (!surveyDreamWindows .containsKey(player)) { surveyDreamWindows.put(player, new SurveyDreamWindow((SpoutPlayer)player, plugin)); }
		surveyDreamWindows.get(player).open();
		
	}
	
	//==========================================================================
	public void displayHelpWindow(Player player) {
		if (!helpWindows .containsKey(player)) { helpWindows.put(player, new HelpWindow((SpoutPlayer)player, plugin)); }
		helpWindows.get(player).open();
		
	}

	//==========================================================================
	public void displayAdminWindow(Player player) {
		if (!adminWindows .containsKey(player)) { adminWindows.put(player, new AdminWindow((SpoutPlayer)player, plugin)); }
		adminWindows.get(player).open();
		
	}
	
}
