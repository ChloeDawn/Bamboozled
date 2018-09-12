package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public final class BlockBamboo extends Block implements IPlantable {
    public static final IProperty<Integer> AGE = PropertyInteger.create("age", 0, 15);
    public static final IProperty<Integer> LEAVES = PropertyInteger.create("leaves", 0, 3);

    public static final ImmutableMap<EnumFacing, IProperty<Boolean>> SIDES = Stream.of(EnumFacing.values())
        .filter(facing -> facing.getAxis().isHorizontal())
        .collect(Maps.toImmutableEnumMap(Function.identity(), it -> PropertyBool.create(it.getName())));

    public static final AxisAlignedBB SIMPLE_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);

    public static final ImmutableList<AxisAlignedBB> FANCY_AABB = ImmutableList.of(
        new AxisAlignedBB(0.125, 0.0, 0.125, 0.3125, 1.0, 0.3125),
        new AxisAlignedBB(0.6875, 0.0, 0.25, 0.875, 1.0, 0.4375),
        new AxisAlignedBB(0.125, 0.0, 0.625, 0.3125, 1.0, 0.8125),
        new AxisAlignedBB(0.6875, 0.0, 0.5625, 0.875, 1.0, 0.75),
        new AxisAlignedBB(0.4375, 0.0, 0.375, 0.625, 1.0, 0.5625),
        new AxisAlignedBB(0.375, 0.0, 0.6875, 0.5625, 1.0, 0.875),
        new AxisAlignedBB(0.4375, 0.0, 0.0625, 0.625, 1.0, 0.25),
        new AxisAlignedBB(0.1875, 0.0, 0.375, 0.375, 1.0, 0.5625)
    );

    public static final ImmutableList<AxisAlignedBB> FANCY_AABB_TOP = ImmutableList.of(
        new AxisAlignedBB(0.125, 0.0, 0.125, 0.3125, 0.75, 0.3125),
        new AxisAlignedBB(0.6875, 0.0, 0.25, 0.875, 0.625, 0.4375),
        new AxisAlignedBB(0.125, 0.0, 0.625, 0.3125, 0.6875, 0.8125),
        new AxisAlignedBB(0.6875, 0.0, 0.5625, 0.875, 0.8125, 0.75),
        new AxisAlignedBB(0.4375, 0.0, 0.375, 0.625, 0.9375, 0.5625),
        new AxisAlignedBB(0.375, 0.0, 0.6875, 0.5625, 0.875, 0.875),
        new AxisAlignedBB(0.4375, 0.0, 0.0625, 0.625, 0.875, 0.25),
        new AxisAlignedBB(0.1875, 0.0, 0.375, 0.375, 0.8125, 0.5625)
    );

    public static final ImmutableMap<EnumFacing, AxisAlignedBB> FANCY_AABB_SIDE = Maps.immutableEnumMap(
        ImmutableMap.of(
            EnumFacing.NORTH, new AxisAlignedBB(0.75, 0.0, -0.0625, 0.9375, 1.0, 0.125),
            EnumFacing.SOUTH, new AxisAlignedBB(0.0625, 0.0, 0.875, 0.25, 1.0, 1.0625),
            EnumFacing.WEST, new AxisAlignedBB(-0.0625, 0.0, 0.375, 0.125, 1.0, 0.5625)
        )
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
        @Nullable val hit = event.getTarget();

        if (hit == null || RayTraceResult.Type.BLOCK != hit.typeOfHit) {
            return;
        }

        val position = hit.getBlockPos();
        val player = event.getPlayer();
        val world = player.world;
        val state = world.getBlockState(position);

        if (BamboozledBlocks.BAMBOO != state.getBlock()) {
            return;
        }

        val actualState = state.getActualState(world, position);
        val up = actualState.getValue(BlockBamboo.SIDES.get(EnumFacing.UP));
        val boxes = Lists.newArrayList(up ? BlockBamboo.FANCY_AABB : BlockBamboo.FANCY_AABB_TOP);

        for (val facing : BlockBamboo.FANCY_AABB_SIDE.keySet()) {
            val property = BlockBamboo.SIDES.get(facing);

            if (actualState.getValue(property)) {
                boxes.add(BlockBamboo.FANCY_AABB_SIDE.get(facing));
            }
        }

        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
            SourceFactor.ONE, DestFactor.ZERO
        );
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        val partialTicks = event.getPartialTicks();
        val x = position.getX() - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
        val y = position.getY() - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
        val z = position.getZ() - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);

        for (val box : boxes) {
            RenderGlobal.drawSelectionBoundingBox(box.grow(0.002D).offset(x, y, z), 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

        event.setCanceled(true);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        val state = this.getDefaultState();
        val age = meta % 16;

        return state.withProperty(BlockBamboo.AGE, age);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockBamboo.AGE);
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        var actualState = state;

        actualState = this.getLeavesForPosition(actualState, position);
        actualState = this.getSidesForPosition(actualState, access, position);

        return actualState;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos position, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos position, final AxisAlignedBB entityBox, final List<AxisAlignedBB> boxes, final Entity entity, final boolean isActualState) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            Block.addCollisionBoxToList(position, entityBox, boxes, BlockBamboo.SIMPLE_AABB);

            return;
        }

        val actualState = state.getActualState(world, position);
        val up = BlockBamboo.SIDES.get(EnumFacing.UP);
        val isUp = actualState.getValue(up);

        for (val box : isUp ? BlockBamboo.FANCY_AABB : BlockBamboo.FANCY_AABB_TOP) {
            Block.addCollisionBoxToList(position, entityBox, boxes, box);
        }

        for (val facing : BlockBamboo.FANCY_AABB_SIDE.keySet()) {
            val property = BlockBamboo.SIDES.get(facing);

            if (actualState.getValue(property)) {
                val box = BlockBamboo.FANCY_AABB_SIDE.get(facing);

                Block.addCollisionBoxToList(position, entityBox, boxes, box);
            }
        }
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    public void updateTick(final World world, final BlockPos position, final IBlockState state, final Random rand) {
        if (!world.isRemote && !this.canPlaceBlockAt(world, position)) {
            world.destroyBlock(position, true);
        }

        val above = position.up();

        if (!world.isAirBlock(above)) {
            return;
        }

        var i = 1;

        while (this == world.getBlockState(position.down(i)).getBlock()) {
            ++i;
        }

        if (6 > i && ForgeHooks.onCropsGrowPre(world, position, state, true)) {
            val age = state.getValue(BlockBamboo.AGE);

            if (15 == age) {
                world.setBlockState(above, this.getDefaultState());
                world.setBlockState(position, state.withProperty(BlockBamboo.AGE, 0), 4);
            } else {
                world.setBlockState(position, state.withProperty(BlockBamboo.AGE, age + 1), 4);
            }

            ForgeHooks.onCropsGrowPost(world, position, state, world.getBlockState(position));
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(final IBlockState state, final World world, final BlockPos position, final Block neighbor, final BlockPos offset) {
        if (!world.isRemote && !this.canPlaceBlockAt(world, position)) {
            world.destroyBlock(position, true);
        }
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos position, final IBlockState state) {
        if (!world.isRemote && !this.canPlaceBlockAt(world, position)) {
            world.destroyBlock(position, true);
        }
    }

    @Override
    @Deprecated
    @Nullable
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos position, final Vec3d start, final Vec3d end) {
        if (!Bamboozled.getConfig().isFancyBambooEnabled()) {
            return this.rayTrace(position, start, end, BlockBamboo.SIMPLE_AABB);
        }

        val actualState = state.getActualState(world, position);
        val up = actualState.getValue(BlockBamboo.SIDES.get(EnumFacing.UP));
        val boxes = Lists.newArrayList(up ? BlockBamboo.FANCY_AABB : BlockBamboo.FANCY_AABB_TOP);

        for (val facing : BlockBamboo.FANCY_AABB_SIDE.keySet()) {
            val property = BlockBamboo.SIDES.get(facing);

            if (actualState.getValue(property)) {
                boxes.add(BlockBamboo.FANCY_AABB_SIDE.get(facing));
            }
        }

        val x = position.getX();
        val y = position.getY();
        val z = position.getZ();
        val hits = Lists.<RayTraceResult>newArrayList();
        val a = start.subtract(x, y, z);
        val b = end.subtract(x, y, z);

        for (val box : boxes) {
            @Nullable val hit = box.calculateIntercept(a, b);

            if (hit != null) {
                val vec = hit.hitVec.add(x, y, z);

                hits.add(new RayTraceResult(vec, hit.sideHit, position));
            }
        }

        @Nullable RayTraceResult ret = null;
        var sqrDis = 0.0D;

        for (val hit : hits) {
            val newSqrDis = hit.hitVec.squareDistanceTo(end);

            if (newSqrDis > sqrDis) {
                ret = hit;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos position) {
        val below = position.down();
        val state = world.getBlockState(below);
        val block = state.getBlock();

        return this == block || block.canSustainPlant(state, world, below, EnumFacing.UP, (IPlantable) Blocks.SAPLING);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new BlockStateContainer.Builder(this);

        builder.add(BlockBamboo.AGE, BlockBamboo.LEAVES);
        BlockBamboo.SIDES.values().forEach(builder::add);

        return builder.build();
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        if (face.getAxis().isHorizontal()) {
            return false;
        }

        val offset = position.offset(face);
        val other = access.getBlockState(offset);

        return this == other.getBlock();
    }

    @Override
    public EnumPlantType getPlantType(final IBlockAccess access, final BlockPos position) {
        return Bamboozled.getTropicalPlantType();
    }

    @Override
    public IBlockState getPlant(final IBlockAccess access, final BlockPos position) {
        val state = access.getBlockState(position);

        if (this != state.getBlock()) {
            throw new IllegalStateException("Expected " + this.getRegistryName() + ", received " + state);
        }

        return state;
    }

    private IBlockState getSidesForPosition(IBlockState state, final IBlockAccess access, final BlockPos position) {
        for (val facing : BlockBamboo.SIDES.keySet()) {
            val offset = position.offset(facing);
            val other = access.getBlockState(offset);
            val block = other.getBlock();
            val property = BlockBamboo.SIDES.get(facing);

            state = state.withProperty(property, this == block);
        }

        return state;
    }

    private IBlockState getLeavesForPosition(final IBlockState state, final BlockPos position) {
        val x = position.getX();
        val y = position.getY();
        val z = position.getZ();
        val seed = MathHelper.getCoordinateRandom(x, y, z);

        return state.withProperty(BlockBamboo.LEAVES, new Random(seed).nextInt(4));
    }
}
