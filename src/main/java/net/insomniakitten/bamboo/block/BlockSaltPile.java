package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public final class BlockSaltPile extends Block {
    public static final ImmutableMap<EnumFacing, IProperty<Connection>> CONNECTIONS = Stream.of(EnumFacing.values())
        .filter(facing -> facing.getAxis().isHorizontal())
        .collect(ImmutableMap.toImmutableMap(Function.identity(), it -> PropertyEnum.create(it.getName(), Connection.class)));

    public static final ImmutableList<AxisAlignedBB> CONNECTION_AABB = ImmutableList.of(
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0),
        new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125),
        new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
    );

    public BlockSaltPile() {
        super(Material.CIRCUITS, MapColor.SNOW);
        this.setSoundType(SoundType.SAND);
        this.setHardness(0.0F);
        this.setResistance(0.0F);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        var actualState = state;

        for (val facing : BlockSaltPile.CONNECTIONS.keySet()) {
            val property = BlockSaltPile.CONNECTIONS.get(facing);
            val connection = this.getConnection(access, position, facing);

            actualState = actualState.withProperty(property, connection);
        }

        return actualState;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(final IBlockAccess access, final BlockPos position) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        val actualState = state.getActualState(access, position);
        val index = this.getBoundingBoxIndex(actualState);

        return BlockSaltPile.CONNECTION_AABB.get(index);
    }

    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(final IBlockAccess access, final IBlockState state, final BlockPos position, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Deprecated
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        return Block.NULL_AABB;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos position) {
        val below = position.down();
        val state = world.getBlockState(below);
        val shape = state.getBlockFaceShape(world, below, EnumFacing.UP);

        return BlockFaceShape.SOLID == shape;
    }

    @Override
    public void onEntityCollision(final World world, final BlockPos position, final IBlockState state, final Entity entity) {
        if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
            return;
        }

        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        if (((EntityLivingBase) entity).isEntityUndead()) {
            if (world.getTotalWorldTime() % 20 == 0) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        val builder = new BlockStateContainer.Builder(this);

        BlockSaltPile.CONNECTIONS.values().forEach(builder::add);

        return builder.build();
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private int getBoundingBoxIndex(final IBlockState state) {
        val n = this.getConnection(state, EnumFacing.NORTH).isConnected();
        val s = this.getConnection(state, EnumFacing.SOUTH).isConnected();
        val e = this.getConnection(state, EnumFacing.EAST).isConnected();
        val w = this.getConnection(state, EnumFacing.WEST).isConnected();

        var index = 0;

        if (n || s && !e && !w) {
            index |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }

        if (s || n && !e && !w) {
            index |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }

        if (e || w && !n && !s) {
            index |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }

        if (w || e && !n && !s) {
            index |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }

        return index;
    }

    private Connection getConnection(final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        val offset = position.offset(face);

        if (this == access.getBlockState(offset).getBlock()) {
            return Connection.SIDE;
        }

        if (this == access.getBlockState(offset.down()).getBlock()) {
            return Connection.SIDE;
        }

        if (this == access.getBlockState(offset.up()).getBlock()) {
            return Connection.UP;
        }

        return Connection.NONE;
    }

    private Connection getConnection(final IBlockState state, final EnumFacing face) {
        val property = BlockSaltPile.CONNECTIONS.get(face);

        return state.getValue(property);
    }

    private enum Connection implements IStringSerializable {
        NONE, SIDE, UP;

        public final boolean isConnected() {
            return Connection.NONE != this;
        }

        @Override
        public final String getName() {
            return this.toString();
        }

        @Override
        public final String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
