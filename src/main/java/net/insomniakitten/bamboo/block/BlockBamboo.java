package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledConfig;
import net.insomniakitten.bamboo.client.BlockModelMapper;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

public final class BlockBamboo extends BlockBase implements IPlantable, BlockModelMapper, ItemBlockSupplier, OreEntrySupplier {

    public static final PropertyInteger PROP_AGE = PropertyInteger.create("age", 0, 15);

    private static final ImmutableMap<EnumFacing, PropertyBool> PROPS_SIDES = ImmutableMap.of(
            UP, PropertyBool.create("up"),
            NORTH, PropertyBool.create("north"),
            SOUTH, PropertyBool.create("south"),
            WEST, PropertyBool.create("west"),
            EAST, PropertyBool.create("east")
    );

    private static final AxisAlignedBB AABB_SIMPLE = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);

    private static final ImmutableList<AxisAlignedBB> AABB_NORMAL = ImmutableList.of(
            new AxisAlignedBB(0.125, 0.0, 0.125, 0.3125, 1.0, 0.3125),
            new AxisAlignedBB(0.6875, 0.0, 0.25, 0.875, 1.0, 0.4375),
            new AxisAlignedBB(0.125, 0.0, 0.625, 0.3125, 1.0, 0.8125),
            new AxisAlignedBB(0.6875, 0.0, 0.5625, 0.875, 1.0, 0.75),
            new AxisAlignedBB(0.4375, 0.0, 0.375, 0.625, 1.0, 0.5625),
            new AxisAlignedBB(0.375, 0.0, 0.6875, 0.5625, 1.0, 0.875),
            new AxisAlignedBB(0.4375, 0.0, 0.0625, 0.625, 1.0, 0.25),
            new AxisAlignedBB(0.1875, 0.0, 0.375, 0.375, 1.0, 0.5625)
    );

    private static final ImmutableList<AxisAlignedBB> AABB_NORMAL_TOP = ImmutableList.of(
            new AxisAlignedBB(0.125, 0.0, 0.125, 0.3125, 0.75, 0.3125),
            new AxisAlignedBB(0.6875, 0.0, 0.25, 0.875, 0.625, 0.4375),
            new AxisAlignedBB(0.125, 0.0, 0.625, 0.3125, 0.6875, 0.8125),
            new AxisAlignedBB(0.6875, 0.0, 0.5625, 0.875, 0.8125, 0.75),
            new AxisAlignedBB(0.4375, 0.0, 0.375, 0.625, 0.9375, 0.5625),
            new AxisAlignedBB(0.375, 0.0, 0.6875, 0.5625, 0.875, 0.875),
            new AxisAlignedBB(0.4375, 0.0, 0.0625, 0.625, 0.875, 0.25),
            new AxisAlignedBB(0.1875, 0.0, 0.375, 0.375, 0.8125, 0.5625)
    );

    private static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB_SIDE = ImmutableMap.of(
            NORTH, new AxisAlignedBB(0.75, 0.0, -0.0625, 0.9375, 1.0, 0.125),
            SOUTH, new AxisAlignedBB(0.0625, 0.0, 0.875, 0.25, 1.0, 1.0625),
            WEST, new AxisAlignedBB(-0.0625, 0.0, 0.375, 0.125, 1.0, 0.5625)
    );

    private static EnumPlantType plantTypeTropical;

    private final boolean advancedBamboo;

    public BlockBamboo() {
        super(Material.WOOD, MapColor.GREEN, SoundType.PLANT, 0.2F, 1.0F);
        advancedBamboo = BamboozledConfig.GENERAL.fancyBamboo;
        setSoundType(SoundType.PLANT);
        setHarvestLevel("shears", 0);
        setFullBlock(false);
        setOpaqueBlock(false);
        setTickRandomly(true);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(PROP_AGE, meta);
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        for (EnumFacing side : PROPS_SIDES.keySet()) {
            Block block = world.getBlockState(pos.offset(side)).getBlock();
            state = state.withProperty(PROPS_SIDES.get(side), block == this);
        }
        return state;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        boolean renderUp = world.getBlockState(pos.up()).getBlock() != this && side == EnumFacing.UP;
        boolean renderDown = world.getBlockState(pos.down()).getBlock() != this && side == EnumFacing.DOWN;
        return (renderUp || renderDown) && super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        checkForDrop(state, world, pos);
        if (world.isAirBlock(pos.up())) {
            int i = 1;
            while (world.getBlockState(pos.down(i)).getBlock() == this) ++i;
            if (i < 6 && ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
                int age = state.getValue(PROP_AGE);
                if (age == 15) {
                    world.setBlockState(pos.up(), getDefaultState());
                    world.setBlockState(pos, state.withProperty(PROP_AGE, 0), 4);
                } else world.setBlockState(pos, state.withProperty(PROP_AGE, age + 1), 4);
                ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
            }
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        checkForDrop(state, world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        checkForDrop(state, world, pos);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
        boolean canSustainPlant = state.getBlock().canSustainPlant(state, world, pos.down(), UP, (BlockSapling) Blocks.SAPLING);
        return state.getBlock() == this || canSustainPlant;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        builder.add(PROP_AGE);
        for (IProperty<?> property : PROPS_SIDES.values()) {
            builder.add(property);
        }
        return builder.build();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockBase(this, "inventory");
    }

    @Override
    public void getCollisionBoxes(IBlockState state, IBlockAccess world, BlockPos pos, List<AxisAlignedBB> boxes) {
        if (!advancedBamboo) {
            boxes.add(AABB_SIMPLE);
            return;
        }

        state = state.getActualState(world, pos);

        if (state.getValue(PROPS_SIDES.get(UP))) {
            boxes.addAll(AABB_NORMAL);
        } else boxes.addAll(AABB_NORMAL_TOP);

        for (EnumFacing side : AABB_SIDE.keySet()) {
            if (state.getValue(PROPS_SIDES.get(side))) {
                boxes.add(AABB_SIDE.get(side));
            }
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROP_AGE);
    }

    private void checkForDrop(IBlockState state, World world, BlockPos pos) {
        if (!canPlaceBlockAt(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        if (plantTypeTropical == null) {
            String name = "Tropical";
            Bamboozled.LOGGER.debug("Registering new PlantType \"{}\"", name);
            plantTypeTropical = EnumPlantType.getPlantType(name);
        }
        return plantTypeTropical;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IStateMapper getModelMapper() {
        return new StateMap.Builder().ignore(PROP_AGE).build();
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this), "bamboo");
    }

}
