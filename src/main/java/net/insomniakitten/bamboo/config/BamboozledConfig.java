package net.insomniakitten.bamboo.config;

import net.insomniakitten.bamboo.Bamboozled;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = Bamboozled.ID)
@Config(modid = Bamboozled.ID, name = Bamboozled.ID, category = "")
@SuppressWarnings("RedundantFieldInitialization")
public final class BamboozledConfig {
    public static final Client CLIENT = new Client();
    public static final General GENERAL = new General();
    public static final World WORLD = new World();

    private static final Logger LOGGER = Bamboozled.getLogger("config");

    private BamboozledConfig() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onConfigurationChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Bamboozled.ID.equals(event.getModID())) {
            BamboozledConfig.LOGGER.info("Syncing configuration");
            BamboozledConfig.syncConfiguration(event.isWorldRunning());
            BamboozledConfig.LOGGER.info("Configuration synced");
        }
    }

    private static void syncConfiguration(final boolean isWorldRunning) {
        final FMLCommonHandler handler = FMLCommonHandler.instance();

        if (handler.getSide().isServer() || !isWorldRunning) {
            ConfigManager.sync(Bamboozled.ID, Type.INSTANCE);
            return;
        }

        final boolean lastFancySaltOre = Bamboozled.getClientConfig().isFancySaltOreForced();

        ConfigManager.sync(Bamboozled.ID, Type.INSTANCE);

        final boolean fancySaltOre = Bamboozled.getClientConfig().isFancySaltOreForced();

        if (fancySaltOre != lastFancySaltOre) {
            BamboozledConfig.LOGGER.info("Reloading world renderers");
            handler.reloadRenderers();
        }
    }

    public static final class Client implements ClientConfig {
        @Name("force_fancy_salt_ore")
        @Comment({ "Should halite always render as a translucent block?",
                   "If false, halite will render solid on Fast graphics." })
        public volatile boolean fancySaltOreForced = false;

        private Client() {
            BamboozledConfig.LOGGER.info("Client configuration initialized");
        }

        @Override
        public boolean isFancySaltOreForced() {
            return this.fancySaltOreForced;
        }
    }

    public static final class General implements GeneralConfig {
        @Name("in_world_bamboo_drying")
        @Comment("Should bundles of bamboo dry out over time when placed outside under the sun?")
        @RequiresMcRestart
        public boolean inWorldBambooDryingEnabled = true;

        @Name("salt_block_drops_itself")
        @Comment("Should salt blocks drop themselves when broken? If false, they will drop 9 salt piles")
        @RequiresMcRestart
        public boolean saltBlockDropsEnabled = false;

        @Name("salt_hurts_undead")
        @Comment("Should salt hurt undead mobs that walk on it?")
        @RequiresMcRestart
        public boolean saltUndeadDamageEnabled = true;

        @Name("throwable_salt_piles")
        @Comment({ "Should piles of salt be throwable?",
                   "If \"salt_hurts_undead\" is enabled, thrown salt will also deal damage" })
        @RequiresMcRestart
        public boolean throwableSaltPilesEnabled = true;

        @Name("throw_requires_sneaking")
        @Comment({ "Should sneaking be required to throw salt piles?",
                   "This config is unused if \"throwable_salt_piles\" is disabled" })
        @RequiresMcRestart
        public boolean throwSneakingRequirementEnabled = false;

        @Name("fancy_bamboo")
        @Comment({ "Should the bounding box of bamboo be fancy and detailed?",
                   "When false, collision logic will also be simplified" })
        @RequiresMcRestart
        public boolean fancyBambooEnabled = true;

        private General() {
            BamboozledConfig.LOGGER.info("General configuration initialized");
        }

        @Override
        public boolean isInWorldBambooDryingEnabled() {
            return this.inWorldBambooDryingEnabled;
        }

        @Override
        public boolean isSaltBlockDropsEnabled() {
            return this.saltBlockDropsEnabled;
        }

        @Override
        public boolean isSaltUndeadDamageEnabled() {
            return this.saltUndeadDamageEnabled;
        }

        @Override
        public boolean isThrowableSaltPilesEnabled() {
            return this.throwableSaltPilesEnabled;
        }

        @Override
        public boolean isThrowSneakingRequirementEnabled() {
            return this.throwSneakingRequirementEnabled;
        }

        @Override
        public boolean isFancyBambooEnabled() {
            return this.fancyBambooEnabled;
        }
    }

    public static final class World implements WorldConfig {
        @Name("generate_bamboo")
        @Comment("Should bamboo stalks be generated in tropical biomes?")
        @RequiresMcRestart
        public boolean bambooGenerationEnabled = true;

        @Name("generate_salt_ore")
        @Comment("Should halite clusters be generated underground?")
        @RequiresMcRestart
        public boolean saltOreGenerationEnabled = true;

        @Name("salt_ore_cluster_size")
        @Comment("The size of generated halite clusters")
        @RequiresMcRestart
        public int saltClusterSize = 8;

        private World() {
            BamboozledConfig.LOGGER.info("World configuration initialized");
        }

        @Override
        public boolean isBambooGenerationEnabled() {
            return this.bambooGenerationEnabled;
        }

        @Override
        public boolean isSaltOreGenerationEnabled() {
            return this.saltOreGenerationEnabled;
        }

        @Override
        public int getSaltClusterSize() {
            return this.saltClusterSize;
        }
    }
}
