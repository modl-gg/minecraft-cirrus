package dev.simplix.cirrus.model;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.menu.DisplayedMenu;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.NonNull;

public final class Click {

    private final CirrusClickType clickType;
    private final DisplayedMenu clickedMenu;
    private final CirrusBaseItemStack clickedItem;
    private final int slot;

    public Click(
        @NonNull CirrusClickType clickType,
        @NonNull DisplayedMenu clickedMenu,
        @Nullable CirrusBaseItemStack clickedItem,
        int slot) {
        this.clickType = clickType;
        this.clickedMenu = clickedMenu;
        this.clickedItem = clickedItem;
        this.slot = slot;
    }

    public CirrusPlayerWrapper player() {
        return this.clickedMenu.player();
    }

    public CirrusClickType clickType() {
        return this.clickType;
    }

    public DisplayedMenu clickedMenu() {
        return this.clickedMenu;
    }

    public List<String> arguments() {
        if (this.clickedItem instanceof CirrusItem) {
            CirrusItem cirrusItem = (CirrusItem) this.clickedItem;
            return cirrusItem.actionArguments();
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends CirrusBaseItemStack> T clickedItem() {
        return (T) this.clickedItem;
    }

    public int slot() {
        return this.slot;
    }
}