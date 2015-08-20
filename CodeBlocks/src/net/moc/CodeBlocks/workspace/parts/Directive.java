package net.moc.CodeBlocks.workspace.parts;

public class Directive {
	private String name = "";
	private int range = 0;
	private boolean doPickUpItems = false;
	private boolean doAttack = false;
	private String[] attackTargets = {""};
	
	public String getName() { return name; }
	public int getRange() { return range; }
	public boolean doPickUpItems() { return doPickUpItems; }
	public boolean doAttack() { return doAttack; }
	public String[] getAttackTargets() { return attackTargets; }

	public Directive(String name, String data) {
		this.name = name;
		
		for (String i : data.split(";")) {
			if (i.equalsIgnoreCase("pickupitems")) { doPickUpItems = true; continue; }
			
			String[] s = i.split(":"); if (s.length < 2) continue;
			
			if (s[0].equalsIgnoreCase("range")) { try { range = Integer.parseInt(s[1]); } catch (NumberFormatException e) { range = 1; } }
			else if (s[0].equalsIgnoreCase("attack")) { attackTargets = s[1].split(",");  }
			
		}
		
	}

}
