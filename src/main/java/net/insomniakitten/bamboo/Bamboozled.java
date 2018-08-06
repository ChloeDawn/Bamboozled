package net.insomniakitten.bamboo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.world.GeneratorBamboo;
import net.insomniakitten.bamboo.world.GeneratorSaltOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Bamboozled.ID, useMetadata = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bamboozled {
    public static final String ID = "bamboozled";

    @Getter(onMethod = @__({ @Mod.InstanceFactory, @Deprecated }))
    public static final Bamboozled INSTANCE = new Bamboozled();

    public static final CreativeTabs TAB = new CreativeTabs(Bamboozled.ID) {
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslationKey() {
            return "item_group." + Bamboozled.ID + ".label";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(BamboozledItems.BAMBOO);
        }
    };

    public static final EnumPlantType TROPICAL_PLANT_TYPE =
        EnumPlantType.getPlantType("Tropical");

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
    public void onPreInit(FMLPreInitializationEvent event) {
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
}
