package dev.simplix.cirrus.inventory;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertSame;

class InventoryTrackerTest {

    @Test
    void trackedInventoryItemsRetainsLiveArrayCompatibility() {
        CirrusBaseItemStack[] items = new CirrusBaseItemStack[]{null};
        InventoryTracker.TrackedInventory tracked = new InventoryTracker.TrackedInventory(
            1,
            CirrusInventoryType.GENERIC_9X2,
            CirrusChatElement.empty(),
            items,
            null,
            new AtomicInteger()
        );

        assertSame(items, tracked.items());
    }
}
