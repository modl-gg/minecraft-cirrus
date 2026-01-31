package dev.simplix.cirrus.menu;

import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.schematic.MenuSchematic;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Accessors(fluent = true)
public class MenuElement {

    @Nullable
    private transient MenuSchematic menuSchematic;

    @Nullable
    private CirrusBaseItemStack item;

    @Nullable
    @Getter
    private Integer slot;

    @Nullable
    private String actionHandler;

    public void set(@NonNull CirrusItem cirrusItem) {
        this.set(cirrusItem, cirrusItem.actionHandler());
    }

    public void set(@NonNull CirrusBaseItemStack item, @Nullable String actionHandler) {
        this.actionHandler = actionHandler;
        this.item = item;
        if (this.menuSchematic != null && this.slot != null) {
            applyChanges(this.menuSchematic, this.slot);
        }
    }

    public void applyChanges(@NonNull MenuSchematic menuSchematic, int slot) {
        this.slot = slot;
        menuSchematic.set(item, slot, actionHandler);
    }

    public void set(@NonNull CirrusBaseItemStack item) {
        this.set(item, null);
    }

    public Optional<MenuSchematic> menuSchematic() {
        return Optional.ofNullable(menuSchematic);
    }

    public Optional<String> actionHandlerString() {
        return Optional.ofNullable(actionHandler);
    }

    public Optional<CirrusBaseItemStack> item() {
        return Optional.ofNullable(this.item);
    }
}