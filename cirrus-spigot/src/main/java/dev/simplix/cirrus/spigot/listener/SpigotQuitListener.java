package dev.simplix.cirrus.spigot.listener;

import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.menu.Menus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class SpigotQuitListener implements Listener {

    private final InventoryTracker inventoryTracker;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Menus.remove(uuid);
        inventoryTracker.untrackAll(uuid);
    }
}
