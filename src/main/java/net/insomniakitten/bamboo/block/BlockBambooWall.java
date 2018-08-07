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
import net.minecraft.block.properties.IProperty;
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

    private static final ImmutableList<AxisAlignedBB> AABB_SELECTION = BlockBambooWall.AABB_COLLISION.stream()
        .map(it -> it.setMaxY(1.0D)).collect(ImmutableList.toImmutableList());

    private static final ImmutableMap<EnumFacing, IProperty<Boolean>> PROP_SIDES = ImmutableMap.of(
        EnumFacing.NORTH, PropertyBool.create("north"),
        EnumFacing.SOUTH, PropertyBool.create("south"),
        EnumFacing.WEST, PropertyBool.create("west"),
        EnumFacing.EAST, PropertyBool.create("east")
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
    public IBlockState getActualState(IBlockState state, final IBlockAccess access, final BlockPos pos) {
        for (val side : EnumFacing.HORIZONTALS) {
            state = state.withProperty(BlockBambooWall.PROP_SIDES.get(side), this.canConnectTo(access, pos.offset(side), side));
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
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return BlockBambooWall.AABB_SELECTION.get(this.getBoundingBoxIndex(state.getActualState(access, pos)));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos pos, final EnumFacing side) {
        return side.getAxis().isHorizontal() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER_BIG;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> boxes, final Entity entity, final boolean isActualState) {
        val actual = isActualState ? state : state.getActualState(world, pos);
        Block.addCollisionBoxToList(pos, entityBox, boxes, BlockBambooWall.AABB_COLLISION.get(0));
        for (val side : EnumFacing.HORIZONTALS) {
            if (actual.getValue(BlockBambooWall.PROP_SIDES.get(side))) {
                val aabb = BlockBambooWall.AABB_COLLISION.get(1 << side.getHorizontalIndex());
                Block.addCollisionBoxToList(pos, entityBox, boxes, aabb);
            }
        }
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
        return BlockBambooWall.AABB_SELECTION.get(this.getBoundingBoxIndex(state.getActualState(world, pos))).offset(pos);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        val boxes = Lists.newArrayList(BlockBambooWall.AABB_SELECTION.get(0));
        val actual = state.getActualState(world, pos);

        for (val side : EnumFacing.HORIZONTALS) {
            if (actual.getValue(BlockBambooWall.PROP_SIDES.get(side))) {
                val i = 1 << side.getHorizontalIndex();
                boxes.add(BlockBambooWall.AABB_SELECTION.get(i));
            }
        }

        if (boxes.size() <= 1) {
            val box = boxes.isEmpty() ? Block.FULL_BLOCK_AABB : boxes.get(0);
            return this.rayTrace(pos, start, end, box);
        }

        return BoundingBoxes.rayTrace(boxes, pos, start, end);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new Builder(this);
        BlockBambooWall.PROP_SIDES.values().forEach(builder::add);
        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        val offset = pos.offset(side);
        val other = access.getBlockState(offset);
        if (side.getAxis().isVertical()) {
            val actual = state.getActualState(access, pos);
            return actual == other.getActualState(access, offset);
        } else {
            return state.getBlock() == other.getBlock();
        }
    }

    @Override
    public boolean canPlaceTorchOnTop(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return true;
    }

    public boolean canConnectTo(final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        val state = access.getBlockState(pos);

        if (state.getBlock() == this) {
            return true;
        }

        val shape = state.getBlockFaceShape(access, pos, side);
        return shape == BlockFaceShape.SOLID || shape == BlockFaceShape.MIDDLE_POLE;
    }

    private int getBoundingBoxIndex(final IBlockState state) {
        var i = 0;
        for (val side : EnumFacing.HORIZONTALS) {
            if (state.getValue(BlockBambooWall.PROP_SIDES.get(side))) {
                i |= 1 << side.getHorizontalIndex();
            }
        }
        return i;
    }
}
