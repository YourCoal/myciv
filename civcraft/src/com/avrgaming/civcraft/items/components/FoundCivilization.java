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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigBuildableInfo;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.interactive.InteractiveCivName;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.structure.Buildable;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.CallbackInterface;
import com.avrgaming.civcraft.util.CivColor;

public class FoundCivilization extends ItemComponent implements CallbackInterface{
	
	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+"Founds a Civilization");
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+"<Right Click To Use>");
		attrUtil.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrUtil.addLore(CivColor.Gold+"Soulbound");
	}
	
	public void foundCiv(Player player) throws CivException {
		Resident resident = CivGlobal.getResident(player);
		if (resident == null) {
			throw new CivException("You must be a registered resident to found a civ. This shouldn't happen. Contact an admin.");
		}
			
		/* Build a preview for the Capitol structure. */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Checking structure position...Please wait.");
		ConfigBuildableInfo info = CivSettings.structures.get("s_capitol");
		Buildable.buildVerifyStatic(player, info, player.getLocation(), this);	
	}
	
	public void onInteract(PlayerInteractEvent event) {
		
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
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
					try {
						foundCiv(player);
					} catch (CivException e) {
						CivMessage.sendError(player, e.getMessage());
					}
				} catch (CivException e) {
					return;
				}
				player.updateInventory();
			}
		}
		TaskMaster.syncTask(new SyncTask(event.getPlayer().getName()));
	}

	@Override
	public void execute(String playerName) {
		
		final Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		
		final Resident resident = CivGlobal.getResident(player);
		
		new Thread(new Runnable() {
            public void run() {
                try {
                	resident.desiredTownLocation = player.getLocation();
                	CivMessage.sendHeading(player, "Founding A Civilization!");
					Thread.sleep(500);
					CivMessage.send(player, CivColor.LightGreen+"You and your possible friends have finally chosen to settle.");
					Thread.sleep(3500);
					CivMessage.send(player, CivColor.LightGreen+"While you may be alone, or have few members, will your new Empire spread?");
					Thread.sleep(3500);
					CivMessage.send(player, CivColor.LightGreen+"Will you expand to new frontiers, dominating your region?");
					Thread.sleep(3500);
					CivMessage.send(player, CivColor.LightGreen+"Can your civilization withstand the test of time, of war, debt, and raids?");
					//Thread.sleep(3500);
					//CivMessage.send(player, CivColor.LightGray+ChatColor.ITALIC+"We are preparing to put a preview of the structure here.");
					CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
					//Thread.sleep(5000);
					//ConfigBuildableInfo info = new ConfigBuildableInfo();
					//Structure struct = Structure.newStructure(player.getLocation(), info.id = "s_capitol", resident.getTown());
					//try {
					//	struct.buildPlayerPreview(player, player.getLocation());
					//} catch (IOException e) {
					//	e.printStackTrace();
					//	throw new CivException("Internal IO Error.");
					//}
					//Thread.sleep(5000);
					CivMessage.sendHeading(player, "Pre-Generating your new capitol...");
					//Thread.sleep(10000);
					Thread.sleep(5000);
					CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"What shall your new Civilization be called?");
					resident.setInteractiveMode(new InteractiveCivName());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        }).start();
		
//		/* Save the location so we dont have to re-validate the structure position. */
//		resident.desiredTownLocation = player.getLocation();
//		CivMessage.sendHeading(player, "Founding A New Civ");
//		CivMessage.send(player, CivColor.LightGreen+"You and your small band of travelers have finally found the chosen land.");
//		CivMessage.send(player, CivColor.LightGreen+"While you are few, will your numbers will grow?");
//		CivMessage.send(player, CivColor.LightGreen+"Will you journey boldy forth into new frontiers?");
//		CivMessage.send(player, CivColor.LightGreen+"Can you build a Civilization that can stand the test of time?");
//		CivMessage.send(player, " ");
//		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"What shall your new Civilization be called?");
//		CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
//		resident.setInteractiveMode(new InteractiveCivName());
	}
}
