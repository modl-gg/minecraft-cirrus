package dev.simplix.cirrus.schematic.impl;

import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.actionhandler.ActionHandler;
import dev.simplix.cirrus.actionhandler.RegisteredActionHandler;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.menu.MenuElement;
import dev.simplix.cirrus.menu.MenuRow;
import dev.simplix.cirrus.model.BusinessItemMap;
import dev.simplix.cirrus.model.CirrusSound;
import dev.simplix.cirrus.model.CirrusSoundCategory;
import dev.simplix.cirrus.model.MenuContent;
import dev.simplix.cirrus.model.PlaceholderItem;
import dev.simplix.cirrus.model.SimpleSound;
import dev.simplix.cirrus.schematic.MenuSchematic;
import dev.simplix.cirrus.service.CapacityService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class SimpleMenuSchematic implements MenuSchematic {

    protected final List<RegisteredActionHandler> actionHandlers = new ArrayList<>();
    private final Map<Integer, String> actionHandlerStringMap = new HashMap<>(0);
    @Builder.Default
    private String title = "Menu";
    @Builder.Default
    private CirrusInventoryType type = CirrusInventoryType.GENERIC_9X3;
    @Builder.Default
    private PlaceholderItem placeholderItem = null;
    @Builder.Default
    private Set<Integer> reservedSlots = new HashSet<>(0);
    @Builder.Default
    private BusinessItemMap businessItems = new BusinessItemMap();
    @Builder.Default
    private MenuContent rootItems = new MenuContent();
    @Builder.Default
    private SimpleSound soundOnOpen = new SimpleSound(
        CirrusSound.BLOCK_NOTE_BLOCK_CHIME,
        CirrusSoundCategory.AMBIENT,
        .4f,
        1.5f);

    @Override
    public MenuSchematic copy() {
        return SimpleMenuSchematic
            .builder()
            .title(this.title)
            .placeholderItem(this.placeholderItem.copy())
            .reservedSlots(new HashSet<>(this.reservedSlots))
            .businessItems(this.businessItems.copy())
            .type(this.type)
            .rootItems(this.rootItems.copy())
            .build();
    }

    @Override
    @Nullable
    public String title() {
        return this.title;
    }

    @Override
    public Locale locale() {
        return Objects.requireNonNull(Cirrus.defaultLocale(), "defaultLocale must not be null");
    }

    @Override
    public CirrusInventoryType type() {
        return Objects.requireNonNull(this.type, "Type must not be null");
    }

    @Override
    public int typicalSize(int protocolVersion) {
        return type().typicalSize(protocolVersion);
    }

    @Override
    public Optional<PlaceholderItem> placeholderItem() {
        return Optional.ofNullable(this.placeholderItem);
    }

    /**
     * returns the center slot for a menu. It does this by first calculating the typical size of the
     * menu and dividing it by 2 to find the middle position. Then, it checks whether the typical size
     * is odd or even by checking the remainder when divided by 2. If the typical size is odd, it
     * returns the middle position as the center slot. Otherwise, if the typical size is even, it
     * returns the middle position minus 5 as the center slot. This ensures that the center slot is
     * always in the middle of the menu, regardless of whether the typical size is odd or even.
     *
     * @return
     */
    @Override
    public int centerSlot() {
        final int pos = this.typicalSize() / 2;
        return this.typicalSize() % 2 == 1 ? pos : pos - 5;
    }

    @Override
    public MenuRow row(int row) {
        LinkedList<MenuElement> items = new LinkedList<>();

        int i = 9 * (row - 1);
        while (i < 9 * row) {
            items.add(element(i));
            i++;
        }

        return new MenuRow(items);
    }

    @Override
    public SimpleMenuSchematic remove(int slot) {
        this.rootItems.remove(slot);
        return this;
    }

    @Override
    public Optional<String> actionHandlerString(int slot) {
        return Optional.ofNullable(actionHandlerStringMap.get(slot));
    }

    @Override
    public Optional<ActionHandler> actionHandler(int slot) {
        return actionHandlerString(slot).flatMap(this::findActionHandler);
    }

    @Override
    public MenuSchematic set(CirrusBaseItemStack item, int slot, @Nullable String actionHandler) {
        rootItems().put(slot, item);
        if (actionHandler != null) {
            actionHandlerStringMap.put(slot, actionHandler);
        }

        return this;
    }

    @Override
    public SimpleMenuSchematic set(CirrusItem item) {
        return (SimpleMenuSchematic) MenuSchematic.super.set(item);
    }

    @Override
    public MenuElement element(int slot) {
        return new MenuElement(this, get(slot), slot, actionHandlerString(slot).orElse(null));
    }

    @Nullable
    @Override
    public CirrusBaseItemStack get(int slot) {
        return this.rootItems.get(slot);
    }

    @Override
    public int add(CirrusBaseItemStack item, @Nullable String actionHandler) {
        final int capacity = Cirrus.service(CapacityService.class).capacity(type());
        for (int i = -1; i < capacity; i++) {
            if (!rootItems().containsKey(i) && !reservedSlots().contains(i)) {
                rootItems().put(i, item);
                return i;
            }
        }
        return -1;
    }

    private Optional<ActionHandler> findActionHandler(String actionHandlerString) {
        for (RegisteredActionHandler registeredActionHandler : actionHandlers) {
            if (registeredActionHandler.name().equals(actionHandlerString)) {
                return Optional.of(registeredActionHandler.handler());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SimpleMenuSchematic)) {
            return false;
        }

        SimpleMenuSchematic other = (SimpleMenuSchematic) obj;
        return ((other.title == null && this.title() == null) || other.title().equals(title()))
               && other.type() == type()
               && other.placeholderItem().equals(placeholderItem())
               && other.reservedSlots().equals(reservedSlots())
               && other.businessItems().equals(businessItems())
               && other.rootItems().equals(rootItems());
    }
}

