package net.insomniakitten.bamboo;

import lombok.val;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;

public final class BamboozledIntegration {
    private BamboozledIntegration() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    static void addCharsetCarries() {
        BamboozledIntegration.addCharsetCarry(BamboozledBlocks.BAMBOO_CRATE);
    }

    private static void addCharsetCarry(final IForgeRegistryEntry<Block> block) {
        val registryName = Objects.requireNonNull(block.getRegistryName(), "registryName");
        FMLInterModComms.sendMessage("charset", "addCarry", registryName);
    }
}
