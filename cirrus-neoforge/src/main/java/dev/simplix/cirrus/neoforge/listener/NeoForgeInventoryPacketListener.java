package dev.simplix.cirrus.neoforge.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.neoforge.menubuilder.NeoForgeMenuBuildService;
public class NeoForgeInventoryPacketListener extends AbstractInventoryPacketListener {

    public NeoForgeInventoryPacketListener(InventoryTracker inventoryTracker, NeoForgeMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }
}
