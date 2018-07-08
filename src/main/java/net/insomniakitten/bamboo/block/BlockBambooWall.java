package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.util.BoundingBoxes;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
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
import java.util.List;

public final class BlockBambooWall extends Block {
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

    private static final ImmutableList<AxisAlignedBB> AABB_SELECTION = AABB_COLLISION.stream()
        .map(it -> it.setMaxY(1.0D)).collect(ImmutableList.toImmutableList());

    private static final ImmutableMap<EnumFacing, PropertyBool> PROP_SIDES = ImmutableMap.of(
        EnumFacing.NORTH, PropertyBool.create("north"),
        EnumFacing.SOUTH, PropertyBool.create("south"),
        EnumFacing.WEST, PropertyBool.create("west"),
        EnumFacing.EAST, PropertyBool.create("east")
    );

    public BlockBambooWall() {
        super(Material.WOOD);
        setHardness(1.0F);
        setResistance(5.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        for (val side : EnumFacing.HORIZONTALS) {
            state = state.withProperty(PROP_SIDES.get(side), canConnectTo(access, pos.offset(side), side));
        }
        return state;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return AABB_SELECTION.get(getBoundingBoxIndex(state.getActualState(access, pos)));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing side) {
        return side.getAxis().isHorizontal() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER_BIG;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> boxes, Entity entity, boolean isActualState) {
        val actual = isActualState ? state : state.getActualState(world, pos);
        addCollisionBoxToList(pos, entityBox, boxes, AABB_COLLISION.get(0));
        for (val side : EnumFacing.HORIZONTALS) {
            if (actual.getValue(PROP_SIDES.get(side))) {
                val aabb = AABB_COLLISION.get(1 << side.getHorizontalIndex());
                addCollisionBoxToList(pos, entityBox, boxes, aabb);
            }
        }
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return AABB_SELECTION.get(getBoundingBoxIndex(getActualState(state, world, pos))).offset(pos);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        val boxes = Lists.newArrayList(AABB_SELECTION.get(0));
        val actual = state.getActualState(world, pos);

        for (val side : EnumFacing.HORIZONTALS) {
            if (actual.getValue(PROP_SIDES.get(side))) {
                val i = 1 << side.getHorizontalIndex();
                boxes.add(AABB_SELECTION.get(i));
            }
        }

        if (boxes.size() <= 1) {
            val box = !boxes.isEmpty() ? boxes.get(0) : FULL_BLOCK_AABB;
            return rayTrace(pos, start, end, box);
        }

        return BoundingBoxes.rayTrace(boxes, pos, start, end);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new Builder(this);
        PROP_SIDES.values().forEach(builder::add);
        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        val offset = pos.offset(side);
        val other = access.getBlockState(offset);
        if (side.getAxis().isVertical()) {
            val actual = state.getActualState(access, pos);
            return actual == other.getActualState(access, offset);
        } else return state.getBlock() == other.getBlock();
    }

    private int getBoundingBoxIndex(IBlockState state) {
        var i = 0;
        for (val side : EnumFacing.HORIZONTALS) {
            if (state.getValue(PROP_SIDES.get(side))) {
                i |= 1 << side.getHorizontalIndex();
            }
        }
        return i;
    }

    public boolean canConnectTo(IBlockAccess access, BlockPos pos, EnumFacing side) {
        val state = access.getBlockState(pos);
        if (state.getBlock() != this) {
            val shape = state.getBlockFaceShape(access, pos, side);
            return shape == BlockFaceShape.SOLID || shape == BlockFaceShape.MIDDLE_POLE;
        } else return true;
    }
}
