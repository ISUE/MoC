package net.moc.MOCChemistry.GUI.Widget;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.inventory.SpoutItemStack;

public class MOCItemWidget extends GenericContainer {
	private GenericLabel itemCount;
	private GenericItemWidget itemWidget;
	private GenericGradient gradientBackground;

	public MOCItemWidget() {
		super();
		this.setLayout(ContainerType.OVERLAY);
		this.width = 20;
		this.height = 20;
		
		this.itemCount = new GenericLabel();
		this.itemCount.setX(0).setY(0);
		this.itemCount.setWidth(this.width).setHeight(this.height);
		this.itemCount.setPriority(RenderPriority.Lowest);
		
		this.itemWidget = new GenericItemWidget();
		this.itemWidget.setTypeId(1);
		this.itemWidget.setData((short) 0);
		this.itemWidget.setX(0).setY(0);
		this.itemWidget.setWidth(this.width).setHeight(this.height);
		this.itemWidget.setPriority(RenderPriority.Low);
		this.itemWidget.setVisible(false);
		
		this.gradientBackground = new GenericGradient(new Color(150,150,150));
		this.gradientBackground.setX(0).setY(0);
		this.gradientBackground.setWidth(this.getWidth()).setHeight(this.getHeight());
		
		this.addChildren(itemWidget, itemCount, gradientBackground);
		
	}
	
	public MOCItemWidget setWidth(int width) {
		super.setWidth(width);
		updateGUI();
		return this;
		
	}
	
	public MOCItemWidget setHeight(int height) {
		super.setHeight(height);
		updateGUI();
		return this;
		
	}
	
	public void setItem(SpoutItemStack item) {
		if (item == null || item.getAmount() < 1) item = new SpoutItemStack(0);

		if (item.getType() == Material.AIR) {
			this.itemWidget.setTypeId(1);
			this.itemWidget.setData((short) 0);
			this.itemWidget.setVisible(false);
			
		} else {
			this.itemWidget.setTypeId(item.getTypeId());
			this.itemWidget.setData(item.getDurability());
			this.itemWidget.setVisible(true);
			
		}
		
		
		if (item.getAmount() > 1) this.itemCount.setText("" + item.getAmount());
		else this.itemCount.setText("");
		
		updateTooltip(item);
		
		this.itemWidget.setDirty(true);
		this.itemCount.setDirty(true);
		this.gradientBackground.setDirty(true);
		
	}

	private void updateGUI() {
		this.itemWidget.setWidth(this.width).setHeight(this.height);
		this.itemWidget.setDirty(true);
		
		this.itemCount.setWidth(this.width).setHeight(this.height);
		this.itemCount.setDirty(true);
		
		this.gradientBackground.setWidth(this.width).setHeight(this.height);
		this.gradientBackground.setDirty(true);

	}

	private void updateTooltip(SpoutItemStack item) {
		String tooltip = formatName(item.getMaterial().getName());

		if (item.getEnchantments() != null && item.getEnchantments().size() > 0) {
			for (Enchantment e : item.getEnchantments().keySet()) {
				tooltip = tooltip + "\n" + ChatColor.GRAY + formatName(e.getName()) + roman(item.getEnchantments().get(e));

			}

		}

		this.itemWidget.setTooltip(tooltip);

	}
	
	private String formatName(String name) {
		if (name == null) return null;
		
		String[] parts = name.replace('_', ' ').toLowerCase().split(" ");
		String retval = "";
		
		for (String s : parts) { retval = retval.concat(Character.toUpperCase(s.charAt(0)) + s.substring(1) + " "); }
		
		return retval;
		
	}
	
	private String roman(int n) {
		switch(n) {
		case 1: return "I";
		case 2: return "II";
		case 3: return "III";
		case 4: return "IV";
		case 5: return "V";
		case 6: return "VI";
		case 7: return "VII";
		case 8: return "VIII";
		case 9: return "IX";
		case 10: return "X";
		default: return n + "";

		}
		
	}
	
}
