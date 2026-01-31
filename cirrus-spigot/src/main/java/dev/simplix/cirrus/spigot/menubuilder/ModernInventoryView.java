package dev.simplix.cirrus.spigot.menubuilder;

import dev.simplix.cirrus.menu.Menu;
import lombok.NonNull;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public final class ModernInventoryView implements InventoryView {

    private final Menu menu;
    private final Player player;
    private final Inventory top;
    private final Inventory bottom;
    private String title;

    public ModernInventoryView(
        @NonNull Menu menu,
        @NonNull Player player,
        @NonNull Inventory top,
        @NonNull Inventory bottom) {
        this.player = player;
        this.menu = menu;
        this.top = top;
        this.bottom = bottom;
        this.title = menu.title();
    }

    @Override
    public Inventory getTopInventory() {
        return top;
    }

    @Override
    public Inventory getBottomInventory() {
        return bottom;
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        return top.getType();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getOriginalTitle() {
        return menu.title();
    }

    @Override
    public boolean setProperty(Property property, int value) {
        return false;
    }

    @Override
    public int countSlots() {
        return top.getSize() + bottom.getSize();
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        if (slot < top.getSize()) {
            top.setItem(slot, item);
        } else {
            bottom.setItem(slot - top.getSize(), item);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < top.getSize()) {
            return top.getItem(slot);
        } else {
            return bottom.getItem(slot - top.getSize());
        }
    }

    @Override
    public void setCursor(ItemStack item) {
        player.setItemOnCursor(item);
    }

    @Override
    public ItemStack getCursor() {
        return player.getItemOnCursor();
    }

    @Override
    public Inventory getInventory(int rawSlot) {
        if (rawSlot < top.getSize()) {
            return top;
        } else {
            return bottom;
        }
    }

    @Override
    public int convertSlot(int rawSlot) {
        if (rawSlot < top.getSize()) {
            return rawSlot;
        } else {
            return rawSlot - top.getSize();
        }
    }

    @Override
    public InventoryType.SlotType getSlotType(int slot) {
        if (slot < 0 || slot >= countSlots()) {
            return InventoryType.SlotType.OUTSIDE;
        }
        return InventoryType.SlotType.CONTAINER;
    }

    @Override
    public void close() {
        player.closeInventory();
    }

}
