package net.moc.MOCKiosk.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import net.moc.MOCKiosk.MOCKiosk;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;
import net.moc.MOCKiosk.SQL.MOCKioskKioskSlide;
import org.bukkit.block.Block;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.CheckBox;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericCheckBox;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ManagerWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCKiosk plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F;//, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	private ArrayList<Widget> mainWidgets = new ArrayList<Widget>();
	private ArrayList<Widget> popupWidgets = new ArrayList<Widget>();
	private Block block;
	private MOCKioskKioskSlide editSlide;
	private MOCKioskKiosk editKiosk;
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Title
	private Label labelTitle;
	
	//Name
	private Label labelKioskName;
	private TextField textfieldKioskName;
	
	//Near Text
	private Label labelNearText;
	private TextField textfieldNearText;
	
	//Near URL
	private Label labelNearURL;
	private TextField textfieldNearURL;
	
	//Click Text
	private Label labelClickText;
	private TextField textfieldClickText;
	
	//Click URL
	private Label labelClickURL;
	private TextField textfieldClickURL;
	
	//Use popup
	private CheckBox checkboxUsePopup;
	private Label labelUsePopup;
	
	//Buttons Main
	private Button buttonCancel, buttonSave;
	
	//----------------------------
	//Popup Heading
	private Label labelPopupHeading;
	
	//Popup title
	private Label labelPopupTitle;
	private TextField textfieldPopupTitle;
	
	//Popup text
	private Label labelPopupText;
	private TextField textfieldPopupText;
	
	//Popup url
	private Label labelPopupURL;
	private TextField textfieldPopupURL;
	
	//Popup image url
	private Label labelPopupImage;
	private TextField textfieldPopupImage;
	
	//Popup buttons
	private Button buttonPopupBack, buttonPopupSave;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public ManagerWindow(SpoutPlayer player, MOCKiosk plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Kiosk");
		this.labelTitle.setScale(this.scaleLarge);
		
		//Name
		this.labelKioskName = new GenericLabel("Kiosk Name:");
		this.labelKioskName.setScale(this.scaleNormal);
		
		this.textfieldKioskName = new GenericTextField();
		this.textfieldKioskName.setTooltip("Kiosk Name");
		this.textfieldKioskName.setFieldColor(this.textFieldColor);
		this.textfieldKioskName.setEnabled(true);
		this.textfieldKioskName.setMaximumLines(1);
		this.textfieldKioskName.setMaximumCharacters(100);
		
		//Near Text
		this.labelNearText = new GenericLabel("Near Text:");
		this.labelNearText.setScale(this.scaleNormal);

		this.textfieldNearText = new GenericTextField();
		this.textfieldNearText.setTooltip("Near Text");
		this.textfieldNearText.setFieldColor(this.textFieldColor);
		this.textfieldNearText.setEnabled(true);
		this.textfieldNearText.setMaximumLines(4);
		this.textfieldNearText.setMaximumCharacters(1000);
		
		//Near URL
		this.labelNearURL = new GenericLabel("Near URL:");
		this.labelNearURL.setScale(this.scaleNormal);

		this.textfieldNearURL = new GenericTextField();
		this.textfieldNearURL.setTooltip("Near URL");
		this.textfieldNearURL.setFieldColor(this.textFieldColor);
		this.textfieldNearURL.setEnabled(true);
		this.textfieldNearURL.setMaximumLines(1);
		this.textfieldNearURL.setMaximumCharacters(1000);
		
		//Click Text
		this.labelClickText = new GenericLabel("Click Text:");
		this.labelClickText.setScale(this.scaleNormal);

		this.textfieldClickText = new GenericTextField();
		this.textfieldClickText.setTooltip("Click Text");
		this.textfieldClickText.setFieldColor(this.textFieldColor);
		this.textfieldClickText.setEnabled(true);
		this.textfieldClickText.setMaximumLines(4);
		this.textfieldClickText.setMaximumCharacters(1000);
		
		//Click URL
		this.labelClickURL = new GenericLabel("Click URL:");
		this.labelClickURL.setScale(this.scaleNormal);

		this.textfieldClickURL = new GenericTextField();
		this.textfieldClickURL.setTooltip("Click URL");
		this.textfieldClickURL.setFieldColor(this.textFieldColor);
		this.textfieldClickURL.setEnabled(true);
		this.textfieldClickURL.setMaximumLines(1);
		this.textfieldClickURL.setMaximumCharacters(1000);
		
		//Use pop up
		this.checkboxUsePopup = new GenericCheckBox("");
		this.checkboxUsePopup.setTooltip("Use Popup");
		
		this.labelUsePopup = new GenericLabel("Use Popup - You will add data on next page, after pressing 'Save'.");
		this.labelUsePopup.setScale(this.scaleNormal);
		
		//Buttons Main
		this.buttonCancel = new GenericButton("Cancel");
		this.buttonCancel.setTooltip("Cancel");
		this.buttonCancel.setHoverColor(this.hoverColor);
		
		this.buttonSave = new GenericButton("Save");
		this.buttonSave.setTooltip("Save");
		this.buttonSave.setHoverColor(this.hoverColor);
		
		//----------------------------
		//Pop up Heading
		this.labelPopupHeading = new GenericLabel("Kiosk Popup Data");
		this.labelPopupHeading.setScale(this.scaleLarge);
		
		//Pop up title
		this.labelPopupTitle = new GenericLabel("Popup Title:");
		this.labelPopupTitle.setScale(this.scaleNormal);

		this.textfieldPopupTitle = new GenericTextField();
		this.textfieldPopupTitle.setTooltip("Popup Title");
		this.textfieldPopupTitle.setFieldColor(this.textFieldColor);
		this.textfieldPopupTitle.setEnabled(true);
		this.textfieldPopupTitle.setMaximumLines(1);
		this.textfieldPopupTitle.setMaximumCharacters(1000);
		
		//Popup Text
		this.labelPopupText = new GenericLabel("Popup Text:");
		this.labelPopupText.setScale(this.scaleNormal);

		this.textfieldPopupText = new GenericTextField();
		this.textfieldPopupText.setTooltip("Popup Text");
		this.textfieldPopupText.setFieldColor(this.textFieldColor);
		this.textfieldPopupText.setEnabled(true);
		this.textfieldPopupText.setMaximumLines(6);
		this.textfieldPopupText.setMaximumCharacters(5000);
		
		//Popup URL
		this.labelPopupURL = new GenericLabel("Popup URL:");
		this.labelPopupURL.setScale(this.scaleNormal);

		this.textfieldPopupURL = new GenericTextField();
		this.textfieldPopupURL.setTooltip("Popup URL");
		this.textfieldPopupURL.setFieldColor(this.textFieldColor);
		this.textfieldPopupURL.setEnabled(true);
		this.textfieldPopupURL.setMaximumLines(1);
		this.textfieldPopupURL.setMaximumCharacters(1000);
		
		//Popup Image URL
		this.labelPopupImage = new GenericLabel("Image URL:");
		this.labelPopupImage.setScale(this.scaleNormal);

		this.textfieldPopupImage = new GenericTextField();
		this.textfieldPopupImage.setTooltip("Popup Image URL");
		this.textfieldPopupImage.setFieldColor(this.textFieldColor);
		this.textfieldPopupImage.setEnabled(true);
		this.textfieldPopupImage.setMaximumLines(1);
		this.textfieldPopupImage.setMaximumCharacters(1000);
		
		//Popup buttons
		this.buttonPopupBack = new GenericButton("< Back");
		this.buttonPopupBack.setTooltip("Back");
		this.buttonPopupBack.setHoverColor(this.hoverColor);
		
		this.buttonPopupSave = new GenericButton("Save");
		this.buttonPopupSave.setTooltip("Save");
		this.buttonPopupSave.setHoverColor(this.hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, gradientBackground, labelTitle, labelKioskName, textfieldKioskName, labelNearText, textfieldNearText);
		attachWidgets(plugin, labelNearURL, textfieldNearURL, labelClickText, textfieldClickText);
		attachWidgets(plugin, labelClickURL, textfieldClickURL, checkboxUsePopup, labelUsePopup, buttonCancel, buttonSave);
		
		attachWidgets(plugin, labelPopupHeading, labelPopupTitle, textfieldPopupTitle, labelPopupText);
		attachWidgets(plugin, textfieldPopupText, labelPopupURL, textfieldPopupURL, labelPopupImage);
		attachWidgets(plugin, textfieldPopupImage, buttonPopupBack, buttonPopupSave);
		
		this.mainWidgets.addAll(Arrays.asList(labelTitle, labelKioskName, textfieldKioskName, labelNearText, textfieldNearText, labelNearURL,
												textfieldNearURL, labelClickText, textfieldClickText, labelClickURL, textfieldClickURL,
												checkboxUsePopup, labelUsePopup, buttonCancel, buttonSave));
		
		this.popupWidgets.addAll(Arrays.asList(labelPopupHeading, labelPopupTitle, textfieldPopupTitle, labelPopupText, textfieldPopupText, labelPopupURL,
												textfieldPopupURL, labelPopupImage, textfieldPopupImage, buttonPopupBack, buttonPopupSave));

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
		this.editKiosk = null;
		this.editSlide = null;
		this.block = null;
		
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - this.screenBuffer * 2;
		int windowHeight = player.getMainScreen().getHeight() - this.screenBuffer * 2;
        int upLeftX = this.screenBuffer; 
        int upLeftY = this.screenBuffer;
        int downLeftX = this.screenBuffer;
        int downLeftY = player.getMainScreen().getHeight() - this.screenBuffer;

		//Background
		this.gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		this.gradientBackground.setX(upLeftX).setY(upLeftY);

		//--------------------------------------------------------------------------------------------------------------
		//Title
		this.labelTitle.setX(upLeftX + 5).setY(upLeftY + 5);
		this.labelTitle.setHeight(15).setWidth(40);
		
		//Name
		this.labelKioskName.setX(upLeftX + 9).setY(upLeftY + 23);
		this.labelKioskName.setHeight(15).setWidth(40);
		
		this.textfieldKioskName.setText("");
		this.textfieldKioskName.setX(upLeftX + 65).setY(upLeftY + 20);
		this.textfieldKioskName.setHeight(15).setWidth(windowWidth - this.textfieldKioskName.getX());
		
		//Near Text
		this.labelNearText.setX(upLeftX + 12).setY(upLeftY + 43);
		this.labelNearText.setHeight(15).setWidth(40);

		this.textfieldNearText.setText("");
		this.textfieldNearText.setX(upLeftX + 65).setY(upLeftY + 40);
		this.textfieldNearText.setHeight(50).setWidth(windowWidth - this.textfieldNearText.getX());
		
		//Near URL
		this.labelNearURL.setX(upLeftX + 16).setY(upLeftY + 98);
		this.labelNearURL.setHeight(15).setWidth(40);

		this.textfieldNearURL.setText("");
		this.textfieldNearURL.setX(upLeftX + 65).setY(upLeftY + 95);
		this.textfieldNearURL.setHeight(15).setWidth(windowWidth - this.textfieldNearURL.getX());
		
		//Click Text
		this.labelClickText.setX(upLeftX + 14).setY(upLeftY + 118);
		this.labelClickText.setHeight(15).setWidth(40);

		this.textfieldClickText.setText("");
		this.textfieldClickText.setX(upLeftX + 65).setY(upLeftY + 115);
		this.textfieldClickText.setHeight(50).setWidth(windowWidth - this.textfieldClickText.getX());
		
		//Click URL
		this.labelClickURL.setX(upLeftX + 18).setY(upLeftY + 173);
		this.labelClickURL.setHeight(15).setWidth(40);

		this.textfieldClickURL.setText("");
		this.textfieldClickURL.setX(upLeftX + 65).setY(upLeftY + 170);
		this.textfieldClickURL.setHeight(15).setWidth(windowWidth - this.textfieldClickURL.getX());
		
		//Use popup
		this.checkboxUsePopup.setX(upLeftX + 5).setY(upLeftY + 187);
		this.checkboxUsePopup.setHeight(20).setWidth(20);
		this.checkboxUsePopup.setChecked(false);
		
		this.labelUsePopup.setX(upLeftX + 30).setY(upLeftY + 193);
		this.labelUsePopup.setHeight(15).setWidth(40);
		
		//Buttons Main
		this.buttonCancel.setX(downLeftX + 5).setY(downLeftY - 20);
		this.buttonCancel.setHeight(15).setWidth(40);
		
		this.buttonSave.setX(downLeftX + windowWidth - 45).setY(downLeftY - 20);
		this.buttonSave.setHeight(15).setWidth(40);
		//-------------------------------------------------------------------------------------------------------
		//Popup Heading
		this.labelPopupHeading.setX(upLeftX + 5).setY(upLeftY + 5);
		this.labelPopupHeading.setHeight(15).setWidth(40);
		
		//Popup title
		this.labelPopupTitle.setX(upLeftX + 7).setY(upLeftY + 23);
		this.labelPopupTitle.setHeight(15).setWidth(40);

		this.textfieldPopupTitle.setText("");
		this.textfieldPopupTitle.setX(upLeftX + 65).setY(upLeftY + 20);
		this.textfieldPopupTitle.setHeight(15).setWidth(windowWidth - this.textfieldPopupTitle.getX());
		
		//Popup Text
		this.labelPopupText.setX(upLeftX + 6).setY(upLeftY + 43);
		this.labelPopupText.setHeight(15).setWidth(40);

		this.textfieldPopupText.setText("");
		this.textfieldPopupText.setX(upLeftX + 65).setY(upLeftY + 40);
		this.textfieldPopupText.setHeight(75).setWidth(windowWidth - this.textfieldPopupText.getX());
		
		//Popup URL
		this.labelPopupURL.setX(upLeftX + 10).setY(upLeftY + 123);
		this.labelPopupURL.setHeight(15).setWidth(40);

		this.textfieldPopupURL.setText("");
		this.textfieldPopupURL.setX(upLeftX + 65).setY(upLeftY + 120);
		this.textfieldPopupURL.setHeight(15).setWidth(windowWidth - this.textfieldPopupURL.getX());
		
		//Popup Image URL
		this.labelPopupImage.setX(upLeftX + 12).setY(upLeftY + 143);
		this.labelPopupImage.setHeight(15).setWidth(40);

		this.textfieldPopupImage.setText("");
		this.textfieldPopupImage.setX(upLeftX + 65).setY(upLeftY + 140);
		this.textfieldPopupImage.setHeight(15).setWidth(windowWidth - this.textfieldPopupImage.getX());
		
		//Popup buttons
		this.buttonPopupBack.setX(downLeftX + 5).setY(downLeftY - 20);
		this.buttonPopupBack.setHeight(15).setWidth(40);
		
		this.buttonPopupSave.setX(downLeftX + windowWidth - 45).setY(downLeftY - 20);
		this.buttonPopupSave.setHeight(15).setWidth(40);
		//-------------------------------------------------------------------------------------------------------
		
		//Show main widgets and hide pop up ones
		for (Widget widget : this.mainWidgets) {
			widget.setVisible(true);
		}

		for (Widget widget : this.popupWidgets) {
			widget.setVisible(false);
		}
		
	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(MOCKioskKiosk kiosk) {
		this.initialize();
		
		//Load kiosk data
		this.loadKioskData(kiosk);
		
		if (this.editKiosk != null) {
			this.player.getMainScreen().attachPopupScreen(this);
			this.refresh();
			
		}
		
	}

	private void loadKioskData(MOCKioskKiosk kiosk) {
		this.editKiosk = kiosk;
		this.block = kiosk.getLocation().getBlock();

		this.textfieldKioskName.setText(kiosk.getName());

		this.textfieldNearText.setText(kiosk.getNeartext());
		this.textfieldNearURL.setText(kiosk.getNearurl());
		this.textfieldClickText.setText(kiosk.getClicktext());
		this.textfieldClickURL.setText(kiosk.getClickurl());

		this.editSlide = this.plugin.getSQL().getSlide(kiosk.getPopup_deck_id());
		
		if (this.editSlide != null) {
			this.checkboxUsePopup.setChecked(true);
			this.textfieldPopupTitle.setText(this.editSlide.getTitle());
			this.textfieldPopupText.setText(this.editSlide.getText());
			this.textfieldPopupURL.setText(this.editSlide.getUrl());
			this.textfieldPopupImage.setText(this.editSlide.getImage());
			
		}
		
	}
	
	public void open(Block block) {
		this.initialize();
		
		this.block = block;
		
		this.player.getMainScreen().attachPopupScreen(this);
		
		this.refresh();
		
	}
	
	private void refresh() {
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
		//Cancel button
		if (button.equals(this.buttonCancel)) {
			closeWindow();
			
			return;
			
		}
		
		//Save button
		if (button.equals(this.buttonSave)) {
			if (this.checkboxUsePopup.isChecked()) {
				for (Widget widget : this.mainWidgets) {
					widget.setVisible(false);
					widget.setDirty(true);
				}

				for (Widget widget : this.popupWidgets) {
					widget.setVisible(true);
					widget.setDirty(true);
				}
				
			} else {
				this.plugin.getSQL().saveKiosk(this.textfieldKioskName.getText(), this.textfieldNearText.getText(), this.textfieldNearURL.getText(),
						this.textfieldClickText.getText(), this.textfieldClickURL.getText(), this.checkboxUsePopup.isChecked(), this.textfieldPopupTitle.getText(),
						this.textfieldPopupText.getText(), this.textfieldPopupURL.getText(), this.textfieldPopupImage.getText(), 2, this.player.getName(), this.block, this.editKiosk, this.editSlide);
				
				closeWindow();
				
			}

		}
		
		//Popup Back button
		if (button.equals(this.buttonPopupBack)) {
			for (Widget widget : this.mainWidgets) {
				widget.setVisible(true);
				widget.setDirty(true);
			}

			for (Widget widget : this.popupWidgets) {
				widget.setVisible(false);
				widget.setDirty(true);
			}
			
		}
		
		//Popup Save button
		if (button.equals(this.buttonPopupSave)) {
			this.plugin.getSQL().saveKiosk(this.textfieldKioskName.getText(), this.textfieldNearText.getText(), this.textfieldNearURL.getText(),
					this.textfieldClickText.getText(), this.textfieldClickURL.getText(), this.checkboxUsePopup.isChecked(), this.textfieldPopupTitle.getText(),
					this.textfieldPopupText.getText(), this.textfieldPopupURL.getText(), this.textfieldPopupImage.getText(), 2, this.player.getName(), this.block, this.editKiosk, this.editSlide);
			
			closeWindow();
			
		}
		
	}
	//================================================================================================================
}
