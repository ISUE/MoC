package moc.DreamCrafter.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryHandler {
	public static void restorePlayerInventory(Player player, Inventory inventory) {
		if(inventory != null) {
			player.getInventory().clear();
			player.getInventory().setContents(inventory.getContents());
			
		}
		
	}
	
	public static Inventory savePlayerInventory(Player player) {
		Inventory inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
		inventory.setContents(player.getInventory().getContents());
		return inventory;
		
	}
	
}
