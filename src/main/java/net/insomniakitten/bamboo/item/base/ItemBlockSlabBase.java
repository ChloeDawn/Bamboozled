package net.insomniakitten.bamboo.item.base;

import lombok.val;
import net.insomniakitten.bamboo.block.base.BlockSlab;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockSlabBase extends ItemBlockBase {
    private final BlockSlab slab;

    public ItemBlockSlabBase(final Block slab) {
        super(slab);
        this.slab = (BlockSlab) slab;
    }

    @Override
    public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        val stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            if (this.tryPlace(player, stack, world, pos, facing)) {
                return EnumActionResult.SUCCESS;
            }
            if (this.tryPlace(player, stack, world, pos.offset(facing), null)) {
                return EnumActionResult.SUCCESS;
            }
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(final World world, final BlockPos pos, final EnumFacing side, final EntityPlayer player, final ItemStack stack) {
        val state = world.getBlockState(pos);

        if (state.getBlock() == this.slab) {
            if (side == EnumFacing.UP && this.slab.isLower(state)) {
                return true;
            }

            if (side == EnumFacing.DOWN && this.slab.isUpper(state)) {
                return true;
            }
        }

        if (world.getBlockState(pos.offset(side)).getBlock() == this.slab) {
            return true;
        }

        return super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    private boolean tryPlace(final EntityPlayer player, final ItemStack stack, final World world, final BlockPos pos, final EnumFacing side) {
        val state = world.getBlockState(pos);

        if (state.getBlock() != this.slab) {
            return false;
        }

        if (this.slab.isDouble(state)) return false;
        if (side == null || side == EnumFacing.UP && slab.isLower(state)) {
            val dSlab = this.slab.getDouble();
            val aabb = dSlab.getCollisionBoundingBox(world, pos);

            if (aabb == null) {
                return false;
            }

            if (world.checkNoEntityCollision(aabb.offset(pos)) && world.setBlockState(pos, dSlab)) {
                val sound = dSlab.getBlock().getSoundType(dSlab, world, pos, player);
                world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound
                    .getPitch() * 0.8F);
                stack.shrink(1);
            }

            return true;
        }

        if (side == EnumFacing.DOWN && this.slab.isUpper(state)) {
            val dSlab = this.slab.getDouble();
            val aabb = dSlab.getCollisionBoundingBox(world, pos);

            if (aabb == null) {
                return false;
            }

            if (world.checkNoEntityCollision(aabb.offset(pos)) && world.setBlockState(pos, dSlab)) {
                val sound = dSlab.getBlock().getSoundType(dSlab, world, pos, player);
                world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound
                    .getPitch() * 0.8F);
                stack.shrink(1);
            }

            return true;
        }

        return false;
    }
}
