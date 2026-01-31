package dev.simplix.cirrus.spigot;

import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.menu.MenuUpdateTask;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.service.RunSyncService;
import dev.simplix.cirrus.spigot.listener.SpigotMenuListener;
import dev.simplix.cirrus.spigot.menubuilder.SpigotMenuBuildService;
import dev.simplix.cirrus.spigot.services.SpigotItemService;
import dev.simplix.cirrus.spigot.services.converters.BukkitItemStackConverter;
import dev.simplix.cirrus.spigot.services.converters.ItemStackConverter;
import dev.simplix.cirrus.spigot.services.converters.ItemTypeMaterialConverter;
import dev.simplix.cirrus.spigot.services.converters.MaterialDataItemTypeConverter;
import dev.simplix.cirrus.spigot.services.converters.NmsNbtQuerzNbtConverter;
import dev.simplix.cirrus.spigot.services.converters.QuerzNbtNmsNbtConverter;
import dev.simplix.cirrus.spigot.services.converters.SpigotClickTypeConverter;
import dev.simplix.cirrus.spigot.services.converters.SpigotInventoryTypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
@RequiredArgsConstructor
public class CirrusSpigot {

    private final JavaPlugin plugin;

    public void init() {
        Cirrus.init();
        Cirrus.canDisplayAsync(false);
        Cirrus.isSpigot(true);

        Cirrus.registerService(ItemService.class, new SpigotItemService());
        Cirrus.registerService(BukkitItemStackConverter.class, new BukkitItemStackConverter());
        Cirrus.registerService(ItemStackConverter.class, new ItemStackConverter());
        Cirrus.registerService(ItemTypeMaterialConverter.class, new ItemTypeMaterialConverter());
        Cirrus.registerService(MaterialDataItemTypeConverter.class, new MaterialDataItemTypeConverter());
        Cirrus.registerService(NmsNbtQuerzNbtConverter.class, new NmsNbtQuerzNbtConverter());
        Cirrus.registerService(QuerzNbtNmsNbtConverter.class, new QuerzNbtNmsNbtConverter());
        Cirrus.registerService(SpigotClickTypeConverter.class, new SpigotClickTypeConverter());
        Cirrus.registerService(SpigotInventoryTypeConverter.class, new SpigotInventoryTypeConverter());
        Cirrus.registerService(MenuBuildService.class, new SpigotMenuBuildService(plugin));
        Cirrus.registerService(
            RunSyncService.class,
            runnable -> Bukkit.getScheduler().runTask(plugin, runnable));

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new MenuUpdateTask(), 0L, 1L);
        Bukkit.getPluginManager().registerEvents(new SpigotMenuListener(), plugin);
    }

}
