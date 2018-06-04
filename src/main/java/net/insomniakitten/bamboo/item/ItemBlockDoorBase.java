package net.insomniakitten.bamboo.item;

import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockDoorBase extends ItemDoor {
    private final Block doorBlock;

    public ItemBlockDoorBase(Block doorBlock) {
        super(doorBlock);
        this.doorBlock = doorBlock;
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(Bamboozled.ID + "." + name);
    }

    @Override
    public String getUnlocalizedName() {
        return doorBlock.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return doorBlock.getUnlocalizedName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) items.add(new ItemStack(this));
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return doorBlock.getCreativeTabToDisplayOn();
    }
}
