package net.insomniakitten.bamboo.block.entity;

import net.insomniakitten.bamboo.block.BlockBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockEntityBambooChest extends TileEntityChest {
    private static final AxisAlignedBB RENDER_BOUNDING_BOX = new AxisAlignedBB(-1, 0, -1, 2, 2, 2);

    private BlockChest.Type type;

    public BlockEntityBambooChest() {}

    public BlockEntityBambooChest(final BlockChest.Type type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : BlockBambooChest.CONTAINER_NAME;
    }

    @Override
    public void openInventory(final EntityPlayer player) {
        if (player.isSpectator()) {
            return;
        }

        if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
        }

        ++this.numPlayersUsing;

        this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);

        if (BlockBambooChest.TYPE_BAMBOO_TRAP == this.getChestType()) {
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
        }
    }

    @Override
    public void closeInventory(final EntityPlayer player) {
        if (player.isSpectator()) {
            return;
        }

        if (!(this.getBlockType() instanceof BlockBambooChest)) {
            return;
        }

        --this.numPlayersUsing;

        this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);

        if (BlockBambooChest.TYPE_BAMBOO_TRAP == this.getChestType()) {
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
        }
    }

    @Override
    public BlockChest.Type getChestType() {
        if (this.type != null) {
            return this.type;
        }

        if (this.world == null) {
            return BlockBambooChest.TYPE_BAMBOO;
        }

        if (this.getBlockType() instanceof BlockBambooChest) {
            this.type = ((BlockChest) this.getBlockType()).chestType;
            return this.type;
        }

        return BlockBambooChest.TYPE_BAMBOO;
    }

    @Override
    public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return BlockEntityBambooChest.RENDER_BOUNDING_BOX.offset(this.getPos());
    }

}
