package moc.DreamCrafter.gui.widget;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.Rectangle;

public class MOCListWidgetMS extends GenericListWidget {
	private ArrayList<ListWidgetItem> selectedItems = new ArrayList<ListWidgetItem>();
	
	public void onSelected(int item, boolean doubleClick) {
		ListWidgetItem selection = getItem(item);
		//See if new selection is already in the list
		if (this.selectedItems.contains(selection)) {
			//It was, remove it
			this.selectedItems.remove(selection);
			selection.setTitle("");
			
		} else {
			//It wasn't, add it
			this.selectedItems.add(selection);
			selection.setTitle("[Selected]");
		}

		setDirty(true);
		Bukkit.getServer().getPluginManager().callEvent(new MOCComboBoxSelectionEvent(this.getScreen().getPlayer(), this.getScreen(), this, "New selection"));
		
	}
	
	public ArrayList<String> getSelectedItems() {
		ArrayList<String> retval = new ArrayList<String>();
		
		for (ListWidgetItem nextItem : this.selectedItems) {
			retval.add(nextItem.getText());
		}
		
		return retval;
		
	}
	
	public ListWidget clearSelection() {
		for (ListWidgetItem item : getItems()) {
			item.setTitle("");
		}
		this.selectedItems.clear();
		setSelection(-1);
		return this;
	}
	
	public void ensureVisible(Rectangle rect) {}
	
}
