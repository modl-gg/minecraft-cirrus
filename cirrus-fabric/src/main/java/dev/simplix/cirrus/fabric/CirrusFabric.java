package dev.simplix.cirrus.fabric;

import com.github.retrooper.packetevents.PacketEvents;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.fabric.listener.FabricInventoryPacketListener;
import dev.simplix.cirrus.fabric.listener.FabricQuitListener;
import dev.simplix.cirrus.fabric.menubuilder.FabricMenuBuildService;
import dev.simplix.cirrus.fabric.services.FabricItemService;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.service.RunSyncService;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CirrusFabric {

    private final MinecraftServer server;
    private volatile ScheduledExecutorService menuUpdateExecutor;

    public void init() {
        if (menuUpdateExecutor != null) return;
        Cirrus.init();
        Cirrus.canDisplayAsync(true);

        FabricMenuBuildService menuBuildService = new FabricMenuBuildService();

        Cirrus.registerService(ItemService.class, new FabricItemService());
        Cirrus.registerService(MenuBuildService.class, menuBuildService);
        Cirrus.registerService(RunSyncService.class, (RunSyncService) runnable -> server.execute(runnable));

        PacketEvents.getAPI().getEventManager().registerListener(
            new FabricInventoryPacketListener(menuBuildService.getInventoryTracker(), menuBuildService)
        );

        new FabricQuitListener(menuBuildService.getInventoryTracker()).register();

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
