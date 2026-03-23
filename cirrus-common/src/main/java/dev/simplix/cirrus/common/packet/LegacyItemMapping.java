package dev.simplix.cirrus.common.packet;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LegacyItemMapping {

    private static final class LegacyEntry {
        private final int dataValue;

        LegacyEntry(int dataValue) {
            this.dataValue = dataValue;
        }

        int dataValue() {
            return dataValue;
        }
    }

    private final Map<String, LegacyEntry> MAPPINGS = new HashMap<>();

    static {
        // Skulls
        put("minecraft:skeleton_skull", 0);
        put("minecraft:wither_skeleton_skull", 1);
        put("minecraft:zombie_head", 2);
        put("minecraft:player_head", 3);
        put("minecraft:creeper_head", 4);
        put("minecraft:dragon_head", 5);

        // Fish
        put("minecraft:cod", 0);
        put("minecraft:salmon", 1);
        put("minecraft:tropical_fish", 2);
        put("minecraft:pufferfish", 3);

        // Wool (all 16 colors)
        put("minecraft:white_wool", 0);
        put("minecraft:orange_wool", 1);
        put("minecraft:magenta_wool", 2);
        put("minecraft:light_blue_wool", 3);
        put("minecraft:yellow_wool", 4);
        put("minecraft:lime_wool", 5);
        put("minecraft:pink_wool", 6);
        put("minecraft:gray_wool", 7);
        put("minecraft:light_gray_wool", 8);
        put("minecraft:cyan_wool", 9);
        put("minecraft:purple_wool", 10);
        put("minecraft:blue_wool", 11);
        put("minecraft:brown_wool", 12);
        put("minecraft:green_wool", 13);
        put("minecraft:red_wool", 14);
        put("minecraft:black_wool", 15);

        // Stained glass panes (all 16 colors)
        put("minecraft:white_stained_glass_pane", 0);
        put("minecraft:orange_stained_glass_pane", 1);
        put("minecraft:magenta_stained_glass_pane", 2);
        put("minecraft:light_blue_stained_glass_pane", 3);
        put("minecraft:yellow_stained_glass_pane", 4);
        put("minecraft:lime_stained_glass_pane", 5);
        put("minecraft:pink_stained_glass_pane", 6);
        put("minecraft:gray_stained_glass_pane", 7);
        put("minecraft:light_gray_stained_glass_pane", 8);
        put("minecraft:cyan_stained_glass_pane", 9);
        put("minecraft:purple_stained_glass_pane", 10);
        put("minecraft:blue_stained_glass_pane", 11);
        put("minecraft:brown_stained_glass_pane", 12);
        put("minecraft:green_stained_glass_pane", 13);
        put("minecraft:red_stained_glass_pane", 14);
        put("minecraft:black_stained_glass_pane", 15);

        // Terracotta (all 16 colors)
        put("minecraft:white_terracotta", 0);
        put("minecraft:orange_terracotta", 1);
        put("minecraft:magenta_terracotta", 2);
        put("minecraft:light_blue_terracotta", 3);
        put("minecraft:yellow_terracotta", 4);
        put("minecraft:lime_terracotta", 5);
        put("minecraft:pink_terracotta", 6);
        put("minecraft:gray_terracotta", 7);
        put("minecraft:light_gray_terracotta", 8);
        put("minecraft:cyan_terracotta", 9);
        put("minecraft:purple_terracotta", 10);
        put("minecraft:blue_terracotta", 11);
        put("minecraft:brown_terracotta", 12);
        put("minecraft:green_terracotta", 13);
        put("minecraft:red_terracotta", 14);
        put("minecraft:black_terracotta", 15);

        // Dyes (all 16 colors — legacy dye data values are inverted from wool)
        put("minecraft:ink_sac", 0);
        put("minecraft:red_dye", 1);
        put("minecraft:green_dye", 2);
        put("minecraft:cocoa_beans", 3);
        put("minecraft:lapis_lazuli", 4);
        put("minecraft:purple_dye", 5);
        put("minecraft:cyan_dye", 6);
        put("minecraft:light_gray_dye", 7);
        put("minecraft:gray_dye", 8);
        put("minecraft:pink_dye", 9);
        put("minecraft:lime_dye", 10);
        put("minecraft:yellow_dye", 11);
        put("minecraft:light_blue_dye", 12);
        put("minecraft:magenta_dye", 13);
        put("minecraft:orange_dye", 14);
        put("minecraft:bone_meal", 15);

        // Beds
        put("minecraft:white_bed", 0);
        put("minecraft:orange_bed", 1);
        put("minecraft:magenta_bed", 2);
        put("minecraft:light_blue_bed", 3);
        put("minecraft:yellow_bed", 4);
        put("minecraft:lime_bed", 5);
        put("minecraft:pink_bed", 6);
        put("minecraft:gray_bed", 7);
        put("minecraft:light_gray_bed", 8);
        put("minecraft:cyan_bed", 9);
        put("minecraft:purple_bed", 10);
        put("minecraft:blue_bed", 11);
        put("minecraft:brown_bed", 12);
        put("minecraft:green_bed", 13);
        put("minecraft:red_bed", 14);
        put("minecraft:black_bed", 15);

        // Signs
        put("minecraft:oak_sign", 0);
    }

    private void put(String identifier, int dataValue) {
        MAPPINGS.put(identifier, new LegacyEntry(dataValue));
    }

    public int getDataValue(String identifier) {
        LegacyEntry entry = MAPPINGS.get(identifier);
        return entry != null ? entry.dataValue() : 0;
    }
}
