package dev.simplix.cirrus.model;

import java.util.Objects;
import lombok.NonNull;

public final class CirrusSound {

    private final String identifier;

    public static final CirrusSound UI_BUTTON_CLICK = of("minecraft:ui.button.click");
    public static final CirrusSound BLOCK_NOTE_BLOCK_PLING = of("minecraft:block.note_block.pling");
    public static final CirrusSound BLOCK_NOTE_BLOCK_CHIME = of("minecraft:block.note_block.chime");
    public static final CirrusSound BLOCK_NOTE_BLOCK_BELL = of("minecraft:block.note_block.bell");
    public static final CirrusSound ENTITY_EXPERIENCE_ORB_PICKUP = of("minecraft:entity.experience_orb.pickup");
    public static final CirrusSound ENTITY_ITEM_PICKUP = of("minecraft:entity.item.pickup");
    public static final CirrusSound ENTITY_PLAYER_LEVELUP = of("minecraft:entity.player.levelup");
    public static final CirrusSound BLOCK_CHEST_OPEN = of("minecraft:block.chest.open");
    public static final CirrusSound BLOCK_CHEST_CLOSE = of("minecraft:block.chest.close");
    public static final CirrusSound BLOCK_WOODEN_DOOR_OPEN = of("minecraft:block.wooden_door.open");
    public static final CirrusSound BLOCK_WOODEN_DOOR_CLOSE = of("minecraft:block.wooden_door.close");

    private CirrusSound(@NonNull String identifier) {
        this.identifier = identifier.toLowerCase();
    }

    public static CirrusSound of(@NonNull String identifier) {
        if (!identifier.contains(":")) {
            identifier = "minecraft:" + identifier;
        }
        return new CirrusSound(identifier);
    }

    public String identifier() {
        return identifier;
    }

    public String name() {
        int colonIndex = identifier.indexOf(':');
        return identifier.substring(colonIndex + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CirrusSound that = (CirrusSound) o;
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
