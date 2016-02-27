package com.avrgaming.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivTaskAbortException;
import com.avrgaming.civcraft.lorestorage.LoreMaterial;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.object.StructureChest;
import com.avrgaming.civcraft.structure.Granary;
import com.avrgaming.civcraft.structure.Granary.BrHam;
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.MultiInventory;

public class GranaryAsyncTask extends CivAsyncTask {
	
	Granary granary;
	public static HashSet<String> debugTowns = new HashSet<String>();
	
	public static void debug(Granary granary, String msg) {
		if (debugTowns.contains(granary.getTown().getName())) {
			CivLog.warning("GranaryDebug:"+granary.getTown().getName()+":"+msg);
		}
	}
	
	public GranaryAsyncTask(Structure granary) {
		this.granary = (Granary)granary;
	}
	
	public void processGranaryUpdate() {
		if (!granary.isActive()) {
			debug(granary, "granary inactive...");
			return;
		}
		
		debug(granary, "Processing trommel...");
		ArrayList<StructureChest> sources = granary.getAllChestsById(2);
		ArrayList<StructureChest> destinations = granary.getAllChestsById(3);
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for granary in town:"+granary.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();
		try {
			for (StructureChest src : sources) {		
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					granary.skippedCounter++;
					return;
				}
				source_inv.addInventory(tmp);
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					granary.skippedCounter++;
					return;
				}
				dest_inv.addInventory(tmp);
				for (ItemStack stack : tmp.getContents()) {
					if (stack == null) {
						full = false;
						break;
					}
				}
			}
			if (full) {
				return;
			}
		} catch (InterruptedException e) {
			return;
		}
		
		debug(granary, "Processing granary:"+granary.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < granary.skippedCounter+1; i++) {
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				if (Granary.getHammerLevel() == 1 && ItemManager.getId(stack) == CivData.BREAD) {
					try {
						this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.BREAD, 16));
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = 10000;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((granary.getLevel1(BrHam.HAMMER1_ADD))*randMax))) {
						int rand2 = rand.nextInt(randMax);
						if (rand2 < (randMax/10)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 20);
						} else if (rand2 < (randMax/8)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 16);
						} else if (rand2 < (randMax/6)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 12);
						} else if (rand2 < (randMax/4)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 8);
						} else if (rand2 < (randMax/2)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 6);
						} else {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
						}
					}  else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
					}
					//Try to add the new item to the dest chest, if we cant, oh well.
					 try {
						debug(granary, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}	
		}
		granary.skippedCounter = 0;
	}
	
	@Override
	public void run() {
		if (this.granary.lock.tryLock()) {
			try {
				try {
					if (this.granary.getTown().getGovernment().id.equals("gov_anarchy")) {
						Random rand = new Random();
						int randMax = 100;
						int rand1 = rand.nextInt(randMax);
						Double chance = CivSettings.getDouble(CivSettings.structureConfig, "trommel.penalty_rate") * 100;
						if (rand1 < chance) {
							processGranaryUpdate();
							debug(this.granary, "Not penalized");
						} else {
							debug(this.granary, "Skip Due to Penalty");
						}
					} else {
						processGranaryUpdate();
						if (this.granary.getTown().getGovernment().id.equals("gov_monarchy")) {
							debug(this.granary, "Doing Bonus");
							processGranaryUpdate();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.granary.lock.unlock();
			}
		} else {
			debug(this.granary, "Failed to get lock while trying to start task, aborting.");
		}
	}
}
