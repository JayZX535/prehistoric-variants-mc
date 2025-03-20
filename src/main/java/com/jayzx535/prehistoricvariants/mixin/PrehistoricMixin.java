package com.jayzx535.prehistoricvariants.mixin;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.github.teamfossilsarcheology.fossil.entity.prehistoric.base.Prehistoric;
import com.github.teamfossilsarcheology.fossil.entity.variant.VariantCondition;
import com.github.teamfossilsarcheology.fossil.entity.variant.VariantRegistry;
import com.jayzx535.prehistoricvariants.IBonusVariantEntity;
import com.jayzx535.prehistoricvariants.PrehistoricVariants;
import com.jayzx535.prehistoricvariants.entity.variant.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

@Mixin(Prehistoric.class)
public abstract class PrehistoricMixin extends TamableAnimal implements IBonusVariantEntity {
	
	@Shadow
	private Map<VariantRegistry.RegistryObject<?>, VariantCondition.WithVariant<?>> allVariants;
	
	protected PrehistoricMixin(EntityType<? extends TamableAnimal> tamableIn, Level levelIn) {
		super(tamableIn, levelIn);
	}
	
	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", at = @At("TAIL"))
	public void finalizeSpawn(ServerLevelAccessor levelIn, DifficultyInstance difficultyIn, MobSpawnType reasonIn, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> callbackReturnableIn) {
		this.applyPrehistoricVariantConditions();
		return;
	}
	
	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;procreate(Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;)V", at = @At(value = "INVOKE", target = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	public void procreate(Prehistoric mateIn, CallbackInfo callbackIn, Calendar calendarIn, Entity entityIn, Prehistoric prehistoricIn) {
		// Since this is called after finalizeSpawn for the baby, inherited variants have a chance to overwrite
		if ((Object) prehistoricIn instanceof PrehistoricMixin prehistoricBaby) {
			if (this.getType().equals(prehistoricIn.getType())) {
				if (this.allVariants.containsKey(PrehistoricVariants.randomCondition())) {
					VariantCondition.WithVariant<?> conditionWithVariant = this.allVariants.get(PrehistoricVariants.randomCondition());
					if (conditionWithVariant.condition() instanceof RandomCondition randomCondition && randomCondition.testInheritance(this.random)) {
						PrehistoricVariants.getLogger().debug("Baby of type " + prehistoricBaby.getType().getRegistryName().toString() + " inherited the random variant " + conditionWithVariant.variant().getVariantId() + " from its parent.");
						prehistoricBaby.setVariant(PrehistoricVariants.randomCondition(), conditionWithVariant);
					}
				}
				if (this.allVariants.containsKey(PrehistoricVariants.spawnBiomeCondition())) {
					VariantCondition.WithVariant<?> conditionWithVariant = this.allVariants.get(PrehistoricVariants.spawnBiomeCondition());
					if (conditionWithVariant.condition() instanceof SpawnBiomeCondition biomeCondition && biomeCondition.testInheritance(this.random)) {
						PrehistoricVariants.getLogger().debug("Baby of type " + prehistoricBaby.getType().getRegistryName().toString() + " inherited the biome variant " + conditionWithVariant.variant().getVariantId() + " from its parent.");
						prehistoricBaby.setVariant(PrehistoricVariants.spawnBiomeCondition(), conditionWithVariant);
					}
				}
			}
		}
	}
	
	@Shadow
	private <T extends VariantCondition> void setVariant(VariantRegistry.RegistryObject<?> type, @NotNull VariantCondition.WithVariant<T> pair) {}
	@Shadow
	private void clearVariant(VariantRegistry.RegistryObject<?> type) {}

	@Override
	public void applyPrehistoricVariantConditions() {
		Prehistoric thisPrehistoric = (Prehistoric) (Object) this;
		for (VariantCondition.WithVariant<RandomCondition> pair : thisPrehistoric.variantsByCondition(RandomCondition.class)) {
            if (pair.condition().randomSpawn(thisPrehistoric)) {
				PrehistoricVariants.getLogger().debug("Assigned random variant " + pair.variant().getVariantId() + " to entity " + thisPrehistoric.getUUID() + " (type: " + thisPrehistoric.getType().getRegistryName().toString() + ")).");
            	this.setVariant(PrehistoricVariants.randomCondition(), pair);
                break;
            } else if (this.hasMatchingVariant(PrehistoricVariants.randomCondition(), pair)) {
            	this.clearVariant(PrehistoricVariants.randomCondition());
            }
        }
		
		for (VariantCondition.WithVariant<SpawnBiomeCondition> pair : thisPrehistoric.variantsByCondition(SpawnBiomeCondition.class)) {
            if (pair.condition().testBiome(thisPrehistoric)) {
				PrehistoricVariants.getLogger().debug("Assigned biome variant " + pair.variant().getVariantId() + " to entity " + thisPrehistoric.getUUID() + " (type: " + thisPrehistoric.getType().getRegistryName().toString() + ")).");
            	this.setVariant(PrehistoricVariants.spawnBiomeCondition(), pair);
                break;
            } else if (this.hasMatchingVariant(PrehistoricVariants.spawnBiomeCondition(), pair)) {
            	this.clearVariant(PrehistoricVariants.spawnBiomeCondition());
            }
        }
	}
	
	@Unique
	public boolean hasMatchingVariant(VariantRegistry.RegistryObject<?> typeIn, VariantCondition.WithVariant<?> pairIn) {
		return this.allVariants.containsKey(typeIn) && Objects.equals(this.allVariants.get(typeIn).condition(), pairIn.condition());
	}
}
