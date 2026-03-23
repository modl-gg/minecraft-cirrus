package dev.simplix.cirrus.model;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import java.util.Arrays;

public final class PlaceholderItem {

    private final CirrusBaseItemStack item;
    private final String actionHandler;
    private final int[] slots;

    public PlaceholderItem(CirrusBaseItemStack item, String actionHandler, int[] slots) {
        this.item = item;
        this.actionHandler = actionHandler;
        this.slots = slots;
    }

    public CirrusBaseItemStack item() {
        return this.item;
    }

    public String actionHandler() {
        return this.actionHandler;
    }

    public int[] slots() {
        return this.slots;
    }

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
