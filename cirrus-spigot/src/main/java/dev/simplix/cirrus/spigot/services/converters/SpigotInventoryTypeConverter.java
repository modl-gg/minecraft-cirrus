package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.menu.CirrusInventoryType;
import java.util.function.Function;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryType;

public class SpigotInventoryTypeConverter implements Function<CirrusInventoryType, InventoryType> {

    @Override
    public InventoryType apply(@NonNull CirrusInventoryType src) {
        switch (src) {
            case ANVIL: return InventoryType.ANVIL;
            case BEACON: return InventoryType.BEACON;
            case BREWING_STAND: return InventoryType.BREWING;
            case CRAFTING: return InventoryType.WORKBENCH;
            case GENERIC_9X1:
            case GENERIC_9X2:
            case GENERIC_9X3:
            case GENERIC_9X4:
            case GENERIC_9X5:
            case GENERIC_9X6: return InventoryType.CHEST;
            case GENERIC_3X3: return InventoryType.DISPENSER;
            case ENCHANTMENT: return InventoryType.ENCHANTING;
            case FURNACE: return InventoryType.FURNACE;
            case HOPPER: return InventoryType.HOPPER;
            case MERCHANT: return InventoryType.MERCHANT;
            case BLAST_FURNACE: return InventoryType.BLAST_FURNACE;
            case GRINDSTONE: return InventoryType.GRINDSTONE;
            case LECTERN: return InventoryType.LECTERN;
            case LOOM: return InventoryType.LOOM;
            case SHULKER_BOX: return InventoryType.SHULKER_BOX;
            case SMITHING: return InventoryType.SMITHING;
            case SMOKER: return InventoryType.SMOKER;
            case CARTOGRAPHY: return InventoryType.CARTOGRAPHY;
            case STONECUTTER: return InventoryType.STONECUTTER;
            default: throw new IllegalArgumentException("Unknown inventory type: " + src);
        }
    }
}
