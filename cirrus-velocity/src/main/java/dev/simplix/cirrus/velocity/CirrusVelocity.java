package dev.simplix.cirrus.velocity;

import com.github.retrooper.packetevents.PacketEvents;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.velocity.listener.InventoryPacketListener;
import dev.simplix.cirrus.velocity.listener.QuitListener;
import dev.simplix.cirrus.velocity.menubuilder.VelocityMenuBuildService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CirrusVelocity {
    private final Object plugin;
    private final ProxyServer proxyServer;

    public void init() {
        Cirrus.init();

        VelocityMenuBuildService menuBuildService = new VelocityMenuBuildService();

        proxyServer.getEventManager().register(plugin, new QuitListener(menuBuildService.getInventoryTracker()));
        Cirrus.registerService(MenuBuildService.class, menuBuildService);

        PacketEvents.getAPI().getEventManager().registerListener(
            new InventoryPacketListener(menuBuildService.getInventoryTracker(), menuBuildService)
        );

        proxyServer
            .getScheduler()
            .buildTask(plugin, new MenuUpdateTask())
            .repeat(50, TimeUnit.MILLISECONDS)
            .schedule();
    }
}
