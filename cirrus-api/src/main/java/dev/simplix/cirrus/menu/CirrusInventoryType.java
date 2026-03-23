package dev.simplix.cirrus.menu;

public enum CirrusInventoryType {

    GENERIC_9X1(9, 0),
    GENERIC_9X2(18, 1),
    GENERIC_9X3(27, 2),
    GENERIC_9X4(36, 3),
    GENERIC_9X5(45, 4),
    GENERIC_9X6(54, 5),
    GENERIC_3X3(9, 6),
    ANVIL(3, 8),
    BEACON(1, 9),
    BLAST_FURNACE(3, 10),
    BREWING_STAND(5, 11),
    CRAFTING(10, 12),
    ENCHANTMENT(2, 13),
    FURNACE(3, 14),
    GRINDSTONE(3, 15),
    HOPPER(5, 16),
    LECTERN(1, 17),
    LOOM(4, 18),
    MERCHANT(3, 19),
    SHULKER_BOX(27, 20),
    SMITHING(4, 21),
    SMOKER(3, 22),
    CARTOGRAPHY(3, 23),
    STONECUTTER(2, 24);

    private final int size;
    private final int packetEventsTypeId;

    CirrusInventoryType(int size, int packetEventsTypeId) {
        this.size = size;
        this.packetEventsTypeId = packetEventsTypeId;
    }

    public int size() {
        return size;
    }

    public int typicalSize(int protocolVersion) {
        return size;
    }

    public int toPacketEventsTypeId() {
        return packetEventsTypeId;
    }

    /**
     * Returns the legacy inventory type string used in the Open Window packet for
     * Minecraft clients older than 1.14 (protocol versions 1.8–1.13).
     * Types that didn't exist before 1.14 fall back to "minecraft:chest".
     */
    public String toLegacyType() {
        switch (this) {
            case GENERIC_9X1:
            case GENERIC_9X2:
            case GENERIC_9X3:
            case GENERIC_9X4:
            case GENERIC_9X5:
            case GENERIC_9X6:
                return "minecraft:chest";
            case GENERIC_3X3:
                return "minecraft:dispenser";
            case ANVIL:
                return "minecraft:anvil";
            case BEACON:
                return "minecraft:beacon";
            case BREWING_STAND:
                return "minecraft:brewing_stand";
            case CRAFTING:
                return "minecraft:crafting_table";
            case ENCHANTMENT:
                return "minecraft:enchanting_table";
            case FURNACE:
                return "minecraft:furnace";
            case HOPPER:
                return "minecraft:hopper";
            case MERCHANT:
                return "minecraft:villager";
            case SHULKER_BOX:
                return "minecraft:shulker_box";
            default:
                return "minecraft:chest";
        }
    }

    public static CirrusInventoryType fromSize(int size) {
        switch (size) {
            case 9: return GENERIC_9X1;
            case 18: return GENERIC_9X2;
            case 27: return GENERIC_9X3;
            case 36: return GENERIC_9X4;
            case 45: return GENERIC_9X5;
            case 54: return GENERIC_9X6;
            default: throw new IllegalArgumentException("Invalid size: " + size);
        }
    }

    public static CirrusInventoryType calculateForContent(int contentSize) {
        if (contentSize <= 9) return GENERIC_9X1;
        if (contentSize <= 18) return GENERIC_9X2;
        if (contentSize <= 27) return GENERIC_9X3;
        if (contentSize <= 36) return GENERIC_9X4;
        if (contentSize <= 45) return GENERIC_9X5;
        return GENERIC_9X6;
    }
}
