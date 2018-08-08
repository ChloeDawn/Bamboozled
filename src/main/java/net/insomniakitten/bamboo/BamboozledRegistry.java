package net.insomniakitten.bamboo;

import lombok.val;
import net.insomniakitten.bamboo.block.*;
import net.insomniakitten.bamboo.block.base.BlockFence;
import net.insomniakitten.bamboo.block.base.BlockPlanks;
import net.insomniakitten.bamboo.block.base.BlockSlab;
import net.insomniakitten.bamboo.block.base.BlockStairs;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.item.ItemBlockBambooBundle;
import net.insomniakitten.bamboo.item.ItemBlockSaltCrystal;
import net.insomniakitten.bamboo.item.ItemSaltPile;
import net.insomniakitten.bamboo.item.base.ItemBase;
import net.insomniakitten.bamboo.item.base.ItemBlockBase;
import net.insomniakitten.bamboo.item.base.ItemBlockDoorBase;
import net.insomniakitten.bamboo.item.base.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.base.ItemBlockSlabBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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
        BamboozledRegistry.registerBlock(event, "bamboo_dried_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_planks_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_door", new BlockBambooDoor());
        BamboozledRegistry.registerBlock(event, "salt_ore", new BlockSaltOre());
        BamboozledRegistry.registerBlock(event, "salt_pile", new BlockSaltPile());
        BamboozledRegistry.registerBlock(event, "salt_block", new BlockSalt());
        BamboozledRegistry.registerBlock(event, "salt_crystal_block", new BlockSaltCrystal());
        BamboozledRegistry.registerBlock(event, "salt_crystal_block_stairs", new BlockStairs(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledRegistry.registerBlock(event, "salt_crystal_brick_stairs", new BlockStairs(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledRegistry.registerBlock(event, "salt_crystal_block_slab", new BlockSlab(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledRegistry.registerBlock(event, "salt_crystal_brick_slab", new BlockSlab(Material.ROCK, MapColor.SNOW, SoundType.STONE, 1.5F, 17.5F));
        BamboozledRegistry.registerBlock(event, "rope", new BlockRope());
        BamboozledRegistry.registerBlock(event, "rope_fence", new BlockFence(Material.WOOD, SoundType.WOOD, 2.0F, 5.0F));
        BamboozledRegistry.registerBlock(event, "bamboo_chest", new BlockBambooChest(BlockBambooChest.TYPE_BAMBOO));
        BamboozledRegistry.registerBlock(event, "trapped_bamboo_chest", new BlockBambooChest(BlockBambooChest.TYPE_BAMBOO_TRAP));

        BamboozledRegistry.registerBlockEntity(BlockEntityBambooChest.class, "bamboo_chest");
    }

    @SubscribeEvent
    static void onItemRegistry(final RegistryEvent.Register<Item> event) {
        BamboozledRegistry.registerItem(event, "bamboo", new ItemBlockBase(BamboozledBlocks.BAMBOO));
        BamboozledRegistry.registerItem(event, "bamboo_dried", new ItemBase());
        BamboozledRegistry.registerItem(event, "bamboo_bundle", new ItemBlockBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE));
        BamboozledRegistry.registerItem(event, "bamboo_dried_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS));
        BamboozledRegistry.registerItem(event, "bamboo_dried_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB));
        BamboozledRegistry.registerItem(event, "bamboo_planks", new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS));
        BamboozledRegistry.registerItem(event, "bamboo_planks_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS));
        BamboozledRegistry.registerItem(event, "bamboo_planks_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB));
        BamboozledRegistry.registerItem(event, "bamboo_wall", new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL));
        BamboozledRegistry.registerItem(event, "bamboo_dried_fence", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_FENCE));
        BamboozledRegistry.registerItem(event, "bamboo_planks_fence", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_FENCE));
        BamboozledRegistry.registerItem(event, "bamboo_door", new ItemBlockDoorBase(BamboozledBlocks.BAMBOO_DOOR));
        BamboozledRegistry.registerItem(event, "salt_ore", new ItemBlockBase(BamboozledBlocks.SALT_ORE));
        BamboozledRegistry.registerItem(event, "salt_pile", new ItemSaltPile(BamboozledBlocks.SALT_PILE));
        BamboozledRegistry.registerItem(event, "salt_block", new ItemBlockBase(BamboozledBlocks.SALT_BLOCK));
        BamboozledRegistry.registerItem(event, "salt_crystal", new ItemBase());
        BamboozledRegistry.registerItem(event, "salt_crystal_block", new ItemBlockSaltCrystal(BamboozledBlocks.SALT_CRYSTAL_BLOCK));
        BamboozledRegistry.registerItem(event, "salt_crystal_block_stairs", new ItemBlockBase(BamboozledBlocks.SALT_CRYSTAL_BLOCK_STAIRS));
        BamboozledRegistry.registerItem(event, "salt_crystal_brick_stairs", new ItemBlockBase(BamboozledBlocks.SALT_CRYSTAL_BRICK_STAIRS));
        BamboozledRegistry.registerItem(event, "salt_crystal_block_slab", new ItemBlockSlabBase(BamboozledBlocks.SALT_CRYSTAL_BLOCK_SLAB));
        BamboozledRegistry.registerItem(event, "salt_crystal_brick_slab", new ItemBlockSlabBase(BamboozledBlocks.SALT_CRYSTAL_BRICK_SLAB));
        BamboozledRegistry.registerItem(event, "rope", new ItemBlockBase(BamboozledBlocks.ROPE));
        BamboozledRegistry.registerItem(event, "rope_fence", new ItemBlockBase(BamboozledBlocks.ROPE_FENCE));
        BamboozledRegistry.registerItem(event, "bamboo_chest", new ItemBlockBase(BamboozledBlocks.BAMBOO_CHEST));
        BamboozledRegistry.registerItem(event, "trapped_bamboo_chest", new ItemBlockBase(BamboozledBlocks.TRAPPED_BAMBOO_CHEST));
    }

    @SubscribeEvent
    static void onEntityRegistry(final RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityFallingSaltBlock.ENTRY.build());
        event.getRegistry().register(EntityThrownSaltPile.ENTRY.build());
    }

    @SubscribeEvent
    static void onRecipeRegistry(final RegistryEvent.Register<IRecipe> event) {
        GameRegistry.addSmelting(BamboozledItems.BAMBOO, new ItemStack(BamboozledItems.BAMBOO_DRIED, 1), 0.0F);
        GameRegistry.addSmelting(BamboozledItems.SALT_BLOCK, new ItemStack(BamboozledItems.SALT_PILE, 4), 0.0F);
        GameRegistry.addSmelting(new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 1), new ItemStack(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, 2), 0.1F);

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
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_DRIED_FENCE, 0, "fenceBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_PLANKS_FENCE, 0, "fenceWood");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_DOOR, 0, "doorBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_ORE, 0, "oreSalt", "oreHalite");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_PILE, 0, "dustSalt");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_BLOCK, 0, "blockSalt");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL, 0, "crystalSalt", "gemSalt");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 0, "blockSaltCrystal");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, "blockSaltCrystal", "blockSaltCrystalBrick");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 2, "blockSaltCrystal", "blockSaltCrystalBrick");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK, 2, "blockSaltCrystal", "blockSaltCrystalChiseled");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK_STAIRS, 0, "stairsSaltCrystal");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BRICK_STAIRS, 0, "stairsSaltCrystal");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BLOCK_SLAB, 0, "slabSaltCrystal");
        BamboozledRegistry.registerOre(BamboozledItems.SALT_CRYSTAL_BRICK_SLAB, 0, "slabSaltCrystal");
        BamboozledRegistry.registerOre(BamboozledItems.ROPE_FENCE, 0, "fenceWood", "fenceRope");
        BamboozledRegistry.registerOre(BamboozledItems.BAMBOO_CHEST, 0, "chest", "chestBamboo");
        BamboozledRegistry.registerOre(BamboozledItems.TRAPPED_BAMBOO_CHEST, 0, "chest", "chestTrapped", "chestBamboo");
    }

    private static void registerBlock(final RegistryEvent.Register<Block> event, final String name, final Block block) {
        block.setRegistryName(Bamboozled.ID, name);
        block.setTranslationKey(Bamboozled.ID + "." + name);
        block.setCreativeTab(Bamboozled.getItemGroup());
        event.getRegistry().register(block);
    }

    private static void registerBlockEntity(final Class<? extends TileEntity> clazz, final String name) {
        val key = new ResourceLocation(Bamboozled.ID, name);
        GameRegistry.registerTileEntity(clazz, key);
    }

    private static void registerItem(final RegistryEvent.Register<Item> event, final String name, final Item item) {
        item.setRegistryName(Bamboozled.ID, name);
        item.setTranslationKey(Bamboozled.ID + "." + name);
        item.setCreativeTab(Bamboozled.getItemGroup());
        event.getRegistry().register(item);
    }

    private static void registerOre(final Item item, final int meta, final String... ores) {
        val stack = new ItemStack(item, 1, meta);
        for (val ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
