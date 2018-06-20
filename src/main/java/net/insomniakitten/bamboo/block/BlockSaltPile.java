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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
    private static final ImmutableMap<EnumFacing, PropertyEnum<ConnectionType>> PROP_CONNECTIONS = Stream.of(EnumFacing.HORIZONTALS)
        .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> PropertyEnum.create(it.getName(), ConnectionType.class)));

    private static final ImmutableList<AxisAlignedBB> AABB = ImmutableList.of(
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
    );

    public BlockSaltPile() {
        super(Material.CIRCUITS, MapColor.SNOW);
        setSoundType(SoundType.SAND);
        setHardness(0.0F);
        setResistance(0.0F);
    }

    private int getBoundingBoxIndex(IBlockState state) {
        val north = state.getValue(PROP_CONNECTIONS.get(EnumFacing.NORTH)).isConnected();
        val south = state.getValue(PROP_CONNECTIONS.get(EnumFacing.SOUTH)).isConnected();
        val east = state.getValue(PROP_CONNECTIONS.get(EnumFacing.EAST)).isConnected();
        val west = state.getValue(PROP_CONNECTIONS.get(EnumFacing.WEST)).isConnected();

        var index = 0;

        if (north || (south && !east && !west)) {
            index |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }

        if (south || (north && !east && !west)) {
            index |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }

        if (east || (west && !north && !south)) {
            index |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }

        if (west || (east && !north && !south)) {
            index |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }

        return index;
    }

    private void checkForDrop(IBlockState state, World world, BlockPos pos) {
        if (!world.isRemote && !canPlaceBlockAt(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private ConnectionType getConnectionType(IBlockAccess access, BlockPos pos, EnumFacing direction) {
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

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        for (val side : EnumFacing.HORIZONTALS) {
            state = state.withProperty(PROP_CONNECTIONS.get(side), getConnectionType(access, pos, side));
        }
        return state;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess access, BlockPos pos) {
        return false;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return AABB.get(getBoundingBoxIndex(state.getActualState(world, pos))).offset(pos);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        checkForDrop(state, world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        checkForDrop(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        val below = pos.down();
        val state = world.getBlockState(below);
        return state.getBlockFaceShape(world, below, EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) return;
        if (!(entity instanceof EntityLiving)) return;
        if (((EntityLivingBase) entity).isEntityUndead()) {
            if (world.getTotalWorldTime() % 20 == 0) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new Builder(this);
        PROP_CONNECTIONS.values().forEach(builder::add);
        return builder.build();
    }

    public enum ConnectionType implements IStringSerializable {
        UP, SIDE, NONE;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean isConnected() {
            return this != NONE;
        }
    }
}
