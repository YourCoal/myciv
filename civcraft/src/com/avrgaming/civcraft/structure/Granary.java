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

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.util.BlockCoord;
import com.avrgaming.civcraft.util.SimpleBlock;

public class Granary extends Structure {
	
	public static final int MAX_CHANCE = CivSettings.getIntegerStructure("granary.max"); //100%
	public static final int HAMMER_1_ADD = CivSettings.getIntegerStructure("granary.lvl_1_add"); //100%
	public static final int HAMMER_1_SUB = CivSettings.getIntegerStructure("granary.lvl_1_sub"); //100%
	
	private static int hammer_level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum BrHam {
		HAMMER1_ADD,
		HAMMER1_SUB
	}
	
	protected Granary(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		setHammerLevel(town.saved_granary_hammer_level);
	}
	
	public Granary(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";
		out += "Hammer Level: "+Granary.hammer_level;
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "chest";
	}
	
	public double getLevel1(BrHam BrHam) {
		double chance = 0;
		switch (BrHam) {
		case HAMMER1_ADD:
			chance = HAMMER_1_ADD;
			break;
		case HAMMER1_SUB:
			chance = HAMMER_1_SUB;
		default:
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
//		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.EXTRACTION);
//		chance += increase;
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		Granary.hammer_level = getTown().saved_granary_hammer_level;
	}
	
	public static int getHammerLevel() {
		return hammer_level;
	}
	
	public void setHammerLevel(int hammerLevel) {
		Granary.hammer_level = hammerLevel;
	}
}
