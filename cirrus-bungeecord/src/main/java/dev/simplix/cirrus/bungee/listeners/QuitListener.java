package dev.simplix.cirrus.bungee.listeners;

import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.menu.Menus;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class QuitListener implements Listener {

    private final InventoryTracker inventoryTracker;

    @EventHandler
    public void onQuit(@NonNull PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Menus.remove(uuid);
        inventoryTracker.untrackAll(uuid);
    }
}
