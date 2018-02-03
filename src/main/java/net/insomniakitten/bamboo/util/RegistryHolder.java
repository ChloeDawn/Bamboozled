package net.insomniakitten.bamboo.util;

import com.google.common.collect.ImmutableList;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.LinkedList;
import java.util.List;

public final class RegistryHolder<V extends IForgeRegistryEntry<V>> {

    private final List<V> entries = new LinkedList<>();

    public Registry begin(RegistryEvent.Register<V> event) {
        return new Registry(event, entries);
    }

    public ImmutableList<V> entries() {
        return ImmutableList.copyOf(entries);
    }

    public final class Registry {
        private final RegistryEvent.Register<V> event;
        private final List<V> entries;

        private Registry(RegistryEvent.Register<V> event, List<V> entries) {
            this.event = event;
            this.entries = entries;
        }

        public final Registry register(V entry, String name) {
            if (entry.getRegistryName() == null) {
                entry.setRegistryName(new ResourceLocation(Bamboozled.ID, name));
            }
            if (entry instanceof Block) {
                ((Block) entry).setUnlocalizedName(name);
            }
            if (entry instanceof Item) {
                ((Item) entry).setUnlocalizedName(name);
            }
            return register(entry);
        }

        public final Registry register(V entry) {
            if (!entries.contains(entry)) {
                event.getRegistry().register(entry);
                entries.add(entry);
            }
            return this;
        }
    }

}
