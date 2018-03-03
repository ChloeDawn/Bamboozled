package net.insomniakitten.bamboo.world;

import net.insomniakitten.bamboo.BamboozledConfig;
import net.insomniakitten.bamboo.BamboozledObjects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class GeneratorSaltOre {

    private final int clusterSize = BamboozledConfig.WORLD.clusterSize;

    @SubscribeEvent
    public void onChunkPopulation(PopulateChunkEvent.Post event) {
        int originX = event.getChunkX() << 4;
        int originZ = event.getChunkZ() << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(originX, 0, originZ);
        for (int i = 0; i < 4; ++i) {
            int x = event.getRand().nextInt(16) + 8;
            int z = event.getRand().nextInt(16) + 8;
            moveToLiquid(event.getWorld(), pos.setPos(originX + x, 0, originZ + z));
            generateCluster(event.getWorld(), event.getRand(), pos);
        }
    }

    private void moveToLiquid(World world, final BlockPos.MutableBlockPos pos) {
        final Chunk chunk = world.getChunkFromBlockCoords(pos);
        IBlockState target;
        pos.setY(world.getHeight(pos.getX(), pos.getZ()));
        do {
            target = chunk.getBlockState(pos.move(EnumFacing.DOWN));
        } while (!world.isOutsideBuildHeight(pos) && !target.getMaterial().isLiquid());
    }

    private void generateCluster(World world, Random rand, BlockPos.MutableBlockPos pos) {
        final Chunk chunk = world.getChunkFromBlockCoords(pos);
        if (!chunk.getBlockState(pos).getMaterial().isLiquid()) return;
        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos(pos);
        final int size = (rand.nextInt(clusterSize - 2) + 2);
        for (int x = pos.getX() - size; x <= pos.getX() + size; ++x) {
            for (int z = pos.getZ() - size; z <= pos.getZ() + size; ++z) {
                int rX = x - pos.getX();
                int rZ = z - pos.getZ();
                if (((rX * rX) + (rZ * rZ)) <= (size * size)) {
                    for (int y = pos.getY() - 1; y <= pos.getY() + 1; ++y) {
                        Block block = chunk.getBlockState(target.setPos(x, y, z)).getBlock();
                        if (block == Blocks.DIRT || block == Blocks.CLAY || block == Blocks.GRAVEL) {
                            world.setBlockState(target, BamboozledObjects.SALT_ORE.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }

}
