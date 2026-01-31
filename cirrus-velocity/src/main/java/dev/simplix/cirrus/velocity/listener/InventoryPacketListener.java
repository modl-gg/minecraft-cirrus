package dev.simplix.cirrus.velocity.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.velocity.menubuilder.VelocityMenuBuildService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryPacketListener extends AbstractInventoryPacketListener {

    public InventoryPacketListener(InventoryTracker inventoryTracker, VelocityMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }
}
