package moc.DreamCrafter.gui;

import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.InventoryHandler;
import moc.DreamCrafter.data.WorldData;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericCheckBox;
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

public class InBuildWorldWindow  extends GenericPopup {
	private MOCDreamCrafter plugin;
	private SpoutPlayer player;
	private int padding = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	
	private Gradient gradientBackground;
	private Label titleLabel;
	private GenericButton spawnButton, inventoryButton, leaveButton, endpointButton;
	private GenericCheckBox publishCheckbox;
	
	public InBuildWorldWindow(SpoutPlayer player, MOCDreamCrafter plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
        gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		titleLabel = new GenericLabel("Building World - " + ((Player)player).getWorld().getName());
		titleLabel.setScale(scaleLarge);
		titleLabel.setPriority(RenderPriority.Normal);
		
		publishCheckbox = new GenericCheckBox();
		publishCheckbox.setText("Publish World");
		publishCheckbox.setPriority(RenderPriority.Normal);
		
		spawnButton = new GenericButton("Set Spawn");
		spawnButton.setTooltip("Sets spawn location of world to your current location");
		spawnButton.setHoverColor(hoverColor);
		spawnButton.setPriority(RenderPriority.Normal);
		
		inventoryButton = new GenericButton("Set Inventory");
		inventoryButton.setTooltip("Sets the starting inventory to your current inventory");
		inventoryButton.setHoverColor(hoverColor);
		inventoryButton.setPriority(RenderPriority.Normal);
		
		endpointButton = new GenericButton("Set Endpoint");
		endpointButton.setTooltip("Sets endpoint of world to selected block");
		endpointButton.setHoverColor(hoverColor);
		endpointButton.setPriority(RenderPriority.Normal);
		
		leaveButton = new GenericButton("Leave");
		leaveButton.setTooltip("Leave this world");
		leaveButton.setHoverColor(hoverColor);
		leaveButton.setPriority(RenderPriority.Normal);
		
		attachWidgets(plugin, gradientBackground, titleLabel,
				leaveButton, publishCheckbox, spawnButton, inventoryButton, endpointButton
				);
	}
	
	private void init() {
		int windowWidth = 300;
		int windowHeight = 120;
        int upLeftX = (player.getMainScreen().getWidth() / 2) - (windowWidth / 2); 
        int upLeftY = (player.getMainScreen().getHeight() / 2) - (windowHeight / 2);
        int downLeftX = upLeftX;
        int downLeftY = upLeftY + windowHeight;

		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
		titleLabel.setX(upLeftX + padding).setY(upLeftY + padding);
		titleLabel.setHeight(15).setWidth(40);
		
		publishCheckbox.setX(titleLabel.getX()).setY(titleLabel.getY() + titleLabel.getHeight() + padding);
		publishCheckbox.setHeight(15).setWidth(80);
		
		spawnButton.setX(publishCheckbox.getX()).setY(publishCheckbox.getY() + publishCheckbox.getHeight() + padding);
		spawnButton.setHeight(15).setWidth(80);
		
		inventoryButton.setX(spawnButton.getX()).setY(spawnButton.getY() + spawnButton.getHeight() + padding);
		inventoryButton.setHeight(15).setWidth(80);
		
		endpointButton.setX(inventoryButton.getX()).setY(inventoryButton.getY() + inventoryButton.getHeight() + padding);
		endpointButton.setHeight(15).setWidth(80);
		
		leaveButton.setWidth(60).setHeight(15);
		leaveButton.setX(downLeftX + windowWidth - leaveButton.getWidth() - padding).setY(downLeftY - padding - leaveButton.getHeight());
		
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
		publishCheckbox.setChecked(worldData.isPublished);
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void open(){
//		closeWindow();
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
		
		if(button.equals(spawnButton)) {
			plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName()).spawnpoint = player.getLocation();
			player.sendMessage("Spawnpoint set");
			closeWindow();
		}
		
		if(button.equals(inventoryButton)) {
			plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName()).inventory = InventoryHandler.savePlayerInventory(player);
			player.sendMessage("Inventory set");
			closeWindow();
		}
		
		if(button.equals(endpointButton)) {
			player.performCommand("dc end");
			closeWindow();
		}
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	public void closeWindow() {
		onClose();
		player.getMainScreen().closePopup();
		player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	private void onClose() {
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
		worldData.isPublished = publishCheckbox.isChecked();
		plugin.getPersistentDataHandler().saveAll();
	}
}
