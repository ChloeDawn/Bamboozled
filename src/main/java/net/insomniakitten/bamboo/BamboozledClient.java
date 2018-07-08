package net.insomniakitten.bamboo;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.entity.render.RenderThrownSaltPile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.function.Consumer;

@UtilityClass
@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Bamboozled.ID, value = Side.CLIENT)
public class BamboozledClient {
    @SubscribeEvent
    void onModelRegistry(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingSaltBlock.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownSaltPile.class, RenderThrownSaltPile::new);

        registerMapper(BamboozledBlocks.BAMBOO, mapper -> {
            mapper.ignore(BlockBamboo.PROP_AGE, BlockBamboo.PROP_LEAVES);
            BlockBamboo.PROP_SIDES.values().forEach(mapper::ignore);
        });

        registerMapper(BamboozledBlocks.BAMBOO_DOOR, mapper ->
            mapper.ignore(BlockDoor.POWERED)
        );

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

    private void registerMapper(Block block, Consumer<StateMap.Builder> consumer) {
        val builder = new StateMap.Builder();
        consumer.accept(builder);
        ModelLoader.setCustomStateMapper(block, builder.build());
    }

    private void registerModel(Item item, int meta, String variant) {
        val name = Objects.requireNonNull(item.getRegistryName());
        val model = new ModelResourceLocation(name, variant);
        ModelLoader.setCustomModelResourceLocation(item, meta, model);
    }
}
