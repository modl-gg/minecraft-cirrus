package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.item.CirrusItemType;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;

@Slf4j
public class ItemTypeMaterialConverter implements Function<CirrusItemType, Material> {

    @Override
    public Material apply(@NonNull CirrusItemType src) {
        try {
            String name = src.identifier();
            if (name.startsWith("minecraft:")) {
                name = name.substring("minecraft:".length());
            }
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.debug("[Cirrus] Material {} not found on this server version, using STONE as fallback", src.identifier());
            return Material.STONE;
        }
    }

}
