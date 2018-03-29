package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.block.base.BlockBase;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public final class BlockRope extends BlockBase {

    private static final PropertyDirection FACING = PropertyDirection.create("facing", f -> f != EnumFacing.UP);

    private static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB_ROPE = ImmutableMap.of(
            EnumFacing.DOWN, new AxisAlignedBB(0.375D, 0.0, 0.375D, 0.625D, 1.0D, 0.625D),
            EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.0D, 0.8125D, 0.625D, 1.0D, 1.0D),
            EnumFacing.SOUTH, new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.1875D),
            EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
            EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.1875D, 1.0D, 0.625D)
    );

    public BlockRope() {
        super(Material.CIRCUITS, SoundType.CLOTH, 0.4F, 2.0F);
        setFullBlock(false);
        setOpaqueBlock(false);
    }

    public IBlockState withFacing(EnumFacing facing) {
        return getDefaultState().withProperty(FACING, facing);
    }

    private boolean doFacingsMatch(IBlockState a, IBlockState b) {
        boolean matchBlock = a.getBlock() == this && b.getBlock() == this;
        return matchBlock && a.getValue(FACING) == b.getValue(FACING);
    }

    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing facing) {
        pos = pos.offset(facing.getOpposite());
        IBlockState state = world.getBlockState(pos);
        BlockFaceShape shape = state.getBlockFaceShape(world, pos, facing);
        IBlockState above = world.getBlockState(pos.offset(facing).up());
        boolean isValidSide = FACING.getAllowedValues().contains(facing);
        return isValidSide && (above.getBlock() == this || shape == BlockFaceShape.SOLID);
    }

    private void checkForDrop(World world, BlockPos pos, IBlockState state) {
        if (!canPlaceAt(world, pos, state.getValue(FACING))) {
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
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @Deprecated
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState offset = world.getBlockState(pos.offset(side));
        return (offset.getBlock() != this || !doFacingsMatch(state, offset))
                && super.shouldSideBeRendered(state, world, pos, side);
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
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing side : FACING.getAllowedValues()) {
            if (canPlaceAt(world, pos, side)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == BamboozledItems.ROPE) {
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos(pos);

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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing placerFacing = placer.getHorizontalFacing().getOpposite();
        IBlockState above = world.getBlockState(pos.up());
        if (above.getBlock() == this) {
            return withFacing(above.getValue(FACING));
        }
        if (canPlaceAt(world, pos, side)) {
            return withFacing(side);
        }
        if (canPlaceAt(world, pos, placerFacing)) {
            return withFacing(placerFacing);
        }
        for (EnumFacing facing : FACING.getAllowedValues()) {
            if (canPlaceAt(world, pos, facing)) {
                return withFacing(facing);
            }
        }
        return withFacing(EnumFacing.DOWN);
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess world, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(AABB_ROPE.get(state.getValue(FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

}
