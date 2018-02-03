package net.insomniakitten.bamboo.item;

import net.minecraft.block.Block;

public class ItemSubBlockBase extends ItemBlockBase {

    public ItemSubBlockBase(Block block, String variant) {
        super(block, variant);
        setHasSubtypes(true);
    }

    public ItemSubBlockBase(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
