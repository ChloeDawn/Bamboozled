package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.init.BamboozledItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BamboozledItemGroup extends CreativeTabs {
    private static final String TRANSLATION_KEY = "item_group." + Bamboozled.ID + ".label";

    static final CreativeTabs INSTANCE = new BamboozledItemGroup();

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
        return BamboozledItemGroup.TRANSLATION_KEY;
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
