package net.moc.MOCKiosk.SQL;

public class MOCKioskKioskDeck implements Comparable<MOCKioskKioskDeck> {
	private int id;
	private String name;
	
	public MOCKioskKioskDeck(int id, String name) {
		this.id = id;
		this.name = name;
		
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int compareTo(MOCKioskKioskDeck another) { return this.name.compareTo(another.name); }
	
}
