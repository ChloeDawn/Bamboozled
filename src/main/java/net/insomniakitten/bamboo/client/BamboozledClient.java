package net.insomniakitten.bamboo.client;

import com.google.common.base.Stopwatch;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.client.render.EntityRendererThrownSaltPile;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.insomniakitten.bamboo.init.BamboozledItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Bamboozled.ID, value = Side.CLIENT)
@Log4j2(topic = Bamboozled.ID + ".client")
public final class BamboozledClient {
    private BamboozledClient() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onRegisterModels(final ModelRegistryEvent context) {
        BamboozledClient.LOGGER.info("Beginning model registration");

        val stopwatch = Stopwatch.createStarted();

        BamboozledClient.registerEntityRenderers();
        BamboozledClient.registerStateMappers();
        BamboozledClient.registerItemModels();

        val elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledClient.LOGGER.info("Model registration completed in {}ms", elapsed);
    }

    private static void registerEntityRenderers() {
        BamboozledClient.registerEntityRenderer(EntityFallingSaltBlock.class, RenderFallingBlock::new);
        BamboozledClient.registerEntityRenderer(EntityThrownSaltPile.class, EntityRendererThrownSaltPile::new);
    }

    private static void registerStateMappers() {
        BamboozledClient.registerStateMapper(BamboozledBlocks.BAMBOO, it -> {
            it.ignore(BlockBamboo.AGE, BlockBamboo.LEAVES);
            BlockBamboo.SIDES.values().forEach(it::ignore);
        });

        BamboozledClient.registerStateMapper(BamboozledBlocks.BAMBOO_DOOR, it -> it.ignore(BlockDoor.POWERED));
    }

    private static void registerItemModels() {
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_DRIED, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_BUNDLE, 0, "axis=y,dried=0");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_BUNDLE, 1, "axis=y,dried=3");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_DRIED_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_DRIED_SLAB, 0, "variant=lower");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_PLANKS, 0, "orientation=horizontal");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_PLANKS, 1, "orientation=vertical");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_PLANKS_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_PLANKS_SLAB, 0, "variant=lower");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_WALL, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_DRIED_FENCE, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_PLANKS_FENCE, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_DOOR, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_ORE, 0, "normal");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_PILE, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_BLOCK, 0, "normal");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK, 0, "variant=block");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK, 1, "variant=bricks");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK, 2, "variant=bricks_cracked");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK, 3, "variant=bricks_chiseled");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BRICK_STAIRS, 0, "facing=east,half=bottom,shape=straight");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BLOCK_SLAB, 0, "variant=lower");
        BamboozledClient.registerItemModel(BamboozledItems.SALT_CRYSTAL_BRICK_SLAB, 0, "variant=lower");
        BamboozledClient.registerItemModel(BamboozledItems.ROPE, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.ROPE_FENCE, 0, "inventory");
        BamboozledClient.registerItemModel(BamboozledItems.BAMBOO_CRATE, 0, "normal");
    }

    private static <T extends Entity> void registerEntityRenderer(final Class<T> clazz, final IRenderFactory<? super T> factory) {
        BamboozledClient.LOGGER.debug("Registering entity renderer factory for {}", clazz);

        RenderingRegistry.registerEntityRenderingHandler(clazz, factory);
    }

    private static void registerStateMapper(final Block block, final Consumer<StateMap.Builder> consumer) {
        if (Blocks.AIR == block) {
            throw new IllegalStateException("Empty block");
        }

        if (block.getRegistryName() == null) {
            throw new IllegalStateException("Missing registry name");
        }

        BamboozledClient.LOGGER.debug("Registering block model mapper for '{}'", block.getRegistryName());

        val builder = new StateMap.Builder();

        consumer.accept(builder);

        val stateMapper = builder.build();

        ModelLoader.setCustomStateMapper(block, stateMapper);
    }

    private static void registerItemModel(final Item item, final int meta, final String variant) {
        if (Items.AIR == item) {
            throw new IllegalStateException("Empty item");
        }

        @Nullable val name = item.getRegistryName();

        if (name == null) {
            throw new IllegalStateException("Missing registry name");
        }

        if (variant.isEmpty()) {
            throw new IllegalStateException("Empty variant string");
        }

        BamboozledClient.LOGGER.debug("Registering item model variant '{}' for '{}'{meta={}}", variant, item.getRegistryName(), meta);

        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, variant));
    }
}
