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
package com.avrgaming.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.object.Buff;
import com.avrgaming.civcraft.object.StructureSign;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.structure.gui.StructureGUI;
import com.avrgaming.civcraft.util.BlockCoord;
import com.avrgaming.civcraft.util.SimpleBlock;

public class Trommel2 extends Structure {
	
	Player player;
	
	public static final int MAX_CHANCE = CivSettings.getIntegerStructure("trommel.max"); //100%
	//XXX Iron
	public static final int IRON_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.iron");
	public static final int IRON_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.iron");
	//XXX Gold
	public static final int GOLD_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.gold");
	public static final int GOLD_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.gold");
	//XXX Lapis
	public static final int LAPIS_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.lapis");
	public static final int LAPIS_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.lapis");
	//XXX Redstone
	public static final int REDSTONE_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.redstone");
	public static final int REDSTONE_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.redstone");
	//XXX Diamond
	public static final int DIAMOND_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.diamond");
	public static final int DIAMOND_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.diamond");
	//XXX Emerald
	public static final int EMERALD_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.emerald");
	public static final int EMERALD_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.emerald");
	//XXX Hammers
	public static final int HAMMERS_BONUS1 = CivSettings.getIntegerStructure("trommel_bonus1.hammers");
	public static final int HAMMERS_BONUS2 = CivSettings.getIntegerStructure("trommel_bonus2.hammers");
	
	private static int block_level = 1;
	private static int bonus_level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum MineralNew {
		HAMMERS,
		EMERALD,
		DIAMOND,
		REDSTONE,
		LAPIS,
		GOLD,
		IRON
	}
	
	protected Trommel2(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
		setBlockLevel(town.saved_trommel_block_level);
		setBonusLevel(town.saved_trommel_bonus_level);
	}
	
	public Trommel2(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";
		out += "Block Level: "+Trommel2.block_level;
		out += "Bonus Level: "+Trommel2.bonus_level;
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "minecart";
	}
	
	public double getLvl1Bonus(MineralNew mineral) {
		double chance = 0;
		switch (mineral) {
		case HAMMERS:
			chance = HAMMERS_BONUS1;
			break;
		case EMERALD:
			chance = EMERALD_BONUS1;
			break;
		case DIAMOND:
			chance = DIAMOND_BONUS1;
			break;
		case REDSTONE:
			chance = REDSTONE_BONUS1;
			break;
		case LAPIS:
			chance = LAPIS_BONUS1;
			break;
		case GOLD:
			chance = GOLD_BONUS1;
			break;
		case IRON:
			chance = IRON_BONUS1;
		default:
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.EXTRACTION);
		chance += increase;
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		Trommel2.block_level = getTown().saved_trommel_block_level;
		Trommel2.bonus_level = getTown().saved_trommel_bonus_level;
	}
	
	public static int getBlockLevel() {
		return block_level;
	}
	
	public static int getBonusLevel() {
		return bonus_level;
	}
	
	public void setBlockLevel(int blockLevel) {
		Trommel2.block_level = blockLevel;
	}
	
	public void setBonusLevel(int bonusLevel) {
		Trommel2.block_level = bonusLevel;
	}
	
//	public void openInfoChest(Player player, PlayerInteractEvent event) {
//		for (Structure struct : this.getTown().getStructures()) {
//			if (struct instanceof Trommel) {
//				event.setCancelled(true);
//				@SuppressWarnings("unused")
//				ArrayList<StructureChest> chests = struct.getAllChestsById(1);
//				StructureGUI.viewTrommel(player.getPlayer());
//			}
//		}
//	}
	
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	@Override
	public void updateSignText() {
		StructureSign sign = getSignFromSpecialId(0);
		if (sign == null) {
			CivLog.error("sign from special id was null");
			return;
		}
		sign.setText("Information");
		sign.update();
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < Trommel2.block_level) {
			StructureGUI.viewTrommel(player.getPlayer());
		} else {
			StructureGUI.viewTrommel(player.getPlayer());
		}
	}
}