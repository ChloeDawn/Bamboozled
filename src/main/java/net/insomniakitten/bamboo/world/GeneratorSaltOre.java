package net.insomniakitten.bamboo.world;

import lombok.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class GeneratorSaltOre {
    private GeneratorSaltOre() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onChunkPopulation(final PopulateChunkEvent.Post event) {
        val x = event.getChunkX() << 4;
        val z = event.getChunkZ() << 4;
        val position = new MutableBlockPos(x, 0, z);
        val rX = event.getRand().nextInt(16) + 8;
        val rZ = event.getRand().nextInt(16) + 8;

        GeneratorSaltOre.movePositionToSurface(event.getWorld(), position.setPos(x + rX, 0, z + rZ));
        GeneratorSaltOre.generateCluster(event.getWorld(), event.getRand(), position.toImmutable());
    }

    private static void movePositionToSurface(final World world, final MutableBlockPos position) {
        val chunk = world.getChunk(position);
        val x = position.getX();
        val z = position.getZ();
        val height = world.getHeight(x, z);

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

        val target = new MutableBlockPos(position);
        val clusterSize = Bamboozled.getWorldConfig().getSaltClusterSize();
        val size = random.nextInt(Math.max(clusterSize - 2, 1)) + 2;

        for (var x = position.getX() - size; x <= position.getX() + size; ++x) {
            for (var z = position.getZ() - size; z <= position.getZ() + size; ++z) {
                val rX = x - position.getX();
                val rZ = z - position.getZ();

                if (rX * rX + rZ * rZ > size * size) {
                    continue;
                }

                for (var y = position.getY() - 1; y <= position.getY() + 1; ++y) {
                    val block = world.getBlockState(target.setPos(x, y, z)).getBlock();

                    if (Blocks.DIRT == block || Blocks.CLAY == block) {
                        world.setBlockState(target, BamboozledBlocks.SALT_ORE.getDefaultState(), 2 | 16);
                    }
                }
            }
        }
    }
}
