package net.moc.MOCChemistry;

import org.bukkit.util.Vector;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MOCChemistryAccident {
	MOCChemistry plugin;
	public MOCChemistryAccident(MOCChemistry plugin) { this.plugin = plugin; }
	
	public void harvestAccident(SpoutPlayer player) {
		damage(player, plugin.getConfiguration().getHavrestDamageChance(), plugin.getConfiguration().getHavrestDamageAmount());
		punt(player, plugin.getConfiguration().getHavrestPuntChance(), plugin.getConfiguration().getHavrestPuntAmount());
		
	}
	
	public void splitAccident(SpoutPlayer player) {
		damage(player, plugin.getConfiguration().getSplitDamageChance(), plugin.getConfiguration().getSplitDamageAmount());
		punt(player, plugin.getConfiguration().getSplitPuntChance(), plugin.getConfiguration().getSplitPuntAmount());
		
	}
	
	public void combineAccident(SpoutPlayer player) {
		damage(player, plugin.getConfiguration().getCombineDamageChance(), plugin.getConfiguration().getCombineDamageAmount());
		punt(player, plugin.getConfiguration().getCombinePuntChance(), plugin.getConfiguration().getCombinePuntAmount());
		
	}

	//============================================================================================================
	private void damage(SpoutPlayer player, int chance, int damagePercent) {
		int roll = (int)(Math.random() * 100);
		if (roll <= chance) {
			player.damage(damagePercent * 1.0);
			//this.plugin.getLog().sendPlayerWarn(player, "Chemical reaction caused you to take some damage!");
			
		}
		
	}

	private void punt(SpoutPlayer player, int chance, double distance) {
		int roll = (int)(Math.random() * 100);
		if (roll <= chance) { 
			player.setVelocity(Vector.getRandom().multiply(distance));
			//this.plugin.getLog().sendPlayerWarn(player, "Chemical reaction produced too much energy! You are pushed away by the great force!");
			
		}
		
	}
	//============================================================================================================

//	private void teleport(SpoutPlayer player, int chance, int distance) {
//		int roll = (int)(Math.random() * 100);
//		if (roll <= chance) player.teleport(new Location(player.getWorld(), player.getLocation().getX() + (distance * 2 * (Math.random() - 0.5)),
//																			player.getLocation().getY() + (distance * 2 * (Math.random() - 0.5)),
//																			player.getLocation().getZ() + (distance * 2 * (Math.random() - 0.5))));
//		
//	}

}
