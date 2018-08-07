package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.world.GeneratorBamboo;
import net.insomniakitten.bamboo.world.GeneratorSaltOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Bamboozled.ID, useMetadata = true)
public final class Bamboozled {
    public static final String ID = "bamboozled";

    private static final Bamboozled INSTANCE = new Bamboozled();

    public static final EnumPlantType TROPICAL_PLANT_TYPE = EnumPlantType.getPlantType("Tropical");

    private static final CreativeTabs ITEM_GROUP = new CreativeTabs(Bamboozled.ID) {
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslationKey() {
            return "item_group.bamboozled.label";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(BamboozledItems.BAMBOO);
        }
    };

    private Bamboozled() {}

    @Mod.InstanceFactory
    public static Bamboozled getInstance() {
        return Bamboozled.INSTANCE;
    }

    public static CreativeTabs getItemGroup() {
        return Bamboozled.ITEM_GROUP;
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
