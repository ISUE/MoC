package net.moc.MOCDreamCatcher.Data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Thought {
	//=============================================
	private String name;
	private String author;
	private Inventory inventory;
	private Location start;
	private Location end;
	private Survey survey;
	private boolean isPublished;
	
	//=============================================
	public Thought(String name, String author) {
		this.name = name;
		this.author = author;
		inventory = null;
		start = null;
		end = null;
		survey = null;
		isPublished = false;
		
	}

	//=============================================
	public String getName() { return name;}
	public void setName(String name) { this.name = name; }

	public String getAuthor() { return author;}
	public void setAuthor(String author) { this.author = author; }

	public Inventory getInventory() { return inventory; }
	public void setInventory(Inventory inventory) {
		if (inventory == null) this.inventory = null;
		else {
			this.inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER); 
			this.inventory.setContents(inventory.getContents());
			
		}
		
	}

	public Location getStart() { return start; }
	public void setStart(Location start) { this.start = start; }

	public Location getEnd() { return end; }
	public void setEnd(Location end) { this.end = end; }

	public Survey getSurvey() { return survey; }
	public void setSurvey(Survey survey) { this.survey = survey; }
	
	public boolean isPublished() { return isPublished; }
	public void setPublished(boolean value) { isPublished = value; }
	
}
