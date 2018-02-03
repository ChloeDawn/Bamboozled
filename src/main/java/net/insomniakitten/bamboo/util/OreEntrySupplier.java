package net.insomniakitten.bamboo.util;

import com.google.common.base.Equivalence;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface OreEntrySupplier {

    void getOreEntries(OreCollection oreEntries);

    final class OreCollection {
        private final Map<Equivalence.Wrapper<ItemStack>, List<String>> ores = new IdentityHashMap<>();

        private final Equivalence<ItemStack> stackEqv = new Equivalence<ItemStack>() {
            @Override
            protected boolean doEquivalent(ItemStack a, ItemStack b) {
                return ItemStack.areItemStackShareTagsEqual(a, b);
            }

            @Override
            @SuppressWarnings("ConstantConditions")
            protected int doHash(ItemStack stack) {
                int result = stack.getItem().getRegistryName().hashCode();
                result = 31 * result + stack.getItemDamage();
                if (stack.hasTagCompound()) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    result = 31 * result + nbt.hashCode();
                }
                return result;
            }
        };

        private OreCollection() {}

        public static OreCollection create() {
            return new OreCollection();
        }

        public List<String> put(ItemStack key, String value) {
            Objects.requireNonNull(key);
            return ores.putIfAbsent(stackEqv.wrap(key), Collections.singletonList(value));
        }

        public List<String> put(ItemStack key, String... values) {
            Objects.requireNonNull(key);
            return ores.putIfAbsent(stackEqv.wrap(key), Arrays.asList(values));
        }

        public List<String> put(ItemStack key, List<String> value) {
            Objects.requireNonNull(key);
            return ores.putIfAbsent(stackEqv.wrap(key), value);
        }

        public Set<Map.Entry<Equivalence.Wrapper<ItemStack>, List<String>>> entries() {
            return ores.entrySet();
        }
    }

}
