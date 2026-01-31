package dev.simplix.cirrus.spigot.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.spigot.menubuilder.SpigotMenuBuildService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpigotInventoryPacketListener extends AbstractInventoryPacketListener {

    public SpigotInventoryPacketListener(InventoryTracker inventoryTracker, SpigotMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }
}
