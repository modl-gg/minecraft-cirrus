package dev.simplix.cirrus.fabric.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.fabric.menubuilder.FabricMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricInventoryPacketListener extends AbstractInventoryPacketListener {

    public FabricInventoryPacketListener(InventoryTracker inventoryTracker, FabricMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }

    @Override
    protected UUID getPlayerUuid(Object playerHandle) {
        if (!(playerHandle instanceof ServerPlayerEntity)) {
            return null;
        }
        return ((ServerPlayerEntity) playerHandle).getUuid();
    }
}
