package dev.simplix.cirrus.neoforge.listener;

import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.menu.Menus;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class NeoForgeQuitListener {

    private final InventoryTracker inventoryTracker;

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID uuid = player.getUUID();
            Menus.remove(uuid);
            inventoryTracker.untrackAll(uuid);
        }
    }
}
