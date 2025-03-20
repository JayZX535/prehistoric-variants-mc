package com.jayzx535.prehistoricvariants.entity.variant;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

public class SpawnBiomeCondition extends InheritableCondition {

	private final TagKey<Biome> biomeTag;
	
	protected SpawnBiomeCondition(double chance, double inheritanceChanceIn, TagKey<Biome> biomeTagIn, ModStatusCheckerSubcondition modStatusCheckerIn) {
		super(chance, inheritanceChanceIn, modStatusCheckerIn);
		this.biomeTag = biomeTagIn;
	}
	
	public static class Deserializer implements JsonDeserializer<SpawnBiomeCondition> {
        @Override
        public SpawnBiomeCondition deserialize(JsonElement jsonIn, Type typeIn, JsonDeserializationContext contextIn) throws JsonParseException {
        	JsonObject jsonObj = jsonIn.getAsJsonObject();
            return new SpawnBiomeCondition(GsonHelper.getAsDouble(jsonObj, "spawn_chance", 1), GsonHelper.getAsDouble(jsonObj, "inheritance_chance", 0), TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GsonHelper.getAsString(jsonObj, "biome_tag"))),
            	jsonObj.has("required_mods") ? ModStatusCheckerSubcondition.deserializer(jsonObj.getAsJsonObject("required_mods")) : ModStatusCheckerSubcondition.EMPTY);
        }
    }

    public static void save(CompoundTag tag, SpawnBiomeCondition condition) {
    	InheritableCondition.save(tag, condition);
        tag.putString("BiomeTag", condition.biomeTag.location().toString());
    }

    public static SpawnBiomeCondition load(CompoundTag tagIn) {
        return new SpawnBiomeCondition(tagIn.getDouble("SpawnChance"), tagIn.getDouble("InheritanceChance"), TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(tagIn.getString("BiomeTag"))), tagIn.contains("RequiredMods") ? ModStatusCheckerSubcondition.readFromTag(tagIn.getCompound("RequiredMods")) : ModStatusCheckerSubcondition.EMPTY);
    }
	
	public boolean testBiome(Entity entityIn) {
		if (!this.modStatusChecker.requiredModsLoaded()) return false;
		return entityIn.level.getBiome(entityIn.blockPosition()).is(this.biomeTag) && entityIn.level.random.nextDouble() <= this.chance;
	}
}
