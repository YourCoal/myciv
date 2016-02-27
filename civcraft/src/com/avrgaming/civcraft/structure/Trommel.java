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

public class Trommel extends Structure {
	
	//XXX For All Levels
	private static final double DIRT_GRAVEL = CivSettings.getDoubleStructure("trommel.dirtgravel");
	//XXX Level 1
	public static final int BONUS1_MAX = CivSettings.getIntegerStructure("trommel_bonus1.max"); //100%
	private static final double BONUS1_IRON = CivSettings.getDoubleStructure("trommel_bonus1.iron");
	private static final double BONUS1_GOLD = CivSettings.getDoubleStructure("trommel_bonus1.gold");
	private static final double BONUS1_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus1.diamond");
	private static final double BONUS1_EMERALD = CivSettings.getDoubleStructure("trommel_bonus1.emerald");
	private static final double BONUS1_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus1.hammers");
	//XXX Level 2
	public static final int BONUS2_MAX = CivSettings.getIntegerStructure("trommel_bonus2.max"); //100%
	private static final double BONUS2_IRON = CivSettings.getDoubleStructure("trommel_bonus2.iron");
	private static final double BONUS2_GOLD = CivSettings.getDoubleStructure("trommel_bonus2.gold");
	private static final double BONUS2_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus2.diamond");
	private static final double BONUS2_EMERALD = CivSettings.getDoubleStructure("trommel_bonus2.emerald");
	private static final double BONUS2_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus2.hammers");
	//XXX Level 3
	public static final int BONUS3_MAX = CivSettings.getIntegerStructure("trommel_bonus3.max"); //100%
	private static final double BONUS3_IRON = CivSettings.getDoubleStructure("trommel_bonus3.iron");
	private static final double BONUS3_GOLD = CivSettings.getDoubleStructure("trommel_bonus3.gold");
	private static final double BONUS3_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus3.diamond");
	private static final double BONUS3_EMERALD = CivSettings.getDoubleStructure("trommel_bonus3.emerald");
	private static final double BONUS3_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus3.hammers");
	//XXX Level 4
	public static final int BONUS4_MAX = CivSettings.getIntegerStructure("trommel_bonus4.max"); //100%
	private static final double BONUS4_IRON = CivSettings.getDoubleStructure("trommel_bonus4.iron");
	private static final double BONUS4_GOLD = CivSettings.getDoubleStructure("trommel_bonus4.gold");
	private static final double BONUS4_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus4.diamond");
	private static final double BONUS4_EMERALD = CivSettings.getDoubleStructure("trommel_bonus4.emerald");
	private static final double BONUS4_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus4.hammers");
	//XXX Level 5
	public static final int BONUS5_MAX = CivSettings.getIntegerStructure("trommel_bonus5.max"); //100%
	private static final double BONUS5_IRON = CivSettings.getDoubleStructure("trommel_bonus5.iron");
	private static final double BONUS5_GOLD = CivSettings.getDoubleStructure("trommel_bonus5.gold");
	private static final double BONUS5_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus5.diamond");
	private static final double BONUS5_EMERALD = CivSettings.getDoubleStructure("trommel_bonus5.emerald");
	private static final double BONUS5_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus5.hammers");
	//XXX Level 6
	public static final int BONUS6_MAX = CivSettings.getIntegerStructure("trommel_bonus6.max"); //100%
	private static final double BONUS6_IRON = CivSettings.getDoubleStructure("trommel_bonus6.iron");
	private static final double BONUS6_GOLD = CivSettings.getDoubleStructure("trommel_bonus6.gold");
	private static final double BONUS6_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus6.diamond");
	private static final double BONUS6_EMERALD = CivSettings.getDoubleStructure("trommel_bonus6.emerald");
	private static final double BONUS6_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus6.hammers");
	//XXX Level 7
	public static final int BONUS7_MAX = CivSettings.getIntegerStructure("trommel_bonus7.max"); //100%
	private static final double BONUS7_IRON = CivSettings.getDoubleStructure("trommel_bonus7.iron");
	private static final double BONUS7_GOLD = CivSettings.getDoubleStructure("trommel_bonus7.gold");
	private static final double BONUS7_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus7.diamond");
	private static final double BONUS7_EMERALD = CivSettings.getDoubleStructure("trommel_bonus7.emerald");
	private static final double BONUS7_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus7.hammers");
	//XXX Level 8
	public static final int BONUS8_MAX = CivSettings.getIntegerStructure("trommel_bonus8.max"); //100%
	private static final double BONUS8_IRON = CivSettings.getDoubleStructure("trommel_bonus8.iron");
	private static final double BONUS8_GOLD = CivSettings.getDoubleStructure("trommel_bonus8.gold");
	private static final double BONUS8_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus8.diamond");
	private static final double BONUS8_EMERALD = CivSettings.getDoubleStructure("trommel_bonus8.emerald");
	private static final double BONUS8_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus8.hammers");
	//XXX Level 9
	public static final int BONUS9_MAX = CivSettings.getIntegerStructure("trommel_bonus9.max"); //100%
	private static final double BONUS9_IRON = CivSettings.getDoubleStructure("trommel_bonus9.iron");
	private static final double BONUS9_GOLD = CivSettings.getDoubleStructure("trommel_bonus9.gold");
	private static final double BONUS9_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus9.diamond");
	private static final double BONUS9_EMERALD = CivSettings.getDoubleStructure("trommel_bonus9.emerald");
	private static final double BONUS9_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus9.hammers");
	//XXX Level 1
	public static final int BONUS10_MAX = CivSettings.getIntegerStructure("trommel_bonus1.max"); //100%
	private static final double BONUS10_IRON = CivSettings.getDoubleStructure("trommel_bonus1.iron");
	private static final double BONUS10_GOLD = CivSettings.getDoubleStructure("trommel_bonus1.gold");
	private static final double BONUS10_DIAMOND = CivSettings.getDoubleStructure("trommel_bonus1.diamond");
	private static final double BONUS10_EMERALD = CivSettings.getDoubleStructure("trommel_bonus1.emerald");
	private static final double BONUS10_HAMMERS = CivSettings.getDoubleStructure("trommel_bonus1.hammers");
	
	
	private static int block_level = 1;
	private static int bonus_level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum Mineral {
		HAMMERS,
		EMERALD,
		DIAMOND,
		GOLD,
		IRON,
		DIRTGRAVEL
	}
	
