package net.moc.MOCChemistry.GUI;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TestWindow extends GenericPopup {
	private SpoutPlayer player;
	private GenericItemWidget[] slotOutput = new GenericItemWidget[4];

	public TestWindow(SpoutPlayer player, JavaPlugin plugin) {
        this.player = player;
        
		for (int i = 0 ; i < slotOutput.length ; i++) {
			slotOutput[i] = new GenericItemWidget(new ItemStack(Material.STONE));
			slotOutput[i].setX(245 + (i % 2) * 21);
			slotOutput[i].setY(45 + (i / 2) * 21);
			slotOutput[i].setWidth(20);
			slotOutput[i].setHeight(20);
			attachWidget(plugin, slotOutput[i]);
			
			//slotOutput[i].setTypeId(0);
			
		}

	}
	
	public void open() {
		this.player.getMainScreen().attachPopupScreen(this);
		
	}
	
}
