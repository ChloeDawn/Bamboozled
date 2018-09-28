package net.insomniakitten.bamboo.block.base;

import lombok.val;
import net.insomniakitten.bamboo.util.LazyBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
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
import java.util.function.Supplier;

public class BlockPlanks extends Block {
    private static final IProperty<Orientation> ORIENTATION = PropertyEnum.create("orientation", Orientation.class);

    private final Supplier<ItemBlock> item = LazyBlockItem.of(this);

    public BlockPlanks() {
        super(Material.WOOD);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2.0F);
        this.setResistance(15.0F);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        val state = this.getDefaultState();
        val orientation = Orientation.byOrdinal(meta);

        return state.withProperty(BlockPlanks.ORIENTATION, orientation);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        val orientation = state.getValue(BlockPlanks.ORIENTATION);

        return orientation.ordinal();
    }

    @Override
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        items.add(new ItemStack(this.item.get(), 1, 0));
        items.add(new ItemStack(this.item.get(), 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockPlanks.ORIENTATION);
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess access, final BlockPos position, final IBlockState state, final int fortune) {
        drops.add(new ItemStack(this.item.get(), 1, this.getMetaFromState(state)));
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult hit, final World world, final BlockPos position, final EntityPlayer player) {
        return new ItemStack(this.item.get(), 1, this.getMetaFromState(state));
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos position, final EnumFacing axis) {
        val state = world.getBlockState(position);

        if (this != state.getBlock()) {
            throw new IllegalStateException("Cannot rotate " + state + " at " + position);
        }

        return world.setBlockState(position, state.cycleProperty(BlockPlanks.ORIENTATION));
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos position, final EnumFacing face, final float x, final float y, final float z, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        val state = this.getDefaultState();
        val orientation = meta > 0 ? Orientation.VERTICAL : Orientation.HORIZONTAL;

        return state.withProperty(BlockPlanks.ORIENTATION, orientation);
    }

    public enum Orientation implements IStringSerializable {
        HORIZONTAL, VERTICAL;

        private static final Orientation[] BY_ORDINAL = Orientation.values();

        public static Orientation byOrdinal(final int ordinal) {
            return Orientation.BY_ORDINAL[ordinal % Orientation.BY_ORDINAL.length];
        }

        @Override
        public final String getName() {
            return this.toString();
        }

        @Override
        public final String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
