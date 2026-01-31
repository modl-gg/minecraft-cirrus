package dev.simplix.cirrus.model;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import java.util.Arrays;

public record PlaceholderItem(CirrusBaseItemStack item, String actionHandler, int[] slots) {

    public static PlaceholderItem of(CirrusBaseItemStack item, String actionHandler, int... slots) {
        return new PlaceholderItem(item, actionHandler, slots);
    }

    public PlaceholderItem copy() {
        return new PlaceholderItem(
            this.item,
            this.actionHandler,
            Arrays.copyOf(this.slots, this.slots.length));
    }
}