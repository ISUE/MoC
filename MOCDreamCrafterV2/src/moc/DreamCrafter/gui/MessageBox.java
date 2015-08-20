package moc.DreamCrafter.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import moc.DreamCrafter.MOCDreamCrafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MessageBox implements Listener {

	private MOCDreamCrafter plugin;
	private SpoutPlayer player; 
	private Color backgroundColor = new Color(20,70,60);
	private int padding = 5;	
	private int popupPadding = 75;
	private Label labelTitle, message;
	private Gradient gradientBackground;
	private GenericPopup parent;
	private List<Widget> widgets = new ArrayList<Widget>();
	
	public Button OkayButton, CancelButton;
	
	public MessageBox(SpoutPlayer player, MOCDreamCrafter plugin, GenericPopup parent, String title, String text) {
        this.plugin = plugin;
        this.player = player;
        this.parent = parent;
        
        gradientBackground = new GenericGradient(this.backgroundColor);
		gradientBackground.setPriority(RenderPriority.Low);
		
		labelTitle = new GenericLabel(title);
		labelTitle.setScale(1.2f);
		labelTitle.setPriority(RenderPriority.Lowest);
		
		message = new GenericLabel(text);
		message.setScale(1.0f);
		message.setPriority(RenderPriority.Lowest);
		
		OkayButton = new GenericButton("Okay");
		OkayButton.setPriority(RenderPriority.Lowest);
		
		CancelButton = new GenericButton("Cancel");
		CancelButton.setPriority(RenderPriority.Lowest);
		
		widgets.addAll(Arrays.asList(gradientBackground, labelTitle, message, OkayButton, CancelButton));
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	public void initialize() {
		//Corners and size of the window
		int windowWidth = player.getMainScreen().getWidth() - 2*popupPadding;
		int windowHeight = player.getMainScreen().getHeight() - 2*popupPadding;
        int upLeftX = popupPadding; 
        int upLeftY = popupPadding;
        int downLeftX = popupPadding;
        int downLeftY = player.getMainScreen().getHeight() - popupPadding;

		//Background
		this.gradientBackground.setHeight(windowHeight).setWidth(windowWidth);
		this.gradientBackground.setX(upLeftX).setY(upLeftY);

		//Title
		labelTitle.setX(upLeftX + padding).setY(upLeftY + padding);
		labelTitle.setHeight(15).setWidth(140);
		
		message.setX(labelTitle.getX() + padding).setY(labelTitle.getY() + labelTitle.getHeight() + padding);
		message.setHeight(50).setWidth(300);
		
		OkayButton.setX(gradientBackground.getX() + padding).setY(downLeftY - padding - 15);
		OkayButton.setWidth(75).setHeight(15);
		
		CancelButton.setX(OkayButton.getX() + OkayButton.getWidth() + padding).setY(downLeftY - padding - 15);
		CancelButton.setWidth(75).setHeight(15);
	}
	
	public void open(){
		plugin.getLog().info("MessageBox open");
		this.initialize();
		
		for(Widget w : widgets) {
			parent.attachWidget(plugin, w);
			w.setDirty(true);
		}
		
		parent.setDirty(true);
	}
	public void destroy() {
		for(Widget w : widgets)
			parent.removeWidget(w);
		parent.setDirty(true);
	}
	
	@EventHandler
	public void onEvent(ButtonClickEvent event) {		
		if(event.getButton().equals(OkayButton) || event.getButton().equals(CancelButton))
			destroy();
	}
}
