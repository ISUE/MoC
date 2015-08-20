package net.moc.MOCKiosk.GUI;

import net.moc.MOCKiosk.MOCKiosk;
import net.moc.MOCKiosk.GUI.Widgets.MOCListWidget;
import net.moc.MOCKiosk.SQL.MOCKioskKiosk;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.ListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.packet.PacketScreenAction;
import org.getspout.spoutapi.packet.ScreenAction;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BrowserWindow extends GenericPopup {
	//================================================================================================================
	//----------------------------------------------------------------
	private MOCKiosk plugin;
	private SpoutPlayer player;
	private int screenBuffer = 5;
	private float scaleLarge = 1.2F;//, scaleNormal = 1.0F, scaleMedium = 0.7F, scaleSmall = 0.5F;
	private Color backgroundColor = new Color(20,70,110);
	private Color textFieldColor = new Color(20,60,100);
	private Color hoverColor = new Color(50,110,180);
	//----------------------------------------------------------------
	//Background
	private Gradient gradientBackground;
	
	//Title
	private Label labelTitle;
	
	//List
	private MOCListWidget listKiosks;
	
	//Buttons
	private Button buttonView, buttonVisit, buttonShowAll;
	
	//----------------------------------------------------------------
	//================================================================================================================
	
	
	
	//================================================================================================================
	public BrowserWindow(SpoutPlayer player, MOCKiosk plugin) {
        this.plugin = plugin;
        this.player = player;

        //Set window transparent
        this.setTransparent(true);
        
		//Background
		this.gradientBackground = new GenericGradient(this.backgroundColor);
		this.gradientBackground.setPriority(RenderPriority.Highest);

		//Title
		this.labelTitle = new GenericLabel("Kiosk Browser");
		this.labelTitle.setScale(this.scaleLarge);
		
		//List
		this.listKiosks = new MOCListWidget();
		this.listKiosks.setTooltip("Select a kiosk from the list");
		this.listKiosks.setBackgroundColor(this.textFieldColor);

		//Buttons
		this.buttonView = new GenericButton("View");
		this.buttonView.setTooltip("Display kiosk's information");
		this.buttonView.setHoverColor(this.hoverColor);

		this.buttonVisit = new GenericButton("Visit");
		this.buttonVisit.setTooltip("Teleport to the kiosk");
		this.buttonVisit.setHoverColor(this.hoverColor);

		this.buttonShowAll = new GenericButton("Show All");
		this.buttonShowAll.setTooltip("Display all of the kiosks");
		this.buttonShowAll.setHoverColor(this.hoverColor);

		//Attach widgets to the screen
		attachWidgets(plugin, this.gradientBackground, this.labelTitle, this.listKiosks, this.buttonView, this.buttonVisit, this.buttonShowAll);

		//Initialize
		this.initialize();
		
	}
	//================================================================================================================

	
	
	//================================================================================================================
	//Initialize/reset widgets
	public void initialize() {
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

		//Title
		this.labelTitle.setX(upLeftX + 5).setY(upLeftY + 5);
		this.labelTitle.setHeight(15).setWidth(40);
		
		//List
		this.listKiosks.setX(upLeftX+5).setY(upLeftY+25);
		this.listKiosks.setWidth(windowWidth - this.screenBuffer * 2).setHeight(180);
		this.listKiosks.clear();
		for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
			if (!kiosk.getOwnerName().equalsIgnoreCase(this.player.getName())) continue;
			
			String worldName;
			if (kiosk.getLocation() == null) worldName = "N/A";
			else worldName = kiosk.getLocation().getWorld().getName();
			
			this.listKiosks.addItem(new ListWidgetItem("Kiosk ID: " + kiosk.getId() + " Name: " + kiosk.getName(), "Owner: " + kiosk.getOwnerName() + " World: " + worldName));
			
		}
		this.listKiosks.clearSelection();

		//Buttons
		this.buttonView.setX(downLeftX + 5).setY(downLeftY-20);
		this.buttonView.setWidth(100).setHeight(15);
		this.buttonView.setEnabled(false);
		
		this.buttonVisit.setX(downLeftX + windowWidth - 105).setY(downLeftY-20);
		this.buttonVisit.setWidth(100).setHeight(15);
		this.buttonVisit.setEnabled(false);
		
		this.buttonShowAll.setX(downLeftX + windowWidth / 2 - 50).setY(downLeftY-20);
		this.buttonShowAll.setWidth(100).setHeight(15);
		if(this.player.hasPermission("MOCKiosk.admin")) {
			this.buttonShowAll.setEnabled(true);
			this.buttonShowAll.setVisible(true);
		} else {
			this.buttonShowAll.setEnabled(false);
			this.buttonShowAll.setVisible(false);
		}
		
	}
	//================================================================================================================
	
	
	
	//================================================================================================================
	//Open the GUI
	public void open(){
		this.initialize();
		
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
		//View
		if (button.equals(this.buttonView)) {
			closeWindow();
			
			for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
				if (this.listKiosks.getSelectedItem().getTitle().equalsIgnoreCase("Kiosk ID: " + kiosk.getId() + " Name: " + kiosk.getName())) {
					this.plugin.getGui().displayManagerWindowGUI(player, kiosk);
					break;
					
				}
					
			}

		}
		
		//Visit
		if (button.equals(this.buttonVisit)) {
			closeWindow();
			
			for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
				if (this.listKiosks.getSelectedItem().getTitle().equalsIgnoreCase("Kiosk ID: " + kiosk.getId() + " Name: " + kiosk.getName())) {
					this.player.teleport(kiosk.getLocation());
					break;
					
				}
					
			}

		}
		
		//Show all
		if (button.equals(this.buttonShowAll)) {
			this.listKiosks.clear();
			
			for (MOCKioskKiosk kiosk : this.plugin.getSQL().getKiosks()) {
				String worldName;
				if (kiosk.getLocation() == null) worldName = "N/A";
				else worldName = kiosk.getLocation().getWorld().getName();
				
				this.listKiosks.addItem(new ListWidgetItem("Kiosk ID: " + kiosk.getId() + " Name: " + kiosk.getName(), "Owner: " + kiosk.getOwnerName() + " World: " + worldName));
				
			}
			
			this.listKiosks.clearSelection();
			
		}
		
	}
	//================================================================================================================



	public void onSelection(ListWidget listWidget) {
		//Make sure it's our widget
		if (!listWidget.equals(this.listKiosks)) return;
		//TODO
		if (this.listKiosks.getSelectedItem() == null) {
			this.buttonView.setEnabled(false);
			this.buttonView.setDirty(true);
			
			this.buttonVisit.setEnabled(false);
			this.buttonVisit.setDirty(true);
			
		} else {
			this.buttonView.setEnabled(true);
			this.buttonView.setDirty(true);
			
			this.buttonVisit.setEnabled(true);
			this.buttonVisit.setDirty(true);
			
		}
		
	}
	
}
