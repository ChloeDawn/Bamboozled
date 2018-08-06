package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public final class BlockSaltPile extends Block {
    private static final ImmutableMap<EnumFacing, IProperty<ConnectionType>> PROP_CONNECTIONS = Stream.of(EnumFacing.HORIZONTALS)
        .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> PropertyEnum.create(it.getName(), ConnectionType.class)));

    private static final ImmutableList<AxisAlignedBB> AABB = ImmutableList.of(new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0));

    public BlockSaltPile() {
        super(Material.CIRCUITS, MapColor.SNOW);
        this.setSoundType(SoundType.SAND);
        this.setHardness(0.0F);
        this.setResistance(0.0F);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        var actualState = state;
        for (val side : EnumFacing.HORIZONTALS) {
            actualState = actualState.withProperty(BlockSaltPile.PROP_CONNECTIONS.get(side), this.getConnectionType(access, pos, side));
        }
        return actualState;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(final IBlockAccess access, final BlockPos pos) {
        return false;
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return BlockSaltPile.AABB.get(this.getBoundingBoxIndex(state.getActualState(access, pos)));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block block, final BlockPos fromPos) {
        this.checkForDrop(state, world, pos);
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
        this.checkForDrop(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos pos) {
        val below = pos.down();
        val state = world.getBlockState(below);
        return state.getBlockFaceShape(world, below, EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void onEntityCollision(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
            return;
        }

        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        if (((EntityLivingBase) entity).isEntityUndead()) {
            if (world.getTotalWorldTime() % 20 == 0) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new Builder(this);
        BlockSaltPile.PROP_CONNECTIONS.values().forEach(builder::add);
        return builder.build();
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private int getBoundingBoxIndex(final IBlockState state) {
        val north = state.getValue(BlockSaltPile.PROP_CONNECTIONS.get(EnumFacing.NORTH)).isConnected();
        val south = state.getValue(BlockSaltPile.PROP_CONNECTIONS.get(EnumFacing.SOUTH)).isConnected();
        val east = state.getValue(BlockSaltPile.PROP_CONNECTIONS.get(EnumFacing.EAST)).isConnected();
        val west = state.getValue(BlockSaltPile.PROP_CONNECTIONS.get(EnumFacing.WEST)).isConnected();

        var index = 0;

        if (north || south && !east && !west) {
            index |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }

        if (south || north && !east && !west) {
            index |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }

        if (east || west && !north && !south) {
            index |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }

        if (west || east && !north && !south) {
            index |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }

        return index;
    }

    private void checkForDrop(final IBlockState state, final World world, final BlockPos pos) {
        if (!world.isRemote && !this.canPlaceBlockAt(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private ConnectionType getConnectionType(final IBlockAccess access, final BlockPos pos, final EnumFacing direction) {
        val offset = pos.offset(direction);

        if (access.getBlockState(offset).getBlock() == this) {
            return ConnectionType.SIDE;
        }

        if (access.getBlockState(offset.down()).getBlock() == this) {
            return ConnectionType.SIDE;
        }

        if (access.getBlockState(offset.up()).getBlock() == this) {
            return ConnectionType.UP;
        }

        return ConnectionType.NONE;
    }

    private enum ConnectionType implements IStringSerializable {
        NONE, SIDE, UP;

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public boolean isConnected() {
            return this != ConnectionType.NONE;
        }
    }
}
