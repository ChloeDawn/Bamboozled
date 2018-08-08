package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import java.util.Locale;

public final class BlockSaltCrystal extends Block {
    public static final IProperty<Variant> PROP_VARIANT = PropertyEnum.create("variant", Variant.class);

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
        return this.getDefaultState().withProperty(BlockSaltCrystal.PROP_VARIANT, Variant.valueOf(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockSaltCrystal.PROP_VARIANT).ordinal();
    }

    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockSaltCrystal.PROP_VARIANT).ordinal();
    }

    @Override
    public void getSubBlocks(final CreativeTabs group, final NonNullList<ItemStack> items) {
        items.add(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 0));
        items.add(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 1));
        items.add(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 2));
        items.add(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 3));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSaltCrystal.PROP_VARIANT);
    }

    public enum Variant implements IStringSerializable {
        BLOCK, BRICKS, BRICKS_CRACKED, BRICKS_CHISELED;

        private static final Variant[] VALUES = Variant.values();

        public static Variant valueOf(final int meta) {
            return Variant.VALUES[meta % Variant.VALUES.length];
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
