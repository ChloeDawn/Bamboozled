package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.world.GeneratorBamboo;
import net.insomniakitten.bamboo.world.GeneratorSaltOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Bamboozled.ID, useMetadata = true)
public final class Bamboozled {
    public static final String ID = "bamboozled";

    public static final EnumPlantType TROPICAL_PLANT_TYPE = EnumPlantType.getPlantType("Tropical");

    private static final Bamboozled INSTANCE = new Bamboozled();

    private Bamboozled() {}

    @Mod.InstanceFactory
    public static Bamboozled getInstance() {
        return Bamboozled.INSTANCE;
    }

    public static CreativeTabs getItemGroup() {
        return BamboozledItemGroup.INSTANCE;
    }

    public static BamboozledConfig.General getConfig() {
        return BamboozledConfig.GENERAL;
    }

    public static BamboozledConfig.Client getClientConfig() {
        return BamboozledConfig.CLIENT;
    }

    public static BamboozledConfig.World getWorldConfig() {
        return BamboozledConfig.WORLD;
    }

    @EventHandler
    void onPreInitialization(final FMLPreInitializationEvent event) {
        if (Bamboozled.getWorldConfig().isBambooGenerationEnabled()) {
            MinecraftForge.EVENT_BUS.register(GeneratorBamboo.class);
        }

        if (Bamboozled.getWorldConfig().isSaltOreGenerationEnabled()) {
            MinecraftForge.EVENT_BUS.register(GeneratorSaltOre.class);
        }

        if (Bamboozled.getConfig().isFancyBambooEnabled()) {
            MinecraftForge.EVENT_BUS.register(BlockBamboo.class);
        }
    }

    @EventHandler
    void onInitialization(final FMLInitializationEvent event) {
        BamboozledIntegration.onInitialization();
    }
}
