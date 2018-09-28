package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.config.BamboozledConfig;
import net.insomniakitten.bamboo.config.ClientConfig;
import net.insomniakitten.bamboo.config.GeneralConfig;
import net.insomniakitten.bamboo.config.WorldConfig;
import net.insomniakitten.bamboo.world.GeneratorBamboo;
import net.insomniakitten.bamboo.world.GeneratorSaltOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Bamboozled.ID, useMetadata = true)
public final class Bamboozled {
    public static final String ID = "bamboozled";

    private static final Logger LOGGER = Bamboozled.getLogger("main");

    private static final Bamboozled INSTANCE;
    private static final EnumPlantType TROPICAL_PLANT_TYPE;

    static {
        Bamboozled.LOGGER.info("Initializing mod instance");
        INSTANCE = new Bamboozled();

        Bamboozled.LOGGER.info("Registering tropical plant type");
        TROPICAL_PLANT_TYPE = EnumPlantType.getPlantType("Tropical");
    }

    private Bamboozled() {}

    @InstanceFactory
    public static Bamboozled getInstance() {
        return Bamboozled.INSTANCE;
    }

    public static EnumPlantType getTropicalPlantType() {
        return Bamboozled.TROPICAL_PLANT_TYPE;
    }

    public static CreativeTabs getItemGroup() {
        return BamboozledItemGroup.INSTANCE;
    }

    public static GeneralConfig getConfig() {
        return BamboozledConfig.GENERAL;
    }

    public static ClientConfig getClientConfig() {
        return BamboozledConfig.CLIENT;
    }

    public static WorldConfig getWorldConfig() {
        return BamboozledConfig.WORLD;
    }

    public static Logger getLogger(final String topic) {
        if (topic.isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        return LogManager.getLogger(Bamboozled.ID + "." + topic);
    }

    public static ResourceLocation addNamespace(final String string) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("String cannot be empty");
        }

        return new ResourceLocation(Bamboozled.ID, string);
    }

    public static String addNamespace(final String string, final char delimiter) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("String cannot be empty");
        }

        return String.format("%s%s%s", Bamboozled.ID, delimiter, string);
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
        BamboozledIntegration.init();
    }
}
