package net.moc.MOCDreamCatcher.GUI;

import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.GUI.widgets.MOCComboBoxSelectionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Screen;

public class GUIEventListener implements Listener {
	//=============================================
	private MOCDreamCatcher plugin; public MOCDreamCatcher getPlugin() { return plugin; }
	public GUIEventListener(MOCDreamCatcher plugin) { this.plugin = plugin; }
	//=============================================

	//*****************************************************************************
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		Screen screen = event.getScreen();
		
		if(screen instanceof AwakeWindow) ((AwakeWindow) screen).onClick(event.getButton());	
		else if(screen instanceof NewThoughtWindow) ((NewThoughtWindow) screen).onClick(event.getButton());	
		else if(screen instanceof ThoughtWindow) ((ThoughtWindow) screen).onClick(event.getButton());	
		else if(screen instanceof DreamWindow) ((DreamWindow) screen).onClick(event.getButton());	
		else if(screen instanceof SurveyCreationWindow) ((SurveyCreationWindow) screen).onClick(event.getButton());	
		else if(screen instanceof SurveyDreamWindow) ((SurveyDreamWindow) screen).onClick(event.getButton());	
		else if(screen instanceof HelpWindow) ((HelpWindow) screen).onClick(event.getButton());	
		else if(screen instanceof AdminWindow) ((AdminWindow) screen).onClick(event.getButton());	
		
	}
	
	//*****************************************************************************
	@EventHandler
	public void onEvent(MOCComboBoxSelectionEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof AwakeWindow) { ((AwakeWindow) screen).onSelection(event.getListWidget()); }
		else if(screen instanceof AdminWindow) ((AdminWindow) screen).onSelection(event.getListWidget());	
		
	}

}
