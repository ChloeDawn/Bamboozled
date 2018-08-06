package net.insomniakitten.bamboo.world;

import lombok.experimental.UtilityClass;
import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@UtilityClass
public class GeneratorBamboo {
    @SubscribeEvent
    void onChunkPopulation(PopulateChunkEvent.Post event) {
        if (event.getRand().nextInt(6) != 0) return;
        val world = event.getWorld();
        val rand = event.getRand();
        val x = event.getChunkX() * 16 + 8;
        val z = event.getChunkZ() * 16 + 8;
        val pos = new MutableBlockPos(z, 0, z);
        for (var i = 0; i < 8; ++i) {
            pos.setPos(x + rand.nextInt(5) - rand.nextInt(5), 0, z + rand.nextInt(5) - rand.nextInt(5));
            if (world.getBiome(pos).isHighHumidity()) {
                getSurface(world, pos);
                val maxHeight = 2 + rand.nextInt(rand.nextInt(3) + 4);
                for (var height = 0; height < maxHeight; ++height) {
                    val toPlace = pos.move(EnumFacing.UP, 1);
                    if (world.isAirBlock(toPlace) && BamboozledBlocks.BAMBOO.canPlaceBlockAt(world, toPlace)) {
                        world.setBlockState(toPlace, BamboozledBlocks.BAMBOO.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    private void getSurface(World world, final MutableBlockPos pos) {
        val chunk = world.getChunk(pos);
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
}
