package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockRope extends Block {
    private static final PropertyDirection PROP_FACING = PropertyDirection.create("facing", f -> f != EnumFacing.UP);

    private static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB = ImmutableMap.of(
            EnumFacing.DOWN, new AxisAlignedBB(0.375D, 0.0, 0.375D, 0.625D, 1.0D, 0.625D),
            EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.0D, 0.8125D, 0.625D, 1.0D, 1.0D),
            EnumFacing.SOUTH, new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.1875D),
            EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
            EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.1875D, 1.0D, 0.625D)
    );

    public BlockRope() {
        super(Material.CIRCUITS);
        setSoundType(SoundType.CLOTH);
        setHardness(0.4F);
        setResistance(2.0F);
    }

    public IBlockState withFacing(EnumFacing facing) {
        return getDefaultState().withProperty(PROP_FACING, facing);
    }

    private void checkForDrop(World world, BlockPos pos, IBlockState state) {
        if (!canPlaceBlockOnSide(world, pos, state.getValue(PROP_FACING))) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return withFacing(EnumFacing.values()[meta & 7]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROP_FACING).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(PROP_FACING, rotation.rotate(state.getValue(PROP_FACING)));
    }

    @Override
    @Deprecated
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(PROP_FACING)));
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return AABB.get(state.getValue(PROP_FACING));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        checkForDrop(world, pos, state);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        checkForDrop(world, pos, state);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        val offset = pos.offset(side.getOpposite());
        val state = world.getBlockState(offset);
        val shape = state.getBlockFaceShape(world, offset, side);
        val isValidSide = PROP_FACING.getAllowedValues().contains(side);
        return isValidSide && (world.getBlockState(pos.up()).getBlock() == this || shape == BlockFaceShape.SOLID);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (val side : PROP_FACING.getAllowedValues()) {
            if (canPlaceBlockOnSide(world, pos, side)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        val stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == BamboozledItems.ROPE) {
            val chunk = world.getChunkFromBlockCoords(pos);
            val target = new BlockPos.MutableBlockPos(pos);

            do {
                target.move(EnumFacing.DOWN);
            } while (!world.isOutsideBuildHeight(target) && chunk.getBlockState(target).getBlock() == this);

            if (chunk.getBlockState(target).getBlock().isReplaceable(world, target) && world.setBlockState(target, state)) {
                world.playSound(null, target, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);
                if (!player.isCreative()) stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROP_FACING);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess access, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        if (side.getAxis().isVertical()) {
            val other = access.getBlockState(pos.offset(side));
            return other.getBlock() == this && state.getValue(PROP_FACING) == other.getValue(PROP_FACING);
        } else return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        val placerFacing = placer.getHorizontalFacing().getOpposite();
        val above = world.getBlockState(pos.up());
        if (above.getBlock() == this) {
            return withFacing(above.getValue(PROP_FACING));
        }
        if (canPlaceBlockOnSide(world, pos, side)) {
            return withFacing(side);
        }
        if (canPlaceBlockOnSide(world, pos, placerFacing)) {
            return withFacing(placerFacing);
        }
        for (val facing : PROP_FACING.getAllowedValues()) {
            if (canPlaceBlockOnSide(world, pos, facing)) {
                return withFacing(facing);
            }
        }
        return withFacing(EnumFacing.DOWN);
    }
}
