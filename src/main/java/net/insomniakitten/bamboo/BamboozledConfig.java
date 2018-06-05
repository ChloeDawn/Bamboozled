package net.insomniakitten.bamboo;

import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@UtilityClass
@EventBusSubscriber(modid = Bamboozled.ID)
@Config(modid = Bamboozled.ID, name = Bamboozled.ID, category = "")
public class BamboozledConfig {
    protected final Client CLIENT = new Client();
    protected final General GENERAL = new General();
    protected final World WORLD = new World();

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Bamboozled.ID.equals(event.getModID())) {
            val lastFancySaltOre = CLIENT.isFancySaltOreForced();
            ConfigManager.sync(Bamboozled.ID, Config.Type.INSTANCE);
            if (CLIENT.isFancySaltOreForced() != lastFancySaltOre) {
                Bamboozled.LOGGER.debug("Reloading renderers...");
                FMLCommonHandler.instance().reloadRenderers();
            }
        }
    }

    @Data
    public final class Client {
        @Name("force_fancy_salt_ore")
        @Comment({ "Should halite always render as a translucent block?",
                   "If false, halite will render solid on Fast graphics." })
        private boolean fancySaltOreForced = false;
    }

    @Data
    public final class General {
        @Name("in_world_bamboo_drying")
        @Comment("Should bundles of bamboo dry out over time when placed outside under the sun?")
        @RequiresMcRestart
        private boolean inWorldBambooDryingEnabled = true;

        @Name("salt_block_drops_itself")
        @Comment("Should salt blocks drop themselves when broken? If false, they will drop 9 salt piles")
        @RequiresMcRestart
        private boolean saltBlockDropsEnabled = false;

        @Name("salt_hurts_undead")
        @Comment("Should salt hurt undead mobs that walk on it?")
        @RequiresMcRestart
        private boolean saltUndeadDamageEnabled = true;

        @Name("throwable_salt_piles")
        @Comment({ "Should piles of salt be throwable?",
                   "If \"salt_hurts_undead\" is enabled, thrown salt will also deal damage" })
        @RequiresMcRestart
        private boolean throwableSaltPilesEnabled = true;

        @Name("throw_requires_sneaking")
        @Comment({ "Should sneaking be required to throw salt piles?",
                   "This config is unused if \"throwable_salt_piles\" is disabled" })
        @RequiresMcRestart
        private boolean throwSneakingRequirementEnabled = false;

        @Name("fancy_bamboo")
        @Comment({ "Should the bounding box of bamboo be fancy and detailed?",
                   "When false, collision logic will also be simplified" })
        @RequiresMcRestart
        private boolean fancyBambooEnabled = true;
    }

    @Data
    public final class World {
        @Name("generate_bamboo")
        @Comment("Should bamboo stalks be generated in tropical biomes?")
        @RequiresMcRestart
        private boolean bambooGenerationEnabled = true;

        @Name("generate_salt_ore")
        @Comment("Should halite clusters be generated underground?")
        @RequiresMcRestart
        private boolean saltOreGenerationEnabled = true;

        @Name("salt_ore_cluster_size")
        @Comment("The size of generated halite clusters")
        @RequiresMcRestart
        private int saltClusterSize = 8;
    }
}
