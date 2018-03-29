package net.insomniakitten.bamboo.world;

import net.insomniakitten.bamboo.BamboozledBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public final class GeneratorBamboo {

    public GeneratorBamboo() { }

    private static void getSurface(World world, final MutableBlockPos pos) {
        final Chunk chunk = world.getChunkFromBlockCoords(pos);
        IBlockState target;
        pos.setY(world.getHeight(pos.getX(), pos.getZ()));
        do {
            target = chunk.getBlockState(pos.move(EnumFacing.DOWN));
        } while (!world.isOutsideBuildHeight(pos)
                && ((!target.getMaterial().isLiquid()
                && target.getBlock().isReplaceable(world, pos))
                || target.getBlock().isFoliage(world, pos)
                || target.getBlock().isWood(world, pos)));
    }

    @SubscribeEvent
    public void onChunkPopulation(PopulateChunkEvent.Post event) {
        if (event.getRand().nextInt(6) != 0) return;
        World world = event.getWorld();
        Random rand = event.getRand();
        int x = event.getChunkX() * 16 + 8;
        int z = event.getChunkZ() * 16 + 8;
        MutableBlockPos pos = new MutableBlockPos(z, 0, z);
        for (int i = 0; i < 8; ++i) {
            pos.setPos(x + rand.nextInt(5) - rand.nextInt(5), 0, z + rand.nextInt(5) - rand.nextInt(5));
            if (world.getBiome(pos).isHighHumidity()) {
                getSurface(world, pos);
                int maxHeight = 2 + rand.nextInt(rand.nextInt(3) + 4);
                for (int height = 0; height < maxHeight; ++height) {
                    BlockPos toPlace = pos.move(EnumFacing.UP, 1);
                    if (world.isAirBlock(toPlace) && BamboozledBlocks.BAMBOO.canPlaceBlockAt(world, toPlace)) {
                        world.setBlockState(toPlace, BamboozledBlocks.BAMBOO.getDefaultState(), 2);
                    }
                }
            }
        }
    }

}
