package net.insomniakitten.bamboo.item.base;

import lombok.val;
import net.insomniakitten.bamboo.block.base.BlockSlabBase;
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
    private final BlockSlabBase slab;

    public ItemBlockSlabBase(Block slab) {
        super(slab);
        this.slab = (BlockSlabBase) slab;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        val stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            if (tryPlace(player, stack, world, pos, facing)) {
                return EnumActionResult.SUCCESS;
            }
            if (tryPlace(player, stack, world, pos.offset(facing), null)) {
                return EnumActionResult.SUCCESS;
            }
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        val state = world.getBlockState(pos);
        return state.getBlock() == slab
                && (side == EnumFacing.UP && slab.isLower(state)
                || side == EnumFacing.DOWN && slab.isUpper(state))
                || world.getBlockState(pos.offset(side)).getBlock() == this.slab
                || super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    private boolean tryPlace(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        val state = world.getBlockState(pos);
        if (state.getBlock() != slab) return false;
        if (slab.isDouble(state)) return false;
        if (side == null || side == EnumFacing.UP && slab.isLower(state) || side == EnumFacing.DOWN && slab.isUpper(state)) {
            val dSlab = slab.getDouble();
            val aabb = dSlab.getCollisionBoundingBox(world, pos);
            if (aabb == null) return false;
            if (world.checkNoEntityCollision(aabb.offset(pos)) && world.setBlockState(pos, dSlab)) {
                val sound = dSlab.getBlock().getSoundType(dSlab, world, pos, player);
                world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                        (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }
}
