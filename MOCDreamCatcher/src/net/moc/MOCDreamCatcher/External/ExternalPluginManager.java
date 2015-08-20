package net.moc.MOCDreamCatcher.External;

import java.util.ArrayList;

import moc.MOCFizziks.MOCFizziks;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.trait.CurrentLocation;
import net.dmg2.GravitySheep.GravitySheep;
import net.moc.MOCConvo.MOCConvo;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.getspout.spout.Spout;

import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExternalPluginManager {
	//================================================================
	private MOCDreamCatcher plugin;
	
	//Plugins
	private MultiverseCore multiverseCore; public MultiverseCore getMultiverse() { return multiverseCore; }
	private WorldGuardPlugin worldGuard; public WorldGuardPlugin getWorldGuard() { return worldGuard; }
	private PermissionsEx permissionsEx; public PermissionsEx getPermissionsEx() { return permissionsEx; }
	
	private Citizens citizens;
	private MOCConvo mocconvo;
	private MOCFizziks mocfizziks;
	private GravitySheep gravitySheep;
	
	private boolean allPluginsPresent = false;


	public boolean pluginsPresent() { return allPluginsPresent; }
	
	//================================================================
	public ExternalPluginManager(MOCDreamCatcher plugin) { this.plugin = plugin; loadPlugins(); }
	
	//================================================================
	private void loadPlugins() {
		PluginManager pm = plugin.getServer().getPluginManager();
		
		multiverseCore = (MultiverseCore) pm.getPlugin("Multiverse-Core");
		worldGuard = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
		permissionsEx = (PermissionsEx) pm.getPlugin("PermissionsEx");
		Spout spout = (Spout) pm.getPlugin("Spout");
		
		citizens = (Citizens) pm.getPlugin("Citizens");
		mocconvo = (MOCConvo) pm.getPlugin("MOCConvo");
		mocfizziks = (MOCFizziks) pm.getPlugin("MOCFizziks");
		gravitySheep = (GravitySheep) pm.getPlugin("GravitySheep");
		
		if(multiverseCore == null) { plugin.getLog().warn("Missing Multiverse plugin. Required for DreamCrafter - please install."); allPluginsPresent = false; return; }
		if(worldGuard == null) { plugin.getLog().warn("Missing WorldGuard plugin. Required for DreamCrafter - please install."); allPluginsPresent = false; return; }
		if(permissionsEx == null) { plugin.getLog().warn("Missing PermissionsEx plugin. Required for DreamCrafter - please install."); allPluginsPresent = false; return; }
		
		if (spout == null) { plugin.getLog().warn("SpoutPlugin not available. GUI part will not be used."); plugin.setSpoutAvailable(false); }
		else plugin.setSpoutAvailable(true);
		
		allPluginsPresent = true;
		
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	//================================================================
	public void copyWorldData(String worldFrom, String worldTo) {
		debug("Copying world data.");
		World world1 = plugin.getServer().getWorld(worldFrom);
		World world2 = plugin.getServer().getWorld(worldTo);
		
		if (world1 == null || world2 == null) return;
		
		//Citizens ===================================================
		if (citizens != null) {
			ArrayList<NPC> npcToCopy = new ArrayList<NPC>();
			
			//Find matching NPCs
			for (NPC npc : citizens.getNPCRegistry()) {
				Location location = getNPCLocation(npc);
				
				if (location != null && location.getWorld().getName().equalsIgnoreCase(world1.getName())) {
					debug("Copying npc " + npc.getId() + " " + npc.getFullName() + " " + location);
					npcToCopy.add(npc);
					
				}
				
			}
			
			//Spawn new NPCs
			for (NPC npc : npcToCopy) {
				//Create new npc
				CitizensNPC fromNPC = (CitizensNPC) npc;
				CitizensNPC newNPC = (CitizensNPC) citizens.getNPCRegistry().createNPC(getNPCEntityType(npc), npc.getName());
				
				//Copy traits
				DataKey key = new MemoryDataKey();
				fromNPC.save(key);
				newNPC.load(key);
				
				//Initialize traits
				for (Trait t : newNPC.getTraits()) t.onCopy();
				
				//Spawn at same location in world 2
				Location fromLocation = getNPCLocation(npc);
				Location location = new Location(world2, fromLocation.getX(), fromLocation.getY(), fromLocation.getZ());
				location.getChunk().load();
				
				newNPC.getBukkitEntity().teleport(location);
				newNPC.getTrait(CurrentLocation.class).setLocation(location);
	            
				debug("Base NPC state " + fromNPC.isSpawned() + " " + fromNPC.getId() + " " + fromNPC.getFullName() + " " + getNPCLocation(fromNPC));
				debug("Spawning new npc " + newNPC.isSpawned() + " " + newNPC.getId() + " " + newNPC.getFullName() + " " + getNPCLocation(newNPC));

			}
			
		}
		
		//MOCConvo ===================================================
		if (mocconvo != null) debug("Copying world data for MOCConvo - " + mocconvo.getSQL().copyConvoBlocks(worldFrom, worldTo));
		
		//MOCFizziks =================================================
		if (mocfizziks != null) debug("Copying world data for MOCFizziks - " + mocfizziks.getAPI().copyWorld(world2, world1));
		
		//GravitySheep ===============================================
		if (gravitySheep != null) debug("Copying world data for GravitySheep - " + gravitySheep.getAPI().copyWorld(world2, world1));
		
	}
	
	//================================================================
	public void deleteWorldData(String worldName) {
		World world = plugin.getServer().getWorld(worldName);
		
		debug("Deleting world data. " + world + " " + worldName);
		if (world == null) return;
		
		//Citizens ===================================================
		if (citizens != null) {
			ArrayList<NPC> npcToDelete = new ArrayList<NPC>();
			
			for (NPC npc : citizens.getNPCRegistry()) {
				Location location = getNPCLocation(npc);
				//debug("Inspecting NPC " + npc.getId() + " " + npc.getFullName() + " at " + location);
				
				if (location != null && location.getWorld().getName().equalsIgnoreCase(world.getName())) npcToDelete.add(npc);
				
			}
			
			for (NPC npc : npcToDelete) {
				debug("Deleting npc " + npc.getId() + " " + npc.getFullName() + " " + getNPCLocation(npc));
				npc.destroy();
				
			}
			
		}
		
		//MOCConvo ===================================================
		if (mocconvo != null) debug("MocConvo clean up returned - " + mocconvo.getSQL().DeleteAllConvoBlocks(worldName));
		
		//MOCFizziks =================================================
		if (mocfizziks != null) debug("Removing world data for MOCFizziks - " + mocfizziks.getAPI().removeWorld(worldName));
		
		//GravitySheep ===============================================
		if (gravitySheep != null) debug("Removing world data for GravitySheep - " + gravitySheep.getAPI().removeWorld(worldName));
		
	}

	//================================================================
	public void unloadWorldData(String worldName) {
		debug("Unloading world data.");
		World world = plugin.getServer().getWorld(worldName);
		
		if (world == null) return;
		
		//Citizens ===================================================
		if (citizens != null) {
			for (NPC npc : citizens.getNPCRegistry()) {
				Location location = getNPCLocation(npc);
				if (location != null && location.getWorld().getName().equalsIgnoreCase(world.getName())) {
					boolean result = npc.despawn();
					debug("Despawning npc " + result + " " + npc.getId() + " " + npc.getFullName() + " " + location);
					
				}
				
			}
			
		}
		
	}

	//================================================================
	public void loadWorldData(String worldName) {
		debug("Loading world data.");
		World world = plugin.getServer().getWorld(worldName);
		
		if (world == null) return;
		
		//Citizens ===================================================
		if (citizens != null) {
			for (NPC npc : citizens.getNPCRegistry()) {
				Location location = getNPCLocation(npc);
				if (location != null && location.getWorld().getName().equalsIgnoreCase(world.getName())) {
					while (!location.getChunk().load()) {}
					
					final NPC n = npc;
					final Location loc = location.clone();
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							boolean result = n.spawn(loc);
							
							debug("Spawning npc " + result + " " + n.getId() + " " + n.getFullName() + " " + loc);
							
						}
					}, 100L);
					
				}
				
			}
			
		}
		
	}

	//================================================================
