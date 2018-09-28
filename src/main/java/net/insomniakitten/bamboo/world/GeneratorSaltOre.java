package net.insomniakitten.bamboo.world;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class GeneratorSaltOre {
    private GeneratorSaltOre() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onChunkPopulation(final PopulateChunkEvent.Post event) {
        final int x = event.getChunkX() << 4;
        final int z = event.getChunkZ() << 4;
        final MutableBlockPos position = new MutableBlockPos(x, 0, z);
        final int rX = event.getRand().nextInt(16) + 8;
        final int rZ = event.getRand().nextInt(16) + 8;

        GeneratorSaltOre.movePositionToSurface(event.getWorld(), position.setPos(x + rX, 0, z + rZ));
        GeneratorSaltOre.generateCluster(event.getWorld(), event.getRand(), position.toImmutable());
    }

    private static void movePositionToSurface(final World world, final MutableBlockPos position) {
        final Chunk chunk = world.getChunk(position);
        final int x = position.getX();
        final int z = position.getZ();
        final int height = world.getHeight(x, z);

        position.setY(height);

        IBlockState target;

        do {
            target = chunk.getBlockState(position.move(EnumFacing.DOWN));
        } while (!world.isOutsideBuildHeight(position) && target.getMaterial().isReplaceable());
    }

    private static void generateCluster(final World world, final Random random, final BlockPos position) {
        if (!world.getBlockState(position.up()).getMaterial().isLiquid()) {
            return;
        }

        final MutableBlockPos target = new MutableBlockPos(position);
        final int clusterSize = Bamboozled.getWorldConfig().saltOreClusterSize;
        final int size = random.nextInt(Math.max(clusterSize - 2, 1)) + 2;

        for (int x = position.getX() - size; x <= position.getX() + size; ++x) {
            for (int z = position.getZ() - size; z <= position.getZ() + size; ++z) {
                final int rX = x - position.getX();
                final int rZ = z - position.getZ();

                if (rX * rX + rZ * rZ > size * size) {
                    continue;
                }

                for (int y = position.getY() - 1; y <= position.getY() + 1; ++y) {
                    final Block block = world.getBlockState(target.setPos(x, y, z)).getBlock();

                    if (Blocks.DIRT == block || Blocks.CLAY == block) {
                        world.setBlockState(target, BamboozledBlocks.SALT_ORE.getDefaultState(), 2 | 16);
                    }
                }
            }
        }
    }
}
