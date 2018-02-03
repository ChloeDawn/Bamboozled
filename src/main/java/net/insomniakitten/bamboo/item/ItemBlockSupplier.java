package net.insomniakitten.bamboo.item;

import net.minecraft.item.ItemBlock;

import javax.annotation.Nullable;

public interface ItemBlockSupplier {

    @Nullable
    ItemBlock getItemBlock();

}
