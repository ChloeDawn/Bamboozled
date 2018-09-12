package net.insomniakitten.bamboo.block;

import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.init.BamboozledItems;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.util.LazyBlockItem;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;
import java.util.function.Supplier;

public final class BlockSalt extends BlockFalling {
    public static final int DUST_COLOR = 0xE9E9E9;

    private final Supplier<ItemBlock> item = new LazyBlockItem(this);

    public BlockSalt() {
        super(Material.SAND);
        this.setHardness(0.5F);
        this.setResistance(2.5F);
        this.setSoundType(SoundType.SAND);
    }

    @Override
    public void updateTick(final World world, final BlockPos position, final IBlockState state, final Random random) {
        if (world.isRemote || position.getY() < 0 || !this.isEmpty(world, position.down())) {
            return;
        }

        if (BlockFalling.fallInstantly) {
            world.setBlockToAir(position);

            var below = position.down();

            while (below.getY() > 0 && this.isEmpty(world, position.down())) {
                below = below.down();
            }

            if (below.getY() > 0) {
                world.setBlockState(below.up(), world.getBlockState(position));
            }

            return;
        }

        val min = position.add(-32, -32, -32);
        val max = position.add(32, 32, 32);

        if (world.isAreaLoaded(min, max)) {
            val x = position.getX() + 0.5D;
            val y = position.getY();
            val z = position.getZ() + 0.5D;

            world.spawnEntity(new EntityFallingSaltBlock(world, x, y, z));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getDustColor(final IBlockState state) {
        return BlockSalt.DUST_COLOR;
    }

    @Override
    public void onEntityWalk(final World world, final BlockPos position, final Entity entity) {
        if (!Bamboozled.getConfig().isSaltUndeadDamageEnabled()) {
            return;
        }

        if (!(entity instanceof EntityLiving)) {
            return;
        }

        if (((EntityLivingBase) entity).isEntityUndead()) {
            if (world.getTotalWorldTime() % 20 == 0) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1);
            }
        }
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess access, final BlockPos position, final IBlockState state, final int fortune) {
        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
            drops.add(new ItemStack(this.item.get()));
        } else {
            drops.add(new ItemStack(BamboozledItems.SALT_PILE, 9));
        }
    }

    private boolean isEmpty(final IBlockAccess access, final BlockPos position) {
        val state = access.getBlockState(position);
        val block = state.getBlock();

        return block.isAir(state, access, position) && BlockFalling.canFallThrough(state);
    }
}
