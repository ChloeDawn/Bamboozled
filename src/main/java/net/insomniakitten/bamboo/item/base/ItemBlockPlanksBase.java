package net.insomniakitten.bamboo.item.base;

import lombok.experimental.var;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockPlanksBase extends ItemSubBlockBase {
    public ItemBlockPlanksBase(final Block block) {
        super(block);
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        var name = this.getTranslationKey();
        if (stack.getMetadata() > 0) {
            name += "_vertical";
        }
        return name;
    }
}
