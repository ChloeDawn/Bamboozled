package net.insomniakitten.bamboo.block;

import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooCrate;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class BlockBambooCrate extends Block {
    public BlockBambooCrate() {
        super(Material.WOOD, MapColor.WOOD);
        this.setHardness(2.5F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public void breakBlock(final World world, final BlockPos position, final IBlockState state) {
        if (world.isRemote) {
            return;
        }

        if (world.restoringBlockSnapshots) {
            return;
        }

        if (!world.getGameRules().getBoolean("doTileDrops")) {
            return;
        }

        @Nullable val blockEntity = world.getTileEntity(position);

        if (blockEntity == null) {
            return;
        }

        if (!(blockEntity instanceof BlockEntityBambooCrate)) {
            val clazz = blockEntity.getClass().toString();
            val key = String.valueOf(TileEntity.getKey(blockEntity.getClass()));
            val message = "Unexpected block entity '" + key + "' " + clazz + " at " + position;
            throw new IllegalStateException(message);
        }

        val items = ((BlockEntityBambooCrate) blockEntity).getItems();

        for (var slot = 0; slot < items.getSlots(); ++slot) {
            val item = items.getStackInSlot(slot);

            if (Block.captureDrops.get()) {
                Block.capturedDrops.get().add(item);
                continue;
            }

            val x = (double) position.getX() + (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
            val y = (double) position.getY() + (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
            val z = (double) position.getZ() + (double) (world.rand.nextFloat() * 0.5F) + 0.25D;
            val entity = new EntityItem(world, x, y, z, item);

            entity.setDefaultPickupDelay();
            world.spawnEntity(entity);
        }
    }

    @Override
    public void onFallenUpon(final World world, final BlockPos position, final Entity entity, final float fallDistance) {
        super.onFallenUpon(world, position, entity, fallDistance * 0.5F);
    }

    @Override
    public void onLanded(final World world, final Entity entity) {
        if (entity.isSneaking()) {
            super.onLanded(world, entity);

            return;
        }

        if (entity.motionY < 0.0) {
            entity.motionY = -entity.motionY * 0.66;

            if (!(entity instanceof EntityLivingBase)) {
                entity.motionY *= 0.8;
            }
        }
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return this == state.getBlock();
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return this == state.getBlock() ? new BlockEntityBambooCrate() : null;
    }
}
