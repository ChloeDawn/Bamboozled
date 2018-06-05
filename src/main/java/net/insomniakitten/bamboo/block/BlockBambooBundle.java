package net.insomniakitten.bamboo.block;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.base.BlockBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public final class BlockBambooBundle extends BlockBase {
    private static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);
    private static final PropertyInteger DRIED = PropertyInteger.create("dried", 0, 3);

    public BlockBambooBundle() {
        super(Material.PLANTS, SoundType.WOOD, 1.0F, 5.0F);
        setHarvestLevel("axe", 0);
        setTickRandomly(true);
    }

    private static boolean isDry(IBlockState state) {
        return state.getValue(DRIED) == 3;
    }

    public static boolean isDry(int meta) {
        return meta == 1;
    }

    public static int getDryProgress(IBlockState state) {
        return state.getValue(DRIED);
    }

    @Override
    @Deprecated
    public Material getMaterial(IBlockState state) {
        return isDry(state) ? Material.WOOD : Material.PLANTS;
    }

    @Override
    @Deprecated
    public MapColor getMapColor(IBlockState state, IBlockAccess access, BlockPos pos) {
        return isDry(state) ? MapColor.WOOD : MapColor.GREEN;
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, Axis.values()[meta & 3]).withProperty(DRIED, meta >> 2);
    }

    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X: return state.withProperty(AXIS, Axis.Z);
                    case Z: return state.withProperty(AXIS, Axis.X);
                    default: return state;
                }
            default: return state;
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (Bamboozled.getConfig().isInWorldBambooDryingEnabled()
                && !world.isRemote
                && !isDry(state)
                && world.isDaytime()
                && world.canBlockSeeSky(pos.up())) {
            world.setBlockState(pos, state.cycleProperty(DRIED));
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, DRIED);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, isDry(state) ? 1 : 0));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, isDry(state) ? 1 : 0);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return isDry(state) ? SoundType.WOOD : SoundType.PLANT;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(AXIS, side.getAxis()).withProperty(DRIED, isDry(meta) ? 3 : 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        val axis = state.getValue(AXIS).ordinal();
        val dried = state.getValue(DRIED) << 2;
        return axis | dried;
    }
}
