package net.insomniakitten.bamboo.init;

import com.google.common.base.Stopwatch;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.entity.EntityFallingSaltBlock;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.concurrent.TimeUnit;

@ObjectHolder(Bamboozled.ID)
@EventBusSubscriber(modid = Bamboozled.ID)
@Log4j2(topic = Bamboozled.ID + ".entities")
public final class BamboozledEntities {
    public static final EntityEntry FALLING_SALT_BLOCK = null;
    public static final EntityEntry THROWN_SALT_PILE = null;

    private static int nextId;

    private BamboozledEntities() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

    @SubscribeEvent
    static void onRegisterEntities(final RegistryEvent.Register<EntityEntry> event) {
        BamboozledEntities.LOGGER.info("Beginning entity registration");

        val stopwatch = Stopwatch.createStarted();
        val registry = event.getRegistry();

        BamboozledEntities.registerEntity(registry, EntityFallingSaltBlock.class, "falling_salt_block", 256, 1, true);
        BamboozledEntities.registerEntity(registry, EntityThrownSaltPile.class, "thrown_salt_pile", 64, 1, true);

        val elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);

        BamboozledEntities.LOGGER.info("Entity registration completed in {}ms", elapsed);
    }

    private static void registerEntity(final IForgeRegistry<EntityEntry> registry, final Class<? extends Entity> clazz, final String name, final int range, final int updateFrequency, final boolean sendVelocityUpdates) {
        BamboozledEntities.LOGGER.debug("Registering entity '{}:{}'", Bamboozled.ID, name);

        val builder = EntityEntryBuilder.create().entity(clazz);

        builder.id(Bamboozled.addNamespace(name), BamboozledEntities.getNextId());
        builder.name(Bamboozled.addNamespace(name, '.'));
        builder.tracker(range, updateFrequency, sendVelocityUpdates);

        registry.register(builder.build());
    }

    private static int getNextId() {
        return BamboozledEntities.nextId++;
    }
}
