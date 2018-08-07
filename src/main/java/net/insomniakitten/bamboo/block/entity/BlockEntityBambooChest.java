package net.insomniakitten.bamboo.block.entity;

import net.minecraft.block.BlockChest;
import net.minecraft.tileentity.TileEntityChest;

public final class BlockEntityBambooChest extends TileEntityChest {
    public BlockEntityBambooChest() {}

    public BlockEntityBambooChest(final BlockChest.Type type) {
        super(type);
    }
}
