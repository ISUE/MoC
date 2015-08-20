package net.dmg2.DynamicMOTD;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DynamicMOTDPlayerListener implements Listener {

	//============================================================
	private DynamicMOTD plugin;
	public DynamicMOTDPlayerListener(DynamicMOTD plugin) {
		this.plugin = plugin;
	}
	//============================================================

	//#######################################################################################################################
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent event) {
		event.getPlayer();
		plugin.log.sendPlayerNormal(event.getPlayer(), "[" + plugin.config.getString("dmotd.owner") + "] " + ChatColor.AQUA + plugin.config.getString("dmotd.msg"));
	}

}
