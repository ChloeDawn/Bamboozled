package net.insomniakitten.bamboo.util;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyBlockItem<T extends Item> implements Supplier<T> {
    private final Block block;
    private final Function<Block, T> function;

    @Nullable
    private T item;

    private LazyBlockItem(final Block block, final Function<Block, T> function) {
        this.block = block;
        this.function = function;
    }

    public static <T extends Item> Supplier<T> of(final Block block) {
        return new LazyBlockItem<>(block, LazyBlockItem::lookupTypedItem);
    }

    public static <T extends Item> Supplier<T> of(final Block block, final Function<Block, T> function) {
        return new LazyBlockItem<>(block, function);
    }

    @Override
    public T get() {
        return this.getItem();
    }

    private T getItem() {
        if (this.item == null) {
            if (this.block.getRegistryName() == null) {
                throw new IllegalArgumentException("Block must be registered");
            }

            this.setItem();
        }

        return this.item;
    }

    private void setItem() {
        val item = this.function.apply(this.block);

        if (Items.AIR == item) {
            val name = Objects.requireNonNull(this.block.getRegistryName());

            throw new IllegalStateException("Missing an item mapping: " + name);
        }

        this.item = item;
    }

    private static <T> T lookupTypedItem(final Block block) {
        final Item item = Item.getItemFromBlock(block);

        if (Items.AIR == item) {
            val name = Objects.requireNonNull(block.getRegistryName());

            throw new IllegalStateException("Missing an item mapping: " + name);
        }

        final T typedItem;

        try {
            typedItem = (T) item;
        } catch (final ClassCastException e) {
            final Method method;

            try {
                method = LazyBlockItem.class.getDeclaredMethod("lookupTypedItem", Block.class);
            } catch (NoSuchMethodException e1) {
                // This should never happen. If it does, the universe probably imploded
                throw new AssertionError("Existential crisis, could not find self", e1);
            }

            val type = method.getGenericReturnType().getTypeName();
            val name = Objects.requireNonNull(block.getRegistryName());

            throw new IllegalStateException("Item mapping for '" + name + "' cannot be cast to '" + type + "'");
        }

        return typedItem;
    }
}
