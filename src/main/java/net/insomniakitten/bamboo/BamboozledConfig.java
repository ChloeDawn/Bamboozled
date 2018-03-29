package net.insomniakitten.bamboo;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Bamboozled.ID, name = Bamboozled.ID, category = "")
@Mod.EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledConfig {

    public static final Client CLIENT = new Client();
    public static final General GENERAL = new General();
    public static final World WORLD = new World();

    private BamboozledConfig() {}

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Bamboozled.ID.equals(event.getModID())) {
            final boolean lastFancySaltOre = CLIENT.forceFancySaltOre;
            ConfigManager.sync(Bamboozled.ID, Config.Type.INSTANCE);
            if (CLIENT.forceFancySaltOre != lastFancySaltOre) {
                Bamboozled.LOGGER.debug("Reloading renderers...");
                FMLCommonHandler.instance().reloadRenderers();
            }
        }
    }

    public static final class Client {
        @Config.Name("force_fancy_salt_ore")
        @Config.Comment({ "Should halite always render as a translucent block?",
                          "If false, halite will render solid on Fast graphics." })
        public boolean forceFancySaltOre = false;

        private Client() {}
    }

    public static final class General {
        @Config.Name("in_world_bamboo_drying")
        @Config.Comment("Should bundles of bamboo dry out over time when placed outside under the sun?")
        @Config.RequiresMcRestart
        public boolean inWorldBambooDrying = true;

        @Config.Name("salt_block_drops_itself")
        @Config.Comment("Should salt blocks drop themselves when broken? If false, they will drop 9 salt piles")
        @Config.RequiresMcRestart
        public boolean saltBlockDropsItself = false;

        @Config.Name("salt_hurts_undead")
        @Config.Comment("Should salt hurt undead mobs that walk on it?")
        @Config.RequiresMcRestart
        public boolean saltHurtsUndead = true;

        @Config.Name("throwable_salt_piles")
        @Config.Comment({ "Should piles of salt be throwable?",
                          "If \"salt_hurts_undead\" is enabled, thrown salt will also deal damage" })
        @Config.RequiresMcRestart
        public boolean throwableSaltPiles = true;

        @Config.Name("throw_requires_sneaking")
        @Config.Comment({ "Should sneaking be required to throw salt piles?",
                          "This config is unused if \"throwable_salt_piles\" is disabled" })
        @Config.RequiresMcRestart
        public boolean throwRequiresSneaking = false;

        @Config.Name("advanced_slab_interaction")
        @Config.Comment("Should slabs be breakable individually when they are a double slab?")
        @Config.RequiresMcRestart
        public boolean advancedSlabInteraction = true;

        @Config.Name("fancy_bamboo")
        @Config.Comment({ "Should the bounding box of bamboo be fancy and detailed?",
                          "When false, collision logic will also be simplified" })
        @Config.RequiresMcRestart
        public boolean fancyBamboo = true;

        private General() {}
    }

    public static final class World {
        @Config.Name("generate_bamboo")
        @Config.Comment("Should bamboo stalks be generated in tropical biomes?")
        @Config.RequiresMcRestart
        public boolean generateBamboo = true;

        @Config.Name("generate_salt_ore")
        @Config.Comment("Should halite clusters be generated underground?")
        @Config.RequiresMcRestart
        public boolean generateSaltOre = true;

        @Config.Name("salt_ore_cluster_size")
        @Config.Comment("The size of generated halite clusters")
        @Config.RequiresMcRestart
        public int saltClusterSize = 8;

        private World() {}
    }

}
