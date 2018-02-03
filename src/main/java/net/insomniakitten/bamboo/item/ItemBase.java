package net.insomniakitten.bamboo.item;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.client.ItemModelSupplier;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBase extends Item implements ItemModelSupplier, OreEntrySupplier {

    private final String variant;

    public ItemBase(String variant) {
        this.variant = variant;
        setCreativeTab(Bamboozled.TAB);
    }

    public ItemBase() {
        this("inventory");
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(Bamboozled.ID + "." + name);
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

    @Override
    public void getModels(List<ModelResourceLocation> models) {
        //noinspection ConstantConditions
        models.add(new ModelResourceLocation(getRegistryName(), variant));
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {

    }

}
