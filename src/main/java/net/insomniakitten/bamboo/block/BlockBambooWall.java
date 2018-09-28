package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class BlockBambooWall extends Block {
    public static final ImmutableList<AxisAlignedBB> COLLISION_AABB = ImmutableList.of(
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

    public static final ImmutableList<AxisAlignedBB> SELECTION_AABB = BlockBambooWall.COLLISION_AABB.stream()
        .map(it -> it.setMaxY(1.0D))
        .collect(ImmutableList.toImmutableList());

    public static final ImmutableMap<EnumFacing, IProperty<Boolean>> SIDES = Stream.of(EnumFacing.values())
        .filter(facing -> facing.getAxis().isHorizontal())
        .collect(Maps.toImmutableEnumMap(Function.identity(), it -> PropertyBool.create(it.getName())));

    private static final Set<BlockFaceShape> CONNECTABLE_SHAPES = Sets.immutableEnumSet(
        BlockFaceShape.SOLID, BlockFaceShape.MIDDLE_POLE
    );

    public BlockBambooWall() {
        super(Material.WOOD);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, final IBlockAccess access, final BlockPos position) {
        for (final EnumFacing facing : BlockBambooWall.SIDES.keySet()) {
            final IProperty<Boolean> property = BlockBambooWall.SIDES.get(facing);
            final BlockPos offset = position.offset(facing);

            state = state.withProperty(property, this.canConnectTo(access, offset, facing));
        }

        return state;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        final IBlockState actualState = state.getActualState(access, position);
        final int index = this.getBoundingBoxIndex(actualState);

        return BlockBambooWall.SELECTION_AABB.get(index);
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos position, final EnumFacing face) {
        return face.getAxis().isHorizontal() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER_BIG;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos position, final AxisAlignedBB entityBox, final List<AxisAlignedBB> boxes, final Entity entity, final boolean isActualState) {
        Block.addCollisionBoxToList(position, entityBox, boxes, BlockBambooWall.COLLISION_AABB.get(0));

        final IBlockState actualState = isActualState ? state : state.getActualState(world, position);

        for (final EnumFacing facing : BlockBambooWall.SIDES.keySet()) {
            final IProperty<Boolean> property = BlockBambooWall.SIDES.get(facing);

            if (actualState.getValue(property)) {
                final int index = 1 << facing.getHorizontalIndex();
                final AxisAlignedBB aabb = BlockBambooWall.COLLISION_AABB.get(index);

                Block.addCollisionBoxToList(position, entityBox, boxes, aabb);
            }
        }
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos position) {
        final IBlockState actualState = state.getActualState(world, position);
        final int index = this.getBoundingBoxIndex(actualState);
        final AxisAlignedBB aabb = BlockBambooWall.SELECTION_AABB.get(index);

        return aabb.offset(position);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos position, final Vec3d start, final Vec3d end) {
        final List<AxisAlignedBB> boxes = Lists.newArrayList(BlockBambooWall.SELECTION_AABB.get(0));
        final IBlockState actualState = state.getActualState(world, position);

        for (final EnumFacing facing : BlockBambooWall.SIDES.keySet()) {
            final IProperty<Boolean> property = BlockBambooWall.SIDES.get(facing);

            if (actualState.getValue(property)) {
                final int index = 1 << facing.getHorizontalIndex();
                final AxisAlignedBB aabb = BlockBambooWall.SELECTION_AABB.get(index);

                boxes.add(aabb);
            }
        }

        if (boxes.size() <= 1) {
            return this.rayTrace(position, start, end, boxes.get(0));
        }

        final int x = position.getX();
        final int y = position.getY();
        final int z = position.getZ();
        final List<RayTraceResult> hits = new ArrayList<>();
        final Vec3d a = start.subtract(x, y, z);
        final Vec3d b = end.subtract(x, y, z);

        for (final AxisAlignedBB box : boxes) {
            @Nullable final RayTraceResult hit = box.calculateIntercept(a, b);

            if (hit != null) {
                final Vec3d vec = hit.hitVec.add(x, y, z);

                hits.add(new RayTraceResult(vec, hit.sideHit, position));
            }
        }

        @Nullable
        RayTraceResult ret = null;
        double sqrDis = 0.0D;

        for (final RayTraceResult hit : hits) {
            final double newSqrDis = hit.hitVec.squareDistanceTo(end);

            if (newSqrDis > sqrDis) {
                ret = hit;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        final BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        BlockBambooWall.SIDES.values().forEach(builder::add);

        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        final BlockPos offset = position.offset(face);
        final IBlockState other = access.getBlockState(offset);

        if (face.getAxis().isHorizontal()) {
            return state.getBlock() == other.getBlock();
        }

        final IBlockState actualState = state.getActualState(access, position);
        final IBlockState otherActualState = other.getActualState(access, offset);

        return actualState == otherActualState;
    }

    @Override
    public boolean canPlaceTorchOnTop(final IBlockState state, final IBlockAccess world, final BlockPos position) {
        return true;
    }

    public boolean canConnectTo(final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        final IBlockState state = access.getBlockState(position);

        if (this == state.getBlock()) {
            return true;
        }

        final BlockFaceShape shape = state.getBlockFaceShape(access, position, face);

        return BlockBambooWall.CONNECTABLE_SHAPES.contains(shape);
    }

    private int getBoundingBoxIndex(final IBlockState state) {
        int index = 0;

        for (final EnumFacing facing : BlockBambooWall.SIDES.keySet()) {
            final IProperty<Boolean> property = BlockBambooWall.SIDES.get(facing);

            if (state.getValue(property)) {
                index |= 1 << facing.getHorizontalIndex();
            }
        }

        return index;
    }
}
