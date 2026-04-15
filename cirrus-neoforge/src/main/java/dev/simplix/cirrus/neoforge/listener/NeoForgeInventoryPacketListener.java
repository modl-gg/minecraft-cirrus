package dev.simplix.cirrus.neoforge.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.neoforge.menubuilder.NeoForgeMenuBuildService;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public class NeoForgeInventoryPacketListener extends AbstractInventoryPacketListener {

    public NeoForgeInventoryPacketListener(InventoryTracker inventoryTracker, NeoForgeMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }

    @Override
    protected UUID getPlayerUuid(Object playerHandle) {
        if (!(playerHandle instanceof ServerPlayer)) {
            return null;
        }
        return ((ServerPlayer) playerHandle).getUUID();
    }
}
