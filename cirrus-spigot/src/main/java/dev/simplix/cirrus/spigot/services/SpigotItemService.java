package dev.simplix.cirrus.spigot.services;

import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.service.ItemService;
import java.util.Arrays;
import java.util.Set;
import org.bukkit.Material;

public class SpigotItemService extends ItemService {

    private final Set<String> materialNames = Arrays
        .stream(Material.values())
        .map(Material::name)
        .collect(java.util.stream.Collectors.toSet());

    @Override
    public boolean isItemAvailable(CirrusItemType itemType, int protocolVersion) {
        String name = itemType.identifier();
        if (name.startsWith("minecraft:")) {
            name = name.substring("minecraft:".length());
        }
        final Material material = material(name.toUpperCase());
        return material != null && material.isItem() && !material.isAir();
    }

    private Material material(String name) {
        if (!materialNames.contains(name)) {
            return null;
        }
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}
