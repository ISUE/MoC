package net.moc.MOCRater.GUI.Widgets;

import org.bukkit.Bukkit;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericComboBox;

public class MOCComboBox extends GenericComboBox {
	public void onSelectionChanged(int i, String text) {
		//Update the text, since Generic one does not do this.
		this.setText(text);
		this.setDirty(true);
		
		//Throw fake button click event to update the windows
		Bukkit.getServer().getPluginManager().callEvent(new ButtonClickEvent(this.getScreen().getPlayer(), this.getScreen(), this));
		
	}

}
