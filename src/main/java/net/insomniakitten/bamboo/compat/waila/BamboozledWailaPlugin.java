package net.insomniakitten.bamboo.compat.waila;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.block.BlockBambooBundle;
import net.insomniakitten.bamboo.compat.waila.provider.ProviderBambooBundle;

@WailaPlugin
public final class BamboozledWailaPlugin implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        Bamboozled.LOGGER.debug("Registering Hwyla data providers");
        registrar.registerBodyProvider(ProviderBambooBundle.INSTANCE, BlockBambooBundle.class);
    }

}
