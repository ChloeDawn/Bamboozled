package net.insomniakitten.bamboo.block.base;

import lombok.val;
import net.minecraft.block.BlockSlab;
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

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BlockSlabBase extends BlockBase {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockSlabBase(Material material, MapColor mapColor, SoundType sound, float hardness, float resistance) {
        super(material, mapColor, sound, hardness, resistance);
        useNeighborBrightness = true;
        setLightOpacity(0);
    }

    public BlockSlabBase(Material material, SoundType sound, float hardness, float resistance) {
        super(material, sound, hardness, resistance);
        useNeighborBrightness = true;
        setLightOpacity(0);
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

    @Deprecated
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess access, BlockPos pos) {
        val light = access.getCombinedLight(pos, state.getLightValue(access, pos));
        if (light == 0) {
            pos = pos.down();
            state = access.getBlockState(pos);
            return access.getCombinedLight(pos, state.getLightValue(access, pos));
        }
        return light;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        val target = access.getBlockState(pos.offset(side));
        if (side.getAxis().isHorizontal()) {
            if (target.getBlock() instanceof BlockSlabBase && target.getPropertyKeys().contains(VARIANT)) {
                if (state.getValue(VARIANT) == target.getValue(VARIANT)) {
                    return false;
                }
            }
            if (target.getBlock() instanceof BlockSlab && target.getPropertyKeys().contains(BlockSlab.HALF)) {
                if (state.getValue(VARIANT).doesMatchHalf(target.getValue(BlockSlab.HALF))) {
                    return false;
                }
            }
        }
        return super.shouldSideBeRendered(state, access, pos, side);
    }

    @Override
    @Deprecated
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    @Deprecated
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getStateForPlacement(world, pos, side, hitX, hitY, hitZ, meta, placer, placer.getActiveHand());
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
        return !ForgeModContainer.disableStairSlabCulling && state.getValue(VARIANT).isSideSolid(side);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return isDouble(state) ? 2 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        switch (side) {
            case UP:
                return getLower();
            case DOWN:
                return getUpper();
            default:
                return hitY > 0.5D ? getUpper() : getLower();
        }
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess access, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(state.getValue(VARIANT).getBoundingBox());
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess access, IBlockState state, BlockPos pos, EnumFacing side) {
        return state.getValue(VARIANT).isSideSolid(side) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return isDouble(state);
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
