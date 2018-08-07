package net.insomniakitten.bamboo.block;

import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nullable;

public final class BlockBambooChest extends BlockChest {
    public static final Type TYPE_BAMBOO = EnumHelper.addEnum(Type.class, "BAMBOO", new Class[0]);
    public static final Type TYPE_BAMBOO_TRAP = EnumHelper.addEnum(Type.class, "BAMBOO_TRAP", new Class[0]);

    public static final String CONTAINER_NAME = "container." + Bamboozled.ID + ".bamboo_chest";
    public static final String LARGE_CONTAINER_NAME = "container." + Bamboozled.ID + ".bamboo_chest_large";

    public BlockBambooChest(final Type type) {
        super(type);
        this.setHardness(2.5F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    @Nullable
    public ILockableContainer getContainer(final World world, final BlockPos pos, final boolean allowBlocking) {
        val tile = world.getTileEntity(pos);

        if (!(tile instanceof TileEntityChest)) {
            return null;
        }

        var lockable = (ILockableContainer) tile;

        if (!allowBlocking && this.isBlocked(world, pos)) {
            return null;
        }

        for (val horizontal : EnumFacing.Plane.HORIZONTAL) {
            val offset = pos.offset(horizontal);
            val block = world.getBlockState(offset).getBlock();

            if (block == this) {
                if (!allowBlocking && this.isBlocked(world, offset)) {
                    // Forge: fix MC-99321
                    return null;
                }

                val otherTile = world.getTileEntity(offset);

                if (otherTile instanceof TileEntityChest) {
                    if (horizontal != EnumFacing.WEST && horizontal != EnumFacing.NORTH) {
                        lockable = new InventoryLargeChest(BlockBambooChest.LARGE_CONTAINER_NAME, lockable, (ILockableContainer) otherTile);
                    } else {
                        lockable = new InventoryLargeChest(BlockBambooChest.LARGE_CONTAINER_NAME, (ILockableContainer) otherTile, lockable);
                    }
                }
            }
        }

        return lockable;
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new BlockEntityBambooChest();
    }

    @Override
    @Deprecated
    public boolean canProvidePower(final IBlockState state) {
        return BlockBambooChest.TYPE_BAMBOO_TRAP == this.chestType;
    }
}
