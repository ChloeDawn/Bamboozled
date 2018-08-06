package net.insomniakitten.bamboo.compat.waila;

import lombok.val;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

@WailaPlugin
public final class BamboozledWailaPlugin implements IWailaPlugin {
    @Override
    public void register(final IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new DataProvider(), BlockBambooBundle.class);
    }

    private static final class DataProvider implements IWailaDataProvider {
        @Override
        public List<String> getWailaBody(final ItemStack stack, final List<String> tooltip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
            if (Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
                val state = accessor.getBlockState();
                val progress = state.getValue(BlockBambooBundle.PROP_DRIED);
                if (progress < 3) {
                    val key = "waila.bamboozled.bamboo_bundle.dry_progress";
                    val txt = new TextComponentTranslation(key, 33 * progress);
                    tooltip.add(txt.getUnformattedComponentText());
                }
            }
            return tooltip;
        }
    }
}
