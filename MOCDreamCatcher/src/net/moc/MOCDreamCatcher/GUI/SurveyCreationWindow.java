package net.moc.MOCDreamCatcher.GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import net.moc.MOCDreamCatcher.MOCDreamCatcher;
import net.moc.MOCDreamCatcher.GUI.widgets.MOCComboBox;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
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

public class SurveyCreationWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCDreamCatcher plugin;
	private SpoutPlayer player;
	private int screenBufferX = 5, screenBufferY = 5;
	private float scaleLarge = 1.0F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;

	//Title
	private GenericLabel labelTitle;
	
	//Questions
	private GenericLabel labelQuestion01;
	private MOCComboBox comboQuestion01;

	private GenericLabel labelQuestion02;
	private MOCComboBox comboQuestion02;

	private GenericLabel labelQuestion03;
	private MOCComboBox comboQuestion03;

	private GenericLabel labelQuestion04;
	private MOCComboBox comboQuestion04;

	private GenericLabel labelQuestion05;
	private MOCComboBox comboQuestion05;

	//Comments
	private GenericTextField textComments;

	//Window buttons
	private GenericButton buttonClose, buttonSubmit;

	//----------------------------------------------------------------
	//================================================================================================================

	public SurveyCreationWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
		this.plugin = plugin;
		this.player = player;

		//Set window transparent
		setTransparent(true);

		//Background
		background = new GenericGradient(backgroundColor);
		background.setPriority(RenderPriority.Highest);

		//Title
		labelTitle = new GenericLabel(plugin.getDescription().getFullName());
		labelTitle.setScale(scaleLarge);

		//Questions
		labelQuestion01 = new GenericLabel("Rate overall creation experience");

		comboQuestion01 = new MOCComboBox();
		comboQuestion01.setItems(Arrays.asList("5-Excellent", "4-Good", "3-Average", "2-Fair", "1-Poor", ""));
		comboQuestion01.setHoverColor(this.hoverColor);

		labelQuestion02 = new GenericLabel("Did you have enough tools to create the dream?");

		comboQuestion02 = new MOCComboBox();
		comboQuestion02.setItems(Arrays.asList("Yes", "No", ""));
		comboQuestion02.setHoverColor(this.hoverColor);

		labelQuestion03 = new GenericLabel("Rate the difficulty of using tool provided");

		comboQuestion03 = new MOCComboBox();
		comboQuestion03.setItems(Arrays.asList("5-Very Easy", "4-Easy", "3-Average", "2-Difficult", "1-Impossible", ""));
		comboQuestion03.setHoverColor(this.hoverColor);

		labelQuestion04 = new GenericLabel("Will you create other worlds in the future?");

		comboQuestion04 = new MOCComboBox();
		comboQuestion04.setItems(Arrays.asList("Yes", "Maybe", "No", ""));
		comboQuestion04.setHoverColor(this.hoverColor);

		labelQuestion05 = new GenericLabel("Was this world inspired / based on another world?");

		comboQuestion05 = new MOCComboBox();
		comboQuestion05.setItems(Arrays.asList("Yes", "No", ""));
		comboQuestion05.setHoverColor(this.hoverColor);

		//Comments
		textComments = new GenericTextField();
		textComments.setTooltip("Enter other comments here");
		textComments.setFieldColor(this.textFieldColor);
		textComments.setMaximumLines(8);
		textComments.setMaximumCharacters(4000);

		//Window buttons
		buttonClose = new GenericButton("X");
		buttonClose.setTooltip("Close");
		buttonClose.setHoverColor(hoverColor);

		buttonSubmit = new GenericButton("Submit");
		buttonSubmit.setTooltip("Submit survey");
		buttonSubmit.setHoverColor(hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, buttonClose, buttonSubmit, textComments);
		attachWidgets(plugin, labelQuestion01, labelQuestion02, labelQuestion03, labelQuestion04, labelQuestion05);
		attachWidgets(plugin, comboQuestion01, comboQuestion02, comboQuestion03, comboQuestion04, comboQuestion05);

		//Initialize
		initialize();

	}

	//================================================================================================================
	//Initialize/reset widgets
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
		labelTitle.setX(upLeftX+5).setY(upLeftY+5);
		labelTitle.setHeight(15).setWidth(windowWidth);

		//Questions
		labelQuestion01.setX(upLeftX+110).setY(upLeftY+20);
		labelQuestion01.setHeight(15).setWidth(40);
		
		comboQuestion01.setX(upLeftX+5).setY(upLeftY+18);
		comboQuestion01.setHeight(15).setWidth(100);
		comboQuestion01.setText("");
		comboQuestion01.setSelection(5);
		
		
		labelQuestion02.setX(upLeftX+110).setY(upLeftY+35);
		labelQuestion02.setHeight(15).setWidth(40);
		
		comboQuestion02.setX(upLeftX+5).setY(upLeftY+33);
		comboQuestion02.setHeight(15).setWidth(100);
		comboQuestion02.setText("");
		comboQuestion02.setSelection(2);
		
		
		labelQuestion03.setX(upLeftX+110).setY(upLeftY+50);
		labelQuestion03.setHeight(15).setWidth(40);
		
		comboQuestion03.setX(upLeftX+5).setY(upLeftY+48);
		comboQuestion03.setHeight(15).setWidth(100);
		comboQuestion03.setText("");
		comboQuestion03.setSelection(5);
		
		
		labelQuestion04.setX(upLeftX+110).setY(upLeftY+65);
		labelQuestion04.setHeight(15).setWidth(40);
		
		comboQuestion04.setX(upLeftX+5).setY(upLeftY+63);
		comboQuestion04.setHeight(15).setWidth(100);
		comboQuestion04.setText("");
		comboQuestion04.setSelection(3);
		
		
		labelQuestion05.setX(upLeftX+110).setY(upLeftY+80);
		labelQuestion05.setHeight(15).setWidth(40);
		
		comboQuestion05.setX(upLeftX+5).setY(upLeftY+78);
		comboQuestion05.setHeight(15).setWidth(100);
		comboQuestion05.setText("");
		comboQuestion05.setSelection(2);
		
		//Comments
		textComments.setX(upLeftX+5).setY(upLeftY+95);
		textComments.setWidth(windowWidth-10).setHeight(110);
		textComments.setText("");
		textComments.setFocus(true);
		
		//Window buttons
		buttonClose.setX(upLeftX + windowWidth - 20).setY(upLeftY+5);
		buttonClose.setWidth(15).setHeight(15);

		buttonSubmit.setX(upLeftX + windowWidth/2 - 30).setY(upLeftY + windowHeight - 20);
		buttonSubmit.setWidth(60).setHeight(15);

	}

	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonClose)) { closeWindow(); return; }

		if (button.equals(buttonSubmit)) {
			String thoughtName = "UnknownThought";
			try { thoughtName = player.getWorld().getName().split("_")[player.getLocation().getWorld().getName().split("_").length-1]; } catch (ArrayIndexOutOfBoundsException e) {}
			
			String surveyName = thoughtName + "-" + plugin.getConfiguration().getDateString() + ".txt";
			
			File surveyDir = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "survey" + File.separator + player.getName() + File.separator + "thinking");
			surveyDir.mkdirs();
			
			File surveyFile = new File(surveyDir.getAbsolutePath() + File.separator + surveyName);
			
			try {
				PrintStream ps = new PrintStream(surveyFile);
				
				ps.println(labelQuestion01.getText() + " : " + comboQuestion01.getSelectedItem());
				ps.println(labelQuestion02.getText() + " : " + comboQuestion02.getSelectedItem());
				ps.println(labelQuestion03.getText() + " : " + comboQuestion03.getSelectedItem());
				ps.println(labelQuestion04.getText() + " : " + comboQuestion04.getSelectedItem());
				ps.println(labelQuestion05.getText() + " : " + comboQuestion05.getSelectedItem());
				ps.println("Comments :");
				ps.println(textComments.getText());
				
				ps.close();
				
			} catch (FileNotFoundException e) { e.printStackTrace(); }
			
			
			closeWindow();
			return;
			
		}

	}

	//================================================================================================================
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);

		//Redo close Popup - just to make sure....
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));

	}


	//================================================================================================================
	public void open() {
		initialize();
		
		player.getMainScreen().attachPopupScreen(this);

		refresh();

	}

	//================================================================================================================
	private void refresh() { setDirty(true); for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); } }
	
}
