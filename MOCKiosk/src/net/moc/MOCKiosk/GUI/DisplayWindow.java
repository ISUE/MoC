package net.moc.MOCKiosk.GUI;

import net.moc.MOCKiosk.MOCKiosk;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;
import net.moc.MOCKiosk.SQL.MOCKioskKioskSlide;
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
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class DisplayWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCKiosk plugin;
	private SpoutPlayer player;
	private int screenBufferX = 75, screenBufferY = 15;
	private float scaleLarge = 1.2F;//, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(20,60,100);
	private Color hoverColor = new Color(50,110,180);
	private MOCKioskKiosk kiosk;
	private MOCKioskKioskSlide slide;
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Title
	private Label labelTitle;
	
	//Picture
	private Texture texturePicture;
	
	//Description
	private TextField textfieldDescription;
	
	//Buttons
	private Button buttonWebsite, buttonEdit;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public DisplayWindow(SpoutPlayer player, MOCKiosk plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Kiosk Title");
		this.labelTitle.setScale(this.scaleLarge);
		
		//Picture
		this.texturePicture = new GenericTexture();
		this.texturePicture.setPriority(RenderPriority.Lowest);

		//Description
		this.textfieldDescription = new GenericTextField();
		this.textfieldDescription.setTooltip("Kiosk description");
		this.textfieldDescription.setFieldColor(this.textFieldColor);
		this.textfieldDescription.setEnabled(false);
		this.textfieldDescription.setMaximumLines(6);
		this.textfieldDescription.setMaximumCharacters(5000);
		
		//Buttons
		this.buttonWebsite = new GenericButton("Website");
		this.buttonWebsite.setTooltip("Display the website for this kiosk");
		this.buttonWebsite.setHoverColor(this.hoverColor);

		this.buttonEdit = new GenericButton("Edit");
		this.buttonEdit.setTooltip("Edit this kiosk");
		this.buttonEdit.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, this.gradientBackground, this.labelTitle, this.texturePicture, this.textfieldDescription, this.buttonWebsite, this.buttonEdit);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		kiosk = null;
		slide = null;
		
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBufferY * 2;
        int upLeftX = this.screenBufferX; 
        int upLeftY = this.screenBufferY;

		//Background
		this.gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		this.gradientBackground.setX(upLeftX).setY(upLeftY);

		//Title
		this.labelTitle.setWidth(40).setHeight(15);
		this.labelTitle.setX(upLeftX + 5).setY(upLeftY + 5);
		this.labelTitle.setText("");
		
		//Picture
		this.texturePicture.setWidth(100);
		this.texturePicture.setHeight(100);
		this.texturePicture.setX(upLeftX + windowWidth / 2 - this.texturePicture.getWidth() / 2).setY(upLeftY + 20);
		this.texturePicture.setUrl("http://www.eecs.ucf.edu/isuelab/images/logo4.png");

		//Description
		this.textfieldDescription.setWidth(windowWidth - 10).setHeight(75);
		this.textfieldDescription.setX(upLeftX + 5).setY(upLeftY + 130);
		this.textfieldDescription.setText("");
		this.textfieldDescription.setEnabled(true);
		
		//Buttons
		this.buttonWebsite.setWidth(40).setHeight(15);
		this.buttonWebsite.setX(upLeftX + 5).setY(upLeftY + 112);

		this.buttonEdit.setWidth(40).setHeight(15);
		this.buttonEdit.setX(upLeftX + windowWidth - 45).setY(upLeftY + 112);

	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(MOCKioskKiosk kiosk) {
		this.initialize();
		
		this.kiosk = kiosk;
		
		for (MOCKioskKioskSlide s : this.plugin.getSQL().getSlides()) {
			if (s.getIndeck_id() == this.kiosk.getPopup_deck_id()) {
				this.slide = s;
				break;
				
			}
			
		}
		
		this.labelTitle.setText(this.slide.getTitle());
		this.textfieldDescription.setText(this.slide.getText());
		
		if (this.kiosk.getOwnerName().equalsIgnoreCase(this.player.getName())) this.buttonEdit.setEnabled(true);
		else this.buttonEdit.setEnabled(false);
		
		if (this.slide.getUrl().length() > 0) this.buttonWebsite.setEnabled(true);
		else this.buttonWebsite.setEnabled(false);

		this.texturePicture.setUrl(this.slide.getImage());

		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		
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
		//Edit button
		if (button.equals(this.buttonEdit)) {
			closeWindow();
			this.plugin.getGui().displayManagerWindowGUI(player, this.kiosk);
		}
		
		//Web site button
		if (button.equals(this.buttonWebsite)) {
			//TODO
		}
		
	}
	//================================================================================================================
}
