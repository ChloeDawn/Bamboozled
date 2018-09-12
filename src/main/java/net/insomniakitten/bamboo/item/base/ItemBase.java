package net.insomniakitten.bamboo.item.base;

import lombok.val;
import lombok.var;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBase extends Item {
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        val key = this.getTranslationKey(stack);

        if (I18n.hasKey(key + ".desc")) {
            tooltip.add(I18n.format(key + ".desc"));
            return;
        }

        for (var i = 0; I18n.hasKey(key + ".desc." + i); ++i) {
            tooltip.add(I18n.format(key + ".desc." + i));
        }
    }
}
