package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.model.CirrusClickType;
import java.util.function.Function;
import lombok.NonNull;
import org.bukkit.event.inventory.ClickType;

public class SpigotClickTypeConverter implements Function<ClickType, CirrusClickType> {

    @Override
    public CirrusClickType apply(@NonNull ClickType src) {
        return switch (src) {
            case LEFT -> CirrusClickType.LEFT_CLICK;
            case RIGHT -> CirrusClickType.RIGHT_CLICK;
            case DROP -> CirrusClickType.DROP;
            case MIDDLE -> CirrusClickType.MIDDLE_CLICK;
            case NUMBER_KEY -> CirrusClickType.NUMBER_KEY_1;
            case SHIFT_LEFT -> CirrusClickType.SHIFT_LEFT_CLICK;
            case SHIFT_RIGHT -> CirrusClickType.SHIFT_RIGHT_CLICK;
            case CONTROL_DROP -> CirrusClickType.CTRL_DROP;
            case DOUBLE_CLICK -> CirrusClickType.DOUBLE_CLICK;
            case WINDOW_BORDER_LEFT -> CirrusClickType.LEFT_CLICK_OUTSIDE;
            case WINDOW_BORDER_RIGHT -> CirrusClickType.RIGHT_CLICK_OUTSIDE;
            case CREATIVE -> CirrusClickType.CREATIVE;
            case SWAP_OFFHAND -> CirrusClickType.SWAP_OFFHAND;
            case UNKNOWN -> CirrusClickType.UNKNOWN;
        };
    }
}
