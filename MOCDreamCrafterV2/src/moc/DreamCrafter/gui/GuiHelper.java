package moc.DreamCrafter.gui;

import java.util.List;

import org.getspout.spoutapi.gui.Button;

public class GuiHelper {

	public static void PositionVerticalButtonList(List<Button> buttons, int x, int y, int padding) {
		Button lastButton = null;
		
		for(Button b : buttons) {
			if(lastButton != null) 
				b.setX(lastButton.getX()).setY(lastButton.getY() + lastButton.getHeight() + padding);
			else
				b.setX(x).setY(y);
			lastButton = b;
		}
	}
	
}
