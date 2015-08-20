package net.dmg2.LevelPlayerStats;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LPSEventListener implements Listener {
	private int levelHPMultiplier = 20;

	public LPSEventListener(LevelPlayerStats plugin) {}
	
	@EventHandler
	public void onEvent (PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		player.setMaxHealth((player.getLevel() + 1) * levelHPMultiplier);
		
		
	}
	
	@EventHandler
	public void onEvent (PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		player.setMaxHealth((player.getLevel() + 1) * levelHPMultiplier);
		
	}
	
	@EventHandler
	public void onEvent (PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		player.setMaxHealth((player.getLevel() + 1) * levelHPMultiplier);
		
	}
	
	@EventHandler
	public void onEvent (EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		
		if (e instanceof Player) {
			Player p = (Player) e;
			event.setDamage(event.getDamage() * (p.getLevel() + 1));
			
		}
		
	}

}
