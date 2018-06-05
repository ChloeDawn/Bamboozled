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

    public BlockSlab(Material material, MapColor mapColor, SoundType sound, float hardness, float resistance) {
        super(material, mapColor);
        setSoundType(sound);
        setHardness(hardness);
        setResistance(resistance);
        useNeighborBrightness = true;
        setLightOpacity(0);
    }

    public BlockSlab(Material material, SoundType sound, float hardness, float resistance) {
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
        return state.getValue(VARIANT).isLower();
    }

    public final boolean isUpper(IBlockState state) {
        return state.getValue(VARIANT).isUpper();
    }

    public final boolean isDouble(IBlockState state) {
        return state.getValue(VARIANT).isDouble();
    }

    @Override
    @Deprecated
    public boolean isTopSolid(IBlockState state) {
        return isDouble(state) || isUpper(state);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, Variant.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(VARIANT).getBoundingBox();
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess access, BlockPos pos) {
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing side) {
        return state.getValue(VARIANT).isSideSolid(side) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    @Deprecated
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        if (ForgeModContainer.disableStairSlabCulling) return false;

        if (side.getAxis().isVertical()) {
            return state.getValue(VARIANT).isSideSolid(side);
        }

        val target = access.getBlockState(pos.offset(side));
        val block = target.getBlock();
        val variant = state.getValue(VARIANT);

        return block instanceof BlockSlab && variant == target.getValue(VARIANT)
                || block instanceof net.minecraft.block.BlockSlab && variant.doesMatchHalf(target.getValue(net.minecraft.block.BlockSlab.HALF));
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return isDouble(state) ? 2 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return side == EnumFacing.UP || hitY <= 0.5D ? getLower() : getUpper();
    }

    public enum Variant implements IStringSerializable {
        LOWER(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), EnumFacing.DOWN, EnumBlockHalf.BOTTOM),
        UPPER(new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D), EnumFacing.UP, EnumBlockHalf.TOP),
        DOUBLE(FULL_BLOCK_AABB, null, null);

        public static final Variant[] VALUES = values();

        private final AxisAlignedBB boundingBox;
        private final EnumFacing side;
        private final EnumBlockHalf half;

        Variant(AxisAlignedBB boundingBox, EnumFacing side, EnumBlockHalf half) {
            this.boundingBox = boundingBox;
            this.side = side;
            this.half = half;
        }

        public boolean isLower() {
            return this == LOWER;
        }

        public boolean isUpper() {
            return this == UPPER;
        }

        public boolean isDouble() {
            return this == DOUBLE;
        }

        public AxisAlignedBB getBoundingBox() {
            return boundingBox;
        }

        public boolean isSideSolid(EnumFacing side) {
            return this.side == side;
        }

        public boolean doesMatchHalf(EnumBlockHalf half) {
            return this.half == half;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
