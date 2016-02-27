package com.avrgaming.civcraft.camp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.lorestorage.LoreGuiItem;
import com.avrgaming.civcraft.lorestorage.LoreGuiItemListener;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.util.ItemManager;

public class CampGUI {
	
	public static Inventory garden = null;
	public static Inventory guiInventory = null;
//	public static final int MAX_CHEST_SIZE = 6;
	Camp camp;
	
	public static void viewGarden(Player player) {	
		if (garden == null) {
			garden = Bukkit.getServer().createInventory(player, 9*2, "Garden Information");
			
			garden.setItem(2, LoreGuiItem.build(CivColor.LightBlue+CivColor.BOLD+"Function", ItemManager.getId(Material.FURNACE), 0, 
				CivColor.RESET+"The garden a place in a camp where you are able to grow your",
				CivColor.RESET+"crops, and use them however you want: Eat, Stock, or Longhouse.",
				CivColor.RESET+"The plants on your garden only grow when someone is loading",
				CivColor.RESET+"the chunks of it, and it grows through vanilla growth."
			));
			
			if (Camp.isGardenEnabled() == false) {
				garden.setItem(6, LoreGuiItem.build(CivColor.LightBlue+CivColor.BOLD+"Current State", ItemManager.getId(Material.BEDROCK), 0, 
					CivColor.RESET+CivColor.Red+CivColor.BOLD+"Your Garden Is Disabled! Upgrade using /camp upgrade buy"
				));
			} else {
				garden.setItem(6, LoreGuiItem.build(CivColor.LightBlue+CivColor.BOLD+"Current State", ItemManager.getId(Material.CHEST), 0, 
					CivColor.RESET+CivColor.Green+CivColor.BOLD+"Your Garden Is Enabled! Plant crops to grow it."
				));
			}
			LoreGuiItemListener.guiInventories.put(garden.getName(), garden);
		}
		
		if (player != null && player.isOnline() && player.isValid()) {
			player.openInventory(garden);
		}
	}
	
	//XXX Has to be enabled until I figure out why camps hate signs
	public static void spawnGuiBook(Player player) {
		if (guiInventory == null) {
			guiInventory = Bukkit.getServer().createInventory(player, 2*9, "Camp Information");
	
			ItemStack infoRec = LoreGuiItem.build("garden",  ItemManager.getId(Material.DIAMOND_HOE), 
						0, CivColor.Gold+"<Click To View>");
			infoRec = LoreGuiItem.setAction(infoRec, "OpenStructureInventory");
			infoRec = LoreGuiItem.setActionData(infoRec, "invType", "viewGarden");
			guiInventory.addItem(infoRec);
			
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
		}
		player.openInventory(guiInventory);
	}
}
