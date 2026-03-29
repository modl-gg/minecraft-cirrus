package dev.simplix.cirrus.fabric.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.fabric.menubuilder.FabricMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
public class FabricInventoryPacketListener extends AbstractInventoryPacketListener {

    public FabricInventoryPacketListener(InventoryTracker inventoryTracker, FabricMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }
}
