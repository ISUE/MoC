package net.moc.CodeBlocks.workspace;

public class RobotnikStats {
	private double healthMax;
	private double healthCurrent;
	private double armorMax;
	private double armorCurrent;
	private double powerMax;
	private double powerCurrent;
	private double damage;
	
	public RobotnikStats(double healthMax, double armorMax, double powerMax, double damage) {
		this.healthMax = healthMax;
		this.healthCurrent = healthMax;
		
		this.armorMax = armorMax;
		this.armorCurrent = armorMax;
		
		this.powerMax = powerMax;
		this.powerCurrent = 100;
		
		this.damage = damage;
		
	}

	public double getHealthMax() { return healthMax; }
	public void setHealthMax(double healthMax) { this.healthMax = healthMax; }

	public double getHealthCurrent() { return healthCurrent; }
	public void setHealthCurrent(double healthCurrent) { this.healthCurrent = healthCurrent; }
	public double addHealthCurrent(double v) { healthCurrent += v; if (healthCurrent < 0) healthCurrent = 0; if (healthCurrent > healthMax) healthCurrent = healthMax; return healthCurrent; }

	public double getArmorMax() { return armorMax; }
	public void setArmorMax(double armorMax) { this.armorMax = armorMax; }

	public double getArmorCurrent() { return armorCurrent; }
	public void setArmorCurrent(double armorCurrent) { this.armorCurrent = armorCurrent; }
	public double addArmorCurrent(double v) { armorCurrent += v; if (armorCurrent < 0) armorCurrent = 0; if (armorCurrent > armorMax) armorCurrent = armorMax; return armorCurrent; }

	public double getPowerMax() { return powerMax; }
	public void setPowerMax(double powerMax) { this.powerMax = powerMax; }

	public double getPowerCurrent() { return powerCurrent; }
    public void setPowerCurrent(double powerCurrent) { this.powerCurrent = powerCurrent; }
	public double addPowerCurrent(double v) { powerCurrent += v; if (powerCurrent < 0) powerCurrent = 0; if (powerCurrent > powerMax) powerCurrent = powerMax; return powerCurrent; }
	
	public double getDamage() { return damage; }
	public void setDamage(double damage) { this.damage = damage; }

}
