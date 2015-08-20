package net.moc.CodeBlocks.blocks;

import net.moc.CodeBlocks.CodeBlocks;
import org.getspout.spoutapi.material.item.GenericCustomItem;

public class ToolItem extends GenericCustomItem {
	private static String name = "Tool Item";
	private static String texture = "toolitem.png";
	
	public ToolItem(CodeBlocks plugin) {
		super(plugin, name, plugin.getBlockImageURL() + texture);
	}

}
