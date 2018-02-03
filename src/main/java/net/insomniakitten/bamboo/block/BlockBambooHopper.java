package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.client.BlockModelMapper;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.tile.TileBambooHopper;
import net.insomniakitten.bamboo.tile.TileEntitySupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public final class BlockBambooHopper extends BlockBase implements TileEntitySupplier, ItemBlockSupplier, BlockModelMapper {

    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyDirection CONNECT = PropertyDirection.create("connect", f -> f != EnumFacing.UP);

    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockBambooHopper() {
        super(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F);
        setOpaqueBlock(false);
        setFullBlock(false);
    }

    @Override
    @Deprecated
    public final IBlockState getStateFromMeta(int meta) {
        boolean powered = (meta & 1) == 1;
        EnumFacing connect = EnumFacing.VALUES[meta >> 1];
        return getDefaultState()
                .withProperty(POWERED, powered)
                .withProperty(CONNECT, connect);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECT, POWERED);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBambooHopper();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(CONNECT, placer.isSneaking() && side != EnumFacing.DOWN ? side.getOpposite() : EnumFacing.DOWN);
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess world, BlockPos pos, List<AxisAlignedBB> boxes) {
        Collections.addAll(boxes, BASE_AABB, NORTH_AABB, SOUTH_AABB, EAST_AABB, WEST_AABB);
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        int powered = state.getValue(POWERED) ? 1 : 0;
        int connect = state.getValue(CONNECT).ordinal();
        return powered | connect << 1;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }

    @Override
    public Class<? extends TileEntity> getTileClass() {
        return TileBambooHopper.class;
    }

    @Override
    public String getTileKey() {
        //noinspection ConstantConditions
        return getRegistryName().toString();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockBase(this, "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IStateMapper getModelMapper() {
        return new StateMap.Builder().ignore(POWERED).build();
    }

}
