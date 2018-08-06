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
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import scala.Int;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class BlockBamboo extends Block implements IPlantable {
    public static final IProperty<Integer> PROP_AGE = PropertyInteger.create("age", 0, 15);
    public static final IProperty<Integer> PROP_LEAVES = PropertyInteger.create("leaves", 0, 3);

    public static final ImmutableMap<EnumFacing, PropertyBool> PROP_SIDES = ImmutableMap.of(
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
        this.setHardness(0.2F);
        this.setResistance(1.0F);
        this.setSoundType(SoundType.PLANT);
        this.setHarvestLevel("shears", 0);
        this.setTickRandomly(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    static void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
        if (event.getTarget() == null) {
            return;
        }
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        val pos = event.getTarget().getBlockPos();
        val player = event.getPlayer();
        val world = player.world;
        val state = world.getBlockState(pos);

        if (state.getBlock() != BamboozledBlocks.BAMBOO) {
            return;
        }

        val actual = state.getActualState(world, pos);
        val up = actual.getValue(BlockBamboo.PROP_SIDES.get(EnumFacing.UP));
        val boxes = Lists.newArrayList(up ? BlockBamboo.AABB_NORMAL : BlockBamboo.AABB_NORMAL_TOP);

        for (val side : BlockBamboo.AABB_SIDE.keySet()) {
            if (actual.getValue(BlockBamboo.PROP_SIDES.get(side))) {
                boxes.add(BlockBamboo.AABB_SIDE.get(side));
            }
        }

        BoundingBoxes.renderAt(boxes, player, pos, event.getPartialTicks());

        event.setCanceled(true);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockBamboo.PROP_AGE, meta);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockBamboo.PROP_AGE);
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        var actualState = state;
        actualState = this.getLeavesForPos(actualState, pos);
        actualState = this.getConnectionsForPos(actualState, access, pos);
        return actualState;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> boxes, final Entity entity, final boolean isActualState) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            Block.addCollisionBoxToList(pos, entityBox, boxes, BlockBamboo.AABB_SIMPLE);
            return;
        }

        val actual = state.getActualState(world, pos);

        for (val box : actual.getValue(BlockBamboo.PROP_SIDES.get(EnumFacing.UP)) ? BlockBamboo.AABB_NORMAL : BlockBamboo.AABB_NORMAL_TOP) {
            Block.addCollisionBoxToList(pos, entityBox, boxes, box);
        }

        for (val side : BlockBamboo.AABB_SIDE.keySet()) {
            if (actual.getValue(BlockBamboo.PROP_SIDES.get(side))) {
                Block.addCollisionBoxToList(pos, entityBox, boxes, BlockBamboo.AABB_SIDE.get(side));
            }
        }
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
        this.checkForDrop(state, world, pos);

        if (world.isAirBlock(pos.up())) {
            var i = 1;

            while (world.getBlockState(pos.down(i)).getBlock() == this) {
                ++i;
            }

            if (i < 6 && ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
                val age = state.getValue(BlockBamboo.PROP_AGE);

                if (age == 15) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                    world.setBlockState(pos, state.withProperty(BlockBamboo.PROP_AGE, 0), 4);
                } else {
                    world.setBlockState(pos, state.withProperty(BlockBamboo.PROP_AGE, age + 1), 4);
                }

                ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
            }
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block block, final BlockPos fromPos) {
        this.checkForDrop(state, world, pos);
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos pos, final IBlockState state) {
        this.checkForDrop(state, world, pos);
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            return this.rayTrace(pos, start, end, BlockBamboo.AABB_SIMPLE);
        }

        val boxes = Lists.<AxisAlignedBB>newArrayList();
        val actual = state.getActualState(world, pos);

        if (actual.getValue(BlockBamboo.PROP_SIDES.get(EnumFacing.UP))) {
            boxes.addAll(BlockBamboo.AABB_NORMAL);
        } else {
            boxes.addAll(BlockBamboo.AABB_NORMAL_TOP);
        }

        for (val side : BlockBamboo.AABB_SIDE.keySet()) {
            if (actual.getValue(BlockBamboo.PROP_SIDES.get(side))) {
                boxes.add(BlockBamboo.AABB_SIDE.get(side));
            }
        }

        return BoundingBoxes.rayTrace(boxes, pos, start, end);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos pos) {
        val state = world.getBlockState(pos.down());

        if (state.getBlock() == this) {
            return true;
        }

        return state.getBlock().canSustainPlant(state, world, pos.down(), EnumFacing.UP, (IPlantable) Blocks.SAPLING);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new BlockStateContainer.Builder(this);
        builder.add(BlockBamboo.PROP_AGE, BlockBamboo.PROP_LEAVES);
        BlockBamboo.PROP_SIDES.values().forEach(builder::add);
        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        return side.getAxis().isVertical() && access.getBlockState(pos.offset(side)).getBlock() == this;
    }

    @Override
    public EnumPlantType getPlantType(final IBlockAccess access, final BlockPos pos) {
        return Bamboozled.TROPICAL_PLANT_TYPE;
    }

    @Override
    public IBlockState getPlant(final IBlockAccess world, final BlockPos pos) {
        return world.getBlockState(pos);
    }

    private IBlockState getConnectionsForPos(IBlockState state, final IBlockAccess access, final BlockPos pos) {
        for (val side : BlockBamboo.PROP_SIDES.keySet()) {
            val block = access.getBlockState(pos.offset(side)).getBlock();
            state = state.withProperty(BlockBamboo.PROP_SIDES.get(side), block == this);
        }
        return state;
    }

    private IBlockState getLeavesForPos(final IBlockState state, final BlockPos pos) {
        val rand = new Random(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()));
        return state.withProperty(BlockBamboo.PROP_LEAVES, rand.nextInt(4));
    }

    private void checkForDrop(final IBlockState state, final World world, final BlockPos pos) {
        if (!this.canPlaceBlockAt(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }
}
