package dev.simplix.cirrus.bungee.listeners;

import dev.simplix.cirrus.bungee.menubuilder.BungeeMenuBuildService;
import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryPacketListener extends AbstractInventoryPacketListener {

    public InventoryPacketListener(InventoryTracker inventoryTracker, BungeeMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }
}
