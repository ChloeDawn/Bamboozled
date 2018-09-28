package net.insomniakitten.bamboo.item.base;

import net.insomniakitten.bamboo.block.base.BlockSlab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemBlockSlabBase extends ItemBlockBase {
    private final BlockSlab slab;

    public ItemBlockSlabBase(final Block slab) {
        super(slab);

        if (!(slab instanceof BlockSlab)) {
            throw new IllegalArgumentException(slab.getClass().getName());
        }

        this.slab = (BlockSlab) slab;
    }

    @Override
    public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos position, final EnumHand hand, final EnumFacing face, final float x, final float y, final float z) {
        final ItemStack stack = player.getHeldItem(hand);

        if (stack.isEmpty()) {
            return EnumActionResult.FAIL;
        }

        if (!player.canPlayerEdit(position.offset(face), face, stack)) {
            return EnumActionResult.FAIL;
        }

        if (this.tryPlace(player, stack, world, position, face)) {
            return EnumActionResult.SUCCESS;
        }

        if (this.tryPlace(player, stack, world, position.offset(face), null)) {
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUse(player, world, position, hand, face, x, y, z);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(final World world, final BlockPos position, final EnumFacing face, final EntityPlayer player, final ItemStack stack) {
        final IBlockState state = world.getBlockState(position);

        if (this.slab == state.getBlock()) {
            if (EnumFacing.UP == face && this.slab.isLowerSlab(state)) {
                return true;
            }

            if (EnumFacing.DOWN == face && this.slab.isUpperSlab(state)) {
                return true;
            }
        }

        final BlockPos offset = position.offset(face);
        final IBlockState other = world.getBlockState(offset);

        if (this.slab == other.getBlock()) {
            return true;
        }

        return super.canPlaceBlockOnSide(world, position, face, player, stack);
    }

    private boolean tryPlace(final EntityPlayer player, final ItemStack stack, final World world, final BlockPos position, @Nullable final EnumFacing face) {
        final IBlockState state = world.getBlockState(position);

        if (state.getBlock() != this.slab) {
            return false;
        }

        if (this.slab.isDoubleSlab(state)) {
            return false;
        }

        if (face == null) {
            return this.placeDoubleSlab(world, position, player, stack);
        }

        if (face == EnumFacing.UP && this.slab.isLowerSlab(state)) {
            return this.placeDoubleSlab(world, position, player, stack);
        }

        if (face == EnumFacing.DOWN && this.slab.isUpperSlab(state)) {
            return this.placeDoubleSlab(world, position, player, stack);
        }

        return false;
    }

    private boolean placeDoubleSlab(final World world, final BlockPos position, final EntityPlayer player, final ItemStack stack) {
        final IBlockState slab = this.slab.getDoubleSlab();
        @Nullable final AxisAlignedBB box = slab.getCollisionBoundingBox(world, position);

        if (box == null) {
            return false;
        }

        if (!world.checkNoEntityCollision(box.offset(position))) {
            return false;
        }

        if (!world.setBlockState(position, slab)) {
            return false;
        }

        final SoundType soundType = slab.getBlock().getSoundType(slab, world, position, player);
        final SoundEvent sound = soundType.getPlaceSound();
        final float volume = (soundType.getVolume() + 1.0F) / 2.0F;
        final float pitch = soundType.getPitch() * 0.8F;

        world.playSound(player, position, sound, SoundCategory.BLOCKS, volume, pitch);
        stack.shrink(1);

        return true;
    }
}
