package net.insomniakitten.bamboo.item;

import lombok.experimental.var;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.item.base.ItemSubBlockBase;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public final class ItemBambooBundle extends ItemSubBlockBase {
    public ItemBambooBundle(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (Bamboozled.getConfig().isInWorldBambooDryingEnabled() || BlockBambooBundle.isDry(stack.getMetadata())) {
            super.addInformation(stack, world, tooltip, flag);
        }
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        return BlockBambooBundle.isDry(stack.getMetadata()) ? 288 : -1;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        var name = super.getTranslationKey(stack);
        if (BlockBambooBundle.isDry(stack.getMetadata())) {
            name += "_dried";
        }
        return name;
    }
}
