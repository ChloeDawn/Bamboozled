package net.insomniakitten.bamboo.block.base;

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

    public BlockStairs(Material material, MapColor mapColor, SoundType sound, float hardness, float resistance) {
        super(Blocks.AIR.getDefaultState());
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
        useNeighborBrightness = true;
        this.material = material;
        this.mapColor = mapColor;
    }

    public BlockStairs(Material material, SoundType sound, float hardness, float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    @Override
    @Deprecated
    public Material getMaterial(IBlockState state) {
        return material;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {}

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {}

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess access, BlockPos pos) {
        return access.getCombinedLight(pos, state.getLightValue(access, pos));
    }

    @Override
    public float getExplosionResistance(Entity exploder) {
        return blockResistance / 5.0F;
    }

    @Override
    public int tickRate(World worldIn) {
        return 10;
    }

    @Override
    public Vec3d modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3d motion) {
        return motion;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return FULL_BLOCK_AABB.offset(pos);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return isCollidable();
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {}

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {}

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {}

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {}

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {}

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess access, BlockPos pos) {
        return mapColor;
    }
}
