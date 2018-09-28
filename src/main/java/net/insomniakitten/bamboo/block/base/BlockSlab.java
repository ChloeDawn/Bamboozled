package net.insomniakitten.bamboo.block.base;

import com.google.common.base.MoreObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

public class BlockSlab extends Block {
    private static final IProperty<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockSlab(final Material material, final MapColor mapColor, final SoundType sound, final float hardness, final float resistance) {
        super(material, mapColor);
        this.setSoundType(sound);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
    }

    public BlockSlab(final Material material, final SoundType sound, final float hardness, final float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    public final IBlockState getLowerSlab() {
        final IBlockState state = this.getDefaultState();

        return state.withProperty(BlockSlab.VARIANT, Variant.LOWER);
    }

    public final IBlockState getUpperSlab() {
        final IBlockState state = this.getDefaultState();

        return state.withProperty(BlockSlab.VARIANT, Variant.UPPER);
    }

    public final IBlockState getDoubleSlab() {
        final IBlockState state = this.getDefaultState();

        return state.withProperty(BlockSlab.VARIANT, Variant.DOUBLE);
    }

    public final boolean isLowerSlab(final IBlockState state) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.isLower();
    }

    public final boolean isUpperSlab(final IBlockState state) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.isUpper();
    }

    public final boolean isDoubleSlab(final IBlockState state) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.isDouble();
    }

    @Override
    @Deprecated
    public boolean isTopSolid(final IBlockState state) {
        return this.isDoubleSlab(state) || this.isUpperSlab(state);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        final IBlockState state = this.getDefaultState();
        final Variant variant = Variant.byOrdinal(meta);

        return state.withProperty(BlockSlab.VARIANT, variant);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.ordinal();
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return this.isDoubleSlab(state);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.getBoundingBox();
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        final int lightValue = state.getLightValue(access, position);
        final int combinedLight = access.getCombinedLight(position, lightValue);

        if (combinedLight != 0) {
            return combinedLight;
        }

        final BlockPos below = position.down();
        final IBlockState other = access.getBlockState(below);
        final int otherLightValue = other.getLightValue(access, below);

        return access.getCombinedLight(below, otherLightValue);
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos position, final EnumFacing face) {
        final Variant variant = state.getValue(BlockSlab.VARIANT);

        return variant.isFaceSolid(face) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return this.isDoubleSlab(state);
    }

    @Override
    @Deprecated
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSlab.VARIANT);
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return false;
        }

        if (face.getAxis().isVertical()) {
            return state.getValue(BlockSlab.VARIANT).isFaceSolid(face);
        }

        final BlockPos offset = position.offset(face);
        final IBlockState other = access.getBlockState(offset);
        final Block block = other.getBlock();

        if (block instanceof BlockSlab) {
            final Variant variant = state.getValue(BlockSlab.VARIANT);

            if (variant == other.getValue(BlockSlab.VARIANT)) {
                return true;
            }
        }

        if (block instanceof net.minecraft.block.BlockSlab) {
            final Variant variant = state.getValue(BlockSlab.VARIANT);
            final EnumBlockHalf half = other.getValue(net.minecraft.block.BlockSlab.HALF);

            return variant.isEquivalentTo(half);
        }

        return false;
    }

    @Override
    public int quantityDropped(final IBlockState state, final int fortune, final Random random) {
        return this.isDoubleSlab(state) ? 2 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos position, final EnumFacing face, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        return EnumFacing.UP == face || 0.5 >= hitY ? this.getLowerSlab() : this.getUpperSlab();
    }

    public enum Variant implements IStringSerializable {
        DOUBLE(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), null, null),
        LOWER(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), EnumBlockHalf.BOTTOM, EnumFacing.DOWN),
        UPPER(new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), EnumBlockHalf.TOP, EnumFacing.UP);

        private static final Variant[] BY_ORDINAL = Variant.values();

        private final AxisAlignedBB aabb;

        @Nullable
        private final EnumBlockHalf half;

        @Nullable
        private final EnumFacing face;

        private final String toString;

        Variant(final AxisAlignedBB aabb, @Nullable final EnumBlockHalf half, @Nullable final EnumFacing face) {
            this.aabb = aabb;
            this.face = face;
            this.half = half;
            this.toString = MoreObjects.toStringHelper(this.getName())
                .add("aabb", this.aabb.toString().replaceFirst("box", ""))
                .add("half", this.half != null ? this.half.getName() : "none")
                .add("face", this.face != null ? this.face.getName() : "all")
                .toString();
        }

        public static Variant byOrdinal(final int ordinal) {
            return Variant.BY_ORDINAL[ordinal % Variant.BY_ORDINAL.length];
        }

        public final boolean isLower() {
            return Variant.LOWER == this;
        }

        public final boolean isUpper() {
            return Variant.UPPER == this;
        }

        public final boolean isDouble() {
            return Variant.DOUBLE == this;
        }

        public final AxisAlignedBB getBoundingBox() {
            return this.aabb;
        }

        public final boolean isEquivalentTo(final EnumBlockHalf half) {
            return this.half == half;
        }

        public final boolean isFaceSolid(final EnumFacing face) {
            return this.face == null || this.face == face;
        }

        @Override
        public final String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public final String toString() {
            return this.toString;
        }
    }
}
