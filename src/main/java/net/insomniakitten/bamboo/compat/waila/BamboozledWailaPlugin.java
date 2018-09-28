package net.insomniakitten.bamboo.compat.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Logger;

import java.util.List;

@WailaPlugin
public final class BamboozledWailaPlugin implements IWailaPlugin {
    private static final Logger LOGGER = Bamboozled.getLogger("waila");

    @Override
    public void register(final IWailaRegistrar registrar) {
        BamboozledWailaPlugin.LOGGER.info("Registering data provider");

        registrar.registerBodyProvider(new DataProvider(), BlockBambooBundle.class);
    }

    private static final class DataProvider implements IWailaDataProvider {
        private static final String DRY_PROGRESS = "waila.bamboozled.bamboo_bundle.dry_progress";

        @Override
        public List<String> getWailaBody(final ItemStack stack, final List<String> tooltip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
            if (!Bamboozled.getConfig().isInWorldBambooDryingEnabled()) {
                return tooltip;
            }

            final IBlockState state = accessor.getBlockState();
            final int dried = state.getValue(BlockBambooBundle.DRIED);

            if (dried >= 3) {
                return tooltip;
            }

            final int progress = 33 * dried;
            final ITextComponent component = new TextComponentTranslation(DataProvider.DRY_PROGRESS, progress);
            final String line = component.getUnformattedComponentText();

            tooltip.add(line);

            return tooltip;
        }
    }
}
