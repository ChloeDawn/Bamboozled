package net.insomniakitten.bamboo.item.base;

import net.minecraft.block.Block;

public class ItemSubBlockBase extends ItemBlockBase {
    public ItemSubBlockBase(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
