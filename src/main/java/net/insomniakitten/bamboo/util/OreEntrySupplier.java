package net.insomniakitten.bamboo.util;

import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public interface OreEntrySupplier {

    void getOreEntries(OreCollection oreEntries);

    final class OreCollection extends HashMap<ItemStack, List<String>> {
        public List<String> put(ItemStack key, String value) {
            return putIfAbsent(key, Collections.singletonList(value));
        }

        public List<String> put(ItemStack key, String... values) {
            return putIfAbsent(key, Arrays.asList(values));
        }

        @Override
        public List<String> put(ItemStack key, List<String> value) {
            return putIfAbsent(key, value);
        }
    }

}
