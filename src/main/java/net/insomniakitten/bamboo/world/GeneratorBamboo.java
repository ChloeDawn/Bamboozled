package net.insomniakitten.bamboo.world;

import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class GeneratorBamboo {
    private GeneratorBamboo() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onChunkPopulation(final PopulateChunkEvent.Post event) {
        if (event.getRand().nextInt(6) != 0) {
            return;
        }

        final World world = event.getWorld();
        final Random random = event.getRand();
        final int x = event.getChunkX() * 16 + 8;
        final int z = event.getChunkZ() * 16 + 8;
        final MutableBlockPos position = new MutableBlockPos(z, 0, z);

        for (int i = 0; i < 8; ++i) {
            final int newX = x + random.nextInt(5) - random.nextInt(5);
            final int newZ = z + random.nextInt(5) - random.nextInt(5);

            position.setPos(newX, 0, newZ);

            if (!world.getBiome(position).isHighHumidity()) {
                continue;
            }

            GeneratorBamboo.movePositionToSurface(position, world);

            final int maxHeight = 2 + random.nextInt(random.nextInt(3) + 4);

            for (int height = 0; height < maxHeight; ++height) {
                final MutableBlockPos toPlace = position.move(EnumFacing.UP, 1);

                if (world.isAirBlock(toPlace) && BamboozledBlocks.BAMBOO.canPlaceBlockAt(world, toPlace)) {
                    world.setBlockState(toPlace, BamboozledBlocks.BAMBOO.getDefaultState(), 2);
                }
            }
        }
    }

    private static void movePositionToSurface(final MutableBlockPos position, final World world) {
        final Chunk chunk = world.getChunk(position);
        final int x = position.getX();
        final int z = position.getZ();
        final int height = world.getHeight(x, z);

        position.setY(height);

        IBlockState target;

        do {
            target = chunk.getBlockState(position.move(EnumFacing.DOWN));
        } while (GeneratorBamboo.isAboveSurface(world, position, target));
    }

    private static boolean isAboveSurface(final World world, final MutableBlockPos position, final IBlockState state) {
        if (world.isOutsideBuildHeight(position)) {
            return false;
        }

        final Block block = state.getBlock();

        if (state.getMaterial().isLiquid()) {
            return false;
        }

        if (block.isReplaceable(world, position)) {
            return true;
        }

        return block.isFoliage(world, position) || block.isWood(world, position);
    }
}
