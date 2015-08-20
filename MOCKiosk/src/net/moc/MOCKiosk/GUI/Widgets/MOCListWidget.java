package net.moc.MOCKiosk.GUI.Widgets;

import org.bukkit.Bukkit;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.Rectangle;

public class MOCListWidget extends GenericListWidget {
	private ListWidgetItem lastSelection = null;
	
	public void onSelected(int item, boolean doubleClick) {
		if (this.lastSelection == null) {
			this.lastSelection = getItem(item);
			
		} else {
			if (this.lastSelection == getItem(item)) {
				//Clear selection
				clearSelection();
				setDirty(true);
			} else {
				this.lastSelection = getItem(item);
			}
			
		}
		
		//Create the click event
		Bukkit.getServer().getPluginManager().callEvent(new MOCComboBoxSelectionEvent(this.getScreen().getPlayer(), this.getScreen(), this, "New selection"));
		
	}
	
	public ListWidget clearSelection() {
		this.lastSelection = null;
		setSelection(-1);
		return this;
	}
	
	public void ensureVisible(Rectangle rect) {}

}
