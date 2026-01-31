package dev.simplix.cirrus.velocity.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.inventory.InventoryTracker.TrackedInventory;
import dev.simplix.cirrus.model.CirrusClickType;
import dev.simplix.cirrus.velocity.menubuilder.VelocityMenuBuildService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryPacketListener extends PacketListenerAbstract {

    private final InventoryTracker inventoryTracker;
    private final VelocityMenuBuildService menuBuildService;

    public InventoryPacketListener(InventoryTracker inventoryTracker, VelocityMenuBuildService menuBuildService) {
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
        if (trackedOpt.isEmpty()) {
            return;
        }

        TrackedInventory tracked = trackedOpt.get();
        event.setCancelled(true);

        int slot = wrapper.getSlot();
        int button = wrapper.getButton();
        WrapperPlayClientClickWindow.WindowClickType clickType = wrapper.getWindowClickType();

        CirrusClickType cirrusClickType = mapClickType(clickType, button, slot);

        menuBuildService.handleClick(
            tracked,
            slot,
            cirrusClickType
        );
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

    private CirrusClickType mapClickType(WrapperPlayClientClickWindow.WindowClickType type, int button, int slot) {
        return switch (type) {
            case PICKUP -> button == 0 ? CirrusClickType.LEFT_CLICK : CirrusClickType.RIGHT_CLICK;
            case QUICK_MOVE -> button == 0 ? CirrusClickType.SHIFT_LEFT_CLICK : CirrusClickType.SHIFT_RIGHT_CLICK;
            case SWAP -> switch (button) {
                case 0 -> CirrusClickType.NUMBER_KEY_1;
                case 1 -> CirrusClickType.NUMBER_KEY_2;
                case 2 -> CirrusClickType.NUMBER_KEY_3;
                case 3 -> CirrusClickType.NUMBER_KEY_4;
                case 4 -> CirrusClickType.NUMBER_KEY_5;
                case 5 -> CirrusClickType.NUMBER_KEY_6;
                case 6 -> CirrusClickType.NUMBER_KEY_7;
                case 7 -> CirrusClickType.NUMBER_KEY_8;
                case 8 -> CirrusClickType.NUMBER_KEY_9;
                case 40 -> CirrusClickType.OFFHAND_SWAP;
                default -> CirrusClickType.UNKNOWN;
            };
            case CLONE -> CirrusClickType.MIDDLE_CLICK;
            case THROW -> button == 0 ? CirrusClickType.DROP : CirrusClickType.CTRL_DROP;
            case QUICK_CRAFT -> mapQuickCraft(button);
            case PICKUP_ALL -> CirrusClickType.DOUBLE_CLICK;
            default -> CirrusClickType.UNKNOWN;
        };
    }

    private CirrusClickType mapQuickCraft(int button) {
        return switch (button) {
            case 0 -> CirrusClickType.DRAG_START_LEFT;
            case 4 -> CirrusClickType.DRAG_START_RIGHT;
            case 8 -> CirrusClickType.DRAG_START_MIDDLE;
            case 1 -> CirrusClickType.DRAG_ADD_LEFT;
            case 5 -> CirrusClickType.DRAG_ADD_RIGHT;
            case 9 -> CirrusClickType.DRAG_ADD_MIDDLE;
            case 2 -> CirrusClickType.DRAG_END_LEFT;
            case 6 -> CirrusClickType.DRAG_END_RIGHT;
            case 10 -> CirrusClickType.DRAG_END_MIDDLE;
            default -> CirrusClickType.UNKNOWN;
        };
    }
}
