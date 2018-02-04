package net.insomniakitten.bamboo.block;

import com.google.common.collect.Maps;
import net.insomniakitten.bamboo.client.BlockModelMapper;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.tile.TileBambooHopper;
import net.insomniakitten.bamboo.tile.TileEntitySupplier;
import net.insomniakitten.bamboo.util.RayTraceHelper;
import net.minecraft.block.Block;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class BlockBambooHopper extends BlockBase implements TileEntitySupplier, ItemBlockSupplier, BlockModelMapper {

    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyDirection CONNECT = PropertyDirection.create("connect", f -> f != EnumFacing.UP);

    private static final AxisAlignedBB AABB_UPPER = new AxisAlignedBB(0.0D, 0.625D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_UPPER_N = new AxisAlignedBB(0.0D, 0.625D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_UPPER_S = new AxisAlignedBB(0.0D, 0.625D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB AABB_UPPER_E = new AxisAlignedBB(0.0D, 0.625D, 0.0D, 0.125D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_UPPER_W = new AxisAlignedBB(0.875D, 0.625D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_PLATE = new AxisAlignedBB(0.125D, 0.625D, 0.125D, 0.875D, 0.6875D, 0.125D);
    private static final AxisAlignedBB AABB_LOWER = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.625D, 0.75D);

    private static final Map<EnumFacing, AxisAlignedBB> AABB_JOINTS = Maps.newEnumMap(EnumFacing.class);

    static {
        AABB_JOINTS.put(EnumFacing.DOWN, new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.25D, 0.625D));
        AABB_JOINTS.put(EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 0.5D, 0.25D));
        AABB_JOINTS.put(EnumFacing.SOUTH, new AxisAlignedBB(0.375D, 0.25D, 0.75D, 0.625D, 0.5D, 1.0D));
        AABB_JOINTS.put(EnumFacing.EAST, new AxisAlignedBB(0.75D, 0.25D, 0.375D, 1.0D, 0.5D, 0.625D));
        AABB_JOINTS.put(EnumFacing.WEST, new AxisAlignedBB(0.0D, 0.25D, 0.375D, 0.25D, 0.5D, 0.625D));
    }

    public BlockBambooHopper() {
        super(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F);
        setOpaqueBlock(false);
        setFullBlock(false);
    }

    private void updatePoweredState(World world, BlockPos pos, IBlockState state) {
        if (world.isBlockPowered(pos) != state.getValue(POWERED)) {
            world.setBlockState(pos, state.cycleProperty(POWERED), 4);
        }
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

    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(CONNECT, rot.rotate(state.getValue(CONNECT)));
    }

    @Override
    @Deprecated
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(CONNECT)));
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        updatePoweredState(world, pos, state);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        updatePoweredState(world, pos, state);
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
        Collections.addAll(boxes, AABB_UPPER_N, AABB_UPPER_E, AABB_UPPER_S, AABB_UPPER_W, AABB_PLATE, AABB_LOWER);
        boxes.add(AABB_JOINTS.get(state.getValue(CONNECT)));
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        int powered = state.getValue(POWERED) ? 1 : 0;
        int connect = state.getValue(CONNECT).ordinal();
        return powered | connect << 1;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        Collections.addAll(boxes, AABB_UPPER, AABB_LOWER);
        boxes.add(AABB_JOINTS.get(state.getValue(CONNECT)));
        return RayTraceHelper.rayTraceMultiAABB(boxes, pos, start, end);
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
