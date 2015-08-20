package net.moc.MOCKiosk;

import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

public class MOCKioskKeyListener implements BindingExecutionDelegate {

	//==============================================================
	private MOCKiosk plugin;
	public MOCKioskKeyListener(MOCKiosk plugin) {
		this.plugin = plugin;
	}
	//==============================================================
	
	public void keyPressed(KeyBindingEvent event) {
		//Make sure no windows are open
		if (event.getScreenType() != ScreenType.GAME_SCREEN) return;
		
		//Open the GUI
		this.plugin.getGui().displayBrowserWindowGUI(event.getPlayer());

	}

	public void keyReleased(KeyBindingEvent event) {}

}