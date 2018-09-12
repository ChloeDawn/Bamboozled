package net.insomniakitten.bamboo.init;

import com.google.common.base.Stopwatch;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.item.ItemBlockBambooBundle;
import net.insomniakitten.bamboo.item.ItemBlockSaltCrystal;
import net.insomniakitten.bamboo.item.ItemSaltPile;
import net.insomniakitten.bamboo.item.base.ItemBase;
import net.insomniakitten.bamboo.item.base.ItemBlockBase;
import net.insomniakitten.bamboo.item.base.ItemBlockDoorBase;
import net.insomniakitten.bamboo.item.base.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.base.ItemBlockSlabBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.concurrent.TimeUnit;

@ObjectHolder(Bamboozled.ID)
@EventBusSubscriber(modid = Bamboozled.ID)
@Log4j2(topic = Bamboozled.ID + ".items")
public final class BamboozledItems {
    public static final Item BAMBOO = Items.AIR;
    public static final Item BAMBOO_DRIED = Items.AIR;
    public static final Item BAMBOO_BUNDLE = Items.AIR;
    public static final Item BAMBOO_DRIED_STAIRS = Items.AIR;
    public static final Item BAMBOO_DRIED_SLAB = Items.AIR;
    public static final Item BAMBOO_PLANKS = Items.AIR;
    public static final Item BAMBOO_PLANKS_STAIRS = Items.AIR;
    public static final Item BAMBOO_PLANKS_SLAB = Items.AIR;
    public static final Item BAMBOO_WALL = Items.AIR;
    public static final Item BAMBOO_DRIED_FENCE = Items.AIR;
    public static final Item BAMBOO_PLANKS_FENCE = Items.AIR;
    public static final Item BAMBOO_DOOR = Items.AIR;
    public static final Item SALT_ORE = Items.AIR;
    public static final Item SALT_PILE = Items.AIR;
    public static final Item SALT_BLOCK = Items.AIR;
    public static final Item SALT_CRYSTAL = Items.AIR;
    public static final Item SALT_CRYSTAL_BLOCK = Items.AIR;
    public static final Item SALT_CRYSTAL_BLOCK_STAIRS = Items.AIR;
    public static final Item SALT_CRYSTAL_BRICK_STAIRS = Items.AIR;
    public static final Item SALT_CRYSTAL_BLOCK_SLAB = Items.AIR;
    public static final Item SALT_CRYSTAL_BRICK_SLAB = Items.AIR;
    public static final Item ROPE = Items.AIR;
    public static final Item ROPE_FENCE = Items.AIR;
    public static final Item BAMBOO_CRATE = Items.AIR;

    private BamboozledItems() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }
    
    @SubscribeEvent
    static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        BamboozledItems.LOGGER.info("Beginning item registration");

        val stopwatch = Stopwatch.createStarted();
        val registry = event.getRegistry();

        BamboozledItems.registerItem(registry, "bamboo", new ItemBlockBase(BamboozledBlocks.BAMBOO));
        BamboozledItems.registerItem(registry, "bamboo_dried", new ItemBase());
        BamboozledItems.registerItem(registry, "bamboo_bundle", new ItemBlockBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE));
        BamboozledItems.registerItem(registry, "bamboo_dried_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS));
        BamboozledItems.registerItem(registry, "bamboo_dried_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB));
        BamboozledItems.registerItem(registry, "bamboo_planks", new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS));
        BamboozledItems.registerItem(registry, "bamboo_planks_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS));
        BamboozledItems.registerItem(registry, "bamboo_planks_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB));
        BamboozledItems.registerItem(registry, "bamboo_wall", new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL));
        BamboozledItems.registerItem(registry, "bamboo_dried_fence", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_FENCE));
        BamboozledItems.registerItem(registry, "bamboo_planks_fence", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_FENCE));
        BamboozledItems.registerItem(registry, "bamboo_door", new ItemBlockDoorBase(BamboozledBlocks.BAMBOO_DOOR));
        BamboozledItems.registerItem(registry, "salt_ore", new ItemBlockBase(BamboozledBlocks.SALT_ORE));
        BamboozledItems.registerItem(registry, "salt_pile", new ItemSaltPile(BamboozledBlocks.SALT_PILE));
        BamboozledItems.registerItem(registry, "salt_block", new ItemBlockBase(BamboozledBlocks.SALT_BLOCK));
        BamboozledItems.registerItem(registry, "salt_crystal", new ItemBase());
        BamboozledItems.registerItem(registry, "salt_crystal_block", new ItemBlockSaltCrystal(BamboozledBlocks.SALT_CRYSTAL_BLOCK));
        BamboozledItems.registerItem(registry, "salt_crystal_block_stairs", new ItemBlockBase(BamboozledBlocks.SALT_CRYSTAL_BLOCK_STAIRS));
        BamboozledItems.registerItem(registry, "salt_crystal_brick_stairs", new ItemBlockBase(BamboozledBlocks.SALT_CRYSTAL_BRICK_STAIRS));
        BamboozledItems.registerItem(registry, "salt_crystal_block_slab", new ItemBlockSlabBase(BamboozledBlocks.SALT_CRYSTAL_BLOCK_SLAB));
        BamboozledItems.registerItem(registry, "salt_crystal_brick_slab", new ItemBlockSlabBase(BamboozledBlocks.SALT_CRYSTAL_BRICK_SLAB));
        BamboozledItems.registerItem(registry, "rope", new ItemBlockBase(BamboozledBlocks.ROPE));
        BamboozledItems.registerItem(registry, "rope_fence", new ItemBlockBase(BamboozledBlocks.ROPE_FENCE));
        BamboozledItems.registerItem(registry, "bamboo_crate", new ItemBlockBase(BamboozledBlocks.BAMBOO_CRATE));

        val elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledItems.LOGGER.info("Item registration completed in {}ms", elapsed);
    }

    private static void registerItem(final IForgeRegistry<Item> registry, final String name, final Item item) {
        BamboozledItems.LOGGER.debug("Registering item '{}:{}'", Bamboozled.ID, name);

        item.setRegistryName(Bamboozled.addNamespace(name));
        item.setTranslationKey(Bamboozled.addNamespace(name, '.'));
        item.setCreativeTab(Bamboozled.getItemGroup());
        
        registry.register(item);
    }
}
