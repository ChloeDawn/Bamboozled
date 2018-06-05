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
import net.insomniakitten.bamboo.entity.render.RenderThrownSaltPile;
import net.insomniakitten.bamboo.item.ItemBambooBundle;
import net.insomniakitten.bamboo.item.base.ItemBase;
import net.insomniakitten.bamboo.item.base.ItemBlockBase;
import net.insomniakitten.bamboo.item.base.ItemBlockDoorBase;
import net.insomniakitten.bamboo.item.base.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.base.ItemBlockSlabBase;
import net.insomniakitten.bamboo.item.ItemSaltPile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

@UtilityClass
@EventBusSubscriber(modid = Bamboozled.ID)
public class BamboozledRegistry {
    @SubscribeEvent
    protected void onBlockRegistry(RegistryEvent.Register<Block> event) {
        Bamboozled.LOGGER.debug("Registering blocks...");
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
    protected void onItemRegistry(RegistryEvent.Register<Item> event) {
        Bamboozled.LOGGER.debug("Registering items...");
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
    protected void onEntityRegistry(RegistryEvent.Register<EntityEntry> event) {
        Bamboozled.LOGGER.debug("Registering entity entries...");
        event.getRegistry().register(EntityFallingSaltBlock.ENTRY.build());
        event.getRegistry().register(EntityThrownSaltPile.ENTRY.build());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    protected void onModelRegistry(ModelRegistryEvent event) {
        Bamboozled.LOGGER.debug("Registering entity renderers...");
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingSaltBlock.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownSaltPile.class, RenderThrownSaltPile::new);

        Bamboozled.LOGGER.debug("Registering state mappers...");
        registerMapper(BamboozledBlocks.BAMBOO, BlockBamboo.PROP_AGE);
        registerMapper(BamboozledBlocks.BAMBOO_DOOR, BlockDoor.POWERED);

        Bamboozled.LOGGER.debug("Registering item models...");
        registerModel(BamboozledItems.BAMBOO, 0, "inventory");
        registerModel(BamboozledItems.BAMBOO_DRIED, 0, "inventory");
        registerModel(BamboozledItems.BAMBOO_BUNDLE, 0, "axis=y,dried=0");
        registerModel(BamboozledItems.BAMBOO_BUNDLE, 1, "axis=y,dried=3");
        registerModel(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        registerModel(BamboozledItems.BAMBOO_DRIED_SLAB, 0, "variant=lower");
        registerModel(BamboozledItems.BAMBOO_PLANKS, 0, "orientation=horizontal");
        registerModel(BamboozledItems.BAMBOO_PLANKS, 1, "orientation=vertical");
        registerModel(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        registerModel(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, "variant=lower");
        registerModel(BamboozledItems.BAMBOO_WALL, 0, "inventory");
        registerModel(BamboozledItems.BAMBOO_DOOR, 0, "inventory");
        registerModel(BamboozledItems.SALT_ORE, 0, "normal");
        registerModel(BamboozledItems.SALT_PILE, 0, "inventory");
        registerModel(BamboozledItems.SALT_BLOCK, 0, "normal");
        registerModel(BamboozledItems.ROPE, 0, "inventory");
    }

    @SubscribeEvent
    protected void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
        Bamboozled.LOGGER.debug("Registering ore dictionary entries...");
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

        Bamboozled.LOGGER.debug("Registering furnace smelting recipes...");
        GameRegistry.addSmelting(BamboozledItems.BAMBOO, new ItemStack(BamboozledItems.BAMBOO_DRIED), 0.0F);
        GameRegistry.addSmelting(BamboozledBlocks.SALT_ORE, new ItemStack(BamboozledItems.SALT_PILE, 4), 0.0F);
    }

    private void registerBlock(RegistryEvent.Register<Block> event, String name, Block block) {
        block.setRegistryName(Bamboozled.ID, name);
        block.setUnlocalizedName(Bamboozled.ID + "." + name);
        block.setCreativeTab(Bamboozled.TAB);
        event.getRegistry().register(block);
    }

    private void registerItem(RegistryEvent.Register<Item> event, String name, Item item) {
        item.setRegistryName(Bamboozled.ID, name);
        item.setUnlocalizedName(Bamboozled.ID + "." + name);
        item.setCreativeTab(Bamboozled.TAB);
        event.getRegistry().register(item);
    }

    @SideOnly(Side.CLIENT)
    private void registerMapper(Block block, IProperty property) {
        val mapper = new StateMap.Builder().ignore(property).build();
        ModelLoader.setCustomStateMapper(block, mapper);
    }

    @SideOnly(Side.CLIENT)
    private void registerModel(Item item, int meta, String variant) {
        val name = Objects.requireNonNull(item.getRegistryName());
        val model = new ModelResourceLocation(name, variant);
        ModelLoader.setCustomModelResourceLocation(item, meta, model);
    }

    private void registerOre(Item item, int meta, String... ores) {
        val stack = new ItemStack(item, 1, meta);
        for (val ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
