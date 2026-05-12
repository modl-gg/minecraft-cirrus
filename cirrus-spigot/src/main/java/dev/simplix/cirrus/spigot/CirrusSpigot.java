package dev.simplix.cirrus.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.service.RunSyncService;
import dev.simplix.cirrus.spigot.listener.SpigotInventoryPacketListener;
import dev.simplix.cirrus.spigot.listener.SpigotQuitListener;
import dev.simplix.cirrus.spigot.menubuilder.SpigotMenuBuildService;
import dev.simplix.cirrus.spigot.services.SpigotItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class CirrusSpigot {

    private static final boolean IS_FOLIA;
    private static Method foliaGetGlobalScheduler;
    private static Method foliaGlobalRun;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;

        if (IS_FOLIA) {
            try {
                foliaGetGlobalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
                Object scheduler = foliaGetGlobalScheduler.invoke(null);
                foliaGlobalRun = scheduler.getClass().getMethod("run",
                        Plugin.class, Consumer.class);
            } catch (Exception e) {
                // Will fall back to direct execution in foliaRunGlobal
            }
        }
    }

    private final JavaPlugin plugin;

    public void init() {
        Cirrus.init();
        Cirrus.canDisplayAsync(true);
        Cirrus.isSpigot(true);

        SpigotMenuBuildService menuBuildService = new SpigotMenuBuildService();

        Cirrus.registerService(ItemService.class, new SpigotItemService());
        Cirrus.registerService(MenuBuildService.class, menuBuildService);

        if (IS_FOLIA) {
            Cirrus.registerService(RunSyncService.class,
                runnable -> foliaRunGlobal(plugin, runnable));
        } else {
            Cirrus.registerService(RunSyncService.class,
                runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
        }

        PacketEvents.getAPI().getEventManager().registerListener(
            new SpigotInventoryPacketListener(menuBuildService.getInventoryTracker(), menuBuildService)
        );

        Bukkit.getPluginManager().registerEvents(
            new SpigotQuitListener(menuBuildService.getInventoryTracker()),
            plugin
        );

        if (IS_FOLIA) {
            foliaRunAsyncTimer(plugin, new MenuUpdateTask(), 50, 50, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new MenuUpdateTask(), 0L, 1L);
        }
    }

    private static void foliaRunGlobal(JavaPlugin plugin, Runnable task) {
        try {
            Object globalScheduler = foliaGetGlobalScheduler.invoke(null);
            Consumer<Object> consumer = (scheduledTask) -> task.run();
            foliaGlobalRun.invoke(globalScheduler, plugin, consumer);
        } catch (Exception e) {
            log.error("Failed to run Folia global task", e);
            task.run();
        }
    }

    private static void foliaRunAsyncTimer(JavaPlugin plugin, Runnable task,
                                            long delay, long period, TimeUnit unit) {
        try {
            Object asyncScheduler = Bukkit.class.getMethod("getAsyncScheduler").invoke(null);
            Method runAtFixedRate = asyncScheduler.getClass().getMethod("runAtFixedRate",
                    Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
            Consumer<Object> consumer = (scheduledTask) -> task.run();
            runAtFixedRate.invoke(asyncScheduler, plugin, consumer, delay, period, unit);
        } catch (Exception e) {
            log.error("Failed to start Folia async timer", e);
        }
    }
}
