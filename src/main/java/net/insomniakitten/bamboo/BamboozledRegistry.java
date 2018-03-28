package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.block.BlockBambooWall;
import net.insomniakitten.bamboo.block.BlockRope;
import net.insomniakitten.bamboo.block.BlockSalt;
import net.insomniakitten.bamboo.block.BlockSaltOre;
import net.insomniakitten.bamboo.block.BlockSaltPile;
import net.insomniakitten.bamboo.block.base.BlockPlanksBase;
import net.insomniakitten.bamboo.block.base.BlockSlabBase;
import net.insomniakitten.bamboo.block.base.BlockStairsBase;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.item.ItemBambooBundle;
import net.insomniakitten.bamboo.item.ItemBase;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockPlanksBase;
import net.insomniakitten.bamboo.item.ItemBlockSlabBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledRegistry {

    private BamboozledRegistry() {}

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        Bamboozled.LOGGER.debug("Registering blocks...");
        event.getRegistry().registerAll(
                new BlockBamboo().setRegistryName("bamboo").setUnlocalizedName("bamboo"),
                new BlockBambooBundle().setRegistryName("bamboo_bundle").setUnlocalizedName("bamboo_bundle"),
                new BlockStairsBase(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F).setRegistryName("bamboo_dried_stairs").setUnlocalizedName("bamboo_dried_stairs"),
                new BlockSlabBase(Material.WOOD, SoundType.WOOD, 1.0F, 5.0F).setRegistryName("bamboo_dried_slab").setUnlocalizedName("bamboo_dried_slab"),
                new BlockPlanksBase().setRegistryName("bamboo_planks").setUnlocalizedName("bamboo_planks"),
                new BlockStairsBase(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F).setRegistryName("bamboo_planks_stairs").setUnlocalizedName("bamboo_planks_stairs"),
                new BlockSlabBase(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F).setRegistryName("bamboo_planks_slab").setUnlocalizedName("bamboo_planks_slab"),
                new BlockBambooWall().setRegistryName("bamboo_wall").setUnlocalizedName("bamboo_wall"),
                new BlockSaltOre().setRegistryName("salt_ore").setUnlocalizedName("salt_ore"),
                new BlockSaltPile().setRegistryName("salt_pile").setUnlocalizedName("salt_pile"),
                new BlockSalt().setRegistryName("salt_block").setUnlocalizedName("salt_block"),
                new BlockRope().setRegistryName("rope").setUnlocalizedName("rope")
        );
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        Bamboozled.LOGGER.debug("Registering items...");
        event.getRegistry().registerAll(
                new ItemBlockBase(BamboozledBlocks.BAMBOO).setRegistryName("bamboo"),
                new ItemBase().setRegistryName("bamboo_dried").setUnlocalizedName("bamboo_dried"),
                new ItemBambooBundle(BamboozledBlocks.BAMBOO_BUNDLE).setRegistryName("bamboo_bundle"),
                new ItemBlockBase(BamboozledBlocks.BAMBOO_DRIED_STAIRS).setRegistryName("bamboo_dried_stairs"),
                new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_DRIED_SLAB).setRegistryName("bamboo_dried_slab"),
                new ItemBlockPlanksBase(BamboozledBlocks.BAMBOO_PLANKS).setRegistryName("bamboo_planks"),
                new ItemBlockBase(BamboozledBlocks.BAMBOO_PLANKS_STAIRS).setRegistryName("bamboo_planks_stairs"),
                new ItemBlockSlabBase(BamboozledBlocks.BAMBOO_PLANKS_SLAB).setRegistryName("bamboo_planks_slab"),
                new ItemBlockBase(BamboozledBlocks.BAMBOO_WALL).setRegistryName("bamboo_wall"),
                new ItemBlockBase(BamboozledBlocks.SALT_ORE).setRegistryName("salt_ore"),
                new ItemBlockBase(BamboozledBlocks.SALT_PILE).setRegistryName("salt_pile"),
                new ItemBlockBase(BamboozledBlocks.SALT_BLOCK).setRegistryName("salt_block"),
                new ItemBlockBase(BamboozledBlocks.ROPE).setRegistryName("rope")
        );
    }

    @SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityEntry> event) {
        Bamboozled.LOGGER.debug("Registering entity entries...");
        event.getRegistry().register(EntityFallingSaltBlock.INSTANCE.build());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public static void onModelRegistry(ModelRegistryEvent event) {
        Bamboozled.LOGGER.debug("Registering entity renderers...");
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingSaltBlock.class, RenderFallingBlock::new);

        Bamboozled.LOGGER.debug("Registering state mappers...");
        ModelLoader.setCustomStateMapper(BamboozledBlocks.BAMBOO, new StateMap.Builder().ignore(BlockBamboo.PROP_AGE).build());

        Bamboozled.LOGGER.debug("Registering item models...");
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO, 0, new ModelResourceLocation(BamboozledItems.BAMBOO.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_DRIED, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_DRIED.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_BUNDLE, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_BUNDLE.getRegistryName(), "axis=y,dried=0"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_BUNDLE, 1, new ModelResourceLocation(BamboozledItems.BAMBOO_BUNDLE.getRegistryName(), "axis=y,dried=3"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_DRIED_STAIRS.getRegistryName(), "facing=east,half=bottom,shape=straight"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_DRIED_SLAB, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_DRIED_SLAB.getRegistryName(), "variant=lower"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_PLANKS, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_PLANKS.getRegistryName(), "orientation=horizontal"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_PLANKS, 1, new ModelResourceLocation(BamboozledItems.BAMBOO_PLANKS.getRegistryName(), "orientation=vertical"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_PLANKS_STAIRS.getRegistryName(), "facing=east,half=bottom,shape=straight"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_PLANKS_SLAB.getRegistryName(), "variant=lower"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.BAMBOO_WALL, 0, new ModelResourceLocation(BamboozledItems.BAMBOO_WALL.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.SALT_ORE, 0, new ModelResourceLocation(BamboozledItems.SALT_ORE.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.SALT_PILE, 0, new ModelResourceLocation(BamboozledItems.SALT_PILE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.SALT_BLOCK, 0, new ModelResourceLocation(BamboozledItems.SALT_BLOCK.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(BamboozledItems.ROPE, 0, new ModelResourceLocation(BamboozledItems.ROPE.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
        Bamboozled.LOGGER.debug("Registering ore dictionary entries...");
        OreDictionary.registerOre("bamboo", BamboozledItems.BAMBOO);
        OreDictionary.registerOre("blockBamboo", BamboozledItems.BAMBOO_BUNDLE);
        OreDictionary.registerOre("blockBamboo", new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 1));
        OreDictionary.registerOre("blockBambooDried", new ItemStack(BamboozledItems.BAMBOO_BUNDLE, 1, 1));
        OreDictionary.registerOre("stairBamboo", BamboozledItems.BAMBOO_DRIED_STAIRS);
        OreDictionary.registerOre("stairWood", BamboozledItems.BAMBOO_DRIED_STAIRS);
        OreDictionary.registerOre("slabBamboo", BamboozledItems.BAMBOO_DRIED_SLAB);
        OreDictionary.registerOre("slabWood", BamboozledItems.BAMBOO_DRIED_SLAB);
        OreDictionary.registerOre("plankBamboo", BamboozledItems.BAMBOO_PLANKS);
        OreDictionary.registerOre("plankWood", BamboozledItems.BAMBOO_PLANKS);
        OreDictionary.registerOre("plankBamboo", new ItemStack(BamboozledItems.BAMBOO_PLANKS, 1, 1));
        OreDictionary.registerOre("plankWood", new ItemStack(BamboozledItems.BAMBOO_PLANKS, 1, 1));
        OreDictionary.registerOre("plankBambooVertical", new ItemStack(BamboozledItems.BAMBOO_PLANKS, 1, 1));
        OreDictionary.registerOre("plankWoodVertical", new ItemStack(BamboozledItems.BAMBOO_PLANKS, 1, 1));
        OreDictionary.registerOre("stairWood", BamboozledItems.BAMBOO_PLANKS_STAIRS);
        OreDictionary.registerOre("slabWood", BamboozledItems.BAMBOO_PLANKS_SLAB);
        OreDictionary.registerOre("wallBamboo", BamboozledItems.BAMBOO_WALL);
        OreDictionary.registerOre("oreSalt", BamboozledItems.SALT_ORE);
        OreDictionary.registerOre("oreHalite", BamboozledItems.SALT_ORE);
        OreDictionary.registerOre("dustSalt", BamboozledItems.SALT_PILE);
        OreDictionary.registerOre("blockSalt", BamboozledItems.SALT_BLOCK);

        Bamboozled.LOGGER.debug("Registering furnace smelting recipes...");
        GameRegistry.addSmelting(BamboozledItems.BAMBOO, new ItemStack(BamboozledItems.BAMBOO_DRIED), 0.0F);
        GameRegistry.addSmelting(BamboozledBlocks.SALT_ORE, new ItemStack(BamboozledItems.SALT_PILE, 4), 0.0F);
    }

}
