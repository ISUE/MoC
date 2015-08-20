package net.moc.MOCRater.GUI;

import java.io.File;

import net.moc.MOCRater.GUI.Widgets.MOCListWidget;
import net.moc.MOCRater.SQL.MOCComment;
import net.moc.MOCRater.SQL.MOCPattern;
import net.moc.MOCRater.MOCRater;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PatternBrowserWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCRater plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	private MOCPattern selectedPattern;
	private MOCComment selectedComment;
	//----------------------------------------------------------------
	//Background
	private Gradient background;

	//Heading
	private Label headingTitle, headingTitleDescription;

	//Text field
	private Label textLabel;
	private GenericTextField textTextField;

	//Pattern
	private Label patternLabel;
	private Button patternButtonEdit, patternButtonAdd;
	private ListWidget patternList, patternListComments;

	//Window buttons
	private Button buttonViewComment, buttonCritique, buttonClose;
	
	//Screenshot
	private Button buttonShowPicture, buttonScreenShotClose;
	private Texture screenshot;
	//----------------------------------------------------------------
	//================================================================================================================



	//================================================================================================================
	public PatternBrowserWindow(SpoutPlayer player, MOCRater plugin) {
		this.plugin = plugin;
		this.player = player;

		//Set window transparent
		this.setTransparent(true);


		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);


		//Heading
		this.headingTitle = new GenericLabel(this.plugin.getDescription().getFullName() + ": Pattern Browser");
		this.headingTitle.setScale(this.scaleLarge);

		this.headingTitleDescription = new GenericLabel("Patterns give a sctructure to design ideas and solutions (Google \"Pattern language\").");
		this.headingTitleDescription.setScale(this.scaleSmall);


		//Text field
		this.textLabel = new GenericLabel("Description:");
		this.textLabel.setTooltip("Pattern description");
		this.textLabel.setScale(this.scaleNormal);

		this.textTextField = new GenericTextField();
		this.textTextField.setTooltip("Pattern description");
		this.textTextField.setFieldColor(this.textFieldColor);
		this.textTextField.setMaximumLines(15);
		this.textTextField.setMaximumCharacters(5000);


		//Pattern
		this.patternLabel = new GenericLabel("Patterns:");
		this.patternLabel.setTooltip("Pattern List");
		this.patternLabel.setScale(this.scaleNormal);


		this.patternButtonEdit = new GenericButton("Edit");
		this.patternButtonEdit.setTooltip("Edit currently selected pattern");
		this.patternButtonEdit.setHoverColor(this.hoverColor);

		this.patternButtonAdd = new GenericButton("Add");
		this.patternButtonAdd.setTooltip("Add a new Pattern. This will cost gold.");
		this.patternButtonAdd.setHoverColor(this.hoverColor);

		this.patternList = new MOCListWidget();
		this.patternList.setTooltip("Select a pattern from the list");
		this.patternList.setBackgroundColor(this.textFieldColor);

		this.patternListComments = new MOCListWidget();
		this.patternListComments.setTooltip("Comments about this patterns");
		this.patternListComments.setBackgroundColor(this.textFieldColor);

		//Window buttons
		this.buttonViewComment = new GenericButton("View Comment");
		this.buttonViewComment.setTooltip("View Comment");
		this.buttonViewComment.setHoverColor(this.hoverColor);

		this.buttonCritique = new GenericButton("Critique Pattern");
		this.buttonCritique.setTooltip("Submit a critique for the pattern");
		this.buttonCritique.setHoverColor(this.hoverColor);

		this.buttonShowPicture = new GenericButton("Picture");
		this.buttonShowPicture.setTooltip("Display the picture of this pattern");
		this.buttonShowPicture.setHoverColor(this.hoverColor);

		this.buttonScreenShotClose = new GenericButton("Close");
		this.buttonScreenShotClose.setTooltip("Close");
		this.buttonScreenShotClose.setHoverColor(this.hoverColor);
		this.buttonScreenShotClose.setPriority(RenderPriority.Lowest);

		this.screenshot = new GenericTexture();
		this.screenshot.setPriority(RenderPriority.Low);

		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);


		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.headingTitle, this.headingTitleDescription, this.buttonShowPicture);
		attachWidgets(plugin, this.textLabel, this.textTextField, this.patternButtonEdit, this.patternButtonAdd);
		attachWidgets(plugin, this.patternLabel, this.patternList, this.patternListComments, this.buttonViewComment, this.buttonCritique, this.buttonClose);
		attachWidgets(plugin, this.screenshot, this.buttonScreenShotClose);

		//Initialize
		this.initialize();

	}
	//================================================================================================================



	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBuffer * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBuffer * 2;
		int upLeftX = this.screenBuffer; 
		int upLeftY = this.screenBuffer;
		int upRightX = player.getMainScreen().getWidth() - this.screenBuffer;
		int upRightY = this.screenBuffer;
		int downLeftX = this.screenBuffer;
		int downLeftY = player.getMainScreen().getHeight() - this.screenBuffer;
		int downRightX = player.getMainScreen().getWidth() - this.screenBuffer;
		int downRightY = player.getMainScreen().getHeight() - this.screenBuffer;


		//Background
		this.background.setHeight(windowHeight).setWidth(windowWidth);
		this.background.setX(upLeftX).setY(upLeftY);

		
		//-------------------------------------------------------------------
		//Upper Left Corner anchor
		//-------------------------------------------------------------------
		//Heading
		this.headingTitle.setX(upLeftX+5).setY(upLeftY+5);
		this.headingTitle.setHeight(15).setWidth(windowWidth);

		this.headingTitleDescription.setX(upLeftX+5).setY(upLeftY+15);
		this.headingTitleDescription.setHeight(15).setWidth(windowWidth);
		//-------------------------------------------------------------------


		//-------------------------------------------------------------------
		//Upper Right Corner anchor
		//-------------------------------------------------------------------
		//Text field
		this.textLabel.setX(upRightX-305).setY(upRightY+30);
		this.textLabel.setWidth(40).setHeight(15);

		this.textTextField.setX(upRightX-305).setY(upRightY+40);
		this.textTextField.setWidth(300).setHeight(160);
		this.textTextField.setEnabled(false);
		this.textTextField.setText("");
		//-------------------------------------------------------------------


		//-------------------------------------------------------------------
		//Lower Left Corner anchor
		//-------------------------------------------------------------------
		//Pattern
		this.patternLabel.setX(upLeftX+5).setY(upLeftY+30);
		this.patternLabel.setWidth(40).setHeight(15);

		this.patternButtonEdit.setX(downLeftX+5).setY(downLeftY-20);
		this.patternButtonEdit.setWidth(50).setHeight(15);
		this.patternButtonEdit.setEnabled(false);

		this.patternButtonAdd.setX(downLeftX+55).setY(downLeftY-20);
		this.patternButtonAdd.setWidth(50).setHeight(15);
		if (this.player.hasPermission("MOCRater.patternsaddedit")) this.patternButtonAdd.setEnabled(true);
		else  this.patternButtonAdd.setEnabled(false);

		this.patternList.setX(upLeftX+5).setY(upLeftY+40);
		this.patternList.setWidth(100).setHeight(160);
		this.patternList.clear();
		for (MOCPattern pattern : this.plugin.getSQL().getPatterns()) {
			this.patternList.addItem(new ListWidgetItem("", pattern.getName()));
		}
		this.patternList.clearSelection();
		
		this.patternListComments.setX(upRightX-75).setY(upRightY+40);
		this.patternListComments.setWidth(70).setHeight(160);
		this.patternListComments.clear();
		this.patternListComments.clearSelection();
		this.patternListComments.setVisible(false);
		//-------------------------------------------------------------------



		//Window buttons
		this.buttonViewComment.setX(downRightX-75).setY(downRightY-20);
		this.buttonViewComment.setWidth(70).setHeight(15);
		this.buttonViewComment.setEnabled(false);
		this.buttonViewComment.setVisible(false);

		this.buttonCritique.setX(downRightX-205).setY(downRightY-20);
		this.buttonCritique.setWidth(100).setHeight(15);
		this.buttonCritique.setEnabled(false);

		this.buttonShowPicture.setX(downRightX-305).setY(downRightY-20);
		this.buttonShowPicture.setWidth(100).setHeight(15);
		this.buttonShowPicture.setEnabled(false);
		
		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);

		//Screen shot
		this.screenshot.setWidth(player.getMainScreen().getWidth() - this.screenBuffer * 4);
		this.screenshot.setHeight(player.getMainScreen().getHeight() - this.screenBuffer * 4);
		this.screenshot.setX(this.screenBuffer * 2).setY(this.screenBuffer * 2);
		this.screenshot.setUrl("");
		this.screenshot.setVisible(false);
		
		this.buttonScreenShotClose.setWidth(40).setHeight(15);
		this.buttonScreenShotClose.setX(player.getMainScreen().getWidth() / 2 - 20).setY(player.getMainScreen().getHeight() - 20 - this.screenBuffer * 2);
		this.buttonScreenShotClose.setVisible(false);
		
	}
	//================================================================================================================



	//================================================================================================================
	//Open the GUI
	public void open(boolean takeScreenshot) {
		//Get the screen shot
		if (takeScreenshot) this.player.sendScreenshotRequest();
		
		this.initialize();

		this.player.getMainScreen().attachPopupScreen(this);

		this.setDirty(true);

		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
			
		}

	}
	//================================================================================================================



	//================================================================================================================
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		
		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close and Cancel buttons
		if (button.equals(this.buttonClose)) {
			closeWindow();
			
			return;
			
		}
		
		//Button View Comment
		if (button.equals(this.buttonViewComment)) {
			closeWindow();
			this.plugin.getGui().displayRatingBrowserWindowGUI(player, this.selectedComment);
			return;
			
		}

		//Critique button
		if (button.equals(this.buttonCritique)) {
			closeWindow();
			this.plugin.getGui().displayRatingWindowGUI(this.player, false, this.patternList.getSelectedItem().getText());
			return;
			
		}
		
		//Screen shot
		if (button.equals(this.buttonShowPicture)) {
			for(Widget widget : getAttachedWidgets()) {
				widget.setDirty(true);
				widget.setVisible(false);
				
			}
			
			this.background.setVisible(true);
			this.background.setDirty(true);
			
			File screenShot = new File(this.plugin.getScreenshotFolder() + File.separator + this.selectedPattern.getScreenshot());
			if (screenShot.exists()) {
				SpoutManager.getFileManager().addToCache(plugin, screenShot);
				this.screenshot.setUrl(screenShot.getName());
				
			} else {
				this.screenshot.setUrl("http://www.eecs.ucf.edu/isuelab/images/logo4.png");
				
			}
			
			this.screenshot.setVisible(true);
			this.screenshot.setDirty(true);
			
			this.buttonScreenShotClose.setVisible(true);
			this.buttonScreenShotClose.setDirty(true);
			
			this.setDirty(true);
			
			return;
			
		}

		if (button.equals(this.buttonScreenShotClose)) {
			for(Widget widget : getAttachedWidgets()) {
				widget.setDirty(true);
				widget.setVisible(true);
				
			}
			
			this.screenshot.setUrl("");
			this.screenshot.setVisible(false);
			this.screenshot.setDirty(true);
			
			this.buttonScreenShotClose.setVisible(false);
			this.buttonScreenShotClose.setDirty(true);
			
			this.setDirty(true);
			
			return;
		}
		
		//Edit button
		if (button.equals(this.patternButtonEdit)) {
			closeWindow();
			this.plugin.getGui().displayPatternManagerWindowGUI(this.player, this.patternList.getSelectedItem().getText());
			return;
		}

		//Add button
		if (button.equals(this.patternButtonAdd)) {
			closeWindow();
			this.plugin.getGui().displayPatternManagerWindowGUI(this.player, null);
			return;
		}
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	}
	
	public void onSelection(ListWidget listWidget) {
		if (listWidget == this.patternList) {
			if (this.patternList.getSelectedItem() == null) {
				this.patternButtonEdit.setEnabled(false);
				this.patternButtonEdit.setDirty(true);

				this.buttonCritique.setEnabled(false);
				this.buttonCritique.setDirty(true);

				this.buttonShowPicture.setEnabled(false);
				this.buttonShowPicture.setDirty(true);
				
				this.buttonViewComment.setEnabled(false);
				this.buttonViewComment.setDirty(true);

				this.selectedPattern = null;
				this.selectedComment = null;

				this.patternListComments.clearSelection();
				this.patternListComments.clear();
				this.patternListComments.setVisible(false);
				this.patternListComments.setDirty(true);

				this.buttonViewComment.setVisible(false);
				this.buttonViewComment.setDirty(true);
				
				this.textTextField.setWidth(300);
				this.textTextField.setDirty(true);

			} else {
				for (MOCPattern pattern : this.plugin.getSQL().getPatterns()) {
					if (pattern.getName().equalsIgnoreCase(this.patternList.getSelectedItem().getText())) {
						this.selectedPattern = pattern;
						this.selectedComment = null;

						this.buttonViewComment.setEnabled(false);
						this.buttonViewComment.setDirty(true);

						String relatedPatterns;
						if (this.plugin.getSQL().getPatternRelatedPatterns(pattern.getName()).isEmpty()) {
							relatedPatterns = "";

						} else {
							relatedPatterns = "Related Patterns: " + this.plugin.getSQL().getPatternRelatedPatterns(pattern.getName()) + "\n";

						}

						this.textTextField.setText(relatedPatterns +
								"Context: " + pattern.getContext() +
								"\nProblem: " + pattern.getProblem() +
								"\nSolution: " + pattern.getSolution());

						if (this.player.hasPermission("MOCRater.patternsaddedit") && pattern.getPlayerName().equalsIgnoreCase(this.player.getName())) this.patternButtonEdit.setEnabled(true);
						else this.patternButtonEdit.setEnabled(false);

						this.patternListComments.clearSelection();
						this.patternListComments.clear();
						
						for (MOCComment comment : this.plugin.getSQL().getComments()) {
							if (comment.getPattern_id() == pattern.getPattern_id()) {
								this.patternListComments.addItem(new ListWidgetItem(comment.getRating_comment_id()+"", comment.getTitle()));

							}

						}
						
						if (this.patternListComments.getItems().length > 0) {
							this.patternListComments.setVisible(true);
							
							this.buttonViewComment.setVisible(true);
							this.buttonViewComment.setDirty(true);
							
							this.textTextField.setWidth(220);
							this.textTextField.setDirty(true);
						} else {
							this.patternListComments.setVisible(false);
							
							this.buttonViewComment.setVisible(false);
							this.buttonViewComment.setDirty(true);
							
							this.textTextField.setWidth(300);
							this.textTextField.setDirty(true);
						}
						
						this.patternListComments.setDirty(true);

						break;

					}

				}

				this.textTextField.setDirty(true);
				this.patternButtonEdit.setDirty(true);

				this.buttonCritique.setEnabled(true);
				this.buttonCritique.setDirty(true);

				this.buttonShowPicture.setEnabled(true);
				this.buttonShowPicture.setDirty(true);

			}

		} else if (listWidget == this.patternListComments) {
			if (this.patternListComments.getSelectedItem() == null) {
				this.buttonViewComment.setEnabled(false);
				this.buttonViewComment.setDirty(true);
				this.selectedComment = null;
				
			} else {
				for (MOCComment comment : this.plugin.getSQL().getComments()) {
					if (this.patternListComments.getSelectedItem().getTitle().equalsIgnoreCase(comment.getRating_comment_id()+"")) {
						this.selectedComment = comment;
						
						this.buttonViewComment.setEnabled(true);
						this.buttonViewComment.setDirty(true);
						
					}
					
				}
				
			}
			
		}
		
	}
	//================================================================================================================
}

