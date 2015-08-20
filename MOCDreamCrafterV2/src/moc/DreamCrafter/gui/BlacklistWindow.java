package moc.DreamCrafter.gui;

import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.WorldData;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
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
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BlacklistWindow  extends GenericPopup {
	private MOCDreamCrafter plugin;
	private SpoutPlayer player;
	private int padding = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F;//, scaleMedium = 0.7F, scaleSmall = 0.5F;
	
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	
	private Gradient gradientBackground, panelBackground;
	private Label titleLabel, propertiesLabel, blacklistLabel;
	private GenericButton publishButton, saveButton, leaveButton;
	private GenericTextField blacklistTextField;
	private GenericCheckBox publishCheckbox;
	
	public BlacklistWindow(SpoutPlayer player, MOCDreamCrafter plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
        gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		titleLabel = new GenericLabel(((Player)player).getWorld().getName());
		titleLabel.setScale(scaleLarge);
		titleLabel.setPriority(RenderPriority.Normal);
		
		blacklistLabel = new GenericLabel("Block Black List");
		blacklistLabel.setScale(scaleNormal);
		blacklistLabel.setPriority(RenderPriority.Normal);
		blacklistLabel.setTooltip("Enter block names or ids separated by spaces");
		
		blacklistTextField = new GenericTextField();
		blacklistTextField.setPriority(RenderPriority.Normal);
		
		saveButton = new GenericButton("Save");
		saveButton.setTooltip("Save these settings");
		saveButton.setHoverColor(hoverColor);
		saveButton.setPriority(RenderPriority.Normal);
		
		attachWidgets(plugin, gradientBackground, titleLabel,
				saveButton,
				blacklistLabel, blacklistTextField
				);
	}
	
	private void init() {
		int windowWidth = 300;
		int windowHeight = player.getMainScreen().getHeight() - this.padding * 4;
        int upLeftX = (player.getMainScreen().getWidth() / 2) - (windowWidth / 2); 
        int upLeftY = 2 * padding;
        int downLeftX = upLeftX;
        int downLeftY = upLeftY + windowHeight;

		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
		titleLabel.setX(upLeftX + padding).setY(upLeftY + padding);
		titleLabel.setHeight(15).setWidth(40);
		
		blacklistLabel.setX(titleLabel.getX() + padding).setY(titleLabel.getY() + titleLabel.getHeight() + padding);
		blacklistLabel.setHeight(15).setWidth(40);
		
		blacklistTextField.setX(upLeftX + padding).setY(blacklistLabel.getY() + blacklistLabel.getHeight() + padding);
		blacklistTextField.setHeight(windowHeight - (6 * padding) - titleLabel.getHeight() - blacklistLabel.getHeight()).setWidth(windowWidth - 2 * padding);
		blacklistTextField.setMaximumCharacters(2000).setMaximumLines(2);
		
		saveButton.setWidth(60).setHeight(15);
		saveButton.setX(downLeftX + windowWidth - padding - saveButton.getWidth()).setY(downLeftY - padding - saveButton.getHeight());
		
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
		blacklistTextField.setText(worldData.blacklist);
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void onClick(Button button) {
		if(button.equals(saveButton)) {
			WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
			worldData.setBlacklist(blacklistTextField.getText(), player);
			plugin.getPersistentDataHandler().saveAll();
			closeWindow();
		}
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void open(){
		close();
		init();
		player.getMainScreen().attachPopupScreen(this);
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
		}
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
	}
}
