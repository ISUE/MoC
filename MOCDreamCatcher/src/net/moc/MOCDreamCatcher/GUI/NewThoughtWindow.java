package net.moc.MOCDreamCatcher.GUI;

import net.moc.MOCDreamCatcher.MOCDreamCatcher;
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

public class NewThoughtWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCDreamCatcher plugin;
	private SpoutPlayer player;
	private int screenBufferX = 150, screenBufferY = 90;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(60,60,60);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private GenericGradient background;
	
	//Title
	private GenericLabel labelTitle;
	
	//Text field
	private GenericTextField textThoughtName;
	
	//Window buttons
	private GenericButton buttonOK, buttonCancel;
	
	//----------------------------------------------------------------
	//================================================================================================================

	public NewThoughtWindow(SpoutPlayer player, MOCDreamCatcher plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);

		//Background
		this.background = new GenericGradient(backgroundColor);
		this.background.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("New Thought Name");
		
		//Text field
		this.textThoughtName = new GenericTextField();
		this.textThoughtName.setFieldColor(textFieldColor);
		this.textThoughtName.setMaximumLines(1);
		
		//Window buttons
		this.buttonOK = new GenericButton("OK");
		this.buttonOK.setHoverColor(hoverColor);
		
		this.buttonCancel = new GenericButton("Cancel");
		this.buttonCancel.setHoverColor(hoverColor);
		
		//Attach widgets to the screen
		attachWidgets(plugin, background, labelTitle, textThoughtName, buttonOK, buttonCancel);
		
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
		
		//Text field
		textThoughtName.setText("");
		textThoughtName.setX(upLeftX+5).setY(upLeftY+20);
		textThoughtName.setWidth(windowWidth-10).setHeight(15);
		
		//Window buttons
		buttonOK.setX(upLeftX + 5).setY(upLeftY + 40);
		buttonOK.setWidth((windowWidth - 10) / 2).setHeight(15);
		
		buttonCancel.setX(upLeftX + 5 + (windowWidth - 10) / 2).setY(upLeftY + 40);
		buttonCancel.setWidth((windowWidth - 10) / 2).setHeight(15);
		
		refresh();
		
	}
	
	//================================================================================================================
	//On button click
	public void onClick(Button button) {
		//Close button
		if (button.equals(buttonCancel)) { closeWindow(); plugin.getGUI().displayGUI(player); return; }
		
		if (button.equals(buttonOK)) {
			//Get thought name
			String newThoughtName = textThoughtName.getText().trim();
			newThoughtName = newThoughtName.replaceAll("[^A-Za-z0-9]", "");
			
			//Make sure not empty
			if (newThoughtName.length() == 0) return;
			
			//Make sure name is not taken
			if (plugin.getDreamNet().getPlayer(player.getName()).getThought(newThoughtName) != null) return;
			
			//Start new thought
			plugin.getDreamNet().createThought(player.getName(), newThoughtName);
			
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
	private void refresh() {
		setDirty(true);
		for(Widget widget : getAttachedWidgets()) { widget.setDirty(true); }
		
	}
	
}
