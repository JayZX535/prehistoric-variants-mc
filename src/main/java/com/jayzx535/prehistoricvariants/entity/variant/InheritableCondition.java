package com.jayzx535.prehistoricvariants.entity.variant;
import java.util.Random;

import com.github.teamfossilsarcheology.fossil.entity.variant.VariantCondition;

import net.minecraft.nbt.CompoundTag;

public abstract class InheritableCondition extends VariantCondition {
	protected final double inheritanceChance;
	protected ModStatusCheckerSubcondition modStatusChecker;
	
	protected InheritableCondition(double chance, double inheritanceChanceIn, ModStatusCheckerSubcondition modStatusCheckerIn) {
		super(chance);
		this.inheritanceChance = inheritanceChanceIn;
		this.modStatusChecker = modStatusCheckerIn;
	}

    public static void save(CompoundTag tag, InheritableCondition condition) {
    	tag.putDouble("SpawnChance", condition.chance);
    	tag.putDouble("InheritanceChance", condition.inheritanceChance);
    	CompoundTag checkerTag = condition.modStatusChecker.writeToTag();
    	if (!checkerTag.isEmpty()) tag.put("RequiredMods", checkerTag);
    }
	
	public boolean testInheritance(Random randomIn) {
		if (!this.modStatusChecker.requiredModsLoaded()) return false;
		boolean inheritance = randomIn.nextDouble() <= this.inheritanceChance;
		return inheritance;
	}
}
