/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
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
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.structure.Trommel;
import com.avrgaming.civcraft.structure.Trommel.Mineral;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.MultiInventory;

public class TrommelAsyncTask extends CivAsyncTask {
	
	Trommel trommel;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(Trommel trommel, String msg) {
		if (debugTowns.contains(trommel.getTown().getName())) {
			CivLog.warning("TrommelDebug:"+trommel.getTown().getName()+":"+msg);
		}
	}	
	
	public TrommelAsyncTask(Structure trommel) {
		this.trommel = (Trommel)trommel;
	}
	
	public void processTrommelUpdate() {
		if (!trommel.isActive()) {
			debug(trommel, "trommel inactive...");
			return;
		}
		
		debug(trommel, "Processing trommel...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = trommel.getAllChestsById(1);
		ArrayList<StructureChest> destinations = trommel.getAllChestsById(2);
		
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for trommel in town:"+trommel.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();
		
		try {
			for (StructureChest src : sources) {
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					trommel.skippedCounter++;
					return;
				}
				source_inv.addInventory(tmp);
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					trommel.skippedCounter++;
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
				/* Trommel destination chest is full, stop processing. */
				return;
			}
			
		} catch (InterruptedException e) {
			return;
		}
		
		debug(trommel, "Processing trommel:"+trommel.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < trommel.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				
				//XXX Block Lvl 1, Bonus Lvl 1
				if (Trommel.getBonusLevel() == 1 && Trommel.getBlockLevel() >= 1 && ItemManager.getId(stack) == CivData.COBBLESTONE) {
					try {
						this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.COBBLESTONE, 1));
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Trommel.BONUS1_MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.HAMMERS))*randMax))) {
						int rand2 = rand.nextInt(randMax);
						if (rand2 < (randMax/8)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 32);
						} else if (rand2 < (randMax/5)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 20);
						} else if (rand2 < (randMax/4)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 16);
						} else if (rand2 < (randMax/2)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 8);
						} else {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
						}
					} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.EMERALD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
					} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.DIAMOND))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.DIRTGRAVEL))*randMax))) {
						int rand3 = rand.nextInt(randMax);
						if (rand3 < (randMax/3)) {
							newItem = ItemManager.createItemStack(CivData.GRAVEL, 1);
						} else {
							newItem = ItemManager.createItemStack(CivData.DIRT, 1);
						}
					}  else {
						newItem = ItemManager.createItemStack(CivData.AIR, 1);
					}
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(trommel, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				//XXX Block Lvl 1, Bonus Lvl 2
				if (Trommel.getBonusLevel() == 2 && Trommel.getBlockLevel() >= 1 && ItemManager.getId(stack) == CivData.COBBLESTONE) {
					try {
						this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.COBBLESTONE, 1));
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Trommel.BONUS1_MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.HAMMERS))*randMax))) {
						int rand2 = rand.nextInt(randMax);
						if (rand2 < (randMax/8)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 32);
						} else if (rand2 < (randMax/5)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 20);
						} else if (rand2 < (randMax/4)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 16);
						} else if (rand2 < (randMax/2)) {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 8);
						} else {
							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
						}
					} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.EMERALD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
					} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.DIAMOND))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.DIRTGRAVEL))*randMax))) {
						int rand3 = rand.nextInt(randMax);
						if (rand3 < (randMax/3)) {
							newItem = ItemManager.createItemStack(CivData.GRAVEL, 1);
						} else {
							newItem = ItemManager.createItemStack(CivData.DIRT, 1);
						}
					}  else {
						newItem = ItemManager.createItemStack(CivData.AIR, 1);
					}
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(trommel, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				
				
				//XXX Block Lvl 3, Bonus Lvl 1
				if (ItemManager.getId(stack) == CivData.STONE) {
					if (Trommel.getBonusLevel() == 1 && Trommel.getBlockLevel() >= 3 && ItemManager.getData(stack) == ItemManager.getData(ItemManager.getMaterialData(CivData.STONE, CivData.GRANITE))) {
						try {
							this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.GRANITE));
						} catch (InterruptedException e) {
							return;
						}
						
						// Attempt to get special resources
						Random rand = new Random();
						int randMax = Trommel.BONUS1_MAX;
						int rand1 = rand.nextInt(randMax);
						ItemStack newItem;
						if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.HAMMERS))*randMax))) {
							int rand2 = rand.nextInt(randMax);
							if (rand2 < (randMax/8)) {
									newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 32);
							} else if (rand2 < (randMax/5)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 20);
							} else if (rand2 < (randMax/4)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 16);
							} else if (rand2 < (randMax/2)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 8);
							} else {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
							}
						} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.EMERALD))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
						} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.DIAMOND))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
						} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.GOLD))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
						} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.IRON))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
						} else if (rand1 < ((int)((trommel.getLvl1Bonus(Mineral.DIRTGRAVEL))*randMax))) {
							int rand3 = rand.nextInt(randMax);
							if (rand3 < (randMax/3)) {
								newItem = ItemManager.createItemStack(CivData.GRAVEL, 1);
							} else {
								newItem = ItemManager.createItemStack(CivData.DIRT, 1);
							}
						}  else {
							newItem = ItemManager.createItemStack(CivData.AIR, 1);
						}
						//Try to add the new item to the dest chest, if we cant, oh well.
						try {
							debug(trommel, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
					//XXX Block Lvl 3, Bonus Lvl 2
					if (Trommel.getBonusLevel() == 2 && Trommel.getBlockLevel() >= 3 && ItemManager.getData(stack) == ItemManager.getData(ItemManager.getMaterialData(CivData.STONE, CivData.GRANITE))) {
						try {
							this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.GRANITE));
						} catch (InterruptedException e) {
							return;
						}
						
						// Attempt to get special resources
						Random rand = new Random();
						int randMax = Trommel.BONUS1_MAX;
						int rand1 = rand.nextInt(randMax);
						ItemStack newItem;
						if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.HAMMERS))*randMax))) {
							int rand2 = rand.nextInt(randMax);
							if (rand2 < (randMax/8)) {
									newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 32);
							} else if (rand2 < (randMax/5)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 20);
							} else if (rand2 < (randMax/4)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 16);
							} else if (rand2 < (randMax/2)) {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 8);
							} else {
								newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:hammer"), 4);
							}
						} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.EMERALD))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
						} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.DIAMOND))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
						} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.GOLD))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
						} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.IRON))*randMax))) {
							newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
						} else if (rand1 < ((int)((trommel.getLvl2Bonus(Mineral.DIRTGRAVEL))*randMax))) {
							int rand3 = rand.nextInt(randMax);
							if (rand3 < (randMax/3)) {
								newItem = ItemManager.createItemStack(CivData.GRAVEL, 1);
							} else {
								newItem = ItemManager.createItemStack(CivData.DIRT, 1);
							}
						}  else {
							newItem = ItemManager.createItemStack(CivData.AIR, 1);
						}
						//Try to add the new item to the dest chest, if we cant, oh well.
						try {
							debug(trommel, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}	
		}
		trommel.skippedCounter = 0;
	}
	
	
	
	@Override
	public void run() {
		if (this.trommel.lock.tryLock()) {
			try {
				try {
					if (this.trommel.getTown().getGovernment().id.equals("gov_theocracy") || this.trommel.getTown().getGovernment().id.equals("gov_monarchy")){
						Random rand = new Random();
						int randMax = 100;
						int rand1 = rand.nextInt(randMax);
						Double chance = CivSettings.getDouble(CivSettings.structureConfig, "trommel.penalty_rate") * 100;
						if (rand1 < chance)
						{
							processTrommelUpdate();
							debug(this.trommel, "Not penalized");
						} else {

							debug(this.trommel, "Skip Due to Penalty");
						}
					} else {
						processTrommelUpdate();
						if (this.trommel.getTown().getGovernment().id.equals("gov_despotism")) {
							debug(this.trommel, "Doing Bonus");
							processTrommelUpdate();
						}
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.trommel.lock.unlock();
			}
		} else {
			debug(this.trommel, "Failed to get lock while trying to start task, aborting.");
		}
	}

}