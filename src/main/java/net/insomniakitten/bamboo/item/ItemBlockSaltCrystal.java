package net.insomniakitten.bamboo.item;

import lombok.val;
import net.insomniakitten.bamboo.item.base.ItemSubBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public final class ItemBlockSaltCrystal extends ItemSubBlockBase {
    public ItemBlockSaltCrystal(final Block block) {
        super(block);
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        val name = this.getTranslationKey();
        switch (stack.getMetadata()) {
            case 1: return name + "_bricks";
            case 2: return name + "_bricks_cracked";
            case 3: return name + "_bricks_chiseled";
        }
        return name;
    }
}
