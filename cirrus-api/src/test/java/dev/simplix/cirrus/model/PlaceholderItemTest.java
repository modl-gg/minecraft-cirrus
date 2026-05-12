package dev.simplix.cirrus.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlaceholderItemTest {

    @Test
    void constructorRetainsLiveSlotsCompatibility() {
        int[] slots = new int[]{1, 2};
        PlaceholderItem placeholderItem = new PlaceholderItem(
            null,
            "handler",
            slots
        );

        slots[0] = 9;

        assertArrayEquals(new int[]{9, 2}, placeholderItem.slots());
    }

    @Test
    void slotsReturnsLiveArrayCompatibility() {
        PlaceholderItem placeholderItem = PlaceholderItem.of(
            null,
            "handler",
            1,
            2
        );

        int[] slots = placeholderItem.slots();

        assertSame(slots, placeholderItem.slots());
    }
}
