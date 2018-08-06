package net.insomniakitten.bamboo;

import lombok.val;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.block.BlockBambooDoor;
import net.insomniakitten.bamboo.block.BlockBambooWall;
import net.insomniakitten.bamboo.block.BlockRope;
import net.insomniakitten.bamboo.block.BlockSalt;
import net.insomniakitten.bamboo.block.BlockSaltOre;
import net.insomniakitten.bamboo.block.BlockSaltPile;
import net.insomniakitten.bamboo.block.base.BlockPlanks;
import net.insomniakitten.bamboo.block.base.BlockSlab;
import net.insomniakitten.bamboo.block.base.BlockStairs;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.item.ItemBambooBundle;
import net.insomniakitten.bamboo.item.ItemSaltPile;
import net.insomniakitten.bamboo.item.base.ItemBase;
import net.insomniakitten.bamboo.item.base.ItemBlockBase;
import net.insomniakitten.bamboo.item.base.ItemBlockDoorBase;
import net.insomniakitten.bamboo.item.base.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.base.ItemBlockSlabBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledRegistry {
    private BamboozledRegistry() {}

    @SubscribeEvent
    static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
        BamboozledRegistry.registerBlock(event, "bamboo", new BlockBamboo());
        BamboozledRegistry.registerBlock(event, "bamboo_bundle", new BlockBambooBundle());
        BamboozledRegistry.registerBlock(event, "bamboo_dried_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_dried_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_planks", new BlockPlanks());
        BamboozledRegistry.registerBlock(event, "bamboo_planks_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_planks_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_wall", new BlockBambooWall());
        BamboozledRegistry.registerBlock(event, "bamboo_door", new BlockBambooDoor());
        BamboozledRegistry.registerBlock(event, "salt_ore", new BlockSaltOre());
        BamboozledRegistry.registerBlock(event, "salt_pile", new BlockSaltPile());
        BamboozledRegistry.registerBlock(event, "salt_block", new BlockSalt());
        BamboozledRegistry.registerBlock(event, "rope", new BlockRope());
    }

    @SubscribeEvent
    static void onItemRegistry(final RegistryEvent.Register<Item> event) {
        BamboozledRegistry.registerItem(event, "bamboo", new ItemBlockBase(BamboozledBlocks.BAMBOO));
        BamboozledRegistry.registerItem(event, "bamboo_dried", new ItemBase());
        BamboozledRegistry.registerItem(event, "bamboo_bundle", new ItemBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE));
        BamboozledRegistry.registerItem(event, "bamboo_dried_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS));
        BamboozledRegistry.registerItem(event, "bamboo_dried_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB));
        BamboozledRegistry.registerItem(event, "bamboo_planks", new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS));
        BamboozledRegistry.registerItem(event, "bamboo_planks_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS));
        BamboozledRegistry.registerItem(event, "bamboo_planks_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB));
        BamboozledRegistry.registerItem(event, "bamboo_wall", new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL));
        BamboozledRegistry.registerItem(event, "bamboo_door", new ItemBlockDoorBase(BamboozledBlocks.BAMBOO_DOOR));
        BamboozledRegistry.registerItem(event, "salt_ore", new ItemBlockBase(BamboozledBlocks.SALT_ORE));
        BamboozledRegistry.registerItem(event, "salt_pile", new ItemSaltPile(BamboozledBlocks.SALT_PILE));
        BamboozledRegistry.registerItem(event, "salt_block", new ItemBlockBase(BamboozledBlocks.SALT_BLOCK));
        BamboozledRegistry.registerItem(event, "rope", new ItemBlockBase(BamboozledBlocks.ROPE));
    }

    @SubscribeEvent
    static void onEntityRegistry(final RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityFallingSaltBlock.ENTRY.build());
        event.getRegistry().register(EntityThrownSaltPile.ENTRY.build());
    }

    @SubscribeEvent
    static void onRecipeRegistry(final RegistryEvent.Register<IRecipe> event) {
        BamboozledRegistry.registerSmelting(BamboozledItems.BAMBOO, BamboozledItems.BAMBOO_DRIED, 1);
        BamboozledRegistry.registerSmelting(BamboozledItems.SALT_BLOCK, BamboozledItems.SALT_PILE, 4);

        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO, 0, "bamboo");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_BUNDLE, 0, "blockBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_BUNDLE, 1, "blockBamboo", "blockBambooDried");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, "stairBamboo", "stairWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_DRIED_SLAB, 0, "slabBamboo", "slabWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_PLANKS, 0, "plankBamboo", "plankWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_PLANKS, 1, "plankBamboo", "plankWood", "plankBambooVertical", "plankWoodVertical");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, "stairWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, "slabWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_WALL, 0, "wallBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_DOOR, 0, "doorBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_ORE, 0, "oreSalt", "oreHalite");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_PILE, 0, "dustSalt");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_BLOCK, 0, "blockSalt");
    }

    private static void registerBlock(final RegistryEvent.Register<Block> event, final String name, final Block block) {
        block.setRegistryName(Bamboozled.ID, name);
        block.setTranslationKey(Bamboozled.ID + "." + name);
        block.setCreativeTab(Bamboozled.getItemGroup());
        event.getRegistry().register(block);
    }

    private static void registerItem(final RegistryEvent.Register<Item> event, final String name, final Item item) {
        item.setRegistryName(Bamboozled.ID, name);
        item.setTranslationKey(Bamboozled.ID + "." + name);
        item.setCreativeTab(Bamboozled.getItemGroup());
        event.getRegistry().register(item);
    }

    private static void registerSmelting(final Item input, final Item output, final int amount) {
        GameRegistry.addSmelting(input, new ItemStack(output, amount), 0.0F);
    }

    private static void registerOre(final Item item, final int meta, final String... ores) {
        val stack = new ItemStack(item, 1, meta);
        for (val ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
