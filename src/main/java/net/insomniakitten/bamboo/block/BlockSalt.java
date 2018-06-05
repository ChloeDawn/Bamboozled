package net.insomniakitten.bamboo.block;

import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.block.base.BlockFallingBase;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public final class BlockSalt extends BlockFallingBase {
    public BlockSalt() {
        super(Material.SAND, MapColor.SNOW, SoundType.SAND, 0.5F, 2.5F);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) checkForFall(world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state) {
        return 0xE9E9E9;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (Bamboozled.getConfig().isSaltUndeadDamageEnabled() && entity instanceof EntityLiving) {
            val living = (EntityLivingBase) entity;
            if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                if (world.getTotalWorldTime() % 20 == 0) {
                    living.attackEntityFrom(DamageSource.MAGIC, 1);
                }
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        drops.add(Bamboozled.getConfig().isSaltBlockDropsEnabled() ? new ItemStack(this) : new ItemStack(BamboozledItems.SALT_PILE, 9));
    }

    private void checkForFall(World world, BlockPos pos) {
        if ((world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0) {
            if (!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                if (!world.isRemote) {
                    val x = pos.getX() + 0.5D;
                    val y = pos.getY();
                    val z = pos.getZ() + 0.5D;
                    world.spawnEntity(new EntityFallingSaltBlock(world, x, y, z));
                }
            } else {
                world.setBlockToAir(pos);

                var target = pos.down();

                while (target.getY() > 0 && (world.isAirBlock(target) || canFallThrough(world.getBlockState(target)))) {
                    target = target.down();
                }

                if (target.getY() > 0) {
                    world.setBlockState(target.up(), world.getBlockState(pos));
                }
            }
        }
    }
}
