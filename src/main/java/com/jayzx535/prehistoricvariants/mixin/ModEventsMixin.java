package com.jayzx535.prehistoricvariants.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.github.teamfossilsarcheology.fossil.entity.prehistoric.base.EntityInfo;
import com.github.teamfossilsarcheology.fossil.entity.prehistoric.base.Prehistoric;
import com.github.teamfossilsarcheology.fossil.event.ModEvents;
import com.jayzx535.prehistoricvariants.IBonusVariantEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(ModEvents.class)
public class ModEventsMixin {

	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/event/ModEvents;growEntity(Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/EntityInfo;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;setAgeInDays(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	private static void growEntity(EntityInfo embryo, LivingEntity parent, CallbackInfo callbackIn, Random randomIn, ServerLevel serverLevelIn, Entity entityIn, int intIn, Prehistoric prehistoricIn) {
		if (prehistoricIn instanceof IBonusVariantEntity bonusVariantPrehistoric) bonusVariantPrehistoric.applyPrehistoricVariantConditions();
	}
}
