package net.insomniakitten.bamboo.item;

import lombok.experimental.var;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockPlanksBase extends ItemSubBlockBase {
    public ItemBlockPlanksBase(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        var name = getUnlocalizedName();
        if (stack.getMetadata() > 0) {
            name += "_vertical";
        }
        return name;
    }
}
