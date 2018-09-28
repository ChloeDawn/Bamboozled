package net.insomniakitten.bamboo.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockStairs extends net.minecraft.block.BlockStairs {
    private final Material material;
    private final MapColor mapColor;

    public BlockStairs(final Material material, final MapColor mapColor, final SoundType sound, final float hardness, final float resistance) {
        super(Blocks.AIR.getDefaultState());
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setSoundType(sound);
        this.useNeighborBrightness = true;
        this.material = material;
        this.mapColor = mapColor;
    }

    public BlockStairs(final Material material, final SoundType sound, final float hardness, final float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    @Override
    @Deprecated
    public Material getMaterial(final IBlockState state) {
        return this.material;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos position, final Random rand) {

    }

    @Override
    public void onBlockClicked(final World world, final BlockPos position, final EntityPlayer player) {

    }

    @Override
    public void onPlayerDestroy(final World world, final BlockPos position, final IBlockState state) {

    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        return access.getCombinedLight(position, state.getLightValue(access, position));
    }

    @Override
    public float getExplosionResistance(final Entity exploder) {
        return this.blockResistance / 5.0F;
    }

    @Override
    public int tickRate(final World world) {
        return 10;
    }

    @Override
    public Vec3d modifyAcceleration(final World world, final BlockPos position, final Entity entity, final Vec3d motion) {
        return motion;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos position) {
        return Block.FULL_BLOCK_AABB.offset(position);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canCollideCheck(final IBlockState state, final boolean hitIfLiquid) {
        return this.isCollidable();
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos position) {
        final IBlockState state = world.getBlockState(position);
        final Block block = state.getBlock();

        return block.isReplaceable(world, position);
    }

    @Override
    public void onBlockAdded(final World world, final BlockPos position, final IBlockState state) {

    }

    @Override
    public void breakBlock(final World world, final BlockPos position, final IBlockState state) {

    }

    @Override
    public void onEntityWalk(final World world, final BlockPos position, final Entity entity) {

    }

    @Override
    public void updateTick(final World world, final BlockPos position, final IBlockState state, final Random random) {

    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos position, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing face, final float x, final float y, final float z) {
        return false;
    }

    @Override
    public void onExplosionDestroy(final World world, final BlockPos position, final Explosion explosion) {

    }

    @Override
    @Deprecated
    public MapColor getMapColor(final IBlockState state, final IBlockAccess access, final BlockPos position) {
        return this.mapColor;
    }
}
