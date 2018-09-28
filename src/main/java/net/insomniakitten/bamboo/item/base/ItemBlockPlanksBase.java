package net.insomniakitten.bamboo.item.base;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockPlanksBase extends ItemSubBlockBase {
    public ItemBlockPlanksBase(final Block block) {
        super(block);
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        final String name = this.getTranslationKey();

        return stack.getMetadata() > 0 ? name + "_vertical" : name;
    }
}
