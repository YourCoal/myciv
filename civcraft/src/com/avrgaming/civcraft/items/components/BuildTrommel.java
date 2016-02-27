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
package com.avrgaming.civcraft.items.components;

import gpl.AttributeUtil;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.avrgaming.civcraft.config.ConfigBuildableInfo;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.interactive.InteractiveBuildTrommel;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.structure.Buildable;
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.CallbackInterface;
import com.avrgaming.civcraft.util.CivColor;

public class BuildTrommel extends ItemComponent implements CallbackInterface {
	
	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+"Build a Trommel");
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+"<Right Click To Use>");
		attrUtil.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrUtil.addLore(CivColor.Gold+"Soulbound");
	}
	
	public void buildTrommel(Player player) throws CivException {
		Resident resident = CivGlobal.getResident(player);
		if (resident == null) {
			CivMessage.sendError(player, "You are not a resident! Relog or contact an admin.");
			return;
		}
		
		if (!resident.hasTown()) {
			throw new CivException("You need to be in a town to build a trommel.");
		}
		
		if (resident.hasCamp()) {
			throw new CivException("You cannot build a trommel in a camp.");
		}
			
		/* Build a preview for the structure.  */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Checking structure position... Please wait.");
		ConfigBuildableInfo info = new ConfigBuildableInfo();
		info.id = "s_trommel";
		info.template_base_name = "trommel";
		info.templateYShift = 0;
		info.displayName = "Trommel";
		info.require_tech = "tech_mining";
		info.require_upgrade = "";
		info.require_structure = "";
		info.check_event = "";
		info.effect_event= "";
		info.update_event = "trommel_process";
		info.onBuild_event = "";
		info.limit =  0;
		info.cost = 5000;
		info.upkeep = 1000;
		info.hammer_cost = 800;
		info.max_hitpoints = 200;
		info.points = 1500;
//		//XXX Optional, not normal
		info.destroyable = false;
		info.allow_outside_town = false;
		info.isWonder = false;
		info.regenRate = 0;
		info.tile = false;
		info.allow_demolish = true;
		info.strategic = false;
		info.ignore_floating = false;
		info.has_template = true;
		Buildable.buildVerifyStatic(player, info, player.getLocation(), this);
	}
	
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		} try {
			buildTrommel(event.getPlayer());
		} catch (CivException e) {
			CivMessage.sendError(event.getPlayer(), e.getMessage());
		}
		
		class SyncTask implements Runnable {
			String name;
			
			public SyncTask(String name) {
				this.name = name;
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(name);
				} catch (CivException e) {
					return;
				}
				player.updateInventory();
			}
		}
		TaskMaster.syncTask(new SyncTask(event.getPlayer().getName()));
		return;
	}

	@Override
	public void execute(String playerName) {
		final Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		final Resident resident = CivGlobal.getResident(playerName);
		
		new Thread(new Runnable() {
            public void run() {
                try {
            		CivMessage.sendHeading(player, "Building a Trommel!");
					CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"Do you want to build a trommel here?");
					CivMessage.send(player, CivColor.LightGray+ChatColor.ITALIC+"We are preparing to put a preview of the structure here.");
					CivMessage.send(player, CivColor.LightGray+"(You will be able to cancel this in a few seconds.)");
					Thread.sleep(5000);
					CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
					resident.setInteractiveMode(new InteractiveBuildTrommel(resident.getTown(), player.getLocation(), null));
					ConfigBuildableInfo info = new ConfigBuildableInfo();
					Structure struct = Structure.newStructure(player.getLocation(), info.id = "s_trommel", resident.getTown());
					try {
						struct.buildPlayerPreview(player, player.getLocation());
					} catch (IOException e) {
						e.printStackTrace();
						throw new CivException("Internal IO Error.");
					}
				} catch (InterruptedException | CivException e) {
					e.printStackTrace();
				}
            }
        }).start();
//		CivMessage.sendHeading(player, "Building a Trommel!");
//		CivMessage.send(player, " ");
//		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"Do you want to build a trommel here?");
//		CivMessage.send(player, CivColor.LightGreen+ChatColor.ITALIC+"We have put a preview of the structure here.");
//		CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
//		resident.setInteractiveMode(new InteractiveBuildTrommel(resident.getTown(), player.getLocation(), null));
	}
}