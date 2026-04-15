package dev.simplix.cirrus.bungee.listeners;

import dev.simplix.cirrus.bungee.menubuilder.BungeeMenuBuildService;
import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Slf4j
public class InventoryPacketListener extends AbstractInventoryPacketListener {

    public InventoryPacketListener(InventoryTracker inventoryTracker, BungeeMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }

    @Override
    protected UUID getPlayerUuid(Object playerHandle) {
        if (!(playerHandle instanceof ProxiedPlayer)) {
            return null;
        }
        return ((ProxiedPlayer) playerHandle).getUniqueId();
    }
}
