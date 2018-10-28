package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.init.BamboozledItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

public final class BamboozledItemGroup extends CreativeTabs {
    private static final Logger LOGGER = Bamboozled.getLogger("itemgroup");

    static final CreativeTabs INSTANCE;

    static {
        BamboozledItemGroup.LOGGER.info("Initializing item group");
        INSTANCE = new BamboozledItemGroup();
    }

    private BamboozledItemGroup() {
        super("");
    }

    @Override
    public String getTabLabel() {
        return Bamboozled.ID;
    }

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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean drawInForegroundOfTab() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasScrollbar() {
        return true;
    }
}
