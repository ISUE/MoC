package moc.DreamCrafter.data;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.material.MaterialData;

public class WorldData {
	public String 			name = "";
	public String			description = "";
	public String 			ownerName = "";
	public String 			blacklist = "";
	public boolean 			isActive = true;
	public Location	 		spawnpoint = null;	
	public WorldType		worldType = WorldType.Normal;
	public boolean			isPublished = false;
	public boolean			isThunderEnabled = false;
	public boolean			isStormyWeatherEnabled = false;
	public String			time = "0";
	public Difficulty		difficulty = Difficulty.NORMAL;
	public GameMode			gameMode = GameMode.SURVIVAL;
	public Location			endpoint = null;
	public Inventory		inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
	
	public enum WorldType {
		Normal,
		Build,
		Dream
	}
	
	public void setBlacklist(String s, Player player) {
		if(s.toUpperCase().equals("ALL")) {
			blacklist = s;
			return;
		}
		
		String[] blist = s.split(" ");
		blacklist = "";
		String m;
		
		for(int i = 0; i < blist.length; i++) {
			m = getMaterialName(blist[i]);
			if(m == null)
				player.sendMessage("Blacklist error: '" + blist[i] + "' not recognized");
			else {
				blacklist += (blacklist.length() > 0) ? " " : "";
				blacklist += m;
			}
		}
	}

	public String getCommaSeparatedBlacklist() {
		String commaBlacklist = "";
		String[] blist;
		String mat;
		
		if(blacklist.toUpperCase().equals("ALL")) {
			int numMaterials = MaterialData.getMaterials().size();
			blist = new String[numMaterials];
			for(int i = 0; i < numMaterials; i++)
				blist[i] = String.valueOf(i);
		}
		else 
			blist = blacklist.split(" ");
			
		for(int i = 0; i < blist.length; i++) {
			mat = getMaterialName(blist[i]);
			if ( mat != null ) {
				commaBlacklist += (commaBlacklist.length() > 0) ? "," : "";
				commaBlacklist += mat;
			}
		}
		
		return commaBlacklist;
	}
	
	private String getMaterialName(String s) {
		s = s.toUpperCase();
		
		if(Material.getMaterial(s) == null) {
			try {
				int x = Integer.parseInt(s);
				if(Material.getMaterial(x) == null) 
					return null;
				return String.valueOf( Material.getMaterial(x).getId() );
			} catch(Exception e) {
				return null;
			}
		}
		return String.valueOf( Material.getMaterial(s).getId() );
	}
	
	@Override
	public String toString() {
		String x = "";
		x += "name: " + name + "\n";
		x += "description: " + description + "\n";
		x += "ownerName: " + ownerName + "\n";
		x += "blacklist: " + blacklist + "\n";
		x += "worldType: " + worldType + "\n";
		x += "isPublished: " + isPublished + "\n";
		x += "isThunderEnabled: " + isThunderEnabled + "\n";
		x += "isStormyWeatherEnabled: " + isStormyWeatherEnabled + "\n";
		x += "time: " + time + "\n";
		x += "difficulty: " + difficulty + "\n";
		x += "gameMode: " + gameMode + "\n";
		
		return x;
	}
}
