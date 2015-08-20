package net.moc.MOCRater.SQL;

import java.sql.Timestamp;

import org.bukkit.Location;

public class MOCComment implements Comparable<MOCComment> {
	private int rating_comments_id;
	private int pattern_id;
	private String patternName;
	private String title;
	private int rating_t;
	private String ratingTypeName;
	private int score;
	private int location_id;
	private Location location;
	private String comment;
	private Timestamp createon;
	private int player_id;
	private String playerName;
	private String screen_src;
	private int is_on;
	
	public MOCComment(int rating_comments_id, int pattern_id, String patternName, String title, int rating_t, String ratingTypeName, int score,
			int location_id,	Location location, String comment, Timestamp createon,	int player_id, String playerName, String screen_src, int is_on) {
		this.rating_comments_id = rating_comments_id;
		this.pattern_id = pattern_id;
		this.patternName = patternName;
		this.title = title;
		this.rating_t = rating_t;
		this.ratingTypeName = ratingTypeName;
		this.score = score;
		this.location_id = location_id;
		this.location = location;
		this.comment = comment;
		this.createon = createon;
		this.player_id = player_id;
		this.playerName = playerName;
		this.screen_src = screen_src;
		this.is_on = is_on;
		
	}

	public int getRating_comment_id() {
		return rating_comments_id;
	}

	public void setRating_comment_id(int rating_comments_id) {
		this.rating_comments_id = rating_comments_id;
	}

	public int getPattern_id() {
		return pattern_id;
	}

	public void setPattern_id(int pattern_id) {
		this.pattern_id = pattern_id;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRating_t() {
		return rating_t;
	}

	public void setRating_t(int rating_t) {
		this.rating_t = rating_t;
	}

	public String getRatingTypeName() {
		return ratingTypeName;
	}

	public void setRatingTypeName(String ratingTypeName) {
		this.ratingTypeName = ratingTypeName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getLocation_id() {
		return location_id;
	}

	public void setLocation_id(int location_id) {
		this.location_id = location_id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getCreatedon() {
		return createon;
	}

	public void setCreatedon(Timestamp createon) {
		this.createon = createon;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int compareTo(MOCComment another) {
		return this.title.compareTo(another.title);
	}

	public String getScreenshot() {
		return screen_src;
	}

	public void setScreenshot(String screen_src) {
		this.screen_src = screen_src;
	}

	public boolean isOn() {
		if (this.is_on == 1) return true;
		return false;
	}

	public void setIsOn(boolean isOn) {
		if (isOn) this.is_on = 1;
		else this.is_on = 0;
		
	}
	
}
