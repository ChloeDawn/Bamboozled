package net.insomniakitten.bamboo.block.base;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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

import java.util.Locale;
import java.util.Random;

public class BlockSlab extends Block {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockSlab(final Material material, final MapColor mapColor, final SoundType sound, final float hardness, final float resistance) {
        super(material, mapColor);
        this.setSoundType(sound);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.useNeighborBrightness = true;
        this.setLightOpacity(0);
    }

    public BlockSlab(final Material material, final SoundType sound, final float hardness, final float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    public IBlockState getLower() {
        return this.getDefaultState().withProperty(BlockSlab.VARIANT, Variant.LOWER);
    }

    public IBlockState getUpper() {
        return this.getDefaultState().withProperty(BlockSlab.VARIANT, Variant.UPPER);
    }

    public IBlockState getDouble() {
        return this.getDefaultState().withProperty(BlockSlab.VARIANT, Variant.DOUBLE);
    }

    public final boolean isLower(final IBlockState state) {
        return state.getValue(BlockSlab.VARIANT).isLower();
    }

    public final boolean isUpper(final IBlockState state) {
        return state.getValue(BlockSlab.VARIANT).isUpper();
    }

    public final boolean isDouble(final IBlockState state) {
        return state.getValue(BlockSlab.VARIANT).isDouble();
    }

    @Override
    @Deprecated
    public boolean isTopSolid(final IBlockState state) {
        return this.isDouble(state) || this.isUpper(state);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockSlab.VARIANT, Variant.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockSlab.VARIANT).ordinal();
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return this.isDouble(state);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return state.getValue(BlockSlab.VARIANT).getBoundingBox();
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        val light = access.getCombinedLight(pos, state.getLightValue(access, pos));
        if (light == 0) {
            val below = pos.down();
            val other = access.getBlockState(below);
            return access.getCombinedLight(below, other.getLightValue(access, below));
        }
        return light;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos pos, final EnumFacing side) {
        return state.getValue(BlockSlab.VARIANT).isSideSolid(side) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return this.isDouble(state);
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
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return false;
        }

        if (side.getAxis().isVertical()) {
            return state.getValue(BlockSlab.VARIANT).isSideSolid(side);
        }

        val target = access.getBlockState(pos.offset(side));
        val block = target.getBlock();
        val variant = state.getValue(BlockSlab.VARIANT);

        if (block instanceof BlockSlab) {
            if (variant == target.getValue(BlockSlab.VARIANT)) {
                return true;
            }
        }

        if (block instanceof net.minecraft.block.BlockSlab) {
            return variant.doesMatchHalf(target.getValue(net.minecraft.block.BlockSlab.HALF));
        }

        return false;
    }

    @Override
    public int quantityDropped(final IBlockState state, final int fortune, final Random random) {
        return this.isDouble(state) ? 2 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        return side == EnumFacing.UP || hitY <= 0.5D ? this.getLower() : this.getUpper();
    }

    public enum Variant implements IStringSerializable {
        DOUBLE(Block.FULL_BLOCK_AABB, null, null),
        LOWER(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), EnumFacing.DOWN, EnumBlockHalf.BOTTOM),
        UPPER(new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D), EnumFacing.UP, EnumBlockHalf.TOP);

        public static final Variant[] VALUES = Variant.values();

        private final AxisAlignedBB boundingBox;
        private final EnumFacing side;
        private final EnumBlockHalf half;

        Variant(final AxisAlignedBB boundingBox, final EnumFacing side, final EnumBlockHalf half) {
            this.boundingBox = boundingBox;
            this.side = side;
            this.half = half;
        }

        public boolean isLower() {
            return this == Variant.LOWER;
        }

        public boolean isUpper() {
            return this == Variant.UPPER;
        }

        public boolean isDouble() {
            return this == Variant.DOUBLE;
        }

        public AxisAlignedBB getBoundingBox() {
            return this.boundingBox;
        }

        public boolean isSideSolid(final EnumFacing side) {
            return this.side == null || this.side == side;
        }

        public boolean doesMatchHalf(final EnumBlockHalf half) {
            return this.half == half;
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
