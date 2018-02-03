package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;

public abstract class BlockSlabBase extends BlockSlab {

    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    private String name;

    public BlockSlabBase(Material material, MapColor mapColor, SoundType sound, float hardness, float resistance) {
        super(material, mapColor);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
        setCreativeTab(Bamboozled.TAB);
        setLightOpacity(0);
    }

    public BlockSlabBase(Material material, SoundType sound, float hardness, float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    public IBlockState getLower() {
        return getDefaultState().withProperty(VARIANT, Variant.LOWER);
    }

    public IBlockState getUpper() {
        return getDefaultState().withProperty(VARIANT, Variant.UPPER);
    }

    public IBlockState getDouble() {
        return getDefaultState().withProperty(VARIANT, Variant.DOUBLE);
    }

    public final boolean isLower(IBlockState state) {
        return state.getPropertyKeys().contains(VARIANT)
                && Variant.LOWER == state.getValue(VARIANT);
    }

    public final boolean isUpper(IBlockState state) {
        return state.getPropertyKeys().contains(VARIANT)
                && Variant.UPPER == state.getValue(VARIANT);
    }

    public final boolean isDouble(IBlockState state) {
        return state.getPropertyKeys().contains(VARIANT)
                && Variant.DOUBLE == state.getValue(VARIANT);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(VARIANT).getBoundingBox();
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return isDouble(state) || isUpper(state);
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing side) {
        return state.getValue(VARIANT).isSideSolid(side) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return !ForgeModContainer.disableStairSlabCulling && state.getValue(VARIANT).isSideSolid(side);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getStateForPlacement(world, pos, side, hitX, hitY, hitZ, meta, placer, placer.getActiveHand());
    }

    @Override
    @Deprecated
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState target = world.getBlockState(pos.offset(side));
        if (side.getAxis().isHorizontal()) {
            if (target.getBlock() instanceof BlockSlabBase
                    && target.getPropertyKeys().contains(VARIANT)) {
                if (state.getValue(VARIANT) == target.getValue(VARIANT)) return false;
            }
            if (target.getBlock() instanceof BlockSlab
                    && target.getPropertyKeys().contains(BlockSlab.HALF)) {
                EnumBlockHalf half = target.getValue(BlockSlab.HALF);
                if (state.getValue(VARIANT).doesMatchHalf(half)) return false;
            }
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public String getUnlocalizedName(int meta) {
        return getUnlocalizedName();
    }

    @Override
    @Deprecated // Use state-sensitive version
    public boolean isDouble() {
        return false;
    }

    @Override
    @Deprecated // Only used by ItemSlab
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    @Deprecated // Only used by ItemSlab
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return o -> 0;
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        Variant variant = Variant.VALUES[meta];
        return getDefaultState().withProperty(VARIANT, variant);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public final Block setUnlocalizedName(String name) {
        return super.setUnlocalizedName(Bamboozled.ID + "." + name);
    }

    @Override
    public final String getUnlocalizedName() {
        if (name == null) {
            name = super.getUnlocalizedName()
                    .replace("tile.", "block.");
        }
        return name;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return isDouble(state) ? 2 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (side == EnumFacing.UP) return getLower();
        if (side == EnumFacing.DOWN) return getUpper();
        return hitY > 0.5D ? getUpper() : getLower();
    }

    public enum Variant implements IStringSerializable {
        LOWER(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), EnumFacing.DOWN::equals, EnumBlockHalf.BOTTOM::equals),
        UPPER(new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D), EnumFacing.UP::equals, EnumBlockHalf.TOP::equals),
        DOUBLE(FULL_BLOCK_AABB, side -> true, half -> false);

        public static final Variant[] VALUES = values();

        private final AxisAlignedBB boundingBox;
        private final Predicate<EnumFacing> solidSide;
        private final Predicate<EnumBlockHalf> halfMatcher;

        Variant(AxisAlignedBB boundingBox, Predicate<EnumFacing> solidSide, Predicate<EnumBlockHalf> halfMatcher) {
            this.boundingBox = boundingBox;
            this.solidSide = solidSide;
            this.halfMatcher = halfMatcher;
        }

        public AxisAlignedBB getBoundingBox() {
            return boundingBox;
        }

        public boolean isSideSolid(EnumFacing side) {
            return solidSide.test(side);
        }

        public boolean doesMatchHalf(EnumBlockHalf half) {
            return halfMatcher.test(half);
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
