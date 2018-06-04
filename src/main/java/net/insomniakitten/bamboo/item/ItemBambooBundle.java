package net.insomniakitten.bamboo.item;

import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
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
        if (getBambooBundle().isDryingEnabled() || getBambooBundle().isDry(stack.getMetadata())) {
            super.addInformation(stack, world, tooltip, flag);
        }
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        return getBambooBundle().isDry(stack.getMetadata()) ? 288 : -1;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = super.getUnlocalizedName(stack);
        if (getBambooBundle().isDry(stack.getMetadata())) {
            name += "_dried";
        }
        return name;
    }

    BlockBambooBundle getBambooBundle() {
        return (BlockBambooBundle) BamboozledBlocks.BAMBOO_BUNDLE;
    }
}
