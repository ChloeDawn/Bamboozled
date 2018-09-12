package net.insomniakitten.bamboo.world;

import lombok.var;
import lombok.val;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class GeneratorBamboo {
    private GeneratorBamboo() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onChunkPopulation(final PopulateChunkEvent.Post event) {
        if (event.getRand().nextInt(6) != 0) {
            return;
        }

        val world = event.getWorld();
        val random = event.getRand();
        val x = event.getChunkX() * 16 + 8;
        val z = event.getChunkZ() * 16 + 8;
        val position = new MutableBlockPos(z, 0, z);

        for (var i = 0; i < 8; ++i) {
            val newX = x + random.nextInt(5) - random.nextInt(5);
            val newZ = z + random.nextInt(5) - random.nextInt(5);

            position.setPos(newX, 0, newZ);

            if (!world.getBiome(position).isHighHumidity()) {
                continue;
            }

            GeneratorBamboo.movePositionToSurface(position, world);

            val maxHeight = 2 + random.nextInt(random.nextInt(3) + 4);

            for (var height = 0; height < maxHeight; ++height) {
                val toPlace = position.move(EnumFacing.UP, 1);

                if (world.isAirBlock(toPlace) && BamboozledBlocks.BAMBOO.canPlaceBlockAt(world, toPlace)) {
                    world.setBlockState(toPlace, BamboozledBlocks.BAMBOO.getDefaultState(), 2);
                }
            }
        }
    }

    private static void movePositionToSurface(final MutableBlockPos position, final World world) {
        val chunk = world.getChunk(position);
        val x = position.getX();
        val z = position.getZ();
        val height = world.getHeight(x, z);

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

        val block = state.getBlock();

        if (state.getMaterial().isLiquid()) {
            return false;
        }

        if (block.isReplaceable(world, position)) {
            return true;
        }

        return block.isFoliage(world, position) || block.isWood(world, position);
    }
}
