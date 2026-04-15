package dev.simplix.cirrus.velocity.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.velocity.menubuilder.VelocityMenuBuildService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import com.velocitypowered.api.proxy.Player;

@Slf4j
public class InventoryPacketListener extends AbstractInventoryPacketListener {

    public InventoryPacketListener(InventoryTracker inventoryTracker, VelocityMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }

    @Override
    protected UUID getPlayerUuid(Object playerHandle) {
        if (!(playerHandle instanceof Player)) {
            return null;
        }
        return ((Player) playerHandle).getUniqueId();
    }
}
