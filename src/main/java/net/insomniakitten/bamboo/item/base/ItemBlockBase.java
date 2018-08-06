package net.insomniakitten.bamboo.item.base;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(final Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World world, final List<String> tooltip, final ITooltipFlag flag) {
        val key = this.getTranslationKey(stack);
        if (I18n.hasKey(key + ".desc")) {
            tooltip.add(I18n.format(key + ".desc"));
        } else {
            for (int i = 0; I18n.hasKey(key + ".desc" + i); ++i) {
                tooltip.add(I18n.format(key + ".desc" + i));
            }
        }
    }
}
