package net.insomniakitten.bamboo.init;

import com.google.common.base.Stopwatch;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.block.BlockBambooCrate;
import net.insomniakitten.bamboo.block.BlockBambooDoor;
import net.insomniakitten.bamboo.block.BlockBambooWall;
import net.insomniakitten.bamboo.block.BlockRope;
import net.insomniakitten.bamboo.block.BlockSalt;
import net.insomniakitten.bamboo.block.BlockSaltCrystal;
import net.insomniakitten.bamboo.block.BlockSaltOre;
import net.insomniakitten.bamboo.block.BlockSaltPile;
import net.insomniakitten.bamboo.block.base.BlockFence;
import net.insomniakitten.bamboo.block.base.BlockPlanks;
import net.insomniakitten.bamboo.block.base.BlockSlab;
import net.insomniakitten.bamboo.block.base.BlockStairs;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooCrate;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

@ObjectHolder(Bamboozled.ID)
@EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledBlocks {
    public static final Block BAMBOO = Blocks.AIR;
    public static final Block BAMBOO_BUNDLE = Blocks.AIR;
    public static final Block BAMBOO_DRIED_STAIRS = Blocks.AIR;
    public static final Block BAMBOO_DRIED_SLAB = Blocks.AIR;
    public static final Block BAMBOO_PLANKS = Blocks.AIR;
    public static final Block BAMBOO_PLANKS_STAIRS = Blocks.AIR;
    public static final Block BAMBOO_PLANKS_SLAB = Blocks.AIR;
    public static final Block BAMBOO_WALL = Blocks.AIR;
    public static final Block BAMBOO_DRIED_FENCE = Blocks.AIR;
    public static final Block BAMBOO_PLANKS_FENCE = Blocks.AIR;
    public static final Block BAMBOO_DOOR = Blocks.AIR;
    public static final Block SALT_ORE = Blocks.AIR;
    public static final Block SALT_PILE = Blocks.AIR;
    public static final Block SALT_BLOCK = Blocks.AIR;
    public static final Block SALT_CRYSTAL_BLOCK = Blocks.AIR;
    public static final Block SALT_CRYSTAL_BLOCK_STAIRS = Blocks.AIR;
    public static final Block SALT_CRYSTAL_BRICK_STAIRS = Blocks.AIR;
    public static final Block SALT_CRYSTAL_BLOCK_SLAB = Blocks.AIR;
    public static final Block SALT_CRYSTAL_BRICK_SLAB = Blocks.AIR;
    public static final Block ROPE = Blocks.AIR;
    public static final Block ROPE_FENCE = Blocks.AIR;
    public static final Block BAMBOO_CRATE = Blocks.AIR;

    private static final Logger LOGGER = Bamboozled.getLogger("blocks");

    private BamboozledBlocks() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onRegisterBlocks(final RegistryEvent.Register<Block> context) {
        BamboozledBlocks.LOGGER.info("Beginning block registration");

        final Stopwatch stopwatch = Stopwatch.createStarted();
        final IForgeRegistry<Block> registry = context.getRegistry();

        BamboozledBlocks.registerBlock(registry, "bamboo", new BlockBamboo());
        BamboozledBlocks.registerBlock(registry, "bamboo_bundle", new BlockBambooBundle());
        BamboozledBlocks.registerBlock(registry, "bamboo_dried_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_dried_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_planks", new BlockPlanks());
        BamboozledBlocks.registerBlock(registry, "bamboo_planks_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_planks_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_wall", new BlockBambooWall());
        BamboozledBlocks.registerBlock(registry, "bamboo_dried_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_planks_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_door", new BlockBambooDoor());
        BamboozledBlocks.registerBlock(registry, "salt_ore", new BlockSaltOre());
        BamboozledBlocks.registerBlock(registry, "salt_pile", new BlockSaltPile());
        BamboozledBlocks.registerBlock(registry, "salt_block", new BlockSalt());
        BamboozledBlocks.registerBlock(registry, "salt_crystal_block", new BlockSaltCrystal());
        BamboozledBlocks.registerBlock(registry, "salt_crystal_block_stairs", new BlockStairs(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledBlocks.registerBlock(registry, "salt_crystal_brick_stairs", new BlockStairs(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledBlocks.registerBlock(registry, "salt_crystal_block_slab", new BlockSlab(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledBlocks.registerBlock(registry, "salt_crystal_brick_slab", new BlockSlab(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledBlocks.registerBlock(registry, "rope", new BlockRope());
        BamboozledBlocks.registerBlock(registry, "rope_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledBlocks.registerBlock(registry, "bamboo_crate", new BlockBambooCrate());

        BamboozledBlocks.registerBlockEntity(BlockEntityBambooCrate.class, "bamboo_crate");

        final long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledBlocks.LOGGER.info("Block registration completed in {}ms", elapsed);
    }

    private static void registerBlock(final IForgeRegistry<Block> registry, final String name, final Block block) {
        BamboozledBlocks.LOGGER.debug("Registering block '{}:{}'", Bamboozled.ID, name);

        block.setRegistryName(Bamboozled.addNamespace(name));
        block.setTranslationKey(Bamboozled.addNamespace(name, '.'));
        block.setCreativeTab(Bamboozled.getItemGroup());

        registry.register(block);
    }

    private static void registerBlockEntity(final Class<? extends TileEntity> clazz, final String name) {
        BamboozledBlocks.LOGGER.debug("Registering block entity {} as '{}:{}'", clazz, Bamboozled.ID, name);

        GameRegistry.registerTileEntity(clazz, Bamboozled.addNamespace(name));
    }
}
