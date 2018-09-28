package net.insomniakitten.bamboo;

import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;

public final class BamboozledIntegration {
    private BamboozledIntegration() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    static void init() {
        Charset.init();
    }

    private static final class Charset {
        private static final String ID = "charset";
        private static final String ADD_CARRY = "addCarry";

        private static void init() {
            Charset.addCarry(BamboozledBlocks.BAMBOO_CRATE);
        }

        private static void addCarry(final Block block) {
            if (Blocks.AIR == block) {
                throw new IllegalArgumentException("Empty block");
            }

            @Nullable
            final ResourceLocation name = block.getRegistryName();

            if (name == null) {
                throw new IllegalArgumentException("Missing registry name");
            }

            FMLInterModComms.sendMessage(Charset.ID, Charset.ADD_CARRY, name);
        }
    }
}
