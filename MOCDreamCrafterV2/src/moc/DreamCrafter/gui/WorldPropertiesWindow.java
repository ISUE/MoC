package moc.DreamCrafter.gui;

import java.util.Arrays;
import java.util.List;

import moc.DreamCrafter.MOCDreamCrafter;
import moc.DreamCrafter.data.WorldData;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericCheckBox;
import org.getspout.spoutapi.gui.GenericComboBox;
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

public class WorldPropertiesWindow  extends GenericPopup {
	private MOCDreamCrafter plugin;
	private SpoutPlayer player;
	private int padding = 5;
	private float scaleLarge = 1.2F, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	
	private Color backgroundColor = new Color(20,70,110);
	private Color hoverColor = new Color(50,110,180);
	
	private Gradient gradientBackground;
	private GenericComboBox difficultyCombobox, gameModeCombobox;
	private GenericCheckBox thunderCheckbox, weatherCheckbox;
	private GenericTextField timeTextField;
	private Label titleLabel, timeLabel, difficultyLabel, gamemodeLabel;
	private GenericButton saveButton;
	
	public WorldPropertiesWindow(SpoutPlayer player, MOCDreamCrafter plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
        gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Highest);
		
		titleLabel = new GenericLabel(((Player)player).getWorld().getName() + " - Properties");
		titleLabel.setScale(scaleLarge);
		titleLabel.setPriority(RenderPriority.Normal);
		
		thunderCheckbox = new GenericCheckBox();
		thunderCheckbox.setText("Thunder");
		thunderCheckbox.setPriority(RenderPriority.Normal);
		
		weatherCheckbox = new GenericCheckBox();
		weatherCheckbox.setText("Stormy Weather");
		weatherCheckbox.setPriority(RenderPriority.Normal);
		
		timeTextField = new GenericTextField();
		timeTextField.setPriority(RenderPriority.Normal);
		
		timeLabel = new GenericLabel("Time");
		timeLabel.setScale(scaleNormal);
		timeLabel.setPriority(RenderPriority.Normal);
		timeLabel.setTooltip("Set the time for the world");
		
		difficultyLabel = new GenericLabel("Difficulty");
		difficultyLabel.setScale(scaleNormal);
		difficultyLabel.setPriority(RenderPriority.Normal);
		difficultyLabel.setTooltip("Set the difficulty for the world");
		
		difficultyCombobox =  new GenericComboBox();
		difficultyCombobox.setFormat("%selected%");
		difficultyCombobox.setPriority(RenderPriority.Normal);
		
		gamemodeLabel = new GenericLabel("GameMode");
		gamemodeLabel.setScale(scaleNormal);
		gamemodeLabel.setPriority(RenderPriority.Normal);
		gamemodeLabel.setTooltip("Set the game mode for the world");
		
		gameModeCombobox =  new GenericComboBox();
		gameModeCombobox.setFormat("%selected%");
		gameModeCombobox.setPriority(RenderPriority.Normal);
		
		saveButton = new GenericButton("Save");
		saveButton.setTooltip("Save these settings");
		saveButton.setHoverColor(hoverColor);
		saveButton.setPriority(RenderPriority.Normal);
		
