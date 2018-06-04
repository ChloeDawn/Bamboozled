package net.insomniakitten.bamboo.compat.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@WailaPlugin
public final class BamboozledWailaPlugin implements IWailaPlugin {
    public static final IWailaDataProvider BAMBOO_BUNDLE_PROVIDER = new IWailaDataProvider() {
        @Override
        @SideOnly(Side.CLIENT)
        public List<String> getWailaBody(ItemStack stack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            final BlockBambooBundle bundle = (BlockBambooBundle) BamboozledBlocks.BAMBOO_BUNDLE;
            if (bundle.isDryingEnabled()) {
                final int progress = bundle.getDryProgress(accessor.getBlockState());
                final String key = "waila.bamboozled.bamboo_bundle.dry_progress";
                if (progress < 3 && I18n.hasKey(key)) {
                    tooltip.add(I18n.format(key, 33 * progress));
                }
            }
            return tooltip;
        }
    };

    @Override
    public void register(IWailaRegistrar registrar) {
        Bamboozled.LOGGER.debug("Registering Hwyla data providers...");
        registrar.registerBodyProvider(BAMBOO_BUNDLE_PROVIDER, BlockBambooBundle.class);
    }
}
