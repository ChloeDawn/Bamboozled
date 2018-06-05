package net.insomniakitten.bamboo.item;

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
    public ItemBlockBase(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getUnlocalizedName(stack) + ".desc")) {
            tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc"));
        } else for (int i = 0; I18n.hasKey(getUnlocalizedName(stack) + ".desc" + i); ++i) {
            tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc" + i));
        }
    }
}