	protected Trommel(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
		setBlockLevel(town.saved_trommel_block_level);
		setBonusLevel(town.saved_trommel_bonus_level);
	}
	
	public Trommel(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";
		out += "Block Level: "+Trommel.block_level;
		out += "Bonus Level: "+Trommel.bonus_level;
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "minecart";
	}
	
	public double getLvl1Bonus(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case HAMMERS:
			chance = BONUS1_HAMMERS;
			break;
		case EMERALD:
			chance = BONUS1_EMERALD;
			break;
		case DIAMOND:
			chance = BONUS1_DIAMOND;
			break;
		case GOLD:
			chance = BONUS1_GOLD;
			break;
		case IRON:
			chance = BONUS1_IRON;
			break;
		case DIRTGRAVEL:
			chance = DIRT_GRAVEL;
		default:
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getLvl2Bonus(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case HAMMERS:
			chance = BONUS2_HAMMERS;
			break;
		case EMERALD:
			chance = BONUS2_EMERALD;
			break;
		case DIAMOND:
			chance = BONUS2_DIAMOND;
			break;
		case GOLD:
			chance = BONUS2_GOLD;
			break;
		case IRON:
			chance = BONUS2_IRON;
			break;
		case DIRTGRAVEL:
			chance = DIRT_GRAVEL;
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getLvl3Bonus(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case HAMMERS:
			chance = BONUS3_HAMMERS;
			break;
		case EMERALD:
			chance = BONUS3_EMERALD;
			break;
		case DIAMOND:
			chance = BONUS3_DIAMOND;
			break;
		case GOLD:
			chance = BONUS3_GOLD;
			break;
		case IRON:
			chance = BONUS3_IRON;
			break;
		case DIRTGRAVEL:
			chance = DIRT_GRAVEL;
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getLvl4Bonus(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case HAMMERS:
			chance = BONUS4_HAMMERS;
			break;
		case EMERALD:
			chance = BONUS4_EMERALD;
			break;
		case DIAMOND:
			chance = BONUS4_DIAMOND;
			break;
		case GOLD:
			chance = BONUS4_GOLD;
			break;
		case IRON:
			chance = BONUS4_IRON;
			break;
		case DIRTGRAVEL:
			chance = DIRT_GRAVEL;
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.EXTRACTION);
		chance += increase;
//		try {
//			if (this.getTown().getGovernment().id.equals("gov_despotism")) {
//				chance *= CivSettings.getDouble(CivSettings.structureConfig, "trommel.despotism_rate");
//			} else if (this.getTown().getGovernment().id.equals("gov_theocracy") || this.getTown().getGovernment().id.equals("gov_monarchy")){
//				chance *= CivSettings.getDouble(CivSettings.structureConfig, "trommel.penalty_rate");
//			}
//		} catch (InvalidConfiguration e) {
//			e.printStackTrace();
//		}
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		Trommel.block_level = getTown().saved_trommel_block_level;
		Trommel.bonus_level = getTown().saved_trommel_bonus_level;
	}
	
	public static int getBlockLevel() {
		return block_level;
	}
	
	public static int getBonusLevel() {
		return bonus_level;
	}
	
	public void setBlockLevel(int block_level) {
		Trommel.block_level = block_level;
	}
	
	public void setBonusLevel(int bonus_level) {
		Trommel.bonus_level = bonus_level;
	}
	
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	public void updateSignText() {
		StructureSign sign = getSignFromSpecialId(0);
		if (sign == null) {
			CivLog.error("sign from special id was null");
			return;
		}
		sign.setText("Information:");
		sign.setText("Punch me!");
		sign.update();
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < Trommel.block_level && special_id < Trommel.bonus_level) {
			StructureGUI.viewTrommel(player.getPlayer());
		} else {
			StructureGUI.viewTrommel(player.getPlayer());
		}
	}
}