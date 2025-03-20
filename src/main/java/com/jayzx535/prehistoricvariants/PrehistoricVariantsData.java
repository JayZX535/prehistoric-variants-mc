package com.jayzx535.prehistoricvariants;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = PrehistoricVariants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PrehistoricVariantsData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
    	DataGenerator generator = event.getGenerator();
    	ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    	if (event.includeServer()) {
        	generator.addProvider(new PrehistoricBiomeTags(generator, existingFileHelper));
    	}
    }
    
    public static class PrehistoricBiomeTags extends BiomeTagsProvider {
    	
    	public static final TagKey<Biome> SNOWY_BIOMES = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(PrehistoricVariants.MODID, "snowy_biomes"));
    	public static final TagKey<Biome> SNOWY_SMILODON_BIOMES = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(PrehistoricVariants.MODID, "snowy_smilodon_biomes"));
    	
    	public PrehistoricBiomeTags(DataGenerator dataGeneratorIn, @Nullable ExistingFileHelper existingFileHelperIn) {
    		super(dataGeneratorIn, PrehistoricVariants.MODID, existingFileHelperIn);
    	}
    	
    	@Override
    	protected void addTags() {
    		this.tag(SNOWY_BIOMES).add(Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_OCEAN, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER, Biomes.GROVE,
    			Biomes.ICE_SPIKES, Biomes.JAGGED_PEAKS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_SLOPES, Biomes.SNOWY_TAIGA);
    		this.tag(SNOWY_SMILODON_BIOMES).addTag(SNOWY_BIOMES);
    	}
	}
}
