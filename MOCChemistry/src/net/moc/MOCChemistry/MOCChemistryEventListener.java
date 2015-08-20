package net.moc.MOCChemistry;

import net.moc.MOCChemistry.Blocks.Energy;
import net.moc.MOCChemistry.Blocks.EnergyHarvester;
import net.moc.MOCChemistry.Blocks.ChemistryTable;
import net.moc.MOCChemistry.GUI.ChemistryBookWindow;
import net.moc.MOCChemistry.GUI.ChemistryRecipeEditor;
import net.moc.MOCChemistry.GUI.ChemistryTableWindow;
import net.moc.MOCChemistry.GUI.Event.MOCComboBoxSelectionEvent;
import net.moc.MOCChemistry.GUI.Event.SlotChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.slot.SlotExchangeEvent;
import org.getspout.spoutapi.event.slot.SlotPutEvent;
import org.getspout.spoutapi.event.slot.SlotShiftClickEvent;
import org.getspout.spoutapi.event.slot.SlotTakeEvent;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCChemistryEventListener implements Listener{
	//=====================================================
	private MOCChemistry plugin;
	public MOCChemistryEventListener(MOCChemistry plugin) { this.plugin = plugin; }
	//=====================================================
	
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerQuitEvent event) { this.plugin.getGui().removePlayerWindows(event.getPlayer()); }
	@EventHandler
	public void onEvent(PlayerKickEvent event) { this.plugin.getGui().removePlayerWindows(event.getPlayer()); }
	//##############################################################################################
	@EventHandler
	public void onEvent(ButtonClickEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof ChemistryTableWindow) { ((ChemistryTableWindow) screen).onClick(event.getButton()); }
		if(screen instanceof ChemistryBookWindow) { ((ChemistryBookWindow) screen).onClick(event.getButton()); }
		if(screen instanceof ChemistryRecipeEditor) { ((ChemistryRecipeEditor) screen).onClick(event.getButton()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(MOCComboBoxSelectionEvent event) {
		//Get the screen for the event
		Screen screen = event.getScreen();
		
		//See what kind of screen it is
		if(screen instanceof ChemistryBookWindow) { ((ChemistryBookWindow) screen).onSelection(event.getListWidget()); }
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		
		//Check permission
		if(!event.getPlayer().hasPermission("MOCChemistry.use")) return;
		try {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ((SpoutBlock) event.getClickedBlock()).getCustomBlock() instanceof ChemistryTable) {
				//Open Chemistry Table GUI
				this.plugin.getGui().displayChemistryTableGUI((SpoutPlayer) event.getPlayer());
				event.setCancelled(true);
				return;

			}
			
		} catch (Exception e) {}
		
	}
	//##############################################################################################
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEvent(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		
		//Check permission
		if(!event.getPlayer().hasPermission("MOCChemistry.use")) return;
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutPlayer player = (SpoutPlayer) event.getPlayer();
		
		//Ignore some blocks
		if (block.getCustomBlock() instanceof Energy ||
			block.getCustomBlock() instanceof EnergyHarvester ||
			block.getCustomBlock() instanceof ChemistryTable)
			return;
		
		SpoutItemStack item = new SpoutItemStack(event.getPlayer().getItemInHand());
		if (item.getMaterial() != null && this.plugin.getEnergyHarvesterBlock().getBlockItem().getName().equalsIgnoreCase(item.getMaterial().getName())) {
			int dropAmount = (int)(Math.random() * 100) % 2;
			for (int i = 1 ; i <= dropAmount ; i++) { player.getWorld().dropItemNaturally(block.getLocation(), new SpoutItemStack(this.plugin.getEnergyBlock(), 1)); }
			
			// >:)
			this.plugin.getAccident().harvestAccident(player);
			
		}
		
	}
	//##############################################################################################
	@EventHandler
	public void onEvent(ScreenCloseEvent event) {
		Screen screen = event.getScreen();
		if(screen instanceof ChemistryTableWindow) { ((ChemistryTableWindow) screen).clearInputSyncInventory(); }
		
	}
	
	@EventHandler
	public void onEvent(SlotShiftClickEvent event) {
		Screen screen = event.getPlayer().getMainScreen().getActivePopup();
		if (screen instanceof ChemistryTableWindow) {
			final String playerName = event.getPlayer().getName();
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() { Bukkit.getServer().getPluginManager().callEvent(new SlotChangeEvent(playerName)); }
			}, 5L);
			
		}
		
	}
	
	@EventHandler
	public void onEvent(SlotExchangeEvent event) {
		Screen screen = event.getPlayer().getMainScreen().getActivePopup();
		if (screen instanceof ChemistryTableWindow) {
			final String playerName = event.getPlayer().getName();
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() { Bukkit.getServer().getPluginManager().callEvent(new SlotChangeEvent(playerName)); }
			}, 5L);
			
		}
		
	}
	
	@EventHandler
	public void onEvent(SlotPutEvent event) {
		Screen screen = event.getPlayer().getMainScreen().getActivePopup();
		if (screen instanceof ChemistryTableWindow) {
			final String playerName = event.getPlayer().getName();
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() { Bukkit.getServer().getPluginManager().callEvent(new SlotChangeEvent(playerName)); }
			}, 5L);
			
		}
		
	}
	
	@EventHandler
	public void onEvent(SlotTakeEvent event) {
		Screen screen = event.getPlayer().getMainScreen().getActivePopup();
		if (screen instanceof ChemistryTableWindow) {
			final String playerName = event.getPlayer().getName();
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() { Bukkit.getServer().getPluginManager().callEvent(new SlotChangeEvent(playerName)); }
			}, 5L);
			
		}
		
	}
	
	@EventHandler
	public void onEvent(SlotChangeEvent event) { this.plugin.getGui().getChemistryTableWindow((SpoutPlayer) this.plugin.getServer().getPlayer(event.getPlayerName())).onSlotChange(); }
	
}