		attachWidgets(plugin, gradientBackground,
				thunderCheckbox, weatherCheckbox, 
				timeTextField, 
				titleLabel, timeLabel, difficultyLabel, gamemodeLabel, 
				difficultyCombobox, gameModeCombobox,
				saveButton
				);
	}
	
	private void init() {
		int windowWidth = 200;
		int windowHeight = player.getMainScreen().getHeight() - this.padding * 4;
        int upLeftX = (player.getMainScreen().getWidth() / 2) - (windowWidth / 2); 
        int upLeftY = 2 * padding;
        int downLeftX = upLeftX;
        int downLeftY = upLeftY + windowHeight;

		gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		gradientBackground.setX(upLeftX).setY(upLeftY);
		
		titleLabel.setX(upLeftX + padding).setY(upLeftY + padding);
		titleLabel.setHeight(15).setWidth(40);
		
		thunderCheckbox.setX(titleLabel.getX() + padding).setY(titleLabel.getY() + titleLabel.getHeight() + padding);
		thunderCheckbox.setHeight(15).setWidth(80);
		
		weatherCheckbox.setX(thunderCheckbox.getX()).setY(thunderCheckbox.getY() + thunderCheckbox.getHeight() + padding);
		weatherCheckbox.setHeight(15).setWidth(80);
		
		timeTextField.setX(thunderCheckbox.getX()).setY(weatherCheckbox.getY() + weatherCheckbox.getHeight() + padding);
		timeTextField.setHeight(15).setWidth(40);
		
		timeLabel.setX(timeTextField.getX() + timeTextField.getWidth() + padding).setY(timeTextField.getY());
		timeLabel.setHeight(15).setWidth(40);
		
		difficultyLabel.setX(timeTextField.getX()).setY(timeTextField.getY() + timeTextField.getHeight() + padding);
		difficultyLabel.setHeight(15).setWidth(40);
		
		difficultyCombobox.setX(difficultyLabel.getX()).setY(difficultyLabel.getY() + difficultyLabel.getHeight() + padding);
		difficultyCombobox.setHeight(15).setWidth(100);
		
		gamemodeLabel.setX(difficultyCombobox.getX()).setY(difficultyCombobox.getY() + difficultyCombobox.getHeight() + padding);
		gamemodeLabel.setHeight(15).setWidth(40);
		
		gameModeCombobox.setX(gamemodeLabel.getX()).setY(gamemodeLabel.getY() + gamemodeLabel.getHeight() + padding);
		gameModeCombobox.setHeight(15).setWidth(100);
		
		saveButton.setWidth(60).setHeight(15);
		saveButton.setX(downLeftX + windowWidth - padding - saveButton.getWidth()).setY(downLeftY - padding - saveButton.getHeight());
				
		loadWorldProperties();
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void open(){
		init();
		player.getMainScreen().attachPopupScreen(this);
		setDirty(true);
		
		for(Widget widget : getAttachedWidgets()) {
			widget.setDirty(true);
		}
	}
				
//-----------------------------------------------------------------------------------------------------------

	private void loadWorldProperties() {
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
		
		// Set the current difficulty
		List<String> difficulties = Arrays.asList(
				Difficulty.EASY.toString(),
				Difficulty.NORMAL.toString(),
				Difficulty.HARD.toString(),
				Difficulty.PEACEFUL.toString()
			);
		
		difficultyCombobox.setItems(difficulties);
		
		for(int i = 0; i < difficulties.size(); i++) {
			if(worldData.difficulty.toString().equals(difficulties.get(i)))
				difficultyCombobox.setSelection(i);
		}
		
		// Set the current gamemode
		List<String> gameModes = Arrays.asList(
				GameMode.SURVIVAL.toString(),
				GameMode.CREATIVE.toString(),
				GameMode.ADVENTURE.toString()
			);
		
		gameModeCombobox.setItems(gameModes);
		
		for(int i = 0; i < gameModes.size(); i++) {
			if(worldData.gameMode.toString().equals(gameModes.get(i)))
				gameModeCombobox.setSelection(i);
		}
		
		
		thunderCheckbox.setChecked(worldData.isThunderEnabled);
		weatherCheckbox.setChecked(worldData.isStormyWeatherEnabled);
		timeTextField.setText(worldData.time);
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void save() {
		WorldData worldData = plugin.getPersistentDataHandler().getWorldDataByName(player.getWorld().getName());
		worldData.difficulty = Difficulty.valueOf(difficultyCombobox.getSelectedItem());
		worldData.gameMode = GameMode.valueOf(gameModeCombobox.getSelectedItem());
		worldData.isThunderEnabled = thunderCheckbox.isChecked();
		worldData.isStormyWeatherEnabled = weatherCheckbox.isChecked();
		worldData.time = timeTextField.getText();
		plugin.getPersistentDataHandler().saveAll();
	}
	
//-----------------------------------------------------------------------------------------------------------

	public void onClick(Button button) {
		if(button.equals(saveButton)) {
			save();
			plugin.getWorldHandler().LoadWorldProperties(player.getWorld().getName(), player);
			plugin.getWorldHandler().SetWorldGamemode(player.getWorld().getName(), GameMode.CREATIVE);
			closeWindow();
		}
	}
	
//-----------------------------------------------------------------------------------------------------------
	
	public void closeWindow() {
		this.player.getMainScreen().closePopup();
		this.player.getMainScreen().setDirty(true);
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
	}
}
