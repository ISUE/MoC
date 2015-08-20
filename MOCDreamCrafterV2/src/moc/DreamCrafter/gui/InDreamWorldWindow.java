package moc.DreamCrafter.gui;

import moc.DreamCrafter.MOCDreamCrafter;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class InDreamWorldWindow  extends GenericPopup {
	private MOCDreamCrafter plugin;
	private SpoutPlayer player;
	private int padding = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	
	private Gradient gradientBackground;
	private Label titleLabel;
	private GenericButton leaveButton;
	
	public InDreamWorldWindow(SpoutPlayer player, MOCDreamCrafter plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
        gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		String worldName = ((Player)player).getWorld().getName();
		String[] parts = worldName.split("_");
		
		titleLabel = new GenericLabel("Dreaming World - " + parts[2]);
		titleLabel.setScale(scaleLarge);
		titleLabel.setPriority(RenderPriority.Normal);
		
		leaveButton = new GenericButton("Leave");
		leaveButton.setTooltip("Leave this world");
		leaveButton.setHoverColor(hoverColor);
		leaveButton.setPriority(RenderPriority.Normal);
		
		attachWidgets(plugin, gradientBackground, titleLabel, leaveButton);
	}
	
	private void init() {
		int windowWidth = 250;
		int windowHeight = 80;
        int upLeftX = (player.getMainScreen().getWidth() / 2) - (windowWidth / 2); 
        int upLeftY = (player.getMainScreen().getHeight() / 2) - (windowHeight / 2);
        int downLeftX = upLeftX;
        int downLeftY = upLeftY + windowHeight;

		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
		titleLabel.setX(upLeftX + padding).setY(upLeftY + padding);
		titleLabel.setHeight(15).setWidth(40);
		
		leaveButton.setWidth(60).setHeight(15);
		leaveButton.setX(downLeftX + windowWidth - leaveButton.getWidth() - padding).setY(downLeftY - padding - leaveButton.getHeight());
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void open(){
		closeWindow();
		init();
		player.getMainScreen().attachPopupScreen(this);
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
		}
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void onClick(Button button) {
		if(button.equals(leaveButton)) {
			player.performCommand("dc leave");
			closeWindow();
		}
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	public void closeWindow() {
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
	}
}
