package net.moc.MOCRater.GUI;

import java.io.File;
import net.moc.MOCRater.GUI.Widgets.MOCListWidget;
import net.moc.MOCRater.SQL.MOCComment;
import net.moc.MOCRater.MOCRater;
import org.bukkit.ChatColor;
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

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class RatingBrowserWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCRater plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	
	private MOCComment selectedComment;
	//----------------------------------------------------------------
	//Background
	private Gradient background;

	//Heading
	private Label headingTitle, headingTitleDescription;

	//Rating info
	private Label locationLabel, playerNameLabel, patternNameLabel, ratingTypeandValue;
	
	//Text field
	private GenericTextField textTextField;

	//Ratings
	private Label worldNameLabel;
	private Button likeButton, dislikeButton;
	
	private ListWidget ratingList;
	private Label labelLikeDislikeCounts;

	//Window buttons
	private Button buttonToggleVisible, buttonTeleport, buttonShowPicture, buttonClose;
	private Label labelShown;
	
	//Picture
	private Texture screenshot;
	private Button screenshotCloseButton;
	//----------------------------------------------------------------
	//================================================================================================================



	//================================================================================================================
	public RatingBrowserWindow(SpoutPlayer player, MOCRater plugin) {
		this.plugin = plugin;
		this.player = player;

		//Set window transparent
		this.setTransparent(true);


		//Background
		this.background = new GenericGradient(this.backgroundColor);
		this.background.setPriority(RenderPriority.Highest);


		//Heading
		this.headingTitle = new GenericLabel(this.plugin.getDescription().getFullName() + ": Rating Browser");
		this.headingTitle.setScale(this.scaleLarge);

		this.headingTitleDescription = new GenericLabel("Take a look at the ratings in this world. Like them as you see fit.");
		this.headingTitleDescription.setScale(this.scaleSmall);

		//COmment info
		this.locationLabel = new GenericLabel("Location:");
		this.locationLabel.setTooltip("Rating location");
		this.locationLabel.setScale(this.scaleNormal);

		this.playerNameLabel = new GenericLabel("Player:");
		this.playerNameLabel.setTooltip("Player that submitted the rating");
		this.playerNameLabel.setScale(this.scaleNormal);

		this.patternNameLabel = new GenericLabel("Pattern:");
		this.patternNameLabel.setTooltip("Pattern related to this comment");
		this.patternNameLabel.setScale(this.scaleNormal);

		this.ratingTypeandValue = new GenericLabel("Rating:");
		this.ratingTypeandValue.setTooltip("Rating type and value for this comment");
		this.ratingTypeandValue.setScale(this.scaleNormal);

		//Text field
		this.textTextField = new GenericTextField();
		this.textTextField.setTooltip("Rating description");
		this.textTextField.setFieldColor(this.textFieldColor);
		this.textTextField.setMaximumLines(15);
		this.textTextField.setMaximumCharacters(5000);


		//Comments
		this.worldNameLabel = new GenericLabel("World: " + this.player.getLocation().getWorld().getName());
		this.worldNameLabel.setTooltip("World Name");
		this.worldNameLabel.setScale(this.scaleNormal);

		this.labelLikeDislikeCounts = new GenericLabel("");
		this.labelLikeDislikeCounts.setTooltip("Like / Dislike counts for the rating");
		this.labelLikeDislikeCounts.setScale(this.scaleNormal);

		this.likeButton = new GenericButton("Like");
		this.likeButton.setTooltip("Like currently selected rating");
		this.likeButton.setHoverColor(this.hoverColor);

		this.dislikeButton = new GenericButton("Dislike");
		this.dislikeButton.setTooltip("Dislike currently selected rating");
		this.dislikeButton.setHoverColor(this.hoverColor);


		this.ratingList = new MOCListWidget();
		this.ratingList.setTooltip("Select a rating from the list");
		this.ratingList.setBackgroundColor(this.textFieldColor);


		//Window buttons
		this.buttonTeleport = new GenericButton("Teleport");
		this.buttonTeleport.setTooltip("Teleport to the rating's location if you have the permission"); 
		this.buttonTeleport.setHoverColor(this.hoverColor);

		this.buttonShowPicture = new GenericButton("Picture");
		this.buttonShowPicture.setTooltip("Shows the picture for the selected rating");
		this.buttonShowPicture.setHoverColor(this.hoverColor);

		this.buttonClose = new GenericButton("X");
		this.buttonClose.setTooltip("Close the window");
		this.buttonClose.setHoverColor(this.hoverColor);
		
		this.buttonToggleVisible = new GenericButton("Hide");
		this.buttonToggleVisible.setTooltip("Hides/Shows rating in the world");
		this.buttonToggleVisible.setHoverColor(this.hoverColor);
		
		this.labelShown = new GenericLabel("");
		this.labelShown.setTooltip("Current status of the comment in the world");
		this.labelShown.setScale(this.scaleNormal);
		
		//Screen shot
		this.screenshot = new GenericTexture();
		this.screenshot.setPriority(RenderPriority.Low);
		this.screenshotCloseButton = new GenericButton("Close");
		this.screenshotCloseButton.setPriority(RenderPriority.Lowest);


		//Attach widgets to the screen
		attachWidgets(plugin, this.background, this.headingTitle, this.headingTitleDescription, this.buttonToggleVisible, this.labelShown);
		attachWidgets(plugin, this.locationLabel, this.playerNameLabel, this.patternNameLabel, this.ratingTypeandValue, this.textTextField, this.likeButton, this.dislikeButton);
		attachWidgets(plugin, this.worldNameLabel, this.labelLikeDislikeCounts, this.ratingList, this.buttonShowPicture, this.buttonTeleport, this.buttonClose);
		attachWidgets(plugin, this.screenshot, this.screenshotCloseButton);

		//Initialize
		this.initialize();

	}
	//================================================================================================================



	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		this.selectedComment = null;
		
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
		this.locationLabel.setX(upRightX-305).setY(upRightY+40);
		this.locationLabel.setWidth(40).setHeight(15);

		this.playerNameLabel.setX(upRightX-305).setY(upRightY+50);
		this.playerNameLabel.setWidth(40).setHeight(15);
		
		this.ratingTypeandValue.setX(upRightX-305).setY(upRightY+60);
		this.ratingTypeandValue.setWidth(40).setHeight(15);
		
		this.patternNameLabel.setX(upRightX-305).setY(upRightY+70);
		this.patternNameLabel.setWidth(40).setHeight(15);
		
		this.textTextField.setX(upRightX-305).setY(upRightY+80);
		this.textTextField.setWidth(300).setHeight(120);
		this.textTextField.setEnabled(false);
		this.textTextField.setText("");
		//-------------------------------------------------------------------


		//-------------------------------------------------------------------
		//Lower Left Corner anchor
		//-------------------------------------------------------------------
		//Ratings
		this.worldNameLabel.setX(upLeftX+5).setY(upLeftY+30);
		this.worldNameLabel.setWidth(40).setHeight(15);
		this.worldNameLabel.setText("World: " + this.player.getLocation().getWorld().getName());

		this.labelLikeDislikeCounts.setText("");
		this.labelLikeDislikeCounts.setX(downLeftX+5).setY(downLeftY-29);
		this.labelLikeDislikeCounts.setWidth(40).setHeight(15);

		this.likeButton.setX(downLeftX+5).setY(downLeftY-20);
		this.likeButton.setWidth(50).setHeight(15);
		this.likeButton.setEnabled(false);
		
		this.dislikeButton.setX(downLeftX+55).setY(downLeftY-20);
		this.dislikeButton.setWidth(50).setHeight(15);
		this.dislikeButton.setEnabled(false);

		this.ratingList.setX(upLeftX+5).setY(upLeftY+40);
		this.ratingList.setWidth(100).setHeight(160);
		this.ratingList.clear();
		for (MOCComment comment : this.plugin.getSQL().getComments()) {
			if (comment != null && comment.getLocation().getWorld().getName().equalsIgnoreCase(this.player.getWorld().getName())) {
				this.ratingList.addItem(new ListWidgetItem("ID: " + comment.getRating_comment_id(), comment.getTitle()));
			}
			
		}
		this.ratingList.clearSelection();
		//-------------------------------------------------------------------



		//Window buttons
		this.buttonToggleVisible.setX(upRightX-105).setY(upRightY+60);
		this.buttonToggleVisible.setWidth(100).setHeight(15);
		this.buttonToggleVisible.setEnabled(false);
		
		this.labelShown.setText("");
		this.labelShown.setX(upRightX-90).setY(upRightY+45);
		this.labelShown.setWidth(40).setHeight(15);
		
		this.buttonShowPicture.setX(downRightX-255).setY(downRightY-20);
		this.buttonShowPicture.setWidth(100).setHeight(15);
		this.buttonShowPicture.setEnabled(false);

		this.buttonTeleport.setX(downRightX-155).setY(downRightY-20);
		this.buttonTeleport.setWidth(100).setHeight(15);
		this.buttonTeleport.setEnabled(false);

		this.buttonClose.setX(upRightX-20).setY(upRightY+5);
		this.buttonClose.setWidth(15).setHeight(15);
		
		//Screen shot
		this.screenshot.setWidth(player.getMainScreen().getWidth() - this.screenBuffer * 4);
		this.screenshot.setHeight(player.getMainScreen().getHeight() - this.screenBuffer * 4);
		this.screenshot.setX(this.screenBuffer * 2).setY(this.screenBuffer * 2);
		this.screenshot.setUrl("");
		this.screenshot.setVisible(false);
		
		this.screenshotCloseButton.setWidth(40).setHeight(15);
		this.screenshotCloseButton.setX(player.getMainScreen().getWidth() / 2 - 20).setY(player.getMainScreen().getHeight() - 20 - this.screenBuffer * 2);
		this.screenshotCloseButton.setVisible(false);

	}
	//================================================================================================================



	//================================================================================================================
	//Open the GUI
	public void open(MOCComment comment){
		this.initialize();
		
		if (comment != null) {
			this.selectedComment = comment;
			loadComment();
		}

		this.player.getMainScreen().attachPopupScreen(this);

		this.setDirty(true);

		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }

	}
	
	private void loadComment() {
		for (int i = 0 ; i < this.ratingList.getItems().length ; i++) {
			if (this.ratingList.getItem(i).getTitle().equalsIgnoreCase("ID: " + this.selectedComment.getRating_comment_id())) {
				this.ratingList.setSelection(i);
				break;
				
			}
			
		}
		
		
		this.textTextField.setText(this.selectedComment.getComment());
		
		this.locationLabel.setText("Location: X" + this.selectedComment.getLocation().getBlockX() + ", Y" + this.selectedComment.getLocation().getBlockY() + ", Z" + this.selectedComment.getLocation().getBlockZ());
		
		this.playerNameLabel.setText("Player: " + this.selectedComment.getPlayerName());
		
		if (this.selectedComment.getPatternName().equalsIgnoreCase("")) this.patternNameLabel.setText(""); 
		else this.patternNameLabel.setText("Pattern: " + this.selectedComment.getPatternName());
		
		this.ratingTypeandValue.setText("Rating: " + this.selectedComment.getRatingTypeName() + " - " + this.selectedComment.getScore());
		
		if (this.selectedComment.isOn()) {
			this.buttonToggleVisible.setText("Hide");
			this.labelShown.setText("Status: Shown");
		} else {
			this.buttonToggleVisible.setText("Show");
			this.labelShown.setText("Status: Hidden");
		}
		
		this.textTextField.setDirty(true);
		this.locationLabel.setDirty(true);
		this.playerNameLabel.setDirty(true);
		this.ratingTypeandValue.setDirty(true);
		this.patternNameLabel.setDirty(true);
		
		//Make sure these are only enabled if user haven't liked the comment yet
		if (this.plugin.getSQL().playerLikedComment(this.selectedComment, this.player)) {
			this.likeButton.setEnabled(false);
			this.dislikeButton.setEnabled(false);
			
		} else {
			this.likeButton.setEnabled(true);
			this.dislikeButton.setEnabled(true);
		}
		this.likeButton.setDirty(true);
		this.dislikeButton.setDirty(true);
		
		this.labelLikeDislikeCounts.setText("Likes:" + this.plugin.getSQL().getLikesCount(this.selectedComment.getRating_comment_id()) + " Dislikes:" +
												this.plugin.getSQL().getDislikesCount(this.selectedComment.getRating_comment_id()));
		this.labelLikeDislikeCounts.setDirty(true);
		
		this.buttonShowPicture.setEnabled(true);
		this.buttonShowPicture.setDirty(true);

		if (this.player.hasPermission("MOCRater.teleport")) this.buttonTeleport.setEnabled(true);
		else this.buttonTeleport.setEnabled(false);
		this.buttonTeleport.setDirty(true);

		if (this.player.hasPermission("MOCRater.ratetoggle")) this.buttonToggleVisible.setEnabled(true);
		else this.buttonToggleVisible.setEnabled(false);
		this.buttonToggleVisible.setDirty(true);
		this.labelShown.setDirty(true);
		
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
		//Close
		if (button.equals(this.buttonClose)) {
			closeWindow();
			
			return;
			
		}

		//Teleport button
		if (button.equals(this.buttonTeleport)) {
			for(Widget widget : getAttachedWidgets()) {
				widget.setVisible(false);
				widget.setDirty(true);
				
			}
			
			setDirty(true);
			
			closeWindow();
			
			this.player.teleport(this.selectedComment.getLocation());
			
			return;
		}

		//Picture button
		if (button.equals(this.buttonShowPicture)) {
			for(Widget widget : getAttachedWidgets()) {
				widget.setDirty(true);
				widget.setVisible(false);
				
			}
			
			this.background.setVisible(true);
			this.background.setDirty(true);
			
			File screenShot = new File(this.plugin.getScreenshotFolder() + File.separator + this.selectedComment.getScreenshot());
			if (screenShot.exists()) {
				SpoutManager.getFileManager().addToCache(plugin, screenShot);
				this.screenshot.setUrl(screenShot.getName());
				
			} else {
				this.screenshot.setUrl("http://www.eecs.ucf.edu/isuelab/images/logo4.png");
				
			}
			
			this.screenshot.setVisible(true);
			this.screenshot.setDirty(true);
			
			this.screenshotCloseButton.setVisible(true);
			this.screenshotCloseButton.setDirty(true);
			
			this.setDirty(true);
			
			return;
			
		}
		
		if (button.equals(this.screenshotCloseButton)) {
			for(Widget widget : getAttachedWidgets()) {
				widget.setDirty(true);
				widget.setVisible(true);
				
			}
			
			this.screenshot.setUrl("");
			this.screenshot.setVisible(false);
			this.screenshot.setDirty(true);
			
			this.screenshotCloseButton.setVisible(false);
			this.screenshotCloseButton.setDirty(true);
			
			this.setDirty(true);
			
			return;
		}
		
		

		//Toggle button
		if (button.equals(this.buttonToggleVisible)) {
			if (this.buttonToggleVisible.getText().equalsIgnoreCase("Show")) {
				this.buttonToggleVisible.setText("Hide");
				this.labelShown.setText("Status: Shown");
				this.buttonToggleVisible.setDirty(true);
				this.labelShown.setDirty(true);
				
				this.plugin.getSQL().setCommentIS_ON(this.selectedComment, true);

				BlockVector v1 = new BlockVector(this.selectedComment.getLocation().getX()-5, this.selectedComment.getLocation().getY()-5, this.selectedComment.getLocation().getZ()-5);
				BlockVector v2 = new BlockVector(this.selectedComment.getLocation().getX()+5, this.selectedComment.getLocation().getY()+5, this.selectedComment.getLocation().getZ()+5);

				//Create new region
				ProtectedCuboidRegion region = new ProtectedCuboidRegion(""+selectedComment.getRating_comment_id(), v1, v2);
				
				//Set greeting flag
				region.setFlag(DefaultFlag.GREET_MESSAGE, ChatColor.RED + "" + ChatColor.BOLD + this.selectedComment.getRatingTypeName() +
						"\nID: " + ChatColor.RESET + ChatColor.GREEN + this.selectedComment.getRating_comment_id() +
						ChatColor.RED + ChatColor.BOLD + " Title: " + ChatColor.RESET + ChatColor.GREEN + this.selectedComment.getTitle() +
						"\n" + this.selectedComment.getComment());
				
				//Add it to the world
				this.plugin.getWorldGuard().getRegionManager(this.player.getWorld()).addRegion(region);
				try { this.plugin.getWorldGuard().getRegionManager(this.player.getWorld()).save(); } catch (ProtectionDatabaseException e) { e.printStackTrace(); }
				
			} else {
				this.buttonToggleVisible.setText("Show");
				this.labelShown.setText("Status: Hidden");
				this.buttonToggleVisible.setDirty(true);
				this.labelShown.setDirty(true);
				
				this.plugin.getSQL().setCommentIS_ON(this.selectedComment, false);

				//Remove region
				this.plugin.getWorldGuard().getRegionManager(this.player.getWorld()).removeRegion(""+selectedComment.getRating_comment_id());
				
			}
			
			return;
		}

		//Like button
		if (button.equals(this.likeButton)) {
			this.plugin.getSQL().likeComment(this.selectedComment, this.player, "likes");
			//Disable the button
			this.likeButton.setEnabled(false);
			this.likeButton.setDirty(true);
			
			this.dislikeButton.setEnabled(false);
			this.dislikeButton.setDirty(true);
			
			this.labelLikeDislikeCounts.setText("Likes:" + this.plugin.getSQL().getLikesCount(this.selectedComment.getRating_comment_id()) + " Dislikes:" +
					this.plugin.getSQL().getDislikesCount(this.selectedComment.getRating_comment_id()));
			this.labelLikeDislikeCounts.setDirty(true);
			
			return;
			
		}
		
		//Dislike button
		if (button.equals(this.dislikeButton)) {
			//Save to database
			this.plugin.getSQL().likeComment(this.selectedComment, this.player, "dislikes");
			//Disable the button
			this.likeButton.setEnabled(false);
			this.likeButton.setDirty(true);
			
			this.dislikeButton.setEnabled(false);
			this.dislikeButton.setDirty(true);

			this.labelLikeDislikeCounts.setText("Likes:" + this.plugin.getSQL().getLikesCount(this.selectedComment.getRating_comment_id()) + " Dislikes:" +
					this.plugin.getSQL().getDislikesCount(this.selectedComment.getRating_comment_id()));
			this.labelLikeDislikeCounts.setDirty(true);
			
			return;
			
		}
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	}

	public void onSelection(ListWidget listWidget) {
		if (this.ratingList != listWidget) return; //Make sure we didn't get something weird

		if (this.ratingList.getSelectedItem() == null) {
			this.selectedComment = null;
			
			this.labelLikeDislikeCounts.setText("");
			this.labelLikeDislikeCounts.setDirty(true);
			
			this.likeButton.setEnabled(false);
			this.likeButton.setDirty(true);

			this.dislikeButton.setEnabled(false);
			this.dislikeButton.setDirty(true);

			this.buttonShowPicture.setEnabled(false);
			this.buttonShowPicture.setDirty(true);

			this.buttonTeleport.setEnabled(false);
			this.buttonTeleport.setDirty(true);

			this.buttonToggleVisible.setEnabled(false);
			this.buttonToggleVisible.setDirty(true);
			
			this.labelShown.setText("");
			this.labelShown.setDirty(true);

			this.textTextField.setText("");
			this.locationLabel.setText("Location:");
			this.playerNameLabel.setText("Player:");
			this.patternNameLabel.setText("Pattern:");
			this.ratingTypeandValue.setText("Rating:");

			this.textTextField.setDirty(true);
			this.locationLabel.setDirty(true);
			this.playerNameLabel.setDirty(true);
			this.patternNameLabel.setDirty(true);
			this.ratingTypeandValue.setDirty(true);

		} else {
			for (MOCComment comment : this.plugin.getSQL().getComments()) {
				if (this.ratingList.getSelectedItem().getTitle().equalsIgnoreCase("ID: " + comment.getRating_comment_id())) {
					this.selectedComment = comment;
					
					this.loadComment();
					
					break;

				}

			}

		}

	}
	//================================================================================================================
}
