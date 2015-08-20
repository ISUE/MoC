package net.dmg2.RegenBlock;

import org.bukkit.Location;

public class RegenBlockBlock implements Comparable<RegenBlockBlock> {
	private Location location;
	private int typeId;
	private byte data;
	private String regionName;
	private long respawnTime;
	private int sync;
	private int alarmTime;
	private int alarmRadius;
	private String alarmMessage;
	private int regionType;

	public Location getLocation() { return location; }
	public void setLocation(Location location) { this.location = location; }

	public int getTypeId() { return typeId; }
	public void setTypeId(int typeId) { this.typeId = typeId; }

	public byte getData() { return data; }
	public void setData(byte data) { this.data = data; }

	public String getRegionName() { return regionName; }
	public void setRegionName(String regionName) { this.regionName = regionName; }

	public long getRespawnTime() { return respawnTime; }
	public void setRespawnTime(long respawnTime) { this.respawnTime = respawnTime; }

	public int getAlarmTime() { return alarmTime; }
	public void setAlarmTime(int alarmTime) { this.alarmTime = alarmTime; }
	
	public String getAlarmMessage() { return alarmMessage; }
	public void setAlarmMessage(String alarmMessage) { this.alarmMessage = alarmMessage; }
	
	public int getSync() { return sync; }
	public void setSync(int sync) { this.sync = sync; }
	
	public int getAlarmRadius() { return alarmRadius; }
	public void setAlarmRadius(int alarmRadius) { this.alarmRadius = alarmRadius; }
	
	public int getRegionType() { return regionType; }
	public void setRegionType(int regionType) { this.regionType = regionType; }
	
	public RegenBlockBlock(Location location, int typeId, byte data, String regionName, long respawnTime, int alarmTime, int sync, int alarmRadius, String alarmMessage, int regionType) {
		this.location = location;
		this.typeId = typeId;
		this.data = data;
		this.regionName = regionName;
		this.respawnTime = respawnTime;
		this.alarmTime = alarmTime;
		this.sync = sync;
		this.alarmRadius = alarmRadius;
		this.alarmMessage = alarmMessage;
		this.regionType = regionType;
		
	}

	public int compareTo(RegenBlockBlock o) {
		if (this.respawnTime > o.respawnTime) return -1;
		if (this.respawnTime == o.respawnTime && this.location.getBlockX() > o.getLocation().getBlockX()) return -1;
		if (this.respawnTime == o.respawnTime && this.location.getBlockZ() > o.getLocation().getBlockZ()) return -1;
		if (this.respawnTime == o.respawnTime && this.location.getBlockY() > o.getLocation().getBlockY()) return -1;
		return 1;
		
	}

}
