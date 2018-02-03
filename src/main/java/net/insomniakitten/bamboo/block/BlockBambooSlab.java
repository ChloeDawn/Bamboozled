package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.item.ItemBlockSlabBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class BlockBambooSlab extends BlockSlabBase implements ItemBlockSupplier, OreEntrySupplier {

    public BlockBambooSlab(float hardness, float resistance) {
        super(Material.WOOD, SoundType.WOOD, hardness, resistance);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockSlabBase(this, "variant=lower");
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this), "slabWood", "slabBamboo");
    }

}
