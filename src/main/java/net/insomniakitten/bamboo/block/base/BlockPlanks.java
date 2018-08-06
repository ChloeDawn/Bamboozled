package net.insomniakitten.bamboo.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Locale;

public class BlockPlanks extends Block {
    private static final PropertyEnum<Orientation> ORIENTATION = PropertyEnum.create("orientation", Orientation.class);

    public BlockPlanks() {
        super(Material.WOOD);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2.0F);
        this.setResistance(15.0F);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockPlanks.ORIENTATION, Orientation.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockPlanks.ORIENTATION).ordinal();
    }

    @Override
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockPlanks.ORIENTATION);
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess access, final BlockPos pos, final IBlockState state, final int fortune) {
        drops.add(new ItemStack(this, 1, this.getMetaFromState(state)));
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        world.setBlockState(pos, world.getBlockState(pos).cycleProperty(BlockPlanks.ORIENTATION));
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        return this.getDefaultState().withProperty(BlockPlanks.ORIENTATION, meta > 0 ? Orientation.VERTICAL : Orientation.HORIZONTAL);
    }

    private enum Orientation implements IStringSerializable {
        HORIZONTAL, VERTICAL;

        private static final Orientation[] VALUES = Orientation.values();

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
