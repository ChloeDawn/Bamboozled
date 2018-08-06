package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
    private static final IProperty<EnumFacing> PROP_FACING = PropertyDirection.create("facing", f -> f != EnumFacing.UP);

    private static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB = ImmutableMap.of(
        EnumFacing.DOWN, new AxisAlignedBB(0.375D, 0.0, 0.375D, 0.625D, 1.0D, 0.625D),
        EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.0D, 0.8125D, 0.625D, 1.0D, 1.0D),
        EnumFacing.SOUTH, new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.1875D),
        EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
        EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.1875D, 1.0D, 0.625D)
    );

    public BlockRope() {
        super(Material.CIRCUITS);
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.4F);
        this.setResistance(2.0F);
    }

    public IBlockState withFacing(final EnumFacing facing) {
        return this.getDefaultState().withProperty(BlockRope.PROP_FACING, facing);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.withFacing(EnumFacing.values()[meta & 7]);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockRope.PROP_FACING).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState withRotation(final IBlockState state, final Rotation rotation) {
        return state.withProperty(BlockRope.PROP_FACING, rotation.rotate(state.getValue(BlockRope.PROP_FACING)));
    }

    @Override
    @Deprecated
    public IBlockState withMirror(final IBlockState state, final Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(BlockRope.PROP_FACING)));
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return BlockRope.AABB.get(state.getValue(BlockRope.PROP_FACING));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block block, final BlockPos fromPos) {
        this.checkForDrop(world, pos, state);
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
        this.checkForDrop(world, pos, state);
    }

    @Override
    public boolean canPlaceBlockOnSide(final World world, final BlockPos pos, final EnumFacing side) {
        val offset = pos.offset(side.getOpposite());
        val state = world.getBlockState(offset);
        val shape = state.getBlockFaceShape(world, offset, side);
        val isValidSide = BlockRope.PROP_FACING.getAllowedValues().contains(side);
        return isValidSide && (world.getBlockState(pos.up()).getBlock() == this || shape == BlockFaceShape.SOLID);
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos pos) {
        for (val side : BlockRope.PROP_FACING.getAllowedValues()) {
            if (this.canPlaceBlockOnSide(world, pos, side)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        val stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == BamboozledItems.ROPE) {
            val chunk = world.getChunk(pos);
            val target = new BlockPos.MutableBlockPos(pos);

            do {
                target.move(EnumFacing.DOWN);
            } while (!world.isOutsideBuildHeight(target) && chunk.getBlockState(target).getBlock() == this);

            if (chunk
                .getBlockState(target)
                .getBlock()
                .isReplaceable(world, target) && world.setBlockState(target, state)) {
                world.playSound(null, target, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public EnumPushReaction getPushReaction(final IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockRope.PROP_FACING);
    }

    @Override
    public boolean isLadder(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        if (side.getAxis().isHorizontal()) {
            return false;
        }

        val other = access.getBlockState(pos.offset(side));
        return other.getBlock() == this && state.getValue(BlockRope.PROP_FACING) == other.getValue(BlockRope.PROP_FACING);
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        val placerFacing = placer.getHorizontalFacing().getOpposite();
        val above = world.getBlockState(pos.up());
        if (above.getBlock() == this) {
            return this.withFacing(above.getValue(BlockRope.PROP_FACING));
        }
        if (this.canPlaceBlockOnSide(world, pos, side)) {
            return this.withFacing(side);
        }
        if (this.canPlaceBlockOnSide(world, pos, placerFacing)) {
            return this.withFacing(placerFacing);
        }
        for (val facing : BlockRope.PROP_FACING.getAllowedValues()) {
            if (this.canPlaceBlockOnSide(world, pos, facing)) {
                return this.withFacing(facing);
            }
        }
        return this.withFacing(EnumFacing.DOWN);
    }

    private void checkForDrop(final World world, final BlockPos pos, final IBlockState state) {
        if (!this.canPlaceBlockOnSide(world, pos, state.getValue(BlockRope.PROP_FACING))) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }
}
