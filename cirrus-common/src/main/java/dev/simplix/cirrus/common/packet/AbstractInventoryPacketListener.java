package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.inventory.InventoryTracker.TrackedInventory;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.model.CirrusClickType;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractInventoryPacketListener extends PacketListenerAbstract {

    protected final InventoryTracker inventoryTracker;
    protected final AbstractPacketMenuBuildService menuBuildService;

    public AbstractInventoryPacketListener(InventoryTracker inventoryTracker, AbstractPacketMenuBuildService menuBuildService) {
        super(PacketListenerPriority.NORMAL);
        this.inventoryTracker = inventoryTracker;
        this.menuBuildService = menuBuildService;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            handleClickWindow(event);
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            handleCloseWindow(event);
        }
    }

    private void handleClickWindow(PacketReceiveEvent event) {
        WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
        UUID playerUuid = event.getUser().getUUID();
        int windowId = wrapper.getWindowId();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (!trackedOpt.isPresent()) {
            return;
        }

        TrackedInventory tracked = trackedOpt.get();
        event.setCancelled(true);

        int slot = wrapper.getSlot();
        User user = event.getUser();
        int stateId = tracked.stateId().incrementAndGet();

        // Immediately clear the cursor so the item never visually attaches to it
        user.sendPacket(new WrapperPlayServerSetSlot(-1, stateId, -1, ItemStack.EMPTY));

        // Immediately restore the clicked slot so the item doesn't visually disappear
        if (slot >= 0 && slot < tracked.items().length) {
            CirrusBaseItemStack cirrusItem = tracked.items()[slot];
            ItemStack packetItem = cirrusItem != null
                ? PacketItemStackConverter.toPacketEventsItemStack(cirrusItem, user.getClientVersion().getProtocolVersion())
                : ItemStack.EMPTY;
            user.sendPacket(new WrapperPlayServerSetSlot(windowId, stateId, slot, packetItem));
        }

        int button = wrapper.getButton();
        WrapperPlayClientClickWindow.WindowClickType clickType = wrapper.getWindowClickType();

        CirrusClickType cirrusClickType = ClickTypeMapper.mapClickType(clickType, button, slot);

        menuBuildService.handleClick(tracked, slot, cirrusClickType);
    }

    private void handleCloseWindow(PacketReceiveEvent event) {
        WrapperPlayClientCloseWindow wrapper = new WrapperPlayClientCloseWindow(event);
        UUID playerUuid = event.getUser().getUUID();
        int windowId = wrapper.getWindowId();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (trackedOpt.isPresent()) {
            TrackedInventory tracked = trackedOpt.get();
            menuBuildService.handleClose(playerUuid, tracked);
            inventoryTracker.untrack(playerUuid, windowId);
        }
    }
}
