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
        BlockBambooBundle bundle = (BlockBambooBundle) BamboozledBlocks.BAMBOO_BUNDLE;
        if (bundle.isDryingEnabled() || bundle.isDry(stack.getMetadata())) {
            super.addInformation(stack, world, tooltip, flag);
        }
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        BlockBambooBundle bundle = (BlockBambooBundle) BamboozledBlocks.BAMBOO_BUNDLE;
        return bundle.isDry(stack.getMetadata()) ? 288 : 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        BlockBambooBundle bundle = (BlockBambooBundle) BamboozledBlocks.BAMBOO_BUNDLE;
        String name = super.getUnlocalizedName(stack);
        if (bundle.isDry(stack.getMetadata())) {
            name += "_dried";
        }
        return name;
    }

}
