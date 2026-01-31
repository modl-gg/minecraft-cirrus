package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType;
import dev.simplix.cirrus.model.CirrusClickType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClickTypeMapper {

    public CirrusClickType mapClickType(WindowClickType type, int button, int slot) {
        return switch (type) {
            case PICKUP -> button == 0 ? CirrusClickType.LEFT_CLICK : CirrusClickType.RIGHT_CLICK;
            case QUICK_MOVE -> button == 0 ? CirrusClickType.SHIFT_LEFT_CLICK : CirrusClickType.SHIFT_RIGHT_CLICK;
            case SWAP -> switch (button) {
                case 0 -> CirrusClickType.NUMBER_KEY_1;
                case 1 -> CirrusClickType.NUMBER_KEY_2;
                case 2 -> CirrusClickType.NUMBER_KEY_3;
                case 3 -> CirrusClickType.NUMBER_KEY_4;
                case 4 -> CirrusClickType.NUMBER_KEY_5;
                case 5 -> CirrusClickType.NUMBER_KEY_6;
                case 6 -> CirrusClickType.NUMBER_KEY_7;
                case 7 -> CirrusClickType.NUMBER_KEY_8;
                case 8 -> CirrusClickType.NUMBER_KEY_9;
                case 40 -> CirrusClickType.OFFHAND_SWAP;
                default -> CirrusClickType.UNKNOWN;
            };
            case CLONE -> CirrusClickType.MIDDLE_CLICK;
            case THROW -> button == 0 ? CirrusClickType.DROP : CirrusClickType.CTRL_DROP;
            case QUICK_CRAFT -> mapQuickCraft(button);
            case PICKUP_ALL -> CirrusClickType.DOUBLE_CLICK;
            default -> CirrusClickType.UNKNOWN;
        };
    }

    public CirrusClickType mapQuickCraft(int button) {
        return switch (button) {
            case 0 -> CirrusClickType.DRAG_START_LEFT;
            case 4 -> CirrusClickType.DRAG_START_RIGHT;
            case 8 -> CirrusClickType.DRAG_START_MIDDLE;
            case 1 -> CirrusClickType.DRAG_ADD_LEFT;
            case 5 -> CirrusClickType.DRAG_ADD_RIGHT;
            case 9 -> CirrusClickType.DRAG_ADD_MIDDLE;
            case 2 -> CirrusClickType.DRAG_END_LEFT;
            case 6 -> CirrusClickType.DRAG_END_RIGHT;
            case 10 -> CirrusClickType.DRAG_END_MIDDLE;
            default -> CirrusClickType.UNKNOWN;
        };
    }
}
