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

    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final CreativeTabs TAB = new CreativeTabs(ID) {
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            return "item_group." + ID + ".label";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(BamboozledItems.BAMBOO);
        }
    };

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        if (BamboozledConfig.WORLD.generateBamboo) {
            MinecraftForge.EVENT_BUS.register(GeneratorBamboo.class);
        }
        if (BamboozledConfig.WORLD.generateSaltOre) {
            MinecraftForge.EVENT_BUS.register(GeneratorSaltOre.class);
        }
        if (BamboozledConfig.GENERAL.fancyBamboo) {
            MinecraftForge.EVENT_BUS.register(BlockBamboo.class);
        }
    }
}
