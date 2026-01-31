package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.menu.CirrusInventoryType;
import java.util.function.Function;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryType;

public class SpigotInventoryTypeConverter implements Function<CirrusInventoryType, InventoryType> {

    @Override
    public InventoryType apply(@NonNull CirrusInventoryType src) {
        return switch (src) {
            case ANVIL -> InventoryType.ANVIL;
            case BEACON -> InventoryType.BEACON;
            case BREWING_STAND -> InventoryType.BREWING;
            case CRAFTING -> InventoryType.WORKBENCH;
            case GENERIC_9X1, GENERIC_9X6, GENERIC_9X5, GENERIC_9X4, GENERIC_9X3, GENERIC_9X2 -> InventoryType.CHEST;
            case GENERIC_3X3 -> InventoryType.DISPENSER;
            case ENCHANTMENT -> InventoryType.ENCHANTING;
            case FURNACE -> InventoryType.FURNACE;
            case HOPPER -> InventoryType.HOPPER;
            case MERCHANT -> InventoryType.MERCHANT;
            case BLAST_FURNACE -> InventoryType.BLAST_FURNACE;
            case GRINDSTONE -> InventoryType.GRINDSTONE;
            case LECTERN -> InventoryType.LECTERN;
            case LOOM -> InventoryType.LOOM;
            case SHULKER_BOX -> InventoryType.SHULKER_BOX;
            case SMITHING -> InventoryType.SMITHING;
            case SMOKER -> InventoryType.SMOKER;
            case CARTOGRAPHY -> InventoryType.CARTOGRAPHY;
            case STONECUTTER -> InventoryType.STONECUTTER;
        };
    }
}
