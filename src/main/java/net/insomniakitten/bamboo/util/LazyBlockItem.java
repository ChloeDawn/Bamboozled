package net.insomniakitten.bamboo.util;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public final class LazyBlockItem implements Supplier<ItemBlock> {
    private final Block block;

    @Nullable
    private ItemBlock item;

    public LazyBlockItem(final Block block) {
        this.block = block;
    }

    @Override
    public ItemBlock get() {
        return this.getItem();
    }

    private ItemBlock getItem() {
        if (this.item == null) {
            if (this.block.getRegistryName() == null) {
                throw new IllegalArgumentException("Block must be registered");
            }

            this.setItem();
        }

        return this.item;
    }

    private void setItem() {
        val item = Item.getItemFromBlock(this.block);

        if (Items.AIR == item) {
            val name = Objects.requireNonNull(this.block.getRegistryName());
            throw new IllegalStateException("Missing an item mapping: " + name);
        }

        if (!(item instanceof ItemBlock)) {
            @Nullable val name = item.getRegistryName();
            val clazz = item.getClass().getName();
            throw new IllegalStateException("Not a block item: '" + name + "' " + clazz);
        }

        this.item = (ItemBlock) item;
    }
}