//    registerTrait(TraitInfo.create(Age.class).withName("age"));
//    registerTrait(TraitInfo.create(Anchors.class).withName("anchors"));
//    registerTrait(TraitInfo.create(Controllable.class).withName("controllable"));
//    registerTrait(TraitInfo.create(Equipment.class).withName("equipment"));
//    registerTrait(TraitInfo.create(Gravity.class).withName("gravity"));
//    registerTrait(TraitInfo.create(Inventory.class).withName("inventory"));
//    registerTrait(TraitInfo.create(CurrentLocation.class).withName("location"));
//    registerTrait(TraitInfo.create(LookClose.class).withName("lookclose"));
//    registerTrait(TraitInfo.create(Owner.class).withName("owner"));
//    registerTrait(TraitInfo.create(Poses.class).withName("poses"));
//    registerTrait(TraitInfo.create(Powered.class).withName("powered"));
//    registerTrait(TraitInfo.create(VillagerProfession.class).withName("profession"));
//    registerTrait(TraitInfo.create(Saddle.class).withName("saddle"));
//    registerTrait(TraitInfo.create(Sheared.class).withName("sheared"));
//    registerTrait(TraitInfo.create(NPCSkeletonType.class).withName("skeletontype"));
//    registerTrait(TraitInfo.create(SlimeSize.class).withName("slimesize"));
//    registerTrait(TraitInfo.create(Spawned.class).withName("spawned"));
//    registerTrait(TraitInfo.create(Speech.class).withName("speech"));
//    registerTrait(TraitInfo.create(Text.class).withName("text"));
//    registerTrait(TraitInfo.create(MobType.class).withName("type"));
//    registerTrait(TraitInfo.create(Waypoints.class).withName("waypoints"));
//    registerTrait(TraitInfo.create(WoolColor.class).withName("woolcolor"));
//    registerTrait(TraitInfo.create(ZombieModifier.class).withName("zombiemodifier"));
	private Location getNPCLocation(NPC npc) { return npc.getTrait(CurrentLocation.class).getLocation(); }
	private EntityType getNPCEntityType(NPC npc) { return npc.getTrait(MobType.class).getType(); }
	
	private void debug(String message) {
		plugin.getLog().info("---------------------------------------> " + message + " []" + this);
		
	}

}
