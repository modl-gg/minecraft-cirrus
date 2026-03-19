package dev.simplix.cirrus.common.service;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import dev.simplix.cirrus.actionhandler.ActionHandler;
import dev.simplix.cirrus.common.packet.PacketItemStackConverter;
import dev.simplix.cirrus.common.util.ComponentHelper;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.inventory.InventoryTracker.TrackedInventory;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.menu.DisplayedMenu;
import dev.simplix.cirrus.menu.Menu;
import dev.simplix.cirrus.menu.Menus;
import dev.simplix.cirrus.model.CirrusClickType;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public abstract class AbstractPacketMenuBuildService implements MenuBuildService {

    @Getter
    private final InventoryTracker inventoryTracker = new InventoryTracker();
    private long nextMenuId = 0;

    protected abstract User getUser(CirrusPlayerWrapper playerWrapper);

    protected abstract UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper);

    @Override
    public DisplayedMenu openAndBuildMenu0(Menu menu, CirrusPlayerWrapper playerWrapper) {
        UUID playerUuid = getPlayerUuid(playerWrapper);
        User user = getUser(playerWrapper);

        int windowId = inventoryTracker.generateWindowId(playerUuid);
        long id = generateID();

        CirrusInventoryType invType = menu.type();
        int size = invType.size();

        CirrusChatElement titleElement = CirrusChatElement.ofLegacyText(menu.title() != null ? menu.title() : "");
        Component title = ComponentHelper.removeItalic(titleElement.asComponent());

        CirrusBaseItemStack[] items = new CirrusBaseItemStack[size];
        menu.rootItems().forEach((slot, item) -> {
            if (slot >= 0 && slot < size) {
                items[slot] = item;
            }
        });

        DisplayedMenu displayedMenu = new DisplayedMenu(menu, windowId, playerWrapper, this, id);

        TrackedInventory tracked = new TrackedInventory(
            windowId,
            invType,
            titleElement,
            items,
            displayedMenu,
            new AtomicInteger(0)
        );

        inventoryTracker.track(playerUuid, windowId, tracked);

        sendOpenWindow(user, windowId, invType, title);
        sendWindowItems(user, windowId, items, playerWrapper.protocolVersion(), tracked.stateId());

        return displayedMenu;
    }

    @Override
    public void updateMenu(DisplayedMenu displayedMenu) {
        if (displayedMenu.closed().get()) {
            return;
        }

        User user = getUser(displayedMenu.player());
        UUID playerUuid = getPlayerUuid(displayedMenu.player());
        int windowId = (int) displayedMenu.nativeMenu();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (trackedOpt.isEmpty()) {
            buildAndOpenMenu(displayedMenu.value(), displayedMenu.player());
            return;
        }

        TrackedInventory tracked = trackedOpt.get();
        Menu menu = displayedMenu.value();

        if (tracked.type() != menu.type()) {
            buildAndOpenMenu(menu, displayedMenu.player());
            return;
        }

        CirrusBaseItemStack[] items = tracked.items();
        menu.rootItems().forEach((slot, item) -> {
            if (slot >= 0 && slot < items.length) {
                items[slot] = item;
            }
        });

        sendWindowItems(user, windowId, items, displayedMenu.player().protocolVersion(), tracked.stateId());
    }

    @Override
    public void closeMenu0(DisplayedMenu displayedMenu) {
        User user = getUser(displayedMenu.player());
        int windowId = (int) displayedMenu.nativeMenu();
        user.sendPacket(new WrapperPlayServerCloseWindow(windowId));

        inventoryTracker.untrack(getPlayerUuid(displayedMenu.player()), windowId);
    }

    public void handleClick(TrackedInventory tracked, int slot, CirrusClickType clickType) {
        DisplayedMenu displayedMenu = tracked.displayedMenu();
        if (displayedMenu == null || displayedMenu.closed().get()) {
            return;
        }

        Menu menu = displayedMenu.value();
        Optional<ActionHandler> handlerOpt = menu.actionHandler(slot);

        if (handlerOpt.isEmpty()) {
            // Resync client inventory with incremented state ID to cancel client-side prediction
            User user = getUser(displayedMenu.player());
            int windowId = (int) displayedMenu.nativeMenu();
            sendWindowItems(user, windowId, tracked.items(), displayedMenu.player().protocolVersion(), tracked.stateId());
            return;
        }

        CirrusBaseItemStack clickedItem = slot >= 0 && slot < tracked.items().length
            ? tracked.items()[slot]
            : null;

        Click click = new Click(clickType, displayedMenu, clickedItem, slot);

        try {
            handlerOpt.get().handle(click);
        } catch (Exception e) {
            log.warn("Exception caught in click handler", e);
        }
    }

    public void handleClose(UUID playerUuid, TrackedInventory tracked) {
        DisplayedMenu displayedMenu = tracked.displayedMenu();
        if (displayedMenu != null) {
            displayedMenu.closed().set(true);
        }
        Menus.remove(playerUuid);
    }

    protected void sendOpenWindow(User user, int windowId, CirrusInventoryType type, Component title) {
        WrapperPlayServerOpenWindow packet;
        if (user.getClientVersion().isOlderThan(ClientVersion.V_1_14)) {
            packet = new WrapperPlayServerOpenWindow(
                windowId,
                type.toLegacyType(),
                title,
                type.size(),
                -1
            );
        } else {
            packet = new WrapperPlayServerOpenWindow(
                windowId,
                type.toPacketEventsTypeId(),
                title
            );
        }
        user.sendPacket(packet);
    }

    protected void sendWindowItems(User user, int windowId, CirrusBaseItemStack[] items, int protocolVersion, AtomicInteger stateId) {
        List<ItemStack> packetItems = new ArrayList<>();

        for (CirrusBaseItemStack item : items) {
            if (item == null) {
                packetItems.add(ItemStack.EMPTY);
            } else {
                packetItems.add(PacketItemStackConverter.toPacketEventsItemStack(item, protocolVersion));
            }
        }

        // Increment state ID so the client accepts the resync and cancels its prediction.
        // Set carried item to EMPTY to clear any item on the cursor.
        int newStateId = stateId.incrementAndGet();
        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(windowId, newStateId, packetItems, ItemStack.EMPTY);
        user.sendPacket(packet);
    }

    protected synchronized long generateID() {
        return nextMenuId++;
    }
}
