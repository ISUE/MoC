package net.dmg2.GravitySheep.events;

import net.dmg2.GravitySheep.GravitySheep;
import net.dmg2.GravitySheep.api.GravitySheepRegion;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class GravitySheepBlockListener implements Listener {

	//============================================================
	private GravitySheep plugin;
	public GravitySheepBlockListener(GravitySheep instance) { this.plugin = instance; }
	//============================================================
	//	DROPPED_ITEM is spawnable false ; class interface org.bukkit.entity.Item ; is alive false
	//	FALLING_BLOCK is spawnable false ; class interface org.bukkit.entity.FallingBlock ; is alive false
	//	FIREWORK is spawnable false ; class interface org.bukkit.entity.Firework ; is alive false
	//	SPLASH_POTION is spawnable false ; class interface org.bukkit.entity.ThrownPotion ; is alive false
	//	EGG is spawnable false ; class interface org.bukkit.entity.Egg ; is alive false
	//	FISHING_HOOK is spawnable false ; class interface org.bukkit.entity.Fish ; is alive false
	//	LIGHTNING is spawnable false ; class interface org.bukkit.entity.LightningStrike ; is alive false
	//	WEATHER is spawnable false ; class interface org.bukkit.entity.Weather ; is alive false
	//	PLAYER is spawnable false ; class interface org.bukkit.entity.Player ; is alive true
	//	COMPLEX_PART is spawnable false ; class interface org.bukkit.entity.ComplexEntityPart ; is alive false
	//	UNKNOWN is spawnable false ; class null ; is alive false

	//	EXPERIENCE_ORB is spawnable true ; class interface org.bukkit.entity.ExperienceOrb ; is alive false
	//	PAINTING is spawnable true ; class interface org.bukkit.entity.Painting ; is alive false
	//	ARROW is spawnable true ; class interface org.bukkit.entity.Arrow ; is alive false
	//	SNOWBALL is spawnable true ; class interface org.bukkit.entity.Snowball ; is alive false
	//	FIREBALL is spawnable true ; class interface org.bukkit.entity.LargeFireball ; is alive false
	//	SMALL_FIREBALL is spawnable true ; class interface org.bukkit.entity.SmallFireball ; is alive false
	//	ENDER_PEARL is spawnable true ; class interface org.bukkit.entity.EnderPearl ; is alive false
	//	ENDER_SIGNAL is spawnable true ; class interface org.bukkit.entity.EnderSignal ; is alive false
	//	THROWN_EXP_BOTTLE is spawnable true ; class interface org.bukkit.entity.ThrownExpBottle ; is alive false
	//	ITEM_FRAME is spawnable true ; class interface org.bukkit.entity.ItemFrame ; is alive false
	//	WITHER_SKULL is spawnable true ; class interface org.bukkit.entity.WitherSkull ; is alive false
	//	PRIMED_TNT is spawnable true ; class interface org.bukkit.entity.TNTPrimed ; is alive false
	//	MINECART is spawnable true ; class interface org.bukkit.entity.Minecart ; is alive false
	//	BOAT is spawnable true ; class interface org.bukkit.entity.Boat ; is alive false
	//	CREEPER is spawnable true ; class interface org.bukkit.entity.Creeper ; is alive true
	//	SKELETON is spawnable true ; class interface org.bukkit.entity.Skeleton ; is alive true
	//	SPIDER is spawnable true ; class interface org.bukkit.entity.Spider ; is alive true
	//	GIANT is spawnable true ; class interface org.bukkit.entity.Giant ; is alive true
	//	ZOMBIE is spawnable true ; class interface org.bukkit.entity.Zombie ; is alive true
	//	SLIME is spawnable true ; class interface org.bukkit.entity.Slime ; is alive true
	//	GHAST is spawnable true ; class interface org.bukkit.entity.Ghast ; is alive true
	//	PIG_ZOMBIE is spawnable true ; class interface org.bukkit.entity.PigZombie ; is alive true
	//	ENDERMAN is spawnable true ; class interface org.bukkit.entity.Enderman ; is alive true
	//	CAVE_SPIDER is spawnable true ; class interface org.bukkit.entity.CaveSpider ; is alive true
	//	SILVERFISH is spawnable true ; class interface org.bukkit.entity.Silverfish ; is alive true
	//	BLAZE is spawnable true ; class interface org.bukkit.entity.Blaze ; is alive true
	//	MAGMA_CUBE is spawnable true ; class interface org.bukkit.entity.MagmaCube ; is alive true
	//	ENDER_DRAGON is spawnable true ; class interface org.bukkit.entity.EnderDragon ; is alive true
	//	WITHER is spawnable true ; class interface org.bukkit.entity.Wither ; is alive true
	//	BAT is spawnable true ; class interface org.bukkit.entity.Bat ; is alive true
	//	WITCH is spawnable true ; class interface org.bukkit.entity.Witch ; is alive true
	//	PIG is spawnable true ; class interface org.bukkit.entity.Pig ; is alive true
	//	SHEEP is spawnable true ; class interface org.bukkit.entity.Sheep ; is alive true
	//	COW is spawnable true ; class interface org.bukkit.entity.Cow ; is alive true
	//	CHICKEN is spawnable true ; class interface org.bukkit.entity.Chicken ; is alive true
	//	SQUID is spawnable true ; class interface org.bukkit.entity.Squid ; is alive true
	//	WOLF is spawnable true ; class interface org.bukkit.entity.Wolf ; is alive true
	//	MUSHROOM_COW is spawnable true ; class interface org.bukkit.entity.MushroomCow ; is alive true
	//	SNOWMAN is spawnable true ; class interface org.bukkit.entity.Snowman ; is alive true
	//	OCELOT is spawnable true ; class interface org.bukkit.entity.Ocelot ; is alive true
	//	IRON_GOLEM is spawnable true ; class interface org.bukkit.entity.IronGolem ; is alive true
	//	VILLAGER is spawnable true ; class interface org.bukkit.entity.Villager ; is alive true
	//	ENDER_CRYSTAL is spawnable true ; class interface org.bukkit.entity.EnderCrystal ; is alive false

	@EventHandler
	public void onEvent(BlockRedstoneEvent event) {
		//Check if it was turn on or off
		if (event.getNewCurrent() == 0) return;
		
		//Get region this block may be
		GravitySheepRegion region = plugin.getAPI().getWorld(event.getBlock().getWorld()).getRegion(event.getBlock());
		
		//Check if it was a hit
		if (region == null) return;
		
		triggerRegion(region);

	}
	
	private void triggerRegion(GravitySheepRegion region) {
		EntityType entityType = region.getEntityType();

		if (entityType.isSpawnable()) {
			Entity cannonFodder = region.getWorld().spawn(region.getBase(), entityType.getEntityClass());

			if (cannonFodder instanceof Sheep) { ((Sheep)cannonFodder).setColor(DyeColor.values()[this.plugin.getRandom().nextInt(DyeColor.values().length)]); }
			
			cannonFodder.setVelocity(region.getVelocity());

		} else if (entityType == EntityType.FALLING_BLOCK) {
			//Falling block
			FallingBlock fb = region.getWorld().spawnFallingBlock(region.getBase(), region.getFallingBlockID(), region.getFallingBlockData());
			fb.setVelocity(region.getVelocity());

		}
		
	}
	
}
