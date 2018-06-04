package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.block.BlockBambooDoor;
import net.insomniakitten.bamboo.block.BlockBambooWall;
import net.insomniakitten.bamboo.block.BlockRope;
import net.insomniakitten.bamboo.block.BlockSalt;
import net.insomniakitten.bamboo.block.BlockSaltOre;
import net.insomniakitten.bamboo.block.BlockSaltPile;
import net.insomniakitten.bamboo.block.base.BlockPlanksBase;
import net.insomniakitten.bamboo.block.base.BlockSlabBase;
import net.insomniakitten.bamboo.block.base.BlockStairsBase;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.entity.render.RenderThrownSaltPile;
import net.insomniakitten.bamboo.item.ItemBambooBundle;
import net.insomniakitten.bamboo.item.ItemBase;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockDoorBase;
import net.insomniakitten.bamboo.item.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.ItemBlockSlabBase;
import net.insomniakitten.bamboo.item.ItemSaltPile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledRegistry {
    private BamboozledRegistry() {}

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        Bamboozled.LOGGER.debug("Registering blocks...");
        register(event, "bamboo", new BlockBamboo());
        register(event, "bamboo_bundle", new BlockBambooBundle());
        register(event, "bamboo_dried_stairs", new BlockStairsBase(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        register(event, "bamboo_dried_slab", new BlockSlabBase(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F));
        register(event, "bamboo_planks", new BlockPlanksBase());
        register(event, "bamboo_planks_stairs", new BlockStairsBase(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        register(event, "bamboo_planks_slab", new BlockSlabBase(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F));
        register(event, "bamboo_wall", new BlockBambooWall());
        register(event, "bamboo_door", new BlockBambooDoor());
        register(event, "salt_ore", new BlockSaltOre());
        register(event, "salt_pile", new BlockSaltPile());
        register(event, "salt_block", new BlockSalt());
        register(event, "rope", new BlockRope());
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        Bamboozled.LOGGER.debug("Registering items...");
        register(event, "bamboo", new ItemBlockBase(BamboozledBlocks.BAMBOO));
        register(event, "bamboo_dried", new ItemBase());
        register(event, "bamboo_bundle", new ItemBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE));
        register(event, "bamboo_dried_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS));
        register(event, "bamboo_dried_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB));
        register(event, "bamboo_planks", new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS));
        register(event, "bamboo_planks_stairs", new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS));
        register(event, "bamboo_planks_slab", new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB));
        register(event, "bamboo_wall", new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL));
        register(event, "bamboo_door", new ItemBlockDoorBase(BamboozledBlocks.BAMBOO_DOOR));
        register(event, "salt_ore", new ItemBlockBase(BamboozledBlocks.SALT_ORE));
        register(event, "salt_pile", new ItemSaltPile(BamboozledBlocks.SALT_PILE));
        register(event, "salt_block", new ItemBlockBase(BamboozledBlocks.SALT_BLOCK));
        register(event, "rope", new ItemBlockBase(BamboozledBlocks.ROPE));
    }

    @SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityEntry> event) {
        Bamboozled.LOGGER.debug("Registering entity entries...");
        event.getRegistry().register(EntityFallingSaltBlock.ENTRY.build());
        event.getRegistry().register(EntityThrownSaltPile.ENTRY.build());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public static void onModelRegistry(ModelRegistryEvent event) {
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
    public static void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
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

    private static void register(RegistryEvent.Register<Block> event, String name, Block block) {
        final ResourceLocation registryName = new ResourceLocation(Bamboozled.ID, name);
        event.getRegistry().register(block.setRegistryName(registryName).setUnlocalizedName(name));
    }

    private static void register(RegistryEvent.Register<Item> event, String name, Item item) {
        final ResourceLocation registryName = new ResourceLocation(Bamboozled.ID, name);
        event.getRegistry().register(item.setRegistryName(registryName).setUnlocalizedName(name));
    }

    @SideOnly(Side.CLIENT)
    private static void registerMapper(Block block, IProperty property) {
        final IStateMapper mapper = new StateMap.Builder().ignore(property).build();
        ModelLoader.setCustomStateMapper(block, mapper);
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item, int meta, String variant) {
        final ResourceLocation name = Objects.requireNonNull(item.getRegistryName());
        final ModelResourceLocation model = new ModelResourceLocation(name, variant);
        ModelLoader.setCustomModelResourceLocation(item, meta, model);
    }

    private static void registerOre(Item item, int meta, String... ores) {
        final ItemStack stack = new ItemStack(item, 1, meta);
        for (final String ore : ores) {
            OreDictionary.registerOre(ore, stack);
        }
    }
}
