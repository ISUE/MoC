package net.moc.MOCRater.SQL;

public class MOCLike {
	private int player_id;
	private int comment_id;
	private String choice;
	
	public MOCLike(int player_id, int comment_id, String choice) {
		this.player_id = player_id;
		this.comment_id = comment_id;
		this.choice = choice;
	}

	public int getPlayer_id() {
		return player_id;
	}
	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getChoice() {
		return choice;
	}
	public void setChoice(String choice) {
		this.choice = choice;
	}

}
