package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.BamboozledObjects;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public final class BlockSaltOre extends BlockBase implements ItemBlockSupplier, OreEntrySupplier {

    public BlockSaltOre() {
        super(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F);
        setOpaqueBlock(false);
        setLightOpacity(1);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockBase(this);
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos.offset(side)).getBlock() != this
                && super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onLanded(World world, Entity entity) {
        final double motion = entity.motionY;

        super.onLanded(world, entity);

        if (!entity.onGround || motion >= 0) return;
        if (!(entity instanceof EntityFallingBlock)) return;

        BlockPos pos = new BlockPos(entity);
        IBlockState state = ((EntityFallingBlock) entity).getBlock();

        if (ReflectionHelper.getPrivateValue(
                EntityFallingBlock.class,
                ((EntityFallingBlock) entity),
                "field_145808_f", "dontSetBlock")) return;

        if (state == null || state.getBlock() != Blocks.ANVIL) return;
        if (!world.mayPlace(state.getBlock(), pos, true, EnumFacing.UP, null)) return;

        world.destroyBlock(pos.down(), false);
        int amount = 4 + world.rand.nextInt(5);
        ItemStack stack = new ItemStack(BamboozledObjects.SALT_PILE, amount);
        Block.spawnAsEntity(world, pos.down(), stack);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos.offset(side)).getMaterial().isLiquid();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        int amount = 4 + rand.nextInt(5) * (Math.max(0, rand.nextInt(fortune + 2) - 1) + 1);
        drops.add(new ItemStack(BamboozledObjects.SALT_PILE, amount));
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this), "oreSalt", "oreHalite");
    }

}
