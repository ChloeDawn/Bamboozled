package net.insomniakitten.bamboo.world;

import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
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
    private GeneratorSaltOre() {}

    @SubscribeEvent
    static void onChunkPopulation(final PopulateChunkEvent.Post event) {
        val x = event.getChunkX() << 4;
        val z = event.getChunkZ() << 4;
        val pos = new MutableBlockPos(x, 0, z);
        val randX = event.getRand().nextInt(16) + 8;
        val randZ = event.getRand().nextInt(16) + 8;
        GeneratorSaltOre.findSurface(event.getWorld(), pos.setPos(x + randX, 0, z + randZ));
        GeneratorSaltOre.generateCluster(event.getWorld(), event.getRand(), pos.toImmutable());
    }

    private static void findSurface(final World world, final MutableBlockPos pos) {
        val chunk = world.getChunk(pos);
        IBlockState target;
        pos.setY(world.getHeight(pos.getX(), pos.getZ()));
        do {
            target = chunk.getBlockState(pos.move(EnumFacing.DOWN));
        } while (!world.isOutsideBuildHeight(pos) && target.getMaterial().isReplaceable());
    }

    private static void generateCluster(final World world, final Random rand, final BlockPos pos) {
        if (!world.getBlockState(pos.up()).getMaterial().isLiquid()) return;
        val target = new MutableBlockPos(pos);
        val size = rand.nextInt(Math.max(Bamboozled.getWorldConfig().getSaltClusterSize() - 2, 1)) + 2;
        for (var x = pos.getX() - size; x <= pos.getX() + size; ++x) {
            for (var z = pos.getZ() - size; z <= pos.getZ() + size; ++z) {
                val rX = x - pos.getX();
                val rZ = z - pos.getZ();
                if (rX * rX + rZ * rZ <= size * size) {
                    for (var y = pos.getY() - 1; y <= pos.getY() + 1; ++y) {
                        val block = world.getBlockState(target.setPos(x, y, z)).getBlock();
                        if (block == Blocks.DIRT || block == Blocks.CLAY) {
                            world.setBlockState(target, BamboozledBlocks.SALT_ORE.getDefaultState(), 2 | 16);
                        }
                    }
                }
            }
        }
    }
}
