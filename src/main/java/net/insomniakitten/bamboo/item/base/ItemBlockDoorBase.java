package net.insomniakitten.bamboo.item.base;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockDoorBase extends ItemDoor {
    private final Block door;

    public ItemBlockDoorBase(Block door) {
        super(door);
        this.door = door;
    }

    @Override
    public String getTranslationKey() {
        return door.getTranslationKey();
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return door.getTranslationKey();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return door.getCreativeTab();
    }
}
