package net.insomniakitten.bamboo;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Bamboozled.ID)
public final class BamboozledObjects {

    @GameRegistry.ObjectHolder("bamboo")
    public static final Block BAMBOO_BLOCK = Blocks.AIR;

    @GameRegistry.ObjectHolder("bamboo_bundle")
    public static final Block BAMBOO_BUNDLE = Blocks.AIR;

    @GameRegistry.ObjectHolder("salt_ore")
    public static final Block SALT_ORE = Blocks.AIR;

    @GameRegistry.ObjectHolder("salt_block")
    public static final Block SALT_BLOCK = Blocks.AIR;

    @GameRegistry.ObjectHolder("bamboo_hopper")
    public static final Block BAMBOO_HOPPER = Blocks.AIR;

    @GameRegistry.ObjectHolder("salt_pile")
    public static final Item SALT_PILE = Items.AIR;

    @GameRegistry.ObjectHolder("rope")
    public static final Item ROPE_ITEM = Items.AIR;

    @GameRegistry.ObjectHolder("bamboo")
    public static final Item BAMBOO_ITEM = Items.AIR;

    @GameRegistry.ObjectHolder("bamboo_dried")
    public static final Item BAMBOO_DRIED = Items.AIR;

    private BamboozledObjects() {}

}
