package net.insomniakitten.bamboo.block;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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

public final class BlockSaltOre extends Block {
    public BlockSaltOre() {
        super(Material.ROCK, MapColor.SNOW);
        setSoundType(SoundType.STONE);
        setHardness(1.5F);
        setResistance(17.5F);
        setLightOpacity(1);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return isFancy() ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
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
            "field_145808_f",
            "dontSetBlock")) {
            return;
        }

        if (state == null || state.getBlock() != Blocks.ANVIL) return;
        if (!world.mayPlace(state.getBlock(), pos, true, EnumFacing.UP, null)) return;

        world.destroyBlock(pos.down(), false);

        val amount = 4 + world.rand.nextInt(5);
        val stack = new ItemStack(BamboozledItems.SALT_PILE, amount);

        Block.spawnAsEntity(world, pos.down(), stack);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        return !isFancy() || access.getBlockState(pos.offset(side)).getBlock() == this;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        val rand = access instanceof World ? ((World) access).rand : new Random();
        val amount = 4 + rand.nextInt(5) * (Math.max(0, rand.nextInt(fortune + 2) - 1) + 1);
        drops.add(new ItemStack(BamboozledItems.SALT_PILE, amount));
    }

    private boolean isFancy() {
        return Bamboozled.getClientConfig().isFancySaltOreForced()
            || !Blocks.LEAVES.getDefaultState().isOpaqueCube();
    }
}
