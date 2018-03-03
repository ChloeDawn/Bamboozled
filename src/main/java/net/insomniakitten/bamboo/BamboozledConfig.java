package net.insomniakitten.bamboo;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Bamboozled.ID, name = Bamboozled.ID, category = "")
@Mod.EventBusSubscriber(modid = Bamboozled.ID)
public final class BamboozledConfig {

    @Config.Name("general")
    public static final General GENERAL = new General();

    @Config.Name("world")
    public static final World WORLD = new World();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Bamboozled.ID.equals(event.getModID())) {
            ConfigManager.sync(Bamboozled.ID, Config.Type.INSTANCE);
        }
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

        @Config.Name("advanced_slab_interaction")
        @Config.Comment("Should slabs be breakable individually when they are a double slab?")
        @Config.RequiresMcRestart
        public boolean advancedSlabInteraction = true;

        @Config.Name("fancy_bamboo")
        @Config.Comment({"Should the bounding box of bamboo be fancy and detailed?",
                         "When false, collision logic will also be simplified"})
        @Config.RequiresMcRestart
        public boolean fancyBamboo = true;

        @Config.Name("fancy_hopper")
        @Config.Comment({"Should the bounding box of hopper be fancy and detailed?",
                         "When false, collision logic will also be simplified"})
        @Config.RequiresMcRestart
        public boolean fancyHopper = true;
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
    }

}
