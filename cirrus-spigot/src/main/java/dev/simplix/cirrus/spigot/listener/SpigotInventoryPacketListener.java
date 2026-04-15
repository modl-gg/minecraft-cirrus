package dev.simplix.cirrus.spigot.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.spigot.menubuilder.SpigotMenuBuildService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;

@Slf4j
public class SpigotInventoryPacketListener extends AbstractInventoryPacketListener {

    public SpigotInventoryPacketListener(InventoryTracker inventoryTracker, SpigotMenuBuildService menuBuildService) {
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
