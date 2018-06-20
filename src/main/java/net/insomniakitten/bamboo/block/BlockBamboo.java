package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.util.BoundingBoxes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class BlockBamboo extends Block implements IPlantable {
    public static final PropertyInteger PROP_AGE = PropertyInteger.create("age", 0, 15);
    public static final PropertyBool PROP_CANOPY = PropertyBool.create("canopy");
    public static final PropertyInteger PROP_LEAVES = PropertyInteger.create("leaves", 0, 3);

    private static final ImmutableMap<EnumFacing, PropertyBool> PROP_SIDES = ImmutableMap.of(
            EnumFacing.UP, PropertyBool.create("up"),
            EnumFacing.NORTH, PropertyBool.create("north"),
            EnumFacing.SOUTH, PropertyBool.create("south"),
            EnumFacing.WEST, PropertyBool.create("west"),
            EnumFacing.EAST, PropertyBool.create("east")
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
            EnumFacing.NORTH, new AxisAlignedBB(0.75, 0.0, -0.0625, 0.9375, 1.0, 0.125),
            EnumFacing.SOUTH, new AxisAlignedBB(0.0625, 0.0, 0.875, 0.25, 1.0, 1.0625),
            EnumFacing.WEST, new AxisAlignedBB(-0.0625, 0.0, 0.375, 0.125, 1.0, 0.5625)
    );

    public BlockBamboo() {
        super(Material.WOOD, MapColor.GREEN);
        setHardness(0.2F);
        setResistance(1.0F);
        setSoundType(SoundType.PLANT);
        setHarvestLevel("shears", 0);
        setTickRandomly(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget() == null) return;
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) return;

        val pos = event.getTarget().getBlockPos();
        val player = event.getPlayer();
        val world = player.world;
        val state = world.getBlockState(pos);

        if (state.getBlock() != BamboozledBlocks.BAMBOO) return;

        val actual = state.getActualState(world, pos);
        val up = actual.getValue(PROP_SIDES.get(EnumFacing.UP));
        val boxes = Lists.newArrayList(up ? AABB_NORMAL : AABB_NORMAL_TOP);

        for (val side : AABB_SIDE.keySet()) {
            if (actual.getValue(PROP_SIDES.get(side))) {
                boxes.add(AABB_SIDE.get(side));
            }
        }

        BoundingBoxes.renderAt(boxes, player, pos, event.getPartialTicks());

        event.setCanceled(true);
    }

    private IBlockState getConnectionsForPos(IBlockState state, IBlockAccess access, BlockPos pos) {
        for (val side : PROP_SIDES.keySet()) {
            val block = access.getBlockState(pos.offset(side)).getBlock();
            state = state.withProperty(PROP_SIDES.get(side), block == this);
        }
        return state;
    }

    private IBlockState getLeavesForPos(IBlockState state, BlockPos pos) {
        val rand = new Random(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()));
        return state.withProperty(PROP_LEAVES, rand.nextInt(4));
    }

    private IBlockState getCanopyForPos(IBlockState state, IBlockAccess access, BlockPos pos) {
        if (state.getValue(PROP_SIDES.get(EnumFacing.UP))) {
            return state.withProperty(PROP_CANOPY, false);
        }

        val target = new MutableBlockPos(pos);
        var height = 0;

        do {
            target.move(EnumFacing.DOWN);
            ++height;
        } while (height < 6 && access.getBlockState(target).getBlock() == this);

        return state.withProperty(PROP_CANOPY, height > 5);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(PROP_AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROP_AGE);
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        state = getLeavesForPos(state, pos);
        state = getConnectionsForPos(state, access, pos);
        state = getCanopyForPos(state, access, pos);
        return state;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> boxes, Entity entity, boolean isActualState) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            addCollisionBoxToList(pos, entityBox, boxes, AABB_SIMPLE);
            return;
        }

        val actual = state.getActualState(world, pos);

        for (val box : actual.getValue(PROP_SIDES.get(EnumFacing.UP)) ? AABB_NORMAL : AABB_NORMAL_TOP) {
            addCollisionBoxToList(pos, entityBox, boxes, box);
        }

        for (val side : AABB_SIDE.keySet()) {
            if (actual.getValue(PROP_SIDES.get(side))) {
                addCollisionBoxToList(pos, entityBox, boxes, AABB_SIDE.get(side));
            }
        }
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        checkForDrop(state, world, pos);
        if (world.isAirBlock(pos.up())) {
            var i = 1;
            while (world.getBlockState(pos.down(i)).getBlock() == this) ++i;
            if (i < 6 && ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
                val age = state.getValue(PROP_AGE);
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
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            return rayTrace(pos, start, end, AABB_SIMPLE);
        }

        val boxes = Lists.<AxisAlignedBB>newArrayList();
        val actual = state.getActualState(world, pos);

        if (actual.getValue(PROP_SIDES.get(EnumFacing.UP))) {
            boxes.addAll(AABB_NORMAL);
        } else boxes.addAll(AABB_NORMAL_TOP);

        for (val side : AABB_SIDE.keySet()) {
            if (actual.getValue(PROP_SIDES.get(side))) {
                boxes.add(AABB_SIDE.get(side));
            }
        }

        return BoundingBoxes.rayTrace(boxes, pos, start, end);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        val state = world.getBlockState(pos.down());
        return state.getBlock() == this || state.getBlock().canSustainPlant(
                state, world, pos.down(), EnumFacing.UP, (BlockSapling) Blocks.SAPLING
        );
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new Builder(this);
        builder.add(PROP_AGE, PROP_CANOPY, PROP_LEAVES);
        PROP_SIDES.values().forEach(builder::add);
        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        return side.getAxis().isVertical() && access.getBlockState(pos.offset(side)).getBlock() == this;
    }

    private void checkForDrop(IBlockState state, World world, BlockPos pos) {
        if (!canPlaceBlockAt(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess access, BlockPos pos) {
        return Bamboozled.TROPICAL_PLANT_TYPE;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }
}
