package dev.simplix.cirrus.neoforge;

import com.github.retrooper.packetevents.PacketEvents;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.neoforge.listener.NeoForgeInventoryPacketListener;
import dev.simplix.cirrus.neoforge.listener.NeoForgeQuitListener;
import dev.simplix.cirrus.neoforge.menubuilder.NeoForgeMenuBuildService;
import dev.simplix.cirrus.neoforge.services.NeoForgeItemService;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.service.RunSyncService;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CirrusNeoForge {

    private final MinecraftServer server;
    private volatile ScheduledExecutorService menuUpdateExecutor;

    public void init() {
        if (menuUpdateExecutor != null) return;
        Cirrus.init();
        Cirrus.canDisplayAsync(true);

        NeoForgeMenuBuildService menuBuildService = new NeoForgeMenuBuildService();

        Cirrus.registerService(ItemService.class, new NeoForgeItemService());
        Cirrus.registerService(MenuBuildService.class, menuBuildService);
        Cirrus.registerService(RunSyncService.class, (RunSyncService) server::execute);

        PacketEvents.getAPI().getEventManager().registerListener(
            new NeoForgeInventoryPacketListener(menuBuildService.getInventoryTracker(), menuBuildService)
        );

        NeoForge.EVENT_BUS.register(new NeoForgeQuitListener(menuBuildService.getInventoryTracker()));

        menuUpdateExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cirrus-menu-update");
            t.setDaemon(true);
            return t;
        });
        menuUpdateExecutor.scheduleAtFixedRate(new MenuUpdateTask(), 50, 50, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (menuUpdateExecutor != null) {
            menuUpdateExecutor.shutdownNow();
        }
    }
}
