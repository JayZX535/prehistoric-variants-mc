package com.jayzx535.prehistoricvariants.entity.variant;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

public class RandomCondition extends InheritableCondition {
	
	protected RandomCondition(double chance, double inheritanceChanceIn, ModStatusCheckerSubcondition modStatusCheckerIn) {
		super(chance, inheritanceChanceIn, modStatusCheckerIn);
	}
	
	public static class Deserializer implements JsonDeserializer<RandomCondition> {
        @Override
        public RandomCondition deserialize(JsonElement jsonIn, Type typeIn, JsonDeserializationContext contextIn) throws JsonParseException {
        	JsonObject jsonObj = jsonIn.getAsJsonObject();
            return new RandomCondition(GsonHelper.getAsDouble(jsonObj, "spawn_chance", 1), GsonHelper.getAsDouble(jsonObj, "inheritance_chance", 0),
            	jsonObj.has("required_mods") ? ModStatusCheckerSubcondition.deserializer(jsonObj.getAsJsonObject("required_mods")) : ModStatusCheckerSubcondition.EMPTY);
        }
    }

    public static void save(CompoundTag tag, RandomCondition condition) {
    	InheritableCondition.save(tag, condition);
    }

    public static RandomCondition load(CompoundTag tagIn) {
        return new RandomCondition(tagIn.getDouble("SpawnChance"), tagIn.getDouble("InheritanceChance"), tagIn.contains("RequiredMods") ? ModStatusCheckerSubcondition.readFromTag(tagIn.getCompound("RequiredMods")) : ModStatusCheckerSubcondition.EMPTY);
    }
	
	public boolean randomSpawn(Entity entityIn) {
		if (!this.modStatusChecker.requiredModsLoaded()) return false;
		return entityIn.level.random.nextDouble() <= this.chance;
	}
}
