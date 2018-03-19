package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.insomniakitten.bamboo.block.base.BlockBase;
import net.insomniakitten.bamboo.util.BoundingBoxes;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

public final class BlockBambooWall extends BlockBase {

    private static final ImmutableList<AxisAlignedBB> AABB_COLLISION = ImmutableList.of(
            new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.5D, 0.625D),
            new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.5D, 0.625D),
            new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.5D, 1.0D),
            new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 0.625D),
            new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.5D, 0.625D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D),
            new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D),
            new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D),
            new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 1.0D),
            new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.5D, 0.625D),
            new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.5D, 0.625D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.5D, 1.0D)
    );

    private static final ImmutableList<AxisAlignedBB> AABB_SELECTION = ImmutableList.copyOf(
            AABB_COLLISION.stream().map(aabb -> aabb.setMaxY(1.0D)).collect(Collectors.toList())
    );

    private static final ImmutableMap<EnumFacing, PropertyBool> CONNECTION = ImmutableMap.of(
            NORTH, PropertyBool.create("north"),
            SOUTH, PropertyBool.create("south"),
            WEST, PropertyBool.create("west"),
            EAST, PropertyBool.create("east")
    );

    public BlockBambooWall() {
        super(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F);
        setFullBlock(false);
        setOpaqueBlock(false);
    }

    private int getBoundingBoxIndex(IBlockState state) {
        int i = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (state.getValue(CONNECTION.get(side))) {
                i |= 1 << side.getHorizontalIndex();
            }
        }
        return i;
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockFaceShape shape = world.getBlockState(pos).getBlockFaceShape(world, pos, side);
        boolean isValidShape = shape == BlockFaceShape.SOLID || shape == BlockFaceShape.MIDDLE_POLE;
        return isValidShape || world.getBlockState(pos).getBlock() == this;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            boolean canConnect = canConnectTo(world, pos.offset(side), side);
            state = state.withProperty(CONNECTION.get(side), canConnect);
        }
        return state;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState actualState = state.getActualState(world, pos);
        IBlockState actualStateAt = world.getBlockState(pos.offset(side)).getActualState(world, pos.offset(side));
        boolean matchState = actualState != actualStateAt;
        boolean matchBlock = actualState.getBlock() != actualStateAt.getBlock();
        return (side.getAxis().isVertical() ? matchState : matchBlock)
                && super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        CONNECTION.values().forEach(builder::add);
        return builder.build();
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess world, BlockPos pos, List<AxisAlignedBB> boxes) {
        state = state.getActualState(world, pos);
        boxes.add(AABB_COLLISION.get(0));
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (state.getValue(CONNECTION.get(side))) {
                boxes.add(AABB_COLLISION.get(1 << side.getHorizontalIndex()));
            }
        }
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_SELECTION.get(getBoundingBoxIndex(getActualState(state, source, pos)));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != UP && face != DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER_BIG;
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        state = state.getActualState(world, pos);

        boxes.add(AABB_SELECTION.get(0));

        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (state.getValue(CONNECTION.get(side))) {
                boxes.add(AABB_SELECTION.get(1 << side.getHorizontalIndex()));
            }
        }

        if (boxes.size() <= 1) {
            AxisAlignedBB box = !boxes.isEmpty() ? boxes.get(0) : FULL_BLOCK_AABB;
            return rayTrace(pos, start, end, box);
        }

        return BoundingBoxes.rayTrace(boxes, pos, start, end);
    }

}
