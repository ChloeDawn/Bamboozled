package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.BamboozledConfig;
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

    private final boolean dropBlock;
    private final boolean saltHurtsUndead;

    public BlockSalt() {
        super(Material.SAND, MapColor.SNOW, SoundType.SAND, 0.5F, 2.5F);
        dropBlock = BamboozledConfig.GENERAL.saltBlockDropsItself;
        saltHurtsUndead = BamboozledConfig.GENERAL.saltHurtsUndead;
    }

    public boolean shouldDropBlock() {
        return dropBlock;
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
        if (saltHurtsUndead && entity instanceof EntityLiving) {
            final EntityLivingBase living = (EntityLivingBase) entity;
            if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                if (world.getTotalWorldTime() % 20 == 0) {
                    living.attackEntityFrom(DamageSource.MAGIC, 1);
                }
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(dropBlock ? new ItemStack(this) : new ItemStack(BamboozledItems.SALT_PILE, 9));
    }

    private void checkForFall(World world, BlockPos pos) {
        if ((world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0) {
            if (!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                if (!world.isRemote) {
                    world.spawnEntity(new EntityFallingSaltBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
                }
            } else {
                world.setBlockToAir(pos);

                BlockPos target;

                target = pos.down();

                while ((world.isAirBlock(target) || canFallThrough(world.getBlockState(target))) && target.getY() > 0) {
                    target = target.down();
                }

                if (target.getY() > 0) {
                    world.setBlockState(target.up(), world.getBlockState(pos));
                }
            }
        }
    }

}
