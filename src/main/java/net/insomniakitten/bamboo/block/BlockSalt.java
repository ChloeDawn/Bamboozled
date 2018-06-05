package net.insomniakitten.bamboo.block;

import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public final class BlockSalt extends BlockFalling {
    public BlockSalt() {
        super(Material.SAND);
        setHardness(0.5F);
        setResistance(2.5F);
        setSoundType(SoundType.SAND);
    }

    private boolean isEmpty(IBlockAccess access, BlockPos pos) {
        val state = access.getBlockState(pos);
        return state.getBlock().isAir(state, access, pos) && canFallThrough(state);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote && pos.getY() >= 0 && isEmpty(world, pos.down())) {
            if (fallInstantly || !world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                world.setBlockToAir(pos);

                var target = pos.down();

                while (target.getY() > 0 && isEmpty(world, pos.down())) {
                    target = target.down();
                }

                if (target.getY() > 0) {
                    world.setBlockState(target.up(), world.getBlockState(pos));
                }
            } else {
                val x = pos.getX() + 0.5D;
                val y = pos.getY();
                val z = pos.getZ() + 0.5D;
                world.spawnEntity(new EntityFallingSaltBlock(world, x, y, z));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state) {
        return 0xE9E9E9;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!Bamboozled.getConfig().isSaltUndeadDamageEnabled()) return;
        if (!(entity instanceof EntityLiving)) return;

        if (((EntityLivingBase) entity).isEntityUndead()) {
            if (world.getTotalWorldTime() % 20 == 0) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1);
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
            drops.add(new ItemStack(BamboozledBlocks.SALT_BLOCK));
        } else drops.add(new ItemStack(BamboozledItems.SALT_PILE, 9));
    }
}
