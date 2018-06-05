package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.world.GeneratorBamboo;
import net.insomniakitten.bamboo.world.GeneratorSaltOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Bamboozled.ID, name = Bamboozled.NAME, version = Bamboozled.VERSION)
public final class Bamboozled {
    public static final String ID = "bamboozled";
    public static final String NAME = "Bamboozled";
    public static final String VERSION = "%VERSION%";

    public static final Logger LOGGER = LogManager.getLogger(Bamboozled.ID);

    public static final CreativeTabs TAB = new CreativeTabs(Bamboozled.ID) {
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            return "item_group." + Bamboozled.ID + ".label";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(BamboozledItems.BAMBOO);
        }
    };

    public static BamboozledConfig.General getConfig() {
        return BamboozledConfig.GENERAL;
    }

    public static BamboozledConfig.Client getClientConfig() {
        return BamboozledConfig.CLIENT;
    }

    public static BamboozledConfig.World getWorldConfig() {
        return BamboozledConfig.WORLD;
    }

    @Mod.EventHandler
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
