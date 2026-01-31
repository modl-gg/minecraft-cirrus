package dev.simplix.cirrus.model;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class MenuContent implements ConcurrentMap<Integer, CirrusBaseItemStack> {
    private final Map<Integer, CirrusBaseItemStack> value = new ConcurrentHashMap<>();

    public MenuContent(Map<Integer, CirrusBaseItemStack> items) {
        this.value.putAll(items);
    }

    // Delegate methods
    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean containsKey(@NonNull Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public boolean containsValue(@NonNull Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public CirrusBaseItemStack get(@NonNull Object key) {
        return this.value.get(key);
    }

    @Nullable
    @Override
    public CirrusBaseItemStack put(@NonNull Integer key, @NonNull CirrusBaseItemStack value) {
        return this.value.put(key, value);
    }

    @Override
    public CirrusBaseItemStack remove(Object key) {
        return this.value.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends Integer, ? extends CirrusBaseItemStack> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @NonNull
    @Override
    public Set<Integer> keySet() {
        return this.value.keySet();
    }

    @NonNull
    @Override
    public Collection<CirrusBaseItemStack> values() {
        return this.value.values();
    }

    @NonNull
    @Override
    public Set<Entry<Integer, CirrusBaseItemStack>> entrySet() {
        return this.value.entrySet();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.value.equals(obj);
    }

    @Override
    public String toString() {
        return Objects.toString(this.value);
    }

    @Override
    public CirrusBaseItemStack getOrDefault(Object key, CirrusBaseItemStack defaultValue) {
        return this.value.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super CirrusBaseItemStack> action) {
        this.value.forEach(action);
    }

    @Override
    public CirrusBaseItemStack putIfAbsent(Integer key, CirrusBaseItemStack value) {
        return this.value.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.value.remove(key, value);
    }

    @Override
    public boolean replace(
        Integer key,
        CirrusBaseItemStack oldValue,
        CirrusBaseItemStack newValue) {
        return this.value.replace(key, oldValue, newValue);
    }

    @Override
    public CirrusBaseItemStack replace(Integer key, CirrusBaseItemStack value) {
        return this.value.replace(key, value);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super CirrusBaseItemStack, ? extends CirrusBaseItemStack> function) {
        this.value.replaceAll(function);
    }

    @Override
    public CirrusBaseItemStack computeIfAbsent(
        Integer key,
        @NonNull Function<? super Integer, ? extends CirrusBaseItemStack> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public CirrusBaseItemStack computeIfPresent(
        Integer key,
        @NonNull BiFunction<? super Integer, ? super CirrusBaseItemStack, ? extends CirrusBaseItemStack> remappingFunction) {
        return this.value.computeIfPresent(key, remappingFunction);
    }

    @Override
    public CirrusBaseItemStack compute(
        Integer key,
        @NonNull BiFunction<? super Integer, ? super CirrusBaseItemStack, ? extends CirrusBaseItemStack> remappingFunction) {
        return this.value.compute(key, remappingFunction);
    }

    @Override
    public CirrusBaseItemStack merge(
        Integer key,
        @NonNull CirrusBaseItemStack value,
        @NonNull BiFunction<? super CirrusBaseItemStack, ? super CirrusBaseItemStack, ? extends CirrusBaseItemStack> remappingFunction) {
        return this.value.merge(key, value, remappingFunction);
    }

    public MenuContent copy() {
        return new MenuContent(this);
    }
}
