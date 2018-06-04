package net.insomniakitten.bamboo;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = Bamboozled.ID)
@Config(modid = Bamboozled.ID, name = Bamboozled.ID, category = "")
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
        @Name("force_fancy_salt_ore")
        @Comment({ "Should halite always render as a translucent block?",
                   "If false, halite will render solid on Fast graphics." })
        public boolean forceFancySaltOre = false;

        private Client() {}
    }

    public static final class General {
        @Name("in_world_bamboo_drying")
        @Comment("Should bundles of bamboo dry out over time when placed outside under the sun?")
        @RequiresMcRestart
        public boolean inWorldBambooDrying = true;

        @Name("salt_block_drops_itself")
        @Comment("Should salt blocks drop themselves when broken? If false, they will drop 9 salt piles")
        @RequiresMcRestart
        public boolean saltBlockDropsItself = false;

        @Name("salt_hurts_undead")
        @Comment("Should salt hurt undead mobs that walk on it?")
        @RequiresMcRestart
        public boolean saltHurtsUndead = true;

        @Name("throwable_salt_piles")
        @Comment({ "Should piles of salt be throwable?",
                   "If \"salt_hurts_undead\" is enabled, thrown salt will also deal damage" })
        @RequiresMcRestart
        public boolean throwableSaltPiles = true;

        @Name("throw_requires_sneaking")
        @Comment({ "Should sneaking be required to throw salt piles?",
                   "This config is unused if \"throwable_salt_piles\" is disabled" })
        @RequiresMcRestart
        public boolean throwRequiresSneaking = false;

        @Name("fancy_bamboo")
        @Comment({ "Should the bounding box of bamboo be fancy and detailed?",
                   "When false, collision logic will also be simplified" })
        @RequiresMcRestart
        public boolean fancyBamboo = true;

        private General() {}
    }

    public static final class World {
        @Name("generate_bamboo")
        @Comment("Should bamboo stalks be generated in tropical biomes?")
        @RequiresMcRestart
        public boolean generateBamboo = true;

        @Name("generate_salt_ore")
        @Comment("Should halite clusters be generated underground?")
        @RequiresMcRestart
        public boolean generateSaltOre = true;

        @Name("salt_ore_cluster_size")
        @Comment("The size of generated halite clusters")
        @RequiresMcRestart
        public int saltClusterSize = 8;

        private World() {}
    }
}
