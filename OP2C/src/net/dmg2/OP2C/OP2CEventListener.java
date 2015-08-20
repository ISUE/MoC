package net.dmg2.OP2C;

import java.util.Calendar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class OP2CEventListener implements Listener {
	private OP2C plugin;
	private Calendar calendar;
	public OP2CEventListener(OP2C plugin) { this.plugin = plugin; calendar = Calendar.getInstance(); }
	
	//============================================================================
	@EventHandler
	public void onEvent (PlayerJoinEvent event) {
		log("Player " + event.getPlayer().getName() + " joined the server.");
		
		if (!event.getPlayer().isOp()) return;
		
		setCreative(event.getPlayer());
		
	}
	
	@EventHandler
	public void onEvent (PlayerChangedWorldEvent event) {
		log("Player " + event.getPlayer().getName() + " changed world. From " + event.getFrom() + " to " + event.getPlayer().getWorld().getName());
		
		if (!event.getPlayer().isOp()) return;
		
		setCreative(event.getPlayer());
		
	}
	
//	@EventHandler  (priority = EventPriority.MONITOR)
//	public void onEvent (BlockBreakEvent event) {
//		if (event.isCancelled()) return;
//		event.getBlock().setTypeIdAndData(0, (byte) 0, false);
//		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND_BOOTS, 2));
//		
//	}
	//============================================================================
	//============================================================================
	@EventHandler
	public void onEvent (PlayerQuitEvent event) { log("Player " + event.getPlayer().getName() + " left the server."); }
	
	@EventHandler
	public void onEvent (PlayerGameModeChangeEvent event) { log("Player " + event.getPlayer().getName() + " changed gamemode."); }
	
	@EventHandler
	public void onEvent (ServerCommandEvent event) { log("Sender " + event.getSender().getName() + " issued command " + event.getCommand()); }
	
	@EventHandler
	public void onEvent (ServerListPingEvent event) { log("PING from " + event.getAddress()); }
	
	@EventHandler
	public void onEvent (AsyncPlayerChatEvent event) { log("Player " + event.getPlayer().getName() + " said: " + event.getMessage()); }
	//============================================================================
	private void setCreative(Player player) {
		final String playerName = player.getName();

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable() { public void run() { Bukkit.getServer().getPluginManager().callEvent(new OP2CEvent(playerName)); } }, 20L);
		
	}
	//============================================================================

	
	
	//============================================================================
	@EventHandler
	public void onEvent (OP2CEvent event) {
		Player player = plugin.getServer().getPlayer(event.getPlayerName());
		if (player == null) return;
		
		player.setGameMode(GameMode.CREATIVE);
		player.sendMessage(ChatColor.AQUA + "Game mode set to creative.");
		
	}
	//============================================================================
//	private ArrayList<BlockBreakEvent> breakQueue = new ArrayList<BlockBreakEvent>();
//	private ArrayList<Block> blockQueue = new ArrayList<Block>();
//	@EventHandler
//	public void onEvent (PlayerMoveEvent event) {
//		for (Block b : blockQueue) b.setType(Material.AIR);
//		blockQueue.clear();
//		
//	}
//	
//	@EventHandler
//	public void onEvent (BlockBreakEvent event) {
//		if (breakQueue.contains(event)) {
//			breakQueue.remove(event);
//			if (!event.isCancelled()) blockQueue.add(event.getBlock());
//			return;
//			
//		}
//		
//		if (event.isCancelled()) return;
//		
//		Player player = event.getPlayer();
//		
//		if (!player.isOp()) return;
//		
//		if (player.getItemInHand().getType() == Material.BEDROCK) {
//			for (int x = -10 ; x <= 10 ; x++) {
//				for (int y = -10 ; y <= 10 ; y++) {
//					for (int z = -10 ; z <= 10 ; z++) {
//						if (x == 0 && y == 0 & z == 0) continue;
//						
//						Block theBlock = event.getBlock().getLocation().add(x, y, z).getBlock();
//						BlockBreakEvent newEvent = new BlockBreakEvent(theBlock, player);
//						this.breakQueue.add(newEvent);
//						Bukkit.getServer().getPluginManager().callEvent(newEvent);
//						
//					}
//					
//				}
//				
//			}
//			
//		}
//		
//	}
//	//============================================================================
	
	private void log(String info) {
		String time = calendar.get(Calendar.YEAR) + " " +
					  (calendar.get(Calendar.MONTH)+1) + " " +
					  calendar.get(Calendar.DAY_OF_MONTH) + " " +
					  calendar.get(Calendar.HOUR_OF_DAY) + ":" + 
					  calendar.get(Calendar.MINUTE) + ":" + 
					  calendar.get(Calendar.SECOND);
		
		plugin.getLog().info("[" + time + "] " + info);
		
	}
}
