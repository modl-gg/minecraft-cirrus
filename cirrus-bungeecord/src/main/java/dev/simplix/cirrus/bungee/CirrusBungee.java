package dev.simplix.cirrus.bungee;

import com.github.retrooper.packetevents.PacketEvents;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.bungee.listeners.InventoryPacketListener;
import dev.simplix.cirrus.bungee.listeners.QuitListener;
import dev.simplix.cirrus.bungee.menubuilder.BungeeMenuBuildService;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.service.MenuBuildService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@RequiredArgsConstructor
public class CirrusBungee {

    private final Plugin plugin;

    public void init() {
        Cirrus.init();

        BungeeMenuBuildService menuBuildService = new BungeeMenuBuildService();

        ProxyServer.getInstance().getPluginManager().registerListener(
            plugin,
            new QuitListener(menuBuildService.getInventoryTracker())
        );

        Cirrus.registerService(MenuBuildService.class, menuBuildService);

        PacketEvents.getAPI().getEventManager().registerListener(
            new InventoryPacketListener(menuBuildService.getInventoryTracker(), menuBuildService)
        );

        ProxyServer
            .getInstance()
            .getScheduler()
            .schedule(plugin, new MenuUpdateTask(), 0L, 50L, TimeUnit.MILLISECONDS);
    }
}
