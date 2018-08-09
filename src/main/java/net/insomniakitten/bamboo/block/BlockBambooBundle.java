package net.insomniakitten.bamboo.block;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
    public static final IProperty<Axis> PROP_AXIS = PropertyEnum.create("axis", Axis.class);
    public static final IProperty<Integer> PROP_DRIED = PropertyInteger.create("dried", 0, 3);

    public BlockBambooBundle() {
        super(Material.PLANTS);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setHarvestLevel("axe", 0);
        this.setTickRandomly(true);
    }

    public static boolean isDry(final IBlockState state) {
        return state.getValue(BlockBambooBundle.PROP_DRIED) == 3;
    }

    public static boolean isDry(final int meta) {
        return meta == 1;
    }

    @Override
    @Deprecated
    public Material getMaterial(final IBlockState state) {
        return BlockBambooBundle.isDry(state) ? Material.WOOD : Material.PLANTS;
    }

    @Override
    @Deprecated
    public MapColor getMapColor(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return BlockBambooBundle.isDry(state) ? MapColor.WOOD : MapColor.GREEN;
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState()
            .withProperty(BlockBambooBundle.PROP_AXIS, Axis.values()[meta & 3])
            .withProperty(BlockBambooBundle.PROP_DRIED, meta >> 2);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        val axis = state.getValue(BlockBambooBundle.PROP_AXIS).ordinal();
        val dried = state.getValue(BlockBambooBundle.PROP_DRIED) << 2;
        return axis | dried;
    }

    @Override
    @Deprecated
    public IBlockState withRotation(final IBlockState state, final Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            switch (state.getValue(BlockBambooBundle.PROP_AXIS)) {
                case X: return state.withProperty(BlockBambooBundle.PROP_AXIS, Axis.Z);
                case Y: break;
                case Z: return state.withProperty(BlockBambooBundle.PROP_AXIS, Axis.X);
            }
        }
        return state;
    }

    @Override
    public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
            return;
        }
        if (world.isRemote || !world.isDaytime()) {
            return;
        }
        if (!BlockBambooBundle.isDry(state) && world.canBlockSeeSky(pos.up())) {
            world.setBlockState(pos, state.cycleProperty(BlockBambooBundle.PROP_DRIED));
        }
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        super.onFallenUpon(world, pos, entity, fallDistance * 0.5F);
    }

    @Override
    public void onLanded(World world, Entity entity) {
        if (entity.isSneaking()) {
            super.onLanded(world, entity);
        } else if (entity.motionY < 0.0) {
            entity.motionY = -entity.motionY * 0.66;
            if (!(entity instanceof EntityLivingBase)) {
                entity.motionY *= 0.8;
            }
        }
    }

    @Override
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        items.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 0));
        items.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockBambooBundle.PROP_AXIS, BlockBambooBundle.PROP_DRIED);
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess access, final BlockPos pos, final IBlockState state, final int fortune) {
        drops.add(new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, BlockBambooBundle.isDry(state) ? 1 : 0));
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, BlockBambooBundle.isDry(state) ? 1 : 0);
    }

    @Override // TODO More accurate rotation based on given 'axis'?
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        return world.setBlockState(pos, world.getBlockState(pos).cycleProperty(BlockBambooBundle.PROP_AXIS));
    }

    @Override
    public SoundType getSoundType(final IBlockState state, final World world, final BlockPos pos, @Nullable final Entity entity) {
        return BlockBambooBundle.isDry(state) ? SoundType.WOOD : SoundType.PLANT;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        return this.getDefaultState()
            .withProperty(BlockBambooBundle.PROP_AXIS, side.getAxis())
            .withProperty(BlockBambooBundle.PROP_DRIED, BlockBambooBundle.isDry(meta) ? 3 : 0);
    }
}
