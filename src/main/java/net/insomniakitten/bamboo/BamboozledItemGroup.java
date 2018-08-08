package net.insomniakitten.bamboo;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BamboozledItemGroup extends CreativeTabs {
    private final String translationKey = "item_group." + Bamboozled.ID + ".label";
    private final ResourceLocation backgroundImage = new ResourceLocation(Bamboozled.ID, "textures/gui/item_group.png");

    BamboozledItemGroup() {
        super(Bamboozled.ID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslationKey() {
        return this.translationKey;
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

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getBackgroundImage() {
        return this.backgroundImage;
    }
}
