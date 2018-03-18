package net.insomniakitten.bamboo;

import com.google.common.base.Equivalence;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.block.BlockBambooHopper;
import net.insomniakitten.bamboo.block.BlockBambooPlanks;
import net.insomniakitten.bamboo.block.BlockBambooPressurePlate;
import net.insomniakitten.bamboo.block.BlockBambooSlab;
import net.insomniakitten.bamboo.block.BlockBambooStairs;
import net.insomniakitten.bamboo.block.BlockBambooWall;
import net.insomniakitten.bamboo.block.BlockRope;
import net.insomniakitten.bamboo.block.BlockSalt;
import net.insomniakitten.bamboo.block.BlockSaltOre;
import net.insomniakitten.bamboo.block.BlockSaltPile;
import net.insomniakitten.bamboo.client.BlockModelMapper;
import net.insomniakitten.bamboo.client.ItemModelSupplier;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.item.ItemBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.tile.TileEntitySupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier.OreCollection;
import net.insomniakitten.bamboo.util.RegistryHolder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledRegistry {

    private static final RegistryHolder<Block> BLOCKS = new RegistryHolder<>();
    private static final RegistryHolder<Item> ITEMS = new RegistryHolder<>();

    private BamboozledRegistry() {}

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        BLOCKS.begin(event)
                .register(new BlockBamboo(), "bamboo")
                .register(new BlockBambooBundle(), "bamboo_bundle")
                .register(new BlockBambooStairs(1.0F, 5.0F), "bamboo_dried_stairs")
                .register(new BlockBambooSlab(1.0F, 5.0F), "bamboo_dried_slab")
                .register(new BlockBambooPlanks(), "bamboo_planks")
                .register(new BlockBambooStairs(2.0F, 15.0F), "bamboo_planks_stairs")
                .register(new BlockBambooSlab(2.0F, 15.0F), "bamboo_planks_slab")
                .register(new BlockBambooWall(), "bamboo_wall")
                .register(new BlockBambooPressurePlate(), "bamboo_pressure_plate")
                .register(new BlockBambooHopper(), "bamboo_hopper")
                .register(new BlockSaltOre(), "salt_ore")
                .register(new BlockSaltPile(), "salt_pile")
                .register(new BlockSalt(), "salt_block")
                .register(new BlockRope(), "rope");

        registerTileEntities();
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        RegistryHolder<Item>.Registry items = ITEMS.begin(event)
                .register(new ItemBase() {
                    @Override
                    public void getOreEntries(OreCollection oreEntries) {
                        oreEntries.put(new ItemStack(this), "bambooDried");
                    }
                }, "bamboo_dried");

        for (Block block : BLOCKS.entries()) {
            if (block instanceof ItemBlockSupplier) {
                ItemBlockSupplier provider = (ItemBlockSupplier) block;
                ItemBlock iblock = provider.getItemBlock();
                if (iblock != null) items.register(iblock);
            }
        }

        registerOreEntries();
    }

    @SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityFallingSaltBlock.INSTANCE.build());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingSaltBlock.class, RenderFallingBlock::new);
        for (Block block : BLOCKS.entries()) {
            if (block instanceof BlockModelMapper) {
                IStateMapper mapper = ((BlockModelMapper) block).getModelMapper();
                if (mapper != null) ModelLoader.setCustomStateMapper(block, mapper);
            }
        }

        for (Item item : ITEMS.entries()) {
            if (item instanceof ItemModelSupplier) {
                List<ModelResourceLocation> models = new LinkedList<>();
                ((ItemModelSupplier) item).getModels(models);
                for (int i = 0; i < models.size(); ++i) {
                    ModelResourceLocation model = models.get(i);
                    if (model != null) {
                        ModelLoader.setCustomModelResourceLocation(item, i, model);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
        // TODO Implement a JSON smelting recipe registry...
        GameRegistry.addSmelting(
                new ItemStack(BamboozledObjects.BAMBOO_ITEM),
                new ItemStack(BamboozledObjects.BAMBOO_DRIED),
                0.0F
        );
        GameRegistry.addSmelting(
                new ItemStack(BamboozledObjects.SALT_ORE),
                new ItemStack(BamboozledObjects.SALT_PILE, 4),
                0.0F
        );
    }

    private static void registerTileEntities() {
        for (Block block : BLOCKS.entries()) {
            if (block instanceof TileEntitySupplier) {
                TileEntitySupplier supplier = ((TileEntitySupplier) block);
                Class<? extends TileEntity> tile = supplier.getTileClass();
                String key = supplier.getTileKey();
                GameRegistry.registerTileEntity(tile, key);
            }
        }
    }

    private static void registerOreEntries() {
        OreCollection oreEntries = OreCollection.create();

        for (Block block : BLOCKS.entries()) {
            if (block instanceof OreEntrySupplier) {
                ((OreEntrySupplier) block).getOreEntries(oreEntries);
                for (Map.Entry<Equivalence.Wrapper<ItemStack>, List<String>> entry : oreEntries.entries()) {
                    ItemStack stack = entry.getKey().get().copy();
                    for (String ore : entry.getValue()) {
                        OreDictionary.registerOre(ore, stack);
                    }
                }
            }
        }

        for (Item item : ITEMS.entries()) {
            if (item instanceof OreEntrySupplier) {
                ((OreEntrySupplier) item).getOreEntries(oreEntries);
                for (Map.Entry<Equivalence.Wrapper<ItemStack>, List<String>> entry : oreEntries.entries()) {
                    ItemStack stack = entry.getKey().get().copy();
                    for (String ore : entry.getValue()) {
                        OreDictionary.registerOre(ore, stack);
                    }
                }
            }
        }
    }

}
