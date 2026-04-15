package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
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

    protected abstract UUID getPlayerUuid(Object playerHandle);

    protected Object normalizePlayerHandle(Object playerHandle) {
        return playerHandle;
    }

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
        UUID playerUuid = resolvePlayerUuid(event);
        if (playerUuid == null) {
            log.warn("Ignoring {} because no player context could be resolved", event.getPacketType());
            return;
        }
        int windowId = wrapper.getWindowId();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (!trackedOpt.isPresent()) {
            return;
        }

        TrackedInventory tracked = trackedOpt.get();
        event.setCancelled(true);

        int slot = wrapper.getSlot();
        Object playerHandle = resolvePlayerHandle(event, tracked);
        if (playerHandle == null) {
            log.warn("Ignoring tracked {} for player {} because no player handle could be resolved", event.getPacketType(), playerUuid);
            return;
        }

        int protocolVersion = resolveProtocolVersion(event, tracked);
        int stateId = tracked.stateId().incrementAndGet();

        // Immediately clear the cursor so the item never visually attaches to it
        sendPacket(playerHandle, new WrapperPlayServerSetSlot(-1, stateId, -1, ItemStack.EMPTY));

        // Immediately restore the clicked slot so the item doesn't visually disappear
        if (slot >= 0 && slot < tracked.items().length) {
            CirrusBaseItemStack cirrusItem = tracked.items()[slot];
            ItemStack packetItem = cirrusItem != null
                ? PacketItemStackConverter.toPacketEventsItemStack(cirrusItem, protocolVersion)
                : ItemStack.EMPTY;
            sendPacket(playerHandle, new WrapperPlayServerSetSlot(windowId, stateId, slot, packetItem));
        }

        int button = wrapper.getButton();
        WrapperPlayClientClickWindow.WindowClickType clickType = wrapper.getWindowClickType();

        CirrusClickType cirrusClickType = ClickTypeMapper.mapClickType(clickType, button, slot);

        menuBuildService.handleClick(tracked, slot, cirrusClickType);
    }

    private void handleCloseWindow(PacketReceiveEvent event) {
        WrapperPlayClientCloseWindow wrapper = new WrapperPlayClientCloseWindow(event);
        UUID playerUuid = resolvePlayerUuid(event);
        if (playerUuid == null) {
            log.warn("Ignoring {} because no player context could be resolved", event.getPacketType());
            return;
        }
        int windowId = wrapper.getWindowId();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (trackedOpt.isPresent()) {
            TrackedInventory tracked = trackedOpt.get();
            menuBuildService.handleClose(playerUuid, tracked);
            inventoryTracker.untrack(playerUuid, windowId);
        }
    }

    private UUID resolvePlayerUuid(PacketReceiveEvent event) {
        User user = event.getUser();
        if (user != null) {
            return user.getUUID();
        }
        return getPlayerUuid(normalizePlayerHandle(event.getPlayer()));
    }

    private Object resolvePlayerHandle(PacketReceiveEvent event, TrackedInventory tracked) {
        Object playerHandle = normalizePlayerHandle(event.getPlayer());
        if (playerHandle != null) {
            return playerHandle;
        }

        if (tracked.displayedMenu() == null || tracked.displayedMenu().player() == null) {
            return null;
        }
        return normalizePlayerHandle(tracked.displayedMenu().player().handle());
    }

    private int resolveProtocolVersion(PacketReceiveEvent event, TrackedInventory tracked) {
        User user = event.getUser();
        if (user != null && user.getClientVersion() != null) {
            return user.getClientVersion().getProtocolVersion();
        }

        if (tracked.displayedMenu() != null && tracked.displayedMenu().player() != null) {
            return tracked.displayedMenu().player().protocolVersion();
        }
        return ClientVersion.getLatest().getProtocolVersion();
    }

    private void sendPacket(Object playerHandle, PacketWrapper<?> packet) {
        Object normalizedHandle = normalizePlayerHandle(playerHandle);
        if (normalizedHandle == null || PacketEvents.getAPI() == null) {
            return;
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(normalizedHandle, packet);
    }
}
