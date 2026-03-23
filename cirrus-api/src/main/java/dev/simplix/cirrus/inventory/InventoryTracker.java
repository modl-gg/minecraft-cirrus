package dev.simplix.cirrus.inventory;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.menu.DisplayedMenu;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;

public class InventoryTracker {

    private final Map<UUID, Map<Integer, TrackedInventory>> playerInventories = new ConcurrentHashMap<>();
    private final Map<UUID, AtomicInteger> windowIdCounters = new ConcurrentHashMap<>();

    public void track(@NonNull UUID player, int windowId, @NonNull TrackedInventory inventory) {
        playerInventories
            .computeIfAbsent(player, k -> new ConcurrentHashMap<>())
            .put(windowId, inventory);
    }

    public Optional<TrackedInventory> get(@NonNull UUID player, int windowId) {
        Map<Integer, TrackedInventory> inventories = playerInventories.get(player);
        if (inventories == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(inventories.get(windowId));
    }

    public void untrack(@NonNull UUID player, int windowId) {
        Map<Integer, TrackedInventory> inventories = playerInventories.get(player);
        if (inventories != null) {
            inventories.remove(windowId);
            if (inventories.isEmpty()) {
                playerInventories.remove(player);
            }
        }
    }

    public void untrackAll(@NonNull UUID player) {
        playerInventories.remove(player);
        windowIdCounters.remove(player);
    }

    public int generateWindowId(@NonNull UUID player) {
        AtomicInteger counter = windowIdCounters.computeIfAbsent(player, k -> new AtomicInteger(100));
        Map<Integer, TrackedInventory> inventories = playerInventories.get(player);

        int attempts = 0;
        int windowId;
        do {
            windowId = counter.incrementAndGet();
            if (windowId > 200) {
                counter.set(100);
                windowId = counter.incrementAndGet();
            }
            attempts++;
        } while (inventories != null && inventories.containsKey(windowId) && attempts < 100);

        return windowId;
    }

    public boolean hasOpenInventory(@NonNull UUID player) {
        Map<Integer, TrackedInventory> inventories = playerInventories.get(player);
        return inventories != null && !inventories.isEmpty();
    }

    public static final class TrackedInventory {

        private final int windowId;
        private final CirrusInventoryType type;
        private final CirrusChatElement title;
        private final CirrusBaseItemStack[] items;
        private final DisplayedMenu displayedMenu;
        private final AtomicInteger stateId;

        public TrackedInventory(
            int windowId,
            CirrusInventoryType type,
            CirrusChatElement title,
            CirrusBaseItemStack[] items,
            DisplayedMenu displayedMenu,
            AtomicInteger stateId) {
            this.windowId = windowId;
            this.type = type;
            this.title = title;
            this.items = items;
            this.displayedMenu = displayedMenu;
            this.stateId = stateId;
        }

        public int windowId() {
            return this.windowId;
        }

        public CirrusInventoryType type() {
            return this.type;
        }

        public CirrusChatElement title() {
            return this.title;
        }

        public CirrusBaseItemStack[] items() {
            return this.items;
        }

        public DisplayedMenu displayedMenu() {
            return this.displayedMenu;
        }

        public AtomicInteger stateId() {
            return this.stateId;
        }

        public void setItem(int slot, CirrusBaseItemStack item) {
            if (slot >= 0 && slot < items.length) {
                items[slot] = item;
            }
        }
    }
}
