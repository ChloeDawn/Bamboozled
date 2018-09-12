package net.insomniakitten.bamboo.item.base;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class ItemBlockDoorBase extends ItemDoor {
    private final Block door;

    public ItemBlockDoorBase(final Block door) {
        super(door);
        this.door = door;
    }

    @Override
    public String getTranslationKey() {
        return this.door.getTranslationKey();
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        return this.door.getTranslationKey();
    }

    @Override
    public void getSubItems(final CreativeTabs group, final NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(group)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    @Nullable
    public CreativeTabs getCreativeTab() {
        return this.door.getCreativeTab();
    }
}
