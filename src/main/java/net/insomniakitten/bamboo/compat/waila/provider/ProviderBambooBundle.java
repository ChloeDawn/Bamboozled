package net.insomniakitten.bamboo.compat.waila.provider;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.insomniakitten.bamboo.BamboozledObjects;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public final class ProviderBambooBundle implements IWailaDataProvider {

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack stack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        BlockBambooBundle bundle = (BlockBambooBundle) BamboozledObjects.BAMBOO_BUNDLE;
        int progress = bundle.getDryProgress(accessor.getBlockState());
        if (bundle.isDryingEnabled() && progress < 3) {
            String key = "waila.bamboozled.bamboo_bundle.dry_progress";
            tooltip.add(I18n.format(key, 33 * progress));
        }
        return tooltip;
    }

}
