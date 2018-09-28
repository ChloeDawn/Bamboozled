package net.insomniakitten.bamboo.init;

import com.google.common.base.Stopwatch;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@ObjectHolder(Bamboozled.ID)
@EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledRecipes {
    private static final Logger LOGGER = Bamboozled.getLogger("recipes");

    private BamboozledRecipes() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onRegisterRecipes(final RegistryEvent.Register<IRecipe> event) {
        BamboozledRecipes.LOGGER.info("Beginning recipe registration");

        final Stopwatch stopwatch = Stopwatch.createStarted();

        GameRegistry.addSmelting(BamboozledItems.BAMBOO, new ItemStack(BamboozledItems.BAMBOO_DRIED, 1), 0.0F);
        GameRegistry.addSmelting(BamboozledItems.SALT_BLOCK, new ItemStack(BamboozledItems.SALT_PILE, 4), 0.0F);
        GameRegistry.addSmelting(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 1), new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 2), 0.1F);

        final long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledRecipes.LOGGER.info("Recipe registration completed in {}ms", elapsed);

        BamboozledRecipes.registerOres();
    }

    private static void registerOres() {
        BamboozledRecipes.LOGGER.info("Beginning ore dictionary registration");

        final Stopwatch stopwatch = Stopwatch.createStarted();

        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO, 0, "bamboo");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_BUNDLE, 0, "blockBamboo");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_BUNDLE, 1, "blockBamboo", "blockBambooDried");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, "stairBamboo", "stairWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_DRIED_SLAB, 0, "slabBamboo", "slabWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_PLANKS, 0, "plankBamboo", "plankWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_PLANKS, 1, "plankBamboo", "plankWood", "plankBambooVertical", "plankWoodVertical");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, "stairWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, "slabWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_WALL, 0, "wallBamboo");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_DRIED_FENCE, 0, "fenceBamboo");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_PLANKS_FENCE, 0, "fenceWood");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_DOOR, 0, "doorBamboo");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_ORE, 0, "oreSalt", "oreHalite");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_PILE, 0, "dustSalt");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_BLOCK, 0, "blockSalt");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL, 0, "crystalSalt", "gemSalt");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 0, "blockSaltCrystal");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, "blockSaltCrystal", "blockSaltCrystalBrick");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 2, "blockSaltCrystal", "blockSaltCrystalBrick");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 2, "blockSaltCrystal", "blockSaltCrystalChiseled");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK_STAIRS, 0, "stairsSaltCrystal");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BRICK_STAIRS, 0, "stairsSaltCrystal");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK_SLAB, 0, "slabSaltCrystal");
        BamboozledRecipes.registerOre(BamboozledItems.SALT_CRYSTAL_BRICK_SLAB, 0, "slabSaltCrystal");
        BamboozledRecipes.registerOre(BamboozledItems.ROPE_FENCE, 0, "fenceWood", "fenceRope");
        BamboozledRecipes.registerOre(BamboozledItems.BAMBOO_CRATE, 0, "chest", "chestBamboo", "crate", "crateBamboo");

        final long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledRecipes.LOGGER.info("Ore dictionary registration completed in {}ms", elapsed);
    }

    private static void registerOre(final Item item, final int meta, final String... ores) {
        BamboozledRecipes.LOGGER.debug("Registering ores {} for item '{}'", Arrays.toString(ores), item.getRegistryName());

        final ItemStack stack = new ItemStack(item, 1, meta);

        for (final String ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
