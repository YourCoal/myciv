package com.avrgaming.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.avrgaming.civcraft.structure.gui.StructureGUI;
import com.avrgaming.civcraft.util.CivColor;

public class StructureBook extends ItemComponent {
	
	@Override
	public void onPrepareCreate(AttributeUtil attrs) {
		attrs.addLore(CivColor.Gold+"Structure Info");
		attrs.addLore(CivColor.Rose+"<Right Click to Open>");
	}

	
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		StructureGUI.spawnGuiBook(event.getPlayer());
	}
	
	public void onItemSpawn(ItemSpawnEvent event) {
		event.setCancelled(true);
	}
}
