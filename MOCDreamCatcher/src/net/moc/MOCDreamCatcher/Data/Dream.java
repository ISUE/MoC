package net.moc.MOCDreamCatcher.Data;

public class Dream {
	//====================================================
	private boolean isFinished = false;
	private boolean surveySubmitted = false;
	private String thoughtName;
	private String authorName;
	
	//====================================================
	public Dream(String thoughtName, String author) { this.thoughtName = thoughtName; this.authorName = author; }

	//====================================================
	public boolean isFinished() { return isFinished; }
	public void setFinished(boolean isFinished) { this.isFinished = isFinished; }

	public boolean surveySubmitted() { return surveySubmitted; }
	public void setSurveySubmitted(boolean surveySubmitted) { this.surveySubmitted = surveySubmitted; }

	public String getDreamThought() { return thoughtName; }
	public void setDreamThought(String thoughtName) { this.thoughtName = thoughtName; }
	
	public String getDreamAuthor() { return authorName; }
	public void setDreamAuthor(String authorName) { this.authorName = authorName; }

}
