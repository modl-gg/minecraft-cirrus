package dev.simplix.cirrus.fabric.listener;

import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.menu.Menus;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

@RequiredArgsConstructor
public class FabricQuitListener {

    private final InventoryTracker inventoryTracker;

    public void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID uuid = player.getUuid();
            Menus.remove(uuid);
            inventoryTracker.untrackAll(uuid);
        });
    }
}
