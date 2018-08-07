package net.insomniakitten.bamboo;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;

public final class BamboozledIntegration {
    private BamboozledIntegration() {}

    static void onInitialization() {
        BamboozledIntegration.addCharsetCarry(BamboozledBlocks.BAMBOO_CHEST);
        BamboozledIntegration.addCharsetCarry(BamboozledBlocks.TRAPPED_BAMBOO_CHEST);
    }

    private static void addCharsetCarry(final IForgeRegistryEntry<Block> block) {
        val registryName = Objects.requireNonNull(block.getRegistryName(), "registryName");
        FMLInterModComms.sendMessage("charset", "addCarry", registryName);
    }
}
