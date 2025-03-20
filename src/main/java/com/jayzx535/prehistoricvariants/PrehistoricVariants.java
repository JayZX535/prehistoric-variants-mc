package com.jayzx535.prehistoricvariants;

import com.github.teamfossilsarcheology.fossil.entity.variant.VariantRegistry;
import com.github.teamfossilsarcheology.fossil.entity.variant.VariantRegistry.RegistryObject;
import com.jayzx535.prehistoricvariants.entity.variant.RandomCondition;
import com.jayzx535.prehistoricvariants.entity.variant.SpawnBiomeCondition;
import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(PrehistoricVariants.MODID)
public class PrehistoricVariants {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "prehistoricvariants";
	
	private static RegistryObject<RandomCondition> RANDOM_VARIANT_CONDITION;
	private static RegistryObject<SpawnBiomeCondition> SPAWN_BIOME_VARIANT_CONDITION;
	
	public PrehistoricVariants() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
    	
		// Configs
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MODID + "-common.toml");
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		RANDOM_VARIANT_CONDITION = VariantRegistry.register("random", RandomCondition::save, RandomCondition::load, RandomCondition.class, new RandomCondition.Deserializer());
		SPAWN_BIOME_VARIANT_CONDITION = VariantRegistry.register("spawn_biome", SpawnBiomeCondition::save, SpawnBiomeCondition::load, SpawnBiomeCondition.class, new SpawnBiomeCondition.Deserializer());
	}
	
	public static RegistryObject<RandomCondition> randomCondition() { return RANDOM_VARIANT_CONDITION; }
	public static RegistryObject<SpawnBiomeCondition> spawnBiomeCondition() { return SPAWN_BIOME_VARIANT_CONDITION; }
	
	public static Logger getLogger() { return LOGGER; }
	
	/**public class Config {
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	    public static final ForgeConfigSpec SPEC;
	    
	    // Whether or not to log advanced debug info
	    public static final ForgeConfigSpec.ConfigValue<Boolean> ADVANCED_DEBUGGING;
	    
	    static {
			BUILDER.push("General Settings");
			ADVANCED_DEBUGGING = BUILDER.comment("Prints additional debug information to the log.",
				"In most cases this information isn't necessary and excessive debugging can generate lag.",
				"However, if you are troubleshooting or reporting an issue, advanced log info may be helpful to turn on.")
				.define("advanced_debugging", false);
			BUILDER.pop();
	        SPEC = BUILDER.build();
	    }
	}*/
}
