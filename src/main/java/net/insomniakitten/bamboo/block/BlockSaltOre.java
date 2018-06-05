package net.insomniakitten.bamboo.block;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.block.base.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
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

public final class BlockSaltOre extends BlockBase {
    public BlockSaltOre() {
        super(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F);
        setOpaqueBlock(false);
        setLightOpacity(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return Bamboozled.getClientConfig().isFancySaltOreForced() || isFancyGraphics()
               ? BlockRenderLayer.TRANSLUCENT
               : BlockRenderLayer.SOLID;
    }

    @Override
    public void onLanded(World world, Entity entity) {
        val motion = entity.motionY;

        super.onLanded(world, entity);

        if (!entity.onGround || motion >= 0) return;
        if (!(entity instanceof EntityFallingBlock)) return;

        val pos = new BlockPos(entity);
        val state = ((EntityFallingBlock) entity).getBlock();

        if (ReflectionHelper.getPrivateValue(
                EntityFallingBlock.class,
                ((EntityFallingBlock) entity),
                "field_145808_f", "dontSetBlock")) return;

        if (state == null || state.getBlock() != Blocks.ANVIL) return;
        if (!world.mayPlace(state.getBlock(), pos, true, EnumFacing.UP, null)) return;

        world.destroyBlock(pos.down(), false);

        val amount = 4 + world.rand.nextInt(5);
        val stack = new ItemStack(BamboozledItems.SALT_PILE, amount);

        Block.spawnAsEntity(world, pos.down(), stack);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return !Bamboozled.getClientConfig().isFancySaltOreForced() && !isFancyGraphics()
                || world.getBlockState(pos.offset(face)).getBlock() == this;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        val rand = world instanceof World ? ((World) world).rand : new Random();
        val amount = 4 + rand.nextInt(5) * (Math.max(0, rand.nextInt(fortune + 2) - 1) + 1);
        drops.add(new ItemStack(BamboozledItems.SALT_PILE, amount));
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.SOLID;
    }

    private boolean isFancyGraphics() {
        return !Blocks.LEAVES.isOpaqueCube(Blocks.LEAVES.getDefaultState());
    }
}
