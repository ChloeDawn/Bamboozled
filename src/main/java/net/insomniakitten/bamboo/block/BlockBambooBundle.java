package net.insomniakitten.bamboo.block;

import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.util.LazyBlockItem;
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
import net.minecraft.item.ItemBlock;
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
import java.util.function.Supplier;

public final class BlockBambooBundle extends Block {
    public static final IProperty<Axis> AXIS = PropertyEnum.create("axis", Axis.class);
    public static final IProperty<Integer> DRIED = PropertyInteger.create("dried", 0, 3);

    private static final Axis[] AXIS_BY_ORDINAL = Axis.values();

    private final Supplier<ItemBlock> item = LazyBlockItem.of(this);

    public BlockBambooBundle() {
        super(Material.PLANTS);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setHarvestLevel("axe", 0);
        this.setTickRandomly(true);
    }

    public static boolean isDry(final IBlockState state) {
        return 3 == state.getValue(BlockBambooBundle.DRIED);
    }

    public static boolean isDry(final int meta) {
        return 1 == meta;
    }

    @Override
    @Deprecated
    public Material getMaterial(final IBlockState state) {
        return BlockBambooBundle.isDry(state) ? Material.WOOD : Material.PLANTS;
    }

    @Override
    @Deprecated
    public MapColor getMapColor(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        return BlockBambooBundle.isDry(state) ? MapColor.WOOD : MapColor.GREEN;
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        var state = this.getDefaultState();
        val axis = BlockBambooBundle.AXIS_BY_ORDINAL[meta & 3];
        val dried = meta >> 2;

        state = state.withProperty(BlockBambooBundle.AXIS, axis);
        state = state.withProperty(BlockBambooBundle.DRIED, dried);

        return state;
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        val axis = state.getValue(BlockBambooBundle.AXIS).ordinal();
        val dried = state.getValue(BlockBambooBundle.DRIED) << 2;

        return axis | dried;
    }

    @Override
    @Deprecated
    public IBlockState withRotation(final IBlockState state, final Rotation rotation) {
        if (Rotation.CLOCKWISE_90 == rotation || Rotation.COUNTERCLOCKWISE_90 == rotation) {
            switch (state.getValue(BlockBambooBundle.AXIS)) {
                case X: return state.withProperty(BlockBambooBundle.AXIS, Axis.Z);
                case Y: break;
                case Z: return state.withProperty(BlockBambooBundle.AXIS, Axis.X);
            }
        }

        return state;
    }

    @Override
    public void updateTick(final World world, final BlockPos position, final IBlockState state, final Random rand) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
            return;
        }

        if (world.isRemote || !world.isDaytime()) {
            return;
        }

        if (!BlockBambooBundle.isDry(state) && world.canBlockSeeSky(position.up())) {
            world.setBlockState(position, state.cycleProperty(BlockBambooBundle.DRIED));
        }
    }

    @Override
    public void onFallenUpon(final World world, final BlockPos position, final Entity entity, final float fallDistance) {
        super.onFallenUpon(world, position, entity, fallDistance * 0.5F);
    }

    @Override
    public void onLanded(final World world, final Entity entity) {
        if (entity.isSneaking()) {
            super.onLanded(world, entity);

            return;
        }

        if (entity.motionY < 0.0) {
            entity.motionY = -entity.motionY * 0.66;

            if (!(entity instanceof EntityLivingBase)) {
                entity.motionY *= 0.8;
            }
        }
    }

    @Override
    public void getSubBlocks(final CreativeTabs group, final NonNullList<ItemStack> items) {
        items.add(new ItemStack(this.item.get(), 1, 0));
        items.add(new ItemStack(this.item.get(), 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockBambooBundle.AXIS, BlockBambooBundle.DRIED);
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess access, final BlockPos position, final IBlockState state, final int fortune) {
        drops.add(new ItemStack(this.item.get(), 1, BlockBambooBundle.isDry(state) ? 1 : 0));
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult hit, final World world, final BlockPos position, final EntityPlayer player) {
        return new ItemStack(this.item.get(), 1, BlockBambooBundle.isDry(state) ? 1 : 0);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos position, final EnumFacing face) {
        val state = world.getBlockState(position);
        val block = state.getBlock();

        return this == block && world.setBlockState(position, state.cycleProperty(BlockBambooBundle.AXIS));
    }

    @Override
    public SoundType getSoundType(final IBlockState state, final World world, final BlockPos position, @Nullable final Entity entity) {
        return BlockBambooBundle.isDry(state) ? SoundType.WOOD : SoundType.PLANT;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos position, final EnumFacing face, final float x, final float y, final float z, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        val state = this.getDefaultState();
        val axis = face.getAxis();
        val dried = BlockBambooBundle.isDry(meta) ? 3 : 0;

        return state.withProperty(BlockBambooBundle.AXIS, axis).withProperty(BlockBambooBundle.DRIED, dried);
    }
}
