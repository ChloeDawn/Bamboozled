package net.insomniakitten.bamboo.block;

import lombok.val;
import net.insomniakitten.bamboo.util.LazyBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import java.util.Locale;
import java.util.function.Supplier;

public final class BlockSaltCrystal extends Block {
    public static final IProperty<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    private final Supplier<ItemBlock> item = new LazyBlockItem<>(this);

    public BlockSaltCrystal() {
        super(Material.ROCK, MapColor.SNOW);
        this.setSoundType(SoundType.STONE);
        this.setHardness(1.5F);
        this.setResistance(17.5F);
        this.setLightOpacity(1);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        val state = this.getDefaultState();
        val variant = Variant.byOrdinal(meta);

        return state.withProperty(BlockSaltCrystal.VARIANT, variant);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        val variant = state.getValue(BlockSaltCrystal.VARIANT);

        return variant.ordinal();
    }

    @Override
    public int damageDropped(final IBlockState state) {
        val variant = state.getValue(BlockSaltCrystal.VARIANT);

        return variant.ordinal();
    }

    @Override
    public void getSubBlocks(final CreativeTabs group, final NonNullList<ItemStack> items) {
        for (val variant : BlockSaltCrystal.VARIANT.getAllowedValues()) {
            items.add(new ItemStack(this.item.get(), 1, variant.ordinal()));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSaltCrystal.VARIANT);
    }

    public enum Variant implements IStringSerializable {
        BLOCK, BRICKS, BRICKS_CRACKED, BRICKS_CHISELED;

        private static final Variant[] BY_ORDINAL = Variant.values();

        public static Variant byOrdinal(final int meta) {
            return Variant.BY_ORDINAL[meta % Variant.BY_ORDINAL.length];
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
