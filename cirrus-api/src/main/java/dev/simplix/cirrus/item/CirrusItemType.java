package dev.simplix.cirrus.item;

import java.util.Objects;
import lombok.NonNull;

public final class CirrusItemType {

    private final String identifier;

    public static final CirrusItemType AIR = of("minecraft:air");
    public static final CirrusItemType STONE = of("minecraft:stone");
    public static final CirrusItemType PLAYER_HEAD = of("minecraft:player_head");
    public static final CirrusItemType BARRIER = of("minecraft:barrier");
    public static final CirrusItemType GRAY_STAINED_GLASS_PANE = of("minecraft:gray_stained_glass_pane");
    public static final CirrusItemType BLACK_STAINED_GLASS_PANE = of("minecraft:black_stained_glass_pane");
    public static final CirrusItemType WHITE_STAINED_GLASS_PANE = of("minecraft:white_stained_glass_pane");
    public static final CirrusItemType ARROW = of("minecraft:arrow");
    public static final CirrusItemType PAPER = of("minecraft:paper");
    public static final CirrusItemType BOOK = of("minecraft:book");
    public static final CirrusItemType CHEST = of("minecraft:chest");
    public static final CirrusItemType ENDER_CHEST = of("minecraft:ender_chest");
    public static final CirrusItemType DIAMOND = of("minecraft:diamond");
    public static final CirrusItemType EMERALD = of("minecraft:emerald");
    public static final CirrusItemType GOLD_INGOT = of("minecraft:gold_ingot");
    public static final CirrusItemType IRON_INGOT = of("minecraft:iron_ingot");
    public static final CirrusItemType REDSTONE = of("minecraft:redstone");
    public static final CirrusItemType COMPASS = of("minecraft:compass");
    public static final CirrusItemType CLOCK = of("minecraft:clock");
    public static final CirrusItemType NAME_TAG = of("minecraft:name_tag");
    public static final CirrusItemType WRITTEN_BOOK = of("minecraft:written_book");
    public static final CirrusItemType WRITABLE_BOOK = of("minecraft:writable_book");
    public static final CirrusItemType MAP = of("minecraft:map");
    public static final CirrusItemType FILLED_MAP = of("minecraft:filled_map");
    public static final CirrusItemType HOPPER = of("minecraft:hopper");
    public static final CirrusItemType ENCHANTED_BOOK = of("minecraft:enchanted_book");
    public static final CirrusItemType EMERALD_BLOCK = of("minecraft:emerald_block");
    public static final CirrusItemType BONE_BLOCK = of("minecraft:bone_block");
    public static final CirrusItemType IRON_BLOCK = of("minecraft:iron_block");
    public static final CirrusItemType FIRE_CORAL_BLOCK = of("minecraft:fire_coral_block");
    public static final CirrusItemType COPPER_BLOCK = of("minecraft:copper_block");
    public static final CirrusItemType DARK_OAK_DOOR = of("minecraft:dark_oak_door");
    public static final CirrusItemType ACACIA_DOOR = of("minecraft:acacia_door");
    public static final CirrusItemType ITEM_FRAME = of("minecraft:item_frame");
    public static final CirrusItemType ACACIA_BOAT = of("minecraft:acacia_boat");

    private CirrusItemType(@NonNull String identifier) {
        this.identifier = identifier.toLowerCase();
        if (!this.identifier.contains(":")) {
            throw new IllegalArgumentException("Item type identifier must be namespaced (e.g., 'minecraft:stone')");
        }
    }

    public static CirrusItemType of(@NonNull String identifier) {
        if (!identifier.contains(":")) {
            identifier = "minecraft:" + identifier;
        }
        return new CirrusItemType(identifier);
    }

    public String identifier() {
        return identifier;
    }

    public String name() {
        int colonIndex = identifier.indexOf(':');
        return identifier.substring(colonIndex + 1).toUpperCase();
    }

    public String namespace() {
        int colonIndex = identifier.indexOf(':');
        return identifier.substring(0, colonIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CirrusItemType that = (CirrusItemType) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
