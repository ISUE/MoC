package net.moc.MOCRater;

import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

public class MOCRaterKeyListener implements BindingExecutionDelegate {

	//==============================================================
	private MOCRater plugin;
	public MOCRaterKeyListener(MOCRater plugin) {
		this.plugin = plugin;
	}
	//==============================================================
	
	public void keyPressed(KeyBindingEvent event) {
		//Make sure no windows are open
		if (event.getScreenType() != ScreenType.GAME_SCREEN) return;
		
		//Open the GUI
		this.plugin.gui.displayRatingWindowGUI(event.getPlayer(), true, null);

	}

	public void keyReleased(KeyBindingEvent event) {}

}
