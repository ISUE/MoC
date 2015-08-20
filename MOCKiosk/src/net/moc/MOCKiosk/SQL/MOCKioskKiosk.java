package net.moc.MOCKiosk.SQL;

import org.bukkit.Location;

public class MOCKioskKiosk  implements Comparable<MOCKioskKiosk> {
	private int id;
	private String name;
	private int owner_id;
	private String neartext;
	private String nearurl;
	private String clicktext;
	private String clickurl;
	private int popup_deck_id;
	private int isactive;
	private int location_id;
	
	private String ownerName;
	private Location location;
	
	public MOCKioskKiosk(int id, String name, int owner_id, String ownerName, String neartext, String nearurl, String clicktext, String clickurl, int popup_deck_id, int isactive, int location_id, Location location) {
		this.id = id;
		this.name = name;
		this.owner_id = owner_id;
		this.ownerName = ownerName;
		this.neartext = neartext;
		this.nearurl = nearurl;
		this.clicktext = clicktext;
		this.clickurl = clickurl;
		this.popup_deck_id = popup_deck_id;
		this.isactive = isactive;
		this.location_id = location_id;
		this.location = location;
		
	}

	public String getOwnerName() { return ownerName; }
	public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

	public Location getLocation() { return location; }
	public void setLocation(Location location) { this.location = location; }

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int getOwner_id() { return owner_id; }
	public void setOwner_id(int owner_id) { this.owner_id = owner_id; }

	public String getNeartext() { return neartext; }
	public void setNeartext(String neartext) { this.neartext = neartext; }

	public String getNearurl() { return nearurl; }
	public void setNearurl(String nearurl) { this.nearurl = nearurl; }

	public String getClicktext() { return clicktext; }
	public void setClicktext(String clicktext) { this.clicktext = clicktext; }

	public String getClickurl() { return clickurl; }
	public void setClickurl(String clickurl) { this.clickurl = clickurl; }

	public int getPopup_deck_id() { return popup_deck_id; }
	public void setPopup_deck_id(int popup_deck_id) { this.popup_deck_id = popup_deck_id; }

	public int getIsactive() { return isactive; }
	public void setIsactive(int isactive) { this.isactive = isactive; }

	public int getLocation_id() { return location_id; }
	public void setLocation_id(int location_id) { this.location_id = location_id; }

	public int compareTo(MOCKioskKiosk another) { return this.name.compareTo(another.name); }

}
