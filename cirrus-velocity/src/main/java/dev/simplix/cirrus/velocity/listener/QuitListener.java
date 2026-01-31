package dev.simplix.cirrus.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.menu.Menus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuitListener {

    private final InventoryTracker inventoryTracker;

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Menus.remove(uuid);
        inventoryTracker.untrackAll(uuid);
    }
}
