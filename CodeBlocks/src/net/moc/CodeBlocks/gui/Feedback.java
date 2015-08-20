package net.moc.CodeBlocks.gui;

import java.util.Arrays;

import net.moc.CodeBlocks.CodeBlocks;
import net.moc.CodeBlocks.gui.widgets.MOCComboBox;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericComboBox;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Feedback extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private CodeBlocks plugin;
	private SpoutPlayer player;
	private int screenBufferX = 5, screenBufferY = 5;
	private float scaleLarge = 1.2F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle;
	
	//Combo
	GenericComboBox comboboxType;
	
	//Text
	GenericTextField textComment;
	
	//Window buttons
	private GenericButton buttonSubmit;
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public Feedback(SpoutPlayer player, CodeBlocks plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        setTransparent(true);

		//Background
		background = new GenericGradient(backgroundColor);
		background.setPriority(RenderPriority.Highest);

		//Title
		labelTitle = new GenericLabel(plugin.getDescription().getName() + ": Feedback");
		labelTitle.setScale(scaleLarge);
		
		//Type
		comboboxType = new MOCComboBox();
		comboboxType.setText("");
		comboboxType.setHoverColor(hoverColor);
		comboboxType.setItems(Arrays.asList("General", "Bug Report", "Feature request"));
		
		//Text
		textComment = new GenericTextField();
		textComment.setMaximumLines(0);
		textComment.setMaximumCharacters(1024);
		textComment.setFieldColor(textFieldColor);
		
		//Buttons
		buttonSubmit = new GenericButton("Submit");
		buttonSubmit.setTooltip("Submit feedback");
		buttonSubmit.setHoverColor(hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, buttonSubmit, comboboxType, textComment);

		//Initialize
		this.initialize();
		
	}

	//================================================================================================================
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - screenBufferX * 2;
		int windowHeight = player.getMainScreen().getHeight() - screenBufferY * 2;
        int upLeftX = screenBufferX; 
        int upLeftY = screenBufferY;
        
		//Background
		background.setHeight(windowHeight).setWidth(windowWidth);
		background.setX(upLeftX).setY(upLeftY);
		
		//Title
		labelTitle.setHeight(15).setWidth(windowWidth);
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		
		//Type
		comboboxType.setX(upLeftX + windowWidth - 105).setY(upLeftY+5);
		comboboxType.setHeight(15).setWidth(100);
		comboboxType.setSelection(0);

		//Text
		textComment.setX(upLeftX+5).setY(upLeftY+25);
		textComment.setHeight(windowHeight - 50).setWidth(windowWidth - 10);
		textComment.setText("");
		
		//Window buttons
		buttonSubmit.setWidth(100).setHeight(15);
		buttonSubmit.setX(upLeftX + windowWidth/2 - 50).setY(upLeftY+windowHeight-20);
		
	}

	//================================================================================================================
	//Open the GUI
	public void open(){
		this.initialize();
		this.player.getMainScreen().attachPopupScreen(this);
		
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
	//================================================================================================================
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		
	}
	
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		if (button.equals(buttonSubmit)) {
			if (textComment.getText().length() > 0) {
				plugin.getUseLogger().submitFeedback(comboboxType.getSelectedItem(), player.getName(), textComment.getText());
				closeWindow();
				
			}
			
		}
		
	}
	
}
