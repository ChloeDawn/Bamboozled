package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.val;
import net.insomniakitten.bamboo.util.LazyBlockItem;
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
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Supplier;

public final class BlockRope extends Block {
    public static final IProperty<EnumFacing> FACING = PropertyDirection.create(
        "facing", facing -> EnumFacing.UP != facing
    );

    public static final ImmutableMap<EnumFacing, AxisAlignedBB> FACING_AABB = Maps.immutableEnumMap(
        ImmutableMap.of(
            EnumFacing.DOWN, new AxisAlignedBB(0.375D, 0.0, 0.375D, 0.625D, 1.0D, 0.625D),
            EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.0D, 0.8125D, 0.625D, 1.0D, 1.0D),
            EnumFacing.SOUTH, new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.1875D),
            EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
            EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.1875D, 1.0D, 0.625D)
        )
    );

    public static final Set<BlockFaceShape> CONNECTABLE_CEILING_SHAPES = Sets.immutableEnumSet(
        BlockFaceShape.SOLID, BlockFaceShape.CENTER, BlockFaceShape.CENTER_BIG
    );

    private final Supplier<ItemBlock> item = new LazyBlockItem(this);

    public BlockRope() {
        super(Material.CIRCUITS);
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.4F);
        this.setResistance(2.0F);
    }

    public IBlockState withFacing(final EnumFacing facing) {
        val state = this.getDefaultState();

        return state.withProperty(BlockRope.FACING, facing);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        val facing = EnumFacing.byIndex(meta);

        return this.withFacing(facing);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockRope.FACING).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState withRotation(final IBlockState state, final Rotation rotation) {
        val facing = state.getValue(BlockRope.FACING);

        if (facing.getAxis().isHorizontal()) {
            val rotated = rotation.rotate(facing);

            return state.withProperty(BlockRope.FACING, rotated);
        }

        return state;
    }

    @Override
    @Deprecated
    public IBlockState withMirror(final IBlockState state, final Mirror mirror) {
        val facing = state.getValue(BlockRope.FACING);

        if (facing.getAxis().isHorizontal()) {
            val rotation = mirror.toRotation(facing);

            return state.withRotation(rotation);
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
        val facing = state.getValue(BlockRope.FACING);

        return BlockRope.FACING_AABB.get(facing);
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos position, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void neighborChanged(final IBlockState state, final World world, final BlockPos position, final Block neighbor, final BlockPos offset) {
        val facing = state.getValue(BlockRope.FACING);

        if (!world.isRemote && !this.canPlaceBlockOnSide(world, position, facing)) {
            world.destroyBlock(position, true);
        }
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos position, final IBlockState state) {
        val facing = state.getValue(BlockRope.FACING);

        if (!world.isRemote && !this.canPlaceBlockOnSide(world, position, facing)) {
            world.destroyBlock(position, true);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(final World world, final BlockPos position, final EnumFacing face) {
        val offset = position.offset(face.getOpposite());
        val other = world.getBlockState(offset);
        val shape = other.getBlockFaceShape(world, offset, face);

        if (!BlockRope.FACING.getAllowedValues().contains(face)) {
            return this.canPlaceBlockAt(world, position);
        }

        val above = world.getBlockState(position.up());

        if (this == above.getBlock() && face == above.getValue(BlockRope.FACING)) {
            return true;
        }

        if (EnumFacing.DOWN == face) {
            return BlockRope.CONNECTABLE_CEILING_SHAPES.contains(shape);
        }

        return BlockFaceShape.SOLID == shape;
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos pos) {
        for (val facing : BlockRope.FACING.getAllowedValues()) {
            if (this.canPlaceBlockOnSide(world, pos, facing)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos position, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        val stack = player.getHeldItem(hand);

        if (stack.isEmpty() || this.item.get() != stack.getItem()) {
            return false;
        }

        val chunk = world.getChunk(position);
        val target = new BlockPos.MutableBlockPos(position);

        do {
            target.move(EnumFacing.DOWN);
        } while (!world.isOutsideBuildHeight(target) && state == chunk.getBlockState(target));

        if (!chunk.getBlockState(target).getBlock().isReplaceable(world, target)) {
            return false;
        }

        if (!world.setBlockState(target, state)) {
            return false;
        }

        world.playSound(null, target, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        return true;
    }

    @Override
    @Deprecated
    public EnumPushReaction getPushReaction(final IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockRope.FACING);
    }

    @Override
    public boolean isLadder(final IBlockState state, final IBlockAccess access, final BlockPos position, final EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        if (face.getAxis().isHorizontal()) {
            return false;
        }

        val other = access.getBlockState(position.offset(face));

        return other.getBlock() == this && state.getValue(BlockRope.FACING) == other.getValue(BlockRope.FACING);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos position, final EnumFacing face) {
        val state = world.getBlockState(position);
        val facing = state.getValue(BlockRope.FACING);

        if (facing.getAxis().isVertical()) {
            return false;
        }

        if (this.canPlaceBlockOnSide(world, position, facing.rotateY())) {
            return world.setBlockState(position, state.withProperty(BlockRope.FACING, facing.rotateY()));
        }

        if (this.canPlaceBlockOnSide(world, position, facing.getOpposite())) {
            return world.setBlockState(position, state.withProperty(BlockRope.FACING, facing.getOpposite()));
        }

        if (this.canPlaceBlockOnSide(world, position, facing.rotateYCCW())) {
            return world.setBlockState(position, state.withProperty(BlockRope.FACING, facing.rotateYCCW()));
        }

        return false;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos position, final EnumFacing face, final float x, final float y, final float z, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        val placerFacing = placer.getHorizontalFacing().getOpposite();
        val above = world.getBlockState(position.up());

        if (this == above.getBlock() && face == above.getValue(BlockRope.FACING)) {
            return this.withFacing(above.getValue(BlockRope.FACING));
        }

        if (EnumFacing.UP != face && this.canPlaceBlockOnSide(world, position, face)) {
            return this.withFacing(face);
        }

        if (this.canPlaceBlockOnSide(world, position, placerFacing)) {
            return this.withFacing(placerFacing);
        }

        for (val facing : BlockRope.FACING.getAllowedValues()) {
            if (this.canPlaceBlockOnSide(world, position, facing)) {
                return this.withFacing(facing);
            }
        }

        return this.withFacing(EnumFacing.DOWN);
    }
}
