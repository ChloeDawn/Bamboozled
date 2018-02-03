package net.insomniakitten.bamboo.world;

import com.google.common.base.Predicate;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class GeneratorSaltOre {

    @GameRegistry.ObjectHolder(Bamboozled.ID + ":salt_ore")
    public static final Block SALT_ORE = Blocks.AIR;

    private static final Predicate<IBlockState> PREDICATE = BlockMatcher.forBlock(Blocks.STONE);

    private final int thickness;

    public GeneratorSaltOre() {
        thickness = BamboozledConfig.WORLD.saltOreThickness;
    }

    @SubscribeEvent
    public void onChunkPopulation(PopulateChunkEvent.Post event) {
        BlockPos origin = new BlockPos(event.getChunkX() * 16 + 8, 36, event.getChunkZ() * 16 + 8);
        for (BlockPos pos : BlockPos.getAllInBoxMutable(origin, origin.add(15, 128, 15))) {
            if (event.getWorld().getBlockState(pos).getMaterial() == Material.WATER) {
                BlockPos target = pos.toImmutable();

                for (BlockPos offset : BlockPos.getAllInBoxMutable(
                        target.offset(EnumFacing.EAST, thickness).offset(EnumFacing.SOUTH, thickness),
                        target.offset(EnumFacing.WEST, thickness).offset(EnumFacing.NORTH, thickness))) {
                    replace(event.getWorld(), offset);
                }

                for (int i = 1; i <= thickness; i++) {
                    replace(event.getWorld(), target.offset(EnumFacing.DOWN, i));
                }
            }
        }
    }

    private void replace(World world, BlockPos pos) {
        IBlockState toReplace = world.getBlockState(pos);
        if (toReplace.getBlock().isReplaceableOreGen(toReplace, world, pos, PREDICATE)) {
            world.setBlockState(pos, SALT_ORE.getDefaultState());
        }
    }

}
