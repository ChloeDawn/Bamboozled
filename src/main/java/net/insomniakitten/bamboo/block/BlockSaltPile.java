package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.insomniakitten.bamboo.block.base.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Locale;

import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.WEST;

public final class BlockSaltPile extends BlockBase {

    public static final ImmutableMap<EnumFacing, PropertyEnum<ConnectionType>> CONNECTION = ImmutableMap.of(
            NORTH, PropertyEnum.create("north", ConnectionType.class),
            SOUTH, PropertyEnum.create("south", ConnectionType.class),
            WEST, PropertyEnum.create("west", ConnectionType.class),
            EAST, PropertyEnum.create("east", ConnectionType.class)
    );

    private static final ImmutableList<AxisAlignedBB> AABB_SALT_PILE = ImmutableList.of(
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)
    );

    public BlockSaltPile() {
        super(Material.CIRCUITS, MapColor.SNOW, SoundType.SAND, 0.0F, 0.0F);
        setFullBlock(false);
        setOpaqueBlock(false);
    }

    private boolean canConnectTo(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }

    private boolean isConnectedAt(IBlockState state, EnumFacing side) {
        return state.getValue(CONNECTION.get(side)) != ConnectionType.NONE;
    }

    private int getAABBIndex(IBlockState state) {
        int index = 0;
        boolean north = isConnectedAt(state, NORTH);
        boolean south = isConnectedAt(state, SOUTH);
        boolean east = isConnectedAt(state, EAST);
        boolean west = isConnectedAt(state, WEST);
        if (north || south && !east && !west) index |= 1 << NORTH.getHorizontalIndex();
        if (south || north && !east && !west) index |= 1 << SOUTH.getHorizontalIndex();
        if (east || west && !north && !south) index |= 1 << EAST.getHorizontalIndex();
        if (west || east && !north && !south) index |= 1 << WEST.getHorizontalIndex();
        return index;
    }

    private void checkForDrop(IBlockState state, World world, BlockPos pos) {
        if (!world.isRemote && !canPlaceBlockAt(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean isSolid(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    private ConnectionType getAttachPosition(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        pos = pos.offset(direction);
        IBlockState state = world.getBlockState(pos);
        if (canConnectTo(world, pos) || (!state.isNormalCube() && canConnectTo(world, pos.down()))) {
            return ConnectionType.SIDE;
        }
        if (!world.getBlockState(pos.up()).isNormalCube() && isSolid(state, world, pos) && canConnectTo(world, pos.up())) {
            return state.isBlockNormalCube() ? ConnectionType.UP : ConnectionType.SIDE;
        }
        return ConnectionType.NONE;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            state = state.withProperty(CONNECTION.get(side), getAttachPosition(world, pos, side));
        }
        return state;
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
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
        return isSolid(world.getBlockState(pos.down()), world, pos.down());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        for (IProperty<?> property : CONNECTION.values()) {
            builder.add(property);
        }
        return builder.build();
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess world, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(AABB_SALT_PILE.get(getAABBIndex(state.getActualState(world, pos))));
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {

    }

    public enum ConnectionType implements IStringSerializable {
        UP, SIDE, NONE;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
