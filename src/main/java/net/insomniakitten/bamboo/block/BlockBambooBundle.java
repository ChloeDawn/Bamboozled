package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
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

public final class BlockBambooBundle extends Block {
    public static final PropertyEnum<Axis> PROP_AXIS = PropertyEnum.create("axis", Axis.class);
    public static final PropertyInteger PROP_DRIED = PropertyInteger.create("dried", 0, 3);

    public BlockBambooBundle() {
        super(Material.PLANTS);
        setSoundType(SoundType.WOOD);
        setHardness(1.0F);
        setResistance(5.0F);
        setHarvestLevel("axe", 0);
        setTickRandomly(true);
    }

    public static boolean isDry(IBlockState state) {
        return state.getValue(PROP_DRIED) == 3;
    }

    public static boolean isDry(int meta) {
        return meta == 1;
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
        return getDefaultState().withProperty(PROP_AXIS, Axis.values()[meta & 3]).withProperty(PROP_DRIED, meta >> 2);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROP_AXIS).ordinal() | state.getValue(PROP_DRIED) << 2;
    }

    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            switch (state.getValue(PROP_AXIS)) {
                case X: return state.withProperty(PROP_AXIS, Axis.Z);
                case Z: return state.withProperty(PROP_AXIS, Axis.X);
            }
        }
        return state;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) return;
        if (world.isRemote || !world.isDaytime()) return;
        if (!isDry(state) && world.canBlockSeeSky(pos.up())) {
            world.setBlockState(pos, state.cycleProperty(PROP_DRIED));
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 0));
        items.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROP_AXIS, PROP_DRIED);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, isDry(state) ? 1 : 0));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, isDry(state) ? 1 : 0);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return isDry(state) ? SoundType.WOOD : SoundType.PLANT;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(PROP_AXIS, side.getAxis()).withProperty(PROP_DRIED, isDry(meta) ? 3 : 0);
    }
}
