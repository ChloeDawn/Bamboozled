package net.insomniakitten.bamboo.item.base;

import net.minecraft.block.Block;

public class ItemSubBlockBase extends ItemBlockBase {
    public ItemSubBlockBase(final Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(final int damage) {
        return damage;
    }
}
