package net.moc.MOCRater.SQL;

import java.sql.Timestamp;

public class MOCPattern implements Comparable<MOCPattern> {
	private int pattern_id;
	private int player_id;
	private String playerName;
	private String name;
	private String screen_src;
	private Timestamp createdon;
	private String context;
	private String problem;
	private String solution;
	
	public MOCPattern(int pattern_id, int player_id, String playerName, String name, String screen_src, Timestamp createdon, String context, String problem, String solution) {
		this.pattern_id = pattern_id;
		this.player_id = player_id;
		this.playerName = playerName;
		this.name = name;
		this.screen_src = screen_src;
		this.createdon = createdon;
		this.context = context;
		this.problem = problem;
		this.solution = solution;
		
	}

	public int getPattern_id() { return pattern_id; }
	public void setPattern_id(int pattern_id) { this.pattern_id = pattern_id; }

	public int getPlayer_id() {	return player_id; }
	public void setPlayer_id(int player_id) { this.player_id = player_id; }
	
	public String getPlayerName() { return playerName; }
	public void setPlayerName(String playerName) { this.playerName = playerName; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getScreenshot() { return screen_src; }
	public void setScreenshot(String screen_src) { this.screen_src = screen_src; }
	
	public Timestamp getCreatedon() { return createdon; }
	public void setCreatedon(Timestamp createdon) { this.createdon = createdon; }

	public String getContext() { return context; }
	public void setContext(String context) { this.context = context; }

	public String getProblem() { return problem; }
	public void setProblem(String problem) { this.problem = problem; }

	public String getSolution() { return solution; }
	public void setSolution(String solution) { this.solution = solution; }

	public int compareTo(MOCPattern another) {
		return this.name.compareTo(another.name);
	}

}
