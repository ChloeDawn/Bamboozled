package net.insomniakitten.bamboo;

import lombok.experimental.UtilityClass;
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

@UtilityClass
@EventBusSubscriber(modid = Bamboozled.ID)
public class BamboozledRegistry {
    @SubscribeEvent
    void onBlockRegistry(RegistryEvent.Register<Block> event) {
        registerBlock(event, "bamboo", new BlockBamboo());
        registerBlock(event, "bamboo_bundle", new BlockBambooBundle());
        registerBlock(event, "bamboo_dried_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        registerBlock(event, "bamboo_dried_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        registerBlock(event, "bamboo_planks", new BlockPlanks());
        registerBlock(event, "bamboo_planks_stairs", new BlockStairs(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        registerBlock(event, "bamboo_planks_slab", new BlockSlab(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        registerBlock(event, "bamboo_wall", new BlockBambooWall());
        registerBlock(event, "bamboo_door", new BlockBambooDoor());
        registerBlock(event, "salt_ore", new BlockSaltOre());
        registerBlock(event, "salt_pile", new BlockSaltPile());
        registerBlock(event, "salt_block", new BlockSalt());
        registerBlock(event, "rope", new BlockRope());
    }

    @SubscribeEvent
    void onItemRegistry(RegistryEvent.Register<Item> event) {
        registerItem(event, "bamboo", new ItemBlockBase(BamboozledBlocks.BAMBOO));
        registerItem(event, "bamboo_dried", new ItemBase());
        registerItem(event, "bamboo_bundle", new ItemBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE));
        registerItem(event, "bamboo_dried_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS));
        registerItem(event, "bamboo_dried_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB));
        registerItem(event, "bamboo_planks", new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS));
        registerItem(event, "bamboo_planks_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS));
        registerItem(event, "bamboo_planks_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB));
        registerItem(event, "bamboo_wall", new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL));
        registerItem(event, "bamboo_door", new ItemBlockDoorBase(BamboozledBlocks.BAMBOO_DOOR));
        registerItem(event, "salt_ore", new ItemBlockBase(BamboozledBlocks.SALT_ORE));
        registerItem(event, "salt_pile", new ItemSaltPile(BamboozledBlocks.SALT_PILE));
        registerItem(event, "salt_block", new ItemBlockBase(BamboozledBlocks.SALT_BLOCK));
        registerItem(event, "rope", new ItemBlockBase(BamboozledBlocks.ROPE));
    }

    @SubscribeEvent
    void onEntityRegistry(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityFallingSaltBlock.ENTRY.build());
        event.getRegistry().register(EntityThrownSaltPile.ENTRY.build());
    }

    @SubscribeEvent
    void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
        registerSmelting(BamboozledItems.BAMBOO, BamboozledItems.BAMBOO_DRIED, 1);
        registerSmelting(BamboozledItems.SALT_BLOCK, BamboozledItems.SALT_PILE, 4);

        registerOre(BamboozledItems.BAMBOO, 0, "bamboo");
        registerOre(BamboozledItems.BAMBOO_BUNDLE, 0, "blockBamboo");
        registerOre(BamboozledItems.BAMBOO_BUNDLE, 1, "blockBamboo", "blockBambooDried");
        registerOre(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, "stairBamboo", "stairWood");
        registerOre(BamboozledItems.BAMBOO_DRIED_SLAB, 0, "slabBamboo", "slabWood");
        registerOre(BamboozledItems.BAMBOO_PLANKS, 0, "plankBamboo", "plankWood");
        registerOre(BamboozledItems.BAMBOO_PLANKS, 1, "plankBamboo", "plankWood", "plankBambooVertical", "plankWoodVertical");
        registerOre(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, "stairWood");
        registerOre(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, "slabWood");
        registerOre(BamboozledItems.BAMBOO_WALL, 0, "wallBamboo");
        registerOre(BamboozledItems.BAMBOO_DOOR, 0, "doorBamboo");
        registerOre(BamboozledItems.SALT_ORE, 0, "oreSalt", "oreHalite");
        registerOre(BamboozledItems.SALT_PILE, 0, "dustSalt");
        registerOre(BamboozledItems.SALT_BLOCK, 0, "blockSalt");
    }

    private void registerBlock(RegistryEvent.Register<Block> event, String name, Block block) {
        block.setRegistryName(Bamboozled.ID, name);
        block.setTranslationKey(Bamboozled.ID + "." + name);
        block.setCreativeTab(Bamboozled.TAB);
        event.getRegistry().register(block);
    }

    private void registerItem(RegistryEvent.Register<Item> event, String name, Item item) {
        item.setRegistryName(Bamboozled.ID, name);
        item.setTranslationKey(Bamboozled.ID + "." + name);
        item.setCreativeTab(Bamboozled.TAB);
        event.getRegistry().register(item);
    }

    private void registerSmelting(Item input, Item output, int amount) {
        GameRegistry.addSmelting(input, new ItemStack(output, amount), 0.0F);
    }

    private void registerOre(Item item, int meta, String... ores) {
        val stack = new ItemStack(item, 1, meta);
        for (val ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
