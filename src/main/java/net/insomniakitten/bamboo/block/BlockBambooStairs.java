package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class BlockBambooStairs extends BlockStairsBase implements ItemBlockSupplier, OreEntrySupplier {

    public BlockBambooStairs(float hardness, float resistance) {
        super(Material.WOOD, SoundType.WOOD, hardness, resistance);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockBase(this, "facing=east,half=bottom,shape=straight");
    }

    @Override
    public void getOreEntries(OreEntrySupplier.OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this), "stairWood", "stairBamboo");
    }

}
